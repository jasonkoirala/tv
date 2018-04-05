package com.shirantech.sathitv.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by prativa on 12/2/15.
 */


/**
 * A POJO class to hold comment on posts and photos.
 */
public class Comment {
    @SerializedName("comment")
    private String comment;
    @SerializedName("username")
    private String userName;
    /**
     * Used to show the comment posting status
     */
    private boolean postingComment;

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isPostingComment() {
        return postingComment;
    }

    public void setPostingComment(boolean postingComment) {
        this.postingComment = postingComment;
    }
}


