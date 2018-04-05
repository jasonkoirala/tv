package com.shirantech.sathitv.helper;

import android.content.Context;

//import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.Crashlytics;
import com.shirantech.sathitv.BuildConfig;
import com.shirantech.sathitv.model.response.LoginResponse;

/**
 * Helper class to manage the login in sathi tv.
 */
public class LoginHelper {

    /**
     * Store data(token, user id, username) from successful login/register in preference
     *
     * @param context       context to use
     * @param loginResponse response from server
     */
    public static void setLogin(final Context context, final LoginResponse loginResponse) {
        PreferencesHelper.setLoginToken(context, loginResponse.getToken());
        PreferencesHelper.setUserId(context, loginResponse.getUserId());
        PreferencesHelper.setUsername(context, loginResponse.getUsername());
    }

    /**
     * Set username and user email in the crashlytics to use advanced user id features
     *
     * @param username  username to set
     * @param userEmail user email to set
     */
    public static void setInCrashlytics(final String username, final String userEmail) {
        if (!BuildConfig.DEBUG) {
            Crashlytics.getInstance().core.setUserName(username);
            Crashlytics.getInstance().core.setUserEmail(userEmail);
        }
    }
}
