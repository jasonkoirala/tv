package com.shirantech.sathitv.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;
import com.shirantech.sathitv.BuildConfig;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.helper.CrashlyticsHelper;
import com.shirantech.sathitv.helper.FileHelper;
import com.shirantech.sathitv.helper.LoginHelper;
import com.shirantech.sathitv.helper.PreferencesHelper;
import com.shirantech.sathitv.helper.ScreenHelper;
import com.shirantech.sathitv.logger.Log;
import com.shirantech.sathitv.model.response.LoginResponse;
import com.shirantech.sathitv.network.ApiConstants;
import com.shirantech.sathitv.utils.AppLog;
import com.shirantech.sathitv.utils.AppUtil;
import com.shirantech.sathitv.widget.CustomProgressView;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * An {@link Activity} for the registration process.
 */
public class RegistrationActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "Registration";

//    private static final String URL_REGISTER = ApiConstants.BASE_URL + ApiConstants.MID_URL + "/appusernew/register";
    private static final String URL_REGISTER = ApiConstants.BASE_URL + "appusernew/register";
    public static final String EXTRA_USER_TYPE = "userType";
    public static final int SELECT_IMAGE = 0;
    private RelativeLayout mMainLayout;
    private EditText editTextName, editTextPassword, editTextPhone, editTextEmail, editTextDescription;
    private AutoCompleteTextView mCountryAutoCompleteTextView;
    private Button mRegisterButton, buttonUploadPhoto;
    private RadioButton radioButton;
    private RadioGroup radioGroupRegisterAs;
    private ImageView imageViewRegistration;
    private CustomProgressView progressViewRegistration;

    Uri imageUri;
    private File imageFile = null;

    private List<String> countryList;

    /**
     * Callback for registration. Same {@link LoginResponse} can be used for registration response
     */
    private FutureCallback<LoginResponse> mRegistrationCallback = new FutureCallback<LoginResponse>() {
        @Override
        public void onCompleted(Exception exception, LoginResponse serverResponse) {
            AppLog.showLog(TAG, "serverResponse :: " + serverResponse);
            if (null == exception) {
                if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_SUCCESS, serverResponse.getStatus())) {
                    LoginHelper.setLogin(RegistrationActivity.this, serverResponse);
                    LoginHelper.setInCrashlytics(serverResponse.getUsername(), serverResponse.getEmail());
                    showProgress(false);
                    String imageUrl = serverResponse.getImageUrl();
                    PreferencesHelper.setProfileImageUrl(RegistrationActivity.this,imageUrl);
                    startMainActivity();
                } else if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_FAILURE, serverResponse.getStatus())) {
                    AppLog.showLog(TAG, serverResponse.getMessage());
                    showFailureMessage(serverResponse.getMessage());
                    // TODO : track registration failure using Google Analytics(or Crashlytics)
                } else {
                    showFailureMessage(R.string.error_problem_occurred);
                    // TODO : track connection/server error using Google Analytics(or Crashlytics)
                }
            } else {
                AppLog.showLog(TAG, "exception :: " + exception);
//                showFailureMessage(serverResponse.getMessage());
                showFailureMessage(R.string.error_problem_occurred);
                CrashlyticsHelper.sendCaughtException(exception);
            }
        }

        /**
         * Show the failure message when rating unsuccessful.
         *
         * @param errorMessageId String id of error message to show
         */
        private void showFailureMessage(int errorMessageId) {
            showProgress(false);
            Snackbar.make(mMainLayout, errorMessageId, Snackbar.LENGTH_SHORT).show();
        }

        /**
         * Show the failure message when rating unsuccessful.
         *
         * @param errorMessage error message to show
         */
        private void showFailureMessage(String errorMessage) {
            showProgress(false);
            Snackbar.make(mMainLayout, errorMessage, Snackbar.LENGTH_SHORT).show();
        }
    };

    /**
     * Get the {@link Intent} to start this activity
     *
     * @param context context to initialize the Intent.
     * @return the Intent to start {@link RegistrationActivity}
     */
    public static Intent getStartIntent(final Context context) {
        return new Intent(context, RegistrationActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_black);
        initViews();
        setScreenBackground();
    }

    /**
     * Set radial gradient background programmatically
     */
    @SuppressWarnings("deprecation")
    private void setScreenBackground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            findViewById(R.id.main_layout).setBackground(ScreenHelper.getSplashBg(RegistrationActivity.this));
        } else {
            findViewById(R.id.main_layout).setBackgroundDrawable(ScreenHelper.getSplashBg(RegistrationActivity.this));
        }
    }

    private void initViews() {
        mMainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        editTextName = (EditText) findViewById(R.id.editTextRegistrationName);
        editTextPassword = (EditText) findViewById(R.id.editTextRegistrationPassword);
        editTextPhone = (EditText) findViewById(R.id.editTextRegistrationPhone);
        editTextEmail = (EditText) findViewById(R.id.editTextRegistrationEmail);
        editTextDescription = (EditText) findViewById(R.id.editTextRegistrationDescription);
        buttonUploadPhoto = (Button) findViewById(R.id.buttonUploadRegistration);
        buttonUploadPhoto.setOnClickListener(this);
        imageViewRegistration = (ImageView) findViewById(R.id.imageViewRegistration);
        radioGroupRegisterAs = (RadioGroup) findViewById(R.id.radioGroupRegisterAs);
        mCountryAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.country_autoCompleteTextView);

        progressViewRegistration = (CustomProgressView) findViewById(R.id.progressViewRegistration);
        mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(this);
        mCountryAutoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });
        setupCountryAutoComplete();




    }

    private void setupCountryAutoComplete() {
        countryList = Arrays.asList(getResources().getStringArray(R.array.country_array));
        mCountryAutoCompleteTextView.setAdapter(new ArrayAdapter<>(
                RegistrationActivity.this, android.R.layout.simple_list_item_1, countryList));
    }

    @Override
    public void onClick(View clickedView) {
        switch (clickedView.getId()) {
            case R.id.register_button:
                AppLog.showLog(TAG,"register ");
                try {
                        attemptRegister();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.buttonUploadRegistration:
                Intent intentUpload = new Intent();
                intentUpload.setType("image/*");
                intentUpload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intentUpload, "Select picture"), SELECT_IMAGE);
                break;
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
                            imageViewRegistration.setImageURI(imageUri);
                            convertIntoFile(imageUri);
                            AppLog.showLog(TAG, "image uri ::" + imageUri);
                        }
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


    private void attemptRegister() {
        AppUtil.hideSoftKeyboard(RegistrationActivity.this);

        // reset errors
        resetError();

        // Store values at the time of the register attempt.
        final String name = editTextName.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String phone = editTextPhone.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String country = mCountryAutoCompleteTextView.getText().toString().trim();
        final String description = editTextDescription.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a proper country name
        if (TextUtils.isEmpty(country)) {
            showError(mCountryAutoCompleteTextView, R.string.error_country_required);
            focusView = mCountryAutoCompleteTextView;
            cancel = true;
        } else if (!countryList.contains(country)) {
            showError(mCountryAutoCompleteTextView, R.string.error_country_invalid);
            focusView = mCountryAutoCompleteTextView;
            cancel = true;
        }

        // Check for a valid email.
        if (TextUtils.isEmpty(email)) {
            showError(editTextEmail, R.string.error_email_required);
            focusView = editTextEmail;
            cancel = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError(editTextEmail, R.string.error_invalid_email);
            focusView = editTextEmail;
            cancel = true;
        }

        // Check for a valid phone number.
        if (TextUtils.isEmpty(phone)) {
            showError(editTextPhone, R.string.error_phone_required);
            focusView = editTextPhone;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            showError(editTextPassword, R.string.error_password_required);
            focusView = editTextPassword;
            cancel = true;
        } else if (password.length() < 6) {
            showError(editTextPassword, R.string.error_password_short);
            focusView = editTextPassword;
            cancel = true;
        }

        // Check for a valid name.
        if (TextUtils.isEmpty(name)) {
            showError(editTextName, R.string.error_name_required);
            focusView = editTextName;
            cancel = true;
        }


        // Check for a valid name.
        if (TextUtils.isEmpty(name)) {
            showError(editTextDescription, R.string.error_description_required);
            focusView = editTextDescription;
            cancel = true;
        }


        if(null == imageUri){
            Toast.makeText(RegistrationActivity.this, "Please select your photo. ", Toast.LENGTH_SHORT).show();
        }
        if (cancel) {
            // There was an error; don't attempt register and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user registration attempt.
//            showProgress(true);
            showProgress(true);
            startRegistration(name, password, phone, email, country, description);
        }
    }

    /**
     * Reset the error messages in all {@link TextInputLayout}
     */
    @VisibleForTesting
    protected void resetError() {
        editTextName.setError(null);
        editTextPassword.setError(null);
        editTextPhone.setError(null);
        editTextEmail.setError(null);
        editTextDescription.setError(null);
        mCountryAutoCompleteTextView.setError(null);
    }

    /**
     * Show error message in the provided {@link TextInputLayout}
     *
     * @param editText       {@link EditText} to show the error
     * @param errorMessageId id of error message to show
     */
    private void showError(final EditText editText, final int errorMessageId) {
        editText.setError(getString(errorMessageId));
    }

    /**
     * Shows the progress UI and hides the register form or vice versa.
     *
     * @param show <code>true</code> to show the progress <code>false</code> otherwise.
     */
    public void showProgress(final boolean show) {
        progressViewRegistration.setVisibility(show ? View.VISIBLE : View.GONE);
            progressViewRegistration.setProgressMessage(R.string.message_register);
    }


    /**
     * Start the registration task with the information provided
     *
     * @param name     name of the user
     * @param password password for the account
     * @param phone    phone number of the user
     * @param email    email of the user
     * @param country  country of the user
     */
    private void startRegistration(String name, String password, String phone, String email, String country, String description) {
        if (AppUtil.isInternetConnected(this)) {
            final Builders.Any.B builder = Ion.with(this).load(URL_REGISTER);
            if (BuildConfig.DEBUG) {
                builder.setLogging(TAG, Log.VERBOSE);
            }
            String gcmToken = PreferencesHelper.getGcmToken(RegistrationActivity.this);
            final String deviceId = AppUtil.getUniquePsuedoID(getApplication());
            final String osType = "Android";
            final String versionName = BuildConfig.VERSION_NAME;
            radioButton = (RadioButton) findViewById(radioGroupRegisterAs.getCheckedRadioButtonId());
            final String registerAs = radioButton.getText().toString();
            AppLog.showLog(TAG, "registerAs" + registerAs);
            AppLog.showLog(TAG, "registerAs" + registerAs);
            String userType;
            if(registerAs.equalsIgnoreCase("Model")){
                userType = "1";
            }else{
                userType = "0";
            }
            AppLog.showLog(TAG, "post data  :: " + name+" "+password+" "+phone+" "+email+" "+ country+" "+gcmToken+
            " "+userType+" "+deviceId+" "+description+" "+osType+" "+versionName+ " "+new File(getRealPathFromURI(imageUri)));
            builder.setHeader(ApiConstants.HEADER_KEY_ACCEPT, ApiConstants.HEADER_ACCEPT_VALUE)

                    .setMultipartParameter("name", name)
                    .setMultipartParameter("password", password)
                    .setMultipartParameter("mobileNumber", phone)
                    .setMultipartParameter("email", email)
                    .setMultipartParameter("country", country)
                    .setMultipartParameter("loginType", "NORMAL")
                    .setMultipartParameter("fbId", "0")
                    .setMultipartParameter("userType", userType)
                    .setMultipartParameter("gcmId", gcmToken)
                    .setMultipartParameter("deviceId", deviceId)
                    .setMultipartParameter("description", description)
                    .setMultipartParameter("OSType", osType)
                    .setMultipartParameter("versionName", versionName)
                    .setMultipartFile("profile_image", new File(getRealPathFromURI(imageUri)))
                    .as(LoginResponse.class)
                    .setCallback(mRegistrationCallback);
        } else {
            showProgress(false);
            Snackbar.make(mMainLayout, R.string.error_network_connection, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Start the {@link MainActivity} without prompting user to login
     */
    private void startMainActivity() {
        startActivity(MainActivity.getStartIntent(RegistrationActivity.this)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }
}
