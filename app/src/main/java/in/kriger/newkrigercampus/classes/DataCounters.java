package in.kriger.newkrigercampus.classes;

public class DataCounters {

    private String uid,type;

    public static final String TABLE_NAME = "data_counter";

    public static final String COLUMN_UID = "uid";
    public static final String COLUMN_TYPE = "type";

    public static final String CONNECTIONS = "connections";
    public static final String CONNECTREQUEST = "connectrequest";
    public static final String CONNECTREQUESTSENT = "connectrequestsent";
    public static final String PROFILEVIEWS = "profileviews";
    public static final String CONTACT = "contact";
    public static final String GROUPINVITES="groupinvites";
    public static final String GROUPPOSTS="groupposts";
    public static final String GROUPS="groups";

    public static final String NOTIFICATIONS = "notifications";
    public static final String MUTENOTIFICATIONS = "mutenotifications";
    public static final String INFLUENCER = "influencer";
    public static final String CONNECTOR = "connector";
    public static final String EXPERT = "expert";


    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_UID + " TEXT,"
                    + COLUMN_TYPE + " TEXT,"
                    + "PRIMARY KEY ("+COLUMN_UID+","+COLUMN_TYPE+")"
                    + ")";

    public DataCounters() {
    }

    public DataCounters(String uid, String type) {
        this.uid = uid;
        this.type = type;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
