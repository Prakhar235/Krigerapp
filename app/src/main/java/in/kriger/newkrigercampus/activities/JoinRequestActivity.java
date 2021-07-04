package in.kriger.newkrigercampus.activities;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.flowlayout.FlowLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.classes.Group_Post;
import in.kriger.newkrigercampus.classes.UserIdString;
import in.kriger.newkrigercampus.database.DatabaseHelper;
import in.kriger.newkrigercampus.extras.Processor;
import in.kriger.newkrigercampus.services.KrigerConstants;
import in.kriger.newkrigercampus.services.FireService;

public class JoinRequestActivity extends AppCompatActivity {
    private ImageView grp_image;
    private TextView grp_name;
    private TextView date_creation;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private TextView grp_about_text;
    private TextView admin_count;

    private TextView members_count, posts_count,noposts,lposts;
    private RecyclerView recyclerView,admin_list;
    private FirebaseRecyclerAdapter<Group_Post, connectionViewHolder> adapter;
    private FirebaseRecyclerAdapter<UserIdString, listViewHolder> adapter_admin;


    String grp_id, post_id;
    private Button send_request, request_sent;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/");
    DatabaseHelper db ;

    private FlowLayout flow_subs;
    private FlowLayout flow_exams;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_join_request);
        setTitle("Join Group");
        //Action Bar Support
        final androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile_bar_gradient));

        final String[] exams = getResources().getStringArray(R.array.list_exams);
        final String[] subs = getResources().getStringArray(R.array.list_sub);

        grp_image =  findViewById(R.id.group_image);
        grp_name =  findViewById(R.id.group_name);
        date_creation =  findViewById(R.id.date_creation);
        posts_count =  findViewById(R.id.no_posts);
        grp_about_text =  findViewById(R.id.group_about_text);
        members_count =  findViewById(R.id.no_members);
        admin_count=findViewById(R.id.admin_count);
        noposts= findViewById(R.id.noposts);
        lposts=findViewById(R.id.lposts);
        flow_exams = findViewById(R.id.flow_exams);
        flow_subs = findViewById(R.id.flow_sub);
        grp_id = getIntent().getExtras().getString("grp_id");
        post_id = getIntent().getExtras().getString("post_id");
        db = new DatabaseHelper(getApplicationContext());




        recyclerView =  findViewById(R.id.recycler_view_sample_post);
        admin_list=findViewById(R.id.admin_list);
        recyclerView.setNestedScrollingEnabled(false);
        admin_list.setNestedScrollingEnabled(false);



        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(JoinRequestActivity.this);


        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        LinearLayoutManager mLayoutManager1 =
                new LinearLayoutManager(JoinRequestActivity.this);


        mLayoutManager1.setReverseLayout(true);
        mLayoutManager1.setStackFromEnd(true);
        admin_list.setLayoutManager(mLayoutManager1);
        admin_list.setItemAnimator(new DefaultItemAnimator());


        send_request =  findViewById(R.id.send_join_req);
        request_sent =  findViewById(R.id.request_sent);

        reference.child("Group_Data").child(grp_id).child("admins").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int count = (int) dataSnapshot.getChildrenCount();
                    admin_count.setText(String.valueOf(count) + " Admins");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.child("Group_Name").child(grp_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    grp_name.setText(dataSnapshot.child("name").getValue().toString());
                    grp_about_text.setText(dataSnapshot.child("about").getValue().toString());

                    flow_exams.removeAllViews();
                    flow_subs.removeAllViews();


                    if (dataSnapshot.child("exams").exists()) {

                        for (DataSnapshot childsnapshot : dataSnapshot.child("exams").getChildren()) {
                            if (childsnapshot.exists()) {

                                try {

                                    LinearLayout layout1 = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_flow_item, flow_exams, false);
                                    TextView textView = layout1.findViewById(R.id.name);
                                    Integer value = Integer.valueOf(childsnapshot.getValue().toString());
                                    textView.setText(exams[value]);
                                    ImageView imageView = layout1.findViewById(R.id.buttonExitIcon);
                                    imageView.setId(value);
                                    imageView.setVisibility(View.GONE);
                                    flow_exams.addView(layout1);


                                } catch (NullPointerException e) {

                                }
                            }

                        }
                    }

                    if (dataSnapshot.child("subjects").exists()) {

                        for (DataSnapshot childsnapshot : dataSnapshot.child("subjects").getChildren()) {
                            if (childsnapshot.exists()) {

                                try {

                                    LinearLayout layout1 = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_flow_item, flow_subs, false);
                                    TextView textView = layout1.findViewById(R.id.name);
                                    Integer value = Integer.valueOf(childsnapshot.getValue().toString());
                                    textView.setText(subs[value]);
                                    ImageView imageView = layout1.findViewById(R.id.buttonExitIcon);
                                    imageView.setId(value);
                                    imageView.setVisibility(View.GONE);
                                    flow_subs.addView(layout1);


                                } catch (NullPointerException e) {

                                }
                            }

                        }
                    }



                    date_creation.setText(Processor.timestamp(dataSnapshot.child("timestamp").getValue().toString()) + ", " + dataSnapshot.child("timestamp").getValue().toString().substring(0, 4));

                    try {
                        RequestOptions requestOption = new RequestOptions()
                                .placeholder(R.drawable.default_profile)
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


                        Glide.with(getApplicationContext()).load(dataSnapshot.child("thumb").getValue().toString())
                                .apply(requestOption)
                                .into(grp_image);


                    } catch (NullPointerException e) {
                        grp_image.setImageResource(R.drawable.default_groups);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

         //Join Group and Cancel join group request
        reference.child("Group_Data").child(grp_id).child("invites").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    send_request.setVisibility(View.GONE);
                    request_sent.setVisibility(View.VISIBLE);
                    request_sent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            HashMap<String, Object> map = new HashMap<>();
                            map.put("timestamp", ServerValue.TIMESTAMP);
                            reference.child("User_List").child(user.getUid()).child("sent_group_invites").child(grp_id).removeValue();
                            KrigerConstants.group_suggestionRef.child(user.getUid()).child(grp_id).updateChildren(map);
                            reference.child("Group_Data").child(grp_id).child("invites").child(user.getUid()).removeValue();
                            send_request.setVisibility(View.VISIBLE);
                            request_sent.setVisibility(View.GONE);

                        }
                    });

                }else{
                    send_request.setVisibility(View.VISIBLE);
                    request_sent.setVisibility(View.GONE);
                    send_request.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            HashMap<String, Object> map = new HashMap<>();
                            SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
                            final String format = s.format(new Date());
                            map.put("timestamp", format);
                            reference.child("User_List").child(user.getUid()).child("sent_group_invites").child(grp_id).updateChildren(map);
                            KrigerConstants.group_suggestionRef.child(user.getUid()).child(grp_id).removeValue();
                            reference.child("Group_Data").child(grp_id).child("invites").child(user.getUid()).updateChildren(map);
                            send_request.setVisibility(View.GONE);
                            request_sent.setVisibility(View.VISIBLE);

                        }
                    });
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        reference.child("Group_Data").child(grp_id).child("members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int count = (int) dataSnapshot.getChildrenCount();
                    members_count.setText(String.valueOf(count) + " Members");
                } else {
                    members_count.setText("No members yet!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        reference.child("Group_Post").child(grp_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int count = (int) dataSnapshot.getChildrenCount();
                    posts_count.setText(String.valueOf(count) + " Posts");
                    Log.i("count",String.valueOf(count));


                } else {
                    posts_count.setText("0 Posts");
                    recyclerView.setVisibility(View.GONE);
                    lposts.setVisibility(View.GONE);
                    noposts.setVisibility(View.VISIBLE);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        Query connectionnRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("Group_Data").child(grp_id).child("admins").limitToLast(10);

        connectionnRef.keepSynced(true);

        FirebaseRecyclerOptions<UserIdString> options =
                new FirebaseRecyclerOptions.Builder<UserIdString>()
                        .setQuery(connectionnRef, UserIdString.class)
                        .setLifecycleOwner(this)
                        .build();

        adapter_admin=new FirebaseRecyclerAdapter<UserIdString, listViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull listViewHolder holder, int position, @NonNull UserIdString model) {

                FireService.showdetails(getApplicationContext(), holder.sname, holder.sheadline, holder.imageView_dp, holder.btn_tag, adapter_admin.getRef(position).getKey());
                holder.sname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toasty.custom(getApplicationContext(), "Please join the group", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                true).show();
                    }
                });

            }

            @NonNull
            @Override
            public listViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new listViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.group_list,parent,false));
            }


        };
        admin_list.setAdapter(adapter_admin);


        Query postRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("Group_Post").child(grp_id).orderByChild("timestamp").limitToLast(5);

        postRef.keepSynced(true);

        FirebaseRecyclerOptions<Group_Post> options1 =
                new FirebaseRecyclerOptions.Builder<Group_Post>()
                        .setQuery(postRef, Group_Post.class)
                        .setLifecycleOwner(this)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Group_Post, connectionViewHolder>(options1) {
            @Override
            public connectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new connectionViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.question_layout, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final connectionViewHolder holder, final int position, @NonNull final Group_Post model) {


                Log.i("hi", user.getUid());

                holder.question_username.setText(model.getAuthor());
                holder.question_post.setText(model.getText());


                try {
                    if (model.getText().length() > 150) {
                        String text = model.getText().substring(0, 150) + " ... ";
                        String see_more = getApplicationContext().getResources().getString(R.string.see_more);
                        holder.question_post.setText(Html.fromHtml(text + see_more));
                    }

                } catch (NullPointerException e) {

                }



//
                FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("Group_Post_Counter").child(grp_id).child(adapter.getRef(position).getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try{
                            holder.textView_like_num.setText(dataSnapshot.child("count_likes").getValue().toString());
                        }catch (NullPointerException e){
                            holder.textView_like_num.setText("0");
                        }
                        try{
                            holder.textView_comment_num.setText(dataSnapshot.child("count_comments").getValue().toString());
                        }catch (NullPointerException e){
                            holder.textView_comment_num.setText("0");
                        }
                        try{
                            holder.textView_views_num.setText(dataSnapshot.child("count_views").getValue().toString());
                        }catch (NullPointerException e){
                            holder.textView_views_num.setText("0");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });






                //display date and month for current year
                //display date month and year for previous year

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                if (model.getTimestamp().contains(String.valueOf(year))) {


                    holder.question_date.setText(Processor.timestamp(model.getTimestamp()));


                } else {
                    holder.question_date.setText(Processor.timestamp(model.getTimestamp()) + ", " + model.getTimestamp().substring(0, 4));

                }



                if (model.getOriginal() != null) {
                    holder.image_post.setVisibility(View.VISIBLE);
                    holder.loading_circle.setVisibility(View.VISIBLE);
                    RequestOptions requestOption = new RequestOptions()

                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


                    Glide.with(getApplicationContext()).load(model.getOriginal())

                            .transition(DrawableTransitionOptions.withCrossFade())
                            .apply(requestOption)
                            .into(holder.image_post);



                } else {
                    holder.image_post.setImageDrawable(null);
                }


                if(model.getPdf_url()!=null){
                    try{
                        holder.cardViewPdf.setVisibility(View.VISIBLE);
                        holder.layout_pdf.setVisibility(View.VISIBLE);
                        holder.textView_pdf.setVisibility(View.VISIBLE);
                        holder.textView_pdf.setText("Click here to open");

                    }catch (NullPointerException e){

                    }
                }else {
                    holder.cardViewPdf.setVisibility(View.GONE);
                    holder.layout_pdf.setVisibility(View.GONE);
                    holder.textView_pdf.setVisibility(View.GONE);
                }



                FireService.showdetails(getApplicationContext(), holder.question_username,holder.bio, holder.imageView_dp, holder.btn_tag, model.getUid());


                holder.question_username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toasty.custom(getApplicationContext(), "Please join the group", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                true).show();
                    }
                });

                holder.question_post.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toasty.custom(getApplicationContext(), "Please join the group", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                true).show();

                    }
                });


                if (model.getOriginal() != null) {

                    holder.image_post.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toasty.custom(getApplicationContext(), "Please join the group", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                    true).show();
                        }
                    });

                }

                if (model.getPdf_url() != null) {

                    holder.cardViewPdf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toasty.custom(getApplicationContext(), "Please join the group", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                    true).show();
                        }
                    });

                }

                holder.imageButton_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toasty.custom(getApplicationContext(), "Please join the group", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                true).show();

                    }
                });

                holder.imageButton_like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toasty.custom(getApplicationContext(), "Please join the group", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                true).show();
                    }
                });

                holder.imageButton_share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toasty.custom(getApplicationContext(), "Please join the group", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                true).show();
                    }
                });

                holder.show_popup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toasty.custom(getApplicationContext(), "Please join the group", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                true).show();
                    }
                });

                holder.button_connect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toasty.custom(getApplicationContext(), "Please join the group", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                true).show();
                    }
                });

            }


        };

        recyclerView.setAdapter(adapter);





    }


    public class connectionViewHolder extends RecyclerView.ViewHolder {


        private TextView question_post, question_username, question_date,  textView_like_num, textView_comment_num,textView_views_num, textView_section,textView_pdf;
        private Button imageButton_like, imageButton_comment, imageButton_share;
        private LinearLayout layout_pdf;
        private ImageView imageView_dp, image_post;

        private ImageButton show_popup;
        private CardView cardViewPdf;


        private Button btn_tag;


        private TextView bio;
        private ImageView button_connect;
        TextView show_likes;
        private Context context;

        View itemview;
        private ProgressBar loading_circle;


        public connectionViewHolder(final View itemView) {
            super(itemView);

            this.itemview = itemView;
            context = itemView.getContext();
            question_post =  itemView.findViewById(R.id.textView_post);
            question_username =  itemView.findViewById(R.id.textView_username);
            question_date =  itemView.findViewById(R.id.textView_date);
            imageButton_like =  itemView.findViewById(R.id.imagebutton_like);
            imageButton_comment =  itemView.findViewById(R.id.imagebutton_comment);
            imageButton_share =  itemView.findViewById(R.id.imagebutton_share);
            textView_like_num =  itemView.findViewById(R.id.textview_like_num);
            textView_comment_num =  itemView.findViewById(R.id.textview_comment_num);
            textView_views_num =  itemView.findViewById(R.id.textview_views_num);
            show_popup =  itemView.findViewById(R.id.imgbutton_popup);
            image_post =  itemView.findViewById(R.id.image_post);


            cardViewPdf =  itemView.findViewById(R.id.card_view_pdf);
            textView_pdf =  itemView.findViewById(R.id.textView_pdf);
            layout_pdf =  itemView.findViewById(R.id.layout_pdf);

            bio = itemView.findViewById(R.id.textView_bio);
            show_likes =  itemView.findViewById(R.id.show_likes);


            button_connect =  itemView.findViewById(R.id.button_connect);

            imageView_dp =  itemView.findViewById(R.id.profile_photo);

            loading_circle =  itemView.findViewById(R.id.loading);
            btn_tag = itemView.findViewById(R.id.btn_tag);

        }


    }


    public class listViewHolder extends RecyclerView.ViewHolder{


        private TextView sname;
        private TextView sheadline;
        private ImageView imageView_dp,imageView_star;
        private Button btn_tag;

        public listViewHolder(View itemView)
        {
            super(itemView);
            sname =  itemView.findViewById(R.id.sname);
            sheadline =  itemView.findViewById(R.id.sheadline);
            imageView_dp =  itemView.findViewById(R.id.imageButton_dp);
            imageView_star =  itemView.findViewById(R.id.imageview_star);
            btn_tag = itemView.findViewById(R.id.btn_tag);
        }
    }


    @Override
    public void onBackPressed() {


        try{
            Log.d("Intent",getIntent().getExtras().getString("source"));
            Intent intent1 = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent1);
        }catch (NullPointerException e){
            super.onBackPressed();
        }

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


