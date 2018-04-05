package com.shirantech.sathitv.model.response;

import com.google.gson.annotations.SerializedName;
import com.shirantech.sathitv.utils.AppLog;

/**
 * Class holding general response from server. Other classes can extend this class to
 * extract basic server response along with other data.
 */
public class GeneralResponse {
    private static final String TAG = GeneralResponse.class.getName();
    @SerializedName("code")
    protected int code;
    @SerializedName("status")
    protected String status;
    @SerializedName("message")
    protected String message;
    @SerializedName("chinoMessage")
    protected String chinoMessage;

    public int getCode() {
        return code;
    }

    public String getChinoMessage() {
        return chinoMessage;
    }

    public String getStatus() {

        AppLog.showLog(TAG, "status : "+ status+"\n");
        return status;
    }

    public String getMessage() {
        AppLog.showLog(TAG, "message : "+ message+"\n");
        return message;
    }

}
