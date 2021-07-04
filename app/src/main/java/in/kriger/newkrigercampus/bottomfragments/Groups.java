package in.kriger.newkrigercampus.bottomfragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.activities.HomeActivity;
import in.kriger.newkrigercampus.activities.JoinRequestActivity;
import in.kriger.newkrigercampus.activities.ProfileListActivity;
import in.kriger.newkrigercampus.activities.SearchActivity;
import in.kriger.newkrigercampus.classes.Group_Counter;
import in.kriger.newkrigercampus.classes.UserId;
import in.kriger.newkrigercampus.database.DatabaseHelper;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.extras.PrefManager;
import in.kriger.newkrigercampus.groupactivities.Group_Guidelines_Activity;
import in.kriger.newkrigercampus.groupactivities.ShowGroup;
import in.kriger.newkrigercampus.services.KrigerConstants;


/**
 * A simple {@link Fragment} subclass.
 */
public class Groups extends Fragment {



    RecyclerView recyclerView_verti;

    List<String> grp_list = new ArrayList<>();

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private FirebaseRecyclerAdapter<String,connectionViewHolder> adapter_groups ;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/");
    ArrayList<String> list_name;
    private FirebaseRecyclerAdapter<UserId, suggestionViewHolder> adapter_group_suggestions;
    private RecyclerView recyclerView_suggested_groups;

    EditText grp_search;

    TextView no_group_text,your_group_text,add_group_text;

    ImageView btn_grpsearch,imageView_profile;

    ProgressBar progressBar;

    Query groupRef,suggestRef;
    FloatingActionButton create;
    Toolbar toolbar;
    private PrefManager prefManager;

    private DatabaseHelper db;

    Boolean group_status = false;

    public Groups() {
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootview =  inflater.inflate(R.layout.fragment_groups, container, false);


        //GUI
       recyclerView_verti =  rootview.findViewById(R.id.recycler_view_verti);
       recyclerView_verti.setNestedScrollingEnabled(false);

        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(getContext());
        final AppCompatActivity activity= (AppCompatActivity)getActivity();
        toolbar=rootview.findViewById(R.id.toolbar);

        prefManager = new PrefManager(getContext());


        activity.setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        db = new DatabaseHelper(getContext());



        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView_verti.setLayoutManager(mLayoutManager);
        recyclerView_verti.setItemAnimator(new DefaultItemAnimator());



        recyclerView_suggested_groups=rootview.findViewById(R.id.suggestion_groups);
        recyclerView_suggested_groups.setNestedScrollingEnabled(false);
        LinearLayoutManager manager=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        recyclerView_suggested_groups.setLayoutManager(manager);
        recyclerView_suggested_groups.setItemAnimator(new DefaultItemAnimator());
        recyclerView_suggested_groups.setHasFixedSize(true);



        progressBar = rootview.findViewById(R.id.loading);




        no_group_text = rootview.findViewById(R.id.no_group);
        add_group_text = rootview.findViewById(R.id.addgrp_text);
        your_group_text = rootview.findViewById(R.id.your_group_text);
        grp_search = rootview.findViewById(R.id.text_search);
        btn_grpsearch = rootview.findViewById(R.id.btn_text_search);
        create=rootview.findViewById(R.id.create_group);
        btn_grpsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (grp_search.getText().toString().equals("")){
                    Toasty.custom(getContext(), "Nothing to search", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_SHORT, false,
                            true).show();

                }else {

                    Intent intent = new Intent(getContext(), SearchActivity.class);
                    intent.putExtra("search_text",grp_search.getText().toString());
                    startActivity(intent);
                }
            }
        });

        //Group Search Button
        grp_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Intent intent = new Intent(getContext(), SearchActivity.class);
                    intent.putExtra("search_text",grp_search.getText().toString());
                    startActivity(intent);

                    return true;
                }
                return false;
            }
        });

        imageView_profile =  rootview.findViewById(R.id.profile_photo);
        imageView_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), ProfileListActivity.class);
                intent.putExtra("user_id", user.getUid());
                startActivity(intent);

            }
        });

         create.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        startActivity(new Intent(getContext(),Group_Guidelines_Activity.class));
    }
});


        list_name  = new ArrayList<>();


        grp_list.add("1");



        try {

            //Groups RecyclerAdapter
            groupRef = KrigerConstants.user_listRef.child(user.getUid()).child("group");

            groupRef.keepSynced(true);

            FirebaseRecyclerOptions<String> options =
                    new FirebaseRecyclerOptions.Builder<String>()
                            .setQuery(groupRef, String.class)
                            .setLifecycleOwner(this)
                            .build();

            adapter_groups = new FirebaseRecyclerAdapter<String, connectionViewHolder>(options) {
                @Override
                public connectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    return new connectionViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_item_group, parent, false));
                }

                @Override
                protected void onBindViewHolder(@NonNull final connectionViewHolder holder, final int position, @NonNull String model) {
                    holder.imageView_dp.setImageDrawable(null);


                    final String groupid = adapter_groups.getRef(position).getKey();
                    KrigerConstants.group_nameRef.child(groupid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                holder.sname.setText(dataSnapshot.child("name").getValue().toString());
                            } catch (NullPointerException e) {
                                adapter_groups.getRef(position).removeValue();
                            }
                            try {

                                RequestOptions requestOption = new RequestOptions()
                                        .placeholder(R.drawable.default_profile)
                                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


                                Glide.with(getContext()).load(dataSnapshot.child("thumb").getValue().toString())
                                        .apply(requestOption)
                                        .into(holder.imageView_dp);


                            } catch (NullPointerException e) {
                                holder.imageView_dp.setImageResource(R.drawable.default_groups);
                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    KrigerConstants.group_counterRef.child(groupid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            int count_invites = 0, count_post = 0;

                            if (dataSnapshot.child("count_invites").exists()) {
                                count_invites = Integer.valueOf(dataSnapshot.child("count_invites").getValue().toString());
                            }
                            if (dataSnapshot.child("count_posts").exists()) {
                                count_post = Integer.valueOf(dataSnapshot.child("count_posts").getValue().toString());
                            }

                            if (count_invites > 0) {
                                KrigerConstants.group_dataRef.child(groupid).child("members").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (!dataSnapshot.exists()) {
                                            holder.imageView_invite.setVisibility(View.VISIBLE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }

                            if (!(db.isGroupPresent(groupid))) {
                                holder.imageView_new.setVisibility(View.VISIBLE);
                                db.insertGroupCounter(groupid);
                                if (count_post > 0) {
                                    holder.btn_post_count.setVisibility(View.VISIBLE);
                                    holder.btn_post_count.setText(String.valueOf(count_post));
                                }


                            } else {
                                if (db.isGroupOpen(groupid).equals("false")) {
                                    holder.imageView_new.setVisibility(View.VISIBLE);

                                }
                                int extra_post = count_post - db.getGroupCounter(groupid);
                                if (extra_post > 0) {
                                    holder.btn_post_count.setVisibility(View.VISIBLE);
                                    holder.btn_post_count.setText(String.valueOf(extra_post));
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    KrigerConstants.group_postRef.child(groupid).limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                                for (DataSnapshot childsnap : dataSnapshot.getChildren()) {

                                        if (childsnap.child("text").exists()) {
                                           holder.sheadline.setText(childsnap.child("text").getValue().toString());
                                        }
                                        else if (childsnap.child("image_url").exists()) {
                                            holder.sheadline.setText("Image");
                                        }

                                }





                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), ShowGroup.class);
                            if (holder.btn_post_count.getVisibility() == View.VISIBLE) {
                                db.openGroupCounter(groupid);
                                int value = db.getGroupCounter(groupid) + Integer.valueOf(holder.btn_post_count.getText().toString());
                                db.updateGroupCounter(groupid, Group_Counter.COLUMN_COUNT_POSTS, value);
                            } else {
                                db.openGroupCounter(groupid);
                            }

                            intent.putExtra("grp_id", adapter_groups.getRef(position).getKey());
                            if (holder.imageView_invite.getVisibility() == View.VISIBLE) {
                                intent.putExtra("invite", true);
                            }
                            startActivity(intent);

                        }
                    });


                }

                @Override
                public void onDataChanged() {
                    if (getItemCount() == 0) {
                        no_group_text.setVisibility(View.VISIBLE);
                        add_group_text.setVisibility(View.VISIBLE);
                        your_group_text.setVisibility(View.GONE);

                    } else {

                    }
                }
            };

            recyclerView_verti.setAdapter(adapter_groups);

            suggestRef = KrigerConstants.group_suggestionRef.child(user.getUid());
            suggestRef.keepSynced(true);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            final int height = displayMetrics.heightPixels;
            final int width = displayMetrics.widthPixels;
            final double new_width = width * 0.22;
            final double new_height = height * 0.22;

            FirebaseRecyclerOptions<UserId> options1 =
                    new FirebaseRecyclerOptions.Builder<UserId>()
                            .setQuery(suggestRef, UserId.class)
                            .setLifecycleOwner(this)
                            .build();

            adapter_group_suggestions = new FirebaseRecyclerAdapter<UserId, suggestionViewHolder>(options1) {
                @Override
                public suggestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


                    return new suggestionViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.groupsuggestion, parent, false));
                }

                @Override
                protected void onBindViewHolder(@NonNull final suggestionViewHolder holder, final int position, @NonNull UserId model) {
                    try {

                        holder.linearLayout.setLayoutParams(new LinearLayoutCompat.LayoutParams((int)new_width, (int)new_height));
                        myRef.child("Group_Name").child(adapter_group_suggestions.getRef(position).getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    holder.sname.setText(dataSnapshot.child("name").getValue().toString());

                                    try {
                                        RequestOptions requestOption = new RequestOptions()
                                                .placeholder(R.drawable.default_profile)
                                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


                                        Glide.with(getContext()).load(dataSnapshot.child("thumb").getValue().toString())
                                                .apply(requestOption)
                                                .into(holder.imageView_dp);



                                    } catch (NullPointerException e) {
                                    }

                                    try{
                                        if(Integer.valueOf(dataSnapshot.child("group_status").getValue().toString())==1){
                                            group_status = true;
                                        }
                                    }catch (NullPointerException e){

                                    }

                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    } catch (NullPointerException | IndexOutOfBoundsException i) {
                    }
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(group_status){
                                adapter_group_suggestions.getRef(position).removeValue();
                                Toasty.custom(getContext(), "Group has been deleted.", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                        true).show();

                            }else{
                                Intent intent = new Intent(getContext(), JoinRequestActivity.class);
                                intent.putExtra("grp_id", adapter_group_suggestions.getRef(position).getKey());
                                startActivity(intent);
                            }




                        }
                    });
                    holder.grp_delete_suggest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            adapter_group_suggestions.getRef(position).removeValue();
                        }
                    });


                }

            };

        }catch (NullPointerException e){

        }

        recyclerView_suggested_groups.setAdapter(adapter_group_suggestions);





        if (prefManager.getProfileImageUrl() == null) {
            DatabaseReference profileRef = KrigerConstants.user_detailRef.child(user.getUid()).child("thumb");
            profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        if (getContext() != null) {

                            RequestOptions requestOption = new RequestOptions()
                                    .placeholder(R.drawable.default_profile)
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


                            Glide.with(getContext()).load(dataSnapshot.getValue().toString())

                                    .apply(requestOption)
                                    .into(imageView_profile);


                            prefManager.setProfileImageUrl(dataSnapshot.getValue().toString());
                        }
                    } else {
                        imageView_profile.setImageResource(R.drawable.default_profile);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {

            RequestOptions requestOption = new RequestOptions()
                    .placeholder(R.drawable.default_profile)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


            Glide.with(getContext()).load(prefManager.getProfileImageUrl())

                    .apply(requestOption)
                    .into(imageView_profile);


        }






        return rootview;
    }

    public static class connectionViewHolder extends  RecyclerView.ViewHolder{

        private TextView sname;
        private TextView sheadline;
        private ImageView imageView_dp;
        private Button imageView_new,imageView_invite;
        private Button btn_post_count;

        LinearLayout layout_linear;




        public connectionViewHolder(View itemView) {
            super(itemView);

            sname =  itemView.findViewById(R.id.sname);
            sheadline =  itemView.findViewById(R.id.sheadline);
            imageView_dp =  itemView.findViewById(R.id.imageButton_dp);
            imageView_new =  itemView.findViewById(R.id.imageview_new);
            imageView_invite =  itemView.findViewById(R.id.imageview_invite);
            btn_post_count =  itemView.findViewById(R.id.imageview_count);
            layout_linear =  itemView.findViewById(R.id.layout_linear);


        }
    }


    public static class suggestionViewHolder extends  RecyclerView.ViewHolder{

        private TextView sname;
        private ImageView imageView_dp;
        private ImageButton grp_delete_suggest;
        private LinearLayout linearLayout;





        public suggestionViewHolder(View itemView) {
            super(itemView);

            sname =  itemView.findViewById(R.id.grp_name);
            imageView_dp =  itemView.findViewById(R.id.grp_image);
            grp_delete_suggest=itemView.findViewById(R.id.delete_icon);
            linearLayout = itemView.findViewById(R.id.linear_suggestedgroups);


        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                Intent intent=new Intent(getActivity(),HomeActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
