package com.shirantech.sathitv.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * A POJO to hold information about a photo.
 */
public class Photo {
    @SerializedName("id")
    private String photoId;
    @SerializedName("caption")
    private String photoCaption;
    @SerializedName("status")
    private boolean status;
    @SerializedName("imagePath")
    private String imagePath;
    @SerializedName("aspectRatio")
    private float aspectRatio;
    @SerializedName("comment")
    private List<Comment> photoCommentList;
    @SerializedName("rating")
    private float photoRating;
    @SerializedName("myRating")
    private float myRating;

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoCaption(String photoCaption) {
        this.photoCaption = photoCaption;
    }

    public String getPhotoCaption() {
        return photoCaption;
    }

    public boolean isStatus() {
        return status;
    }

    public String getImagePath() {
        return imagePath;
    }

    public float getAspectRatio() {
        return aspectRatio == 0 ? 1 : aspectRatio;
    }

    public void setPhotoCommentList(List<Comment> photoCommentList) {
        this.photoCommentList = photoCommentList;
    }

    public List<Comment> getPhotoCommentList() {
        return photoCommentList;
    }

    public void setPhotoRating(float photoRating) {
        this.photoRating = photoRating;
    }

    public float getPhotoRating() {
        return photoRating;
    }

    public void setMyRating(float myRating) {
        this.myRating = myRating;
    }

    public float getMyRating() {
        return myRating;
    }
}
