package com.shirantech.sathitv.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jyoshna on 1/28/16.
 * a model class that holds the information of NEWS
 */
public class News {
    @SerializedName("title")
    private String title;
    @SerializedName("image")
    private String image;
    @SerializedName("news_url")
    private String news_url;
    @SerializedName("news_source")
    private String news_source;
    @SerializedName("status")
    private boolean status;
    @SerializedName("summary")
    private String summary;
    @SerializedName("created")
    private String created;

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNews_url() {
        return news_url;
    }

    public void setNews_url(String news_url) {
        this.news_url = news_url;
    }

    public String getNews_source() {
        return news_source;
    }

    public void setNews_source(String news_source) {
        this.news_source = news_source;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}