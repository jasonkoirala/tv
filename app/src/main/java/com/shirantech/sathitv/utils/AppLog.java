package com.shirantech.sathitv.utils;

import android.util.Log;


public class AppLog {

    private static final String APP_TAG = "SATHITV";

    public static int showLog(String message) {
        if(com.shirantech.sathitv.BuildConfig.DEBUG) {
            try {
                return Log.i(APP_TAG, message);
            } catch (NullPointerException e) {
                return 0;
            }
        }else{
            return 0;
        }
    }
    public static int showLog(String tag,String message) {
        if(com.shirantech.sathitv.BuildConfig.DEBUG) {
        try {
            return Log.i(tag, message);
        } catch (NullPointerException e) {
            return 0;
        }
        }else{
            return 0;
        }
    }
}
