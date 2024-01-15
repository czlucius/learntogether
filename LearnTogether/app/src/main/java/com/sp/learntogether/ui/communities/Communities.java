package com.sp.learntogether.ui.communities;
import com.dtsx.astra.sdk.AstraDB;
import com.sp.learntogether.R;

public class Communities {
    private String community_type;
    private int imgid;

    public Communities(String community_type) {
        this.community_type = community_type;
        if(community_type == "Mathematics"){
            this.imgid = R.drawable.math;
        } else if(community_type == "English"){
            this.imgid = R.drawable.english;
        } else if(community_type == "Science"){
            this.imgid = R.drawable.science;
        } else if(community_type == "Social Studies"){
            this.imgid = R.drawable.socialStudies;
        } else if(community_type == "History"){
            this.imgid = R.drawable.history;
        } else if(community_type == "Geography"){
            this.imgid = R.drawable.geography;
        } else if(community_type == "Piano"){
            this.imgid = R.drawable.piano;
        } else if(community_type == "Violin"){
            this.imgid = R.drawable.violin;
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
