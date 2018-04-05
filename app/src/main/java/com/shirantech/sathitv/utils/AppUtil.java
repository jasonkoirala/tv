package com.shirantech.sathitv.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.shirantech.sathitv.activity.SplashScreenActivity;
import com.shirantech.sathitv.helper.PreferencesHelper;

import java.util.UUID;

/**
 * App utilities
 */
public class AppUtil {


    public static boolean isInternetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting() && netInfo.isAvailable();
    }

    /**
     * Return pseudo unique ID
     * <br>
     * source : http://stackoverflow.com/a/17625641
     *
     * @return ID
     */
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static String getUniquePsuedoID(Application application) {
        // If all else fails, if the user does have lower than API 9 (lower
        // than Gingerbread), has reset their phone or 'Secure.ANDROID_ID'
        // returns 'null', then simply the ID returned will be solely based
        // off their Android device information. This is where the collisions
        // can happen.
        // Thanks http://www.pocketmagic.net/?p=1662!
        // Try not to use DISPLAY, HOST or ID - these items could change.
        // If there are collisions, there will be overlapping data

        String m_szDevIDShort;
        if (isLowerThanLollipop()) {
            m_szDevIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10)
                    + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10)
                    + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);
        } else {
            m_szDevIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10)
                    + (Build.SUPPORTED_ABIS.length % 10) + (Build.DEVICE.length() % 10)
                    + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);
        }

        // Thanks to @Roman SL!
        // http://stackoverflow.com/a/4789483/950427
        // Only devices with API >= 9 have android.os.Build.SERIAL
        // http://developer.android.com/reference/android/os/Build.html#SERIAL
        // If a user upgrades software or roots their phone, there will be a duplicate entry
        String serial;
        try {
            serial = Build.class.getField("SERIAL").get(null).toString();

            // Go ahead and return the serial for api => 9
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception e) {
            // String needs to be initialized
            serial = "serial"; // some value
        }

        // Thanks @Joe!
        // http://stackoverflow.com/a/2853253/950427
        // Finally, combine the values we have found by using the UUID class to create a unique identifier
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    public static boolean isLowerThanLollipop() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * Hides soft keyboard
     *
     * @param activity Activity to use.
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity
                .INPUT_METHOD_SERVICE);
        if (null != activity.getCurrentFocus()) {
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }
   /* *//**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param context GCM message received.
     *//*
    public static void sendNotification(Context context) {
        Intent intent = new Intent(context, SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("menuFragment", "janamkundali");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 *//* Request code *//*, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                // TODO : change notification icon
                .setSmallIcon(R.drawable.icon_notification)
                .setContentTitle("GCM TITLE")
                .setContentText("GCM RESPONSE")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        notificationManager.notify(0 *//* ID of notification *//*, notificationBuilder.build());
    }*/

    public static void showInvalidTokenSnackBar(View mMainLayout,final String message, final Context context){
//        if (ServerResponse.isTokenInvalid(message)) {
      /*      Snackbar.make(mMainLayout, "Session TimeOut. Please login", Snackbar.LENGTH_LONG)
                    .setCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            super.onDismissed(snackbar, event);
                        }
                    })
                    .show();*/
            Toast.makeText(context, "Session time out. Please login", Toast.LENGTH_SHORT).show();
            PreferencesHelper.signOut(context);
            context.startActivity(new Intent(context, SplashScreenActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            ((Activity)context).finish();

//        }
    /*else{
            Snackbar.make(mMainLayout,message, Snackbar.LENGTH_LONG)
                    .setCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            super.onDismissed(snackbar, event);

                        }
                    })
                    .show();
        }*/

    }

    public static void showAlert(String noticeText, Context context) {
        AlertDialog.Builder noticeAlertBuilder = new AlertDialog.Builder(context);
        noticeAlertBuilder.setMessage(noticeText);
        noticeAlertBuilder.setCancelable(false);
        noticeAlertBuilder.setNegativeButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.dismiss();
                    }
                }
        );
        AlertDialog dialog = noticeAlertBuilder.create();
        dialog.show();
//        setDialogSelector(dialog);

    }


}
