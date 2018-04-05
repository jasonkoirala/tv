package com.shirantech.sathitv.model.response;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import com.google.gson.Gson;

import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Assert;

/**
 * Unit tests for the {@link LoginResponse}.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class LoginResponseTest {

    private static final String TEST_SUCCESS_LOGIN_RESPONSE = "{ " +
            "\"code\": 1000, " +
            "\"status\": \"success\", " +
            "\"token\": \"g6NwWEYsLXOveFA2\", " +
            "\"message\": \"Valid User\" }";
    private static final String TEST_FAILURE_LOGIN_RESPONSE = "{ " +
            "\"code\": 1001, " +
            "\"status\": \"error\", " +
            "\"message\": \"Invalid username or password\" " +
            "}";

    @Test
    public void testGetToken() throws Exception {
        LoginResponse mLoginResponse = new Gson()
                .fromJson(TEST_SUCCESS_LOGIN_RESPONSE, LoginResponse.class);
        Assert.assertThat(mLoginResponse.getToken(), Is.is("g6NwWEYsLXOveFA2"));
    }

    @Test
    public void testNoToken_WhenLoginFailure() throws Exception {
        LoginResponse mLoginResponse = new Gson()
                .fromJson(TEST_FAILURE_LOGIN_RESPONSE, LoginResponse.class);
        Assert.assertThat(mLoginResponse.getToken(), Matchers.isEmptyOrNullString());
    }
}