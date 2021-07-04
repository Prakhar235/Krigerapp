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

import in.kriger.newkrigercampus.adapters.UserSearchAdapter;
import in.kriger.newkrigercampus.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserSearch extends Fragment {

    String search_text;

    String key1,key2;

    RecyclerView recyclerView;

    private UserSearchAdapter mAdapter;

    ArrayList<String> user_list = new ArrayList<>();

    TextView searching;

    String store_search;

    ArrayList<HashMap<String,String>> content_list = new ArrayList<HashMap<String,String>>();


    public UserSearch() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_search, container, false);

        search_text = getArguments().getString("search_text");

        recyclerView =  rootView.findViewById(R.id.recycler_view);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(getContext());

        searching = rootView.findViewById(R.id.textview_searching);


        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new UserSearchAdapter(user_list,getContext(),content_list);
        recyclerView.setAdapter(mAdapter);


        return  rootView;
    }


    public void startSearch(String text){
        user_list.clear();
        mAdapter.notifyDataSetChanged();

        searching.setText("Searching...");
        searching.setVisibility(View.VISIBLE);

        store_search = text.trim();

        mAdapter.setSearch_text(text.trim());

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/from" , 0);
        childUpdates.put("/size", 30);
        childUpdates.put("/index","firebase1");
        childUpdates.put("/type","user");
        childUpdates.put("/body/query/multi_match/query/",text.trim().toLowerCase()+"*");
        childUpdates.put("/body/query/multi_match/fields/0/","name");
        childUpdates.put("/body/query/multi_match/fields/1/","headline");
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            try {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }catch (NullPointerException e){

            }
        }

        key1 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").push().getKey();
        FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("search").child("request").child(key1).updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listensearch();
            }
        });
    }

    private void listensearch(){

        FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("search").child("response").child(key1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                user_list.clear();



                if (dataSnapshot.exists()) {
                    try {
                        if (dataSnapshot.child("hits").child("total").getValue().toString().equals("0")) {

                        } else {
                            searching.setVisibility(View.GONE);

                            for (DataSnapshot childsnapshot : dataSnapshot.child("hits").child("hits").getChildren()) {

                                    try {
                                        user_list.add(childsnapshot.child("_id").getValue().toString());
                                        HashMap<String, String> map = new HashMap<>();
                                        map.put("key", "1");
                                        content_list.add(map);
                                    } catch (NullPointerException e) {

                                    }


                            }

                        }
                    }catch (NullPointerException e){

                    }
                }

                startSearch2();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void startSearch2(){

        HashMap<String,Object> map = new HashMap<>();

        map.put("index","firebase4");
        map.put("type","userdetail");
        map.put("q",store_search+"*");



            try {

                View view = getActivity().getCurrentFocus();
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }catch (NullPointerException e){

            }


        key2 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").push().getKey();
        FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("search").child("request").child(key2).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listensearch2();
            }
        });
    }

    private void listensearch2(){

        FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("search").child("response").child(key2).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                if (dataSnapshot.exists()) {
                    try {
                        if (dataSnapshot.child("hits").child("total").getValue().toString().equals("0")) {
                            searching.setVisibility(View.GONE);

                        } else {
                            searching.setVisibility(View.GONE);

                            for (DataSnapshot childsnapshot : dataSnapshot.child("hits").child("hits").getChildren()) {

                                if (!user_list.contains(childsnapshot.child("_id").getValue().toString())) {
                                    try {
                                        HashMap<String, String> map = new HashMap<>();
                                        for (DataSnapshot childsnap : childsnapshot.child("_source").child("info").getChildren()) {
                                            if (childsnap.hasChildren()) {

                                                for (DataSnapshot childsnap1 : childsnap.getChildren()) {
                                                    for (DataSnapshot childsnap2 : childsnap1.getChildren()) {


                                                        if (childsnap2.getValue().toString().toLowerCase().contains(store_search.toLowerCase())) {
                                                            map.put(childsnap.getKey().toUpperCase(), childsnap2.getValue().toString());
                                                            content_list.add(map);
                                                        }
                                                    }

                                                }

                                            } else {
                                                if (childsnap.getValue().toString().toLowerCase().contains(store_search.toLowerCase())) {
                                                    map.put(childsnap.getKey().toUpperCase(), childsnap.getValue().toString());
                                                    content_list.add(map);
                                                }
                                            }
                                        }
                                        if (map.isEmpty()) {
                                            map.put("key", "1");
                                            content_list.add(map);
                                        }
                                        user_list.add(childsnapshot.child("_id").getValue().toString());
                                    } catch (NullPointerException e) {
                                    }
                                }


                            }
                        }
                    }catch (NullPointerException e){}
                }

                recyclerView.getRecycledViewPool().clear();
                mAdapter.notifyDataSetChanged();

                if (mAdapter.getItemCount() == 0){
                     searching.setText("No Results Found.");
                     searching.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
