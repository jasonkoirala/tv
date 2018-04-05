package com.shirantech.sathitv.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * Gcm response model.
 * <br>
 * Created by samir on 12/12/14.
 */
public class GcmResponse {

    /**
     * Server response for Janam Kundali
     * {"messageType":"Rashifal",
     * "send_date":"2015-11-19",
     * "notificationid":17,"title":"Rashifal",
     * "message":"trdtstr"}
     */
    /**
     * Server response for HealthConsultant
     * {"image":"http:\/\/f1soft-host.com\/sathitv\/assets\/upload\/consultant\/",
     * "messageType":"Consultant",
     * "send_date":"2015-11-18",
     * "notificationid":4,
     * "title":"RE:vbdcdf",
     * "message":"fgsdfg"}
     */

/*server response for model chitchat
* {"senderName":"Charlize theron","messageType":"Chitchat","receiver":18,"send_date":"2016-03-21","sender":19,
* "receiverName":"Amanda seyfried","notificationid":210,"title":"New message","message":"okay"}
* */


    @SerializedName("message")
    private String gcmMessage;
    @SerializedName("title")
    private String gcmTitle;
    @SerializedName("messageType")
    private String messageType;
    @SerializedName("sender")
    private int senderId;
    @SerializedName("senderName")
    private String name;
    @SerializedName("receiverName")
    private String receiverName;
    @SerializedName("notificationid")
    private int notificationId;

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public String getName() {
        return name;
    }

    public int getSenderId() {
        return senderId;
    }

    public String getGcmMessage() {
        return gcmMessage;
    }

    public String getGcmTitle() {
        return gcmTitle;
    }

    public void setGcmMessage(String gcmMessage) {
        this.gcmMessage = gcmMessage;
    }

    public void setGcmTitle(String gcmTitle) {
        this.gcmTitle = gcmTitle;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }
}
