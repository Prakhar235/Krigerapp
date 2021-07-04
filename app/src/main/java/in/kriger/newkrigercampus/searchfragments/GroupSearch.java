package in.kriger.newkrigercampus.searchfragments;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

import in.kriger.newkrigercampus.classes.UserDetails;
import in.kriger.newkrigercampus.adapters.GroupSearchAdapter;
import in.kriger.newkrigercampus.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupSearch extends Fragment {

    String search_text;
    TextView textView;

    String key;

    RecyclerView recyclerView;

    private GroupSearchAdapter mAdapter;

    ArrayList<UserDetails> group_list = new ArrayList<>();

    TextView searching;



    public GroupSearch() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_group_search, container, false);

        search_text = getArguments().getString("search_text");
        searching = rootview.findViewById(R.id.textview_searching);




        recyclerView =  rootview.findViewById(R.id.recycler_view);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(getContext());


        mAdapter = new GroupSearchAdapter(group_list,getContext());



        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);


        return rootview;
    }

    public void startSearch(String text){

        searching.setText("Searching ...");
        searching.setVisibility(View.VISIBLE);

        mAdapter.setSearch(text.trim());


        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/from" , 0);
        childUpdates.put("/size", 20);
        childUpdates.put("/index","firebase2");
        childUpdates.put("/type","group_name");
        childUpdates.put("/body/query/multi_match/query/",text.trim().toLowerCase()+"*");
        childUpdates.put("/body/query/multi_match/fields/0/","name");
        childUpdates.put("/body/query/multi_match/fields/1/","about");
        childUpdates.put("/body/sort/timestamp","desc");
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

                group_list.clear();


                if (dataSnapshot.exists()) {
                    try {
                        if (dataSnapshot.child("hits").child("total").getValue().toString().equals("0")) {
                            searching.setText("No Results Found.");
                            searching.setVisibility(View.VISIBLE);
                        } else {
                            searching.setVisibility(View.GONE);
                            for (DataSnapshot childsnapshot : dataSnapshot.child("hits").child("hits").getChildren()) {
                                if (childsnapshot.child("_source").child("name").exists()) {
                                    UserDetails userDetails = new UserDetails();
                                    userDetails.setUid(childsnapshot.child("_id").getValue().toString());
                                    userDetails.setHeadline(childsnapshot.child("_source").child("about").getValue().toString());
                                    if (childsnapshot.child("_source").child("thumb").exists()) {
                                        userDetails.setImageurl(childsnapshot.child("_source").child("thumb").getValue().toString());
                                    }
                                    userDetails.setName(childsnapshot.child("_source").child("name").getValue().toString());

                                    try {
                                        group_list.add(userDetails);
                                    } catch (NullPointerException e) {
                                    }

                                }

                            }
                            recyclerView.getRecycledViewPool().clear();
                            mAdapter.notifyDataSetChanged();


                        }
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
