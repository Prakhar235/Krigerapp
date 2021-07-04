package in.kriger.newkrigercampus.classes;

import java.util.HashMap;

/**
 * Created by poojanrathod on 6/22/17.
 */

public class Answers {

    private String string,uid,timestamp;


    private String post_id;


    HashMap<String,String> likes;
    
    public Answers() {
    }

    public Answers(String string, String uid, String timestamp) {
        this.string = string;
        this.uid = uid;
       // this.mention_uid = mention_uid;
        this.timestamp = timestamp;


    }



    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


//    public String getMention_uid() {
//        return mention_uid;
//    }
//
//    public void setMention_uid(String mention_uid) {
//        this.mention_uid = mention_uid;
//    }


    public String getTimestamp() {

        return timestamp;

    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


    public HashMap<String, String> getLikes() {
        return likes;
    }

    public void setLikes(HashMap<String, String> likes) {
        this.likes = likes;
    }


    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }
}
