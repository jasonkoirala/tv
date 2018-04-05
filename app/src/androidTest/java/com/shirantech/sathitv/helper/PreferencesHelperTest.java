package com.shirantech.sathitv.helper;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import com.shirantech.sathitv.model.Language;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for the {@link PreferencesHelper}.
 */
@SmallTest
@RunWith(AndroidJUnit4.class)
public class PreferencesHelperTest {

    @Test
    public void preferencesHelper_SaveAndReadDrawerSetting() throws Exception {
        final Context context = InstrumentationRegistry.getTargetContext();
        PreferencesHelper.writeDrawerSetting(context, true);
        final boolean userDrawerLearned = PreferencesHelper.readDrawerSetting(context);
        assertThat(true, is(userDrawerLearned));
    }

    @Test
    public void preferencesHelper_SaveAndReadLanguagePreference() throws Exception {
        final Context context = InstrumentationRegistry.getTargetContext();
        PreferencesHelper.writeLanguagePreference(context, Language.ENGLISH);
        final Language languageFromPrefs = PreferencesHelper.readLanguagePreference(context);
        assertThat(Language.ENGLISH, is(languageFromPrefs));
    }

    @Test
    public void preferencesHelper_DefaultShouldBeNepali() throws Exception {
        final Context context = InstrumentationRegistry.getTargetContext();
        PreferencesHelper.signOut(context);
        final Language defaultLanguage = PreferencesHelper.readLanguagePreference(context);
        assertThat(Language.NEPALI, is(defaultLanguage));
    }

    @Test
    public void preferencesHelper_SignOutAndTest() throws Exception {
        final Context context = InstrumentationRegistry.getTargetContext();
        // pre-fill shared preference with preferred language as English
        PreferencesHelper.writeLanguagePreference(context, Language.ENGLISH);

        // sign out and test the language is Nepali
        PreferencesHelper.signOut(context);
        final Language languageAfterSignOut = PreferencesHelper.readLanguagePreference(context);
        assertThat(Language.NEPALI, is(languageAfterSignOut));
    }
}