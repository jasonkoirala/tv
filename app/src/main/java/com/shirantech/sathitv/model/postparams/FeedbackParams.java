package com.shirantech.sathitv.model.postparams;

import com.google.gson.annotations.SerializedName;

/**
 * A POJO for sending parameters for requesting Janam kundali.
 */
public class FeedbackParams {
    @SerializedName("title")
    private final String title;
    @SerializedName("message")
    private final String message;
    @SerializedName("token")
    private final String token;

    public FeedbackParams(String title,String message, String token) {
        this.title = title;
        this.message = message;
        this.token = token;

    }
}
