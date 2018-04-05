package com.shirantech.sathitv.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jyoshna on 1/28/16.
 */
public class VODDate {
    @SerializedName("date")
    private String date;
    @SerializedName("timezone_type")
    private String timezone_type;
    @SerializedName("timezone")
    private String timezone;

    public String getDate() {
        return date;
    }

    public String getTimezone_type() {
        return timezone_type;
    }

    public String getTimezone() {
        return timezone;
    }
}
