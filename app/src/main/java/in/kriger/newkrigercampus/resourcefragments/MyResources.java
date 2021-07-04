package in.kriger.newkrigercampus.resourcefragments;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.nex3z.flowlayout.FlowLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.activities.PaymentsActivity;
import in.kriger.newkrigercampus.activities.ResourceActivity;
import in.kriger.newkrigercampus.activities.Terms_ConditionsActivity;
import in.kriger.newkrigercampus.classes.Resource;
import in.kriger.newkrigercampus.database.DatabaseHelper;
import in.kriger.newkrigercampus.extras.CustomTypeFaceSpan;
import in.kriger.newkrigercampus.extras.PrefManager;
import in.kriger.newkrigercampus.extras.Processor;
import in.kriger.newkrigercampus.services.KrigerConstants;
import in.kriger.newkrigercampus.services.FireService;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyResources extends Fragment {


    String userid;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Context context;
    RecyclerView recyclerView;

    TextView no_resouces;

    private FirebaseRecyclerAdapter<String, resourceViewHolder> adapter_resource;

    ProgressDialog progressDialog;

    TextView text_coins;

    int resource_value = 0;
    Long count_review = Long.valueOf(0);
    int old_count = 0;
    int new_count = 0;
    int valid_count = 0;

    ArrayList<String> new_timestamp = new ArrayList<>();
    ArrayList<String> old_timestamp = new ArrayList<>();

    HashMap<Integer, Integer> map = new HashMap<>();

    DatabaseHelper db;

    int value_enquiry = 0;

    Boolean is_first = false;
    private PrefManager preferenceManager;


    public MyResources(Context context) {
        this.context = context;
        db = new DatabaseHelper(context);
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_myresources, container, false);

        preferenceManager = new PrefManager(context);
        FloatingActionButton fab = view.findViewById(R.id.fab);
        text_coins = view.findViewById(R.id.text_coins);
        try {
            Log.d("User", getArguments().get("user_id").toString());
            userid = getArguments().get("user_id").toString();
            if (!userid.equals(user.getUid())) {
                fab.setVisibility(View.GONE);
                text_coins.setVisibility(View.GONE);

            }
        } catch (NullPointerException e) {
            userid = user.getUid();
        }
        final String[] exams = getResources().getStringArray(R.array.list_exams);
        final String[] subs = getResources().getStringArray(R.array.list_sub);
        final String[] fees_typelist = getResources().getStringArray(R.array.list_fees_type);

        Button btn_payments = view.findViewById(R.id.btn_payments);
        btn_payments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PaymentsActivity.class);
                startActivity(intent);
            }
        });

       //Create resource
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (is_first) {
                    final Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                    dialog.setContentView(R.layout.layout_dialog);

                    final TextView textView = (TextView) dialog.findViewById(R.id.dialogue_text);
                    textView.setText("Hello " + user.getDisplayName().split(" ")[0] + "\nOur mission is to support Institutes and make India 100% literate.\nIt's because of Institute like you Kriger Campus exist.\nKindly share your details with our Brand Strategy team.\n It will help us understand your needs and serve you better.\nWe will get back to you.");
                    textView.setTextSize(22);
                    final Button posButton = (Button) dialog.findViewById(R.id.button_positive);
                    posButton.setText("OK");
                    posButton.setVisibility(View.VISIBLE);
                    posButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            dialog.dismiss();
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            String[] recipients = {"payments@kriger.in"};
                            intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Brand Marketplace Registration");
                            intent.putExtra(Intent.EXTRA_TEXT, "Kriger Campus User Name : " + user.getDisplayName().split(" ")[0] +"\n\nI would like to add our offering on Kriger Campus marketplace, increase my brand visibility and start getting more enquiries.\n\nThank You");
                            intent.setType("text/html");
                            intent.setPackage("com.google.android.gm");
                            try {

                                startActivity(Intent.createChooser(intent, "Send mail"));


                            } catch (android.content.ActivityNotFoundException ex) {
                                Toasty.custom(context,"There are no email clients installed", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                        true).show();
                            }





                        }
                    });

                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                    dialog.show();


                } else {
                    if (new_count > adapter_resource.getItemCount()) {

                        Intent intent = new Intent(context, Terms_ConditionsActivity.class);
                        intent.putExtra("timestamp", new_timestamp.get(adapter_resource.getItemCount() / 5));
                        startActivity(intent);
                    } else {
                        Toasty.custom(context, "Please recharge before proceeding", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                true).show();

                    }
                }

            }
        });


        KrigerConstants.transaction_detailRef.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    resource_value = Integer.valueOf(dataSnapshot.child("value").getValue().toString());
                    text_coins.setText(String.valueOf(resource_value * 2000));
                    if (dataSnapshot.child("list").exists()) {
                        for (DataSnapshot childsnap : dataSnapshot.child("list").getChildren()) {
                            if (FireService.diffDates(childsnap.getKey())) {

                                KrigerConstants.transaction_detailRef.child(user.getUid()).child("list").child(childsnap.getKey()).removeValue();
                            } else {
                                if (childsnap.getValue().toString().equals("0")) {
                                    old_timestamp.add(childsnap.getKey());
                                    old_count++;
                                } else if (childsnap.getValue().toString().equals("1")) {
                                    new_timestamp.add(childsnap.getKey());
                                    new_count++;
                                }
                            }

                        }
                    }

                    old_count = old_count * 5;
                    new_count = new_count * 5;
                } else {
                    text_coins.setText("0");
                    KrigerConstants.transaction_historyref.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                is_first = true;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                recyclerView.setAdapter(adapter_resource);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(getContext());

        no_resouces = view.findViewById(R.id.no_resources);


        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        Query resourceRef = KrigerConstants.user_listRef.child(user.getUid()).child("resource").orderByValue();

        resourceRef.keepSynced(true);

        FirebaseRecyclerOptions<String> options =
                new FirebaseRecyclerOptions.Builder<String>()
                        .setQuery(resourceRef, String.class)
                        .setLifecycleOwner(this)
                        .build();

        adapter_resource = new FirebaseRecyclerAdapter<String, resourceViewHolder>(options) {
            @Override
            public resourceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new resourceViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_resource, parent, false));
            }


            @Override
            public void onDataChanged() {
                no_resouces.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            protected void onBindViewHolder(@NonNull final resourceViewHolder resourceViewHolder, final int i, @NonNull String s) {

                final String key = adapter_resource.getRef(i).getKey();
                try {

                    if (userid.equals(user.getUid())) {
                        resourceViewHolder.popup.setVisibility(View.VISIBLE);
                    }

                    KrigerConstants.resourceRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final Resource resource = dataSnapshot.getValue(Resource.class);

                            try {

                                resourceViewHolder.layoutvalid.setVisibility(View.VISIBLE);
                                resourceViewHolder.layoutenq.setVisibility(View.VISIBLE);
                                resourceViewHolder.valid.setText(Processor.timestamp(resource.getValid_till()));
                                if (FireService.diffDates(resource.getValid_till())) {
                                    if (old_count > i) {
                                        resourceViewHolder.btn_activate.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    map.put(i, 1);
                                }

                                resourceViewHolder.btn_activate.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        final String timestamp = old_timestamp.get(map.size() / 5);
                                        KrigerConstants.resourceRef.child(key).child("valid_till").setValue(timestamp).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                KrigerConstants.user_listRef.child(user.getUid()).child("resource").child(key).setValue(timestamp).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toasty.custom(context, "Listing Activated", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                                                true).show();
                                                        resourceViewHolder.btn_activate.setVisibility(View.GONE);
                                                        resourceViewHolder.valid.setText(Processor.timestamp(timestamp));
                                                    }
                                                });
                                            }
                                        });

                                    }
                                });

                                resourceViewHolder.name.setText(resource.getName());
                                resourceViewHolder.fee.setText(resource.getFees().toString() + " Per " + fees_typelist[resource.getFees_type().intValue()]);
                                try {

                                    RequestOptions requestOption = new RequestOptions()
                                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


                                    Glide.with(getContext()).load(resource.getThumb())
                                            .apply(requestOption)
                                            .into(resourceViewHolder.imageView_dp);


                                } catch (NullPointerException e) {
                                    resourceViewHolder.imageView_dp.setImageResource(R.drawable.default_groups);
                                }

                                if (resource.getRate() != null) {
                                    resourceViewHolder.rating.setText((resource.getRate().toString()));
                                    resourceViewHolder.rate.setRating(Float.parseFloat((resource.getRate().toString())));
                                } else {
                                    resourceViewHolder.rate.setRating(Float.parseFloat("0.00"));
                                    resourceViewHolder.rating.setText("0.00");
                                }


                                resourceViewHolder.flow_exams.removeAllViews();
                                resourceViewHolder.flow_subs.removeAllViews();


                                LinearLayout layout1 = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_flow_item, resourceViewHolder.flow_exams, false);
                                TextView textView = layout1.findViewById(R.id.name);
                                textView.setText(exams[Integer.valueOf(String.valueOf(resource.getExam()))]);
                                ImageView imageView = layout1.findViewById(R.id.buttonExitIcon);
                                imageView.setId(Integer.valueOf(String.valueOf(resource.getExam())));
                                imageView.setVisibility(View.GONE);
                                resourceViewHolder.flow_exams.addView(layout1);


                                LinearLayout layout2 = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_flow_item, resourceViewHolder.flow_subs, false);
                                TextView textView2 = layout2.findViewById(R.id.name);
                                textView2.setText(subs[Integer.valueOf(String.valueOf(resource.getSubject()))]);
                                ImageView imageView2 = layout2.findViewById(R.id.buttonExitIcon);
                                imageView2.setId(Integer.valueOf(String.valueOf(resource.getSubject())));
                                imageView2.setVisibility(View.GONE);
                                resourceViewHolder.flow_subs.addView(layout2);

                                KrigerConstants.user_detailRef.child(resource.getOwner()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            resourceViewHolder.ownername.setText(dataSnapshot.child("name").getValue().toString());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                KrigerConstants.resource_counterRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.child("count_reviews").exists()) {
                                            count_review = (Long) dataSnapshot.child("count_reviews").getValue();
                                            resourceViewHolder.review_text.setText(count_review.toString() + " Reviews");
                                        } else {
                                            resourceViewHolder.review_text.setText("0 Reviews");
                                        }
                                        if (dataSnapshot.child("count_enquiries").exists()) {
                                            int count_enquiry = db.getResource(key);
                                            int server_enquiry = ((Long) dataSnapshot.child("count_enquiries").getValue()).intValue();
                                            if (server_enquiry - count_enquiry > 0) {
                                                value_enquiry = server_enquiry - count_enquiry;
                                                resourceViewHolder.enquiry_counter.setVisibility(View.VISIBLE);
                                                resourceViewHolder.enquiry_counter.setText(String.valueOf(value_enquiry));
                                                db.insertResource(key, server_enquiry);
                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                //share resource
                                resourceViewHolder.share.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String pushkey = KrigerConstants.resource_shareRef.child(key).push().getKey();
                                        KrigerConstants.resource_shareRef.child(key).child(pushkey).child(user.getUid()).setValue(FireService.getToday());
                                        try {
                                            if (resourceViewHolder.fee.getText().length() > 150) {
                                                resourceViewHolder.fee.setText(resource.getFees().toString());
                                            }

                                        } catch (NullPointerException e) {

                                        }

                                        progressDialog = new ProgressDialog(context);
                                        progressDialog.setMessage("Loading..."); // Setting Message
                                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                                        progressDialog.show(); // Display Progress Dialog
                                        progressDialog.setCancelable(false);


                                        String uid = user.getUid();
                                        String link = "https://kriger.in/?invitedby=" + uid;
                                        FirebaseDynamicLinks.getInstance().createDynamicLink()
                                                .setLink(Uri.parse(link))
                                                .setDomainUriPrefix("https://kriger.page.link")
                                                .setAndroidParameters(
                                                        new DynamicLink.AndroidParameters.Builder("in.kriger.krigercampus")
                                                                .setMinimumVersion(1)
                                                                .build())
                                                .buildShortDynamicLink()
                                                .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                                                    @Override
                                                    public void onSuccess(ShortDynamicLink shortDynamicLink) {

                                                        View inflatedFrame = LayoutInflater.from(context).inflate(R.layout.layout_share, null);

                                                        RelativeLayout relativeLayout = inflatedFrame.findViewById(R.id.relativelayout_share);


                                                        ImageView post = inflatedFrame.findViewById(R.id.imageview_post);
                                                        ImageView share_bottom = inflatedFrame.findViewById(R.id.bottom_share);
                                                        ImageView share_top = inflatedFrame.findViewById(R.id.top_bar);
                                                        TextView watermark = inflatedFrame.findViewById(R.id.watermark);
                                                        watermark.setText("Shared by : " + user.getDisplayName());


                                                        Bitmap bit = Bitmap.createBitmap(resourceViewHolder.itemView.getWidth(), resourceViewHolder.itemView.getHeight(), Bitmap.Config.ARGB_8888);

                                                        Canvas c = new Canvas(bit);
                                                        resourceViewHolder.itemView.draw(c);


                                                        post.setImageBitmap(bit);

                                                        share_bottom.getLayoutParams().width = bit.getWidth();
                                                        share_top.getLayoutParams().width = bit.getWidth();
                                                        watermark.getLayoutParams().width = bit.getWidth();

                                                        relativeLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                                                        Bitmap b;

                                                        try {
                                                            b = Bitmap.createBitmap(relativeLayout.getMeasuredWidth(), relativeLayout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                                                        } catch (NullPointerException e) {

                                                            b = Bitmap.createBitmap(relativeLayout.getMeasuredWidth(), relativeLayout.getMeasuredHeight(), Bitmap.Config.RGB_565);
                                                        }

                                                        Canvas c1 = new Canvas(b);
                                                        relativeLayout.layout(0, 0, relativeLayout.getMeasuredWidth(), relativeLayout.getMeasuredHeight());
                                                        relativeLayout.draw(c1);


                                                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                                        b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                                                        try {

                                                            File cachePath = new File(context.getCacheDir(), "images");
                                                            cachePath.mkdirs(); // don't forget to make the directory
                                                            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
                                                            b.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                                            stream.close();

                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                        String post_text = resource.getDescription();
                                                        try {
                                                            post_text = resource.getDescription().substring(0, 150);
                                                        } catch (StringIndexOutOfBoundsException e) {

                                                        }
//

                                                        String text;
                                                        if (preferenceManager.getAccountType() == 0){
                                                            text = "learner\n"+post_text + " ..." + "\nRead more : " + shortDynamicLink.getShortLink().toString() +
                                                                    "\n" +
                                                                    "Sign up to\n" +
                                                                    "Connect with Students, Teachers, Colleges, Coaching \n" +
                                                                    "Purchase Study material, Classes, Practice papers\n" +
                                                                    "Make or join pan INDIA study groups";

                                                        }else if (preferenceManager.getAccountType() == 1){
                                                            text = "educator\n"+post_text + " ..." + "\nRead more : " + shortDynamicLink.getShortLink().toString() +
                                                                    "\n" +
                                                                    "Sign up to\n" +
                                                                    "Connect with Students, Teachers, Colleges, Coaching \n" +
                                                                    "Purchase Study material, Classes, Practice papers\n" +
                                                                    "Make or join pan INDIA study groups";

                                                        }else{
                                                            text = "corporate\n"+ post_text + " ..." + "\nRead more : " + shortDynamicLink.getShortLink().toString() +
                                                                    "\n" +
                                                                    "Sign up to\n" +
                                                                    "Connect with Students, Teachers, Colleges, Coaching \n" +
                                                                    "Purchase Study material, Classes, Practice papers\n" +
                                                                    "Make or join pan INDIA study groups";

                                                        }

                                                        progressDialog.dismiss();


                                                        File imagePath = new File(context.getCacheDir(), "images");
                                                        File newFile = new File(imagePath, "image.png");
                                                        Uri contentUri = FileProvider.getUriForFile(context, "in.kriger.krigercampus.fileprovider", newFile);
                                                        Intent shareIntent = new Intent();
                                                        shareIntent.setAction(Intent.ACTION_SEND);
                                                        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                                                        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                                                        shareIntent.setType("image/jpeg");
                                                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                                        try {
                                                            context.startActivity(shareIntent);
                                                        } catch (android.content.ActivityNotFoundException ex) {

                                                        }
                                                    }
                                                });


                                    }
                                });
                            } catch (NullPointerException e) {
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    resourceViewHolder.btn_enquire.setVisibility(View.GONE);

                    resourceViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, ResourceActivity.class);
                            if (value_enquiry != 0) {
                                intent.putExtra("value_enquiry", value_enquiry);
                            }
                            intent.putExtra("visibility", true);
                            intent.putExtra("resource_id", key);
                            startActivity(intent);
                        }
                    });


                    resourceViewHolder.popup.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupMenu popup = new PopupMenu(context, v);
                            MenuInflater inflater = popup.getMenuInflater();

                            inflater.inflate(R.menu.menu_resource, popup.getMenu());

                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {

                                    switch (item.getItemId()) {
                                        case R.id.menu_delete:
                                            adapter_resource.getRef(i).removeValue();
                                            KrigerConstants.resourceRef.child(adapter_resource.getRef(i).getKey()).removeValue();
                                            Toasty.custom(context, "Listing Deleted", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                                    true).show();
                                            break;
                                    }
                                    return false;
                                }
                            });

                            Menu menu = popup.getMenu();
                            for (int i = 0; i < menu.size(); i++) {
                                MenuItem mi = menu.getItem(i);

                                applyFontToMenuItem(mi);
                            }

                            MenuPopupHelper menuHelper = new MenuPopupHelper(context, (MenuBuilder) popup.getMenu(), v);
                            menuHelper.setForceShowIcon(true);
                            menuHelper.setGravity(Gravity.END);
                            menuHelper.show();

                        }
                    });

                } catch (NullPointerException e) {
                }


            }

        };


        return view;
    }

    public static class resourceViewHolder extends RecyclerView.ViewHolder {

        private TextView name, rating, review_text, ownername, valid;
        private TextView fee;
        private ImageView imageView_dp;
        private Button btn_enquire;
        private ImageButton popup, share;
        private FlowLayout flow_subs;
        private FlowLayout flow_exams;
        RatingBar rate;
        LinearLayout layoutvalid, layoutenq;
        private Button btn_activate;
        TextView enquiry_counter;


        public resourceViewHolder(View itemView) {
            super(itemView);

            name =  itemView.findViewById(R.id.name);
            ownername = itemView.findViewById(R.id.ownername);
            fee =  itemView.findViewById(R.id.fee);
            imageView_dp =  itemView.findViewById(R.id.profile_photo);
            btn_enquire =  itemView.findViewById(R.id.btn_enquire);
            share = itemView.findViewById(R.id.btn_share);
            popup = itemView.findViewById(R.id.imgbutton_popup);
            flow_exams = itemView.findViewById(R.id.flow_exams);
            flow_subs = itemView.findViewById(R.id.flow_sub);
            rating = itemView.findViewById(R.id.rating);
            rate = itemView.findViewById(R.id.rate);
            review_text = itemView.findViewById(R.id.review_text);
            valid =  itemView.findViewById(R.id.valid);
            layoutvalid = itemView.findViewById(R.id.layoutvalid);
            layoutenq = itemView.findViewById(R.id.layoutenq);
            btn_activate = itemView.findViewById(R.id.btn_activate);
            enquiry_counter = itemView.findViewById(R.id.enquiry_counter);

        }


    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/roboto.light.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypeFaceSpan("", font, Color.BLACK), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @Override
    public void onResume() {
        value_enquiry = 0;
        super.onResume();
    }
}
