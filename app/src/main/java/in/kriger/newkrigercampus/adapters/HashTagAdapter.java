package in.kriger.newkrigercampus.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.activities.AnswerListActivity;
import in.kriger.newkrigercampus.classes.Group_Post;
import in.kriger.newkrigercampus.classes.Post;
import in.kriger.newkrigercampus.extras.CustomTextView;
import in.kriger.newkrigercampus.extras.CustomTypeFaceSpan;
import in.kriger.newkrigercampus.extras.Processor;
import in.kriger.newkrigercampus.services.KrigerConstants;

public class HashTagAdapter extends RecyclerView.Adapter<HashTagAdapter.postViewHolder> {

    private List<Post> postList;
    private List<Group_Post> groupposts;

    Context context;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/");
    String search;


    public HashTagAdapter(List<Post> postList, Context context,List<Group_Post> group_posts) {

        this.postList = postList;
        this.context = context;
        this.groupposts=group_posts;
    }

    @Override
    public postViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.question_layout_search, parent, false);

        return new postViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final postViewHolder holder, final int position) {

        holder.btn_tag.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/impact.ttf"));
        KrigerConstants.postRef.child(postList.get(position).getDocument_id()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    Post model = dataSnapshot.getValue(Post.class);

                    holder.question_string.setText(model.getText());
                    holder.question_string.setTextToHighlight(search);
                    holder.question_string.setTextHighlightColor("#ADD8E6");
                    holder.question_string.setCaseInsensitive(true);
                    holder.question_string.highlight();

                    holder.post_date.setText(Processor.timestamp(model.getTimestamp()));


                    KrigerConstants.user_detailRef.child(model.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {
                                try {
                                    holder.question_username.setText(dataSnapshot.child("name").getValue().toString());
                                    holder.question_username.setTextToHighlight(search);
                                    holder.question_username.setTextHighlightColor("#ADD8E6");
                                    holder.question_username.setCaseInsensitive(true);
                                    holder.question_username.highlight();


                                    holder.headline.setText(dataSnapshot.child("headline").getValue().toString());
                                    holder.headline.setTextToHighlight(search);
                                    holder.headline.setTextHighlightColor("#ADD8E6");
                                    holder.headline.setCaseInsensitive(true);
                                    holder.headline.highlight();


                                    RequestOptions requestOption = new RequestOptions()
                                            .placeholder(R.drawable.default_profile)
                                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


                                    Glide.with(context).load(dataSnapshot.child("thumb").getValue().toString())
                                            //.diskCacheStrategy(DiskCacheStrategy.NONE)
                                            //.transition(DrawableTransitionOptions.withCrossFade())
                                            .apply(requestOption)
                                            .into(holder.imageView_dp);


                                    //Picasso.with(context).load(dataSnapshot.child("thumb").getValue().toString()).placeholder(R.drawable.default_profile).into(holder.imageView_dp);

                                    if(dataSnapshot.child("type").exists()){


                                        try {
                                            for (DataSnapshot childsnapshot : dataSnapshot.child("type").getChildren()){

                                                if(Integer.valueOf(childsnapshot.getValue().toString()) > 4 ){
                                                    holder.question_username.setTextColor(Color.parseColor("#1D85FE"));
                                                    holder.btn_tag.setVisibility(View.VISIBLE);
                                                    holder.btn_tag.setText("EDUCATOR");
                                                    holder.btn_tag.setBackgroundResource(R.drawable.button_background_blue);
                                                    holder.btn_tag.setTextColor(Color.parseColor("#3994e4"));
                                                }
                                            }
                                        } catch (NullPointerException e) {
                                        }


                                    }


                                } catch (NullPointerException e) {
                                    try {
                                        postList.remove(position);
                                        notifyItemRemoved(position);
                                    } catch (IndexOutOfBoundsException exception) {
                                    }

                                }

                            }else{
                                try {
                                    postList.remove(position);
                                    notifyItemRemoved(position);
                                } catch (IndexOutOfBoundsException exception) {
                                }
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            try {

                                Intent intent = new Intent(context, AnswerListActivity.class);
                                intent.putExtra("post_id", postList.get(position).getDocument_id());
                                context.startActivity(intent);
                            }catch (IndexOutOfBoundsException e){
                                Toasty.custom(context, "Post removed by user.", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_SHORT, false,
                                        true).show();

                            }

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void setSearch(String search) {
        this.search = search;
    }


    public static class postViewHolder extends RecyclerView.ViewHolder {


        private CustomTextView question_string,question_username, question_date, textView_follow, textView_like, textView_answers, textView_section;
        private Button imageButton_like, imageButton_comment, imageButton_share;
        private LinearLayout layout_question;
        private ImageView imageView_dp, image_post;
        private TextView post_date;

        private ImageButton show_popup;
        private CardView cardView;

        private ImageView imageView_star;


        private CustomTextView headline;
        private ImageView button_connect;
        TextView show_likes;
        private Context context;

        View itemview;
        private Button btn_tag;


        public postViewHolder(final View itemView) {
            super(itemView);

            this.itemview = itemView;
            context = itemView.getContext();
            question_string = (CustomTextView) itemView.findViewById(R.id.textView_question);
            question_username = (CustomTextView) itemView.findViewById(R.id.textView_username);
            //question_date = (CustomTextView) itemView.findViewById(R.id.textView_date);
            //imageButton_like = (Button) itemView.findViewById(R.id.imageButton_like);
            imageButton_comment = (Button) itemView.findViewById(R.id.imagebutton_comment);
            imageButton_share = (Button) itemView.findViewById(R.id.imagebutton_share);
            //textView_like = (TextView) itemView.findViewById(R.id.textview_follow);
            //textView_answers = (TextView) itemView.findViewById(R.id.textview_answer);
            //textView_follow = (TextView) itemView.findViewById(R.id.textview_follow);
            show_popup = (ImageButton) itemView.findViewById(R.id.imgbutton_popup);
            image_post = (ImageView) itemView.findViewById(R.id.image_post);
            imageView_star = (ImageView) itemView.findViewById(R.id.imageview_star);
            post_date = (TextView) itemView.findViewById(R.id.post_date);

            headline =  itemView.findViewById(R.id.textView_headline);
            show_likes = (TextView) itemView.findViewById(R.id.show_likes);
            cardView = (CardView) itemView.findViewById(R.id.card_view);

            button_connect = (ImageView) itemView.findViewById(R.id.button_connect);

            imageView_dp = (ImageView) itemView.findViewById(R.id.imageButton_dp);
            layout_question = (LinearLayout) itemView.findViewById(R.id.layout_question);
            btn_tag = (Button)itemView.findViewById(R.id.btn_tag);


        }
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/roboto.light.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypeFaceSpan("", font, Color.BLACK), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }
}