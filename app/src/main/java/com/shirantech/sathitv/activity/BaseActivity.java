package com.shirantech.sathitv.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.shirantech.sathitv.BuildConfig;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.logger.Log;
import com.shirantech.sathitv.logger.LogWrapper;

/**
 * A Base {@link AppCompatActivity} which will set orientation to portrait in handsets and both
 * portrait and landscape option in tablets. It also initializes logging.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!BuildConfig.DEBUG) {
            initializeLogging();
        }
    }

    /**
     * Set up targets to receive log data
     */
    protected void initializeLogging() {
        // Wraps Android's native log framework
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);

        Log.i(TAG, "Ready");
    }
}
