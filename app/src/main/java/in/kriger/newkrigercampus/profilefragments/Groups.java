package in.kriger.newkrigercampus.profilefragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.activities.JoinRequestActivity;
import in.kriger.newkrigercampus.database.DatabaseHelper;
import in.kriger.newkrigercampus.groupactivities.ShowGroup;
import in.kriger.newkrigercampus.services.KrigerConstants;

/**
 * A simple {@link Fragment} subclass.
 */
public class Groups extends Fragment {

    RecyclerView recyclerView;
    TextView no_groups;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    private FirebaseRecyclerAdapter<String, groupViewHolder> adapter_group ;
    String userid;

    DatabaseHelper databaseHelper;
    Context context;

    public Groups(Context context) {
        // Required empty public constructor
        this.context = context;
        databaseHelper = new DatabaseHelper(context);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_post, container, false);


        recyclerView =  view.findViewById(R.id.recycler_view);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(getContext());


        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        try{
            Log.d("User",getArguments().get("user_id").toString());
            userid = getArguments().get("user_id").toString();
        }catch (NullPointerException e){
            userid = user.getUid();
        }

        no_groups =  view.findViewById(R.id.no_post);
        no_groups.setText("Not a part of any groups");

        //Recycler View
        Query groupRef = KrigerConstants.user_listRef.child(userid).child("group");

        groupRef.keepSynced(true);

        FirebaseRecyclerOptions<String> options =
                new FirebaseRecyclerOptions.Builder<String>()
                        .setQuery(groupRef, String.class)
                        .setLifecycleOwner(this)
                        .build();

        adapter_group = new FirebaseRecyclerAdapter<String, groupViewHolder>(options) {
            @Override
            public groupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new groupViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.pro_group_list, parent, false));
            }


            @Override
            public void onDataChanged() {
                no_groups.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            protected void onBindViewHolder(@NonNull final groupViewHolder groupViewHolder, int i, @NonNull String s) {
                final String grpid = adapter_group.getRef(i).getKey();
                KrigerConstants.group_nameRef.child(grpid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            groupViewHolder.sname.setText(dataSnapshot.child("name").getValue().toString());
                            try {

                                RequestOptions requestOption = new RequestOptions()
                                        .placeholder(R.drawable.default_profile)
                                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


                                Glide.with(getContext()).load(dataSnapshot.child("thumb").getValue().toString())
                                        .apply(requestOption)
                                        .into(groupViewHolder.imageView_dp);


                            } catch (NullPointerException e) {
                                groupViewHolder.imageView_dp.setImageResource(R.drawable.default_groups);
                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                groupViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (databaseHelper.isGroupPresent(grpid)){
                            Intent intent = new Intent(getContext(), ShowGroup.class);
                            intent.putExtra("grp_id",grpid);

                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(getContext(), JoinRequestActivity.class);
                            intent.putExtra("grp_id", grpid);
                            startActivity(intent);
                        }
                    }
                });

            }
        };


        recyclerView.setAdapter(adapter_group);

        return view;




    }

    //UI of a item
    public static class groupViewHolder extends  RecyclerView.ViewHolder{

        private TextView sname;
        private ImageView imageView_dp;




        public groupViewHolder(View itemView) {
            super(itemView);

            sname =  itemView.findViewById(R.id.sname);
            imageView_dp =  itemView.findViewById(R.id.imageButton_dp);

        }
    }




    }

