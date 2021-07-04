package in.kriger.newkrigercampus.activities;

import android.content.Intent;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import in.kriger.newkrigercampus.R;

public class SuggestionReplyActivity extends AppCompatActivity {

    private Button back_home;
    private TextView thank_text;
    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestionreply);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Suggestions");
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile_bar_gradient));

        back_home=findViewById(R.id.back_btn);
        back_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SuggestionReplyActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });


        thank_text=findViewById(R.id.thanku_text);
        thank_text.setText("Thank You " + user.getDisplayName() + "!" + "\n\n Your suggestions have been submitted successfully.");

    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(this,HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
