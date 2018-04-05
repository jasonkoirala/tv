package com.shirantech.sathitv.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;
import com.shirantech.sathitv.BuildConfig;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.helper.CrashlyticsHelper;
import com.shirantech.sathitv.helper.LoginHelper;
import com.shirantech.sathitv.helper.PreferencesHelper;
import com.shirantech.sathitv.helper.ScreenHelper;
import com.shirantech.sathitv.logger.Log;
import com.shirantech.sathitv.model.postparams.LoginParams;
import com.shirantech.sathitv.model.response.LoginResponse;
import com.shirantech.sathitv.network.ApiConstants;
import com.shirantech.sathitv.utils.AppLog;
import com.shirantech.sathitv.utils.AppUtil;
import com.shirantech.sathitv.widget.CustomFontButton;
import com.shirantech.sathitv.widget.CustomFontTextView;
import com.shirantech.sathitv.widget.CustomProgressView;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.IdName;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnProfileListener;

import java.io.File;
import java.net.URI;
import java.util.List;

/**
 * An {@link Activity} showing the login interface..
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = LoginActivity.class.getName();
    /* private static final String URL_LOGIN = ApiConstants.BASE_URL + ApiConstants.MID_URL + "appusernew/login";
     private static final String URL_REGISTER = ApiConstants.BASE_URL + ApiConstants.MID_URL + "/appusernew/register";*/
    private static final String URL_LOGIN = ApiConstants.BASE_URL + "appusernew/login";
    private static final String URL_REGISTER = ApiConstants.BASE_URL + "appusernew/register";

    private RelativeLayout layoutMain;
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin, buttonRegister;
    private CustomFontTextView textViewFacebook;
    private CustomProgressView progressViewLogin;
    private SimpleFacebook simpleFacebook;
    private String userType;
    private SimpleDraweeView imageView;
    private DownloadManager downloadManager;
    private Uri downloadUri;


    Profile profile;
    /**
     * Callback for login
     */
    private FutureCallback<LoginResponse> mLoginCallback = new FutureCallback<LoginResponse>() {
        @Override
        public void onCompleted(Exception exception, LoginResponse serverResponse) {
            if (null == exception) {
                if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_SUCCESS, serverResponse.getStatus())) {
                    AppLog.showLog(TAG, "sucess " + serverResponse.getMessage());
                    LoginHelper.setLogin(LoginActivity.this, serverResponse);
                    PreferencesHelper.setProfileImageUrl(LoginActivity.this, serverResponse.getImageUrl());
                    LoginHelper.setInCrashlytics(serverResponse.getUsername(), serverResponse.getEmail());
                    AppLog.showLog(TAG, "token" + PreferencesHelper.getLoginToken(LoginActivity.this));
                    showProgress(false);
                    startMainActivity();
                } else if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_FAILURE, serverResponse.getStatus())) {
                    AppLog.showLog(TAG, serverResponse.getMessage());
                    showFailureMessage(serverResponse.getMessage());
                    // TODO : track login failure using Google Analytics(or Crashlytics)
                } else {
                    showFailureMessage(R.string.error_problem_occurred);
                    // TODO : track connection/server error using Google Analytics(or Crashlytics)
                }
            } else {
                AppLog.showLog(TAG, " Login exception " + exception);
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
            Snackbar.make(layoutMain, errorMessageId, Snackbar.LENGTH_SHORT).show();
        }

        /**
         * Show the failure message when rating unsuccessful.
         *
         * @param errorMessage error message to show
         */
        private void showFailureMessage(String errorMessage) {
            showProgress(false);
            Snackbar.make(layoutMain, errorMessage, Snackbar.LENGTH_SHORT).show();
        }
    };

    /**
     * Get the {@link Intent} to start this activity
     *
     * @param context context to initialize the Intent.
     * @return the Intent to start {@link LoginActivity}
     */
    public static Intent getStartIntent(final Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_black);
        setScreenBackground();
        initViews();
        isStoragePermissionGranted();
        AppLog.showLog(TAG, "Photo login::  " + PreferencesHelper.getProfileImageUrl(LoginActivity.this));

    }


    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }


    }

    OnLoginListener onLoginListener = new OnLoginListener() {
        @Override
        public void onLogin(String accessToken, List<Permission> acceptedPermissions, List<Permission> declinedPermissions) {
            // change the state of the button or do whatever you want
            AppLog.showLog(TAG, "Logged in");
            AppLog.showLog(TAG, "token" + accessToken);
            Profile.Properties properties = new Profile.Properties.Builder()
                    .add(Profile.Properties.EMAIL)
                    .add(Profile.Properties.ID)
                    .add(Profile.Properties.NAME)
                    .add(Profile.Properties.PICTURE)
                    .add(Profile.Properties.FIRST_NAME)
                    .add(Profile.Properties.COVER)
                    .add(Profile.Properties.WORK)
                    .build();
            showProgress(true);
            simpleFacebook.getProfile(properties, new ProfileRequestListener());
        }

        @Override
        public void onCancel() {
            // user canceled the dialog
        }

        @Override
        public void onFail(String reason) {
            // failed to login
            AppUtil.showAlert(reason, LoginActivity.this);
        }

        @Override
        public void onException(Throwable throwable) {
            // exception from facebook
            AppUtil.showAlert(throwable.toString(), LoginActivity.this);
        }

    };

    /**
     * Set radial gradient background programmatically
     */
    @SuppressWarnings("deprecation")
    private void setScreenBackground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            findViewById(R.id.layoutLoginMain).setBackground(ScreenHelper.getSplashBg(LoginActivity.this));
        } else {
            findViewById(R.id.layoutLoginMain).setBackgroundDrawable(ScreenHelper.getSplashBg(LoginActivity.this));
        }
    }

    /*
    * views initialization
    * */
    private void initViews() {
        layoutMain = (RelativeLayout) findViewById(R.id.layoutLoginMain);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        editTextPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(this);

        buttonLogin = (CustomFontButton) findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(this);
        progressViewLogin = (CustomProgressView) findViewById(R.id.progressViewLogin);
        imageView = (SimpleDraweeView) findViewById(R.id.imageViewLogin);
        textViewFacebook = (CustomFontTextView) findViewById(R.id.textViewFacebook);
        textViewFacebook.setOnClickListener(this);
        AppLog.showLog(TAG, "pro pic." + PreferencesHelper.getProfileImageUrl(LoginActivity.this));
        if (!TextUtils.isEmpty(PreferencesHelper.getProfileImageUrl(LoginActivity.this))) {
            showPhoto(PreferencesHelper.getProfileImageUrl(LoginActivity.this));
        } else {
            AppLog.showLog(TAG, "No previous uploaded photos.");
        }
    }

    @Override
    protected void onStop() {
        Ion.getDefault(this).cancelAll(this);
        super.onStop();
    }

    @Override
    public void onClick(View clickedView) {
        switch (clickedView.getId()) {
            case R.id.buttonLogin:
                attemptLogin();
                break;
            case R.id.buttonRegister:
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
//                intent.putExtra(RegistrationActivity.EXTRA_USER_TYPE, userType);
                startActivity(intent);
                break;
            case R.id.textViewFacebook:
                setAlertDialogForLoginAs();
                AppLog.showLog(TAG, "button facebook");

                break;
        }
    }


    /*
    * Shows photo in imageview in login section
    * */
    public void showPhoto(String image) {
        AppLog.showLog(TAG, "image ::" + image);
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(
                Uri.parse(image))
                .setProgressiveRenderingEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(imageView.getController())
                .build();
        imageView.setController(controller);

    }

    /*
    * selects either model or user
    * */

    private void setAlertDialogForLoginAs() {
        final CharSequence[] items = {"Model", "User"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("You want to login as :");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
//                Toast.makeText(getApplicationContext(), items[id], Toast.LENGTH_SHORT).show();
                switch (id) {
                    case 0:
                        userType = "1";
                        break;
                    case 1:
                        userType = "0";
                        break;
                    default:
                        break;

                }
                simpleFacebook.login(onLoginListener);
            }


        });
        AlertDialog alert = builder.create();
        alert.show();


    }

    private void attemptLogin() {
        AppUtil.hideSoftKeyboard(LoginActivity.this);

        // reset errors
        resetError();

        // Store values at the time of the login attempt.
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            showPasswordError(R.string.error_password_required);
            focusView = editTextPassword;
            cancel = true;
        } else if (password.length() < 6) {
            showPasswordError(R.string.error_password_short);
            focusView = editTextPassword;
            cancel = true;
        }
        // Check for a valid email.
        if (TextUtils.isEmpty(email)) {
            showEmailError(R.string.error_email_required);
            focusView = editTextEmail;
            cancel = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showEmailError(R.string.error_invalid_email);
            focusView = editTextEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            startLogin(email, password);
        }
    }

    /**
     * Reset the errors in the TextInputLayouts
     */
    @VisibleForTesting
    protected void resetError() {
        editTextEmail.setError(null);
        editTextPassword.setError(null);
    }

    /**
     * Start the login process
     *
     * @param email    user email id
     * @param password password for login
     */
    private void startLogin(String email, String password) {
        if (AppUtil.isInternetConnected(this)) {
            final Builders.Any.B builder = Ion.with(this).load(URL_LOGIN);
            if (BuildConfig.DEBUG) {
                builder.setLogging(TAG, Log.VERBOSE);
            }
            String gcmToken = PreferencesHelper.getGcmToken(LoginActivity.this);
            AppLog.showLog(TAG, "gcmid : " + gcmToken);
            final String deviceId = AppUtil.getUniquePsuedoID(getApplication());
            final String versionName = BuildConfig.VERSION_NAME;
            final String osType = "Android";
            builder.setHeader(ApiConstants.HEADER_KEY_ACCEPT, ApiConstants.HEADER_ACCEPT_VALUE)
                    .setJsonPojoBody(new LoginParams(email, password, gcmToken, deviceId, versionName, osType))
                    .as(LoginResponse.class)
                    .setCallback(mLoginCallback);
        } else {
            showProgress(false);
            Snackbar.make(layoutMain, R.string.error_network_connection, Snackbar.LENGTH_LONG).show();
        }
    }

    public void showEmailError(int errorMessageId) {
        editTextEmail.setError(getString(errorMessageId));
    }

    public void showPasswordError(int errorMessageId) {
        editTextPassword.setError(getString(errorMessageId));
    }

    /**
     * Shows the progress UI and hides the login form or vice versa.
     *
     * @param show <code>true</code> to show the progress <code>false</code> otherwise.
     */
    public void showProgress(final boolean show) {
        progressViewLogin.setVisibility(show ? View.VISIBLE : View.GONE);
        progressViewLogin.setProgressMessage(R.string.message_logging_in);
    }

    private void startMainActivity() {
        startActivity(MainActivity.getStartIntent(LoginActivity.this).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        simpleFacebook = SimpleFacebook.getInstance(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        simpleFacebook.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class ProfileRequestListener extends OnProfileListener {
        @Override
        public void onFail(String reason) {
            AppLog.showLog(TAG, "failed : " + reason);
            // insure that you are logged in before getting the profile
            AppUtil.showAlert(reason, LoginActivity.this);

        }

        @Override
        public void onException(Throwable throwable) {
            AppLog.showLog(TAG, "onException : " + throwable);
            AppUtil.showAlert(throwable.toString(), LoginActivity.this);

        }


        @Override

        public void onThinking() {
            // do nothing
        }

        @Override
        public void onComplete(Profile profile) {
            showProgress(true);
            downloadFacebookProfileImage(profile);
        }
    }

    /*
    * set profile image to preferences and download photo
    * */
    private void downloadFacebookProfileImage(Profile profile) {
        this.profile = profile;

        /*Log.e(TAG, "profile email : " + profile.getEmail());
        AppLog.showLog(TAG, "profile email : " + profile.getEmail());

        AppLog.showLog(TAG, "profile image : " + profile.getId());
        AppLog.showLog(TAG, "profile picture : " + "http://graph.facebook.com/" + profile.getId() + "/picture?type=square");*/
        PreferencesHelper.setProfileImageUrl(this, "http://graph.facebook.com/" + profile.getId() + "/picture?type=square");
        AppLog.showLog(TAG, "profile location : " + profile.getLocation());
        AppLog.showLog(TAG, "profile email : " + profile.getEmail());

        try {
            downloadFile();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * downloadUri
     * Callback for registration. Same {@link LoginResponse} can be used for registration response
     */
    private FutureCallback<LoginResponse> callback = new FutureCallback<LoginResponse>() {
        @Override
        public void onCompleted(Exception exception, LoginResponse serverResponse) {
            if (null == exception) {
                if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_SUCCESS, serverResponse.getStatus())) {
                    AppLog.showLog(TAG, "success");
                    LoginHelper.setLogin(LoginActivity.this, serverResponse);
                    LoginHelper.setInCrashlytics(serverResponse.getUsername(), serverResponse.getEmail());
                    showProgress(false);
                    startMainActivity();
                } else if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_FAILURE, serverResponse.getStatus())) {
                    AppLog.showLog(TAG, serverResponse.getMessage() + serverResponse);
                    AppLog.showLog(TAG, "error code ::" + serverResponse.getCode());
                    int errorCode = serverResponse.getCode();
                  /*  if(errorCode == 1001){
                        showFailureMessage("");
                    }else{*/
                    showFailureMessage(serverResponse.getMessage());
//                    }
                    // TODO : track registration failure using Google Analytics(or Crashlytics)
                } else {
                    showFailureMessage(R.string.error_problem_occurred);
                    // TODO : track connection/server error using Google Analytics(or Crashlytics)
                }
            } else {
                AppLog.showLog(TAG, "Response exception :" + exception);
                showFailureMessage(R.string.error_problem_occurred);
                CrashlyticsHelper.sendCaughtException(exception);
            }
        }

    };


    /*
    * send registration request to server
    * */
    private void startRegistration(String id, String name, String email, IdName location, File imageFile) {
        if (AppUtil.isInternetConnected(this)) {
            final Builders.Any.B builder = Ion.with(this).load(URL_REGISTER);
            AppLog.showLog(TAG, "URL_REGISTER ::::" + URL_REGISTER);
            if (BuildConfig.DEBUG) {
                builder.setLogging(TAG, Log.VERBOSE);
            }

            String gcmToken = PreferencesHelper.getGcmToken(LoginActivity.this);
            final String deviceId = AppUtil.getUniquePsuedoID(getApplication());
            final String osType = "Android";
            final String versionName = BuildConfig.VERSION_NAME;

            AppLog.showLog(TAG, "email::::" + email);

            if (null == imageFile) {
                builder.setHeader(ApiConstants.HEADER_KEY_ACCEPT, ApiConstants.HEADER_ACCEPT_VALUE)
                        .setMultipartParameter("name", name)
                        .setMultipartParameter("password", "")
                        .setMultipartParameter("mobileNumber", "")
                        .setMultipartParameter("email", email)
                        .setMultipartParameter("country", String.valueOf(location))
                        .setMultipartParameter("loginType", "FB")
                        .setMultipartParameter("fbId", id)
                        .setMultipartParameter("gcmId", gcmToken)
                        .setMultipartParameter("userType", userType)
                        .setMultipartParameter("deviceId", deviceId)
                        .setMultipartParameter("OSType", osType)
                        .setMultipartParameter("versionName", versionName)
                        .setMultipartParameter("description", "")
                        .as(LoginResponse.class)
                        .setCallback(callback);
            } else {
                builder.setHeader(ApiConstants.HEADER_KEY_ACCEPT, ApiConstants.HEADER_ACCEPT_VALUE)
                        .setMultipartParameter("name", name)
                        .setMultipartParameter("password", "")
                        .setMultipartParameter("mobileNumber", "")
                        .setMultipartParameter("email", email)
                        .setMultipartParameter("country", String.valueOf(location))
                        .setMultipartParameter("loginType", "FB")
                        .setMultipartParameter("fbId", id)
                        .setMultipartParameter("gcmId", gcmToken)
                        .setMultipartParameter("userType", userType)
                        .setMultipartParameter("deviceId", deviceId)
                        .setMultipartParameter("OSType", osType)
                        .setMultipartParameter("versionName", versionName)
                        .setMultipartParameter("description", "")
                        .setMultipartFile("profile_image", imageFile)
                        .as(LoginResponse.class)
                        .setCallback(callback);
            }

            showProgress(false);

        } else {
            Snackbar.make(layoutMain, R.string.error_network_connection, Snackbar.LENGTH_LONG).show();
        }
    }


    /*
    * download profile picture of facebook
    * */
    private void downloadFile() {
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .mkdirs();
        downloadManager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
        registerReceiver(onComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        downloadUri = Uri.parse("http://graph.facebook.com/" + profile.getId() + "/picture?type=square");
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);
        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "sathiImage.jpg");
        downloadManager.enqueue(request);
    }


    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            AppLog.showLog(TAG, "Broadcast reciver on Complete file name " + getPathOfDownloadedFile(intent));

            if (null == profile.getName()) {
                startRegistration(profile.getId(), "", profile.getEmail(), profile.getLocation(), getPathOfDownloadedFile(intent));
                PreferencesHelper.setEmail(LoginActivity.this, profile.getEmail());
            } else if (null == profile.getEmail()) {
                AppLog.showLog(TAG, "This is null email" );
                startRegistration(profile.getId(), profile.getName(), "", profile.getLocation(), getPathOfDownloadedFile(intent));
            } else if (null == profile.getEmail() && null == profile.getName()) {
                startRegistration(profile.getId(), "", "", profile.getLocation(), getPathOfDownloadedFile(intent));
            } else {
                PreferencesHelper.setEmail(LoginActivity.this, profile.getEmail());
                startRegistration(profile.getId(), profile.getName(), profile.getEmail(), profile.getLocation(), getPathOfDownloadedFile(intent));
            }

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(onComplete);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /* public File getPathOfDownloadedFile(Intent intent) {
        File downloadedFile = null;
        Bundle extras = intent.getExtras();
        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterById(extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID));
        Cursor c = downloadManager.query(q);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            AppLog.showLog(TAG, "DownloadManager " + intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1));
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                Uri uri = downloadManager.getUriForDownloadedFile(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1));

                AppLog.showLog(TAG, "file path  " +uri.getPath());
                downloadedFile = new File(uri.getPath());
                if (downloadedFile.exists()) {
                    AppLog.showLog(TAG, "file exist");
                } else {
                    AppLog.showLog(TAG, "file not exist");
                }
            }
        }
        c.close();
        return downloadedFile;
    }*/


    /*
    * get path of the downloaded profile picture and convert it into file
    * */
    public File getPathOfDownloadedFile(Intent intent) {
        File downloadedFile = null;
        Bundle extras = intent.getExtras();
        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterById(extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID));
        Cursor c = downloadManager.query(q);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                String fileUri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                if (fileUri != null) {
                    File mFile = new File(Uri.parse(fileUri).getPath());
                    String filePath = mFile.getAbsolutePath();
                    downloadedFile = new File(filePath);
                    AppLog.showLog(TAG, "download file" + downloadedFile);
                    if (downloadedFile.exists()) {
                        AppLog.showLog(TAG, "file exist");
                    } else {
                        AppLog.showLog(TAG, "file not exist");
                    }
                }else{
                    System.out.println("fileUri Null");
                }
            }
        }
        c.close();
        return downloadedFile;
    }

    /**
     * Show the failure message when rating unsuccessful.
     *
     * @param errorMessageId String id of error message to show
     */
    private void showFailureMessage(int errorMessageId) {
        showProgress(false);
        Snackbar.make(layoutMain, errorMessageId, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Show the failure message when rating unsuccessful.
     *
     * @param errorMessage error message to show
     */
    private void showFailureMessage(String errorMessage) {
        showProgress(false);
        Snackbar.make(layoutMain, errorMessage, Snackbar.LENGTH_SHORT).show();
    }
};
