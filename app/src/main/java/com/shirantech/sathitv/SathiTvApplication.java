package com.shirantech.sathitv;

import android.app.Application;

/*import com.crashlytics.android.Crashlytics;*/
import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.shirantech.sathitv.helper.PreferencesHelper;
import com.shirantech.sathitv.model.Language;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;

import io.fabric.sdk.android.Fabric;

/**
 * {@link Application} for Sathi Tv.
 */
public class SathiTvApplication extends Application {

    private Language currentLanguage;
    /**
     * language change status. to be used to reload activity if needed
     */
    private boolean languageChanged = false;
    Permission[] permissions = new Permission[] {
            Permission.USER_PHOTOS,
            Permission.EMAIL,
            Permission.PUBLISH_ACTION,
            Permission.USER_ABOUT_ME,
            Permission.PUBLIC_PROFILE,
    };

    @Override
    public void onCreate() {
        super.onCreate();
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
        Fresco.initialize(this);
        setPermissions();
    }

    /**
     * Get the current font if present or the font for preferred language in shared preferences.
     *
     * @return the current font to use
     */
    public Language getCurrentLanguage() {
        if (null == currentLanguage) {
            return PreferencesHelper.readLanguagePreference(this);
        } else {
            return currentLanguage;
        }
    }

    /**
     * Set the given font name as current font.
     *
     * @param currentLanguage font to set as current
     */
    public void setCurrentLanguage(Language currentLanguage) {
        this.currentLanguage = currentLanguage;
    }

    /**
     * Check if language has been changed.
     *
     * @return true if language has been changed else false
     */
    public boolean isLanguageChanged() {
        return languageChanged;
    }

    /**
     * Set language change status
     *
     * @param languageChanged language change status
     */
    public void setLanguageChanged(boolean languageChanged) {
        this.languageChanged = languageChanged;
    }

    public void setPermissions(){
        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId("625994234086470")
                .setNamespace("sromkuapp")
                .setPermissions(permissions)
                .build();
        SimpleFacebook.setConfiguration(configuration);
    }



}


