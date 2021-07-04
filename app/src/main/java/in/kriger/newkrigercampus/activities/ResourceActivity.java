package in.kriger.newkrigercampus.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.flowlayout.FlowLayout;
import java.math.BigDecimal;
import java.util.HashMap;
import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.classes.Enquiry;
import in.kriger.newkrigercampus.classes.Resource;
import in.kriger.newkrigercampus.extras.Processor;
import in.kriger.newkrigercampus.services.KrigerConstants;
import in.kriger.newkrigercampus.services.FireService;

public class ResourceActivity extends AppCompatActivity {

    TextView name, description,shares,views,owner,timestamp,total_enq,type,class_name,fees,fees_type,start_time,end_time,enquiry,enquiry_added,rate_text,review_text;
    Button btn_enquire;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ImageView resource_image;
    FlowLayout flow_sub,flow_exams;
    Button ratings;
    RatingBar rate;

    Long count_review = Long.valueOf(0);
    RecyclerView recyclerView;
    float rating = (float) 0.0;

    static boolean enquire = false;

    int value_enquiry = 0;

    LinearLayout layout_enquiry,timing_layout,layout_total_enq;



    private FirebaseRecyclerAdapter<Enquiry, enquiryViewHolder> adapter_enquiry;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource);

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        final String key = getIntent().getExtras().getString("resource_id");

        resource_image = findViewById(R.id.resource_image);
        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        shares = findViewById(R.id.shares);
        type = findViewById(R.id.type);
        views = findViewById(R.id.views);
        timestamp = findViewById(R.id.timestamp);
        total_enq = findViewById(R.id.total_enq);
        owner = findViewById(R.id.owner);
        btn_enquire = findViewById(R.id.btn_enquire);
        flow_exams = findViewById(R.id.flow_exams);
        flow_sub = findViewById(R.id.flow_sub);
        rate = findViewById(R.id.rate);
        class_name = findViewById(R.id.class_type);
        fees = findViewById(R.id.fees);
        fees_type = findViewById(R.id.fees_type);
        enquiry = findViewById(R.id.enquiry);
        enquiry_added = findViewById(R.id.enquiry_added);
        rate_text = findViewById(R.id.rate_text);
        review_text = findViewById(R.id.review_text);
        layout_enquiry = findViewById(R.id.layout_enquiry);
        timing_layout = findViewById(R.id.timing_layout);
        layout_total_enq = findViewById(R.id.layout_total_enq);



        ratings = findViewById(R.id.rating);
        ratings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewDialog alert = new ViewDialog();
                alert.showDialog(ResourceActivity.this, key);
            }
        });

        final String[] exams = getResources().getStringArray(R.array.list_exams);
        final String[] subs = getResources().getStringArray(R.array.list_sub);
        final String[] classlist = getResources().getStringArray(R.array.list_edu);
        final String[] fees_typelist = getResources().getStringArray(R.array.list_fees_type);
        final String[] list_start_time = getResources().getStringArray(R.array.list_start_time);
        final String[] list_end_time = getResources().getStringArray(R.array.list_end_time);

        try{
            value_enquiry = getIntent().getExtras().getInt("value_enquiry");

        }catch (NullPointerException e){

        }


        btn_enquire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enquire = true;
                EnquireDialog enquireDialog =  new EnquireDialog();
                enquireDialog.showDialog(ResourceActivity.this,name.getText().toString(),key,owner.getText().toString());
            }
        });


        if(getIntent().getExtras().getBoolean("button_visibility")){
            btn_enquire.setVisibility(View.GONE);
            ratings.setVisibility(View.GONE);

        }
        String push_key = KrigerConstants.resource_viewRef.child(key).push().getKey();
        KrigerConstants.resource_viewRef.child(key).child(push_key).child(user.getUid()).setValue(FireService.getToday());



        KrigerConstants.resourceRef.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final Resource resource = dataSnapshot.getValue(Resource.class);


                    if (resource.getRate()!=null){
                        rate.setRating(Float.parseFloat(String.valueOf(resource.getRate())));
                        rate_text.setText(resource.getRate().toString());
                        rating = Float.valueOf(resource.getRate());
                    }else {
                        rate.setRating(Float.parseFloat("0.00"));
                        rate_text.setText("0.00");
                    }


                }else {
                    Toasty.custom(getApplicationContext(), "Resource doesn't exist", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        KrigerConstants.resourceRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final Resource resource = dataSnapshot.getValue(Resource.class);
                    name.setText(resource.getName());
                    description.setText(resource.getDescription());
                    timestamp.setText(Processor.timestamp(resource.getTimestamp()));


                    class_name.setText(classlist[resource.getClass_type().intValue()]);
                    fees.setText(String.valueOf(resource.getFees()) +" Per "+ fees_typelist[resource.getFees_type().intValue()]);

                    if (dataSnapshot.child("time").exists()) {

                        for (DataSnapshot childsnapshot : dataSnapshot.child("time").getChildren()) {
                            if (childsnapshot.exists()) {
                                View childLayout = getLayoutInflater().inflate(R.layout.layout_displaytiming,
                                        timing_layout, false);

                                timing_layout.addView(childLayout);
                                start_time = childLayout.findViewById(R.id.start_time);
                                end_time = childLayout.findViewById(R.id.end_time);
                                start_time.setText(list_start_time[Integer.valueOf(childsnapshot.child("start_time").getValue().toString())] + " to "+ list_start_time[Integer.valueOf(childsnapshot.child("end_time").getValue().toString())]);
                            }

                        }
                    }


                    String[] categories;

                        LinearLayout layout1 = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_flow_item, flow_exams, false);
                        TextView textView = layout1.findViewById(R.id.name);
                        Integer value = Integer.valueOf(String.valueOf(resource.getExam()));
                        textView.setText(exams[value]);
                        ImageView imageView = layout1.findViewById(R.id.buttonExitIcon);
                        imageView.setId(value);
                        imageView.setVisibility(View.GONE);
                        flow_exams.addView(layout1);


                        LinearLayout layout2 = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_flow_item, flow_sub, false);
                        TextView textView2 = layout2.findViewById(R.id.name);
                        Integer value2 = Integer.valueOf(String.valueOf(resource.getSubject()));
                        textView2.setText(subs[value2]);
                        ImageView imageView2 = layout2.findViewById(R.id.buttonExitIcon);
                        imageView2.setId(value2);
                        imageView2.setVisibility(View.GONE);
                        flow_sub.addView(layout2);


                    categories = getResources().getStringArray(R.array.list_resource_type);
                    type.setText(categories[Integer.valueOf(String.valueOf(resource.getType()))]);
                    try {

                        RequestOptions requestOption = new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


                        Glide.with(getApplicationContext()).load(resource.getThumb())
                                .apply(requestOption)
                                .into(resource_image);



                    } catch (NullPointerException e) {
                        imageView.setImageResource(R.drawable.default_profile);
                    }


                    KrigerConstants.user_detailRef.child(resource.getOwner()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                owner.setText(dataSnapshot.child("name").getValue().toString());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                   owner.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(getApplicationContext(), ProfileListActivity.class);
                            intent.putExtra("user_id", resource.getOwner());
                            startActivity(intent);


                        }
                    });

                }else {
                    Toasty.custom(getApplicationContext(), "Resource doesn't exist", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        KrigerConstants.resource_counterRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("count_shares").exists()){
                    shares.setText(dataSnapshot.child("count_shares").getValue().toString());
                }else{
                    shares.setText("0");
                }

                if (dataSnapshot.child("count_views").exists()){
                    views.setText(dataSnapshot.child("count_views").getValue().toString());
                }else{
                    views.setText("0");
                }

                if (dataSnapshot.child("count_reviews").exists()){
                    count_review = (Long)dataSnapshot.child("count_reviews").getValue();
                    review_text.setText(count_review.toString() + " Reviews");
                }else{
                    review_text.setText("0 Reviews");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        setTitle("Resource Listing");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(getApplicationContext());



        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        Query enquiryRef = KrigerConstants.resource_enquiryRef.child(key);

        enquiryRef.keepSynced(true);

        FirebaseRecyclerOptions<Enquiry> options =
                new FirebaseRecyclerOptions.Builder<Enquiry>()
                        .setQuery(enquiryRef, Enquiry.class)
                        .setLifecycleOwner(this)
                        .build();

        adapter_enquiry = new FirebaseRecyclerAdapter<Enquiry, enquiryViewHolder>(options) {
            @Override
            public enquiryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new enquiryViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_enquiry_list, parent, false));
            }


            @Override
            public void onDataChanged() {

            }

            @Override
            protected void onBindViewHolder(@NonNull final enquiryViewHolder enquiryViewHolder, final int i, @NonNull final Enquiry enquiry) {

                if ((adapter_enquiry.getItemCount() - i)<=value_enquiry){
                    enquiryViewHolder.imageView_new.setVisibility(View.VISIBLE);
                }

                KrigerConstants.user_detailRef.child(enquiry.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            enquiryViewHolder.name.setText(dataSnapshot.child("name").getValue().toString());

                            try {
                                RequestOptions requestOption = new RequestOptions()
                                        .placeholder(R.drawable.default_profile)
                                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

                                Glide.with(getApplicationContext()).load(dataSnapshot.child("thumb").getValue().toString())
                                        .apply(requestOption)
                                        .into(enquiryViewHolder.imageView);

                            }catch (NullPointerException e){
                                enquiryViewHolder.imageView.setImageResource(R.drawable.default_profile);
                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                enquiryViewHolder.email.setText(enquiry.getEmail());
                enquiryViewHolder.contact.setText(enquiry.getContact());
                enquiryViewHolder.timestamp.setText(Processor.timestamp(enquiry.getTimestamp()));

                enquiryViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(),ProfileListActivity.class);
                        intent.putExtra("user_id",enquiry.getUid());
                        startActivity(intent);
                    }
                });

                total_enq.setText(String.valueOf(adapter_enquiry.getItemCount()));



            }

        };



        if (value_enquiry != 0){

            enquiry_added.setVisibility(View.VISIBLE);
            enquiry_added.setText(String.valueOf(value_enquiry));
        }

        if(getIntent().getExtras().getBoolean("visibility")){
            btn_enquire.setVisibility(View.GONE);
            ratings.setVisibility(View.GONE);
            layout_enquiry.setVisibility(View.VISIBLE);
            layout_total_enq.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(adapter_enquiry);
        }




    }

    @Override
    public void onBackPressed() {
        //this is only needed if you have specific things
        //that you want to do when the user presses the back button.
        /* your specific things...*/
        super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                this.finish();
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
  //submit rating
    public class ViewDialog {

        public void showDialog(Activity activity, final String key){
            final Dialog dialog = new Dialog(activity, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_rate_resource);

            final RatingBar ratingBar = dialog.findViewById(R.id.ratingBar);
            final EditText review = dialog.findViewById(R.id.review);

            final int[] old_rating = {0};

            ImageButton btn_close = dialog.findViewById(R.id.btn_close);
            btn_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            ratingBar.setStepSize(1);

            KrigerConstants.resource_reviewRef.child(key).child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        old_rating[0] = ((Long) dataSnapshot.child("rate").getValue()).intValue();
                        ratingBar.setRating(Float.valueOf(dataSnapshot.child("rate").getValue().toString()));
                        review.setText(dataSnapshot.child("review").getValue().toString());
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            Button btn_submit = dialog.findViewById(R.id.btn_submit);
            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ratingBar.getRating() != 0.0){
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("rate",ratingBar.getRating());
                        map.put("review",review.getText().toString());
                        map.put("timestamp",FireService.getToday());

                        float new_value = 0;

                        if (old_rating[0] != 0){

                            new_value = (((count_review * rating) + ratingBar.getRating() - old_rating[0])/(count_review));
                            new_value = BigDecimal.valueOf(new_value).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
                        }else{

                            new_value = ((count_review * rating + ratingBar.getRating())/(count_review+1));
                            new_value = BigDecimal.valueOf(new_value).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
                        }

                        KrigerConstants.resourceRef.child(key).child("rate").setValue(String.valueOf(new_value));
                        KrigerConstants.resource_reviewRef.child(key).child(user.getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toasty.custom(getApplicationContext(), "Review Submitted", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                        true).show();
                                dialog.dismiss();
                            }
                        });
                    }else {
                        Toasty.custom(getApplicationContext(), "Please submit rating", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                true).show();

                    }
                }
            });



            dialog.show();

        }
    }

//enquiry Implementation
    public static class EnquireDialog {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        public void showDialog(final Activity activity, final String resourcename, final String key, String ownername){

            final Dialog dialog = new Dialog(activity, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_enquiry);



            final TextView resource_name = dialog.findViewById(R.id.resource_name);
            TextView name = dialog.findViewById(R.id.name);
            final EditText email = dialog.findViewById(R.id.email);
            final EditText contact = dialog.findViewById(R.id.mobile_number);
            TextView authorize = dialog.findViewById(R.id.authorize);
            Button btn_submit = dialog.findViewById(R.id.btn_submit);


            name.setText(user.getDisplayName());
            email.setText(user.getEmail());
            email.setSelection(email.getText().length());
            ImageButton btn_close = dialog.findViewById(R.id.btn_close);
            btn_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });


            if(enquire){
                resource_name.setText(ownername + "\n\nFor the enquiry of\n\n" + resourcename);
                enquire = false;
            }else{
                KrigerConstants.user_detailRef.child(ownername).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String oname = dataSnapshot.child("name").getValue().toString();
                            resource_name.setText(oname + "\n\nFor the enquiry of\n\n" + resourcename);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }



            KrigerConstants.userRef.child(user.getUid()).child("contact").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        contact.setText(dataSnapshot.getValue().toString());
                        contact.setSelection(contact.getText().length());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            authorize.setText("I authorize them to contact me");
            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (email.getText().length() == 0){
                        Toasty.custom(activity,"Email can't be empty", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                true).show();
                    }else if (contact.getText().length() != 10){
                        Toasty.custom(activity,"Contact should be of 10 digits", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                true).show();
                    }else {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("timestamp", FireService.getToday());
                        map.put("contact", contact.getText().toString());
                        map.put("email", email.getText().toString());
                        map.put("uid", user.getUid());
                        String pushkey = KrigerConstants.resource_enquiryRef.child(key).push().getKey();
                        KrigerConstants.resource_enquiryRef.child(key).child(pushkey).updateChildren(map);
                        KrigerConstants.user_listRef.child(user.getUid()).child("enquiry").child(key).setValue(FireService.getToday());
                        Toasty.custom(activity, "Your enquiry has been submitted", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                true).show();
                        dialog.dismiss();
                    }

                }
            });

            dialog.show();











        }
    }


    //UI of a item
    public static class enquiryViewHolder extends  RecyclerView.ViewHolder{

        private TextView name;
        private TextView email;
        private TextView contact;
        private ImageView imageView;
        private TextView timestamp;

        private Button imageView_new;



        public enquiryViewHolder(View itemView) {
            super(itemView);

            name =  itemView.findViewById(R.id.name);
            email =  itemView.findViewById(R.id.email);
            contact =  itemView.findViewById(R.id.contact);
            imageView = itemView.findViewById(R.id.imageButton_dp);
            timestamp = itemView.findViewById(R.id.timestamp);
            imageView_new = itemView.findViewById(R.id.imageview_new);

        }
    }
}
