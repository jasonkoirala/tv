package com.shirantech.sathitv.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jyoshna on 1/28/16.
 * a model class that holds the information of VOD
 */
public class Banner {
    @SerializedName("image")
    private String image;
    public String getImage() {
        return image;
    }
}