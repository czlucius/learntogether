package com.sp.learntogether.ui.communities;

public class forumPostInfo {
    private String name, id, profileImage, forumQuestion, currentDateTime;

    public forumPostInfo(String name, String id, String profileImage, String forumQuestion, String currentDateTime) {
        this.name = name;
        this.id = id;
        this.profileImage = profileImage;
        this.forumQuestion = forumQuestion;
        this.currentDateTime = currentDateTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String uid) {
        this.id = id;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getForumQuestion() {
        return forumQuestion;
    }

    public void setForumQuestion(String forumQuestion) {
        this.forumQuestion = forumQuestion;
    }

    public String getCurrentDateTime() {
        return currentDateTime;
    }

    public void setCurrentDateTime(String currentDateTime) {
        this.currentDateTime = currentDateTime;
    }
}
