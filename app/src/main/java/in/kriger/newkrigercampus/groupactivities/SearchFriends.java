package in.kriger.newkrigercampus.groupactivities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.activities.HomeActivity;
import in.kriger.newkrigercampus.adapters.Adapter_SearchFriends;
import in.kriger.newkrigercampus.classes.Group;
import in.kriger.newkrigercampus.classes.UserDetails;
import in.kriger.newkrigercampus.classes.UserIdString;
import in.kriger.newkrigercampus.database.DatabaseHelper;
import in.kriger.newkrigercampus.extras.PrefManager;
import in.kriger.newkrigercampus.services.KrigerConstants;
import in.kriger.newkrigercampus.services.FireService;

public class SearchFriends extends AppCompatActivity {

    ArrayList<UserDetails> list_name;
    RecyclerView recyclerView_selected;
    Button invite;

    DatabaseReference mainRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/");

    Adapter_SearchFriends adapter_searchFriends;
    List<UserDetails> showlist ;
    ProgressDialog progressDialog;

    TextView selected;

    String grp_id=null;
    Group g;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private FirebaseRecyclerAdapter<UserIdString,connectionViewHolder> adapter_connections ;


    RecyclerView recyclerView_suggested;
    LinearLayout layout_group_refer;

    Dialog progress;

    ArrayList<String> preuserlist = new ArrayList<>();

    DatabaseHelper databaseHelper;


    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);

        g = (Group) getIntent().getSerializableExtra("group");
        grp_id = getIntent().getExtras().getString("grp_id");

        setTitle("Add Members");
        prefManager = new PrefManager(getApplicationContext());
        databaseHelper = new DatabaseHelper(getApplicationContext());

        //Action Bar Support
        final androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile_bar_gradient));

        try{
            if (grp_id != null) {
                KrigerConstants.group_nameRef.child(grp_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            setTitle(dataSnapshot.child("name").getValue().toString() + " : Add Members");

                            KrigerConstants.group_dataRef.child(grp_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot child1 : dataSnapshot.child("owner").getChildren()) {
                                        preuserlist.add(child1.getKey());
                                    }
                                    for (DataSnapshot child2 : dataSnapshot.child("admins").getChildren()) {
                                        preuserlist.add(child2.getKey());
                                    }
                                    for (DataSnapshot child3 : dataSnapshot.child("members").getChildren()) {
                                        preuserlist.add(child3.getKey());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }


        }catch (NullPointerException e){

        }


        selected = findViewById(R.id.selected);
        invite=findViewById(R.id.btn_invite1);
        layout_group_refer=findViewById(R.id.layout_group_refer);

        //send invitation
        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String checkedMark = "\u2713";
                dialogBuilder("Benefits of inviting friends.\n" +
                        "\n" +
                        "Increase your \n" +
                        checkedMark + "Profile Rank\n" +
                        checkedMark + "Post Views \n" +
                        checkedMark + "Connection Requests\n",1,false,"OK",null,null);

            }
        });


        list_name  = new ArrayList<>();

        recyclerView_selected =  findViewById(R.id.choosen_list);
        recyclerView_selected.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView_selected.setItemAnimator(new DefaultItemAnimator());

        showlist = new ArrayList<>();
        adapter_searchFriends = new Adapter_SearchFriends(showlist,this,selected);
        recyclerView_selected.setAdapter(adapter_searchFriends);



        recyclerView_suggested = findViewById(R.id.suggested_list);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(getApplicationContext());


        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView_suggested.setLayoutManager(mLayoutManager);
        recyclerView_suggested.setItemAnimator(new DefaultItemAnimator());



        Query connectionnRef = KrigerConstants.connectionRef.child(user.getUid());

        connectionnRef.keepSynced(true);

        FirebaseRecyclerOptions<UserIdString> options =
                new FirebaseRecyclerOptions.Builder<UserIdString>()
                        .setQuery(connectionnRef, UserIdString.class)
                        .setLifecycleOwner(this)
                        .build();

        adapter_connections = new FirebaseRecyclerAdapter<UserIdString, connectionViewHolder>(options) {
            @Override
            public connectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new connectionViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.kriger_list, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull connectionViewHolder holder,final int position, @NonNull UserIdString model) {
                FireService.showdetails(getApplicationContext(),holder.sname,holder.sheadline,holder.imageView_dp,holder.btn_tag,adapter_connections.getRef(position).getKey());
                holder.line.setVisibility(View.VISIBLE);

                holder.list_kriger.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Boolean add = true;


                        for (int i = 0;i<showlist.size();i++){
                            if (showlist.get(i).getUid().equals(adapter_connections.getRef(position).getKey())){
                                add = false;
                                Toasty.custom(getApplicationContext(),"Member already added", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                        true).show();
                            }else if (preuserlist.contains(showlist.get(i).getUid())){
                                add = false;
                                Toasty.custom(getApplicationContext(),"Member already added", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                        true).show();
                            }
                        }

                        if (add) {
                            FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("User_Detail").child(adapter_connections.getRef(position).getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.exists()) {

                                        UserDetails userDetails = new UserDetails();
                                        userDetails.setUid(adapter_connections.getRef(position).getKey());
                                        if (dataSnapshot.child("thumb").exists()) {
                                            userDetails.setImageurl(dataSnapshot.child("thumb").getValue().toString());
                                        }
                                        userDetails.setSelected(true);
                                        userDetails.setName(dataSnapshot.child("name").getValue().toString());
                                        showlist.add(userDetails);
                                        adapter_searchFriends.notifyDataSetChanged();
                                        updateselected();


                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void onDataChanged() {
                // If there are no chat messages, show a view that invites the user to add a message.
                //mEmptyListMessage.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        };


        recyclerView_suggested.setAdapter(adapter_connections);




    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done_group:

                if (showlist.size() == 0){
                    Toasty.custom(getApplicationContext(),"No one to add", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                }else {
                    if (grp_id == null) {
                        creategroup();
                    } else {
                        editgroup();
                    }
                }


                return true;
            case android.R.id.home:
                super.onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_done, menu);
        return true;
    }




    public void creategroup(){


        if (showlist.size() > 1) {

            
            progress = new Dialog(SearchFriends.this);
            progress.setContentView(R.layout.dialog_progress);
            progress.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            progress.show();
            progress.setCancelable(false);



            HashMap<String, Object> upload = new HashMap<>();


            upload.put("name", g.getGroup_name());
            upload.put("about", g.getGroup_about());
            upload.put("timestamp",FireService.getToday());
            upload.put("exams",g.getPrepration_exam());
            upload.put("subjects",g.getPrepration_subject());
            upload.put("group_status",0);


            final String key = KrigerConstants.group_nameRef.push().getKey();


            KrigerConstants.group_nameRef.child(key).updateChildren(upload).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {


                    StorageReference childref = FirebaseStorage.getInstance().getReference().child("Group").child("group#" + key + "#" + FireService.getToday());
                    childref.putFile(Uri.parse(g.getUrl()))
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                                    setname(key);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toasty.custom(getApplicationContext(),"Upload failed", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                    true).show();

                        }
                    });

                }
            });

        }else {
            Toasty.custom(getApplicationContext(),"Group must have at least 3 members", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                    true).show();
        }
    }

    //Edit Group
    public void editgroup()
    {
        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
        final String format = s.format(new Date());

        for (int i = 0; i < showlist.size(); i++) {
            KrigerConstants.group_dataRef.child(grp_id).child("members").child(showlist.get(i).getUid()).child("timestamp").setValue(format);
            KrigerConstants.group_dataRef.child(grp_id).child("members").child(showlist.get(i).getUid()).child("added_by").setValue(user.getUid());
            KrigerConstants.user_listRef.child(showlist.get(i).getUid()).child("group").child(grp_id).setValue(format).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Intent intent=new Intent(SearchFriends.this,GroupAbout.class);
                    intent.putExtra("grp_id",grp_id);
                    startActivity(intent);
                }
            });
        }



    }

    public void setname(String key){


        String format = FireService.getToday();

        for (int i = 0; i < showlist.size(); i++) {
            mainRef.child("Group_Data").child(key).child("members").child(showlist.get(i).getUid()).child("timestamp").setValue(format);
            mainRef.child("Group_Data").child(key).child("members").child(showlist.get(i).getUid()).child("added_by").setValue(user.getUid());
            mainRef.child("User_List").child(showlist.get(i).getUid()).child("group").child(key).setValue(format);

        }
        mainRef.child("Group_Data").child(key).child("owner").child(user.getUid()).setValue(format);

        mainRef.child("Group_Data").child(key).child("owner").child(user.getUid()).setValue(format);



        HashMap<String, Object> upload1 = new HashMap<>();
        upload1.put("timestamp", format);


        mainRef.child("User_List").child(user.getUid()).child("group").child(key).setValue(format).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progress.dismiss();
                Toasty.custom(getApplicationContext(),"Group created successfully", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                        true).show();

                Intent intent = new Intent(SearchFriends.this, HomeActivity.class);
                intent.putExtra("screen","groups");
                startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progress.dismiss();
            }
        });
    }


    private void dialogBuilder(String text, final int type, Boolean isNegative, String posText, String negText, Object[] arrayList){

        final Dialog dialog = new Dialog(SearchFriends.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.layout_dialog_benefits);

        TextView textView =  dialog.findViewById(R.id.dialogue_text);
        textView.setText(text);


        Button posButton =  dialog.findViewById(R.id.button_positive);
        posButton.setText(posText);
        posButton.setVisibility(View.VISIBLE);
        posButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(type==1)
                {
                    dialog.dismiss();

                    progressDialog = new ProgressDialog(SearchFriends.this);
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
                    Log.i("hi","link");
                    FirebaseDynamicLinks.getInstance().createDynamicLink()
                            .setLink(Uri.parse(link))
                            .setDomainUriPrefix("https://kriger.page.link")
                            .setAndroidParameters(
                                    new DynamicLink.AndroidParameters.Builder("in.kriger.krigercampus")
                                            .setMinimumVersion(1)
                                            .build()).

                                    buildShortDynamicLink()
                            .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                                @Override
                                public void onSuccess(final ShortDynamicLink shortDynamicLink) {

                                    try {

                                        if (grp_id == null) {
                                            Intent shareIntent = new Intent();
                                            shareIntent.setAction(Intent.ACTION_SEND);
//

                                            if (prefManager.getAccountType() == 0){
                                                shareIntent.putExtra(Intent.EXTRA_TEXT, user.getDisplayName() + " has invited you to join academic group : " + g.getGroup_name() + "\nwhich is about " + g.getGroup_about() + "Learner\n"+"\n\nSign up on Kriger Campus: India's Education Network to\n" +
                                                        "Connect with Students, Teachers, Colleges, Coaching \n" +
                                                        "Purchase Study material, Classes, Practice papers\n" +
                                                        "Make or join pan INDIA study groups" + shortDynamicLink.getShortLink().toString());
                                            }else if (prefManager.getAccountType() == 1){
                                                shareIntent.putExtra(Intent.EXTRA_TEXT, user.getDisplayName() + " has invited you to join academic group : " + g.getGroup_name() + "\nwhich is about " + g.getGroup_about() + "educator\n"+"\n\nSign up on Kriger Campus: India's Education Network to\n" +
                                                        "Connect with Students, Teachers, Colleges, Coaching \n" +
                                                        "Purchase Study material, Classes, Practice papers\n" +
                                                        "Make or join pan INDIA study groups" + shortDynamicLink.getShortLink().toString());
                                            }else{
                                                shareIntent.putExtra(Intent.EXTRA_TEXT, user.getDisplayName() + " has invited you to join academic group : " + g.getGroup_name() + "\nwhich is about " + g.getGroup_about() + "corporate\n"+"\n\nSign up on Kriger Campus: India's Education Network to\n" +
                                                        "Connect with Students, Teachers, Colleges, Coaching \n" +
                                                        "Purchase Study material, Classes, Practice papers\n" +
                                                        "Make or join pan INDIA study groups" + shortDynamicLink.getShortLink().toString());
                                            }




                                            shareIntent.setType("text/plain");
                                            startActivity(shareIntent);
                                        } else {

                                            KrigerConstants.group_nameRef.child(grp_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    if (dataSnapshot.exists()){
                                                        try{
                                                            Intent shareIntent = new Intent();
                                                            shareIntent.setAction(Intent.ACTION_SEND);
//
                                                            if (prefManager.getAccountType() == 0){
                                                                shareIntent.putExtra(Intent.EXTRA_TEXT, user.getDisplayName() + " has invited you to join academic group : " + dataSnapshot.child("name").getValue().toString() + "\nwhich is about " + dataSnapshot.child("about").getValue().toString() + "learner\n"+"\n\nSign up on Kriger Campus: India's Education Network to\n" +
                                                                        "Connect with Students, Teachers, Colleges, Coaching \n" +
                                                                        "Purchase Study material, Classes, Practice papers\n" +
                                                                        "Make or join pan INDIA study groups\n" + shortDynamicLink.getShortLink().toString());

                                                            }else if (prefManager.getAccountType() == 1){
                                                                shareIntent.putExtra(Intent.EXTRA_TEXT, user.getDisplayName() + " has invited you to join academic group : " + dataSnapshot.child("name").getValue().toString() + "\nwhich is about " + dataSnapshot.child("about").getValue().toString() + "educator\n"+"\n\nSign up on Kriger Campus: India's Education Network to\n" +
                                                                        "Connect with Students, Teachers, Colleges, Coaching \n" +
                                                                        "Purchase Study material, Classes, Practice papers\n" +
                                                                        "Make or join pan INDIA study groups\n" + shortDynamicLink.getShortLink().toString());

                                                            }else{
                                                                shareIntent.putExtra(Intent.EXTRA_TEXT, user.getDisplayName() + " has invited you to join academic group : " + dataSnapshot.child("name").getValue().toString() + "\nwhich is about " + dataSnapshot.child("about").getValue().toString() + "corporate\n"+"\n\nSign up on Kriger Campus: India's Education Network to\n" +
                                                                        "Connect with Students, Teachers, Colleges, Coaching \n" +
                                                                        "Purchase Study material, Classes, Practice papers\n" +
                                                                        "Make or join pan INDIA study groups\n" + shortDynamicLink.getShortLink().toString());

                                                            }


                                                            shareIntent.setType("text/plain");
                                                            startActivity(shareIntent);
                                                        }catch (NullPointerException e){

                                                        }

                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                        }
                                    }
                                    catch(NullPointerException e){

                                    }



                                }
                            });
                }
            }
        });

        try {
            dialog.show();
        }catch (WindowManager.BadTokenException e){

        }



    }


    public static class connectionViewHolder extends  RecyclerView.ViewHolder{

        private TextView sname;
        private TextView sheadline;
        private ImageView imageView_dp;
        LinearLayout list_kriger;
        View line;
        private Button btn_tag;




        public connectionViewHolder(View itemView) {
            super(itemView);

            sname =  itemView.findViewById(R.id.sname);
            sheadline =  itemView.findViewById(R.id.sheadline);
            imageView_dp =  itemView.findViewById(R.id.imageButton_dp);
            list_kriger =  itemView.findViewById(R.id.list_kiger);
            line = itemView.findViewById(R.id.line);
            btn_tag = itemView.findViewById(R.id.btn_tag);

        }
    }

    public void  updateselected(){
        selected.setText("Selected Members (" + String.valueOf(adapter_searchFriends.getItemCount())+ ")" );



    }







}