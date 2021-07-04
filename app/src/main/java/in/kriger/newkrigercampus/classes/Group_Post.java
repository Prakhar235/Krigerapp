package in.kriger.newkrigercampus.classes;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by poojanrathod on 5/6/17.
 */

public class Group_Post {


    private String author;
    private Integer count_comment,count_like,count_views;
    private String uid;
    private String document_id;
    private String timestamp;
    private String text,original,thumb,pdf_url;



    private String is_Like;
    private HashMap<String,String> views;

    private String grp_id;

    private ArrayList<String> likesList;
    private ArrayList<String> viewsList;

    public static final String TABLE_NAME = "group_post";

    public static final String COLUMN_DOCID = "docid";
    public static final String COLUMN_AUTHOR = "author";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_COUNTVIEWS = "count_views";
    public static final String COLUMN_COUNTCOMMENT = "count_comment";
    public static final String COLUMN_COUNTLIKE = "count_like";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_UID = "uid";
    public static final String COLUMN_ORIGINAL = "original";
    public static final String COLUMN_THUMB = "thumb";
    public static final String COLUMN_PDFURL = "pdf_url";
    public static final String COLUMN_GRP_ID="grp_id";
    public static final String COLUMN_ISLIKE = "islike";
    public static final String COLUMN_VIEWS = "views";
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_DOCID + " TEXT PRIMARY KEY,"
                    + COLUMN_GRP_ID + " TEXT,"
                    + COLUMN_AUTHOR + " TEXT,"
                    + COLUMN_TIMESTAMP + " TEXT,"
                    + COLUMN_COUNTVIEWS + " INTEGER,"
                    + COLUMN_COUNTCOMMENT + " INTEGER,"
                    + COLUMN_COUNTLIKE + " INTEGER,"
                    + COLUMN_TEXT + " TEXT,"
                    + COLUMN_UID + " TEXT,"
                    + COLUMN_ORIGINAL + " TEXT, "
                    + COLUMN_THUMB + " TEXT, "
                    + COLUMN_PDFURL + " TEXT,"
                    + COLUMN_ISLIKE + " TEXT,"
                    + COLUMN_VIEWS + " TEXT"
                    + ")";






    public Group_Post(String original,String thumb,String text,String author, String uid, String timestamp,String pdf_url) {
        this.author = author;
        this.timestamp = timestamp;
        this.uid = uid;
        this.count_views = count_views;
        this.count_comment = count_comment;
        this.count_like = count_like;
        this.text = text;
        this.original=original;
        this.thumb=thumb;
        this.views = views;
        this.document_id = document_id;
        this.grp_id = grp_id;
        this.pdf_url = pdf_url;
       // postTime = new Date().getTime();



    }

    public Group_Post() {
    }



    public String getGrp_id() {return grp_id; }

    public void setGrp_id(String grp_id) {this.grp_id = grp_id; }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getCount_views() {
        return count_views;
    }

    public void setCount_views(Integer count_views) {
        this.count_views = count_views;
    }

    public Integer getCount_comment() {
        return count_comment;
    }

    public void setCount_comment(Integer count_comment) {
        this.count_comment = count_comment;
    }

    public Integer getCount_like() {
        return count_like;
    }

    public void setCount_like(Integer count_like) {
        this.count_like = count_like;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getPdf_url() {
        return pdf_url;
    }

    public void setPdf_url(String pdf_url) {
        this.pdf_url = pdf_url;
    }


    public String getIs_Like() {
        return is_Like;
    }

    public void setIs_Like(String is_Like) {
        this.is_Like=is_Like;
    }

    public HashMap<String, String> getViews() {
        return views;
    }

    public void setViews(HashMap<String, String> views) {
        this.views = views;
    }

    public ArrayList<String> getLikesList() {
        return likesList;
    }

    public void setLikesList(ArrayList<String> likesList) {
        this.likesList = likesList;
    }

    public ArrayList<String> getViewsList() {
        return viewsList;
    }

    public void setViewsList(ArrayList<String> viewsList) {
        this.viewsList = viewsList;
    }

}
