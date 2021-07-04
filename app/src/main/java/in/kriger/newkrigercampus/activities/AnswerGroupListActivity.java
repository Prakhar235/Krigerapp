package in.kriger.newkrigercampus.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.adapters.MentionAdapter;
import in.kriger.newkrigercampus.classes.Answers;
import in.kriger.newkrigercampus.classes.Group_Post;
import in.kriger.newkrigercampus.classes.UserDetails;
import in.kriger.newkrigercampus.database.DatabaseHelper;
import in.kriger.newkrigercampus.extras.Config;
import in.kriger.newkrigercampus.extras.PrefManager;
import in.kriger.newkrigercampus.extras.Processor;
import in.kriger.newkrigercampus.extras.SpecialTextView;
import in.kriger.newkrigercampus.services.KrigerConstants;
import in.kriger.newkrigercampus.services.FireService;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class AnswerGroupListActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener{

    Group_Post post = new Group_Post();

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/");
    ProgressDialog progressDialog;
    private TextView  textView_username, textView_date,textView_like_num,textview_views_num,textView_comment_num,comment_heading,like_heading,textView_pdf;
    private SpecialTextView textView_post;
    private ImageView post_image;
    private RecyclerView recyclerView,recyclerView_likes;
    private LinearLayout send_layout, post_layout;

    private SocialAutoCompleteTextView comment_text;

    private Button imageButton_share,imagebutton_comment,imageButton_like;
    private String question_id,grp_id;
    final DatabaseReference database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/");
    private FirebaseRecyclerAdapter<Answers, AnswerGroupListActivity.answerViewHolder> adapter;
    private FirebaseRecyclerAdapter<String,AnswerGroupListActivity.likesViewHolder> adapter_likes ;
    private ImageView imageView_dp;
    private Button btn_tag;
    private String authorid;
    private TextView textView_bio;
    private ImageButton btn_send;
    private Button button_mention;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private CardView cardViewPdf;
    private LinearLayout layout_pdf;

    private PrefManager prefManager;



    private String key1;

    private DatabaseHelper db;
    private ProgressBar loading_circle;

    MentionAdapter mentionAdapter;


    private ArrayList<String> userlist = new ArrayList<>();


    private int previousLength;
    private boolean backSpace;

    private  List<String> previous_mentions;
    private List<String> after_mentions;

    private Boolean character_lock = false;

    private int number_tag = 1;

    private ArrayList<String> uidlist_post = new ArrayList<>();

    private  Boolean is_mention = false;
    private  Boolean is_hashtag = false;

    private Button button_hashtag;

    private  Boolean emailLock = false;

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerFragment playerFragment;
    private String youtube;
    private LinearLayout youtubelayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_group_list);


        setTitle("Comments");

        //Action Bar Support
        final androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile_bar_gradient));

        prefManager = new PrefManager(getApplicationContext());

        question_id = getIntent().getExtras().getString("post_id");
        grp_id=getIntent().getExtras().getString("grp_id");

        playerFragment =
                (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_player_fragment);
        youtubelayout =  findViewById(R.id.youtube_layout);

        loading_circle = findViewById(R.id.loading);
        db = new DatabaseHelper(getApplicationContext());

        textView_post = findViewById(R.id.textView_post);
        post_image =  findViewById(R.id.post_image);
        textView_username =  findViewById(R.id.textView_username);
        imageButton_share = findViewById(R.id.imagebutton_share);
        imagebutton_comment = findViewById(R.id.imagebutton_comment);
        imageButton_like =  findViewById(R.id.imagebutton_like);
        textView_like_num = findViewById(R.id.textview_like_num);
        textview_views_num = findViewById(R.id.textview_views_num);
        textView_comment_num = findViewById(R.id.textview_comment_num);
        btn_tag = findViewById(R.id.btn_tag);

        cardViewPdf =  findViewById(R.id.card_view_pdf);
        textView_pdf = findViewById(R.id.textView_pdf);
        layout_pdf =  findViewById(R.id.layout_pdf);

        textView_date =  findViewById(R.id.textView_date);
        imageView_dp =  findViewById(R.id.imageButton_dp);
        send_layout =  findViewById(R.id.send_comment);
        post_layout = findViewById(R.id.post_layout);
        comment_text =  findViewById(R.id.comment_text);
        comment_text.setMentionEnabled(true);
        mentionAdapter = new MentionAdapter(AnswerGroupListActivity.this,R.layout.kriger_list);

        comment_text.setMentionAdapter(mentionAdapter);

        comment_text.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserDetails userDetails = (UserDetails) mentionAdapter.getItem(position);

                int temp_start  =  comment_text.getText().toString().lastIndexOf('@');
                int start = comment_text.getText().toString().substring(0,temp_start -  1).lastIndexOf("@");

                StringBuilder builder = new StringBuilder();
                builder.append(comment_text.getText().toString().substring(0,start));

                String username = userDetails.getName().trim().replace(" ","_");
                builder.append("@" + username + " ");
                comment_text.setText(builder);
                comment_text.setSelection(builder.length());

                character_lock = false;
                userlist.add(userDetails.getUid());

                List<String> mentions = comment_text.getMentions();
                int stop = comment_text.getText().toString().lastIndexOf('@');

                if(userDetails.getUid().equals(user.getUid())){
                    comment_text.setText(comment_text.getText().toString().substring(0, stop));
                    comment_text.setSelection(comment_text.length());
                    try {
                        userlist.remove(mentions.size() - 1);

                        Toasty.custom(getApplicationContext(), "You cannot mention yourself.", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_SHORT, false,
                                true).show();
                    } catch (IndexOutOfBoundsException e) {

                    }
                }

                for (int i = 0;i<userlist.size() - 1;i++){
                    if (mentions.get(i).trim().equals(username)){
                        comment_text.setText(comment_text.getText().toString().substring(0,stop) + "@" + username + "_" + number_tag + " ");
                        comment_text.setSelection(comment_text.length());
                        number_tag++;

                    }if (userlist.get(i).equals(userDetails.getUid())){
                        comment_text.setText(comment_text.getText().toString().substring(0,stop));
                        comment_text.setSelection(comment_text.length());
                        try {
                            userlist.remove(mentions.size()-1);

                            Toasty.custom(getApplicationContext(), "You cannot mention same user twice.", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_SHORT, false,
                                    true).show();
                        } catch (IndexOutOfBoundsException e) {

                        }
                    }



                }
                is_mention = false;

                mentionAdapter.clear();
            }
        });

        comment_heading = findViewById(R.id.comment_heading);
        like_heading = findViewById(R.id.like_heading);
        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView_likes =  findViewById(R.id.recycler_view_likes);
        LinearLayoutManager mLayoutManager_likes =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                {
                    //scrolling method
                    @Override
                    public boolean canScrollHorizontally() {
                        return false;
                    }
                };


        recyclerView_likes.setLayoutManager(mLayoutManager_likes);
        recyclerView_likes.setItemAnimator(new DefaultItemAnimator());
        recyclerView_likes.setHasFixedSize(true);



        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable("LIST_STATE_KEY");
            recyclerView_likes.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);

        }

        //comment_text accept max 5000 chars
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(5000);
        comment_text.setFilters(FilterArray);


        //Open UserProfile
        textView_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    Intent intent = new Intent(getApplicationContext(), ProfileListActivity.class);
                    intent.putExtra("user_id", authorid);
                    startActivity(intent);



            }
        });


        //Click on comment button keyboard open
        imagebutton_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comment_text.requestFocus();
                if(comment_text.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(comment_text, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });


        //share post
        imageButton_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                progressDialog = new ProgressDialog(AnswerGroupListActivity.this);
                progressDialog.setMessage("Loading..."); // Setting Message
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                progressDialog.show(); // Display Progress Dialog
                progressDialog.setCancelable(false);
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                }).start();

                String uid = user.getUid();
                String link = "https://kriger.in/?invitedby=" + uid;
                FirebaseDynamicLinks.getInstance().createDynamicLink()
                        .setLink(Uri.parse(link))
                        .setDomainUriPrefix("https://kriger.page.link")
                        .setAndroidParameters(
                                new DynamicLink.AndroidParameters.Builder("in.kriger.krigercampus")
                                        .setMinimumVersion(1)
                                        .build()).setIosParameters(
                        new DynamicLink.IosParameters.Builder("com.example.ios")
                                .setAppStoreId("123456789")
                                .setMinimumVersion("1.0.1")
                                .build())
                        .buildShortDynamicLink()
                        .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                            @Override
                            public void onSuccess(ShortDynamicLink shortDynamicLink) {

                                pushreferral(shortDynamicLink.getShortLink().toString());



                            }
                        });



            }
        });

        textView_date =  findViewById(R.id.textView_date);
        textView_bio = findViewById(R.id.textView_bio);
        imageView_dp =  findViewById(R.id.profile_photo);
        send_layout = findViewById(R.id.send_comment);
        post_layout = findViewById(R.id.post_layout);
        btn_send =  findViewById(R.id.btn_send);

        getquestion();



        //@ mention button implementation
        button_mention = findViewById(R.id.button_mention);
        button_mention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_mention = true;
                comment_text.setText(comment_text.getText().toString() + "@");
                comment_text.setSelection(comment_text.getText().length());


            }

        });

        button_hashtag = findViewById(R.id.button_hashtag);
        button_hashtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_hashtag = true;
                comment_text.setText(comment_text.getText().toString() + "#");
                comment_text.setSelection(comment_text.getText().length());

            }
        });

        //listening every char
        comment_text.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {


                if (count > 0){
                    if (s.charAt(s.length()-1) == '@'){
                        try {
                            if (s.charAt(s.length() - 2) == ' ') {
                                is_mention = true;
                                character_lock = true;
                            }else{
                                emailLock = true;
                            }
                        }catch (IndexOutOfBoundsException e){
                            is_mention = true;
                            character_lock = true;
                        }
                        return;
                    }
                    if (s.charAt(s.length()-1) == '#'){
                        is_hashtag = true;
                        character_lock = true;
                        return;
                    }
                    if (s.charAt(s.length() - 1) == ' '){
                        if (character_lock){
                            character_lock = false;

                        }
                        if (is_hashtag){
                            is_hashtag = false;
                        }
                        if (emailLock){
                            userlist.add("1");
                            emailLock = false;
                        }

                    }
                    if (character_lock){
                        if (is_mention){
                            startSearch(s.toString().substring(s.toString().lastIndexOf("@") + 1));
                        }else if (is_hashtag){
                            startSearch(s.toString().substring(s.toString().lastIndexOf("#") + 1));
                        }

                    }
                }



            }


            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                previousLength = s.length();
                if (count > 0) {
                    if (s.charAt(s.length() - 1) == '@' || s.charAt(s.length() - 1) == '#') {
                        character_lock = false;
                    }
                }

                previous_mentions = comment_text.getMentions();

            }

            public void afterTextChanged(Editable s) {
                backSpace = previousLength > s.length();
                if (backSpace){

                    after_mentions = comment_text.getMentions();

                    if (previous_mentions.size() == after_mentions.size()) {
                        if (!after_mentions.containsAll(previous_mentions)) {

                            for (int i = 0; i < previous_mentions.size(); i++) {
                                try {
                                    if (!(previous_mentions.get(i).equals(after_mentions.get(i)))) {
                                        try {
                                            userlist.remove(i);
                                        } catch (IndexOutOfBoundsException e) {

                                        }
                                        comment_text.setText(comment_text.getText().toString().replace("@" + after_mentions.get(i), ""));
                                        comment_text.setSelection(comment_text.length());
                                        break;

                                    }
                                }catch (IndexOutOfBoundsException e){}
                            }
                        }


                    }
                }



            }
        });




    }

  //Search for user
    public void startSearch(String text){
        key1 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").push().getKey();
        if (is_mention) {


            HashMap<String, Object> map = new HashMap<>();

            map.put("index", "firebase1");
            map.put("type", "user");
            map.put("/body/query/wildcard/name/value", "*" + text.trim().toLowerCase() + "*");


            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                try {
                    imm.showSoftInputFromInputMethod(view.getWindowToken(), 0);
                } catch (NullPointerException e) {

                }
            }


            FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("search").child("request").child(key1).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    listensearch();
                }
            });
        }else if (is_hashtag){

        }


    }

    private void listensearch(){


        FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("search").child("response").child(key1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    if (is_mention) {
                        try {
                            if (dataSnapshot.child("hits").child("total").getValue().toString().equals("0")) {

                            } else {

                                mentionAdapter.clear();
                                for (DataSnapshot childsnapshot : dataSnapshot.child("hits").child("hits").getChildren()) {
                                    if (childsnapshot.child("_source").child("name").exists() && childsnapshot.child("_source").child("headline").exists()) {

                                        try {
                                            UserDetails userDetails = new UserDetails();
                                            userDetails.setName(childsnapshot.child("_source").child("name").getValue().toString());
                                            userDetails.setUid(childsnapshot.child("_id").getValue().toString());
                                            userDetails.setHeadline(childsnapshot.child("_source").child("headline").getValue().toString());
                                            userDetails.setImageurl(childsnapshot.child("_source").child("thumb").getValue().toString());
                                            mentionAdapter.add(userDetails);
                                        } catch (NullPointerException e) {
                                        }

                                    }
                                }
                                mentionAdapter.notifyDataSetChanged();
                            }

                        }catch (NullPointerException e){}
                    }else  if (is_hashtag){

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });



    }




    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        savedState.putParcelable("LIST_STATE_KEY", recyclerView_likes.getLayoutManager().onSaveInstanceState());
    }



    //set Likes
    final static int ITEMS_PER_PAGE = 7;
    public void setlikes() {
        String post_id = getIntent().getExtras().get("post_id").toString();
        String group_id=getIntent().getExtras().get("grp_id").toString();
        Query likesRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("Group_Post_Like").child(group_id).child(post_id);
        likesRef.keepSynced(true);

        FirebaseRecyclerOptions<String> options =
                new FirebaseRecyclerOptions.Builder<String>()
                        .setQuery(likesRef, String.class)
                        .setLifecycleOwner(this)
                        .build();



        //inflate likes layout
        adapter_likes = new FirebaseRecyclerAdapter<String, AnswerGroupListActivity.likesViewHolder>(options) {
            @Override
            public AnswerGroupListActivity.likesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


                int itemWidth = parent.getWidth() / ITEMS_PER_PAGE;
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.likes_layout, parent, false);
                ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
                layoutParams.width = itemWidth;
                itemView.setLayoutParams(layoutParams);
                return new AnswerGroupListActivity.likesViewHolder(itemView);

            }



            @Override
            protected void onBindViewHolder(@NonNull final AnswerGroupListActivity.likesViewHolder holder, final int position, @NonNull String model) {
                String uid = adapter_likes.getRef(position).getKey();

                database.child("User_Detail").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        try {
                            like_heading.setVisibility(View.VISIBLE);
                            if(position<6) {

                                RequestOptions requestOption = new RequestOptions()
                                        .placeholder(R.drawable.default_profile)
                                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


                                Glide.with(getApplicationContext()).load(dataSnapshot.child("thumb").getValue().toString())
                                        .apply(requestOption)
                                        .into(holder.imageView_dp);


                            }
                        } catch (NullPointerException e) {
                            like_heading.setVisibility(View.GONE);
                            holder.imageView_dp.setImageResource(R.drawable.default_profile);


                        }

                        if (position == 6 ){
                            holder.load_button.setVisibility(View.VISIBLE);
                            holder.imageView_dp.setVisibility(View.GONE);

                        }


                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                holder.imageView_dp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), GroupLikeListActivity.class);
                        intent.putExtra("grp_id",grp_id);
                        intent.putExtra("post_id", question_id);
                        startActivity(intent);
                    }
                });

                holder.load_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), GroupLikeListActivity.class);
                        intent.putExtra("grp_id",grp_id);
                        intent.putExtra("post_id", question_id);
                        startActivity(intent);
                    }
                });


            }



            @Override
            public void onDataChanged() {
            }
        };




        recyclerView_likes.setAdapter(adapter_likes);
    }



    //set comments

    public void setanswers() {
        Query answerRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("Group_Post_Comment").child(grp_id).child(question_id);
        answerRef.keepSynced(true);

        FirebaseRecyclerOptions<Answers> options =
                new FirebaseRecyclerOptions.Builder<Answers>()
                        .setQuery(answerRef, Answers.class)
                        .setLifecycleOwner(this)
                        .build();

        //infalte comments text layout
        adapter = new FirebaseRecyclerAdapter<Answers, AnswerGroupListActivity.answerViewHolder>(options) {
            @Override
            public AnswerGroupListActivity.answerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new AnswerGroupListActivity.answerViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.answer_layout, parent, false));
            }



            @Override
            protected void onBindViewHolder(@NonNull final AnswerGroupListActivity.answerViewHolder holder, final int position, @NonNull final Answers model) {
                try {
                    comment_heading.setVisibility(View.VISIBLE);

                    holder.question_comment.setText(model.getString());
                    final ArrayList<String> uidlist = new ArrayList<>();
                    KrigerConstants.group_post_commentRef.child(grp_id).child(question_id).child(adapter.getRef(position).getKey()).child("mention_uid").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){

                                String[] separated = dataSnapshot.getValue().toString().split(",");

                                uidlist.addAll(Arrays.asList(separated));
                                holder.question_comment.setMentionEnabled(true);
                                holder.question_comment.setOnMentionClickListener(new Function2<TextView, String, Unit>() {
                                    @Override
                                    public Unit invoke(TextView textView, String s) {

                                        List<String> mentions = holder.question_comment.getMentions();
                                        for (int i = 0 ;i <mentions.size();i++){
                                            if (mentions.get(i).equals(s)){
                                                if (!uidlist.get(i).equals("1")) {

                                                        Intent intent = new Intent(getApplicationContext(), ProfileListActivity.class);
                                                        intent.putExtra("user_id", uidlist.get(i));
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

                    holder.question_comment.setOnHyperlinkClickListener(new Function2<TextView, String, Unit>() {
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

                    if (holder.question_comment.getHashtags().size() > 0) {
                        holder.question_comment.setOnHashtagClickListener(new Function2<TextView, String, Unit>() {
                            @Override
                            public Unit invoke(TextView textView, String s) {
                                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                                intent.putExtra("search_text", s);
                                startActivity(intent);
                                return null;
                            }
                        });
                    }


                    holder.question_date.setText(Processor.timestamp(model.getTimestamp()));


                    FireService.showdetails(getApplicationContext(),holder.question_username,holder.question_bio, holder.imageView_dp, holder.btn_tag, model.getUid());


                    database.child("Group_Post_Comment_Like").child(grp_id).child(question_id).child(adapter.getRef(position).getKey()).child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                holder.imageButton_like.setBackgroundResource(R.drawable.ic_app_like_r);
                            }else{
                                holder.imageButton_like.setBackgroundResource(R.drawable.ic_app_like);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    database.child("Group_Post_Comment_Counter").child(grp_id).child(question_id).child(adapter.getRef(position).getKey()).child("count_likes").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                holder.textView_like_num.setText(dataSnapshot.getValue().toString());
                            }else{
                                holder.textView_like_num.setText("0");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                } catch (NullPointerException e) {
                    holder.layout_ans.setVisibility(View.GONE);
                    comment_heading.setVisibility(View.GONE);
                }


                //like comment
                holder.comment_like_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        holder.comment_like_layout.setEnabled(false);

                        if (holder.imageButton_like.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.ic_app_like_r).getConstantState())){

                            holder.imageButton_like.setBackgroundResource(R.drawable.ic_app_like);
                            holder.textView_like_num.setText(String.valueOf(Integer.valueOf(holder.textView_like_num.getText().toString()) - 1));

                            mDatabase.child("Group_Post_Comment_Like").child(grp_id).child(question_id).child(adapter.getRef(position).getKey()).child(user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    holder.comment_like_layout.setEnabled(true);
                                }
                            });

                        }else{

                            holder.imageButton_like.setBackgroundResource(R.drawable.ic_app_like_r);
                            holder.textView_like_num.setText(String.valueOf(Integer.valueOf(holder.textView_like_num.getText().toString()) + 1));

                            mDatabase.child("Group_Post_Comment_Like").child(grp_id).child(question_id).child(adapter.getRef(position).getKey()).child(user.getUid()).setValue(FireService.getToday()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    holder.comment_like_layout.setEnabled(true);
                                }
                            });

                        }
                    }
                });



                holder.question_username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            Intent intent = new Intent(getApplicationContext(), ProfileListActivity.class);
                            intent.putExtra("user_id", adapter.getItem(position).getUid());
                            startActivity(intent);



                    }
                });



            }

            @Override
            public void onDataChanged() {
                // If there are no chat messages, show a view that invites the user to add a message.
                //mEmptyListMessage.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        };


        recyclerView.setAdapter(adapter);

    }


    //View of like
    public static class likesViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView_dp;
        public Button load_button;
        public LinearLayout layout_likes;

        public likesViewHolder(View itemView) {
            super(itemView);
            imageView_dp =  itemView.findViewById(R.id.imageButton_dp);
            load_button = itemView.findViewById(R.id.load_button);
            layout_likes = itemView.findViewById(R.id.layout_likes);
        }
    }


    //VIew of a Comment
    public static class answerViewHolder extends RecyclerView.ViewHolder {

        private TextView  question_username, question_bio, question_date, textView_like_num;
        private ImageButton imageButton_like;
        private LinearLayout layout_ans, comment_like_layout;
        private ImageView imageView_dp;

        private SpecialTextView question_comment;
        private Context context;
        private Button btn_tag;

        View itemview;

        public answerViewHolder(View itemView) {
            super(itemView);

            this.itemview = itemView;
            context = itemView.getContext();
            question_comment = itemView.findViewById(R.id.textView_comment);
            question_username =  itemView.findViewById(R.id.textView_username);
            question_bio = itemView.findViewById(R.id.textView_bio);
            question_date = itemView.findViewById(R.id.textView_date);
            textView_like_num = itemView.findViewById(R.id.textview_like_num);
            comment_like_layout = itemView.findViewById(R.id.comment_like_layout);
            imageButton_like = itemView.findViewById(R.id.imagebutton_like);
            layout_ans = itemView.findViewById(R.id.answer_layout);
            imageView_dp = itemView.findViewById(R.id.profile_photo);
            btn_tag = itemView.findViewById(R.id.btn_tag);

        }


    }

    //Get Post
    private void getquestion() {

        final DatabaseReference postRef = database.child("Group_Post").child(grp_id).child(question_id);

        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    setlikes();

                    setanswers();



                    post = dataSnapshot.getValue(Group_Post.class);

                    textView_post.setText(post.getText());
                    if (textView_post.getMentions().size()>0){
                        KrigerConstants.group_postRef.child(grp_id).child(question_id).child("mention_uid").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){

                                    String[] separated = dataSnapshot.getValue().toString().split(",");

                                    uidlist_post.addAll(Arrays.asList(separated));
                                    textView_post.setMentionEnabled(true);
                                    textView_post.setOnMentionClickListener(new Function2<TextView, String, Unit>() {
                                        @Override
                                        public Unit invoke(TextView textView, String s) {

                                            List<String> mentions = textView_post.getMentions();
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

                    View view = post_layout.getChildAt(3);

                    if(view.getId() == R.id.layout_urlpreview){
                        view.setVisibility(View.GONE);
                    }


                    if (textView_post.getHyperlinks().size() >0) {
                        textView_post.setOnHyperlinkClickListener(new Function2<TextView, String, Unit>() {
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

                        if (post.getThumb() == null){

                            if(textView_post.getHyperlinks().get(0).matches(".*(youtube|youtu.be).*")){
                                youtubelayout.setVisibility(View.VISIBLE);

                                String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";

                                Pattern compiledPattern = Pattern.compile(pattern);
                                Matcher matcher = compiledPattern.matcher(textView_post.getHyperlinks().get(0));

                                if(matcher.find()){
                                    youtube =  matcher.group();
                                }

                                playerFragment.initialize(Config.YOUTUBE_API_KEY, AnswerGroupListActivity.this);
                            }else{
                                TextCrawler textCrawler = new TextCrawler();

                                textCrawler.makePreview(new LinkPreviewCallback() {
                                    @Override
                                    public void onPre() {
                                        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                                        View child = inflater.inflate(R.layout.layout_urlpreview, null);
                                        post_layout.addView(child, 3);
                                    }

                                    @Override
                                    public void onPos(SourceContent sourceContent, boolean b) {
                                        try{

                                            View view = post_layout.getChildAt(3);
                                            TextView title = view.findViewById(R.id.sname);
                                            try {
                                                title.setText(sourceContent.getTitle());
                                                TextView description = view.findViewById(R.id.sheadline);
                                                description.setText(sourceContent.getDescription());
                                                if (sourceContent.getImages().size() > 0) {
                                                    ImageView imageView = view.findViewById(R.id.imageButton_dp);
                                                    Glide
                                                            .with(getApplicationContext())
                                                            .load(sourceContent.getImages().get(0))
                                                            .into(imageView);

                                                    view.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            String s = textView_post.getHyperlinks().get(0);
                                                            if (!s.startsWith("http://") && !s.startsWith("https://")) {
                                                                s = "http://" + s;
                                                            }
                                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                                                            startActivity(browserIntent);
                                                        }
                                                    });


                                                } else {
                                                    post_layout.removeViewAt(3);


                                                }
                                                ImageButton imageButton = view.findViewById(R.id.imageButton_cancel);
                                                imageButton.setVisibility(View.GONE);
                                            }catch (NullPointerException e){}
                                        }catch (NullPointerException e){
                                            post_layout.removeViewAt(3);

                                        }

                                    }
                                }, textView_post.getHyperlinks().get(0));

                            }


                        }



                    }


                    try {
                        FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("Group_Post_Counter").child(grp_id).child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                try{
                                    textView_like_num.setText(dataSnapshot.child("count_likes").getValue().toString());
                                }catch (NullPointerException e){
                                    textView_like_num.setText("0");
                                }
                                try{
                                    textView_comment_num.setText(dataSnapshot.child("count_comments").getValue().toString());
                                }catch (NullPointerException e){
                                    textView_comment_num.setText("0");
                                }
                                try{
                                    textview_views_num.setText(dataSnapshot.child("count_views").getValue().toString());
                                }catch (NullPointerException e){
                                    textview_views_num.setText("0");
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }catch (NullPointerException e){}


                    if (post.getThumb() != null) {

                        try {
                            loading_circle.setVisibility(View.VISIBLE);
                            RequestOptions requestOption = new RequestOptions()
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


                            Glide.with(getApplicationContext()).load(post.getThumb())
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .apply(requestOption)
                                    .into(post_image);



                        } catch (NullPointerException e) {

                        }

                    } else {
                        post_image.setVisibility(View.GONE);
                    }

                    //click to see full image
                    if (post.getThumb() != null) {

                        post_image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
                                intent.putExtra("url", post.getThumb());
                                intent.putExtra("posttext",post.getText());
                                intent.putExtra("details",post.getUid());
                                intent.putExtra("date",post.getTimestamp());
                                intent.putExtra("question_id",question_id);
                                startActivity(intent);

                            }
                        });

                    }

                    if(post.getPdf_url()!=null){
                        try{
                            cardViewPdf.setVisibility(View.VISIBLE);
                            layout_pdf.setVisibility(View.VISIBLE);
                            textView_pdf.setVisibility(View.VISIBLE);
                            textView_pdf.setText("Click here to open");

                        }catch (NullPointerException e){

                        }
                    }else {
                        cardViewPdf.setVisibility(View.GONE);
                        layout_pdf.setVisibility(View.GONE);
                        textView_pdf.setVisibility(View.GONE);
                    }

                    cardViewPdf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent();
                            intent.setType(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(post.getPdf_url()));
                            startActivity(intent);
                        }
                    });


                    //Comments
                    btn_send.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            if (comment_text.getText().toString().trim().isEmpty()) {

                                Toasty.custom(getApplicationContext(), R.string.valid_comment, R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_SHORT, false,
                                        true).show();


                            } else {

                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/");
                                String key = mDatabase.child("Group_Post_Comment").child(grp_id).child(question_id).push().getKey();

                                Answers answers = new Answers(comment_text.getText().toString(), user.getUid(),FireService.getToday());
                                mDatabase.child("Group_Post_Comment").child(grp_id).child(question_id).child(key).setValue(answers);
                                textView_comment_num.setText(String.valueOf(Integer.valueOf(textView_comment_num.getText().toString()) + 1));

                                HashMap<String,Object> map = new HashMap<>();

                                if (userlist.size()>0) {
                                    map.put("mention_uid",android.text.TextUtils.join(",", userlist));
                                }
                                List<String> hashtags = comment_text.getHashtags();
                                if (hashtags.size()>0){
                                    map.put("mention_tag",android.text.TextUtils.join(",", hashtags));
                                }

                                if (!map.isEmpty()){
                                    KrigerConstants.group_post_commentRef.child(grp_id).child(question_id).child(key).updateChildren(map);
                                }
                                comment_text.getText().clear();
                            }


                        }

                    });

                    //extended post like
                    FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("Group_Post_Like").child(grp_id).child(dataSnapshot.getKey()).child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){

                                imageButton_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_app_like_r, 0, 0, 0);
                            }else {

                                imageButton_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_app_like, 0, 0, 0);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



                    imageButton_like.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (recyclerView.getVisibility() == View.VISIBLE){
                                    imageButton_like.setEnabled(false);

                                try {

                                    if (db.checkLikeGroupPostValue(question_id).equals("true")) {
                                        db.updateLikeGroupPost(question_id, Group_Post.COLUMN_ISLIKE, "false");
                                        db.updateLikeGroupPost(question_id, Group_Post.COLUMN_COUNTLIKE, String.valueOf(Integer.valueOf(textView_like_num.getText().toString()) - 1));

                                        imageButton_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_app_like, 0, 0, 0);
                                        textView_like_num.setText(String.valueOf(Integer.valueOf(textView_like_num.getText().toString()) - 1));

                                        FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("Group_Post_Like").child(grp_id).child(question_id).child(user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                imageButton_like.setEnabled(true);
                                            }
                                        });


                                    } else {

                                        db.updateLikeGroupPost(question_id, Group_Post.COLUMN_ISLIKE, "true");
                                        db.updateLikeGroupPost(question_id, Group_Post.COLUMN_COUNTLIKE, String.valueOf(Integer.valueOf(textView_like_num.getText().toString()) + 1));

                                        imageButton_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_app_like_r, 0, 0, 0);
                                        textView_like_num.setText(String.valueOf(Integer.valueOf(textView_like_num.getText().toString()) + 1));


                                        FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("Group_Post_Like").child(grp_id).child(question_id).child(user.getUid()).setValue(FireService.getToday()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                imageButton_like.setEnabled(true);
                                            }
                                        });
                                    }
                                } catch (NullPointerException e) {

                                }
                        }



                        }
                    });

                    textView_date.setText(Processor.timestamp(post.getTimestamp()));
                    authorid = post.getUid();


                    FireService.showdetails(getApplicationContext(),textView_username,textView_bio, imageView_dp, btn_tag, post.getUid());


                } else {

                    Toasty.custom(getApplicationContext(), R.string.post_not_exist, R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_SHORT, false,
                            true).show();

                    recyclerView.setVisibility(View.GONE);

                    send_layout.setVisibility(View.GONE);
                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }





        });


    }


   //sharing Post
    public void pushreferral(final String shortLink){

        View inflatedFrame = getLayoutInflater().inflate(R.layout.layout_share, null);

        RelativeLayout relativeLayout =  inflatedFrame.findViewById(R.id.relativelayout_share);


        ImageView imageView =  inflatedFrame.findViewById(R.id.imageview_post);
        ImageView share_bottom =  inflatedFrame.findViewById(R.id.bottom_share);
        ImageView share_top = inflatedFrame.findViewById(R.id.top_bar);
        TextView watermark = inflatedFrame.findViewById(R.id.watermark);
        watermark.setText("Shared by : " + user.getDisplayName());

        Bitmap bit = Bitmap.createBitmap(post_layout.getWidth(), post_layout.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(bit);
        post_layout.draw(c);

        imageView.setImageBitmap(bit);


        share_bottom.getLayoutParams().width = bit.getWidth();
        share_top.getLayoutParams().width = bit.getWidth();
        watermark.getLayoutParams().width = bit.getWidth();

        relativeLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        Bitmap b = Bitmap.createBitmap(relativeLayout.getMeasuredWidth(),relativeLayout.getMeasuredHeight(),Bitmap.Config.RGB_565);
        Canvas c1 = new Canvas(b);
        relativeLayout.layout(0, 0, relativeLayout.getMeasuredWidth(), relativeLayout.getMeasuredHeight());
        relativeLayout.draw(c1);


        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        try {

            File cachePath = new File(getApplicationContext().getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            b.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        String text = post.getText().length() > 150 ? post.getText().substring(0,150) : post.getText().toString();


        if (prefManager.getAccountType() == 0){
            text = "leraner\n"+text +"..."+"\nRead more : " +shortLink +
                    "\n" +
                    "Sign up to\n" +
                    "Connect with Students, Teachers, Colleges, Coaching \n" +
                    "Purchase Study material, Classes, Practice papers\n" +
                    "Make or join pan INDIA study groups";
        }else if (prefManager.getAccountType() == 1){
            text = "educator\n"+text +"..."+"\nRead more : " +shortLink +
                    "\n" +
                    "Sign up to\n" +
                    "Connect with Students, Teachers, Colleges, Coaching \n" +
                    "Purchase Study material, Classes, Practice papers\n" +
                    "Make or join pan INDIA study groups";
        }else{
            text = "corporate\n"+text +"..."+"\nRead more : " +shortLink +
                    "\n" +
                    "Sign up to\n" +
                    "Connect with Students, Teachers, Colleges, Coaching \n" +
                    "Purchase Study material, Classes, Practice papers\n" +
                    "Make or join pan INDIA study groups";
        }


            File imagePath = new File(getApplicationContext().getCacheDir(), "images");
            File newFile = new File(imagePath, "image.png");
            Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), "in.kriger.krigercampus.fileprovider", newFile);
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.setType("image/jpeg");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try {
                startActivity(shareIntent);
            } catch (android.content.ActivityNotFoundException ex) {

            }



    }


    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    public void onBackPressed() {

        try{
            Log.d("Intent",getIntent().getExtras().getString("source"));
            Intent intent1 = new Intent(getApplicationContext(),HomeActivity.class);
            startActivity(intent1);
        }catch (NullPointerException e){
            super.onBackPressed();
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            player.cueVideo(youtube); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = String.format(getString(R.string.player_error), errorReason.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }





}