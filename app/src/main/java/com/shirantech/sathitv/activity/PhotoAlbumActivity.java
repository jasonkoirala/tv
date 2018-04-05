package com.shirantech.sathitv.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;
import com.shirantech.sathitv.BuildConfig;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.adapter.PhotoListAdapter;
import com.shirantech.sathitv.helper.CrashlyticsHelper;
import com.shirantech.sathitv.helper.PreferencesHelper;
import com.shirantech.sathitv.logger.Log;
import com.shirantech.sathitv.model.Photo;
import com.shirantech.sathitv.model.PhotoAlbum;
import com.shirantech.sathitv.model.postparams.PhotoRatingParams;
import com.shirantech.sathitv.model.response.PhotoRatingResponse;
import com.shirantech.sathitv.network.ApiConstants;
import com.shirantech.sathitv.utils.AppUtil;
import com.shirantech.sathitv.widget.PhotoSpacingItemDecoration;

import de.greenrobot.event.EventBus;

/**
 * {@link android.app.Activity} for showing specific photo album.
 * Options for rating, commenting are also available.
 */
public class PhotoAlbumActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "PostActivity";
    private static final String URL_RATE_PHOTO = ApiConstants.APP_USER_URL + "rating";
    private static final String ARG_CLICKED_PHOTO_ALBUM = "clicked_photo_album";
    private static final String ARG_IS_OWN_PHOTO = "is_own_photo";

    private RecyclerView mPhotoListView;
    private PhotoListAdapter mPhotoListAdapter;

    private PhotoAlbum mPhotoAlbum;
    private boolean mOwnPhoto;

    /**
     * Callback for photo rating.
     */
    private class PhotoRatingCallback implements FutureCallback<PhotoRatingResponse> {
        /**
         * Used to update the view with new updated rating of the photo
         */
        final Photo photo;

        /**
         * Initialize the callback with the reference of the photo to later update with the updated rating.
         *
         * @param photo photo to update later.
         */
        public PhotoRatingCallback(final Photo photo) {
            this.photo = photo;
        }

        @Override
        public void onCompleted(Exception exception, PhotoRatingResponse serverResponse) {
            if (null == exception) {
                if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_SUCCESS, serverResponse.getStatus())) {
                    showUpdatedRating(photo, serverResponse.getUpdatedRating());
                } else if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_FAILURE, serverResponse.getStatus())) {
                    Log.d(TAG, serverResponse.getMessage());
                    showFailureMessage(R.string.error_rating_photo);
                    // TODO : track photo rating failure using Google Analytics(or Crashlytics)
                } else {
                    showFailureMessage(R.string.error_problem_occurred);
                    // TODO : track connection/server error using Google Analytics(or Crashlytics)
                }
            } else {
                Log.e(TAG, exception.getLocalizedMessage(), exception);
                showFailureMessage(R.string.error_problem_occurred);
                CrashlyticsHelper.sendCaughtException(exception);
            }
        }

        /**
         * Show the failure message when rating unsuccessful.
         *
         * @param errorMessage error message to show
         */
        private void showFailureMessage(int errorMessage) {
            Snackbar.make(mPhotoListView, errorMessage, Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * Get the {@link Intent} to start this activity
     *
     * @param context           context to initialize the Intent.
     * @param clickedPhotoAlbum {@link PhotoAlbum} data to pass
     * @param ownPhoto          whether this post is owned by the current user or not
     * @return the Intent to start {@link PhotoAlbumActivity}
     */
    public static Intent getStartIntent(Context context, PhotoAlbum clickedPhotoAlbum, boolean ownPhoto) {
        Intent startIntent = new Intent(context, PhotoAlbumActivity.class);
        Gson gson = new Gson();
        startIntent.putExtra(ARG_CLICKED_PHOTO_ALBUM, gson.toJson(clickedPhotoAlbum, PhotoAlbum.class));
        startIntent.putExtra(ARG_IS_OWN_PHOTO, ownPhoto);
        return startIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_album);

        setUpToolbar();
        initView();

        getPhotoAlbumData(getIntent());
        populateView();
    }

    private void setUpToolbar() {
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            //noinspection ConstantConditions
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    private void initView() {
        mPhotoListView = (RecyclerView) findViewById(R.id.photo_listview);
        mPhotoListView.setHasFixedSize(true);
        mPhotoListView.setLayoutManager(new LinearLayoutManager(PhotoAlbumActivity.this));
        final int spacing = (int) getResources().getDimension(R.dimen.spacing_normal);
        mPhotoListView.addItemDecoration(new PhotoSpacingItemDecoration(new Rect(0, 0, 0, spacing)));
    }

    /**
     * Get the {@link PhotoAlbum} data.
     *
     * @param intent {@link Intent} to get photo album data from
     */
    private void getPhotoAlbumData(Intent intent) {
        final Gson gson = new Gson();
        final String photoAlbumJson = intent.getStringExtra(ARG_CLICKED_PHOTO_ALBUM);
        mPhotoAlbum = gson.fromJson(photoAlbumJson, PhotoAlbum.class);
        mOwnPhoto = intent.getBooleanExtra(ARG_IS_OWN_PHOTO, false);
    }

    /**
     * Fill the list with data(description & photos).
     */
    private void populateView() {
        mPhotoListAdapter = new PhotoListAdapter(mPhotoAlbum, mOwnPhoto, this);
        mPhotoListView.setAdapter(mPhotoListAdapter);
    }

    @Override
    public void onClick(View clickedView) {
        switch (clickedView.getId()) {
            case R.id.photo_view: {
                final int currentImagePosition = (int) clickedView.getTag();
                startActivity(FullScreenPhotoActivity.getStartIntent(PhotoAlbumActivity.this, mPhotoAlbum, currentImagePosition));
                break;
            }
            case R.id.submit_rate_button: {
                final Photo currentPhoto = (Photo) clickedView.getTag();
                sendPhotoRating(currentPhoto);
                break;
            }
            case R.id.comment_button: {
                final Photo currentPhoto = (Photo) clickedView.getTag();
                startActivity(CommentActivity.getStartIntent(PhotoAlbumActivity.this, currentPhoto));
                break;
            }
            case R.id.edit_button: {
                final Photo currentPhoto = (Photo) clickedView.getTag();
                startActivity(CaptionEditActivity.getStartIntent(PhotoAlbumActivity.this, currentPhoto));
                break;
            }
            default:
                // no-op
        }
    }

    /**
     * Send photo rating to the server
     *
     * @param currentPhoto rated {@link Photo}. Also used to later update the photo with updated average rating
     */
    private void sendPhotoRating(final Photo currentPhoto) {
        if (AppUtil.isInternetConnected(PhotoAlbumActivity.this)) {
            final String loginToken = PreferencesHelper.getLoginToken(PhotoAlbumActivity.this);
            final String photoId = currentPhoto.getPhotoId();
            final float photoRating = currentPhoto.getMyRating();
            final Builders.Any.B builder = Ion.with(PhotoAlbumActivity.this)
                    .load(URL_RATE_PHOTO);
            if (BuildConfig.DEBUG) {
                builder.setLogging(TAG, Log.VERBOSE);
            }
            builder.setHeader(ApiConstants.HEADER_KEY_ACCEPT, ApiConstants.HEADER_ACCEPT_VALUE)
                    .setJsonPojoBody(new PhotoRatingParams(photoId, photoRating, loginToken))
                    .as(new TypeToken<PhotoRatingResponse>() {
                    })
                    .setCallback(new PhotoRatingCallback(currentPhoto));
        } else {
            Snackbar.make(findViewById(R.id.main_layout), R.string.error_network_connection,
                    Snackbar.LENGTH_LONG).show();
        }
    }

    private void showUpdatedRating(final Photo photo, final float updatedRating) {
        photo.setPhotoRating(updatedRating);
        mPhotoListView.getAdapter().notifyDataSetChanged();
        EventBus.getDefault().postSticky(mPhotoAlbum);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().registerSticky(this);
    }

    @Override
    protected void onStop() {
        Ion.getDefault(this)
                .cancelAll(this);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /**
     * This method will be called when comment list has been changed and notified by {@link CommentActivity}
     *
     * @param updatedPhoto the photo with updated comments
     */
    public void onEventMainThread(Photo updatedPhoto) {
        updatePostWith(updatedPhoto);
        // post event using event bus to notify updated photo album to the {@link PhotoGalleryFragment}
        EventBus.getDefault().postSticky(mPhotoAlbum);
        EventBus.getDefault().removeStickyEvent(updatedPhoto);
    }

    /**
     * Update the {@link #mPhotoAlbum} with provided photo
     *
     * @param updatedPhoto the updated photo
     */
    private void updatePostWith(final Photo updatedPhoto) {
        for (Photo oldPhoto : mPhotoAlbum.getPhotoList()) {
            if (TextUtils.equals(updatedPhoto.getPhotoId(), oldPhoto.getPhotoId())) {
                oldPhoto.setPhotoCaption(updatedPhoto.getPhotoCaption());
                oldPhoto.setPhotoCommentList(updatedPhoto.getPhotoCommentList());
                mPhotoListAdapter.notifyDataSetChanged();
                return;
            }
        }
    }
}
