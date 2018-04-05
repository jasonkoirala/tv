package com.shirantech.sathitv.service;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * A {@link InstanceIDListenerService} to handle the creation, rotation
 * and updating of registration tokens.
 */
public class SathiInstanceIDListenerService extends InstanceIDListenerService {

    private static final String TAG = SathiInstanceIDListenerService.class.getSimpleName();

    /**
     * Called if InstanceID token is updated. This may occur if the security of the previous
     * token had been compromised. This call is initiated by the InstanceID provider.
     */
    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
