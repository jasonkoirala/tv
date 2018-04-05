package com.shirantech.sathitv.model.response;

import com.google.gson.annotations.SerializedName;
import com.shirantech.sathitv.model.Banner;

import java.util.List;

/**
 * Created by jyoshna on 1/28/16.
 * Class for holding response of help url
 */
public class HelpResponse extends GeneralResponse {

    @SerializedName("url")
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
