package com.shirantech.sathitv.model;

import com.google.gson.annotations.SerializedName;

/**
 * A POJO class to hold a Janam Kundali(information).
 */
public class HealthConsultant {
    @SerializedName("title")
    private String title;
    @SerializedName("reply")
    private String reply;
    @SerializedName("uploadImage")
    private String uploadImage;
    @SerializedName("replyImage")
    private String replyImage;
    @SerializedName("message")
    private String message;
    @SerializedName("subject")
    private String subject;
    @SerializedName("symptoms")
    private String symptoms;
    @SerializedName("requestedDate")
    private VODDate requestedDate;
    @SerializedName("repliedDate")
    private VODDate repliedDate;

    public VODDate getRequestedDate() {
        return requestedDate;
    }

    public VODDate getRepliedDate() {
        return repliedDate;
    }

    public String getMessage() {
        return message;
    }

    public String getSubject() {
        return subject;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public String getTitle() {
        return title;
    }

    public String getReply() {
        return reply;
    }

    public String getUploadImage() {
        return uploadImage;
    }

    public String getReplyImage() {
        return replyImage;
    }
}
