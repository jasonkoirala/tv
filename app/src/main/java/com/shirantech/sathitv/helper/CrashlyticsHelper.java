package com.shirantech.sathitv.helper;

import com.crashlytics.android.Crashlytics;
import com.shirantech.sathitv.BuildConfig;

/**
 * Helper class to manage crashlytics crash/error reporting.
 */
public class CrashlyticsHelper {

    /**
     * Send the caught exception to track the caught issues.
     *
     * @param exception The exception caught
     */
    public static void sendCaughtException(Exception exception) {
        if (!BuildConfig.DEBUG) {
            Crashlytics.logException(exception);
        }
    }
}
