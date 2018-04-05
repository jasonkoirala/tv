package com.shirantech.sathitv.model.postparams;

import com.google.gson.annotations.SerializedName;
import com.shirantech.sathitv.utils.AppLog;

/**
 * A POJO for sending parameters for requesting Janam kundali.
 */
public class HealthConsultantParams {
    private static final String TAG = HealthConsultantParams.class.getName();
    @SerializedName("subject")
    private final String subject;
    @SerializedName("message")
    private final String message;
    @SerializedName("symptoms")
    private final String symptoms;
    @SerializedName("token")
    private final String token;

    public HealthConsultantParams(String subject, String message,String symptoms, String token) {
        this.subject = subject;
        this.message = message;
        this.token = token;
        this.symptoms = symptoms;
        AppLog.showLog(TAG, "subject : "+subject+"\n"+"message : "+ message+"\n"+ "symptoms : "+symptoms+ "\n"+ "token : "+token);

    }
}
