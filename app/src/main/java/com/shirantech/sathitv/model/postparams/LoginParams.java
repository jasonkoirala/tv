package com.shirantech.sathitv.model.postparams;

import com.google.gson.annotations.SerializedName;
import com.shirantech.sathitv.utils.AppLog;

/**
 * A POJO for sending login parameters.
 */
public class LoginParams {
    @SerializedName("email")
    String email;
    @SerializedName("password")
    String password;
    @SerializedName("deviceId")
    private String deviceId;
    @SerializedName("gcmId")
    private String gcmId;
    @SerializedName("versionName")
    private String versionName;
    @SerializedName("OSType")
    private String osType;

    public LoginParams(String email, String password, String gcmId, String deviceId, String versionName, String osType) {
        this.email = email;
        this.password = password;
        this.gcmId = gcmId;
        this.deviceId = deviceId;
        this.versionName = versionName;
        this.osType = osType;
    }
}
