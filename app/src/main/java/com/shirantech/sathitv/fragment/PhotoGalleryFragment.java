package com.shirantech.sathitv.fragment;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;
import com.shirantech.sathitv.BuildConfig;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.activity.PhotoAlbumActivity;
import com.shirantech.sathitv.activity.PhotoUploadActivity;
import com.shirantech.sathitv.adapter.PhotoGalleryAdapter;
import com.shirantech.sathitv.helper.CrashlyticsHelper;
import com.shirantech.sathitv.helper.PreferencesHelper;
import com.shirantech.sathitv.logger.Log;
import com.shirantech.sathitv.model.PhotoAlbum;
import com.shirantech.sathitv.model.response.PhotoAlbumListResponse;
import com.shirantech.sathitv.network.ApiConstants;
import com.shirantech.sathitv.utils.AppUtil;
import com.shirantech.sathitv.utils.ServerResponse;
import com.shirantech.sathitv.widget.CustomProgressView;
import com.shirantech.sathitv.widget.RecyclerViewItemDecoration;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * A simple {@link Fragment} subclass showing photos of models in grid.
 * Use the {@link PhotoGalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotoGalleryFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "PhotoGallery";
    private static final String URL_GET_PHOTOS = ApiConstants.APP_USER_URL + "getPhotos";

    private RecyclerView mPhotoAlbumGridView;
    private CustomProgressView mProgressView;

    private PhotoGalleryAdapter mPhotoGalleryAdapter;
    private List<PhotoAlbum> mPhotoAlbumList;
    private List<PhotoAlbum> mOwnPhotoAlbumList;
    /**
     * List to hold the header titles & post list to send to the adapter.
     */
    private List<Object> mDataList;

    /**
     * Callback used in fetching photo list from server
     */
    private FutureCallback<PhotoAlbumListResponse> mPhotoAlbumListCallback = new FutureCallback<PhotoAlbumListResponse>() {
        @Override
        public void onCompleted(Exception exception, PhotoAlbumListResponse serverResponse) {
            if (getActivity() != null && isAdded()) {
                if (null == exception) {
                    if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_SUCCESS, serverResponse.getStatus())) {
                        mPhotoAlbumList = serverResponse.getPhotoAlbumList();
                        mOwnPhotoAlbumList = serverResponse.getOwnPhotoAlbumList();
                        populateData();
                    } else if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_FAILURE, serverResponse.getStatus())) {
                        Log.d(TAG, serverResponse.getMessage());
                        showMessageToUser(serverResponse.getMessage());

                        // TODO : track failure in getting photo using Google Analytics(or Crashlytics)
                    } else {
                        showMessageToUser(R.string.error_problem_occurred);
                        // TODO : track connection/server error using Google Analytics(or Crashlytics)
                    }
                } else {
                    Log.e(TAG, exception.getLocalizedMessage(), exception);
                    showMessageToUser(R.string.error_problem_occurred);
                    CrashlyticsHelper.sendCaughtException(exception);
                }
            }
        }
    };

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment PhotoGalleryFragment.
     */
    public static PhotoGalleryFragment newInstance() {
        final PhotoGalleryFragment fragment = new PhotoGalleryFragment();
        final Bundle args = new Bundle();
        // pass arguments if any
        fragment.setArguments(args);
        return fragment;
    }

    public PhotoGalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mPhotoAlbumGridView = (RecyclerView) view.findViewById(R.id.photo_album_gridView);
        mProgressView = (CustomProgressView) view.findViewById(R.id.progress_view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showProgress(true);
        fetchPhotos();
    }

    /**
     * Show/hide the progress while hiding/showing list
     *
     * @param show <code>true</code> to show the progress <code>false</code> otherwise.
     */
    private void showProgress(final boolean show) {
        final int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mPhotoAlbumGridView.setVisibility(show ? View.GONE : View.VISIBLE);
        mPhotoAlbumGridView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mPhotoAlbumGridView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.setProgressMessage(R.string.message_getting_photos);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * Fetch photos from server
     */
    private void fetchPhotos() {
        if (AppUtil.isInternetConnected(getActivity())) {
            final String loginToken = PreferencesHelper.getLoginToken(getActivity());
            final Builders.Any.B builder = Ion.with(getActivity())
                    .load(URL_GET_PHOTOS);
            if (BuildConfig.DEBUG) {
                builder.setLogging(TAG, Log.VERBOSE);
            }
            builder.setHeader(ApiConstants.HEADER_KEY_ACCEPT, ApiConstants.HEADER_ACCEPT_VALUE)
                    .setBodyParameter(ApiConstants.PARAM_TOKEN, loginToken)
                    .as(PhotoAlbumListResponse.class)
                    .setCallback(mPhotoAlbumListCallback);
        } else {
            showMessageToUser(R.string.error_network_connection);
        }
    }

    /**
     * If photos are obtained from the server populate the list with provided photo list.
     */
    private void populateData() {
        if ((null != mPhotoAlbumList && !mPhotoAlbumList.isEmpty()) || (null != mOwnPhotoAlbumList && !mOwnPhotoAlbumList.isEmpty())) {
            showProgress(false);
            setupPhotoGalleryView();
        } else {
            showMessageToUser(R.string.message_no_photos);
        }
    }

    /**
     * Setup the {@link #mPhotoAlbumGridView} to show grids. And populate the list with provided photo list.
     */
    private void setupPhotoGalleryView() {
        obtainDataList();
        final int spanCount = getResources().getInteger(R.integer.photo_grid_span_count);
        final int itemSpacing = (int) getResources().getDimension(R.dimen.spacing_normal);
        final GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), spanCount);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (PhotoGalleryAdapter.TYPE_HEADER == mPhotoGalleryAdapter.getItemViewType(position)) {
                    return spanCount;
                } else {
                    return 1;
                }
            }
        });
        final RecyclerViewItemDecoration mItemDecoration = new RecyclerViewItemDecoration(itemSpacing);

        mPhotoAlbumGridView.setHasFixedSize(true);
        mPhotoAlbumGridView.setLayoutManager(mLayoutManager);
        mPhotoAlbumGridView.addItemDecoration(mItemDecoration);
        mPhotoGalleryAdapter = new PhotoGalleryAdapter(mDataList, this);
        mPhotoAlbumGridView.setAdapter(mPhotoGalleryAdapter);

        showFAB();
    }

    /**
     * Get all the data (both own photos and other photos) in the same list.
     */
    public void obtainDataList() {
        mDataList = new ArrayList<>();
        if (null != mOwnPhotoAlbumList && !mOwnPhotoAlbumList.isEmpty()) {
            mDataList.add(R.string.own_album);
            mDataList.addAll(mOwnPhotoAlbumList);
        }
        if (null != mPhotoAlbumList && !mPhotoAlbumList.isEmpty()) {
            mDataList.add(R.string.other_albums);
            mDataList.addAll(mPhotoAlbumList);
        }
    }

    /**
     * Adding the FAB dynamically to address the issue,
     * <a href="https://code.google.com/p/android/issues/detail?can=2&q=182093&colspec=ID%20Type%20Status%20Owner%20Summary%20Stars&id=182093">
     * FloatingAcitonButton does not respect Visibility</a>
     * TODO : can add in xml when this issue is fixed
     */
    private void showFAB() {
        if (getView() != null) {
            final FloatingActionButton mAddPhotoFAB = new FloatingActionButton(getActivity());
            final CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            layoutParams.leftMargin = (int) getResources().getDimension(R.dimen.fab_margin);
            layoutParams.rightMargin = (int) getResources().getDimension(R.dimen.fab_margin);
            layoutParams.topMargin = (int) getResources().getDimension(R.dimen.fab_margin);
            layoutParams.bottomMargin = (int) getResources().getDimension(R.dimen.fab_margin);
            layoutParams.gravity = Gravity.BOTTOM | Gravity.END;
            mAddPhotoFAB.setLayoutParams(layoutParams);
            mAddPhotoFAB.setImageResource(R.drawable.ic_camera);
            mAddPhotoFAB.setId(R.id.fab_add_photo);
            mAddPhotoFAB.setOnClickListener(this);
            ((CoordinatorLayout) getView()).addView(mAddPhotoFAB);
            mAddPhotoFAB.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.simple_grow));
        }
    }

    /**
     * Show the message and hide the ProgressBar
     *
     * @param messageId Resource id of message to show.
     */
    private void showMessageToUser(int messageId) {
        mProgressView.setProgressMessage(messageId);
        mProgressView.hideProgressBar();
    }

    /**
     * Show the message and hide the ProgressBar
     *
     * @param message message to show
     */
    private void showMessageToUser(String message) {
        mProgressView.setProgressMessage(message);
        mProgressView.hideProgressBar();
        if (ServerResponse.isTokenInvalid(message)) {
            AppUtil.showInvalidTokenSnackBar(mPhotoAlbumGridView, message, getActivity());
        } else {
            Snackbar.make(mPhotoAlbumGridView, message, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View clickedView) {
        switch (clickedView.getId()) {
            case R.id.fab_add_photo: {
                getActivity().startActivity(PhotoUploadActivity.getStartIntent(getActivity()));
                break;
            }
            case R.id.photo_view: {
                final int clickedPostPosition = (int) clickedView.getTag();
                final PhotoAlbum clickedPhotoAlbum = (PhotoAlbum) mDataList.get(clickedPostPosition);
                getActivity().startActivity(PhotoAlbumActivity.getStartIntent(getActivity(), clickedPhotoAlbum,
                        isOwnPhoto(clickedPhotoAlbum)));
                break;
            }
            case R.id.info_button: {
                new AlertDialog.Builder(getContext())
                        // TODO : change message
                        .setMessage(R.string.message_photo_album_not_verified)
                        .setPositiveButton(R.string.prompt_ok, null)
                        .show();
                break;
            }

        }
    }

    /**
     * Check if the clicked post(photo) in gallery is owned by the user.
     *
     * @param clickedPhotoAlbum clicked post
     * @return <code>true</code> if post is owned by the user <code>false</code> otherwise.
     */
    private boolean isOwnPhoto(PhotoAlbum clickedPhotoAlbum) {
        for (PhotoAlbum tempPhotoAlbum : mOwnPhotoAlbumList) {
            if (TextUtils.equals(clickedPhotoAlbum.getId(), tempPhotoAlbum.getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().registerSticky(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /**
     * This method will be called when post has been updated and notified by {@link PhotoAlbumActivity}
     *
     * @param updatedPhotoAlbum the updated post
     */
    public void onEventMainThread(PhotoAlbum updatedPhotoAlbum) {
        updatePhotoAlbumListWith(updatedPhotoAlbum);
        EventBus.getDefault().removeStickyEvent(updatedPhotoAlbum);
    }

    /**
     * Update the {@link #mPhotoAlbumList} with provided post
     *
     * @param updatedPhotoAlbum the updated photo album
     */
    private void updatePhotoAlbumListWith(final PhotoAlbum updatedPhotoAlbum) {
        for (Object data : mDataList) {
            if (!(data instanceof Integer)) {
                final PhotoAlbum oldPhotoAlbum = (PhotoAlbum) data;
                if (TextUtils.equals(updatedPhotoAlbum.getId(), oldPhotoAlbum.getId())) {
                    oldPhotoAlbum.setPhotoList(updatedPhotoAlbum.getPhotoList());
                    mPhotoGalleryAdapter.notifyDataSetChanged();
                    return;
                }
            }
        }
    }
}
