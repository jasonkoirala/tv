package com.shirantech.sathitv.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;
import com.shirantech.sathitv.BuildConfig;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.helper.CrashlyticsHelper;
import com.shirantech.sathitv.helper.FileHelper;
import com.shirantech.sathitv.helper.PreferencesHelper;
import com.shirantech.sathitv.logger.Log;
import com.shirantech.sathitv.model.postparams.HealthConsultantParams;
import com.shirantech.sathitv.model.response.GeneralResponse;
import com.shirantech.sathitv.model.response.HelpResponse;
import com.shirantech.sathitv.network.ApiConstants;
import com.shirantech.sathitv.utils.AppLog;
import com.shirantech.sathitv.utils.AppUtil;
import com.shirantech.sathitv.utils.ServerResponse;
import com.shirantech.sathitv.widget.CustomProgressView;

import java.io.File;

/**
 * {@link android.app.Activity} showing ui for Janam Kundali request.
 */
public class HealthConsultantActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "Health Consultant";
    public static final int SELECT_IMAGE = 0;
    private static final String URL_REQUEST_HEALTH_CONNSULTANT = ApiConstants.APP_USER_URL + "consultantQuery";
    private static final String URL_REQUEST_HELP = ApiConstants.APP_USER_URL + "getConsultantHelpContent";
    private LinearLayout layoutMain;
    private EditText editTextSubject, editTextMessage, editTextSymptom;
    private Button buttonSubmit, buttonViewReply, buttonUpload, buttonHelp;
    private ImageView imageViewHealthConsultant;
    private CustomProgressView mProgressView;
    Uri imageUri;
    private String url;
    private File imageFile = null;

    /**
     * Callback for health consultant request
     */
    private FutureCallback<GeneralResponse> mRequestCallback = new FutureCallback<GeneralResponse>() {
        @Override
        public void onCompleted(Exception exception, GeneralResponse serverResponse) {
            if (null == exception) {
                if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_SUCCESS, serverResponse.getStatus())) {
                    showSuccessMessage(serverResponse.getMessage());
                } else if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_FAILURE, serverResponse.getStatus())) {
                    Log.d(TAG, serverResponse.getMessage());
                    showFailureMessage(serverResponse.getMessage());

                    // TODO : track janam kundali request failure using Google Analytics(or Crashlytics)
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

        private void showSuccessMessage(String message) {
            mProgressView.setProgressMessage(R.string.message_submitted);
            Snackbar.make(layoutMain, message, Snackbar.LENGTH_LONG)
                    .setCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            super.onDismissed(snackbar, event);
                            finish();
                        }
                    })
                    .setAction("Action", null)
                    .show();
        }

        private void showFailureMessage(String message) {
            showProgress(false);
            if (ServerResponse.isTokenInvalid(message)) {
                AppUtil.showInvalidTokenSnackBar(layoutMain, message, HealthConsultantActivity.this);
            }else{
                Snackbar.make(layoutMain, message, Snackbar.LENGTH_SHORT).show();
            }

        }

        private void showFailureMessage(int messageId) {
            showFailureMessage(getString(messageId));
        }
    };

    private TextWatcher mInputWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // no-op
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(editTextSubject.getText().toString().trim()) &&
                    !TextUtils.isEmpty(editTextMessage.getText().toString().trim()) &&
                    !TextUtils.isEmpty(editTextSymptom.getText().toString().trim())) {
//                showSubmitMenu(true);
            } else {
//                showSubmitMenu(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            // no-op
        }
    };

    /**
     * Get the {@link Intent} to start this activity
     *
     * @param context context to initialize the Intent.
     * @return the Intent to start {@link HealthConsultantActivity}
     */
    public static Intent getStartIntent(Context context) {
        return new Intent(context, HealthConsultantActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_consultant);

        setUpToolbar();
        initViews();
    }

    private void setUpToolbar() {
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            //noinspection ConstantConditions
            getSupportActionBar().setTitle(getString(R.string.dashboard_health_consultant));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    private void initViews() {
        layoutMain = (LinearLayout) findViewById(R.id.layoutHealthConsultantMain);

        editTextSubject = (EditText) findViewById(R.id.editTextSubject);
        editTextSubject.addTextChangedListener(mInputWatcher);

        editTextMessage = (EditText) findViewById(R.id.editTextMessage);
        editTextMessage.addTextChangedListener(mInputWatcher);


        editTextSymptom = (EditText) findViewById(R.id.editTextSymptoms);
        editTextSymptom.addTextChangedListener(mInputWatcher);
        buttonSubmit = (Button) findViewById(R.id.buttonHealthConsultantSubmit);
        buttonSubmit.setOnClickListener(this);
        buttonViewReply = (Button) findViewById(R.id.buttonViewReplyHealth);
        buttonViewReply.setOnClickListener(this);

        buttonUpload = (Button) findViewById(R.id.buttonUploadImage);
        buttonUpload.setOnClickListener(this);

        buttonHelp = (Button) findViewById(R.id.buttonHelp);
        buttonHelp.setOnClickListener(this);
        imageViewHealthConsultant = (ImageView) findViewById(R.id.imageViewHealthConsultant);
        mProgressView = (CustomProgressView) findViewById(R.id.progress_view);
    }

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_rashifal_request, menu);
        menu.findItem(R.id.action_submit_request);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_submit_request) {
            validateFields();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
*/

    /**
     * Validate all fields and send data to server if all fields are valid
     */
    private void validateFields() {
        AppUtil.hideSoftKeyboard(HealthConsultantActivity.this);

        // reset previously set errors
        resetError();

        // Store values at the time of the submit attempt.
        final String subject = editTextSubject.getText().toString().trim();
        final String message = editTextMessage.getText().toString().trim();
        final String symptoms = editTextSymptom.getText().toString().trim();

        boolean valid = true;
        View focusView = null;

        // check for location
        if (TextUtils.isEmpty(subject)) {
            showError(editTextSubject, R.string.error_health_subject_required);
            focusView = editTextSubject;
            valid = false;
        }

        // check for name
        if (TextUtils.isEmpty(message)) {
            showError(editTextMessage, R.string.error_health_message_required);
            focusView = editTextMessage;
            valid = false;
        }

        // check for symptoms
        if (TextUtils.isEmpty(symptoms)) {
            showError(editTextSymptom, R.string.error_health_symptoms_required);
            focusView = editTextSymptom;
            valid = false;
        }

        if (valid) {
            sendJanamKundaliRequest();
        } else {
            // There was an error; don't attempt submit and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
    }

    /**
     * reset the error
     */
    private void resetError() {
        editTextSubject.setError(null);
        editTextMessage.setError(null);
        editTextSymptom.setError(null);
    }

    /**
     * Show error in the provided TextInputLayout
     */
    private void showError(EditText editText, int errorMessageId) {
        editText.setError(getString(errorMessageId));
    }

    /**
     * Send the Janam kundali request to the server
     */
    private void sendJanamKundaliRequest() {
        if (AppUtil.isInternetConnected(HealthConsultantActivity.this)) {
            showProgress(true);

            final String loginToken = PreferencesHelper.getLoginToken(this);
            final String subject = editTextSubject.getText().toString().trim();
            final String message = editTextMessage.getText().toString().trim();
            final String symptoms = editTextSymptom.getText().toString().trim();
            final Builders.Any.B builder = Ion.with(this).load(URL_REQUEST_HEALTH_CONNSULTANT);
            if (BuildConfig.DEBUG) {
                builder.setLogging(TAG, Log.VERBOSE);
            }
            if (null == imageFile) {
                builder.setHeader(ApiConstants.HEADER_KEY_ACCEPT, ApiConstants.HEADER_ACCEPT_VALUE)
                        .setMultipartParameter("subject", subject)
                        .setMultipartParameter("message", message)
                        .setMultipartParameter("symptoms", symptoms)
                        .setMultipartParameter("token", loginToken)
                        .as(GeneralResponse.class)
                        .setCallback(mRequestCallback);
            } else {
                builder.setHeader(ApiConstants.HEADER_KEY_ACCEPT, ApiConstants.HEADER_ACCEPT_VALUE)
                        .setMultipartParameter("subject", subject)
                        .setMultipartParameter("message", message)
                        .setMultipartParameter("symptoms", symptoms)
                        .setMultipartParameter("token", loginToken)
                        .setMultipartFile("image", imageFile)
                        .as(GeneralResponse.class)
                        .setCallback(mRequestCallback);
            }
/*
            builder.setHeader(ApiConstants.HEADER_KEY_ACCEPT, ApiConstants.HEADER_ACCEPT_VALUE)
                    .setJsonPojoBody(new HealthConsultantParams(subject, message, symptoms, loginToken))
                    .as(GeneralResponse.class)
                    .setCallback(mRequestCallback);*/
        } else {
            Snackbar.make(layoutMain, R.string.error_network_connection, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Shows the progress while request submission.
     *
     * @param showProgress <code>true</code> to show the progress <code>false</code> otherwise.
     */
    public void showProgress(final boolean showProgress) {
        // hide submit menu when progress is visible
//        showSubmitMenu(!showProgress);
        mProgressView.setVisibility(showProgress ? View.VISIBLE : View.GONE);
        mProgressView.setProgressMessage(R.string.message_submitting);
    }

    @Override
    protected void onStop() {
        Ion.getDefault(this).cancelAll(this);
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonHealthConsultantSubmit:
                validateFields();
                break;
            case R.id.buttonViewReplyHealth:
                Intent intent = new Intent(HealthConsultantActivity.this, HeathConsultantReplyActivity.class);
                startActivity(intent);
                break;
            case R.id.buttonUploadImage:
                Intent intentUpload = new Intent();
                intentUpload.setType("image/*");
                intentUpload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intentUpload, "Select picture"), SELECT_IMAGE);
                break;
            case R.id.buttonHelp:
                getHelpDataFromServer();
                break;
            default:


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppLog.showLog(TAG, " on activity result");
        AppLog.showLog(TAG, "request code" + requestCode + "result code" + resultCode);
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (null != data) {
                    try {
                        AppLog.showLog(TAG, " data" + data);
                        imageUri = FileHelper.getImageUriWithAuthority(this, data.getData());

                        if (null != imageUri) {
                            imageViewHealthConsultant.setImageURI(imageUri);
                            convertIntoFile(imageUri);
                        }
                        AppLog.showLog(TAG, "image uri ::" + imageUri);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        } else {
            // TODO other result code
        }
    }

    public void convertIntoFile(Uri imageUri) {
        String actualPath = getRealPathFromURI(imageUri);
        AppLog.showLog(TAG, "actualPath" + actualPath);
        AppLog.showLog(TAG, "image uri" + imageUri);
        imageFile = new File(actualPath);
        if (imageFile.exists()) {
            AppLog.showLog(TAG, "File found");
        } else {
            AppLog.showLog(TAG, "File not found");
        }
        AppLog.showLog(TAG, "image file :" + imageFile);

    }


    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = this.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


    /*
   * fetch data for help from server
   * */
    private void getHelpDataFromServer() {
        if (AppUtil.isInternetConnected(this)) {
            showHelpProgress(true);
            final Builders.Any.B builder = Ion.with(this).load(URL_REQUEST_HELP);
            AppLog.showLog(TAG, "url help :: " + URL_REQUEST_HELP);
            if (BuildConfig.DEBUG) {
                builder.setLogging(TAG, Log.VERBOSE);
                builder.setHeader(ApiConstants.HEADER_KEY_ACCEPT, ApiConstants.HEADER_ACCEPT_VALUE)
                        .as(HelpResponse.class)
                        .setCallback(helpCallBack);

            }
        }else {
            Snackbar.make(layoutMain, R.string.error_network_connection, Snackbar.LENGTH_LONG).show();
        }

    }

    /**
     * Callback for help url
     */
    private FutureCallback<HelpResponse> helpCallBack = new FutureCallback<HelpResponse>() {
        @Override
        public void onCompleted(Exception exception, HelpResponse serverResponse) {
            if (null == exception) {
                if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_SUCCESS, serverResponse.getStatus())) {
                    url = serverResponse.getUrl();
                    showHelpProgress(false);
                    if (!TextUtils.isEmpty(url)) {
                        AppLog.showLog(TAG, "help url :: "+url);
                        startActivity(YoutubePlayerActivity.getStartIntent(HealthConsultantActivity.this, url));
                    } else{
                        showMessageToUser(serverResponse.getMessage());
                    }

                } else if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_FAILURE, serverResponse.getStatus())) {
                    AppLog.showLog(TAG, serverResponse.getMessage());
                    String message = serverResponse.getMessage();
                    showMessageToUser(message);
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
    };


    public void showHelpProgress(final boolean showProgress) {
        // hide submit menu when progress is visible
//        showSubmitMenu(!showProgress);
        mProgressView.setVisibility(showProgress ? View.VISIBLE : View.GONE);
        mProgressView.setProgressMessage(R.string.message_loading);
    }

    /**
     * Show the message and hide the ProgressBar
     *
     * @param messageId Resource id of message to show.
     */
    private void showMessageToUser(int messageId) {
        showMessageToUser(getString(messageId));
    }

    /**
     * Show the message and hide the ProgressBar
     *
     * @param message message to show
     */
    private void showMessageToUser(String message) {
        showHelpProgress(false);
        showProgress(false);
        if (ServerResponse.isTokenInvalid(message)) {
            AppUtil.showInvalidTokenSnackBar(layoutMain, message, this);
        }else{
            Snackbar.make(layoutMain, message, Snackbar.LENGTH_SHORT).show();
        }
    }

}
