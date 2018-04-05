package com.shirantech.sathitv.model.response;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import com.google.gson.Gson;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;

/**
 * Unit tests for the {@link GeneralResponse}.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class GeneralResponseTest {

    private static final String TEST_SUCCESS_JSON_RESPONSE = "{ \"status\": \"success\", \"message\": \"successfully fetched data\" }";
    private static final String TEST_EMPTY_JSON_RESPONSE = "";

    @Test
    public void testGetStatus() throws Exception {
        GeneralResponse mGeneralResponse = new Gson().fromJson(TEST_SUCCESS_JSON_RESPONSE, GeneralResponse.class);
        Assert.assertThat(mGeneralResponse.getStatus(), Is.is("success"));
    }

    @Test
    public void testGetMessage() throws Exception {
        GeneralResponse mGeneralResponse = new Gson().fromJson(TEST_SUCCESS_JSON_RESPONSE, GeneralResponse.class);
        Assert.assertThat(mGeneralResponse.getMessage(), Is.is("successfully fetched data"));
    }

    @Test
    public void testResponseNull_WhenEmptyResultFromServer() throws Exception {
        GeneralResponse mGeneralResponse = new Gson().fromJson(TEST_EMPTY_JSON_RESPONSE, GeneralResponse.class);
        Assert.assertNull(mGeneralResponse);
    }
}