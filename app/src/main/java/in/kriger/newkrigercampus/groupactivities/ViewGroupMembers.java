package in.kriger.newkrigercampus.groupactivities;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.adapters.Adapter_ViewGroupMembers_List;
import in.kriger.newkrigercampus.classes.DataCounters;
import in.kriger.newkrigercampus.services.KrigerConstants;


public class ViewGroupMembers extends AppCompatActivity {



    String grp_id;
    RecyclerView members_list;
    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

    private Adapter_ViewGroupMembers_List adapter_groupMembers_list;

    private ArrayList<DataCounters> list_names = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group_members);

        setTitle("Members");


        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile_bar_gradient));


        if (getIntent().getExtras().getString("grp_id") != null) {
            grp_id = getIntent().getExtras().getString("grp_id");

        }

        members_list =  findViewById(R.id.view_GroupMem);

       members_list.setNestedScrollingEnabled(false);


        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(getApplicationContext());


        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        members_list.setLayoutManager(mLayoutManager);
        members_list.setItemAnimator(new DefaultItemAnimator());


        KrigerConstants.group_dataRef.child(grp_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String me = null;

                for (DataSnapshot child_members : dataSnapshot.child("members").getChildren()){
                    list_names.add(new DataCounters(child_members.getKey(),"members"));
                    if (child_members.getKey().equals(user.getUid())){
                        me = "members";
                    }
                }for (DataSnapshot child_admins : dataSnapshot.child("admins").getChildren()){
                    list_names.add(new DataCounters(child_admins.getKey(),"admins"));
                    if (child_admins.getKey().equals(user.getUid())){
                        me = "admins";

                    }
                }for (DataSnapshot child_owner : dataSnapshot.child("owner").getChildren()){
                    list_names.add(new DataCounters(child_owner.getKey(),"owner"));
                    if (child_owner.getKey().equals(user.getUid())){
                        me = "owner";
                    }

                }

                adapter_groupMembers_list = new Adapter_ViewGroupMembers_List(list_names,ViewGroupMembers.this,me,grp_id,members_list);

                members_list.setAdapter(adapter_groupMembers_list);





            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),GroupAbout.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(),GroupAbout.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }





}