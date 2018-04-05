package com.shirantech.sathitv.model.response;

import com.google.gson.annotations.SerializedName;
import com.shirantech.sathitv.model.PhotoAlbum;

import java.util.List;

/**
 * Class for holding response of getting photo albums.
 */
public class PhotoAlbumListResponse extends GeneralResponse {
    @SerializedName("images")
    private List<PhotoAlbum> photoAlbumList;
    @SerializedName("myimages")
    private List<PhotoAlbum> ownPhotoAlbumList;

    public List<PhotoAlbum> getPhotoAlbumList() {
        return photoAlbumList;
    }

    public List<PhotoAlbum> getOwnPhotoAlbumList() {
        return ownPhotoAlbumList;
    }
}
