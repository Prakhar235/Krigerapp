package in.kriger.newkrigercampus.profilefragments;


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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.adapters.Adapter_Post;
import in.kriger.newkrigercampus.classes.Post;
import in.kriger.newkrigercampus.services.KrigerConstants;

/**
 * A simple {@link Fragment} subclass.
 */
public class Posts extends Fragment {
    private RecyclerView recyclerView;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private TextView no_posts;

    Adapter_Post adapter_post;

    public List<Post> postlist = new ArrayList<>();


    String userid ;
    public Posts() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_post, container, false);
        //GUI
        recyclerView =  view.findViewById(R.id.recycler_view);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(getContext());


        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        no_posts =  view.findViewById(R.id.no_post);
        try{
            Log.d("User",getArguments().get("user_id").toString());
            userid = getArguments().get("user_id").toString();
        }catch (NullPointerException e){
            userid = user.getUid();
        }

        adapter_post = new Adapter_Post(getContext(),postlist,getActivity());
        recyclerView.setAdapter(adapter_post);

        //Recycler View
        KrigerConstants.postRef.orderByChild("uid").equalTo(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    List<Post> posts = new ArrayList<>();
                    for (DataSnapshot childsnapshot : dataSnapshot.getChildren()) {
                        Post post = childsnapshot.getValue(Post.class);
                        post.setDocument_id(childsnapshot.getKey());
                        posts.add(post);

                    }

                    postlist.clear();
                    postlist.addAll(posts);
                    adapter_post.notifyDataSetChanged();
                }else {
                    no_posts.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;

    }


}

