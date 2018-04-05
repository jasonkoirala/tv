package com.shirantech.sathitv.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jyoshna on 1/28/16.
 * a model class that holds the information of VOD
 */
public class VOD {
    @SerializedName("title")
    private String title;
    @SerializedName("image")
    private String image;
    @SerializedName("type")
    private boolean type;
    @SerializedName("url")
    private String url;
    @SerializedName("video")
    private String video;
    @SerializedName("typeName")
    private String typeName;
    @SerializedName("is_free")
    private boolean free;
    @SerializedName("duration")
    private String duration;
    @SerializedName("created")
    private String created;

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public boolean getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public String getTypeName() {
        return typeName;
    }

    public boolean isFree() {
        return free;
    }

    public String getDuration() {
        return duration;
    }

    public String getCreated() {
        return created;
    }

    public String getVideo() {
        return video;
    }
}