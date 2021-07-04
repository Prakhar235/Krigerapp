package in.kriger.newkrigercampus.activities;

import android.content.Intent;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.services.FireService;


public class SuggestionActivity extends AppCompatActivity {

    private Button submit_feed;
    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion_team);




        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Suggestions");
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile_bar_gradient));



        submit_feed=findViewById(R.id.submit_feedback);

        final RatingBar ratingBar = findViewById(R.id.ratingBar);
        final EditText editText_newfeatures =findViewById(R.id.edittext_newfeatures);
        final String pushkey = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("Feedback").push().getKey();

        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("Feedback").child(pushkey);

        submit_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                 

                 if(ratingBar.getRating()==0){
                     Toasty.custom(getApplicationContext(), "Please submit rating", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                             true).show();
                 }else{
                     HashMap<String, Object> result = new HashMap<>();
                     result.put("rating", String.valueOf(ratingBar.getRating()));
                     result.put("uid",user.getUid());
                     result.put("text",editText_newfeatures.getText().toString());
                     result.put("timestamp", FireService.getToday());
                     myRef.updateChildren(result);
                     Intent intent=new Intent(SuggestionActivity.this,SuggestionReplyActivity.class);
                     startActivity(intent);
                 }


            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        try {
            Log.d("Intent",getIntent().getExtras().getString("source"));
        }catch (NullPointerException e){
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        }
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
