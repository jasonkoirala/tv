package com.shirantech.sathitv.model.response;

import com.google.gson.annotations.SerializedName;
import com.shirantech.sathitv.model.ChitChat;
import com.shirantech.sathitv.utils.AppLog;

import java.util.List;

/**
 * Created by jyoshna on 3/9/16.
 */
public class ChitChatReceiveMessageResponse extends GeneralResponse {
    private static final String TAG = ChitChatReceiveMessageResponse.class.getName();
    //grooupid chatmessage
    @SerializedName("messageGroupId")
    private String messageGroupId;
    @SerializedName("chatMessage")
    private List<ChitChat> chatMessageList;

    public String getMessageGroupId() {
        return messageGroupId;
    }

    public List<ChitChat> getChatMessageList() {
        AppLog.showLog(TAG, "chatMessageList : "+chatMessageList.size());
        return chatMessageList;
    }



}
