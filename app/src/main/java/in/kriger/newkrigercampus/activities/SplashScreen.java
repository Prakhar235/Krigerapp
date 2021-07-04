package in.kriger.newkrigercampus.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.database.DatabaseHelper;
import in.kriger.newkrigercampus.extras.PrefManager;
import in.kriger.newkrigercampus.groupactivities.CreateGroup;
import in.kriger.newkrigercampus.groupactivities.GroupAbout;
import in.kriger.newkrigercampus.groupactivities.ShowGroup;
import in.kriger.newkrigercampus.services.KrigerConstants;
import in.kriger.newkrigercampus.services.FireService;

public class SplashScreen extends AppCompatActivity {

    private PrefManager prefManager;

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListner;
    private Boolean signedin = false;
    public static final int PERMISSION_ALL = 124;
    public String time;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    String[] textlist_l = {"Let's find your friends on the app.", "Here are some groups you may like to join.", "Kriger Campus is fun with friends, invite them.", "Share your thoughts with your network.", "Explore resources and study better.", "Filled profiles are more popular. Lets update your profile", "Today's 20 connection suggestions just for you."};
    String[] textlist_e = {"Let's find your friends on the app.", "Grow your business and revenue.", "Create study groups of subject & exam and add members.", "Better ratings help you earn more.", "Kriger Campus is fun with friends, invite them.", "Filled profiles are more popular.Lets update your profile.", "Today's 20 connection suggestions just for you.", "Share your thoughts with your network"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        prefManager = new PrefManager(this);


        mAuth = FirebaseAuth.getInstance();
        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if (!user.isEmailVerified()) {
                        FirebaseAuth.getInstance().signOut();
                    } else {
                        signedin = true;
                    }

                }
            }
        };


        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                checkpermission();


            }
        }, SPLASH_TIME_OUT);


    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListner);

    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListner);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public void checkpermission() {


        try {


            //Check for Notification opening
            if (getIntent().getExtras().getString("type") != null) {

                if (signedin) {
                    if (getIntent().getExtras().getString("type").equals("like_post") || getIntent().getExtras().getString("type").equals("comment_post") || getIntent().getExtras().getString("type").equals("like_comment_post")) {
                        Intent intent = new Intent(getApplicationContext(), AnswerListActivity.class);
                        intent.putExtra("post_id", getIntent().getExtras().getString("id"));
                        intent.putExtra("source", "notification");
                        startActivity(intent);
                    } else if (getIntent().getExtras().getString("type").equals("like_group_post") || getIntent().getExtras().getString("type").equals("comment_group_post") || getIntent().getExtras().getString("type").equals("like_comment_group_post")) {
                        Intent intent = new Intent(getApplicationContext(), AnswerGroupListActivity.class);
                        intent.putExtra("post_id", getIntent().getExtras().getString("id_extra"));
                        intent.putExtra("grp_id", getIntent().getExtras().getString("id"));
                        intent.putExtra("source", "notification");
                        startActivity(intent);
                    } else if ((getIntent().getExtras().getString("type")).equals("owner_group")) {
                        Intent intent = new Intent(getApplicationContext(), GroupAbout.class);
                        intent.putExtra("grp_id", getIntent().getExtras().getString("id"));
                        intent.putExtra("source", "notification");
                        startActivity(intent);
                    } else if (getIntent().getExtras().getString("type").equals("connection_request") || getIntent().getExtras().getString("type").equals("admin_krigers_tab")) {
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        intent.putExtra("screen", "krigers");
                        startActivity(intent);
                    } else if (getIntent().getExtras().getString("type").equals("accept_request")) {
                        Intent intent = new Intent(getApplicationContext(), ProfileListActivity.class);
                        intent.putExtra("user_id", getIntent().getExtras().getString("id"));
                        intent.putExtra("source", "notification");
                        startActivity(intent);
                    } else if (getIntent().getExtras().getString("type").equals("referral_score")) {
                        Intent intent = new Intent(getApplicationContext(), ProfileListActivity.class);
                        intent.putExtra("user_id", getIntent().getExtras().getString("id"));
                        intent.putExtra("source", "notification");
                        startActivity(intent);
                    } else if (getIntent().getExtras().getString("type").equals("profile_visits")) {
                        Intent intent = new Intent(getApplicationContext(), ProfileListActivity.class);
                        intent.putExtra("type", "profile_visits");
                        intent.putExtra("count", getIntent().getExtras().getString("count"));
                        intent.putExtra("user_id", user.getUid());
                        startActivity(intent);
                    } else if (getIntent().getExtras().getString("type").equals("invite_group")) {
                        Intent intent = new Intent(getApplicationContext(), GroupAbout.class);
                        intent.putExtra("grp_id", getIntent().getExtras().getString("id"));
                        intent.putExtra("source", "notification");
                        startActivity(intent);
                    } else if (getIntent().getExtras().getString("type").equals("added_group")) {
                        Intent intent = new Intent(getApplicationContext(), GroupAbout.class);
                        intent.putExtra("grp_id", getIntent().getExtras().getString("id"));
                        intent.putExtra("source", "notification");
                        startActivity(intent);
                    } else if (getIntent().getExtras().getString("type").equals("new_post_group")) {
                        Intent intent = new Intent(getApplicationContext(), ShowGroup.class);
                        intent.putExtra("grp_id", getIntent().getExtras().getString("id"));
                        intent.putExtra("source", "notification");
                        startActivity(intent);
                    } else if (getIntent().getExtras().getString("type").equals("admin_post")) {
                        Intent intent = new Intent(getApplicationContext(), AnswerListActivity.class);
                        intent.putExtra("post_id", getIntent().getExtras().getString("id"));
                        intent.putExtra("source", "notification");
                        intent.putExtra("type", "admin_post");
                        startActivity(intent);
                    } else if (getIntent().getExtras().getString("type").equals("admin_profile_tab")) {
                        Intent intent = new Intent(getApplicationContext(), ProfileListActivity.class);
                        intent.putExtra("user_id", getIntent().getExtras().getString("id"));
                        intent.putExtra("source", "notification");
                        startActivity(intent);
                    } else if (getIntent().getExtras().getString("type").equals("admin_group")) {
                        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                        if (db.isGroupPresent(getIntent().getExtras().getString("id"))) {
                            Intent intent = new Intent(getApplicationContext(), ShowGroup.class);
                            intent.putExtra("grp_id", getIntent().getExtras().getString("id"));
                            intent.putExtra("source", "notification");
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getApplicationContext(), JoinRequestActivity.class);
                            intent.putExtra("grp_id", getIntent().getExtras().getString("id"));
                            intent.putExtra("source", "notification");
                            startActivity(intent);
                        }
                    } else if (getIntent().getExtras().getString("type").equals("tag_post")) {
                        Intent intent = new Intent(getApplicationContext(), AnswerListActivity.class);
                        intent.putExtra("post_id", getIntent().getExtras().getString("id"));
                        intent.putExtra("source", "notification");
                        startActivity(intent);
                    } else if (getIntent().getExtras().getString("type").equals("tag_post_comment")) {
                        Intent intent = new Intent(getApplicationContext(), AnswerListActivity.class);
                        intent.putExtra("post_id", getIntent().getExtras().getString("id"));
                        intent.putExtra("source", "notification");
                        startActivity(intent);
                    } else if (getIntent().getExtras().getString("type").equals("tag_group_post")) {
                        Intent intent = new Intent(getApplicationContext(), AnswerGroupListActivity.class);
                        intent.putExtra("post_id", getIntent().getExtras().getString("id_extra"));
                        intent.putExtra("grp_id", getIntent().getExtras().getString("id"));
                        intent.putExtra("source", "notification");
                        startActivity(intent);
                    } else if (getIntent().getExtras().getString("type").equals("tag_group_post_comment")) {
                        Intent intent = new Intent(getApplicationContext(), AnswerGroupListActivity.class);
                        intent.putExtra("post_id", getIntent().getExtras().getString("id_extra"));
                        intent.putExtra("grp_id", getIntent().getExtras().getString("id"));
                        intent.putExtra("source", "notification");
                        startActivity(intent);
                    } else if (getIntent().getExtras().getString("type").equals("admin_create_post")) {
                        Intent intent = new Intent(getApplicationContext(), NewPostActivity.class);
                        intent.putExtra("source", "notification");
                        startActivity(intent);
                    } else if (getIntent().getExtras().getString("type").equals("admin_invite_friends")) {
                        Intent intent = new Intent(getApplicationContext(), ContactListActivity.class);
                        intent.putExtra("source", "notification");
                        startActivity(intent);
                    } else if (getIntent().getExtras().getString("type").equals("admin_suggestion")) {
                        Intent intent = new Intent(getApplicationContext(), SuggestionActivity.class);
                        intent.putExtra("source", "notification");
                        startActivity(intent);
                    } else if (getIntent().getExtras().getString("type").equals("admin_timeline")) {
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                    } else if (getIntent().getExtras().getString("type").equals("admin_create_group")) {
                        Intent intent = new Intent(getApplicationContext(), CreateGroup.class);
                        intent.putExtra("source", "notification");
                        startActivity(intent);
                    } else if (getIntent().getExtras().getString("type").equals("admin_groups_tab")) {
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        intent.putExtra("screen", "groups");
                        startActivity(intent);
                    }else if (getIntent().getExtras().getString("type").equals("resource_enquiry")){
                        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                        intent.putExtra("screen","resources");
                        intent.putExtra("tab","my");
                        startActivity(intent);

                    } else if (getIntent().getExtras().getString("type").equals("visit_2")) {
                        drawDialog(2);

                    } else if (getIntent().getExtras().getString("type").equals("visit_3")) {
                        drawDialog(3);

                    } else if (getIntent().getExtras().getString("type").equals("visit_4")) {
                        drawDialog(4);

                    } else if (getIntent().getExtras().getString("type").equals("visit_5")) {
                        drawDialog(5);

                    } else if (getIntent().getExtras().getString("type").equals("visit_6")) {
                        drawDialog(6);

                    } else if (getIntent().getExtras().getString("type").equals("visit_7")) {
                        drawDialog(7);

                    } else if (getIntent().getExtras().getString("type").equals("visit_8")) {
                        drawDialog(8);

                    } else if (getIntent().getExtras().getString("type").equals("visit_9")) {
                        drawDialog(9);

                    } else {
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                    }


                } else {

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.putExtra("is_start", true);
                    startActivity(intent);

                }


            } else {
                if (signedin) {
                    if ((prefManager.getCountVisits() == 9 && prefManager.getAccountType() > 0) || (prefManager.getCountVisits() == 8 && prefManager.getAccountType()==0)) {
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        intent.putExtra("is_start", true);
                        startActivity(intent);
                    }else {
                        int visit_count = prefManager.getCountVisits();

                        if (visit_count != 0){
                            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                            int mins = Calendar.getInstance().get(Calendar.MINUTE);
                            int days = FireService.getDiffDays(prefManager.getDateOfJoining().substring(0,8));
                            if (days == 0){
                                if (visit_count == 1) {
                                    int diffmins = FireService.getDiffMins(prefManager.getDateOfJoining());
                                    if (diffmins > 45) {
                                        drawDialog(2);
                                    }else{
                                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                        intent.putExtra("is_start", true);
                                        startActivity(intent);
                                    }
                                }else {
                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                    intent.putExtra("is_start", true);
                                    startActivity(intent);
                                }
                            }else {
                                if (hour > 10 && mins > 30) {
                                    if ((days + 1) == prefManager.getCountVisits()) {
                                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                        intent.putExtra("is_start", true);
                                        startActivity(intent);
                                    } else {
                                        drawDialog(prefManager.getCountVisits() + 1);
                                    }
                                } else {
                                    if (days == prefManager.getCountVisits()) {
                                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                        intent.putExtra("is_start", true);
                                        startActivity(intent);
                                    } else {
                                        drawDialog(prefManager.getCountVisits() + 1);
                                    }

                                }
                            }


                        }else {
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            intent.putExtra("is_start", true);
                            intent.putExtra("start","first");
                            startActivity(intent);
                        }

                    }
                } else {

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.putExtra("is_start", true);
                    startActivity(intent);
                }

            }



        } catch (NullPointerException e) {

            if (signedin) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.putExtra("is_start", true);
                startActivity(intent);
            } else {

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.putExtra("is_start", true);
                startActivity(intent);
            }

        }


    }

    private void drawDialog(final int visit_number) {
        final Dialog dialog = new Dialog(SplashScreen.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        final int account_type = prefManager.getAccountType();


        dialog.setContentView(R.layout.layout_dialog);

        TextView textView = (TextView) dialog.findViewById(R.id.dialogue_text);
        if (prefManager.getAccountType() > 0) {
            textView.setText(textlist_e[visit_number - 2]);
        } else {
            textView.setText(textlist_l[visit_number - 2]);
        }

        Button posButton = (Button) dialog.findViewById(R.id.button_positive);
        posButton.setText("OK");
        posButton.setVisibility(View.VISIBLE);
        posButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (visit_number == 2){
                    Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                    intent.putExtra("visit",2);
                    startActivity(intent);

                }else if (visit_number == 3){
                    prefManager.setCountVisits(3);
                    KrigerConstants.user_extra_detailRef.child(user.getUid()).child("count_visits").setValue(3);

                    if (account_type > 0 ){

                        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                        intent.putExtra("screen","resources");
                        intent.putExtra("tab","my");
                        startActivity(intent);

                    }else{
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        intent.putExtra("screen","groups");
                        startActivity(intent);
                    }

                }else if (visit_number == 4){
                    prefManager.setCountVisits(4);

                    KrigerConstants.user_extra_detailRef.child(user.getUid()).child("count_visits").setValue(4);
                    if (account_type > 0){
                        Intent intent = new Intent(getApplicationContext(),CreateGroup.class);
                        intent.putExtra("source","notification");
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(getApplicationContext(),ContactListActivity.class);
                        intent.putExtra("source","notification");
                        startActivity(intent);
                    }

                }else if (visit_number == 5){
                    prefManager.setCountVisits(5);

                    KrigerConstants.user_extra_detailRef.child(user.getUid()).child("count_visits").setValue(5);
                    if (account_type > 0){
                        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                        intent.putExtra("screen","resources");
                        startActivity(intent);

                    }else {
                        Intent intent = new Intent(getApplicationContext(),NewPostActivity.class);
                        intent.putExtra("source","notification");
                        startActivity(intent);
                    }

                }else if (visit_number == 6){
                    prefManager.setCountVisits(6);

                    KrigerConstants.user_extra_detailRef.child(user.getUid()).child("count_visits").setValue(6);
                    if (account_type > 0){
                        Intent intent = new Intent(getApplicationContext(),ContactListActivity.class);
                        intent.putExtra("source","notification");
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                        intent.putExtra("screen","resources");
                        startActivity(intent);
                    }

                }else if (visit_number == 7){
                    prefManager.setCountVisits(7);

                    KrigerConstants.user_extra_detailRef.child(user.getUid()).child("count_visits").setValue(7);
                    Intent intent = new Intent(getApplicationContext(),ProfileListActivity.class);
                    intent.putExtra("user_id",user.getUid());
                    intent.putExtra("source","notification");
                    startActivity(intent);


                }else if (visit_number == 8){
                    prefManager.setCountVisits(8);

                    KrigerConstants.user_extra_detailRef.child(user.getUid()).child("count_visits").setValue(8);
                    Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                    intent.putExtra("screen","krigers");
                    startActivity(intent);


                }else if (visit_number == 9){
                    if (account_type > 0) {
                        prefManager.setCountVisits(9);

                        KrigerConstants.user_extra_detailRef.child(user.getUid()).child("count_visits").setValue(9);
                        Intent intent = new Intent(getApplicationContext(), NewPostActivity.class);
                        intent.putExtra("source","notification");
                        startActivity(intent);
                    }
                }
            }
        });

        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {

        }


    }


}

