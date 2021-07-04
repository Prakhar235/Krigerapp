package in.kriger.newkrigercampus.groupactivities;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.activities.HomeActivity;
import in.kriger.newkrigercampus.activities.ProfileListActivity;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.classes.UserIdString;
import in.kriger.newkrigercampus.services.FireService;

public class GroupInvitesMembers extends AppCompatActivity {

    String grp_id;

    private FirebaseRecyclerAdapter<UserIdString, connectionViewHolder> adapter;
    RecyclerView invites_list;
    TextView noInvites,noInvites2;
    DatabaseReference reference=FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/");
    private Button add_mem;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_invites_members);

        grp_id = getIntent().getExtras().getString("grp_id");

        FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("Group_Name").child(grp_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    setTitle(dataSnapshot.child("name").getValue().toString() +" : Invites");

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile_bar_gradient));



        invites_list =  findViewById(R.id.view_GroupInv);
        noInvites = findViewById(R.id.noInvites);
        noInvites2 =  findViewById(R.id.addmem_text);
        add_mem= findViewById(R.id.add_members);


        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(GroupInvitesMembers.this);


        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        invites_list.setLayoutManager(mLayoutManager);
        invites_list.setItemAnimator(new DefaultItemAnimator());

        add_mem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GroupInvitesMembers.this,SearchFriends.class);
                intent.putExtra("grp_id",grp_id);
                startActivity(intent);
            }
        });

        reference.child("Group_Data").child(grp_id).child("invites").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    setAdapter(); }
                    else
                {
                    noInvites.setVisibility(View.VISIBLE);
                    noInvites2.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void setAdapter() {

        Query connectionnRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("Group_Data").child(grp_id).child("invites");

        connectionnRef.keepSynced(true);

        FirebaseRecyclerOptions<UserIdString> options =
                new FirebaseRecyclerOptions.Builder<UserIdString>()
                        .setQuery(connectionnRef, UserIdString.class)
                        .setLifecycleOwner(this)
                        .build();

        adapter = new FirebaseRecyclerAdapter<UserIdString, connectionViewHolder>(options) {
            @Override
            public connectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new connectionViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_group_invite, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final connectionViewHolder holder, final int position, @NonNull UserIdString model) {
                Log.i("key", String.valueOf(holder.sname));

                FireService.showdetails(getApplicationContext(), holder.sname, holder.sheadline, holder.imageView_dp, holder.btn_tag, adapter.getRef(position).getKey());


                holder.sname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(GroupInvitesMembers.this, ProfileListActivity.class);
                        intent.putExtra("user_id", adapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });


                holder.accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
                        final String format = s.format(new Date());


                        final String key = adapter.getRef(position).getKey();

                        adapter.getRef(position).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                final HashMap<String, Object> map = new HashMap<>();
                                map.put("timestamp", format);

                                reference.child("Group_Data").child(grp_id).child("members").child(key).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        reference.child("User_List").child(key).child("group").child(grp_id).setValue(format).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                reference.child("User_List").child(key).child("sent_group_invites").child(grp_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
//                                                        progress.dismiss();
                                                        Toasty.custom(getApplicationContext(),"User request accepted", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                                                true).show();

                                                    }
                                                });

                                            }
                                        });


                                    }
                                });

                            }
                        });


                    }
                });

                holder.reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        adapter.getRef(position).removeValue();
                        reference.child("User_List").child(adapter.getRef(position).getKey()).child("sent_group_invites").child(grp_id).removeValue();
                        Toasty.custom(getApplicationContext(),"User request declined", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                true).show();
                    }
                });

            }
        };

        invites_list.setAdapter(adapter);


    }







    @Override
    public void onBackPressed() {


        try{
            Log.d("Intent",getIntent().getExtras().getString("source"));
            Intent intent1 = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent1);
        }catch (NullPointerException e){

        }
        super.onBackPressed();
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


    public static class connectionViewHolder extends RecyclerView.ViewHolder {

        private TextView sname;
        private TextView sheadline;
        private ImageView imageView_dp;
        ImageView accept, reject;
        private Button btn_tag;


        public connectionViewHolder(View itemView) {
            super(itemView);

            sname =  itemView.findViewById(R.id.invite_name);
            sheadline =  itemView.findViewById(R.id.invite_headline);
            imageView_dp =  itemView.findViewById(R.id.imageButton_dp);
            accept = itemView.findViewById(R.id.invite_accept);
            reject =  itemView.findViewById(R.id.invite_decline);
            btn_tag =  itemView.findViewById(R.id.btn_tag);
        }
    }
}
