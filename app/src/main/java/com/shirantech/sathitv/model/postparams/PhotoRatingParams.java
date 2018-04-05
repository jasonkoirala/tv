package com.shirantech.sathitv.model.postparams;

import com.google.gson.annotations.SerializedName;

/**
 * A POJO for sending params for photo rating.
 */
public class PhotoRatingParams {
    @SerializedName("imageId")
    private String imageId;
    @SerializedName("rating")
    private float rating;
    @SerializedName("token")
    private String token;

    public PhotoRatingParams(String imageId, float rating, String token) {
        this.imageId = imageId;
        this.rating = rating;
        this.token = token;
    }

    public String getImageId() {
        return imageId;
    }

    public float getRating() {
        return rating;
    }

    public String getToken() {
        return token;
    }
}
