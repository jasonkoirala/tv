package com.shirantech.sathitv.suite;

import com.shirantech.sathitv.adapter.UploadPhotoListAdapterTest;
import com.shirantech.sathitv.helper.FileHelperTest;
import com.shirantech.sathitv.helper.PreferencesHelperTest;
import com.shirantech.sathitv.model.response.LoginResponseTest;
import com.shirantech.sathitv.model.response.GeneralResponseTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Runs all unit tests.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({GeneralResponseTest.class, LoginResponseTest.class,
        PreferencesHelperTest.class, FileHelperTest.class, UploadPhotoListAdapterTest.class})
public class UnitTestSuite {
}
