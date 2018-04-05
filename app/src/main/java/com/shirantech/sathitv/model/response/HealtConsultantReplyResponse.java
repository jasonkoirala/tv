package com.shirantech.sathitv.model.response;

import com.google.gson.annotations.SerializedName;
import com.shirantech.sathitv.model.HealthConsultant;

import java.util.List;

/**
 * Class for holding response of Janam kundali reply.
 */
public class HealtConsultantReplyResponse extends GeneralResponse {
    @SerializedName("queries")
    private List<HealthConsultant> healthConsultantList;


    public List<HealthConsultant> getHealthConsultantList() {
        return healthConsultantList;
    }
}
