package in.kriger.newkrigercampus.resourcefragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spannable;
import android.text.SpannableString;
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
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.nex3z.flowlayout.FlowLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.classes.Resource;
import in.kriger.newkrigercampus.extras.CustomTypeFaceSpan;
import in.kriger.newkrigercampus.extras.Processor;
import in.kriger.newkrigercampus.services.KrigerConstants;
import in.kriger.newkrigercampus.services.FireService;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyEnquires extends Fragment {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Context context;
    RecyclerView recyclerView;

    TextView no_resouces;

    private FirebaseRecyclerAdapter<String,resourceViewHolder> adapter_resource ;

    ProgressDialog progressDialog;
    Long count_review = Long.valueOf(0);



    public MyEnquires() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_enquires, container, false);

        recyclerView =  view.findViewById(R.id.recycler_view);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(getContext());

        no_resouces = view.findViewById(R.id.no_resources);


        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        final String[] exams = getResources().getStringArray(R.array.list_exams);
        final String[] subs = getResources().getStringArray(R.array.list_sub);
        final String[] fees_typelist = getResources().getStringArray(R.array.list_fees_type);


        Query resourceRef = KrigerConstants.user_listRef.child(user.getUid()).child("enquiry");

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

                    resourceViewHolder.layoutenquired.setVisibility(View.VISIBLE);
                    resourceViewHolder.enquired.setText(Processor.timestamp(s));



                KrigerConstants.resourceRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final Resource resource = dataSnapshot.getValue(Resource.class);
                        try {
                            resourceViewHolder.popup.setVisibility(View.GONE);
                            resourceViewHolder.sharelayout.setVisibility(View.GONE);
                            resourceViewHolder.name.setText(resource.getName());
                            resourceViewHolder.fee.setText(resource.getFees().toString() +" Per "+ fees_typelist[resource.getFees_type().intValue()]);
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

                                    if (dataSnapshot.child("count_reviews").exists()){
                                        count_review = (Long)dataSnapshot.child("count_reviews").getValue();
                                        resourceViewHolder.review_text.setText(count_review.toString() + " Reviews");
                                    }else{
                                        resourceViewHolder.review_text.setText("0 Reviews");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                           //Share enquiry
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
                                                    String text = post_text + " ..." + "\nRead more : " + shortDynamicLink.getShortLink().toString() +
                                                            "\n" +
                                                            "Sign up to\n" +
                                                            "Connect with Students, Teachers, Colleges, Coaching \n" +
                                                            "Purchase Study material, Classes, Practice papers\n" +
                                                            "Make or join pan INDIA study groups";

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
                        }catch (NullPointerException e){
                            adapter_resource.getRef(i).removeValue();
                        }

                        }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                resourceViewHolder.btn_enquire.setVisibility(View.GONE);

               //Listing delete
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

                                        Toasty.custom(context, "Listing Deleted", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_SHORT, false,
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



            }

        };

        recyclerView.setAdapter(adapter_resource);

        return view ;


    }

    public static class resourceViewHolder extends  RecyclerView.ViewHolder{

        private TextView name, rating, review_text,ownername,enquired;
        private TextView fee;
        private ImageView imageView_dp;
        private Button btn_enquire;
        private ImageButton popup,share;
        private FlowLayout flow_subs;
        private FlowLayout flow_exams;
        RatingBar rate;
        LinearLayout layoutenquired,sharelayout;


        public resourceViewHolder(View itemView) {
            super(itemView);

            name =  itemView.findViewById(R.id.name);
            ownername =  itemView.findViewById(R.id.ownername);
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
            enquired =  itemView.findViewById(R.id.enquired);
            layoutenquired = itemView.findViewById(R.id.layoutenquired);
            sharelayout = itemView.findViewById(R.id.sharelayout);

        }


    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/roboto.light.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypeFaceSpan("", font, Color.BLACK), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }


}
