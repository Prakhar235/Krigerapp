package in.kriger.newkrigercampus.activities;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Query;


import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.services.KrigerConstants;
import in.kriger.newkrigercampus.services.FireService;

public class LikeListActivity extends AppCompatActivity {

    private FirebaseRecyclerAdapter<String,likesViewHolder> adapter_likes ;
    private RecyclerView recyclerView;

    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_list);

        //Title of Activity
        setTitle("Likes");

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();


        String post_id = getIntent().getExtras().get("post_id").toString();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile_bar_gradient));
        actionBar.setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        recyclerView =  findViewById(R.id.recycler_view);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(getApplicationContext());


        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        //Set Recyclerview
        Query likesRef = KrigerConstants.post_likeRef.child(post_id);
        likesRef.keepSynced(true);

        FirebaseRecyclerOptions<String> options =
                new FirebaseRecyclerOptions.Builder<String>()
                        .setQuery(likesRef, String.class)
                        .setLifecycleOwner(this)
                        .build();

        adapter_likes = new FirebaseRecyclerAdapter<String, likesViewHolder>(options) {
            @Override
            public likesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new likesViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.kriger_list, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final likesViewHolder holder, final int position, @NonNull String model) {
                String uid = adapter_likes.getRef(position).getKey();

                FireService.showdetails(getApplicationContext(),holder.sname,holder.sheadline,holder.imageView_dp,holder.btn_tag,adapter_likes.getRef(position).getKey());


                holder.sname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                            Intent intent = new Intent(getApplicationContext(), ProfileListActivity.class);
                            intent.putExtra("user_id",adapter_likes.getRef(position).getKey());
                            startActivity(intent);




                    }
                });




            }

            @Override
            public void onDataChanged() {
            }
        };


        recyclerView.setAdapter(adapter_likes);


    }

    public static class likesViewHolder extends RecyclerView.ViewHolder{

        private TextView sname;
        private TextView sheadline;
        private ImageView imageView_dp;
        private Button btn_tag;


        public likesViewHolder(View itemView) {
            super(itemView);

            sname =  itemView.findViewById(R.id.sname);
            sheadline =  itemView.findViewById(R.id.sheadline);
            imageView_dp =  itemView.findViewById(R.id.imageButton_dp);
            btn_tag = itemView.findViewById(R.id.btn_tag);

        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),AnswerListActivity.class);
        intent.putExtra("post_id", getIntent().getExtras().get("post_id").toString());
        startActivity(intent);
        super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(),AnswerListActivity.class);
                intent.putExtra("post_id", getIntent().getExtras().get("post_id").toString());
                startActivity(intent);
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
