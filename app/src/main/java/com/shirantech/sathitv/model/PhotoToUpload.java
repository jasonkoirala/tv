package com.shirantech.sathitv.model;

import android.net.Uri;

/**
 * A POJO to hold information about a photo to upload.
 */
public class PhotoToUpload {
    private final Uri photoUri;
    private String photoCaption;
    private final float photoAspectRatio;
    /**
     * Flag to check if the photo is valid or not
     */
    private boolean photoValid = true;

    /**
     * Initialize the immutable {@link #photoUri} and {@link #photoAspectRatio}
     *
     * @param photoUri         uri of the photo
     * @param photoAspectRatio aspect ratio of the photo
     */
    public PhotoToUpload(Uri photoUri, float photoAspectRatio) {
        this.photoUri = photoUri;
        this.photoAspectRatio = photoAspectRatio;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public void setPhotoCaption(String photoCaption) {
        this.photoCaption = photoCaption;
    }

    public String getPhotoCaption() {
        return photoCaption;
    }

    public float getPhotoAspectRatio() {
        return photoAspectRatio;
    }

    public boolean isPhotoValid() {
        return photoValid;
    }

    public void setPhotoValid(boolean photoValid) {
        this.photoValid = photoValid;
    }
}
