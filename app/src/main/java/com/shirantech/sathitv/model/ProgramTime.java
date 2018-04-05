package com.shirantech.sathitv.model;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A model class to hold time of a program
 */
public class ProgramTime {
    @SerializedName("date")
    private String dateTime;

    public String getDateTime() {
        return dateTime;
    }

    /**
     * @return the time in hh:mm am/pm format
     */
    public String getTime() {
        try {
            SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = dateTimeFormatter.parse(dateTime);
            SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm aaa", Locale.getDefault());
            return timeFormatter.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "NA";
        }
    }
}
