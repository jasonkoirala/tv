package com.shirantech.sathitv.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.helper.PreferencesHelper;
import com.shirantech.sathitv.logger.Log;

/**
 * Registers the application with GCM servers asynchronously.
 * <p/>
 * Stores the gcm token in the application's shared preferences.
 */
public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";

    /**
     * Get Intent to start {@link RegistrationIntentService}.
     *
     * @param context context to initialize the intent with.
     * @return the intent to start the service
     */
    public static Intent getStartIntent(Context context) {
        return new Intent(context, RegistrationIntentService.class);
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public RegistrationIntentService() {
        super(TAG);
    }

    /**
     * This method is invoked on the worker thread with a request to process.
     * Only one Intent is processed at a time, but the processing happens on a
     * worker thread that runs independently from other application logic.
     * So, if this code takes a long time, it will hold up other requests to
     * the same IntentService, but it will not hold up anything else.
     * When all requests have been handled, the IntentService stops itself,
     * so you should not call {@link #stopSelf}.
     *
     * @param intent The value passed to {@link Context#startService(Intent)}.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            // In the (unlikely) event that multiple refresh operations occur simultaneously,
            // ensure that they are processed sequentially.
            synchronized (TAG) {
                // Initially this call goes out to the network to retrieve the token, subsequent calls
                // are local.
                // [START get_token]
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                // [END get_token]
                Log.i(TAG, "GCM Registration Token: " + token);

                // store the gcm token in preference
                PreferencesHelper.setGcmToken(this, token);
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            PreferencesHelper.setGcmToken(this, null);
        }
    }
}
