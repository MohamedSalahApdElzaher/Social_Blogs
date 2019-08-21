package comc.mohammedsalah.test;

import java.sql.Timestamp;

public class Blogpost extends postid {
   private String Image_uri, post_title, post_description, id , date;

    // default constructor
    public Blogpost() {
    }

     public Blogpost(String Image_uri , String post_title , String post_description , String id, String date){
        this.id=id;
        this.Image_uri = Image_uri;
        this.post_description=post_description;
        this.post_title=post_title;
        this.date=date;
     }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage_uri() {
        return Image_uri;
    }

    public void setImage_uri(String image_uri) {
        Image_uri = image_uri;
    }

    public String getPost_title() {
        return post_title;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
    }

    public String getPost_description() {
        return post_description;
    }

    public void setPost_description(String post_description) {
        this.post_description = post_description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}