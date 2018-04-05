package com.shirantech.sathitv.model.postparams;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.shirantech.sathitv.utils.AppLog;

/**
 * A POJO for gcm registration.
 */
public class GcmRegistrationParams {
    private static final String TAG = GcmRegistrationParams.class.getName();
    @SerializedName("deviceId")
    private String deviceId;
    @SerializedName("gcmId")
    private String gcmToken;
    @SerializedName("versionName")
    private String versionName;
    @SerializedName("OSType")
    private String osType;
    @SerializedName("userId")
    private int userId;

    public GcmRegistrationParams(String deviceId, String gcmToken, String versionName, String osType, int userId) {
        this.deviceId = deviceId;
        this.gcmToken = gcmToken;
        this.versionName = versionName;
        this.osType = osType;
        this.userId = userId;

        AppLog.showLog(TAG, " deviceId"+deviceId+" gcmToken"+gcmToken+" versionName"+versionName+" osType"+osType+" userId"+userId);
    }
}
