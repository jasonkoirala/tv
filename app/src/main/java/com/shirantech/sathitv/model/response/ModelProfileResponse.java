package com.shirantech.sathitv.model.response;

import com.google.gson.annotations.SerializedName;
import com.shirantech.sathitv.model.ModelProfile;

import java.util.List;

/**
 * Created by jyoshna on 2/18/16.
 */
public class ModelProfileResponse extends GeneralResponse {
    @SerializedName("list")
    private List<ModelProfile> modelList;

    @SerializedName("modelPhotos")
    private List<ModelProfile> modelPhotosList;
/*
    @SerializedName("ModelProfile")
    private ModelProfile modelProfile;*/

    public List<ModelProfile> getModelList() {
        return modelList;
    }

   /* public ModelProfile getModelProfile() {
        return modelProfile;
    }*/

    public List<ModelProfile> getModelPhotosList() {
        return modelPhotosList;
    }
}
