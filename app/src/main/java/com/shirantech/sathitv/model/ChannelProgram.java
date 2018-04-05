package com.shirantech.sathitv.model;

import com.google.gson.annotations.SerializedName;

/**
 * A model class to hold information of a program in a channel
 */
public class ChannelProgram {
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("time")
    private ProgramTime time;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ProgramTime getTime() {
        return time;
    }
}
