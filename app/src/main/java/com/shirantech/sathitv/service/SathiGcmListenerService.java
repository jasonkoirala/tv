package com.shirantech.sathitv.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;


import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.activity.ChatActivity;
import com.shirantech.sathitv.activity.HeathConsultantReplyActivity;
import com.shirantech.sathitv.activity.JanamKundaliReplyActivity;
import com.shirantech.sathitv.activity.SplashScreenActivity;
import com.shirantech.sathitv.fragment.HealthConsultantReplyFragment;
import com.shirantech.sathitv.helper.CrashlyticsHelper;
import com.shirantech.sathitv.helper.PreferencesHelper;
import com.shirantech.sathitv.model.HealthConsultant;
import com.shirantech.sathitv.model.response.GcmResponse;
import com.shirantech.sathitv.utils.AppLog;

/**
 * A {@link GcmListenerService} which enables various aspects of handling messages such as
 * detecting different downstream message types, determining upstream send status, and
 * automatically displaying simple notifications on the app’s behalf.
 */
public class SathiGcmListenerService extends GcmListenerService {

    private static final String TAG = SathiGcmListenerService.class.getSimpleName();
    private GcmResponse gcmResponse;

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        AppLog.showLog(TAG, "From: " + from);
        AppLog.showLog(TAG, "Message::::  " + message);
        AppLog.showLog("GCM" + message);

        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */

        try {
            gcmResponse = new Gson().fromJson(message, GcmResponse.class);
            sendNotification(gcmResponse);


        } catch (NullPointerException ne) {
            CrashlyticsHelper.sendCaughtException(ne);
        }
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param gcmResponse GCM message received.
     */
    private void sendNotification(GcmResponse gcmResponse) {
        Intent intent;
        int notificationId = gcmResponse.getNotificationId();
        AppLog.showLog(TAG, "notification id :: " + notificationId);
        if (gcmResponse.getMessageType().equals("Chitchat")) {
            intent = new Intent(this, ChatActivity.class);
            intent.putExtra(ChatActivity.EXTRA_MESSAGE, gcmResponse.getGcmMessage());
            intent.putExtra(ChatActivity.EXTRA_ID, gcmResponse.getSenderId());
            intent.putExtra(ChatActivity.EXTRA_NAME, gcmResponse.getName());
            intent.putExtra(ChatActivity.EXTRA_IMAGE, PreferencesHelper.getImageUrl(this));
//            broadcastIntent();
        } else if (gcmResponse.getMessageType().equals("Consultant")) {
            intent = new Intent(this, HeathConsultantReplyActivity.class);
            intent.putExtra(HeathConsultantReplyActivity.EXTRA_MESSAGE, gcmResponse.getGcmMessage());
            AppLog.showLog(TAG, "message:: " + gcmResponse.getGcmMessage());

        } else if (gcmResponse.getMessageType().equals("Rashifal")) {
            intent = new Intent(this, JanamKundaliReplyActivity.class);
            intent.putExtra(JanamKundaliReplyActivity.EXTRA_MESSAGE, gcmResponse.getGcmMessage());
            AppLog.showLog(TAG, "message:: " + gcmResponse.getGcmMessage());

        } else {
            intent = new Intent(this, SplashScreenActivity.class);

        }


        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        AppLog.showLog("Get gcm Response" + gcmResponse.getMessageType());
        intent.putExtra("menuFragment", gcmResponse.getMessageType());

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                // TODO : change notification icon
                .setSmallIcon(R.drawable.icon_notification)
                .setContentTitle(gcmResponse.getGcmTitle())
                .setContentText(gcmResponse.getGcmMessage())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId/* ID of notification */, notificationBuilder.build());
    }

   /* public void broadcastIntent()
    {
        Intent intent = new Intent("chatMessage");

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }*/
}
