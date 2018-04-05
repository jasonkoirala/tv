package com.shirantech.sathitv.model.response;

import com.google.gson.annotations.SerializedName;
import com.shirantech.sathitv.logger.Log;
import com.shirantech.sathitv.model.News;
import com.shirantech.sathitv.model.VOD;
import com.shirantech.sathitv.utils.AppLog;

import java.util.List;

/**
 * Created by jyoshna on 1/28/16.
 * Class for holding response of VOD
 */
public class VODResponse {

    @SerializedName("status")
    private String status;
    /*@SerializedName("code")
    private int code;*/
    @SerializedName("vods")
    private List<VOD> vodsList;

    @SerializedName("mods")
    private List<VOD> modsList;
    private String message;
    @SerializedName("offset")
    private int offset;
    @SerializedName("total")
    private int total;



    public List<VOD> getModsList() {
        return modsList;
    }

    public void setModsList(List<VOD> modsList) {
        this.modsList = modsList;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getMessage() {
        return "Some error occured in the server.";
    }

    public List<VOD> getVodsList() {
        return vodsList;
    }

    public void setVodsList(List<VOD> vodsList) {
        this.vodsList = vodsList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
/*
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }*/


}
