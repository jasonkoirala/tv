package com.shirantech.sathitv.model.postparams;

import com.google.gson.annotations.SerializedName;
import com.shirantech.sathitv.utils.AppLog;

/**
 * A POJO for sending parameters for requesting Janam kundali.
 */
public class ChitChatSendMessageParams {
    private static final String TAG = ChitChatSendMessageParams.class.getName();
    @SerializedName("token")
    private final String token;
    @SerializedName("receiverId")
    private final int receiverId;
    @SerializedName("message")
    private final String message;


    public ChitChatSendMessageParams(String token, int receiverId, String message) {
        this.message = message;
        this.token = token;
        this.receiverId = receiverId;
        AppLog.showLog(TAG, "token : "+token+"\n"+"message : "+ message+"\n"+ "receiverId : "+receiverId+ "\n");

    }
}
