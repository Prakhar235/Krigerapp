package in.kriger.newkrigercampus.bottomfragments;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import in.kriger.newkrigercampus.activities.ProfileListActivity;
import in.kriger.newkrigercampus.activities.SearchActivity;
import in.kriger.newkrigercampus.adapters.Adapter_Notification;
import in.kriger.newkrigercampus.classes.Notification;
import in.kriger.newkrigercampus.database.DatabaseHelper;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.extras.PrefManager;
import in.kriger.newkrigercampus.services.KrigerConstants;
import in.kriger.newkrigercampus.utils.PaginationScrollListener;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Notifications extends Fragment {


    private ProgressBar loading;

    RecyclerView recyclerView;

    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private TextView textView_notifications;
    private PrefManager prefManager;


    private FirebaseRecyclerAdapter<Notification, Adapter_Notification.notificationViewHolder> adapter_notifications;



    Adapter_Notification adapter_notification;

    public List<Notification> notificationlist = new ArrayList<>();

    DatabaseHelper db;
    EditText text_search;
    ImageView btn_text_search,imageView_profile;



    String startDate = null;
    private boolean isLoading = false;


    public Notifications() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(in.kriger.newkrigercampus.R.layout.fragment_notification, container, false);

        loading =  rootView.findViewById(R.id.loading);
        prefManager = new PrefManager(getContext());


        textView_notifications =  rootView.findViewById(in.kriger.newkrigercampus.R.id.textview_notification);


        recyclerView =  rootView.findViewById(in.kriger.newkrigercampus.R.id.recycler_view);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(getContext());


        //Clicking Search in soft Keyboard
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

        //Click on Search Button
        btn_text_search =  rootView.findViewById(R.id.btn_text_search);
        btn_text_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                intent.putExtra("search_text", text_search.getText().toString());
                startActivity(intent);

            }
        });

        imageView_profile =  rootView.findViewById(R.id.profile_photo);
        imageView_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), ProfileListActivity.class);
                intent.putExtra("user_id", user.getUid());
                startActivity(intent);

            }
        });

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        startDate = String.valueOf(System.currentTimeMillis());
        db = new DatabaseHelper(getContext());



        adapter_notification = new Adapter_Notification(notificationlist,getContext());

        recyclerView.setAdapter(adapter_notification);

        recyclerView.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                generateQuery(null,String.valueOf(db.getFirstNotificationTimestamp() - 1));
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });


        checkDatabase();


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





        return rootView;

    }





    private void checkDatabase(){


        if (db.getNotificationCount() == 0){
            generateQuery();

        }else{
            setAdapter();
            generateQuery(String.valueOf(db.getLastNotificationTimestamp() +1),null);

        }
    }

    private void generateQuery(String startText,String endText){

        Query postQuery;

        if (endText == null){

            postQuery = KrigerConstants.notificationRef.child(user.getUid()).child("other").orderByChild("timestamp").startAt(Double.valueOf(startText));
        }else {

            postQuery = KrigerConstants.notificationRef.child(user.getUid()).child("other").orderByChild("timestamp").endAt(Double.valueOf(endText)).limitToLast(10);
        }

        getItems(postQuery);



    }

    private void generateQuery(){
        getItems(KrigerConstants.notificationRef.child(user.getUid()).child("other").limitToLast(10));
    }


    private void setAdapter(){
        notificationlist.clear();
        notificationlist.addAll(db.getAllNotification());
        adapter_notification.notifyDataSetChanged();
        loading.setVisibility(View.GONE);
        isLoading = false;
    }





    private void getItems(Query postQuery){

        postQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    List<Notification> notifications = new ArrayList<>();
                    for (DataSnapshot childsnapshot : dataSnapshot.getChildren()) {

                        Notification notification = childsnapshot.getValue(Notification.class);
                        try {
                            notifications.add(notification);

                        }catch (NullPointerException e){}


                    }

                    try {
                        db.insertAllNotifications(notifications);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setAdapter();
                }else{
                    textView_notifications.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.GONE);
                }





            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}