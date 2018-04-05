package com.shirantech.sathitv.network;

/**
 * Constants for api data.
 * </p>
 * Only store data which are not related to user i.e not shown to user.
 * Store messages shown to user in strings.xml.
 */
public class ApiConstants {

//    public static final String BASE_URL = "http://f1soft-host.com";
//    public static final String BASE_URL = "http://sathitv.dtn.com.np/api/";
    public static final String BASE_URL = "http://sathitv.com/app/api/";
//    public static final String BASE_URL = "http://10.13.210.12:81";
    public static final String MID_URL = "/sathitv/api/";
    public static final String APP_USER_URL = BASE_URL + "appuser/";
    public static final String CHANNEL_URL = BASE_URL + "channel/";
    public static final String CHIT_CHAT_URL = BASE_URL + "chitchat/";
/*
    public static final String APP_USER_URL = BASE_URL + MID_URL + "appuser/";
    public static final String CHANNEL_URL = BASE_URL + MID_URL + "channel/";
    public static final String CHIT_CHAT_URL = BASE_URL + MID_URL + "chitchat/";*/


    // keys for posting data in server
    public static final String HEADER_KEY_ACCEPT = "Accept";
    public static final String HEADER_KEY_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_ACCEPT_VALUE = "application/json";
    public static final String PARAM_TOKEN = "token";


    public static final String RESPONSE_STATUS_SUCCESS = "success";
    public static final String RESPONSE_STATUS_FAILURE = "error";


    /**
     * The caller references the constants using <tt>Consts.EMPTY_STRING</tt>,
     * and so on. Thus, the caller should be prevented from constructing objects of
     * this class, by declaring this private constructor.
     */
    private ApiConstants() {
        //this prevents even the native class from
        //calling this ctor as well :
        throw new AssertionError();
    }
}
