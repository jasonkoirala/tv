package com.shirantech.sathitv.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jyoshna on 3/9/16.
 */
public class ChitChat {

    @SerializedName("message")
    private String message;
    @SerializedName("senderId")
    private int senderId;
    @SerializedName("receiverId")
    private int receiverId;
    @SerializedName("sender")
    private String sender;
    @SerializedName("receiver")
    private String receiver;
    @SerializedName("initiate")
    private boolean initiate;
    @SerializedName("created")
    private VODDate created;

    public boolean isInitiate() {
        return initiate;
    }

    public void setInitiate(boolean initiate) {
        this.initiate = initiate;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }


    public void setCreated(VODDate created) {
        this.created = created;
    }


    public String getMessage() {
        return message;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public VODDate getCreated() {
        return created;
    }
}
