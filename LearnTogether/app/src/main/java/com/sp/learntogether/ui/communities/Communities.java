package com.sp.learntogether.ui.communities;

public class Communities {
    private String community_type;
    private int imgid;

    public Communities(String community_type, int imgid) {
        this.community_type = community_type;
        this.imgid = imgid;
    }

    public String getCommunity_type() {
        return community_type;
    }

    public void setCommunity_type(String community_type) {
        this.community_type = community_type;
    }

    public int getImgid() {
        return imgid;
    }

    public void setImgid(int imgid) {
        this.imgid = imgid;
    }
    //    public String getCourse_name() {
//        return community_type;
//    }
//
//
//    public void setCourse_name(String course_name) {
//        this.community_type = course_name;
//    }
}
