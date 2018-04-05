package com.shirantech.sathitv.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * A model class to hold information of a channel
 */
public class TvChannel {
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("url")
    private String streamingUrl;
    @SerializedName("logo")
    private String logoUrl;
    @SerializedName("is_free")
    private boolean free;
    @SerializedName("programs")
    private List<ChannelProgram> channelProgramList;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStreamingUrl() {
        return streamingUrl;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public boolean isFree() {
        return free;
    }

    public List<ChannelProgram> getChannelProgramList() {
        return channelProgramList;
    }
}
