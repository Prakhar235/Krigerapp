package in.kriger.newkrigercampus.bottomfragments;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.activities.ContactListActivity;
import in.kriger.newkrigercampus.activities.NewPostActivity;
import in.kriger.newkrigercampus.activities.ProfileListActivity;
import in.kriger.newkrigercampus.activities.SearchActivity;
import in.kriger.newkrigercampus.activities.SuggestionActivity;
import in.kriger.newkrigercampus.adapters.Adapter_Post;
import in.kriger.newkrigercampus.classes.GPSTracker;
import in.kriger.newkrigercampus.classes.Post;
import in.kriger.newkrigercampus.database.DatabaseHelper;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.extras.PrefManager;
import in.kriger.newkrigercampus.services.KrigerConstants;
import in.kriger.newkrigercampus.utils.PaginationScrollListener;


import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {


    private static final String TAG = Home.class.getSimpleName();


    private RecyclerView recyclerView;

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/");


    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    SwipeRefreshLayout mSwipeRefreshLayout;


    ImageView imageView_profile;



    SharedPreferences pref;
    SharedPreferences.Editor editor;
    GPSTracker gps;

    EditText text_search;
    ImageView btn_text_search;

    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 123;

    FloatingActionMenu menu;

    FloatingActionButton add_post, suggestions, refer;

    View mShadowView;

    private DatabaseHelper db;

    public List<Post> postlist = new ArrayList<>();

    public Adapter_Post adapter_post;




    private boolean isLoading = false;


    private PrefManager prefManager;


    TextView no_post;

    FrameLayout homeframe;






    public Home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_home, container, false);


        pref = getContext().getSharedPreferences("Preference", 0);
        //editor = pref.edit();


        prefManager = new PrefManager(getContext());

        no_post = rootView.findViewById(R.id.no_postdisplay);

        homeframe =  rootView.findViewById(R.id.home_frame_layout);
        recyclerView =  rootView.findViewById(R.id.recycler_view);

        mSwipeRefreshLayout =  rootView.findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {



                mSwipeRefreshLayout.setRefreshing(false);
                db.deleteAllPosts();
                checkDatabase();

            }
        });







        final LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter_post = new Adapter_Post(getContext(), postlist, getActivity());

        recyclerView.setAdapter(adapter_post);


        db = new DatabaseHelper(getContext());


        if (db.getPostCount() == 0) {

            no_post.setVisibility(View.VISIBLE);

        }


        try {
            if (String.valueOf(getArguments().getChar("is_open")).equals("y")) {
                setAdapter();
            }
        } catch (NullPointerException e) {
            setAdapter();
        }

        recyclerView.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;

                generateQuery(null, String.valueOf(Long.valueOf(db.getFirstPostTimestamp()) - 1));



            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });


        imageView_profile =  rootView.findViewById(R.id.profile_photo);
        imageView_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(getContext(), ProfileListActivity.class);
                    intent.putExtra("user_id",user.getUid());
                    startActivity(intent);

            }
        });



        //search user,post,group

        text_search =  rootView.findViewById(R.id.text_search);
        text_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        Intent intent = new Intent(getContext(), SearchActivity.class);
                        intent.putExtra("search_text", text_search.getText().toString());
                        startActivity(intent);

                    return true;
                }
                return false;
            }
        });




        btn_text_search =  rootView.findViewById(R.id.btn_text_search);
        btn_text_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(getContext(), SearchActivity.class);
                    intent.putExtra("search_text", text_search.getText().toString());
                    startActivity(intent);

            }
        });

        menu =  rootView.findViewById(R.id.menu);
        menu.setClosedOnTouchOutside(true);

        add_post =  rootView.findViewById(R.id.menu_item2_post);

        mShadowView =  rootView.findViewById(R.id.shadowView);

        menu.setOnMenuToggleListener(
                new FloatingActionMenu.OnMenuToggleListener() {
                    @Override
                    public void onMenuToggle(boolean opened) {
                        if(opened){
                            mShadowView.setVisibility(View.VISIBLE);

                        }else{
                            mShadowView.setVisibility(View.GONE);
                        }
                    }
                });




        // add a new post
        add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(getContext(), NewPostActivity.class);
                    startActivity(intent);


            }
        });


        // submit feedback
        suggestions = (FloatingActionButton) rootView.findViewById(R.id.menu_item1_suggestions);
        suggestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.i("random",String.valueOf(rand_int1));

                    Intent intent = new Intent(getContext(), SuggestionActivity.class);
                    startActivity(intent);

            }
        });


        //social score
        refer = (FloatingActionButton) rootView.findViewById(R.id.menu_item1_refer);
        refer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            Intent intent = new Intent(getContext(), ContactListActivity.class);
            startActivity(intent);

            }
        });




      /*      if (prefManager.getProfileImageUrl() == null) {
                DatabaseReference profileRef = mDatabase.child("User_Detail").child(user.getUid()).child("thumb");
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

       */





        return rootView;

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getpermission();
            } else {

                Toasty.custom(getContext(), "Until you grant the permission, we cannot help you!", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_SHORT, false,
                        true).show();
            }
        }
    }


    public void getpermission() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {

            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            String formattedDate = df.format(new Date());

            String last_date = pref.getString("loc-date", formattedDate + "1");

            if (!last_date.equals(formattedDate)) {
                editor.putString("loc-date", formattedDate);
                if (gps.canGetLocation()) {

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    KrigerConstants.userRef.child(user.getUid()).child("location").setValue(String.valueOf(latitude) + "+" + String.valueOf(longitude));

                } else {
                }
            }

        }

    }




    public void checkDatabase() {

        if (db.getPostCount() == 0) {
            generateQuery();

        } else {
            setAdapter();
            generateQuery(String.valueOf(Long.valueOf(db.getLastPostTimestamp()) + 1), null);

        }

    }

    private void generateQuery(String startText, String endText) {

        Query postQuery;

        if (endText == null) {
            if (Long.valueOf(startText.substring(6, 7)) > 3) {
                startText = startText.substring(0, 6) + "2" + startText.substring(7);
            }
            postQuery = KrigerConstants.postRef.orderByChild("timestamp").startAt(startText);
        } else {
            if (Long.valueOf(endText.substring(6, 7)) > 3) {
                endText = endText.substring(0, 6) + "2" + endText.substring(7);
            }
            postQuery = KrigerConstants.postRef.orderByChild("timestamp").endAt(endText).limitToLast(20);
        }

        getItems(postQuery);

    }

    private void generateQuery() {

        getItems(KrigerConstants.postRef.limitToLast(20));
    }


    private void setAdapter() {
        adapter_post.removeLoadingFooter();
        postlist.clear();
        postlist.addAll(db.getAllPosts());
        adapter_post.notifyDataSetChanged();
        no_post.setVisibility(View.GONE);
            adapter_post.addLoadingFooter();

        isLoading = false;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_addpost, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.edit_text:
                Intent intent = new Intent(getContext(), NewPostActivity.class);
                startActivity(intent);

                return true;


        }

        return false;
    }


    private void getItems(Query postQuery) {

        postQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                List<Post> posts = new ArrayList<>();
                for (DataSnapshot childsnapshot : dataSnapshot.getChildren()) {
                    Post post = childsnapshot.getValue(Post.class);
                    post.setDocument_id(childsnapshot.getKey());
                    posts.add(post);

                }

                try {
                    db.insertAllPost(posts);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setAdapter();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    public void startDatabase() {

        checkDatabase();
    }
}










