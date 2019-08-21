package comc.mohammedsalah.test;

import java.util.Date;

public class CommrntsClass {
    private String comments , userid;
    private String date ;

    public CommrntsClass(){}

    public CommrntsClass(String comments, String userid, String date ) {
        this.comments = comments;
        this.userid = userid;
        this.date = date;

    }


    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
