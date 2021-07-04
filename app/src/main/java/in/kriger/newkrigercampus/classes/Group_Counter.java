package in.kriger.newkrigercampus.classes;

public class Group_Counter {



    public static final String TABLE_NAME = "group_counter";

    public static final String COLUMN_UID = "uid";
    public static final String COLUMN_COUNT_POSTS = "count_post";
    public static final String COLUMN_COUNT_INVITES = "count_invites";
    public static final String COLUMN_ISNEW = "isnew";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_UID + " TEXT,"
                    + COLUMN_COUNT_POSTS + " INTEGER,"
                    + COLUMN_COUNT_INVITES + " INTEGER,"
                    + COLUMN_ISNEW + " TEXT,"
                    + "PRIMARY KEY ("+COLUMN_UID+")"
                    + ")";

    public Group_Counter() {
    }


    public static String getColumnUid() {
        return COLUMN_UID;
    }

    public static String getColumnCountPosts() {
        return COLUMN_COUNT_POSTS;
    }

    public static String getColumnCountInvites() {
        return COLUMN_COUNT_INVITES;
    }
}
