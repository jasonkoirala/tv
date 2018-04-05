package com.shirantech.sathitv.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * Class for holding response of rating photo.
 */
public class PhotoRatingResponse extends GeneralResponse {

    @SerializedName("avgRating")
    private float updatedRating;

    public float getUpdatedRating() {
        return updatedRating;
    }
}
