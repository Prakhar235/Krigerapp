package in.kriger.newkrigercampus.searchfragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.adapters.HashTagAdapter;
import in.kriger.newkrigercampus.classes.Group_Post;
import in.kriger.newkrigercampus.classes.Post;

public class HashtagSearch extends Fragment {

    String search_text;



    String key;

    HashTagAdapter mAdapter;

    ArrayList<Post> post_list = new ArrayList<>();
    ArrayList<Group_Post> group_posts=new ArrayList<>();

    RecyclerView recyclerView;

    TextView searching;




    public HashtagSearch() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_post_search, container, false);

        search_text = getArguments().getString("search_text");



        recyclerView =  rootview.findViewById(R.id.recycler_view);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(getContext());

        searching = rootview.findViewById(R.id.textview_searching);



        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new HashTagAdapter(post_list,getContext(),group_posts);

        recyclerView.setAdapter(mAdapter);

        startSearch(search_text);





        return rootview;
    }

    public void startSearch(String text){

        searching.setText("Searching ...");
        searching.setVisibility(View.VISIBLE);

        mAdapter.setSearch(text.trim());

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/from" , 0);
        childUpdates.put("/size", 30);
        childUpdates.put("/index","firebase7");
        childUpdates.put("/type","mention_tag");
        childUpdates.put("/body/query/query_string/default_field","mention_tag");
        childUpdates.put("/body/query/query_string/query","*" + text.toLowerCase() + "*");

        /*childUpdates.put("/body/query/query_string/default_field","mention_tag");
        childUpdates.put("/body/query/query_string/query","*" + text.toLowerCase() + "*");
        */
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            try {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }catch (NullPointerException e){

            }
        }

        key = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").push().getKey();
        FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("search").child("request").child(key).updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listensearch();
            }
        });
    }


    private void listensearch(){
        FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("search").child("response").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                post_list.clear();


                if (dataSnapshot.exists()) {
                    try {
                        if (dataSnapshot.child("hits").child("total").getValue().toString().equals("0")){
                            searching.setText("No Results Found.");
                            searching.setVisibility(View.VISIBLE);
                        }else {
                            searching.setVisibility(View.GONE);

                            for (DataSnapshot childsnapshot : dataSnapshot.child("hits").child("hits").getChildren()) {


                                    Post post = childsnapshot.child("_source").getValue(Post.class);
                                    post.setDocument_id(childsnapshot.child("_id").getValue().toString());


                                    try {
                                        post_list.add(post);
                                    } catch (NullPointerException e) {
                                    }


                            }
                            recyclerView.getRecycledViewPool().clear();
                            mAdapter.notifyDataSetChanged();

                        }
                    }catch (NullPointerException e){}
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}

