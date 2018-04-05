package com.shirantech.sathitv.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;
import com.shirantech.sathitv.BuildConfig;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.adapter.UploadPhotoListAdapter;
import com.shirantech.sathitv.helper.CrashlyticsHelper;
import com.shirantech.sathitv.helper.FileHelper;
import com.shirantech.sathitv.helper.PhotoHelper;
import com.shirantech.sathitv.helper.PreferencesHelper;
import com.shirantech.sathitv.logger.Log;
import com.shirantech.sathitv.model.PhotoToUpload;
import com.shirantech.sathitv.model.response.GeneralResponse;
import com.shirantech.sathitv.network.ApiConstants;
import com.shirantech.sathitv.utils.AppUtil;
import com.shirantech.sathitv.widget.PhotoSpacingItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * An {@link Activity} showing the interface for photo uploading.
 */
public class PhotoUploadActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "Photo Upload";
    private static final String URL_PHOTO_UPLOAD = ApiConstants.APP_USER_URL + "uploadImage";
    private static final String PARAM_DESCRIPTION = "description";
    private static final String PARAM_PHOTO = "userfile[%d]";
    private static final String PARAM_ASPECT_RATIO = "aspectRatio[%d]";
    private static final String PARAM_PHOTO_CAPTION = "caption[%d]";
    private static final int ACTION_IMAGE_REQUEST_CODE = 1;

    private CoordinatorLayout mMainLayout;
    private FloatingActionButton mAddPhotoFAB;
    private ProgressBar mUploadProgressBar;
    private RecyclerView mPhotoListView;
    private RelativeLayout mUploadProgressFrame;
    private TextView mMessageView;
    private TextView mUploadMessageView;

    private final List<PhotoToUpload> mUploadPhotoList = new ArrayList<>();
    private UploadPhotoListAdapter mUploadPhotoListAdapter;

    private boolean showPostMenu = false;

    private Uri outputImageUri, selectedImageUri;

    /**
     * Callback used in uploading photo to server.
     */
    private final FutureCallback<GeneralResponse> mPhotoUploadCallback = new FutureCallback<GeneralResponse>() {
        @Override
        public void onCompleted(Exception exception, GeneralResponse serverResponse) {
            if (null == exception) {
                if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_SUCCESS, serverResponse.getStatus())) {
                    showUploadProgress(UploadState.SUCCESS);
                } else if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_FAILURE, serverResponse.getStatus())) {
                    Log.d(TAG, serverResponse.getMessage());
                    showUploadProgress(UploadState.FAILURE);
                    // TODO : track photo uploading failure using Google Analytics(or Crashlytics)
                } else {
                    showUploadProgress(UploadState.FAILURE);
                    // TODO : track connection/server error using Google Analytics(or Crashlytics)
                }
            } else {
                showUploadProgress(UploadState.FAILURE);
                Log.e(TAG, exception.getLocalizedMessage(), exception);
                CrashlyticsHelper.sendCaughtException(exception);
            }
        }
    };

    /**
     * Get the {@link Intent} to start this activity
     *
     * @param context context to initialize the Intent.
     * @return the Intent to start {@link PhotoUploadActivity}
     */
    public static Intent getStartIntent(Context context) {
        //noinspection UnnecessaryLocalVariable
        Intent startIntent = new Intent(context, PhotoUploadActivity.class);
        // pass any extra data if needed here
        // startIntent.putExtra(KEY, VALUE);
        return startIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_upload);

        setUpToolbar();
        initViews();

        // show photo choice without prompting user to click button at first time
        showPhotoSourceChoice();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo_upload, menu);
        menu.findItem(R.id.action_post_photo).setVisible(showPostMenu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_post_photo) {
            sendPhoto();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Send the photo to server
     */
    private void sendPhoto() {
        if (AppUtil.isInternetConnected(PhotoUploadActivity.this)) {
            if (imageValid()) {
                showUploadProgress(UploadState.UPLOADING);
                final String loginToken = PreferencesHelper.getLoginToken(this);
                final String photoAlbumDescription = mUploadPhotoListAdapter.getPhotoAlbumDescription();

                final Builders.Any.B builder = Ion.with(this)
                        .load(URL_PHOTO_UPLOAD)
                        .uploadProgressBar(mUploadProgressBar);
                if (BuildConfig.DEBUG) {
                    builder.setLogging(TAG, Log.VERBOSE);
                }
                builder.setHeader(ApiConstants.HEADER_KEY_ACCEPT, ApiConstants.HEADER_ACCEPT_VALUE)
                        .setMultipartParameter(ApiConstants.PARAM_TOKEN, loginToken)
                        .setMultipartParameter(PARAM_DESCRIPTION, photoAlbumDescription);
                for (int i = 0; i < mUploadPhotoList.size(); i++) {
                    final String photoName = String.format(PARAM_PHOTO, i);
                    final String aspectRatioName = String.format(PARAM_ASPECT_RATIO, i);
                    final String captionName = String.format(PARAM_PHOTO_CAPTION, i);
                    builder.setMultipartFile(photoName,
                            new File(mUploadPhotoList.get(i).getPhotoUri().getPath()));
                    builder.setMultipartParameter(aspectRatioName,
                            String.valueOf(mUploadPhotoList.get(i).getPhotoAspectRatio()));
                    builder.setMultipartParameter(captionName,
                            mUploadPhotoList.get(i).getPhotoCaption());
                }
                builder.as(GeneralResponse.class).setCallback(mPhotoUploadCallback);
            } else {
                Snackbar.make(mMainLayout, R.string.error_data_not_valid, Snackbar.LENGTH_LONG).show();
            }
        } else {
            Snackbar.make(mMainLayout, R.string.error_network_connection, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Check if images are valid or not. Show user invalid info if not valid.
     *
     * @return <code>true</code> if images are valid <code>false</code> otherwise.
     */
    private boolean imageValid() {
        boolean valid = true;
        for (int i = 0; i < mUploadPhotoList.size(); i++) {
            if (!fileExists(mUploadPhotoList.get(i).getPhotoUri())) {
                mUploadPhotoList.get(i).setPhotoValid(false);
                valid = false;
            }
        }
        if (!valid) {
            mUploadPhotoListAdapter.notifyDataSetChanged();
        }
        return valid;
    }

    /**
     * Check if the image file for given Uri exists or not
     *
     * @param imageUri Uri of image to check
     * @return <code>true</code> if exists <code>false</code> otherwise.
     */
    private boolean fileExists(final Uri imageUri) {
        final File imageFile = new File(imageUri.getPath());
        return imageFile.exists();
    }

    private void showUploadProgress(final UploadState uploadState) {
        switch (uploadState) {
            case UPLOADING:
                mUploadProgressFrame.setVisibility(View.VISIBLE);
                mUploadMessageView.setText(uploadState.getMessageResId());
                break;
            case SUCCESS:
                mUploadMessageView.setText(uploadState.getMessageResId());
                finish();
                // TODO : may b restart previous activity
                break;
            case FAILURE:
                mUploadMessageView.setText(uploadState.getMessageResId());
                mUploadProgressFrame.setVisibility(View.GONE);
                Snackbar.make(mMainLayout, uploadState.getMessageResId(), Snackbar.LENGTH_LONG).show();
                break;
        }
    }

    private void setUpToolbar() {
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            //noinspection ConstantConditions
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViews() {
        mMainLayout = (CoordinatorLayout) findViewById(R.id.main_layout);
        mMessageView = (TextView) findViewById(R.id.message_view);
        mMessageView.setOnClickListener(this);
        mPhotoListView = (RecyclerView) findViewById(R.id.listview_photo);
        setUpPhotoListView();
        mAddPhotoFAB = (FloatingActionButton) findViewById(R.id.fab_add_photo);
        mAddPhotoFAB.setOnClickListener(this);
        animateFAB();

        mUploadProgressFrame = (RelativeLayout) findViewById(R.id.frame_upload_progress);
        mUploadProgressBar = (ProgressBar) findViewById(R.id.progressbar_image_upload);
        mUploadMessageView = (TextView) findViewById(R.id.textview_upload_message);
    }

    private void setUpPhotoListView() {
        mPhotoListView.setHasFixedSize(false);
        mPhotoListView.setLayoutManager(new LinearLayoutManager(PhotoUploadActivity.this));
        final int spacing = (int) getResources().getDimension(R.dimen.spacing_normal);
        mPhotoListView.addItemDecoration(new PhotoSpacingItemDecoration(new Rect(spacing, spacing, spacing, spacing)));
        mUploadPhotoListAdapter = new UploadPhotoListAdapter(mUploadPhotoList, this);
        mPhotoListView.setAdapter(mUploadPhotoListAdapter);
    }

    private void animateFAB() {
        mAddPhotoFAB.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                removeOnGlobalLayoutListener();
                mAddPhotoFAB.startAnimation(AnimationUtils.loadAnimation(PhotoUploadActivity.this, R.anim.simple_grow));
            }

            @SuppressWarnings("deprecation")
            private void removeOnGlobalLayoutListener() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mAddPhotoFAB.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    mAddPhotoFAB.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    @Override
    public void onClick(final View clickedView) {
        switch (clickedView.getId()) {
            case R.id.message_view:
            case R.id.fab_add_photo: {
                showPhotoSourceChoice();
                break;
            }
            case R.id.remove_photo_button: {
                final int position = (int) clickedView.getTag();
                removePhotoFromList(position);
                break;
            }
            default:
        }
    }

    /**
     * Present the user with available sources for getting image.
     */
    private void showPhotoSourceChoice() {
        final File photoFile = new File(FileHelper.getImageDirectory(), FileHelper.getUniqueImageName());
        outputImageUri = Uri.fromFile(photoFile);
        // camera intents
        final List<Intent> cameraIntentList = getCameraIntentList(outputImageUri);
        // gallery intent
        final Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent,
                getString(R.string.source_choice_title));

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                cameraIntentList.toArray(new Parcelable[cameraIntentList.size()]));

        startActivityForResult(chooserIntent, ACTION_IMAGE_REQUEST_CODE);
    }

    /**
     * Get the available {@link Intent}s which can capture image.
     *
     * @param outputImageUri uri for the output image
     * @return the possible {@link Intent}s to capture image.
     */
    private List<Intent> getCameraIntentList(final Uri outputImageUri) {
        final List<Intent> cameraIntentList = new ArrayList<>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(
                captureIntent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            captureIntent.setComponent(
                    new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            captureIntent.setPackage(packageName);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputImageUri);
            cameraIntentList.add(captureIntent);
        }
        return cameraIntentList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            if (ACTION_IMAGE_REQUEST_CODE == requestCode) {
                final boolean isCamera;
                if (null == data || data.getData() == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    isCamera = TextUtils.equals(MediaStore.ACTION_IMAGE_CAPTURE, action);
                }

                if (isCamera) {
                    selectedImageUri = outputImageUri;
                } else {
                    selectedImageUri = FileHelper.getImageUriWithAuthority(
                            PhotoUploadActivity.this, data.getData());
                }

                if (selectedImageUri != null) {
                    addPhotoToList();
                } else {
                    Snackbar.make(mMainLayout, R.string.error_could_not_load_photo,
                            Snackbar.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void removePhotoFromList(final int position) {
        mUploadPhotoList.remove(position);
        mUploadPhotoListAdapter.notifyDataSetChanged();
        updateView();
    }

    /**
     * Add the selected photo to {@link #mPhotoListView} and show in {@link #mPhotoListView}
     */
    private void addPhotoToList() {
        mUploadPhotoList.add(getPhotoToAdd());
        mUploadPhotoListAdapter.notifyDataSetChanged();
        updateView();
    }

    /**
     * Update the ui on addition/deletion of image to the list.
     * </p>
     * Show message to add image if list empty else show list and send action menu.
     */
    private void updateView() {
        if (mUploadPhotoList.size() == 0) {
            showMessageView(true);
            showSendAction(false);
        } else {
            showMessageView(false);
            showSendAction(true);
        }
    }

    /**
     * Show/hide the {@link #mMessageView}
     *
     * @param show <code>true</code> if {@link #mMessageView} is to be shown else <code>false</code>.
     */
    private void showMessageView(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mPhotoListView.setVisibility(show ? View.GONE : View.VISIBLE);
        mPhotoListView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mPhotoListView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mMessageView.setVisibility(show ? View.VISIBLE : View.GONE);
        mMessageView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mMessageView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * Show/hide send action menu
     *
     * @param show <code>true</code> to show the send menu <code>false</code> otherwise.
     */
    private void showSendAction(final boolean show) {
        showPostMenu = show;
        invalidateOptionsMenu();
    }

    /**
     * Get the photo to upload from the selected image uri. also obtain the aspect ratio of the image
     * @return the photo to upload
     */
    private PhotoToUpload getPhotoToAdd() {
        final float aspectRatio = PhotoHelper.getAspectRatioOf(selectedImageUri);
        return new PhotoToUpload(selectedImageUri, aspectRatio);
    }

    private enum UploadState {
        UPLOADING(R.string.message_uploading_photos),
        SUCCESS(R.string.message_uploading_success),
        FAILURE(R.string.message_uploading_failure);

        private int messageResId;

        UploadState(int stateMessage) {
            this.messageResId = stateMessage;
        }

        public int getMessageResId() {
            return messageResId;
        }
    }

    @Override
    protected void onStop() {
        Ion.getDefault(this)
                .cancelAll(this);
        super.onStop();
    }
}
