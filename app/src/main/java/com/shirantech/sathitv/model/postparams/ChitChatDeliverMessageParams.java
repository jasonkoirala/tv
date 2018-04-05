package com.shirantech.sathitv.model.postparams;

import com.google.gson.annotations.SerializedName;
import com.shirantech.sathitv.utils.AppLog;

/**
 * A POJO for sending parameters for requesting Janam kundali.
 */
public class ChitChatDeliverMessageParams {
    private static final String TAG = ChitChatDeliverMessageParams.class.getName();
    @SerializedName("token")
    private final String token;
    @SerializedName("messageId")
    private final String messageId;



    public ChitChatDeliverMessageParams(String token, String messageId) {
        this.token = token;
        this.messageId = messageId;
        AppLog.showLog(TAG, "token : "+token+"\n"+ "messageId : "+messageId+ "\n");

    }
}
