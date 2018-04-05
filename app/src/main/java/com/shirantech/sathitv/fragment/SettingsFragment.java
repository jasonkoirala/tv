package com.shirantech.sathitv.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.shirantech.sathitv.R;
import com.shirantech.sathitv.SathiTvApplication;
import com.shirantech.sathitv.helper.LanguageChooserHelper;
import com.shirantech.sathitv.helper.PreferencesHelper;
import com.shirantech.sathitv.model.Language;

/**
 * This fragment shows preferences.
 */
public class SettingsFragment extends PreferenceFragment {

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

                Language selectedLanguage = Language.valueOf(listPreference.getEntryValues()[index].toString());
                PreferencesHelper.writeLanguagePreference(preference.getContext(), selectedLanguage);

                notifyLanguageChange();
            }
            return true;
        }
    };

    /**
     * Use this factory method to create a new instance of this fragment
     *
     * @return A new instance of fragment {@link SettingsFragment}.
     */
    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences
        // to their values. When their values change, their summaries are
        // updated to reflect the new value, per the Android Design
        // guidelines.
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_selected_language)));
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferencesHelper.readString(getActivity(), preference.getKey(), ""));
    }

    /**
     * Notify that the language has been changed. Set it in the application and update the Locale
     * with changed language. {@link Activity}s can use it to recreate themselves to the
     * language change effect.
     */
    private void notifyLanguageChange() {
        ((SathiTvApplication) getActivity().getApplication()).setLanguageChanged(true);
        LanguageChooserHelper.setLocale(getActivity(), PreferencesHelper.readLanguagePreference(getActivity()));
    }
}
