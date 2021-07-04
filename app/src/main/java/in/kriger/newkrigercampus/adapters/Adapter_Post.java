package in.kriger.newkrigercampus.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.activities.AnswerListActivity;
import in.kriger.newkrigercampus.activities.ProfileListActivity;
import in.kriger.newkrigercampus.activities.SearchActivity;
import in.kriger.newkrigercampus.classes.DataCounters;
import in.kriger.newkrigercampus.classes.Post;
import in.kriger.newkrigercampus.database.DatabaseHelper;
import in.kriger.newkrigercampus.extras.CustomTypeFaceSpan;
import in.kriger.newkrigercampus.extras.PrefManager;
import in.kriger.newkrigercampus.extras.Processor;
import in.kriger.newkrigercampus.extras.SpecialTextView;
import in.kriger.newkrigercampus.services.KrigerConstants;
import in.kriger.newkrigercampus.services.FireService;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

import static android.content.Context.CLIPBOARD_SERVICE;

public class Adapter_Post extends RecyclerView.Adapter<RecyclerView.ViewHolder>   {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/");
    ProgressDialog progressDialog;
    Activity activity;
    DatabaseHelper db;
    Context context;
    List<Post> posts;
    TextCrawler textCrawler;
    private boolean isLoadingAdded = false;
    private PrefManager preferenceManager;


    public Adapter_Post(Context context, List<Post> postlist, Activity activity) {
        this.context = context;
        this.posts = postlist;
        this.db = new DatabaseHelper(context);
        this.activity = activity;
        textCrawler = new TextCrawler();
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case ITEM:
                viewHolder = new postViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.question_layout, parent, false));
                break;


            case LOADING:
                viewHolder = new loadingViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.dialog_progress, parent, false));
                break;


        }
        preferenceManager = new PrefManager(context);


        return viewHolder;


    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder1, final int position) {

        final Post model = posts.get(position);


        switch (getItemViewType(position)) {
            case ITEM:


                final postViewHolder holder = (postViewHolder) holder1;

                final ArrayList<String> uidlist = new ArrayList<>();


                View view = holder.layout_question.getChildAt(3);

                if (view.getId() == R.id.layout_urlpreview) {
                    view.setVisibility(View.GONE);
                }

                holder.question_post.setText(model.getText());

                holder.question_username.setTextColor(Color.parseColor("#000000"));


                if (holder.question_post.getHyperlinks().size() > 0) {
                    holder.question_post.setOnHyperlinkClickListener(new Function2<TextView, String, Unit>() {
                        @Override
                        public Unit invoke(TextView textView, String s) {
                            if (!s.startsWith("http://") && !s.startsWith("https://")) {
                                s = "http://" + s;
                            }
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                            context.startActivity(browserIntent);
                            return null;
                        }
                    });

                    if (model.getImage_url() == null) {

                            TextCrawler textCrawler = new TextCrawler();

                            textCrawler.makePreview(new LinkPreviewCallback() {
                                @Override
                                public void onPre() {
                                    LayoutInflater inflater = LayoutInflater.from(context);
                                    View child = inflater.inflate(R.layout.layout_urlpreview, null);
                                    holder.layout_question.addView(child, 3);
                                }

                                @Override
                                public void onPos(SourceContent sourceContent, boolean b) {
                                    try {

                                        View view = holder.layout_question.getChildAt(3);

                                        TextView title = view.findViewById(R.id.sname);

                                        title.setText(sourceContent.getTitle());
                                        TextView description = view.findViewById(R.id.sheadline);
                                        description.setText(sourceContent.getDescription());
                                        if (sourceContent.getImages().size() > 0) {
                                            ImageView imageView = view.findViewById(R.id.imageButton_dp);
                                            Glide
                                                    .with(context)
                                                    .load(sourceContent.getImages().get(0))
                                                    .into(imageView);

                                            view.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    holder.question_post.setText(model.getText());
                                                    String s = holder.question_post.getHyperlinks().get(0);
                                                    if (!s.startsWith("http://") && !s.startsWith("https://")) {
                                                        s = "http://" + s;
                                                    }
                                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                                                    context.startActivity(browserIntent);
                                                }
                                            });



                                        } else {
                                            holder.layout_question.removeViewAt(3);


                                        }
                                        ImageButton imageButton = view.findViewById(R.id.imageButton_cancel);
                                        imageButton.setVisibility(View.GONE);

                                    } catch (NullPointerException e) {
                                        holder.layout_question.removeViewAt(3);

                                    }

                                }
                            }, holder.question_post.getHyperlinks().get(0));


                    }
                }
                if (holder.question_post.getMentions().size() > 0) {
                    KrigerConstants.postRef.child(model.getDocument_id()).child("mention_uid").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {

                                String[] separated = dataSnapshot.getValue().toString().split(",");

                                uidlist.addAll(Arrays.asList(separated));
                                holder.question_post.setMentionEnabled(true);
                                holder.question_post.setOnMentionClickListener(new Function2<TextView, String, Unit>() {
                                    @Override
                                    public Unit invoke(TextView textView, String s) {

                                        List<String> mentions = holder.question_post.getMentions();
                                        for (int i = 0; i < mentions.size(); i++) {
                                            if (mentions.get(i).equals(s)) {
                                                if (!uidlist.get(i).equals("1")) {

                                                        Intent intent = new Intent(context, ProfileListActivity.class);
                                                        intent.putExtra("user_id", uidlist.get(i));
                                                        context.startActivity(intent);

                                                }

                                            }
                                        }
                                        return null;
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

                if (holder.question_post.getHashtags().size() > 0) {
                    holder.question_post.setOnHashtagClickListener(new Function2<TextView, String, Unit>() {
                        @Override
                        public Unit invoke(TextView textView, String s) {
                            Intent intent = new Intent(context, SearchActivity.class);
                            intent.putExtra("search_text", s);
                            context.startActivity(intent);
                            return null;
                        }
                    });
                }


                try {
                    if (model.getText().length() > 150) {
                        String text = model.getText().substring(0, 150) + " ... ";
                        String see_more = context.getResources().getString(R.string.see_more);
                        holder.question_post.setText(Html.fromHtml(text + see_more));
                    }

                } catch (NullPointerException e) {

                }


                ArrayList<Integer> counter = new ArrayList<>();


                try {
                    counter = db.getPostCounter(model.getDocument_id());
                    holder.textView_like_num.setText(String.valueOf(counter.get(0)));
                    holder.textView_comment_num.setText(String.valueOf(counter.get(1)));
                    holder.textView_views_num.setText(String.valueOf(counter.get(2)));
                } catch (IndexOutOfBoundsException e) {

                } catch (IllegalArgumentException e) {

                }

                KrigerConstants.post_counterRef.child(model.getDocument_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("count_likes").exists()) {
                            holder.textView_like_num.setText(dataSnapshot.child("count_likes").getValue().toString());
                            db.updateCounterPost(model.getDocument_id(), Post.COLUMN_COUNTLIKE, Integer.valueOf(dataSnapshot.child("count_likes").getValue().toString()));
                        } else {
                            holder.textView_like_num.setText("0");
                        }
                        if (dataSnapshot.child("count_comments").exists()) {
                            holder.textView_comment_num.setText(dataSnapshot.child("count_comments").getValue().toString());
                            db.updateCounterPost(model.getDocument_id(), Post.COLUMN_COUNTCOMMENT, Integer.valueOf(dataSnapshot.child("count_comments").getValue().toString()));
                        } else {
                            holder.textView_comment_num.setText("0");
                        }
                        if (dataSnapshot.child("count_views").exists()) {
                            holder.textView_views_num.setText(dataSnapshot.child("count_views").getValue().toString());
                            db.updateCounterPost(model.getDocument_id(), Post.COLUMN_COUNTVIEWS, Integer.valueOf(dataSnapshot.child("count_views").getValue().toString()));
                        } else {
                            holder.textView_views_num.setText("0");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                try {
                    String value = db.checkLikePostValue(model.getDocument_id());
                    if (value.equals("true")) {
                        holder.imageButton_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_app_like_r, 0, 0, 0);
                    } else {
                        holder.imageButton_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_app_like, 0, 0, 0);

                    }

                } catch (NullPointerException e) {
                    KrigerConstants.post_likeRef.child(model.getDocument_id()).child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                db.updateLikePost(model.getDocument_id(), Post.COLUMN_ISLIKE, "true");

                                holder.imageButton_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_app_like_r, 0, 0, 0);
                            } else {
                                db.updateLikePost(model.getDocument_id(), Post.COLUMN_ISLIKE, "false");

                                holder.imageButton_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_app_like, 0, 0, 0);
                            }

                            FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("Post_View").child(model.getDocument_id()).child(user.getUid()).setValue(FireService.getToday());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

                if (db.checkDataCounterValue(model.getUid(), DataCounters.CONNECTIONS) || db.checkDataCounterValue(model.getUid(), DataCounters.CONNECTREQUESTSENT) || model.getUid().equals(user.getUid())) {
                    holder.button_connect.setVisibility(View.GONE);
                }


                //display date and month for current year
                //display date month and year for previous year
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);

                try {
                    if (model.getTimestamp().contains(String.valueOf(year))) {
                        holder.question_date.setText(Processor.timestamp(model.getTimestamp()));
                    } else {
                        holder.question_date.setText(Processor.timestamp(model.getTimestamp()) + ", " + model.getTimestamp().substring(0, 4));
                    }

                } catch (NullPointerException e) {
                }

                holder.btn_tag.setVisibility(View.GONE);
                holder.btn_tag.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/impact.ttf"));
                if (db.checkDataCounterValue(model.getUid(), DataCounters.INFLUENCER)) {
                    holder.btn_tag.setVisibility(View.VISIBLE);
                    holder.btn_tag.setText("INFLUENCER");
                    holder.btn_tag.setBackgroundResource(R.drawable.button_background_red);
                    holder.btn_tag.setTextColor(Color.parseColor("#FF0000"));

                }
                if (db.checkDataCounterValue(model.getUid(), DataCounters.CONNECTOR)) {
                    holder.btn_tag.setVisibility(View.VISIBLE);
                    holder.btn_tag.setText("CONNECTOR");
                    holder.btn_tag.setBackgroundResource(R.drawable.button_background_green);
                    holder.btn_tag.setTextColor(Color.parseColor("#a4c639"));

                }
                if (db.checkDataCounterValue(model.getUid(), DataCounters.EXPERT)) {
                    holder.btn_tag.setVisibility(View.VISIBLE);
                    holder.btn_tag.setText("EXPERT");
                    holder.btn_tag.setBackgroundResource(R.drawable.button_background_blue);
                    holder.btn_tag.setTextColor(Color.parseColor("#3994e4"));
                }




                if (model.getImage_url() != null) {

                    try {
                        holder.loading_circle.setVisibility(View.VISIBLE);
                        holder.image_post.setVisibility(View.VISIBLE);
                        RequestOptions requestOption = new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


                        Glide.with(context).load(model.getImage_url())
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .apply(requestOption)
                                .into(holder.image_post);


                    } catch (NullPointerException e) {

                    }


                } else {
                    holder.image_post.setVisibility(View.GONE);
                    holder.loading_circle.setVisibility(View.GONE);
                    holder.image_post.setImageDrawable(null);
                }

                if (model.getPdf_url() != null) {
                    try {
                        holder.cardViewPdf.setVisibility(View.VISIBLE);
                        holder.layout_pdf.setVisibility(View.VISIBLE);
                        holder.textView_pdf.setVisibility(View.VISIBLE);
                        holder.textView_pdf.setText("Click here to open");

                    } catch (NullPointerException e) {

                    }
                } else {
                    holder.cardViewPdf.setVisibility(View.GONE);
                    holder.layout_pdf.setVisibility(View.GONE);
                    holder.textView_pdf.setVisibility(View.GONE);
                }

                holder.cardViewPdf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent();
                        intent.setType(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(model.getPdf_url()));
                        context.startActivity(intent);

                    }
                });


                FireService.showdetails(context, holder.question_username, holder.bio, holder.imageView_dp, holder.btn_tag, model.getUid());

                //report post and delete post
                holder.show_popup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    @SuppressLint("RestrictedApi")
                    public void onClick(View v) {

                        PopupMenu popup = new PopupMenu(context, v);
                        MenuInflater inflater = popup.getMenuInflater();
                        if (/*(authority) ||*/ (user.getUid().equals(model.getUid()))) {
                            inflater.inflate(R.menu.menu_editposts_authority, popup.getMenu());
                        } else {
                            inflater.inflate(R.menu.menu_editposts, popup.getMenu());
                        }
                        final Boolean mute = db.checkDataCounterValue(model.getDocument_id(), DataCounters.MUTENOTIFICATIONS);
                        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
                        final String format = s.format(new Date());
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                switch (item.getItemId()) {
                                    case R.id.report_post:
                                        KrigerConstants.report_postRef.child("post").child(model.getDocument_id()).child(user.getUid()).setValue(format).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toasty.custom(context, "Post reported", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                                        true).show();
                                            }
                                        });
                                        break;
                                    case R.id.delete_post:

                                        deleteItem(position, model.getDocument_id());
                                        break;

                                    case R.id.mute_post:

                                        if (mute) {
                                            db.removeMuteValue(model.getDocument_id());
                                            KrigerConstants.mute_notificationRef.child(user.getUid()).child("post").child(model.getDocument_id()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toasty.custom(context, "Post unmuted", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                                            true).show();
                                                }
                                            });

                                        } else {
                                            db.insertAllCounters(new ArrayList<String>(Arrays.asList(model.getDocument_id())), DataCounters.MUTENOTIFICATIONS);
                                            KrigerConstants.mute_notificationRef.child(user.getUid()).child("post").child(model.getDocument_id()).setValue(format).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toasty.custom(context, "Post muted", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                                            true).show();

                                                }
                                            });
                                        }


                                }
                                return false;
                            }
                        });

                        Menu menu = popup.getMenu();
                        for (int i = 0; i < menu.size(); i++) {
                            MenuItem mi = menu.getItem(i);
                            if (i == menu.size() - 1) {
                                if (mute) {
                                    mi.setTitle("Unmute Post");
                                    mi.setIcon(R.drawable.ic_volume_up_black_24dp);
                                } else {
                                    mi.setTitle("Mute Post");
                                    mi.setIcon(R.drawable.ic_volume_off_black_24dp);
                                }
                            }

                            applyFontToMenuItem(mi);
                        }

                        MenuPopupHelper menuHelper = new MenuPopupHelper(context, (MenuBuilder) popup.getMenu(), v);
                        menuHelper.setForceShowIcon(true);
                        menuHelper.setGravity(Gravity.END);
                        menuHelper.show();

                    }
                });


//
                holder.question_post.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, AnswerListActivity.class);
                        intent.putExtra("post_id", model.getDocument_id());
                        context.startActivity(intent);


                    }
                });

                holder.vlc_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, AnswerListActivity.class);
                        intent.putExtra("post_id", model.getDocument_id());
                        context.startActivity(intent);


                    }
                });


                holder.question_post.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ClipboardManager cManager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                        ClipData cData = ClipData.newPlainText("text", model.getText());
                        cManager.setPrimaryClip(cData);
                        Toasty.custom(context, "Text copied", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                true).show();
                        return true;
                    }
                });


                // post share
                holder.imageButton_share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        try {
                            if (model.getText().length() > 150) {
                                holder.question_post.setText(model.getText());
                            }

                        } catch (NullPointerException e) {

                        }

                        progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("Loading..."); // Setting Message
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                        progressDialog.show(); // Display Progress Dialog
                        progressDialog.setCancelable(false);
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    Thread.sleep(2000);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                progressDialog.dismiss();
                            }
                        }).start();

                        String uid = user.getUid();
                        String link = "https://kriger.in/?invitedby=" + uid;
                        FirebaseDynamicLinks.getInstance().createDynamicLink()
                                .setLink(Uri.parse(link))
                                .setDomainUriPrefix("https://kriger.page.link")
                                .setAndroidParameters(
                                        new DynamicLink.AndroidParameters.Builder("in.kriger.krigercampus")
                                                .setMinimumVersion(1)
                                                .build())/*.setIosParameters(
                new DynamicLink.IosParameters.Builder("com.example.ios")
                        .setAppStoreId("123456789")
                        .setMinimumVersion("1.0.1")
                        .build())*/
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


                                        Bitmap bit = Bitmap.createBitmap(holder.cardView.getWidth(), holder.cardView.getHeight(), Bitmap.Config.ARGB_8888);

                                        Canvas c = new Canvas(bit);
                                        holder.cardView.draw(c);


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
                                        String post_text = model.getText();
                                        try {
                                            post_text = model.getText().substring(0, 150);
                                        } catch (StringIndexOutOfBoundsException e) {

                                        }
//

                                        String text;

                                        if (preferenceManager.getAccountType() == 0){
                                            text =  "Learner\n"+ post_text + " ..." + "\nRead more : " + shortDynamicLink.getShortLink().toString() +
                                                    "\n" +
                                                    "Sign up to\n" +
                                                    "Connect with Students, Teachers, Colleges, Coaching \n" +
                                                    "Purchase Study material, Classes, Practice papers\n" +
                                                    "Make or join pan INDIA study groups";
                                        }else if (preferenceManager.getAccountType() == 1){
                                             text =  "Educator\n"+ post_text + " ..." + "\nRead more : " + shortDynamicLink.getShortLink().toString() +
                                                    "\n" +
                                                    "Sign up to\n" +
                                                    "Connect with Students, Teachers, Colleges, Coaching \n" +
                                                    "Purchase Study material, Classes, Practice papers\n" +
                                                    "Make or join pan INDIA study groups";
                                        }else{
                                             text =  "Corporate\n"+ post_text + " ..." + "\nRead more : " + shortDynamicLink.getShortLink().toString() +
                                                    "\n" +
                                                    "Sign up to\n" +
                                                    "Connect with Students, Teachers, Colleges, Coaching \n" +
                                                    "Purchase Study material, Classes, Practice papers\n" +
                                                    "Make or join pan INDIA study groups";
                                        }


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




                if (model.getImage_url() != null) {

                    holder.image_post.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, AnswerListActivity.class);
                            intent.putExtra("post_id", model.getDocument_id());
                            context.startActivity(intent);

                        }
                    });

                }

                holder.imageButton_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(context, AnswerListActivity.class);
                        intent.putExtra("post_id", model.getDocument_id());
                        context.startActivity(intent);


                    }
                });


                holder.imageButton_like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            holder.imageButton_like.setEnabled(false);

                            if (db.checkLikePostValue(model.getDocument_id()).equals("true")) {

                                db.updateLikePost(model.getDocument_id(), Post.COLUMN_ISLIKE, "false");
                                db.updateLikePost(model.getDocument_id(), Post.COLUMN_COUNTLIKE, String.valueOf(Integer.valueOf(holder.textView_like_num.getText().toString()) - 1));

                                holder.imageButton_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_app_like, 0, 0, 0);

                                holder.textView_like_num.setText(String.valueOf(Integer.valueOf(holder.textView_like_num.getText().toString()) - 1));

                                KrigerConstants.post_likeRef.child(model.getDocument_id()).child(user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        holder.imageButton_like.setEnabled(true);
                                    }
                                });

                            } else {
                                db.updateLikePost(model.getDocument_id(), Post.COLUMN_ISLIKE, "true");

                                db.updateLikePost(model.getDocument_id(), Post.COLUMN_COUNTLIKE, String.valueOf(Integer.valueOf(holder.textView_like_num.getText().toString()) + 1));
                                holder.textView_like_num.setText(String.valueOf(Integer.valueOf(holder.textView_like_num.getText().toString()) + 1));

                                holder.imageButton_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_app_like_r, 0, 0, 0);

                                KrigerConstants.post_likeRef.child(model.getDocument_id()).child(user.getUid()).setValue(FireService.getToday()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        holder.imageButton_like.setEnabled(true);
                                    }
                                });

                            }
                        } catch (NullPointerException e) {

                        }

                    }
                });

                holder.button_connect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SimpleDateFormat s = new SimpleDateFormat(context.getString(R.string.date_format));
                        final String format = s.format(new Date());
                        KrigerConstants.user_suggestionRef.child(user.getUid()).child(model.getUid()).removeValue();
                       KrigerConstants.inivitationRef.child(model.getUid()).child(user.getUid()).setValue(format);
                        db.insertAllCounters(new ArrayList<String>(Arrays.asList(model.getUid())), DataCounters.CONNECTREQUESTSENT);
                        KrigerConstants.sent_connectionRef.child(user.getUid()).child(model.getUid()).setValue(format).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toasty.custom(context, "Invitation sent", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                        true).show();
                                holder.button_connect.setVisibility(View.GONE);
                            }
                        });

                    }
                });


                break;

            case LOADING:
                break;


        }


    }





    public void addLoadingFooter() {
        isLoadingAdded = true;

    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

    }




    @Override
    public int getItemCount() {
        return posts == null ? 0 : posts.size();

    }

    @Override
    public int getItemViewType(int position) {

        return (position == posts.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/roboto.light.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypeFaceSpan("", font, Color.BLACK), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    private void deleteItem(final int position, String document_id) {

        db.deletePost(document_id);

           KrigerConstants.postRef.child(document_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    posts.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, posts.size());
                }
            });

        Toasty.custom(context, "Post deleted", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                true).show();

    }

    public long getItemId(int position) {
        return position;
    }




    //layout for post
    public class postViewHolder extends RecyclerView.ViewHolder {


        TextView show_likes;
        View itemview;
        RecyclerView suggestion_view;
        SpecialTextView question_post;
        private TextView question_username, question_date,  textView_like_num, textView_comment_num, textView_views_num, textView_section, textView_pdf;
        private Button imageButton_like, imageButton_comment, imageButton_share;
        private LinearLayout layout_question, vlc_view, layout_pdf;
        private ImageView imageView_dp, image_post;
        private ImageButton show_popup;
        private CardView cardView, cardViewPdf;
        private TextView bio;
        private ImageView button_connect;
        private Context context;
        private ProgressBar loading_circle;
        private Button btn_tag;



        public postViewHolder(final View itemView) {
            super(itemView);

            this.itemview = itemView;
            context = itemView.getContext();
            question_post = itemView.findViewById(R.id.textView_post);
            question_username = itemView.findViewById(R.id.textView_username);
            question_date = itemView.findViewById(R.id.textView_date);
            imageButton_like = itemView.findViewById(R.id.imagebutton_like);
            imageButton_comment = itemView.findViewById(R.id.imagebutton_comment);
            imageButton_share = itemView.findViewById(R.id.imagebutton_share);
            textView_like_num = itemView.findViewById(R.id.textview_like_num);
            textView_comment_num = itemView.findViewById(R.id.textview_comment_num);
            textView_views_num = itemView.findViewById(R.id.textview_views_num);
            show_popup = itemView.findViewById(R.id.imgbutton_popup);
            image_post = itemView.findViewById(R.id.image_post);

            bio = itemView.findViewById(R.id.textView_bio);
            show_likes = itemView.findViewById(R.id.show_likes);
            cardView = itemView.findViewById(R.id.card_view);
            cardViewPdf = itemView.findViewById(R.id.card_view_pdf);
            textView_pdf = itemView.findViewById(R.id.textView_pdf);

            button_connect = itemView.findViewById(R.id.button_connect);

            imageView_dp = itemView.findViewById(R.id.profile_photo);
            layout_question = itemView.findViewById(R.id.layout_question);
            layout_pdf = itemView.findViewById(R.id.layout_pdf);
            vlc_view = itemView.findViewById(R.id.vlc_view);
            suggestion_view = itemView.findViewById(R.id.suggestion_view1);

            loading_circle = itemView.findViewById(R.id.loading);

            btn_tag = itemView.findViewById(R.id.btn_tag);



        }


    }

    protected class loadingViewHolder extends RecyclerView.ViewHolder {

        public loadingViewHolder(View itemView) {
            super(itemView);
        }
    }









}
