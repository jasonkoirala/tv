package com.shirantech.sathitv.helper;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.shirantech.sathitv.SathiTvApplication;
import com.shirantech.sathitv.callbacks.OnLanguageSelectedCallback;
import com.shirantech.sathitv.fragment.LanguageChooserDialogFragment;
import com.shirantech.sathitv.model.Language;

import java.util.Locale;

/**
 * Helper class to manage the user choice of language. Offer user with the language choice dialog
 * and save it in the user preference.
 */
public class LanguageChooserHelper implements LanguageChooserDialogFragment.OnLanguageConfirmedListener {

    private AppCompatActivity activity;
    private OnLanguageSelectedCallback languageSelectedCallback;

    /**
     * Constructor for passing {@link AppCompatActivity} reference.
     *
     * @param activity {@link AppCompatActivity} to refer for getting resources
     */
    public LanguageChooserHelper(AppCompatActivity activity) {
        this.activity = activity;
    }

    /**
     * Show the user with language picker dialog.
     *
     * @param languageSelectedCallback Callback for user language choice confirmation.
     */
    public void showChoice(@NonNull OnLanguageSelectedCallback languageSelectedCallback) {
        this.languageSelectedCallback = languageSelectedCallback;

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        Fragment prev = activity.getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        LanguageChooserDialogFragment newFragment = LanguageChooserDialogFragment.newInstance();
        newFragment.setLanguageConfirmedListener(this);
        newFragment.show(ft, "dialog");
    }

    @Override
    public void OnLanguageConfirmed(Language selectedLanguage) {
        PreferencesHelper.setLanguageSelected(activity, true);
        PreferencesHelper.writeLanguagePreference(activity, selectedLanguage);
        ((SathiTvApplication) activity.getApplication()).setCurrentLanguage(selectedLanguage);

        languageSelectedCallback.onLanguageSelected();
    }

    public static void setLocale(Context context, Language selectedLanguage) {
        Configuration config = new Configuration(context.getResources().getConfiguration());
        switch (selectedLanguage) {
            case ENGLISH:
                config.locale = Locale.ENGLISH;
                break;
            case NEPALI:
                config.locale = new Locale("ne");
                break;
            default:
                config.locale = Locale.getDefault();
        }
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }
}
