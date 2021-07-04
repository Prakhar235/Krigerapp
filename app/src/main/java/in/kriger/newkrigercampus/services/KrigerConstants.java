package in.kriger.newkrigercampus.services;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URL;

/**
 * Created by poojanrathod on 12/31/17.
 */

public class KrigerConstants {
    URL stagingurl;
    URL produrl;
    URL getmethod()
    {

     return stagingurl;
    }


    final public static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/");

    final public static DatabaseReference postRef = mDatabase.child("Post");

    final public static DatabaseReference user_detailRef = mDatabase.child("User_Detail");

    final public static DatabaseReference connectionRef = mDatabase.child("Connection");

    final public static DatabaseReference feedbackRef = mDatabase.child("Feedback");

    final public static DatabaseReference group_counterRef = mDatabase.child("Group_Counter");

    final public static DatabaseReference group_dataRef = mDatabase.child("Group_Data");

    final public static DatabaseReference group_nameRef = mDatabase.child("Group_Name");

    final public static DatabaseReference group_postRef = mDatabase.child("Group_Post");

    final public static DatabaseReference group_post_commentRef = mDatabase.child("Group_Post_Comment");

    final public static DatabaseReference group_post_comment_counterRef = mDatabase.child("Group_Post_Comment_Counter");

    final public static DatabaseReference group_post_comment_likeRef = mDatabase.child("Group_Post_Comment_Like");

    final public static DatabaseReference group_post_counterRef = mDatabase.child("Group_Post_Counter");

    final public static DatabaseReference group_post_likeRef = mDatabase.child("Group_Post_Like");

    final public static DatabaseReference group_post_viewRef = mDatabase.child("Group_Post_View");

    final public static DatabaseReference group_suggestionRef = mDatabase.child("Group_Suggestion");

    final public static DatabaseReference inivitationRef = mDatabase.child("Invitation");

    final public static DatabaseReference mute_notificationRef = mDatabase.child("Mute_Notification");

    final public static DatabaseReference notificationRef = mDatabase.child("Notification");

    final public static DatabaseReference post_commentRef = mDatabase.child("Post_Comment");

    final public static DatabaseReference post_comment_likeRef = mDatabase.child("Post_Comment_Like");

    final public static DatabaseReference post_comment_like_counterRef = mDatabase.child("Post_Comment_Like_Counter");

    final public static DatabaseReference post_counterRef = mDatabase.child("Post_Counter");

    final public static DatabaseReference post_likeRef = mDatabase.child("Post_Like");

    final public static DatabaseReference post_viewRef = mDatabase.child("Post_View");

    final public static DatabaseReference report_postRef = mDatabase.child("Report_Post");

    final public static DatabaseReference sent_connectionRef = mDatabase.child("Sent_Connection");

    final public static DatabaseReference synced_contactRef = mDatabase.child("Synced_Contact");

    final public static DatabaseReference userRef = mDatabase.child("User");

    final public static DatabaseReference user_counterRef = mDatabase.child("User_Counter");

    final public static DatabaseReference user_extra_detailRef = mDatabase.child("User_Extra_Detail");

    final public static DatabaseReference user_listRef = mDatabase.child("User_List");

    final public static DatabaseReference user_suggestionRef = mDatabase.child("User_Suggestion");

    final public static DatabaseReference user_secondvisitRef = mDatabase.child("Second_Visit_Notification");

    final public static DatabaseReference resourceRef = mDatabase.child("Resource");

    final public static DatabaseReference resource_enquiryRef = mDatabase.child("Resource_Enquiry");

    final public static DatabaseReference resource_counterRef = mDatabase.child("Resource_Counter");

    final public static DatabaseReference resource_shareRef = mDatabase.child("Resource_Share");

    final public static DatabaseReference resource_viewRef = mDatabase.child("Resource_View");

    final public static DatabaseReference resource_reviewRef = mDatabase.child("Resource_Review");

    final public static DatabaseReference transaction_historyref = mDatabase.child("Transaction_History");

    final public static DatabaseReference transaction_detailRef = mDatabase.child("Transaction_Detail");

}
