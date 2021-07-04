package in.kriger.newkrigercampus.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.services.KrigerConstants;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


public class InvitationActivity extends AppCompatActivity {

    ImageView profile_image;
    TextView user_name,contribution;
    TextView posts_no,groups_no,connections_no,visitors_no;
    Button btn_contribution;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);


        String referid = getIntent().getExtras().getString("user_id");

        profile_image =  findViewById(R.id.imageButton_dp);
        user_name =  findViewById(R.id.name);
        contribution =  findViewById(R.id.textview_contribution);

        posts_no = findViewById(R.id.posts_ans);
        groups_no = findViewById(R.id.groups_ans);
        connections_no =  findViewById(R.id.connections_ans);
        visitors_no =  findViewById(R.id.visitors_ans);

        btn_contribution =  findViewById(R.id.btn_start_contributing);
        btn_contribution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                intent.putExtra("is_start",true);
                startActivity(intent);
            }
        });

        KrigerConstants.user_detailRef.child(referid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("name").exists()){
                    user_name.setText(dataSnapshot.child("name").getValue().toString());

                    contribution.setText(dataSnapshot.child("name").getValue().toString().substring(0,dataSnapshot.child("name").getValue().toString().indexOf(' '))+ "\'s contribution to our community");
                }



                if (dataSnapshot.child("thumb").exists()){
                    RequestOptions requestOption = new RequestOptions()
                            .placeholder(R.drawable.default_profile)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

                    Glide.with(getApplicationContext()).load(dataSnapshot.child("thumb").getValue().toString())
                            .apply(requestOption)
                            .into(profile_image);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        KrigerConstants.user_counterRef.child(referid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("count_posts").exists()){
                    posts_no.setText(dataSnapshot.child("count_posts").getValue().toString());
                }
                if (dataSnapshot.child("count_connections").exists()){
                    connections_no.setText(dataSnapshot.child("count_connections").getValue().toString());
                }
                if (dataSnapshot.child("count_groups").exists()){
                    groups_no.setText(dataSnapshot.child("count_groups").getValue().toString());
                }
                int real = 0,fake = 0;

                if (dataSnapshot.child("count_profileviews").exists()){
                    real = Integer.valueOf(dataSnapshot.child("count_profileviews").getValue().toString());
                }
                if (dataSnapshot.child("count_profileviews_fake").exists()){
                    fake = Integer.valueOf(dataSnapshot.child("count_profileviews_fake").getValue().toString());
                }

                visitors_no.setText(String.valueOf(real + fake));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }
}
