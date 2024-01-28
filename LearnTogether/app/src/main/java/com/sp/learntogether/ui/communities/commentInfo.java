package com.sp.learntogether.ui.communities;

public class commentInfo {
    private String username, profileImg, postId, dateTime, commentDesc;

    public commentInfo(String username, String profileImg, String postId, String dateTime, String commentDesc) {
        this.username = username;
        this.profileImg = profileImg;
        this.postId = postId;
        this.dateTime = dateTime;
        this.commentDesc = commentDesc;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getCommentDesc() {
        return commentDesc;
    }

    public void setCommentDesc(String commentDesc) {
        this.commentDesc = commentDesc;
    }
}
