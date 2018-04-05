package com.shirantech.sathitv.model.postparams;

import com.google.gson.annotations.SerializedName;
import com.shirantech.sathitv.utils.AppLog;

/**
 * A POJO for sending parameters for requesting Janam kundali.
 */
public class ChitChatReceiveMessageParams {
    private static final String TAG = ChitChatReceiveMessageParams.class.getName();
    @SerializedName("token")
    private final String token;
    @SerializedName("senderId")
    private final int senderId;



    public ChitChatReceiveMessageParams(String token, int senderId) {
        this.token = token;
        this.senderId = senderId;
        AppLog.showLog(TAG, "token : "+token+"\n"+ "senderId : "+senderId+ "\n");

    }
}
