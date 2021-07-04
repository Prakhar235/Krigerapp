package in.kriger.newkrigercampus.groupactivities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.activities.HomeActivity;
import in.kriger.newkrigercampus.activities.NewGroupPostActivity;
import in.kriger.newkrigercampus.adapters.Adapter_Group_Post;
import in.kriger.newkrigercampus.classes.Group_Post;
import in.kriger.newkrigercampus.database.DatabaseHelper;
import in.kriger.newkrigercampus.services.KrigerConstants;
import in.kriger.newkrigercampus.utils.PaginationScrollListener;


import org.json.JSONException;

public class ShowGroup extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar loading_circle;

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/");



    SwipeRefreshLayout mSwipeRefreshLayout;




    FloatingActionButton add_post;

    private DatabaseHelper db;

    public List<Group_Post> postlist = new ArrayList<>();
    public Adapter_Group_Post adapter_post;


    private boolean isLoading = false;

    String grp_id;
    TextView no_posts;



    LinearLayout grp_layout;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_group);
        loading_circle = findViewById(R.id.loading);



        grp_layout = findViewById(R.id.group_layout);
        grp_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),GroupAbout.class);
                intent.putExtra("grp_id",grp_id);
                intent.putExtra("invite",getIntent().getExtras().getBoolean("invite") );
                startActivity(intent);
            }
        });


        no_posts=findViewById(R.id.no_post_text);

        recyclerView =findViewById(R.id.recycler_view);
        recyclerView.setNestedScrollingEnabled(false);




        mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mSwipeRefreshLayout.setRefreshing(false);
                db.deleteAllGroupPosts(grp_id);
                checkDatabase();

            }
        });




        grp_id=getIntent().getExtras().getString("grp_id");


        final LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        final TextView group_name=findViewById(R.id.group_name);
        final ImageView imageView_icon = findViewById(R.id.action_bar_image);

        final Button invite_icon = findViewById(R.id.imageview_invite);
        invite_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ShowGroup.this,GroupInvitesMembers.class);
                intent.putExtra("grp_id",grp_id);
                startActivity(intent);
            }
        });

        try{
            if (getIntent().getExtras().getBoolean("invite")){
                invite_icon.setVisibility(View.VISIBLE);

            }
        }catch (NullPointerException e){}

        try{
            mDatabase.child("Group_Name").child(grp_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        group_name.setText(dataSnapshot.child("name").getValue().toString());
                        setTitle("");
                        try{

                            RequestOptions requestOption = new RequestOptions()
                                    .placeholder(R.drawable.default_profile)
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


                            Glide.with(getApplicationContext()).load(dataSnapshot.child("thumb").getValue().toString())
                                    .apply(requestOption)
                                    .into(imageView_icon);



                        }catch (NullPointerException e) {
                            imageView_icon.setImageResource(R.drawable.default_groups);
                        }

                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }catch (NullPointerException e){

        }




        db = new DatabaseHelper(getApplicationContext());
        adapter_post = new Adapter_Group_Post(ShowGroup.this,postlist,db);

        recyclerView.setAdapter(adapter_post);


        recyclerView.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                generateQuery(null,String.valueOf(Long.valueOf(db.getFirstGroupPostTimestamp(grp_id))-1));


            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
        // add a new post
        add_post = findViewById(R.id.menu_item2_post);
        add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), NewGroupPostActivity.class);
                intent.putExtra("grp_id",grp_id);
                startActivity(intent);

            }
        });




        try {
            mDatabase.child("Group_Post").child(grp_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        checkDatabase();


                    } else {
                        loading_circle.setVisibility(View.GONE);
                        no_posts.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }catch (Exception e){e.printStackTrace();}

        }



    public void checkDatabase() {


        if (db.getGroupPostCount(grp_id) == 0){
            generateQuery();

        }else{
            setAdapter();
            generateQuery(String.valueOf(Long.valueOf(db.getLastGroupPostTimestamp(grp_id)) +1),null);


        }
    }


    private void generateQuery(String startText,String endText) {

        Query postQuery;

        if (endText == null) {
            if (Long.valueOf(startText.substring(6, 7)) > 3) {
                startText = startText.substring(0, 6) + "2" + startText.substring(7);
            }
            postQuery = KrigerConstants.group_postRef.child(grp_id).orderByChild("timestamp").startAt(startText);
        } else {
            if (Long.valueOf(endText.substring(6, 7)) > 3) {
                endText = endText.substring(0, 6) + "2" + endText.substring(7);
            }
            postQuery = KrigerConstants.group_postRef.child(grp_id).orderByChild("timestamp").endAt(endText).limitToLast(20);
        }

        getItems(postQuery);
        Log.i("count_posts",String.valueOf(db.getGroupPostCount(grp_id)));

    }

    private void generateQuery(){
        getItems(KrigerConstants.group_postRef.child(grp_id).limitToLast(20));
    }



    private void setAdapter(){
        postlist.clear();
        postlist.addAll(db.getAllGroupPosts(grp_id));
        adapter_post.notifyDataSetChanged();
        Log.i("count_post",String.valueOf(db.getGroupPostCount(grp_id)));

        isLoading = false;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
    @Override
    public void onBackPressed() {

        try{
            Log.d("Intent",getIntent().getExtras().getString("source"));
            Intent intent1 = new Intent(getApplicationContext(),HomeActivity.class);
            startActivity(intent1);
        }catch (NullPointerException e){
            Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
            intent.putExtra("screen","groups");
            intent.putExtra("grp_id",grp_id);
            startActivity(intent);

        }
    }




    private void getItems(Query postQuery){

        postQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                List<Group_Post> posts = new ArrayList<>();
                for (DataSnapshot childsnapshot : dataSnapshot.getChildren()) {
                    Group_Post post = childsnapshot.getValue(Group_Post.class);
                    post.setDocument_id(childsnapshot.getKey());
                    post.setGrp_id(grp_id);
                    posts.add(post);


                }

                try {
                    db.insertAllGroupPost(posts);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setAdapter();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }






}
