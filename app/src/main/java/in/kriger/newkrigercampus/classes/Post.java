package in.kriger.newkrigercampus.classes;

/**
 * Created by poojanrathod on 5/6/17.
 */

public class Post {


    private String author;
    private Integer count_comment,count_like,count_views;
    private String uid;
    private String document_id;
    private String timestamp;
    private String text,image_url,image_url_original,hasImage,pdf_url,display_name;
    private  String link_image_url,link_title,link_desc;

    String islike;


    public static final String TABLE_NAME = "post";

    public static final String COLUMN_DOCID = "docid";
    public static final String COLUMN_AUTHOR = "author";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_COUNTVIEWS = "count_views";
    public static final String COLUMN_COUNTCOMMENT = "count_comment";
    public static final String COLUMN_COUNTLIKE = "count_like";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_UID = "uid";
    public static final String COLUMN_IMGURL = "img_url";
    public static final String COLUMN_IMGURLORIGINAL = "img_url_original";
    public static final String COLUMN_PDFURL = "pdf_url";
    public static final String COLUMN_LINKIMGURL = "link_image_url";
    public static final String COLUMN_LINKTITLE = "link_title";
    public static final String COLUMN_LINKDESC = "link_desc";
    public static final String COLUMN_DISPLAYNAME = "display_name";
    public static final String COLUMN_ISLIKE = "islike";
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_DOCID + " TEXT PRIMARY KEY,"
                    + COLUMN_AUTHOR + " TEXT,"
                    + COLUMN_TIMESTAMP + " TEXT,"
                    + COLUMN_COUNTVIEWS + " INTEGER,"
                    + COLUMN_COUNTCOMMENT + " INTEGER,"
                    + COLUMN_COUNTLIKE + " INTEGER,"
                    + COLUMN_TEXT + " TEXT,"
                    + COLUMN_UID + " TEXT,"
                    + COLUMN_IMGURL + " TEXT,"
                    + COLUMN_IMGURLORIGINAL + " TEXT,"
                    + COLUMN_PDFURL + " TEXT,"
                    + COLUMN_LINKIMGURL + " TEXT,"
                    + COLUMN_LINKTITLE + " TEXT,"
                    + COLUMN_LINKDESC + " TEXT,"
                    + COLUMN_DISPLAYNAME + " TEXT,"
                    + COLUMN_ISLIKE + " TEXT"
                    + ")";






    public Post(String uid,String timestamp, String text,String image_url,String image_url_original,String pdf_url) {

        this.timestamp = timestamp;
        this.uid = uid;
        this.text = text;
        this.image_url  = image_url;
        this.image_url_original  = image_url_original;
        this.pdf_url = pdf_url;




    }

    public Post() {
    }




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

    public String getImage_url_original() {
        return image_url_original;
    }

    public void setImage_url_original(String image_url_original) {
        this.image_url_original = image_url_original;
    }


    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getPdf_url() {
        return pdf_url;
    }

    public void setPdf_url(String pdf_url) {
        this.pdf_url = pdf_url;
    }


    public String getLink_image_url() {
        return link_image_url;
    }

    public void setLink_image_url(String link_image_url) {
        this.link_image_url = link_image_url;
    }

    public String getLink_title() {
        return link_title;
    }

    public void setLink_title(String link_title) {
        this.link_title = link_title;
    }


    public String getLink_desc() {
        return link_desc;
    }

    public void setLink_desc(String link_desc) {
        this.link_desc = link_desc;
    }


    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getIslike() {
        return islike;
    }

    public void setIslike(String islike) {
        this.islike = islike;
    }
}
