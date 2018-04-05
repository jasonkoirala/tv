package com.shirantech.sathitv.model.postparams;

import com.google.gson.annotations.SerializedName;

/**
 * A POJO for photo rating.
 */
public class CaptionUpdateParams {
    @SerializedName("imageId")
    private String imageId;
    @SerializedName("caption")
    private String caption;
    @SerializedName("token")
    private String token;

    public CaptionUpdateParams(String imageId, String caption, String token) {
        this.imageId = imageId;
        this.caption = caption;
        this.token = token;
    }

    public String getImageId() {
        return imageId;
    }

    public String getRating() {
        return caption;
    }

    public String getToken() {
        return token;
    }
}
