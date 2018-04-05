package com.shirantech.sathitv.model.response;

import com.google.gson.annotations.SerializedName;
import com.shirantech.sathitv.model.Banner;
import com.shirantech.sathitv.model.News;

import java.util.List;

/**
 * Created by jyoshna on 1/28/16.
 * Class for holding response of Banner images
 */
public class NewsResponse extends GeneralResponse{

    @SerializedName("news")
    private List<News> newsList;
    @SerializedName("offset")
    private int offset;

    public List<News> getNewsList() {
        return newsList;
    }

    public void setNewsList(List<News> newsList) {
        this.newsList = newsList;
    }
    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
