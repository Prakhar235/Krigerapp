package in.kriger.newkrigercampus.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import in.kriger.newkrigercampus.R;

public class Terms_ConditionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms__conditions);
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        setTitle("Add Resources");

        final TextView terms_and_condition = (TextView) findViewById(R.id.terms);

        //terms and condition in blue color
        String blue_TandC = getResources().getString(R.string.termsandconditionsresource);
        terms_and_condition.setHint(Html.fromHtml(blue_TandC));

        final String timestamp = getIntent().getExtras().getString("timestamp");

        final TextView link =  findViewById(R.id.terms);
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://kriger.in/term-conditions");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });


        Button button = findViewById(R.id.button_agree);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),CreateResourceActivity.class);
                intent.putExtra("timestamp",timestamp);
                startActivity(intent);
            }
        });
    }



    @Override
    public void onBackPressed() {
        //this is only needed if you have specific things
        //that you want to do when the user presses the back button.
        /* your specific things...*/
        super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                this.finish();
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
