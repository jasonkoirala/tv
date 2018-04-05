package com.shirantech.sathitv.model.postparams;

import com.google.gson.annotations.SerializedName;

/**
 * A POJO for sending parameters for commenting.
 */
public class PhotoCommentParams {
    @SerializedName("imageId")
    private final String imageId;
    @SerializedName("comment")
    private final String comment;
    @SerializedName("token")
    private final String token;

    public PhotoCommentParams(String imageId, String comment, String token) {
        this.imageId = imageId;
        this.comment = comment;
        this.token = token;
    }
}
