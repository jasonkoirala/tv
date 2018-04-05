package com.shirantech.sathitv.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Class for holding response of Janam kundali reply.
 */
public class HoroscopeResponse{
    @SerializedName("horoscope")
    private List<Horoscope> horoscopeList;
    @SerializedName("status")
    private String status;
    @SerializedName("code")
    private String code;

    private String message;

    public List<Horoscope> getHoroscopeList() {
        return horoscopeList;
    }

    public String getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return "Some error occured in the server.";
    }

    public class Horoscope {
        /**
         * Horoscope array item
         * <p>
         * "title": "Aries",
         * "description": "Aires description",
         * "image": "http://f1soft-host.com/sathitv/assets/upload/horoscope/ee922b0b7d9d5faed89ed88cea81c845.png",
         * "date": {
         * "date": "2015-11-29 00:00:00",
         * "timezone_type": 3,
         * "timezone": "America/Chicago"
         */

        @SerializedName("title")
        private String title;
        @SerializedName("description")
        private String description;
        @SerializedName("image")
        private String image;


        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getImage() {
            return image;
        }
    }
}
