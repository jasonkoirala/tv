package com.shirantech.sathitv.model.postparams;

import com.google.gson.annotations.SerializedName;
import com.shirantech.sathitv.utils.AppLog;

/**
 * A POJO for sending parameters for requesting Janam kundali.
 */
public class JanamKundaliParams {
    private static final String TAG = JanamKundaliParams.class.getName();
    @SerializedName("name")
    private final String name;
    @SerializedName("dob")
    private final String birthDate;
    @SerializedName("time")
    private final String birthTime;
    @SerializedName("birthPlace")
    private final String birthPlace;
    @SerializedName("gender")
    private final String gender;
    @SerializedName("token")
    private final String token;
    @SerializedName("reason")
    private final String reason;


    public JanamKundaliParams(String name, String birthDate, String birthTime, String birthPlace,
                              String reason,String gender, String token) {
        this.name = name;
        this.birthDate = birthDate;
        this.birthTime = birthTime;
        this.birthPlace = birthPlace;
        this.reason = reason;
        this.gender = gender;
        this.token = token;
        AppLog.showLog(TAG, "name :"+name+"\n"+"birthdate  "+birthDate+"\n"+"birthtime"+birthTime+
        "\n"+ "birthplace "+birthPlace+"\n"+"reason "+reason+"\n"+"gender "+gender+"\n"+"token "+token);
    }
}
