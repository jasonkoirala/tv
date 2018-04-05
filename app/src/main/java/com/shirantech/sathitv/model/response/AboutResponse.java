package com.shirantech.sathitv.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jyoshna on 1/28/16.
 * Class for holding response of help url
 */
public class AboutResponse extends GeneralResponse {

    @SerializedName("about")
    private String about;

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }
}
