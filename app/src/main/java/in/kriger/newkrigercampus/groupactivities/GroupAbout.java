package in.kriger.newkrigercampus.groupactivities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.activities.HomeActivity;

import in.kriger.newkrigercampus.adapters.Adapter_GroupMembers_List;
import in.kriger.newkrigercampus.classes.DataCounters;
import in.kriger.newkrigercampus.extras.Processor;
import in.kriger.newkrigercampus.extras.TypefaceUtil;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.services.KrigerConstants;

public class GroupAbout extends AppCompatActivity  {

    String grp_id;
    ImageView  grp_image;
    TextView grp_name,grp_about,mem_count,date_creation,posts_count;
    View view,view1,view2;

    private FlowLayout flow_subs;
    private FlowLayout flow_exams;



    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    Typeface typeface;

    ImageView imageview_geditgrp;



    Button view_members;

    Button btn_leave,add_mem,btn_invite,btn_delete;
    RecyclerView members_list;


    private Adapter_GroupMembers_List adapter_groupMembers_list;

    private ArrayList<DataCounters> list_names = new ArrayList<>();
    ArrayList<String> userlist = new ArrayList<>();


    String me = null;
    LinearLayout linear_pending;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_group_about);


        if (getIntent().getExtras().getString("grp_id") != null) {
            grp_id = getIntent().getExtras().getString("grp_id");

        }
        final String[] exams = getResources().getStringArray(R.array.list_exams);
        final String[] subs = getResources().getStringArray(R.array.list_sub);

        //GUI
        grp_image =  findViewById(R.id.group_image) ;
        grp_name = findViewById(R.id.grp_name);
        grp_about =  findViewById(R.id.grp_about);
        mem_count=findViewById(R.id.mem_count);
        date_creation=findViewById(R.id.date_info);
        posts_count=findViewById(R.id.post_count);
        flow_exams = findViewById(R.id.flow_exams);
        flow_subs = findViewById(R.id.flow_sub);
        view_members=findViewById(R.id.see_more);
        view=findViewById(R.id.view_members);
        view1=findViewById(R.id.view_invites);
        view2=findViewById(R.id.view_delete);
        add_mem=findViewById(R.id.add_mem);
        btn_invite=findViewById(R.id.invites);
        btn_delete=findViewById(R.id.delete);
        imageview_geditgrp =  findViewById(R.id.imageview_editgrp);
        linear_pending = findViewById(R.id.linear_pending);

        final Button invite_icon =  findViewById(R.id.imageview_invite);
        invite_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GroupAbout.this,GroupInvitesMembers.class);
                intent.putExtra("grp_id",grp_id);
                startActivity(intent);
            }
        });

        try{
            if (getIntent().getExtras().getBoolean("invite")){
                invite_icon.setVisibility(View.VISIBLE);
            }
        }catch (NullPointerException e){}


         //See more clickListener
        view_members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GroupAbout.this,ViewGroupMembers.class);
                intent.putExtra("grp_id",grp_id);
                startActivity(intent);
            }
        });

        btn_leave =  findViewById(R.id.btn_leave);

        btn_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (me.equals("owner")){

                    dialogBuilder("We are sorry to see you leave the ownership of this group.\n" +
                            "Assign an owner before you leave.",1,true,"Stay","Assign new owner",null).show();

                }else{
                    dialogBuilder("We are sorry to see you leave this group! It's posts, information and members.",2,true,"Stay","I'll miss them too!",null).show();


                }

            }
        });






        members_list=findViewById(R.id.members_list);
        members_list.setNestedScrollingEnabled(false);

        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(getApplicationContext());
        members_list.setLayoutManager(mLayoutManager);
        members_list.setItemAnimator(new DefaultItemAnimator());


        KrigerConstants.group_dataRef.child(grp_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot child_members : dataSnapshot.child("members").getChildren()){
                    list_names.add(new DataCounters(child_members.getKey(),"members"));
                    userlist.add(child_members.getKey());
                    if (child_members.getKey().equals(user.getUid())){
                        me = "members";
                    }
                }for (DataSnapshot child_admins : dataSnapshot.child("admins").getChildren()){
                    list_names.add(new DataCounters(child_admins.getKey(),"admins"));
                    userlist.add(child_admins.getKey());
                    if (child_admins.getKey().equals(user.getUid())){
                        me = "admins";
                        imageview_geditgrp.setVisibility(View.VISIBLE);
                    }
                }for (DataSnapshot child_owner : dataSnapshot.child("owner").getChildren()){
                    list_names.add(new DataCounters(child_owner.getKey(),"owner"));
                    userlist.add(child_owner.getKey());
                    if (child_owner.getKey().equals(user.getUid())){
                        me = "owner";
                        imageview_geditgrp.setVisibility(View.VISIBLE);
                    }

                }
                Collections.reverse(list_names);

                adapter_groupMembers_list = new Adapter_GroupMembers_List(list_names,GroupAbout.this,me,grp_id);

                members_list.setAdapter(adapter_groupMembers_list);

                mem_count.setText(String.valueOf(list_names.size()) + " Members");




                if(list_names.size()>10){
                    view_members.setVisibility(View.VISIBLE);
                }

                if (me.equals("owner") || me.equals("admins")){
                    linear_pending.setVisibility(View.VISIBLE);
                    view.setVisibility(View.VISIBLE);
                    view1.setVisibility(View.VISIBLE);
                    add_mem.setVisibility(View.VISIBLE);


                }

                if(me.equals("owner")){
                    btn_delete.setVisibility(View.VISIBLE);
                    view2.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        imageview_geditgrp.bringToFront();


        imageview_geditgrp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(GroupAbout.this, CreateGroup.class);
                intent.putExtra("grp_id", grp_id);
                startActivity(intent);

            }
        });




        add_mem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GroupAbout.this,SearchFriends.class);
                intent.putExtra("grp_id",grp_id);
                startActivity(intent);
            }
        });

        btn_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GroupAbout.this,GroupInvitesMembers.class);
                intent.putExtra("grp_id",grp_id);
                startActivity(intent);
            }
        });

        //Delete group
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder("Your group will be delete. Are you sure?",5,true,"No","Yes",null).show();
            }
        });

        typeface = new TypefaceUtil().getfontbold(getApplicationContext());

        KrigerConstants.group_nameRef.child(grp_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    grp_name.setText(dataSnapshot.child("name").getValue().toString());
                    grp_about.setText(dataSnapshot.child("about").getValue().toString());
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




                    if (dataSnapshot.child("timestamp").exists()){
                        date_creation.setText(Processor.timestamp(dataSnapshot.child("timestamp").getValue().toString())+", " + dataSnapshot.child("timestamp").getValue().toString().substring(0,4) );

                    }


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



        KrigerConstants.group_counterRef.child(grp_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("count_posts").exists()) {
                    posts_count.setText(String.valueOf(dataSnapshot.child("count_posts").getValue().toString()) + " Posts");

                } else {
                    posts_count.setText("0 Posts");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    @Override
    public void onBackPressed() {

        try{
            Log.d("Intent",getIntent().getExtras().getString("source"));
            Intent intent1 = new Intent(getApplicationContext(),HomeActivity.class);
            startActivity(intent1);
        }catch (NullPointerException e){
            Intent intent = new Intent(getApplicationContext(),ShowGroup.class);
            intent.putExtra("grp_id",grp_id);
            startActivity(intent);
        }

    }


    private Dialog dialogBuilder(String text, final int type, Boolean isNegative, String posText, String negText, Object[] arrayList){

        final Dialog dialog = new Dialog(GroupAbout.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.layout_dialog);

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

                }else if(type==2)
                {
                    dialog.dismiss();

                }else if(type ==5){
                    dialog.dismiss();
                }
            }
        });

        if (isNegative){

            Button negButton =  dialog.findViewById(R.id.button_negative);
            negButton.setText(negText);
            negButton.setVisibility(View.VISIBLE);
            negButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(type == 1){
                        Intent intent=new Intent(GroupAbout.this,AssignNewOwner.class);
                        intent.putExtra("grp_id",grp_id);
                        startActivity(intent);
                    }else if(type == 2){

                        FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("Group_Data").child(grp_id).child("members").child(user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {

                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("User_List").child(user.getUid()).child("group").child(grp_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toasty.custom(getApplicationContext(), "You left the group.", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                                true).show();

                                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                        intent.putExtra("screen","groups");
                                        startActivity(intent);
                                    }
                                });
                            }
                        });

                    }

                    else if(type ==5){
                        KrigerConstants.group_nameRef.child(grp_id).child("group_status").setValue(1);

                        HashMap<String,Object> map = new HashMap<>();
                        HashMap<String,Object> map1 = new HashMap<>();
                        map1.put(grp_id,null);
                        for (int i = 0 ; i<userlist.size() ;i++){
                            map.put(userlist.get(i),map1);

                        }


                        KrigerConstants.group_dataRef.child(grp_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                               KrigerConstants.user_listRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toasty.custom(getApplicationContext(), "Group has been deleted.", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                                true).show();

                                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                        intent.putExtra("screen", "groups");
                                        startActivity(intent);
                                    }
                                });
                            }
                        });


                    }

                }
            });
        }

        return dialog;

    }



}








