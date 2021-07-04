package in.kriger.newkrigercampus.extras;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by poojanrathod on 2/17/18.
 */

public class PrefManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "krigercampus";

    private static final String USER_PER_DAY = "UserPerDay";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_FIRST_TIME_INTRO = "IsFirstTimeIntro";
    private static final String PROFILE_IMAGE_URL = "profileImageUrl";
    private static final String COUNTER_CONNECTIONS = "connections";
    private static final String COUNTER_CONNECTREQUEST = "connectrequest";
    private static final String COUNTER_SENTREQUEST = "connectrequestsent";
    private static final String COUNTER_GROUPINVITES="groupinvites";
    private static final String COUNTER_GROUPPOSTS="groupposts";
    private static final String COUNTER_GROUPS = "groups";
    private static final String COUNTER_PROFILEVIEWS="profileviews";
    private static final String COUNTER_NOTIFICATIONS = "notifications";
    private static final String ACCOUNT_TYPE = "educator";
    private static final String DEACTIVATE = "deactivate";
    private static final String TRANS_MARKET = "trans_market";
    private static final String TRANS_GROUPS = "trans_groups";
    private static final String TRANS_KRIGERS = "trans_krigers";
    private static final String TRANS_PROFILE = "trans_profile";
    private static final String TRANS_POST_TIPS = "trans_post_tips";
    private static final String TRANS_GROUP_POST_TIPS = "trans_group_post_tips";
    private static final String TRANS_POST_WRITE = "trans_post_write";
    private static final String TRANS_CONNECTION_TIPS = "trans_connection_tips";
    private static final String TRANS_EDIT_PROFILE = "trans_edit_profile";
    private static final String TRANS_FIRST_VISIT = "trans_first_visit";
    private static final String REFRESH_WEEK = "refresh_week";
    private static final String IS_PERMISSION = "is_permission";
    private static final String IS_LASTSIGNIN = "is_lastsignin";
    private static final String ISNEWCONNECTION = "isnewConnection";
    private static final String DATE_OF_JOINING = "dateofjoining";
    private static final String DATABASE_VERSION = "database_version";
    private static final String COUNT_VISITS = "count_visits";

    public PrefManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void setIsLastsignin(String isLastsignin) {
        editor.putString(IS_LASTSIGNIN, isLastsignin);
        editor.commit();
    }

    public void setCountVisits(int count){
        editor.putInt(COUNT_VISITS, count);
        editor.commit();
    }

    public int getCountVisits(){
        return  pref.getInt(COUNT_VISITS,0);
    }


    public String getisLastSignin() {
        return pref.getString(IS_LASTSIGNIN, null);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }


    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, false);
    }

    public void setFirstTimeIntro(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_INTRO, isFirstTime);
        editor.commit();
    }


    public boolean isFirstTimeIntro() {
        return pref.getBoolean(IS_FIRST_TIME_INTRO, false);
    }



    public void setIsnewconnection(boolean isnewconnection) {
        editor.putBoolean(ISNEWCONNECTION, isnewconnection);
        editor.commit();
    }


    public boolean isNewConnection() {
        return pref.getBoolean(ISNEWCONNECTION, false);
    }


    public void setDatabaseVersion(int databaseVersion) {
        editor.putInt(DATABASE_VERSION, databaseVersion);
        editor.commit();
    }

    public int getDatabaseVersion() {
        return pref.getInt(DATABASE_VERSION, 0);
    }


    public String getProfileImageUrl(){
        return pref.getString(PROFILE_IMAGE_URL,null);
    }

    public void setProfileImageUrl(String url){
        editor.putString(PROFILE_IMAGE_URL,url);
        editor.commit();
    }

    public void clearPreferences(){
        editor.remove(PROFILE_IMAGE_URL);
        editor.remove(REFRESH_WEEK);
        editor.remove(IS_LASTSIGNIN);
        editor.remove(COUNTER_CONNECTIONS);
        editor.remove(COUNTER_CONNECTREQUEST);
        editor.remove(COUNTER_SENTREQUEST);
        editor.remove(COUNTER_NOTIFICATIONS);
        editor.remove(COUNTER_GROUPS);
        editor.remove(COUNTER_GROUPINVITES);
        editor.remove(COUNTER_GROUPPOSTS);
        editor.remove(DATE_OF_JOINING);
        editor.remove(ACCOUNT_TYPE);
        editor.remove(DEACTIVATE);
        editor.remove(COUNT_VISITS);
        editor.commit();
    }

    public void clearProfileImageUrl(){
        editor.putString(PROFILE_IMAGE_URL,null);
        editor.commit();
    }

    public void setRefreshWeek(String timestamp){
        editor.putString(REFRESH_WEEK,timestamp);
        editor.commit();
    }

    public String getRefreshWeek(){
        return pref.getString(REFRESH_WEEK,null);

    }

    public void setDateOfJoining(String dateOfJoining){
        editor.putString(DATE_OF_JOINING,dateOfJoining);
        editor.commit();
    }

    public String getDateOfJoining(){
        return pref.getString(DATE_OF_JOINING,"0");

    }

    public int getCounterConnections(){
        return pref.getInt(COUNTER_CONNECTIONS,0);
    }

    public int getCounterConnectRequest(){
        return pref.getInt(COUNTER_CONNECTREQUEST,0);

    }
    public int getCounterProfileviews() {
        return pref.getInt(COUNTER_PROFILEVIEWS,0);
    }
    public int getCounterGroupinvites() {
        return pref.getInt(COUNTER_GROUPINVITES,0);
    }

    public int getCounterGroupposts() {
        return pref.getInt(COUNTER_GROUPPOSTS,0);
    }

    public int getCounterGroups() {
        return pref.getInt(COUNTER_GROUPS,0);
    }

    public int getCounterSentRequest(){
        return pref.getInt(COUNTER_SENTREQUEST,0);
    }



    public void setCounter(int number,String type){
        editor.putInt(type,number);
        editor.commit();
    }


    public int getCounterNotifications(){
        return pref.getInt(COUNTER_NOTIFICATIONS,0);

    }



    public void setTransMarket(boolean transMarket) {
        editor.putBoolean(TRANS_MARKET, transMarket);
        editor.commit();
    }

    public boolean getTransMarket() {
        return pref.getBoolean(TRANS_MARKET, false);
    }

    public void setTransGroups(boolean transGroups) {
        editor.putBoolean(TRANS_GROUPS, transGroups);
        editor.commit();
    }

    public boolean getTransGroups() {
        return pref.getBoolean(TRANS_GROUPS, false);
    }




    public void setTransKrigers(boolean transKrigers) {
        editor.putBoolean(TRANS_KRIGERS, transKrigers);
        editor.commit();
    }

    public boolean getTransKrigers() {
        return pref.getBoolean(TRANS_KRIGERS, false);
    }


    public void setAccountType(int type) {
        editor.putInt(ACCOUNT_TYPE, type);
        editor.commit();
    }

    public int getAccountType() {
        return pref.getInt(ACCOUNT_TYPE, 0);
    }

    public void setDeactivate(boolean deactivate) {
        editor.putBoolean(DEACTIVATE, deactivate);
        editor.commit();
    }

    public boolean getDeactivate() {
        return pref.getBoolean(DEACTIVATE, false);
    }



    public void setTransProfile(boolean transProfile) {
        editor.putBoolean(TRANS_PROFILE, transProfile);
        editor.commit();
    }

    public boolean getTransProfile() {
        return pref.getBoolean(TRANS_PROFILE, false);
    }


    public void setTransPostTips(boolean transPostTips) {
        editor.putBoolean(TRANS_POST_TIPS, transPostTips);
        editor.commit();
    }

    public boolean getTransPostTips() {
        return pref.getBoolean(TRANS_POST_TIPS, false);
    }

    public void setTransGroupPostTips(boolean transGroupPostTips) {
        editor.putBoolean(TRANS_GROUP_POST_TIPS, transGroupPostTips);
        editor.commit();
    }

    public boolean getTransGroupPostTips() {
        return pref.getBoolean(TRANS_GROUP_POST_TIPS, false);
    }

    public void setTransConnectionTips(boolean transConnectionTips) {
        editor.putBoolean(TRANS_CONNECTION_TIPS, transConnectionTips);
        editor.commit();
    }

    public boolean getTransConnectionTips() {
        return pref.getBoolean(TRANS_CONNECTION_TIPS, false);
    }

    public void setIsPermission(boolean isPermission) {
        editor.putBoolean(IS_PERMISSION, isPermission);
        editor.commit();
    }

    public boolean getIsPermission() {
        return pref.getBoolean(IS_PERMISSION, false);
    }

    public void setTransPostWrite(boolean transPostWrite) {
        editor.putBoolean(TRANS_POST_WRITE, transPostWrite);
        editor.commit();
    }

    public boolean getTransPostWrite() {
        return pref.getBoolean(TRANS_POST_WRITE, false);
    }


    public void setTransEditProfile(boolean transEditProfile) {
        editor.putBoolean(TRANS_EDIT_PROFILE, transEditProfile);
        editor.commit();
    }

    public boolean getTransEditProfile() {
        return pref.getBoolean(TRANS_EDIT_PROFILE, false);
    }

    public void setTransFirstVisit(boolean transFirstVisit) {
        editor.putBoolean(TRANS_FIRST_VISIT, transFirstVisit);
        editor.commit();
    }

    public boolean getTransFirstVisit() {
        return pref.getBoolean(TRANS_FIRST_VISIT, false);
    }


}
