package in.kriger.newkrigercampus.bottomfragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.activities.ConnectionTipsActivity;
import in.kriger.newkrigercampus.activities.ContactListActivity;
import in.kriger.newkrigercampus.activities.HomeActivity;
import in.kriger.newkrigercampus.activities.KrigerListActivity;
import in.kriger.newkrigercampus.activities.ProfileListActivity;
import in.kriger.newkrigercampus.activities.SearchActivity;
import in.kriger.newkrigercampus.classes.DataCounters;
import in.kriger.newkrigercampus.classes.UserId;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.database.DatabaseHelper;
import in.kriger.newkrigercampus.extras.PrefManager;
import in.kriger.newkrigercampus.services.KrigerConstants;
import in.kriger.newkrigercampus.services.FireService;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class Krigers extends Fragment {

    private PrefManager prefManager;
    final DatabaseReference database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/");
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private TextView count_kriger,suggestions_added,invitation_added,connection_added;
    private TextView notinvite, invite_name, invite_headline;
    private LinearLayout isinvite,display_invitation;

    private ImageView imageView_dp;
    RequestQueue requestQueue;



    private RecyclerView recyclerView;

    private TextView no_suggestions;
    ArrayList<String> uidlist = new ArrayList<>();

    private ImageView invite_accept, invite_decline;
    private LinearLayout view_krigers;

    private FirebaseRecyclerAdapter<UserId, suggestionViewHolder> adapter_suggestions;

    private int count = 0;





    private  Button btn_tag;

    Dialog progress;

    EditText text_search;
    Toolbar toolbar;

    ImageView btn_text_search,imageView_profile;

    Integer added_connection;

    private DatabaseHelper db ;
    int connectionsvalue=0;
    String moo;
    String goo;
    JSONArray js;


    public Krigers() {
        // Required empty public constructor
    }
    public void sendjsonrequest(String url)
    {
        JsonObjectRequest Jsonobjectrequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    //getting the whole json object from the response


                    //we have the array named hero inside the object
                    //so here we are getting that json array

                 connection_added.setText(response.getString("connections"));
                  goo=response.getJSONArray("invitations").getJSONObject(0).getString("name");
                  moo=response.getJSONArray("invitations").getJSONObject(0).getString("thumbs");
                    js=response.getJSONArray("suggestions");












                    //  String newuri="http://134.209.157.104:6000/profile/all/"+objectid;
                    //  newjsonrequest(newuri);



                    //now looping through all the elements of the json array
                    //for (int i = 0; i < heroArray.length(); i++) {
                    //getting the json object of the particular index inside the array
                    //     JSONObject heroObject = heroArray.getJSONObject(i);

                    //creating a hero object and giving them the values from json object
                    ///   Hero hero = new Hero(heroobject.getString("cover"), heroObject.getString("name"));
                    //  heroList.add (hero.getImageUrl()) ;
                    //  String objectid=heroobject.getString("")



                    //adding the hero to herolist




                }
                catch(JSONException e)
                {
                    e.printStackTrace();


                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(Jsonobjectrequest);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_krigers, container, false);

        prefManager = new PrefManager(getContext());
        //GUI
        count_kriger =  rootView.findViewById(R.id.count_krigers);
        suggestions_added =  rootView.findViewById(R.id.suggestions_added);
        invitation_added = rootView.findViewById(R.id.invitation_added);
        connection_added =  rootView.findViewById(R.id.connection_added);
        requestQueue= Volley.newRequestQueue(getContext());



        notinvite = rootView.findViewById(R.id.notinvite);
        display_invitation =  rootView.findViewById(R.id.display_invitation);


        recyclerView =  rootView.findViewById(R.id.recycler_view);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(getContext());
        final AppCompatActivity activity= (AppCompatActivity)getActivity();
        toolbar=rootView.findViewById(R.id.toolbar);


        db = new DatabaseHelper(getContext());




        activity.setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        added_connection = getArguments().getInt("added_connections");

        if(added_connection != 0){
            connection_added.setVisibility(View.VISIBLE);
            connection_added.setText(String.valueOf(added_connection));
        }
        else{
            connection_added.setVisibility(View.GONE);
        }
        loadPrefs();
        sendjsonrequest("http://134.209.157.104:6000/kriger/0/"+objectid);





        Bundle bundle = this.getArguments();
        if (bundle != null) {
            count = bundle.getInt("key");

        }

        if (count == 1) {
            final AlertDialog alertDialog = new AlertDialog.Builder(
                    getContext()).create();

            // Setting Dialog Title
            alertDialog.setTitle("Expand your network!");

            // Setting Dialog Message
            alertDialog.setMessage("Connect with the suggested us_cities.");

            // Setting Icon to Dialog
            alertDialog.setIcon(R.drawable.tick);

            // Setting OK Button
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });
            // Showing Alert Message
            alertDialog.show();

        }

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        no_suggestions = (TextView) rootView.findViewById(R.id.no_suggestions);






        //CLicking Adding Contacts
        final LinearLayout add_contacts = (LinearLayout) rootView.findViewById(R.id.layout_addcontacts);
        add_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ContactListActivity.class);
                startActivity(intent);

            }
        });


        //CLicking connection guidelines
        LinearLayout connection_guidelines = (LinearLayout) rootView.findViewById(R.id.layout_connection_tips);

        connection_guidelines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), ConnectionTipsActivity.class);
                startActivity(intent);

            }
        });









        //Viewing List of Krigers
        view_krigers = rootView.findViewById(R.id.view_krigers);
        view_krigers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                added_connection = 0;
                Intent intent = new Intent(getContext(), KrigerListActivity.class);
                startActivity(intent);
                connection_added.setVisibility(View.GONE);
            }
        });

        //Clicking Search in soft Keyboard
        text_search = rootView.findViewById(R.id.text_search);
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


        //List of Krigers
        getconnections();

        //List of Invtations
        getinvitations();

        //Adapter for Suggestions
        Query suggestionRef = KrigerConstants.user_suggestionRef.child(user.getUid());

        suggestionRef.keepSynced(true);

        FirebaseRecyclerOptions<UserId> options =
                new FirebaseRecyclerOptions.Builder<UserId>()
                        .setQuery(suggestionRef, UserId.class)
                        .setLifecycleOwner(this)
                        .build();

        adapter_suggestions = new FirebaseRecyclerAdapter<UserId, suggestionViewHolder>(options) {
            @Override
            public suggestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new suggestionViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.suggestion_layout, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final suggestionViewHolder holder, int position, @NonNull UserId model) {

                try {
                    FireService.showdetails(getContext(), holder.sname, holder.sheadline, holder.imageView_dp, holder.btn_tag, adapter_suggestions.getRef(position).getKey());

                } catch (DatabaseException e) {
                    Log.d("Key", adapter_suggestions.getRef(position).getKey());
                }

                final int random = new Random().nextInt(2) + 0;
                if (random == 0){
                    holder.btn_icon.setImageResource(R.color.Offline);
                }else{
                    holder.btn_icon.setImageResource(R.color.Online);
                }
                holder.button_connect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
                        final String format = s.format(new Date());


                        try {

                            progress = new Dialog(getActivity());
                            progress.setContentView(R.layout.dialog_progress);
                            progress.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                            progress.show();


                            final String recipient = adapter_suggestions.getRef(holder.getAdapterPosition()).getKey();
                            adapter_suggestions.getRef(holder.getAdapterPosition()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    database.child("Invitation").child(recipient).child(user.getUid()).setValue(format).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            database.child("Sent_Connection").child(user.getUid()).child(recipient).setValue(format).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progress.dismiss();

                                                    Toasty.custom(getContext(), "Invitation sent", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_SHORT, false,
                                                            true).show();
                                                }
                                            });
                                        }
                                    });

                                }
                            });

                        }catch (IndexOutOfBoundsException e){

                        }




                    }
                });

                //remove suggestions
                holder.sremove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progress = new Dialog(getActivity());
                        progress.setContentView(R.layout.dialog_progress);
                        progress.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                        progress.show();

                        adapter_suggestions.getRef(holder.getAdapterPosition()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progress.dismiss();
                            }
                        });
                    }
                });


                holder.sname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), ProfileListActivity.class);
                        intent.putExtra("user_id", adapter_suggestions.getRef(holder.getAdapterPosition()).getKey());
                        startActivity(intent);
                    }
                });

                suggestions_added.setText(String.valueOf(adapter_suggestions.getItemCount()));


            }

            @Override
            public void onDataChanged() {

                no_suggestions.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
                suggestions_added.setVisibility(getItemCount() == 0 ? View.GONE : View.VISIBLE);

            }



        };


        recyclerView.setAdapter(adapter_suggestions);





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


        KrigerConstants.user_counterRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {

                    if (dataSnapshot.exists()){

                        if (dataSnapshot.child("count_connectRequest").exists()){
                            invitation_added.setVisibility(View.VISIBLE);
                            invitation_added.setText(dataSnapshot.child("count_connectRequest").getValue().toString());
                        }

                    }

                    if(dataSnapshot.child("count_connectRequest").getValue().toString().equals("0")){
                        invitation_added.setVisibility(View.GONE);
                    }




                }catch (NullPointerException e){

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        return rootView;
    }
    String objectid;
    private void loadPrefs() {
        SharedPreferences sp = getActivity().getSharedPreferences("CHECKBOX", 0);
        objectid = sp.getString("_objectid", "");

    }


    public static class suggestionViewHolder extends RecyclerView.ViewHolder {

        private TextView sname;
        private TextView sheadline;
        private ImageView button_connect;
        private ImageView imageView_dp,btn_icon;
        ImageView sremove;
        private Button btn_tag;

        public suggestionViewHolder(View itemView) {
            super(itemView);

            sname =  itemView.findViewById(R.id.sname);
            sheadline =  itemView.findViewById(R.id.sheadline);

            button_connect =  itemView.findViewById(R.id.sconnect);
            imageView_dp =  itemView.findViewById(R.id.imageButton_dp);

            sremove =  itemView.findViewById(R.id.sremove);
            btn_icon =  itemView.findViewById(R.id.btn_icon);

            btn_tag =  itemView.findViewById(R.id.btn_tag);

        }
    }


    public void getconnections() {

        DatabaseReference connectRef = KrigerConstants.user_counterRef.child(user.getUid()).child("count_connections");

        connectRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    count_kriger.setText(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

   //Pending invitation
    public void getinvitations() {

        notinvite.setVisibility(View.GONE);

        Query query = KrigerConstants.inivitationRef.child(user.getUid()).limitToLast(2);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    if (dataSnapshot.exists()) {
                        notinvite.setVisibility(View.GONE);
                        int i =0;
                        uidlist.clear();

                        for (DataSnapshot childsnap : dataSnapshot.getChildren()) {

                            View childLayout = getLayoutInflater().inflate(R.layout.invitation_layout,display_invitation
                                    , false);
                            childLayout.setId(i);

                            display_invitation.addView(childLayout);

                            isinvite =  childLayout.findViewById(R.id.ifinvite);
                            imageView_dp =  childLayout.findViewById(R.id.imageButton_dp);

                            //this is line
                            invite_name =  childLayout.findViewById(R.id.invite_name);
                            invite_headline =  childLayout.findViewById(R.id.invite_headline);

                            btn_tag =  childLayout.findViewById(R.id.btn_tag);
                            invite_decline =  childLayout.findViewById(R.id.invite_decline);
                            invite_accept =  childLayout.findViewById(R.id.invite_accept);

                            isinvite.setVisibility(View.VISIBLE);

                            invite_accept.setId(i);
                            invite_decline.setId(i);
                            invite_name.setId(i);


                            uidlist.add(childsnap.getKey());
                            FireService.showdetails(getContext(), invite_name, invite_headline, imageView_dp, btn_tag, childsnap.getKey());
                            i++;
                            Glide.with(getActivity())
                                    .load(moo)
                                    .into(imageView_dp);
                            invite_name.setText(goo);



                            //Accepting Invitation
                            invite_accept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(final View view) {
                                    display_invitation.removeAllViews();
                                    KrigerConstants.inivitationRef.child(user.getUid()).child(uidlist.get(view.getId())).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            display_invitation.removeAllViews();
                                            HashMap<String,Object> map = new HashMap<>();
                                            map.put("timestamp",FireService.getToday());
                                            map.put("own","true");

                                            final HashMap<String,Object> map1 = new HashMap<>();
                                            map1.put("timestamp",FireService.getToday());
                                            map1.put("own","false");
                                            KrigerConstants.connectionRef.child(user.getUid()).child(uidlist.get(view.getId())).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    KrigerConstants.connectionRef.child(uidlist.get(view.getId())).child(user.getUid()).updateChildren(map1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            db.insertAllCounters(new ArrayList<String>(Arrays.asList(uidlist.get(view.getId()))),DataCounters.CONNECTIONS);

                                                            display_invitation.removeAllViews();

                                                            Toasty.custom(getContext(), "Invitation Accepted.", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_SHORT, false,
                                                                    true).show();
                                                            getinvitations();

                                                        }
                                                    });

                                                }
                                            });

                                        }
                                    });
                                }
                            });


                            //Declining Invitation
                            invite_decline.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    display_invitation.removeAllViews();

                                    KrigerConstants.inivitationRef.child(user.getUid()).child(uidlist.get(view.getId())).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                display_invitation.removeAllViews();

                                                Toasty.custom(getContext(), "Invitation Declined.", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_SHORT, false,
                                                        true).show();
                                                getinvitations();
                                            }
                                        });

                                }
                            });

                            //Opening User Profile
                            invite_name.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getContext(), ProfileListActivity.class);
                                    intent.putExtra("user_id",uidlist.get(v.getId()));
                                    startActivity(intent);
                                }
                            });


                        }
                    } else {
                        notinvite.setVisibility(View.VISIBLE);

                    }

                }catch (IllegalStateException e){

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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
