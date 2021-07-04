package in.kriger.newkrigercampus.classes;

/**
 * Created by poojanrathod on 7/9/17.
 */

public class Notification {

    private String type, origin, origin_id, destination, destination_id, id, post, id_extra,id_name;
    private Long timestamp;
    String id_extra2;


    public static final String TABLE_NAME = "notification";

    public static final String COLUMN_DOCID = "docid";
    public static final String COLUMN_ORIGINID = "origin_id";
    public static final String COLUMN_ORIGIN = "origin";
    public static final String COLUMN_DESTINATIONID = "destination_id";
    public static final String COLUMN_DESTINATION = "destination";
    public static final String COLUMN_POST = "post";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_IDEXTRA = "id_extra";
    public static final String COLUMN_IDNAME = "id_name";
    public static final String COLUMN_IDEXTRA2 = "id_extra2";


    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_DOCID + " TEXT,"
                    + COLUMN_ORIGINID + " TEXT,"
                    + COLUMN_ORIGIN + " TEXT,"
                    + COLUMN_DESTINATIONID + " TEXT,"
                    + COLUMN_DESTINATION + " TEXT,"
                    + COLUMN_POST + " TEXT,"
                    + COLUMN_TIMESTAMP + " TEXT,"
                    + COLUMN_TYPE + " TEXT,"
                    + COLUMN_IDEXTRA + " TEXT,"
                    + COLUMN_IDNAME + " TEXT,"
                    + COLUMN_IDEXTRA2 + " TEXT,"
                    + "PRIMARY KEY (" + COLUMN_ORIGINID + ","  + COLUMN_TYPE + "," + COLUMN_DESTINATIONID + "," + COLUMN_DOCID  + "," + COLUMN_IDEXTRA2 + "," + COLUMN_IDEXTRA + ")"
                    + ")";




    public Notification(String type, String origin, String origin_id, String destination, String destination_id, String id,String post,String id_extra,String id_name) {

        this.type = type;
        this.origin = origin;
        this.origin_id = origin_id;
        this.destination = destination;
        this.destination_id = destination_id;
        this.id = id;
        this.post = post;
        this.id_extra=id_extra;
        this.id_name = id_name;
        //this.timestamp = timestamp;

    }

    public Notification() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getOrigin_id() {
        return origin_id;
    }

    public void setOrigin_id(String origin_id) {
        this.origin_id = origin_id;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getDestination_id() {
        return destination_id;
    }

    public void setDestination_id(String destination_id) {
        this.destination_id = destination_id;
    }

    public String getId_extra() {
        return id_extra;
    }

    public void setId_extra(String id_extra) {
        this.id_extra = id_extra;
    }
    //    public String getTimestamp() {
//        return timestamp;
//    }
//
//    public void setTimestamp(String timestamp) {
//        this.timestamp = timestamp;
//    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }


    public String getId_name() {
        return id_name;
    }

    public void setId_name(String id_name) {
        this.id_name = id_name;
    }

    public String getId_extra2() {
        return id_extra2;
    }

    public void setId_extra2(String id_extra2) {
        this.id_extra2 = id_extra2;
    }
}
