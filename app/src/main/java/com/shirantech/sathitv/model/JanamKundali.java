package com.shirantech.sathitv.model;

import com.google.gson.annotations.SerializedName;

/**
 * A POJO class to hold a Janam Kundali(information).
 */
public class JanamKundali {
    @SerializedName("name")
    private String name;
    @SerializedName("gender")
    private String gender;
    @SerializedName("dob")
    private String birthDate;
    @SerializedName("time")
    private String birthTime;
    @SerializedName("video")
    private String videoUrl;
    @SerializedName("birthplace")
    private String birthPlace;
    @SerializedName("query")
    private String query;
    @SerializedName("reply")
    private String reply;
    @SerializedName("image")
    private String image;
    @SerializedName("requestedDate")
    private VODDate requestedDate;

    @SerializedName("repliedDate")
    private VODDate repliedDate;
    @SerializedName("status")
    private boolean status;

    public VODDate getRequestedDate() {
        return requestedDate;
    }

    public VODDate getRepliedDate() {
        return repliedDate;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getBirthTime() {
        return birthTime;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public String getQuery() {
        return query;
    }

    public String getReply() {
        return reply;
    }

    public boolean getStatus() {
        return status;
    }

    public String getImage() {
        return image;
    }
}
