package in.kriger.newkrigercampus.activities;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ortiz.touchview.TouchImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.extras.Processor;
import in.kriger.newkrigercampus.extras.SpecialTextView;
import in.kriger.newkrigercampus.services.KrigerConstants;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class ImageActivity extends AppCompatActivity {

    private ScaleGestureDetector mScaleGestureDetector;

    private float mScaleFactor = 1.0f;
    TouchImageView imageView;
    SpecialTextView posttext;
    String postcontent;

    private TextView  textView_username, textView_date, textView_bio;
    private ImageView imageView_dp;

    ArrayList<String> uidlist_post = new ArrayList<>();
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        setTitle("Photo");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile_bar_gradient));


        ProgressBar loading_circle = findViewById(R.id.loading);
        imageView = findViewById(R.id.image);
        posttext =  findViewById(R.id.posttext);
        textView_username =  findViewById(R.id.textView_username);
        textView_date = findViewById(R.id.textView_date);
        textView_bio =  findViewById(R.id.textView_bio);
        imageView_dp = findViewById(R.id.profile_photo);

        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());


            loading_circle.setVisibility(View.VISIBLE);

            RequestOptions requestOption = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.DATA);

        Picasso.get().load(getIntent().getExtras().getString("url")).into(imageView);


        postcontent = getIntent().getExtras().getString("posttext");
        posttext.setText(postcontent);


        try{
            if (postcontent.length()>150) {
                String text = postcontent.substring(0, 150) + " ... ";
                String see_more = getApplicationContext().getResources().getString(R.string.see_more);
                posttext.setText(Html.fromHtml(text + see_more ));

            }

        }catch (NullPointerException e){

        }

        posttext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AnswerListActivity.class);
                intent.putExtra("post_id", getIntent().getExtras().getString("question_id"));
                startActivity(intent);

            }
        });







        if (posttext.getMentions().size()>0){
            KrigerConstants.postRef.child(getIntent().getExtras().getString("question_id")).child("mention_uid").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){

                        String[] separated = dataSnapshot.getValue().toString().split(",");

                        uidlist_post.addAll(Arrays.asList(separated));
                        posttext.setMentionEnabled(true);
                        posttext.setOnMentionClickListener(new Function2<TextView, String, Unit>() {
                            @Override
                            public Unit invoke(TextView textView, String s) {

                                List<String> mentions = posttext.getMentions();
                                for (int i = 0 ;i <mentions.size();i++){
                                    if (mentions.get(i).equals(s)){
                                        if (!uidlist_post.get(i).equals("1")) {

                                                Intent intent = new Intent(getApplicationContext(), ProfileListActivity.class);
                                                intent.putExtra("user_id", uidlist_post.get(i));
                                                startActivity(intent);

                                        }

                                    }
                                }
                                return null;
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        if (posttext.getHyperlinks().size() >0) {
            posttext.setOnHyperlinkClickListener(new Function2<TextView, String, Unit>() {
                @Override
                public Unit invoke(TextView textView, String s) {
                    if (!s.startsWith("http://") && !s.startsWith("https://")) {
                        s = "http://" + s;
                    }
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                    startActivity(browserIntent);
                    return null;
                }
            });
        }


        textView_date.setText(Processor.timestamp(getIntent().getExtras().getString("date")));

        KrigerConstants.user_detailRef.child(getIntent().getExtras().getString("details")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    textView_username.setText(dataSnapshot.child("name").getValue().toString());
                    RequestOptions requestOption = new RequestOptions()
                            .placeholder(R.drawable.default_profile)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


                    Glide.with(getApplicationContext()).load(dataSnapshot.child("thumb").getValue().toString())
                            .apply(requestOption)
                            .into(imageView_dp);

                    //Picasso.with(getApplicationContext()).load(dataSnapshot.child("thumb").getValue().toString()).into(imageView_dp);
                    textView_bio.setText(dataSnapshot.child("headline").getValue().toString());
                } catch (NullPointerException e) {
                    imageView_dp.setImageResource(R.drawable.default_profile);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                super.onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return mScaleGestureDetector.onTouchEvent(event);
    }

    // when a scale gesture is detected, use it to resize the image
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            if (mScaleFactor < 8.0f && mScaleFactor > 0.3f) {
                imageView.setScaleX(mScaleFactor);
                imageView.setScaleY(mScaleFactor);
            }
            return true;
        }
    }


}
