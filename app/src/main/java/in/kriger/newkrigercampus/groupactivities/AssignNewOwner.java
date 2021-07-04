package in.kriger.newkrigercampus.groupactivities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.activities.HomeActivity;
import in.kriger.newkrigercampus.activities.ProfileListActivity;
import in.kriger.newkrigercampus.classes.UserIdString;
import in.kriger.newkrigercampus.services.KrigerConstants;
import in.kriger.newkrigercampus.services.FireService;

public class AssignNewOwner extends AppCompatActivity {

    private FirebaseRecyclerAdapter<UserIdString, AssignNewOwner.connectionViewHolder> adapter;
    private FirebaseRecyclerAdapter<UserIdString, AssignNewOwner.connectionViewHolder> adapter_admin;

    String grp_id;
    RecyclerView members_list,admin_list;
    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

    int dialog_position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group_members);

        setTitle("Assign New Owner");


        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile_bar_gradient));


        if (getIntent().getExtras().getString("grp_id") != null) {
            grp_id = getIntent().getExtras().getString("grp_id");

        }

        members_list =  findViewById(R.id.view_GroupMem);
        admin_list= findViewById(R.id.view_GroupAdmin);

        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(getApplicationContext());


        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        members_list.setLayoutManager(mLayoutManager);
        members_list.setItemAnimator(new DefaultItemAnimator());

        LinearLayoutManager mLayoutManager1 =
                new LinearLayoutManager(getApplicationContext());


        mLayoutManager1.setReverseLayout(true);
        mLayoutManager1.setStackFromEnd(true);
        admin_list.setLayoutManager(mLayoutManager1);
        admin_list.setItemAnimator(new DefaultItemAnimator());


        Query connectionnRef = KrigerConstants.group_dataRef.child(grp_id).child("members").limitToLast(20);

        connectionnRef.keepSynced(true);

        FirebaseRecyclerOptions<UserIdString> options =
                new FirebaseRecyclerOptions.Builder<UserIdString>()
                        .setQuery(connectionnRef, UserIdString.class)
                        .setLifecycleOwner(this)
                        .build();

        adapter = new FirebaseRecyclerAdapter<UserIdString, AssignNewOwner.connectionViewHolder>(options) {
            @Override
            public AssignNewOwner.connectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new AssignNewOwner.connectionViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.group_members_list, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final AssignNewOwner.connectionViewHolder holder, final int position, @NonNull UserIdString model) {
                FireService.showdetails(getApplicationContext(), holder.sname, holder.sheadline, holder.imageView_dp, null, adapter.getRef(position).getKey());
                //Assign owner to member
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        KrigerConstants.group_dataRef.child(grp_id).child("owner").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){

                                    dialog_position = position;
                                    dialogBuilder("We are sorry to see you leave this group.\n" +
                                            "Are you sure you want to make " + holder.sname.getText()+" the new owner?",1,true,"No","Yes",null).show();



                                }else
                                {
                                    Intent intent=new Intent(AssignNewOwner.this, ProfileListActivity.class);
                                    intent.putExtra("user_id",adapter.getRef(position).getKey());
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });



                    }
                });



            }
        };

        members_list.setAdapter(adapter);

        Query adminRef = KrigerConstants.group_dataRef.child(grp_id).child("admins").limitToLast(20);

        adminRef.keepSynced(true);

        FirebaseRecyclerOptions<UserIdString> options1 =
                new FirebaseRecyclerOptions.Builder<UserIdString>()
                        .setQuery(adminRef, UserIdString.class)
                        .setLifecycleOwner(this)
                        .build();

        adapter_admin = new FirebaseRecyclerAdapter<UserIdString, AssignNewOwner.connectionViewHolder>(options1) {
            @Override
            public AssignNewOwner.connectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new AssignNewOwner.connectionViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.group_members_list, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final AssignNewOwner.connectionViewHolder holder, final int position, @NonNull UserIdString model) {
                FireService.showdetails(getApplicationContext(), holder.sname, holder.sheadline, holder.imageView_dp, null, adapter_admin.getRef(position).getKey());

                //Assign owner to admin
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        KrigerConstants.group_dataRef.child(grp_id).child("owner").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){

                                    dialog_position = position;
                                    dialogBuilder("We are sorry to see you leave this group.\n" +
                                            "Are you sure you want to make " + holder.sname.getText()+" the new owner?",2,true,"No","Yes",null).show();




                                }else
                                {
                                    Intent intent=new Intent(AssignNewOwner.this,ProfileListActivity.class);
                                    intent.putExtra("user_id",adapter_admin.getRef(position).getKey());
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });



                    }
                });


                holder.admin.setVisibility(View.VISIBLE);

            }
        };

        admin_list.setAdapter(adapter_admin);



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



    private Dialog dialogBuilder(String text, final int type, Boolean isNegative, String posText, String negText, Object[] arrayList){

        final Dialog dialog = new Dialog(AssignNewOwner.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
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

                }else if(type==2){
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
                        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
                        final String format = s.format(new Date());
                        KrigerConstants.group_dataRef.child(grp_id).child("owner").child(user.getUid()).removeValue();
                        KrigerConstants.group_dataRef.child(grp_id).child("owner").child(adapter.getRef(dialog_position).getKey()).setValue(format);
                        KrigerConstants.group_dataRef.child(grp_id).child("members").child(adapter.getRef(dialog_position).getKey()).removeValue();


                        FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("User_List").child(user.getUid()).child("group").child(grp_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toasty.custom(getApplicationContext(),"You left the group", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                        true).show();
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                intent.putExtra("screen","groups");
                                startActivity(intent);
                            }
                        });
                    }else if(type == 2){
                        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
                        final String format = s.format(new Date());
                        KrigerConstants.group_dataRef.child(grp_id).child("owner").child(user.getUid()).removeValue();
                        KrigerConstants.group_dataRef.child(grp_id).child("owner").child(adapter_admin.getRef(dialog_position).getKey()).setValue(format);
                        KrigerConstants.group_dataRef.child(grp_id).child("admins").child(adapter_admin.getRef(dialog_position).getKey()).removeValue();


                        FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("User_List").child(user.getUid()).child("group").child(grp_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toasty.custom(getApplicationContext(),"You left the group", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                        true).show();
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                intent.putExtra("screen","groups");
                                startActivity(intent);
                            }
                        });

                    }


                }
            });
        }





        return dialog;

    }




    public static class connectionViewHolder extends RecyclerView.ViewHolder {

        public TextView sname;
        public TextView sheadline;
        public ImageView imageView_dp, imageView_star;
        public Button admin;


        public connectionViewHolder(View itemView) {
            super(itemView);

            sname =  itemView.findViewById(R.id.sname);
            sheadline =  itemView.findViewById(R.id.sheadline);
            imageView_dp =  itemView.findViewById(R.id.imageButton_dp);
            imageView_star =  itemView.findViewById(R.id.imageview_star);
            admin=itemView.findViewById(R.id.btn_admin);

        }
    }


}