package in.kriger.newkrigercampus.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.database.DatabaseHelper;
import in.kriger.newkrigercampus.extras.PrefManager;
import in.kriger.newkrigercampus.services.KrigerConstants;

public class SettingsActivity extends AppCompatActivity {

    TextView privacy,terms,changepass,website,logout,delete,email_input,email;
    ImageButton fb,insta,twitter,linkedin;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private PrefManager prefManager;
    DatabaseHelper db;
    View email_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        setTitle("Settings");

        prefManager = new PrefManager(getApplicationContext());
        db = new DatabaseHelper(getApplicationContext());
        privacy = findViewById(R.id.privacy);
        terms = findViewById(R.id.terms);
        changepass = findViewById(R.id.changepass);
        website = findViewById(R.id.website);
        logout = findViewById(R.id.logout);
        delete = findViewById(R.id.delete);
        fb = findViewById(R.id.fb);
        insta = findViewById(R.id.insta);
        twitter = findViewById(R.id.twitter);
        linkedin = findViewById(R.id.linkdin);
        email = findViewById(R.id.email);
        email_input = findViewById(R.id.email_input);
        email_view = findViewById(R.id.email_view);

        KrigerConstants.userRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("email").exists()){
                    email.setVisibility(View.VISIBLE);
                    email_input.setVisibility(View.VISIBLE);
                    email_view.setVisibility(View.VISIBLE);
                    email_input.setText(dataSnapshot.child("email").getValue().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),WebViewActivity.class);
                intent.putExtra("url","https://kriger.in/term-conditions");
                intent.putExtra("title","Privacy Policy");
                startActivity(intent);
            }
        });

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),WebViewActivity.class);
                intent.putExtra("url","https://kriger.in/term-conditions");
                intent.putExtra("title","Terms and Conditions");
                startActivity(intent);
            }
        });

        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),WebViewActivity.class);
                intent.putExtra("url","https://kriger.in");
                intent.putExtra("title","Our Website");
                startActivity(intent);
            }
        });

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),WebViewActivity.class);
                intent.putExtra("url","https://www.facebook.com/Krigercampus/");
                intent.putExtra("title","Facebook");
                startActivity(intent);
            }
        });

        insta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),WebViewActivity.class);
                intent.putExtra("url","https://www.instagram.com/krigercampus");
                intent.putExtra("title","Instagram");
                startActivity(intent);
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),WebViewActivity.class);
                intent.putExtra("url","https://twitter.com/krigercampus");
                intent.putExtra("title","Twitter");
                startActivity(intent);
            }
        });

        linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),WebViewActivity.class);
                intent.putExtra("url","https://www.linkedin.com/company/kriger-campus/");
                intent.putExtra("title","Linked In");
                startActivity(intent);
            }
        });

        changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogBuilder("You will be logged out of the app. Are you sure?",4,true,"No","Yes",null);

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogBuilder("Just a quick reminder, Deleting your account means you'll lose touch with your networks, notifications of your activities etc. Are you sure?",5,true,"No","Yes",null);

            }
        });

    }

    private Dialog dialogBuilder(String text, final int type, Boolean isNegative, String posText, String negText, final Object content){

        final Dialog dialog = new Dialog(SettingsActivity.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        dialog.setContentView(R.layout.layout_dialog);

        TextView textView = dialog.findViewById(R.id.dialogue_text);
        textView.setText(text);


        Button posButton =  dialog.findViewById(R.id.button_positive);
        posButton.setText(posText);
        posButton.setVisibility(View.VISIBLE);
        posButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(type==4)
                {
                    dialog.dismiss();

                }else if(type ==5){
                    dialog.dismiss();
                }
            }
        });
        if (isNegative){

            Button negButton =  dialog.findViewById(R.id.button_negative);
            negButton.setText(negText);
            negButton.setVisibility(View.VISIBLE);
            negButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     if (type == 4){
                         KrigerConstants.userRef.child(user.getUid()).child("profile_token").removeValue();
                         FirebaseAuth.getInstance().signOut();
                         prefManager.clearPreferences();
                         db.deleteAllTables();
                         Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                         startActivity(intent);
                    }else if(type == 5){
                         KrigerConstants.userRef.child(user.getUid()).child("deactivate").setValue(1);
                         KrigerConstants.userRef.child(user.getUid()).child("profile_token").removeValue();
                         FirebaseAuth.getInstance().signOut();
                         prefManager.clearPreferences();
                         prefManager.setDeactivate(true);
                         db.deleteAllTables();
                         Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                         startActivity(intent);
                     }
                    dialog.dismiss();

                }
            });
        }

        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {

        }

        return dialog;

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ProfileListActivity.class);
        intent.putExtra("user_id",user.getUid());
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);

        super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(), ProfileListActivity.class);
                intent.putExtra("user_id", user.getUid());
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
