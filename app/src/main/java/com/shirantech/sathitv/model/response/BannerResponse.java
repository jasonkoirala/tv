package com.shirantech.sathitv.model.response;

import com.google.gson.annotations.SerializedName;
import com.shirantech.sathitv.model.Banner;
import com.shirantech.sathitv.model.News;
import com.shirantech.sathitv.model.VOD;

import java.util.List;

/**
 * Created by jyoshna on 1/28/16.
 * Class for holding response of Banner images
 */
public class BannerResponse extends GeneralResponse{

    @SerializedName("bannerImages")
    private List<Banner> bannerImagesList;

    public List<Banner> getBannerImagesList() {
        return bannerImagesList;
    }
}
