package com.shirantech.sathitv.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;
import com.shirantech.sathitv.BuildConfig;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.fragment.FullScreenPhotoFragment;
import com.shirantech.sathitv.helper.CrashlyticsHelper;
import com.shirantech.sathitv.helper.PreferencesHelper;
import com.shirantech.sathitv.logger.Log;
import com.shirantech.sathitv.model.Photo;
import com.shirantech.sathitv.model.postparams.CaptionUpdateParams;
import com.shirantech.sathitv.model.response.GeneralResponse;
import com.shirantech.sathitv.network.ApiConstants;
import com.shirantech.sathitv.utils.AppUtil;

import de.greenrobot.event.EventBus;

/**
 * {@link android.app.Activity} for photo caption editing.
 */
public class CaptionEditActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "CaptionEdit";
    private static final String URL_CAPTION_UPDATE = ApiConstants.APP_USER_URL + "editCaption";
    private static final String ARG_PHOTO_DETAILS = "photo_detail";

    private LinearLayout mCaptionInputLayout;
    private EditText mPhotoCaptionEditText;
    private Button mUpdateCaptionButton;
    private LinearLayout mCaptionViewLayout;
    private TextView mPhotoCaptionView;
    private TextView mCaptionUpdateStatus;

    private Photo mPhoto;

    /**
     * Callback for photo caption update
     */
    private FutureCallback<GeneralResponse> mCaptionUpdateCallback = new FutureCallback<GeneralResponse>() {
        @Override
        public void onCompleted(Exception exception, GeneralResponse serverResponse) {
            if (null == exception) {
                if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_SUCCESS, serverResponse.getStatus())) {
                    showSuccessMessage(serverResponse.getMessage());
                } else if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_FAILURE, serverResponse.getStatus())) {
                    Log.d(TAG, serverResponse.getMessage());
                    showErrorMessage(R.string.error_rating_photo);
                    // TODO : track caption update failure using Google Analytics(or Crashlytics)
                } else {
                    showErrorMessage(R.string.error_problem_occurred);
                    // TODO : track connection/server error using Google Analytics(or Crashlytics)

                }
            } else {
                Log.e(TAG, exception.getLocalizedMessage(), exception);
                showErrorMessage(R.string.error_problem_occurred);
                CrashlyticsHelper.sendCaughtException(exception);
            }
        }
    };

    /**
     * Get the {@link Intent} to start this activity
     *
     * @param context context to initialize the Intent.
     * @param photo   {@link Photo} to pass
     * @return the Intent to start {@link CaptionEditActivity}
     */
    public static Intent getStartIntent(Context context, Photo photo) {
        Intent startIntent = new Intent(context, CaptionEditActivity.class);
        Gson gson = new Gson();
        startIntent.putExtra(ARG_PHOTO_DETAILS, gson.toJson(photo, Photo.class));
        return startIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caption_edit);

        mPhoto = getPhoto(getIntent());
        initViews();
    }

    private void initViews() {
        final FullScreenPhotoFragment imageFragment = (FullScreenPhotoFragment) getSupportFragmentManager().findFragmentById(R.id.photo_view);
        imageFragment.showPhoto(mPhoto);

        mCaptionInputLayout = (LinearLayout) findViewById(R.id.caption_input_layout);
        mPhotoCaptionEditText = (EditText) findViewById(R.id.photo_caption_editText);
        mPhotoCaptionEditText.setText(mPhoto.getPhotoCaption());
        mPhotoCaptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // no op
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUpdateCaptionButton.setText(TextUtils.equals(mPhoto.getPhotoCaption(), s.toString().trim())
                        || TextUtils.isEmpty(s) ? R.string.cancel_update : R.string.update_caption);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // no op
            }
        });
        mUpdateCaptionButton = (Button) findViewById(R.id.update_caption_button);
        mUpdateCaptionButton.setOnClickListener(this);

        mCaptionViewLayout = (LinearLayout) findViewById(R.id.caption_view_layout);
        mPhotoCaptionView = (TextView) findViewById(R.id.photo_caption_view);
        mCaptionUpdateStatus = (TextView) findViewById(R.id.caption_update_status);
    }

    /**
     * Get the {@link Photo}.
     *
     * @param intent {@link Intent} to get photo from
     * @return {@link Photo} to show image.
     */
    private Photo getPhoto(Intent intent) {
        final String photoJson = intent.getStringExtra(ARG_PHOTO_DETAILS);
        return new Gson().fromJson(photoJson, Photo.class);
    }


    @Override
    public void onClick(View clickedView) {
        if (R.id.update_caption_button == clickedView.getId()) {
            if (TextUtils.equals(mUpdateCaptionButton.getText(), getString(R.string.cancel_update))) {
                finish();
            } else {
                updateCaption();
            }
        }
    }

    /**
     * Send updated photo caption to the server
     */
    private void updateCaption() {
        if (AppUtil.isInternetConnected(CaptionEditActivity.this)) {
            final String loginToken = PreferencesHelper.getLoginToken(this);
            final String updatedCaption = mPhotoCaptionEditText.getText().toString();
            final String photoId = mPhoto.getPhotoId();

            final Builders.Any.B builder = Ion.with(this)
                    .load(URL_CAPTION_UPDATE);
            if (BuildConfig.DEBUG) {
                builder.setLogging(TAG, Log.VERBOSE);
            }
            builder.setHeader(ApiConstants.HEADER_KEY_ACCEPT, ApiConstants.HEADER_ACCEPT_VALUE)
                    .setJsonPojoBody(new CaptionUpdateParams(photoId, updatedCaption, loginToken))
                    .as(GeneralResponse.class)
                    .setCallback(mCaptionUpdateCallback);

            AppUtil.hideSoftKeyboard(CaptionEditActivity.this);
            showCaptionView(true);
        } else {
            showMessage(R.string.error_network_connection);
        }
    }

    /**
     * Show the provided message
     *
     * @param messageId resource id of message to show
     */
    private void showMessage(int messageId) {
        Snackbar.make(findViewById(R.id.main_layout), messageId, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Show the provided message
     *
     * @param message message to show
     */
    private void showMessage(String message) {
        Snackbar.make(findViewById(R.id.main_layout), message, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Show the provided error message and update status
     *
     * @param message message to show
     */
    private void showSuccessMessage(String message) {
        showMessage(message);
        mCaptionUpdateStatus.setVisibility(View.GONE);

        // post event using event bus to notify about the updated photo caption to the {@link PostActivity}
        mPhoto.setPhotoCaption(mPhotoCaptionEditText.getText().toString());
        EventBus.getDefault().postSticky(mPhoto);
    }

    /**
     * Show the provided error message and show back the editText
     *
     * @param messageId resource id of message to show
     */
    private void showErrorMessage(int messageId) {
        showMessage(messageId);
        showCaptionView(false);
    }

    /**
     * Show/Hide the updated caption with update status
     *
     * @param show <code>true</code> to show caption view and hide caption EditText
     *             <code>false</code> for reverse
     */
    private void showCaptionView(final boolean show) {
        final int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mCaptionInputLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        mCaptionInputLayout.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCaptionInputLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mPhotoCaptionView.setText(mPhotoCaptionEditText.getText());
        mCaptionViewLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        mCaptionViewLayout.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCaptionViewLayout.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    protected void onStop() {
        Ion.getDefault(this)
                .cancelAll(this);
        super.onStop();
    }
}
