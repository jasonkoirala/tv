package com.shirantech.sathitv.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shirantech.sathitv.activity.SettingsActivity;
import com.shirantech.sathitv.model.ChitChat;
import com.shirantech.sathitv.model.Language;
import com.shirantech.sathitv.utils.AppLog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Easy storage and retrieval of preferences.
 */
public class PreferencesHelper {

    private static final String SATHI_PREFERENCE = "sathi_tv_preference";
    private static final String PREF_SENT_TOKEN_TO_SERVER = "sent_token_to_server";
    private static final String PREF_GCM_TOKEN = "gcm_token";
    private static final String PREF_LOGIN_TOKEN = "login_token";
    private static final String PREF_USER_ID = "user_id";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_EMAIL = "email";
    private static final String PREF_PROFILE_IMAGE_URL = "profile_image_url";
    private static final String PREF_IMAGE_URL = "image_url";
    private static final String PREF_IS_LANGUAGE_SELECTED = "is_language_selected";
    private static final String PREF_LANGUAGE_PREFERENCE = "selected_language";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private static final String PREF_FILES = "files";

    /**
     * Store the GCM token in preferences.
     *
     * @param context  The Context which to obtain the SharedPreferences from.
     * @param gcmToken GCM token to save.
     */
    public static void setGcmToken(Context context, String gcmToken) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(PREF_GCM_TOKEN, gcmToken);
        editor.apply();
    }

    /**
     * Get the GCM token stored in preferences.
     *
     * @param context The Context which to obtain the SharedPreferences from.
     * @return the gcm token if found else <code>null</code>.
     */
    public static String getGcmToken(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getString(PREF_GCM_TOKEN, null);
    }

    /**
     * Write if GCM registration token has been sent to server in preferences.
     *
     * @param context   The Context which to obtain the SharedPreferences from.
     * @param tokenSent if token has been sent or not
     */
    public static void setTokenSent(Context context, boolean tokenSent) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(PREF_SENT_TOKEN_TO_SERVER, tokenSent);
        editor.apply();
    }

    /**
     * Check if GCM registration token has been sent to server from preferences.
     *
     * @param context The Context which to obtain the SharedPreferences from.
     * @return <code>true</code> if token sent <code>false</code> otherwise.
     */
    public static boolean isTokenSent(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getBoolean(PREF_SENT_TOKEN_TO_SERVER, false);
    }

    /**
     * Store the login token in preferences.
     *
     * @param context    The Context which to obtain the SharedPreferences from.
     * @param loginToken login token to save.
     */
    public static void setLoginToken(Context context, String loginToken) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(PREF_LOGIN_TOKEN, loginToken);
        editor.apply();
    }

    /**
     * Get the login token stored in preferences.
     *
     * @param context The Context which to obtain the SharedPreferences from.
     * @return the login token if found else <code>null</code>.
     */
    public static String getLoginToken(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getString(PREF_LOGIN_TOKEN, null);
    }

    /**
     * Store the user id in preferences.
     *
     * @param context The Context which to obtain the SharedPreferences from.
     * @param userId  User id to save.
     */
    public static void setUserId(Context context, int userId) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putInt(PREF_USER_ID, userId);
        editor.apply();
    }

    /**
     * Get the user id stored in preferences.
     *
     * @param context The Context which to obtain the SharedPreferences from.
     * @return the user id if found else <code>null</code>.
     */
    public static int getUserId(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getInt(PREF_USER_ID, 0);
    }

    /**
     * Store the username in preferences.
     *
     * @param context  The Context which to obtain the SharedPreferences from.
     * @param username Username to save.
     */
    public static void setUsername(Context context, String username) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(PREF_USERNAME, username);
        editor.apply();
    }

    /**
     * Get the username stored in preferences.
     *
     * @param context The Context which to obtain the SharedPreferences from.
     * @return the username if found else <code>null</code>.
     */
    public static String getUsername(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getString(PREF_USERNAME, null);
    }


 /*   public static void setChatMessage(Context context, List<ChitChat> chatArrayList) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(PREF_FILES, new Gson().toJson(chatArrayList));
        editor.apply();
    }

    public static List<ChitChat> getChatMessage(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        List<ChitChat> chatList = new ArrayList<>();
//        return preferences.getString(PREF_FILES, null);
        String chatMessages = preferences.getString(PREF_FILES, null);
        Type listType = new TypeToken<ChitChat>() {
        }.getType();
        chatList = new Gson().fromJson(chatMessages, listType);
        return chatList;
    }
*/
    /**
     * Check if user is logged in or not
     *
     * @param context context to use
     * @return <code>true</code> if logged in <code>false</code> otherwise
     */
    public static boolean isLoggedIn(Context context) {
        return !TextUtils.isEmpty(PreferencesHelper.getLoginToken(context));
    }

    /**
     * Write if user has selected language previously or not in preferences.
     *
     * @param context            The Context which to obtain the SharedPreferences from.
     * @param isLanguageSelected if language is selected or not
     */
    public static void setLanguageSelected(Context context, boolean isLanguageSelected) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(PREF_IS_LANGUAGE_SELECTED, isLanguageSelected);
        editor.apply();
    }

    /**
     * Check if language is previously selected by user from preferences.
     *
     * @param context The Context which to obtain the SharedPreferences from.
     * @return <code>true</code> if already selected <code>false</code> otherwise.
     */
    public static boolean checkIfLanguageSelected(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getBoolean(PREF_IS_LANGUAGE_SELECTED, false);
    }

    /**
     * Writes the user's preferred language to preferences.
     *
     * @param context          The Context which to obtain the SharedPreferences from.
     * @param selectedLanguage language preferred by user to write.
     */
    public static void writeLanguagePreference(Context context, Language selectedLanguage) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(PREF_LANGUAGE_PREFERENCE, selectedLanguage.name());
        editor.apply();
    }

    /**
     * Retrieves the user's preferred language from preferences.
     *
     * @param context The Context which to obtain the SharedPreferences from.
     * @return language preferred by user to write <code>NEPALI</code> if nothing was saved previously.
     */
    public static Language readLanguagePreference(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return Language.valueOf(preferences.getString(PREF_LANGUAGE_PREFERENCE, Language.NEPALI.name()));
    }

    /**
     * Writes the user's familiarity with drawer to preferences.
     *
     * @param context           The Context which to obtain the SharedPreferences from.
     * @param userLearnedDrawer user's drawer familiarity to write.
     */
    public static void writeDrawerSetting(Context context, boolean userLearnedDrawer) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(PREF_USER_LEARNED_DRAWER, userLearnedDrawer);
        editor.apply();
    }

    /**
     * Retrieves the user's familiarity with drawer from preferences.
     *
     * @param context The Context which to obtain the SharedPreferences from.
     * @return user's drawer familiarity <code>false</code> if nothing was saved previously.
     */
    public static boolean readDrawerSetting(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getBoolean(PREF_USER_LEARNED_DRAWER, false);
    }

    /**
     * Use this method only from {@link SettingsActivity} where value is to be directly obtained
     * using key.
     *
     * @param context      The Context which to obtain the SharedPreferences from.
     * @param key          key to use to get the value.
     * @param defaultValue default value if no value found for the given key
     * @return value for the provided key from the SharedPreferences
     */
    public static String readString(Context context, String key, String defaultValue) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getString(key, defaultValue);
    }

    /**
     * Signs out by removing data from preference.
     *
     * @param context The Context which to obtain the SharedPreferences from.
     */
    public static void signOut(Context context) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.remove(PREF_LOGIN_TOKEN);
        editor.apply();
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.edit();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SATHI_PREFERENCE, Context.MODE_PRIVATE);
    }




    /**
     * Store the username in preferences.
     *
     * @param context  The Context which to obtain the SharedPreferences from.
     * @param imageUrl imageUrl to save.
     */
    public static void setProfileImageUrl(Context context, String imageUrl) {
        AppLog.showLog("set pro image"+imageUrl);
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(PREF_PROFILE_IMAGE_URL, imageUrl);
        editor.apply();
    }

    /**
     * Get the username stored in preferences.
     *
     * @param context The Context which to obtain the SharedPreferences from.
     * @return the imageUrl if found else <code>null</code>.
     */
    public static String getProfileImageUrl(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getString(PREF_PROFILE_IMAGE_URL, null);
    }




    /**
     * Store the username in preferences.
     *
     * @param context  The Context which to obtain the SharedPreferences from.
     * @param imageUrl imageUrl to save.
     */
    public static void setImageUrl(Context context, String imageUrl) {
        AppLog.showLog("receiver url ::::"+imageUrl);
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(PREF_IMAGE_URL, imageUrl);
        editor.apply();
    }

    /**
     * Get the username stored in preferences.
     *
     * @param context The Context which to obtain the SharedPreferences from.
     * @return the imageUrl if found else <code>null</code>.
     */
    public static String getImageUrl(Context context) {

        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getString(PREF_IMAGE_URL, null);
    }



    /**
     * Store the email id from fb  in preferences.
     *
     *
     */
    public static void setEmail(Context context, String email) {
        AppLog.showLog("email ::::"+email);
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(PREF_EMAIL, email);
        editor.apply();
    }

    /**
     * Get the username stored in preferences.
     *
     * @param context The Context which to obtain the SharedPreferences from.
     * @return the imageUrl if found else <code>null</code>.
     */
    public static String getEmail(Context context) {

        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getString(PREF_EMAIL, null);
    }




}
