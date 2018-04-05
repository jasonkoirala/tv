package com.shirantech.sathitv.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * A model class to hold photos and information for a single photo album
 */
public class PhotoAlbum {
    @SerializedName("postId")
    private String id;
    @SerializedName("description")
    private String description;
    @SerializedName("postStatus")
    private boolean photoAlbumStatus;
    @SerializedName("img")
    private List<Photo> photoList;
    @SerializedName("postComment")
    private List<Comment> CommentList;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean getPhotoAlbumStatus() {
        return photoAlbumStatus;
    }

    public void setPhotoAlbumStatus(boolean photoAlbumStatus) {
        this.photoAlbumStatus = photoAlbumStatus;
    }

    public void setPhotoList(List<Photo> photoList) {
        this.photoList = photoList;
    }

    public List<Photo> getPhotoList() {
        return photoList;
    }

    public void setCommentList(List<Comment> CommentList) {
        this.CommentList = CommentList;
    }

    public List<Comment> getCommentList() {
        return CommentList;
    }
}
