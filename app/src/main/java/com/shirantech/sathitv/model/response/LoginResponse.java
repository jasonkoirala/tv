package com.shirantech.sathitv.model.response;

import android.nfc.Tag;

import com.google.gson.annotations.SerializedName;
import com.shirantech.sathitv.logger.Log;
import com.shirantech.sathitv.utils.AppLog;

/**
 * Class for holding response of Login(and Registration).
 */
public class LoginResponse extends GeneralResponse {
    @SerializedName("userId")
    private int userId;
    @SerializedName("token")
    private String token;
    @SerializedName("name")
    private String username;
    @SerializedName("email")
    private String email;
    @SerializedName("imageUrl")
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    @SerializedName("profileImageMessage")
    private String profileImageMessage;

    public String getProfileImageMessage() {
        return profileImageMessage;
    }
    public String getToken() {
        return token;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

}
