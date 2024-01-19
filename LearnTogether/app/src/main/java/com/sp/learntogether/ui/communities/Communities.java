package com.sp.learntogether.ui.communities;
import com.sp.learntogether.R;

public class Communities {
    private String community_type;
    private int imgid;

    public Communities(String community_type) {
        this.community_type = community_type;
        if(community_type.equals("Mathematics")){
            this.imgid = R.drawable.math;
        } else if(community_type.equals("English")){
            this.imgid = R.drawable.english;
        } else if(community_type.equals("Science")){
            this.imgid = R.drawable.science;
        } else if(community_type.equals("Social Studies")){
            this.imgid = R.drawable.socialstudies;
        } else if(community_type.equals("History")){
            this.imgid = R.drawable.history;
        } else if(community_type.equals("Geography")){
            this.imgid = R.drawable.geography;
        } else if(community_type.equals("Piano")){
            this.imgid = R.drawable.piano;
        } else if(community_type.equals("Violin")){
            this.imgid = R.drawable.violin;
        } else if(community_type.equals("Planting")){
            this.imgid = R.drawable.planting;
        }
        else{
            this.imgid = R.drawable.notfound;
        }
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
