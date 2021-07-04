package in.kriger.newkrigercampus.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.kriger.newkrigercampus.classes.DataCounters;
import in.kriger.newkrigercampus.classes.Group_Counter;
import in.kriger.newkrigercampus.classes.Notification;
import in.kriger.newkrigercampus.classes.Group_Post;
import in.kriger.newkrigercampus.classes.Post;
import in.kriger.newkrigercampus.classes.Resource;


public class DatabaseHelper extends SQLiteOpenHelper{

    // Database Version

    private static final int DATABASE_VERSION = 31;


    // Database Name
    private static final String DATABASE_NAME = "kriger_campus_db";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL(Post.CREATE_TABLE);
        db.execSQL(Group_Post.CREATE_TABLE);
        db.execSQL(DataCounters.CREATE_TABLE);
        db.execSQL(Notification.CREATE_TABLE);
        db.execSQL(Group_Counter.CREATE_TABLE);
        db.execSQL(Resource.CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Post.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Group_Post.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataCounters.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Notification.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Group_Counter.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Resource.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public void deleteAllTables(){

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM "+ Post.TABLE_NAME);
        db.execSQL("DELETE FROM " + Group_Post.TABLE_NAME);
        db.execSQL("DELETE FROM " + DataCounters.TABLE_NAME);
        db.execSQL("DELETE FROM " + Notification.TABLE_NAME);
        db.execSQL("DELETE FROM " + Group_Counter.TABLE_NAME);
        db.execSQL("DELETE FROM " + Resource.TABLE_NAME);

        db.close();
    }

    public void insertAllCounters(ArrayList<String> uidlist, String type){

        SQLiteDatabase db = this.getWritableDatabase();


        for (int i = 0;i<uidlist.size();i++){

            ContentValues values = new ContentValues();
            values.put(DataCounters.COLUMN_UID,uidlist.get(i));
            values.put(DataCounters.COLUMN_TYPE,type);

            db.insertWithOnConflict(DataCounters.TABLE_NAME,null,values,SQLiteDatabase.CONFLICT_REPLACE);

            }


        db.close();
    }

    public void insertResource(String resid, int count){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Resource.COLUMN_RESID,resid);
        values.put(Resource.COLUMN_COUNTENQUIRY,count);

        db.insertWithOnConflict(Resource.TABLE_NAME,null,values,SQLiteDatabase.CONFLICT_REPLACE);
        db.close();

    }

    public int getResource(String resid){

        int counter = 0;

        String selectQuery = "SELECT  * FROM " + Resource.TABLE_NAME + " WHERE " +
                Resource.COLUMN_RESID + " =? " ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,  new String[] {resid});


        if (cursor.moveToFirst()){
            do {

                counter = cursor.getInt(cursor.getColumnIndex(Resource.COLUMN_COUNTENQUIRY));
            }while (cursor.moveToNext());
        }


        // close db connection
        db.close();

        return counter;


    }

    public void insertAllPost(List<Post> posts) throws JSONException {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();


        for (int i = 0;i<posts.size();i++) {
            ContentValues values = new ContentValues();
            // no need to add them

            values.put(Post.COLUMN_DOCID, posts.get(i).getDocument_id());
            values.put(Post.COLUMN_AUTHOR, posts.get(i).getAuthor());
            values.put(Post.COLUMN_TIMESTAMP, posts.get(i).getTimestamp());
            values.put(Post.COLUMN_TEXT, posts.get(i).getText());
            values.put(Post.COLUMN_UID, posts.get(i).getUid());
            values.put(Post.COLUMN_IMGURL, posts.get(i).getImage_url());
            values.put(Post.COLUMN_IMGURLORIGINAL, posts.get(i).getImage_url_original());
            values.put(Post.COLUMN_LINKIMGURL, posts.get(i).getLink_image_url());
            values.put(Post.COLUMN_LINKTITLE, posts.get(i).getLink_title());
            values.put(Post.COLUMN_LINKDESC, posts.get(i).getLink_desc());
            values.put(Post.COLUMN_PDFURL, posts.get(i).getPdf_url());


            // insert row
            db.insertWithOnConflict(Post.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        }


        // close db connection
        db.close();


    }

    public int getPostCount() {
        String countQuery = "SELECT  * FROM " + Post.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Post.TABLE_NAME + " ORDER BY " +
                Post.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Post post = new Post();
                post.setDocument_id(cursor.getString(cursor.getColumnIndex(Post.COLUMN_DOCID)));
                post.setAuthor(cursor.getString(cursor.getColumnIndex(Post.COLUMN_AUTHOR)));
                post.setTimestamp(cursor.getString(cursor.getColumnIndex(Post.COLUMN_TIMESTAMP)));
                post.setCount_comment(cursor.getInt(cursor.getColumnIndex(Post.COLUMN_COUNTCOMMENT)));
                post.setCount_like(cursor.getInt(cursor.getColumnIndex(Post.COLUMN_COUNTLIKE)));
                post.setCount_views(cursor.getInt(cursor.getColumnIndex(Post.COLUMN_COUNTVIEWS)));
                post.setText(cursor.getString(cursor.getColumnIndex(Post.COLUMN_TEXT)));
                post.setUid(cursor.getString(cursor.getColumnIndex(Post.COLUMN_UID)));
                post.setImage_url(cursor.getString(cursor.getColumnIndex(Post.COLUMN_IMGURL)));
                post.setImage_url_original(cursor.getString(cursor.getColumnIndex(Post.COLUMN_IMGURLORIGINAL)));
                post.setLink_image_url(cursor.getString(cursor.getColumnIndex(Post.COLUMN_LINKIMGURL)));
                post.setLink_title(cursor.getString(cursor.getColumnIndex(Post.COLUMN_LINKTITLE)));
                post.setLink_desc(cursor.getString(cursor.getColumnIndex(Post.COLUMN_LINKDESC)));
                post.setPdf_url(cursor.getString(cursor.getColumnIndex(Post.COLUMN_PDFURL)));
                post.setIslike(cursor.getString(cursor.getColumnIndex(Post.COLUMN_ISLIKE)));

                posts.add(post);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return posts;
    }

    public String getLastPostTimestamp(){

        String selectQuery = "SELECT  * FROM " + Post.TABLE_NAME + " WHERE " + Post.COLUMN_TIMESTAMP + "!='null'" + " ORDER BY " +
                Post.COLUMN_TIMESTAMP + " DESC LIMIT 1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        String timestamp = null;

        if (cursor.moveToFirst()){
            do {
                timestamp = cursor.getString(cursor.getColumnIndex(Post.COLUMN_TIMESTAMP));
            }while (cursor.moveToNext());
        }


        // close db connection
        db.close();

        return timestamp;

    }



    public String getFirstPostTimestamp(){

        String selectQuery = "SELECT  * FROM " + Post.TABLE_NAME + " WHERE " + Post.COLUMN_TIMESTAMP + "!='null'" + " ORDER BY " +
                Post.COLUMN_TIMESTAMP + " LIMIT 1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        String timestamp = null;

        if (cursor.moveToFirst()){
            do {
                timestamp = cursor.getString(cursor.getColumnIndex(Post.COLUMN_TIMESTAMP));
            }while (cursor.moveToNext());
        }


        // close db connection
        db.close();
        return timestamp;

    }

    public Post getPost(String postid){


        Post post = new Post();

        String selectQuery = "SELECT  * FROM " + Post.TABLE_NAME + " WHERE " +
                Post.COLUMN_DOCID + " =? " ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,  new String[] {postid});


        if (cursor.moveToFirst()){
            do {

                post.setUid(cursor.getString(cursor.getColumnIndex(Post.COLUMN_UID)));
                post.setCount_comment(cursor.getInt(cursor.getColumnIndex(Post.COLUMN_COUNTCOMMENT)));
                post.setCount_like(cursor.getInt(cursor.getColumnIndex(Post.COLUMN_COUNTLIKE)));
                post.setCount_views(cursor.getInt(cursor.getColumnIndex(Post.COLUMN_COUNTVIEWS)));
                post.setIslike(cursor.getString(cursor.getColumnIndex(Post.COLUMN_ISLIKE)));
                post.setText(cursor.getString(cursor.getColumnIndex(Post.COLUMN_TEXT)));
                post.setImage_url(cursor.getString(cursor.getColumnIndex(Post.COLUMN_IMGURL)));
                post.setImage_url_original(cursor.getString(cursor.getColumnIndex(Post.COLUMN_IMGURLORIGINAL)));
                post.setPdf_url(cursor.getString(cursor.getColumnIndex(Post.COLUMN_PDFURL)));
                post.setLink_image_url(cursor.getString(cursor.getColumnIndex(Post.COLUMN_LINKIMGURL)));
                post.setLink_title(cursor.getString(cursor.getColumnIndex(Post.COLUMN_LINKTITLE)));
                post.setLink_desc(cursor.getString(cursor.getColumnIndex(Post.COLUMN_LINKDESC)));
                post.setTimestamp(cursor.getString(cursor.getColumnIndex(Post.COLUMN_TIMESTAMP)));
            }while (cursor.moveToNext());
        }


        // close db connection
        db.close();

        return post;




    }



    public boolean deletePost(String docid)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            return db.delete(Post.TABLE_NAME, Post.COLUMN_DOCID + "='" + docid+"'", null) > 0;
        }catch (Exception e){
            return true;
        }

    }



    public boolean deleteBadges(){

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            return db.delete(DataCounters.TABLE_NAME, DataCounters.COLUMN_TYPE + " IN ('" + DataCounters.CONNECTOR + "','"+ DataCounters.INFLUENCER + "')", null) > 0;
        }catch (Exception e){
            return true;
        }


    }


    public ArrayList<Integer> getPostCounter(String postid){

        ArrayList<Integer> counter = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + Post.TABLE_NAME + " WHERE " +
                Post.COLUMN_DOCID + " =? " ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,  new String[] {postid});


        if (cursor.moveToFirst()){
            do {

                counter.add(cursor.getInt(cursor.getColumnIndex(Post.COLUMN_COUNTLIKE)));
                counter.add(cursor.getInt(cursor.getColumnIndex(Post.COLUMN_COUNTCOMMENT)));
                counter.add(cursor.getInt(cursor.getColumnIndex(Post.COLUMN_COUNTVIEWS)));
            }while (cursor.moveToNext());
        }


        // close db connection
        db.close();

        return counter;



    }

    public void updateCounterPost(String postid,String type,int value){
        ContentValues cv = new ContentValues();
        cv.put(type,value);

        SQLiteDatabase db = this.getWritableDatabase();

        db.update(Post.TABLE_NAME,cv,Post.COLUMN_DOCID+"= ?",new String[]{postid});

        db.close();



    }

    public void updateLikePost(String postid,String type,String value){
        ContentValues cv = new ContentValues();
        cv.put(type,value);

        SQLiteDatabase db = this.getWritableDatabase();

        db.update(Post.TABLE_NAME,cv,Post.COLUMN_DOCID+"= ?",new String[]{postid});

        db.close();



    }

    public void updateCommentPost(String postid,String type,String value){
        ContentValues cv = new ContentValues();
        cv.put(type,value);

        SQLiteDatabase db = this.getWritableDatabase();

        db.update(Post.TABLE_NAME,cv,Post.COLUMN_DOCID+"= ?",new String[]{postid});

        db.close();



    }

    public boolean isGroupPresent(String groupid){
        SQLiteDatabase sqldb = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + Group_Counter.TABLE_NAME + " WHERE " +
                Group_Counter.COLUMN_UID + " =? ";

        Cursor cursor = sqldb.rawQuery(selectQuery, new String[] {groupid});
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;

    }

    public String isGroupOpen(String groupid){

        String isgroupopen = null;

        String selectQuery = "SELECT  * FROM " + Group_Counter.TABLE_NAME + " WHERE " +
                Group_Counter.COLUMN_UID + " =? " ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,  new String[] {groupid});


        if (cursor.moveToFirst()){
            do {
                isgroupopen = cursor.getString(cursor.getColumnIndex(Group_Counter.COLUMN_ISNEW));

            }while (cursor.moveToNext());
        }


        // close db connection
        db.close();

        return isgroupopen;
    }

    public int getGroupCounter(String groupid){

        int counter = 0;

        String selectQuery = "SELECT  * FROM " + Group_Counter.TABLE_NAME + " WHERE " +
                Group_Counter.COLUMN_UID + " =? " ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,  new String[] {groupid});


        if (cursor.moveToFirst()){
            do {

                counter = cursor.getInt(cursor.getColumnIndex(Group_Counter.COLUMN_COUNT_POSTS));

            }while (cursor.moveToNext());
        }


        // close db connection
        db.close();

        return counter;

    }

    public void updateGroupCounter(String groupid,String type,int value){
        ContentValues cv = new ContentValues();
        cv.put(type,value);

        SQLiteDatabase db = this.getWritableDatabase();

        db.update(Group_Counter.TABLE_NAME,cv,Group_Counter.COLUMN_UID+"= ?",new String[]{groupid});

        db.close();


    }

    public void openGroupCounter(String groupid){
        ContentValues cv = new ContentValues();
        cv.put(Group_Counter.COLUMN_ISNEW,"true");

        SQLiteDatabase db = this.getWritableDatabase();

        db.update(Group_Counter.TABLE_NAME,cv,Group_Counter.COLUMN_UID+"= ?",new String[]{groupid});

        db.close();

    }

    public void insertGroupCounter(String groupid){

        SQLiteDatabase db = this.getWritableDatabase();


            ContentValues values = new ContentValues();
            // no need to add them
            values.put(Group_Counter.COLUMN_UID, groupid);
            values.put(Group_Counter.COLUMN_COUNT_INVITES,0);
            values.put(Group_Counter.COLUMN_COUNT_POSTS,0);
            values.put(Group_Counter.COLUMN_ISNEW,"false");

            // insert row
            db.insertWithOnConflict(Group_Counter.TABLE_NAME, null, values,SQLiteDatabase.CONFLICT_REPLACE);


        // close db connection
        db.close();


    }

    public boolean isPost(String postid){
        SQLiteDatabase sqldb = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + Post.TABLE_NAME + " WHERE " +
                Post.COLUMN_DOCID + " =? ";

        Cursor cursor = sqldb.rawQuery(selectQuery, new String[] {postid});
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public boolean checkDataCounterValue(String postid,String type){

        String selectQuery = "SELECT  * FROM " + DataCounters.TABLE_NAME + " WHERE " +
                DataCounters.COLUMN_UID + " =? AND "+DataCounters.COLUMN_TYPE+ "=? " ;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, new String[] {postid,type});

            Boolean present = false;
            if (cursor.moveToFirst()){
                do {
                    present = true;
                }while (cursor.moveToNext());
            }

            // close db connection
            db.close();

            return present;

        }catch (NullPointerException e){

            return false;
        }




    }

    public String checkLikePostValue(String postid){

        String selectQuery = "SELECT  * FROM " + Post.TABLE_NAME + " WHERE " +
                Post.COLUMN_DOCID + " =? " ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {postid});
        String present = " ";

        if (cursor.moveToFirst()){
            do {
                present = cursor.getString(cursor.getColumnIndex(Post.COLUMN_ISLIKE));
            }while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        return present;

    }



    public boolean removeMuteValue(String postid){

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            return db.delete(DataCounters.TABLE_NAME, DataCounters.COLUMN_UID + "='" + postid+"'",null) > 0;
        }catch (Exception e){
            return true;
        }

    }


    public ArrayList<String> getAllUid(){

        ArrayList<String> uidlist = new ArrayList<>();

        String selectQuery = "SELECT "+ DataCounters.COLUMN_UID + "  FROM " + DataCounters.TABLE_NAME + " WHERE " + DataCounters.COLUMN_TYPE +" IN('"+DataCounters.CONNECTIONS+"','"+DataCounters.CONNECTREQUESTSENT+"')";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        String timestamp = null;

        if (cursor.moveToFirst()){
            do {
                uidlist.add(cursor.getString(cursor.getColumnIndex(DataCounters.COLUMN_UID)));
            }while (cursor.moveToNext());
        }


        // close db connection
        db.close();

        return uidlist;


    }

    public HashMap<String,Object> getAllConnections(String uid){

        HashMap<String,Object> map = new HashMap<>();
        int i = 0;


        String selectQuery = "SELECT "+ DataCounters.COLUMN_UID + "  FROM " + DataCounters.TABLE_NAME + " WHERE " + DataCounters.COLUMN_TYPE +" IN('"+DataCounters.CONNECTIONS+"')";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        map.put(String.valueOf(i),uid.toLowerCase());
        i++;


        if (cursor.moveToFirst()){
            do {
                map.put(String.valueOf(i),cursor.getString(cursor.getColumnIndex(DataCounters.COLUMN_UID)).toLowerCase());
                i++;

            }while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        return map;

    }


    public int getNotificationCount(){
        String countQuery = "SELECT  * FROM " + Notification.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;

    }


    public List<Notification> getAllNotification(){

        List<Notification> notifications = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Notification.TABLE_NAME + " ORDER BY " +
                Post.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Notification notification = new Notification();
                notification.setId(cursor.getString(cursor.getColumnIndex(Notification.COLUMN_DOCID)));
                notification.setOrigin_id(cursor.getString(cursor.getColumnIndex(Notification.COLUMN_ORIGINID)));
                notification.setOrigin(cursor.getString(cursor.getColumnIndex(Notification.COLUMN_ORIGIN)));
                notification.setDestination_id(cursor.getString(cursor.getColumnIndex(Notification.COLUMN_DESTINATIONID)));
                notification.setDestination(cursor.getString(cursor.getColumnIndex(Notification.COLUMN_DESTINATION)));
                notification.setPost(cursor.getString(cursor.getColumnIndex(Notification.COLUMN_POST)));
                notification.setTimestamp(cursor.getLong(cursor.getColumnIndex(Notification.COLUMN_TIMESTAMP)));
                notification.setType(cursor.getString(cursor.getColumnIndex(Notification.COLUMN_TYPE)));
                notification.setId_extra(cursor.getString(cursor.getColumnIndex(Notification.COLUMN_IDEXTRA)));
                notification.setId_name(cursor.getString(cursor.getColumnIndex(Notification.COLUMN_IDNAME)));
                notification.setId_extra2(cursor.getString(cursor.getColumnIndex(Notification.COLUMN_IDEXTRA2)));



                notifications.add(notification);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notifications;

    }

    public void insertAllNotifications(List<Notification> notifications) throws JSONException {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        for (int i  = 0 ;i<notifications.size();i++) {

            ContentValues values = new ContentValues();
            // no need to add them
            values.put(Notification.COLUMN_DOCID, notifications.get(i).getId() == null ? "1" : notifications.get(i).getId());
            values.put(Notification.COLUMN_ORIGINID, notifications.get(i).getOrigin_id() == null ? "1" : notifications.get(i).getOrigin_id());
            values.put(Notification.COLUMN_ORIGIN, notifications.get(i).getOrigin() == null ? "1" : notifications.get(i).getOrigin());
            values.put(Notification.COLUMN_DESTINATIONID, notifications.get(i).getDestination_id() == null ? "1" : notifications.get(i).getDestination_id());
            values.put(Notification.COLUMN_DESTINATION, notifications.get(i).getDestination() == null ? "1" : notifications.get(i).getDestination());
            values.put(Notification.COLUMN_POST, notifications.get(i).getPost() == null ? "1" : notifications.get(i).getPost());
            values.put(Notification.COLUMN_TIMESTAMP, notifications.get(i).getTimestamp());
            values.put(Notification.COLUMN_TYPE, notifications.get(i).getType() == null ? "1" : notifications.get(i).getType());
            values.put(Notification.COLUMN_IDEXTRA,notifications.get(i).getId_extra() == null ? "1" : notifications.get(i).getId_extra());
            values.put(Notification.COLUMN_IDNAME,notifications.get(i).getId_name() == null ? "1" : notifications.get(i).getId_name());
            values.put(Notification.COLUMN_IDEXTRA2,notifications.get(i) == null ? "1" : notifications.get(i).getId_extra2());
            // insert row
            db.insertWithOnConflict(Notification.TABLE_NAME, null, values,SQLiteDatabase.CONFLICT_REPLACE);

            //db.insert(Notification.TABLE_NAME,null,values);



        }

        // close db connection
        db.close();


    }

    public Long getLastNotificationTimestamp(){

        String selectQuery = "SELECT  * FROM " + Notification.TABLE_NAME + " ORDER BY " +
                Post.COLUMN_TIMESTAMP + " DESC LIMIT 1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        Long timestamp = null;

        if (cursor.moveToFirst()){
            do {
                timestamp = cursor.getLong(cursor.getColumnIndex(Notification.COLUMN_TIMESTAMP));
            }while (cursor.moveToNext());
        }


        // close db connection
        db.close();

        return timestamp;



    }

    public Long getFirstNotificationTimestamp(){

        String selectQuery = "SELECT  * FROM " + Notification.TABLE_NAME + " ORDER BY " +
                Post.COLUMN_TIMESTAMP + " LIMIT 1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        Long timestamp = null;

        if (cursor.moveToFirst()){
            do {
                timestamp = cursor.getLong(cursor.getColumnIndex(Post.COLUMN_TIMESTAMP));
            }while (cursor.moveToNext());
        }


        // close db connection
        db.close();

        return timestamp;

    }

    public void insertAllGroupPost(List<Group_Post> groupposts) throws JSONException {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        for (int i  = 0 ;i<groupposts.size();i++) {

            ContentValues values = new ContentValues();
            // no need to add them
            values.put(Group_Post.COLUMN_DOCID, groupposts.get(i).getDocument_id());
            values.put(Group_Post.COLUMN_GRP_ID, groupposts.get(i).getGrp_id());
            values.put(Group_Post.COLUMN_AUTHOR, groupposts.get(i).getAuthor());
            values.put(Group_Post.COLUMN_TIMESTAMP, groupposts.get(i).getTimestamp());
            values.put(Group_Post.COLUMN_TEXT, groupposts.get(i).getText());
            values.put(Group_Post.COLUMN_UID, groupposts.get(i).getUid());
            values.put(Group_Post.COLUMN_ORIGINAL, groupposts.get(i).getOriginal());

            values.put(Group_Post.COLUMN_ORIGINAL, groupposts.get(i).getOriginal());
            values.put(Group_Post.COLUMN_THUMB, groupposts.get(i).getThumb());










            // insert row
            db.insertWithOnConflict(Group_Post.TABLE_NAME, null, values,SQLiteDatabase.CONFLICT_REPLACE);

        }

        // close db connection
        db.close();


    }

    public int getGroupPostCount(String groupid) {
        String countQuery = "SELECT  * FROM " + Group_Post.TABLE_NAME + " WHERE " + Group_Post.COLUMN_GRP_ID + " =? ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, new String[] {groupid});

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }


    public List<Group_Post> getAllGroupPosts(String groupid) {
        List<Group_Post> posts = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Group_Post.TABLE_NAME + " WHERE " + Group_Post.COLUMN_GRP_ID + " =? " + " ORDER BY " +
                Group_Post.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {groupid});

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Group_Post post = new Group_Post();
                post.setDocument_id(cursor.getString(cursor.getColumnIndex(Group_Post.COLUMN_DOCID)));
                post.setGrp_id(cursor.getString(cursor.getColumnIndex(Group_Post.COLUMN_GRP_ID)));
                post.setAuthor(cursor.getString(cursor.getColumnIndex(Group_Post.COLUMN_AUTHOR)));
                post.setTimestamp(cursor.getString(cursor.getColumnIndex(Group_Post.COLUMN_TIMESTAMP)));
                post.setCount_views(cursor.getInt(cursor.getColumnIndex(Group_Post.COLUMN_COUNTVIEWS)));
                post.setCount_comment(cursor.getInt(cursor.getColumnIndex(Group_Post.COLUMN_COUNTCOMMENT)));
                post.setCount_like(cursor.getInt(cursor.getColumnIndex(Group_Post.COLUMN_COUNTLIKE)));
                post.setText(cursor.getString(cursor.getColumnIndex(Group_Post.COLUMN_TEXT)));
                post.setUid(cursor.getString(cursor.getColumnIndex(Group_Post.COLUMN_UID)));
                post.setOriginal(cursor.getString(cursor.getColumnIndex(Group_Post.COLUMN_ORIGINAL)));
                post.setPdf_url(cursor.getString(cursor.getColumnIndex(Group_Post.COLUMN_PDFURL)));
                post.setIs_Like(cursor.getString(cursor.getColumnIndex(Group_Post.COLUMN_ISLIKE)));
                post.setThumb(cursor.getString(cursor.getColumnIndex(Group_Post.COLUMN_THUMB)));

                posts.add(post);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return posts;
    }

    public String getLastGroupPostTimestamp(String groupid){

        String selectQuery = "SELECT  * FROM " + Group_Post.TABLE_NAME + " WHERE " + Group_Post.COLUMN_GRP_ID + " =? " + " ORDER BY " +
                Group_Post.COLUMN_TIMESTAMP + " DESC LIMIT 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {groupid});
        String timestamp = null;
        if (cursor.moveToFirst()){
            do {
                timestamp = cursor.getString(cursor.getColumnIndex(Group_Post.COLUMN_TIMESTAMP));
            }while (cursor.moveToNext());
        }
        // close db connection
        db.close();
        return timestamp;

    }

    public String getFirstGroupPostTimestamp(String groupid){
        String selectQuery = "SELECT  * FROM " + Group_Post.TABLE_NAME + " WHERE " + Group_Post.COLUMN_GRP_ID + " =? " + " ORDER BY " +
                Group_Post.COLUMN_TIMESTAMP + " LIMIT 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {groupid});
        String timestamp = null;
        if (cursor.moveToFirst()){
            do {
                timestamp = cursor.getString(cursor.getColumnIndex(Group_Post.COLUMN_TIMESTAMP));
            }while (cursor.moveToNext());
        }
        // close db connection
        db.close();
        return timestamp;
    }






    public boolean deleteGroupPost(String docid,String grp_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            return db.delete(Group_Post.TABLE_NAME, Group_Post.COLUMN_DOCID + "='" + docid+"'",null) > 0;
        }catch (Exception e){
            return true;
        }
    }

    public void deleteAllGroupPosts(String groupid){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ Group_Post.TABLE_NAME + " WHERE " + Group_Post.COLUMN_GRP_ID + " ='"+groupid +"'");
        db.close();
    }

    public void deleteAllPosts(){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ Post.TABLE_NAME);
        db.close();
    }

    public ArrayList<Integer> getGroupPostCounter(String postid){

        ArrayList<Integer> counter = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + Group_Post.TABLE_NAME + " WHERE " +
                Group_Post.COLUMN_DOCID + " =? " ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,  new String[] {postid});


        if (cursor.moveToFirst()){
            do {
                Group_Post post = new Group_Post();

                counter.add(cursor.getInt(cursor.getColumnIndex(Group_Post.COLUMN_COUNTLIKE)));
                counter.add(cursor.getInt(cursor.getColumnIndex(Group_Post.COLUMN_COUNTCOMMENT)));
                counter.add(cursor.getInt(cursor.getColumnIndex(Group_Post.COLUMN_COUNTVIEWS)));
            }while (cursor.moveToNext());
        }


        // close db connection
        db.close();

        return counter;



    }

    public void updateCounterGroupPost(String postid,String type,int value){
        ContentValues cv = new ContentValues();
        cv.put(type,value);

        SQLiteDatabase db = this.getWritableDatabase();

        db.update(Group_Post.TABLE_NAME,cv,Group_Post.COLUMN_DOCID+"= ?",new String[]{postid});

        db.close();



    }

    public void updateLikeGroupPost(String postid,String type,String value){
        ContentValues cv = new ContentValues();
        cv.put(type,value);

        SQLiteDatabase db = this.getWritableDatabase();

        db.update(Group_Post.TABLE_NAME,cv,Group_Post.COLUMN_DOCID+"= ?",new String[]{postid});

        db.close();



    }




    public String checkLikeGroupPostValue(String postid){

        String selectQuery = "SELECT  * FROM " + Group_Post.TABLE_NAME + " WHERE " +
                Group_Post.COLUMN_DOCID + " =? " ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {postid});
        String present = " ";

        if (cursor.moveToFirst()){
            do {
                present = cursor.getString(cursor.getColumnIndex(Group_Post.COLUMN_ISLIKE));
            }while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        return present;

    }

    public ArrayList<String> getAllDataCounters(String type){

        ArrayList<String> uidlist = new ArrayList<>();

        String selectQuery = "SELECT "+ DataCounters.COLUMN_UID + "  FROM " + DataCounters.TABLE_NAME + " WHERE " + DataCounters.COLUMN_TYPE +" IN('"+DataCounters.CONTACT+"')";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        String timestamp = null;

        if (cursor.moveToFirst()){
            do {
                uidlist.add(cursor.getString(cursor.getColumnIndex(DataCounters.COLUMN_UID)));
            }while (cursor.moveToNext());
        }


        // close db connection
        db.close();

        return uidlist;

    }



        }


