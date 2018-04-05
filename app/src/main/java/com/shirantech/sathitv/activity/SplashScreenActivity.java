package com.shirantech.sathitv.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.ImageView;
import android.view.View;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;
import com.shirantech.sathitv.BuildConfig;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.callbacks.OnLanguageSelectedCallback;
import com.shirantech.sathitv.helper.CrashlyticsHelper;
import com.shirantech.sathitv.helper.LanguageChooserHelper;
import com.shirantech.sathitv.helper.PreferencesHelper;
import com.shirantech.sathitv.helper.ScreenHelper;
import com.shirantech.sathitv.logger.Log;
import com.shirantech.sathitv.model.postparams.GcmRegistrationParams;
import com.shirantech.sathitv.model.response.GeneralResponse;
import com.shirantech.sathitv.network.ApiConstants;
import com.shirantech.sathitv.service.RegistrationIntentService;
import com.shirantech.sathitv.utils.AppLog;
import com.shirantech.sathitv.utils.AppUtil;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * An {@link Activity} showing splash screen.
 */
public class SplashScreenActivity extends BaseActivity implements OnLanguageSelectedCallback {
    private static final String TAG = SplashScreenActivity.class.getName();
    private static final String URL_GCM_REGISTER = ApiConstants.APP_USER_URL + "gcmRegister";
    private ImageView imageView;
    private TextView textView;
    String menuFragment;
    /**
     * Callback for gcm registration
     */
    private FutureCallback<GeneralResponse> mGcmRegistrationCallback = new FutureCallback<GeneralResponse>() {
        @Override
        public void onCompleted(Exception exception, GeneralResponse serverResponse) {
            if (null == exception) {
                if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_SUCCESS, serverResponse.getStatus())) {
                    PreferencesHelper.setTokenSent(SplashScreenActivity.this, true);
                } else {
                    // no-op
                    // do nothing when gcm token registration fails.
                    // there is always next time. :)
                    // TODO : you may track token registration failure using Google Analytics(or Crashlytics)
                }
            } else {
                Log.e(TAG, exception.getLocalizedMessage(), exception);
                CrashlyticsHelper.sendCaughtException(exception);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        imageView = (ImageView) findViewById(R.id.imageViewSplash);
        textView = (TextView) findViewById(R.id.textView);
        setScreenBackground();
        if (AppUtil.isInternetConnected(this)) {
            startGcmRegistration();
            askLanguagePreference();
            getIntentMessage();
            printHashKey(this);
        }else{
            imageView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);

        }
    }

    private void getIntentMessage(){
        if(getIntent().getExtras() != null) {
            menuFragment = getIntent().getExtras().getString("menuFragment");

            AppLog.showLog(TAG, "intent message "+menuFragment);
        }

    }

    /**
     * Set radial gradient background programmatically
     */
    @SuppressWarnings("deprecation")
    private void setScreenBackground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            findViewById(R.id.main_layout).setBackground(ScreenHelper.getSplashBg(SplashScreenActivity.this));
        } else {
            findViewById(R.id.main_layout).setBackgroundDrawable(ScreenHelper.getSplashBg(SplashScreenActivity.this));
        }
    }

    /**
     * Register in Gcm server to get the token. If already registered then send the token to the server only if logged in.
     * </p>
     * Note: Trying to get the token earlier so that it can be easily fetched later.
     */
    private void startGcmRegistration() {
        // get the gcm token from the gcm-server(google) if it has not been previously obtained
        if (TextUtils.isEmpty(PreferencesHelper.getGcmToken(SplashScreenActivity.this))) {
            AppLog.showLog(TAG, "empty");
            startService(RegistrationIntentService.getStartIntent(SplashScreenActivity.this));
        }
        // if user has logged in and gcm token has not been sent previously send the token to the server
        else if (!TextUtils.isEmpty(PreferencesHelper.getLoginToken(SplashScreenActivity.this)) &&
                !PreferencesHelper.isTokenSent(SplashScreenActivity.this)) {
            AppLog.showLog(TAG, "not empty");
            sendTokenToServer();
        }
    }

    /**
     * Send the gcm token(id) to the server.
     */
    private void sendTokenToServer() {
        if (AppUtil.isInternetConnected(SplashScreenActivity.this)) {
            AppLog.showLog(TAG, "send token to server");
            final String gcmToken = PreferencesHelper.getGcmToken(SplashScreenActivity.this);
            final String deviceId = AppUtil.getUniquePsuedoID(getApplication());
            final String versionName = BuildConfig.VERSION_NAME;
            final String osType = "Android";
            final int userId = PreferencesHelper.getUserId(SplashScreenActivity.this);
            AppLog.showLog(TAG, "user id ::"+userId);
            final Builders.Any.B builder = Ion.with(SplashScreenActivity.this)
                    .load(URL_GCM_REGISTER);
            if (BuildConfig.DEBUG) {
                builder.setLogging(TAG, Log.VERBOSE);
            }
            builder.setHeader(ApiConstants.HEADER_KEY_ACCEPT, ApiConstants.HEADER_ACCEPT_VALUE)
                    .setJsonPojoBody(new GcmRegistrationParams(deviceId, gcmToken, versionName, osType, userId))
                    .as(GeneralResponse.class)
                    .setCallback(mGcmRegistrationCallback);
        } else {
            Snackbar.make(imageView, "Please check your internet connection",Snackbar.LENGTH_SHORT).show();
            // no-op
            // try again next time
        }
    }

    /**
     * Show user with language choice if it has not been previously selected.
     */
    private void askLanguagePreference() {
        if (PreferencesHelper.checkIfLanguageSelected(this)) {
            onLanguageSelected();
        } else {
            new LanguageChooserHelper(this).showChoice(this);
        }
    }

    /**
     * Called when language selection is completed.
     */
    @Override
    public void onLanguageSelected() {
        LanguageChooserHelper.setLocale(this, PreferencesHelper.readLanguagePreference(this));
        startNextActivity();
    }

    /**
     * Start the next activity with time delay.
     */
    private void startNextActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(getNextStartIntent());
                finish();
            }
        }, 2000);
    }

    /**
     * Get the {@link Intent} to start the next activity.
     *
     * @return {@link Intent} to start {@link MainActivity} if already logged in else {@link LoginActivity}
     */
    private Intent getNextStartIntent() {
        if (PreferencesHelper.isLoggedIn(this)) {
            if(!TextUtils.isEmpty(menuFragment)){
                return MainActivity.getStartIntentWithExtras(SplashScreenActivity.this,menuFragment);
            }else {
                return MainActivity.getStartIntent(SplashScreenActivity.this);
            }
        } else {
//            return UserTypeActivity.getStartIntent(SplashScreenActivity.this);
            return LoginActivity.getStartIntent(SplashScreenActivity.this);
        }
    }


    public static void printHashKey(Context pContext) {
        try {
            String packageName = pContext.getApplicationContext().getPackageName();
            PackageInfo packageInfo = pContext.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                AppLog.showLog(TAG, "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "printHashKey()", e);
        } catch (Exception e) {
            Log.e(TAG, "printHashKey()", e);
        }
    }
}
