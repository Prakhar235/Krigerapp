package in.kriger.newkrigercampus.activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import in.kriger.newkrigercampus.R;

public class GroupPostTipsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_post_tips);

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile_bar_gradient));

        //Title of Activity
        setTitle("Tips");

        TextView tips_text =  findViewById(R.id.tips_text);
        String post_tips = getResources().getString(R.string.your_ideas_worth_spreading);
        tips_text.setText(Html.fromHtml(post_tips));

        TextView tips_text2 =  findViewById(R.id.tips_text2);
        String why_post_tips = getResources().getString(R.string.why_post_guidelines);
        tips_text2.setText(why_post_tips);

        TextView tips_text3 =  findViewById(R.id.tips_text3);
        String how_post_tips = getResources().getString(R.string.how_post_guidelines);
        tips_text3.setText(how_post_tips);

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),NewGroupPostActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(),NewGroupPostActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
