package com.shirantech.sathitv.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by prativa on 10/13/15.
 * Model class to handle information of the Interview
 */
public class InterviewsReplyResponse extends GeneralResponse {

    /**
     * Data Format
     * {
     * "code": 1000,
     * "status": "success",
     * "interviews": [
     * {
     * "title": "test interview",
     * "description": "bbb",
     * "video": "http://f1soft-host.com/sathitv/assets/upload/interview/486dbf5176add9cf44f42293bc188b03.wmv"
     *  "iamge": "http://f1soft-host.com/sathitv/assets/upload/interviewThump/f87033780b03dfb69741e2b288bfda34.png"

     * },
     * {
     * "title": "test",
     * "description": "aa",
     * "video": "http://f1soft-host.com/sathitv/assets/upload/interview/0d9c265515df6ea00ef3eff3b52956e3.wmv"
     * }
     * ]
     * }
     */

    @SerializedName("interviews")
    private List<InterviewItem> interviewItemList;

    public List<InterviewItem> getInterviewItemList() {
        return interviewItemList;
    }

    public class InterviewItem {
        public String getTitle() {
            return title;
        }


        public String getDescription() {
            return description;
        }


        public String getVideoUrl() {
            return videoUrl;
        }


        @SerializedName("title")
        private String title;
        @SerializedName("description")
        private String description;
        @SerializedName("video")
        private String videoUrl;
        @SerializedName("image")
        private String image;

        public String getImage() {
            return image;
        }
    }

}
