package com.shirantech.sathitv.model.response;

import com.google.gson.annotations.SerializedName;
import com.shirantech.sathitv.model.TvChannel;

import java.util.List;

/**
 * Class for holding response of channel list.
 */
public class TvChannelListResponse extends GeneralResponse {
    @SerializedName("channels")
    private List<TvChannel> tvChannelList;

    public List<TvChannel> getTvChannelList() {
        return tvChannelList;
    }
}
