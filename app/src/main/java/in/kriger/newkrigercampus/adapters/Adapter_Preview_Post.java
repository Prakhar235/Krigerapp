package in.kriger.newkrigercampus.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;
import java.util.List;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.activities.LoginActivity;
import in.kriger.newkrigercampus.classes.PreviewPost;
import in.kriger.newkrigercampus.classes.UserDetails;
import in.kriger.newkrigercampus.services.FireService;

public class Adapter_Preview_Post extends RecyclerView.Adapter<Adapter_Preview_Post.postViewHolder>{
    Context context;
    List<PreviewPost> previewposts;
    Activity activity;

    public Adapter_Preview_Post(Context context, List<PreviewPost> previewpostlist,Activity activity) {
        this.context = context;
        this.previewposts = previewpostlist;
        this.activity = activity;
    }

    @NonNull
    @Override
    public postViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        postViewHolder viewHolder = new postViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.question_layout, parent, false));

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull postViewHolder holder, final int position) {


        final PreviewPost model = previewposts.get(position);



        holder.question_username.setText(model.getName());
        holder.bio.setText(model.getHeadline());
        try {
            RequestOptions requestOption = new RequestOptions()
                    .placeholder(R.drawable.default_profile)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


            Glide.with(context).load(model.getImageurl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(requestOption)
                    .into(holder.imageView_dp);

        } catch (NullPointerException e) {
            holder.imageView_dp.setImageResource(R.drawable.default_profile);
        }

        holder.question_post.setText(model.getText());

        try{
            if (model.getText().length()>150) {
                String temp = model.getText().substring(0, 150) + "...  ";
                String see_more = context.getResources().getString(R.string.see_more);
                String text = "<font color='black'>" + temp + "</font><font color='#3994e4'>" + see_more + "</font>";
                holder.question_post.setText(Html.fromHtml(text));
            }

        }catch (NullPointerException e){

        }

        if(position<5){
            try {
                holder.question_date.setText(FireService.getPreviewToday(2));

            }catch (NullPointerException e){}

        }else if(position<10){
            try {
                holder.question_date.setText(FireService.getPreviewToday(4));

            }catch (NullPointerException e){}
        }else if(position<15){
            try {
                holder.question_date.setText(FireService.getPreviewToday(6));

            }catch (NullPointerException e){}
        }else {
            try {
                holder.question_date.setText(FireService.getPreviewToday(8));

            }catch (NullPointerException e){}
        }




        if (model.getImage_url() != null) {
            holder.image_post.setVisibility(View.VISIBLE);
            holder.loading_circle.setVisibility(View.VISIBLE);
            RequestOptions requestOption = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


            Glide.with(context).load(model.getImage_url())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(requestOption)
                    .into(holder.image_post);


        } else {
            holder.image_post.setVisibility(View.GONE);
            holder.loading_circle.setVisibility(View.GONE);
            holder.image_post.setImageDrawable(null);
        }



        try {
            holder.textView_like_num.setText(model.getCount_like());
            holder.textView_comment_num.setText(model.getCount_comment());
            holder.textView_views_num.setText(model.getCount_views());
        }catch (IndexOutOfBoundsException e){

        }
        holder.button_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogBuilder("Kindly register to explore all the feature and connect with 10000+ Indians to make India 100% literate.",2,true,"Yes","No",null);

            }
        });

        holder.question_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogBuilder("Kindly register to explore all the feature and connect with 10000+ Indians to make India 100% literate.",2,true,"Yes","No",null);

            }
        });

        holder.bio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogBuilder("Kindly register to explore all the feature and connect with 10000+ Indians to make India 100% literate.",2,true,"Yes","No",null);

            }
        });
        holder.imageView_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogBuilder("Kindly register to explore all the feature and connect with 10000+ Indians to make India 100% literate.",2,true,"Yes","No",null);

            }
        });
        holder.imageButton_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogBuilder("Kindly register to explore all the feature and connect with 10000+ Indians to make India 100% literate.",2,true,"Yes","No",null);

            }
        });
        holder.imageButton_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogBuilder("Kindly register to explore all the feature and connect with 10000+ Indians to make India 100% literate.",2,true,"Yes","No",null);

            }
        });
        holder.imageButton_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogBuilder("Kindly register to explore all the feature and connect with 10000+ Indians to make India 100% literate.",2,true,"Yes","No",null);

            }
        });
        holder.question_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogBuilder("Kindly register to explore all the feature and connect with 10000+ Indians to make India 100% literate.",2,true,"Yes","No",null);

            }
        });
        holder.show_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogBuilder("Kindly register to explore all the feature and connect with 10000+ Indians to make India 100% literate.",2,true,"Yes","No",null);

            }
        });

        holder.textView_like_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogBuilder("Kindly register to explore all the feature and connect with 10000+ Indians to make India 100% literate.",2,true,"Yes","No",null);

            }
        });
        holder.textView_comment_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogBuilder("Kindly register to explore all the feature and connect with 10000+ Indians to make India 100% literate.",2,true,"Yes","No",null);

            }
        });
        holder.textView_views_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogBuilder("Kindly register to explore all the feature and connect with 10000+ Indians to make India 100% literate.",2,true,"Yes","No",null);

            }
        });
        holder.image_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogBuilder("Kindly register to explore all the feature and connect with 10000+ Indians to make India 100% literate.",2,true,"Yes","No",null);

            }
        });


    }

    @Override
    public int getItemCount() {
        return previewposts.size();
    }


    //layout for post
    public  class postViewHolder extends RecyclerView.ViewHolder {


        private TextView question_post, question_username, question_date,  textView_like_num, textView_comment_num, textView_views_num,textView_section;
        private Button imageButton_like, imageButton_comment, imageButton_share;

        private ImageView imageView_dp, image_post;

        private ImageButton show_popup;



        private TextView bio;
        private ImageView button_connect;
        TextView show_likes;
        private Context context;

        View itemview;

        RecyclerView suggestion_view;
        private ProgressBar loading_circle;




        public postViewHolder(final View itemView) {
            super(itemView);

            this.itemview = itemView;
            context = itemView.getContext();
            question_post =  itemView.findViewById(R.id.textView_post);
            question_username =  itemView.findViewById(R.id.textView_username);
            question_date =  itemView.findViewById(R.id.textView_date);
            imageButton_like =  itemView.findViewById(R.id.imagebutton_like);
            imageButton_comment =  itemView.findViewById(R.id.imagebutton_comment);
            imageButton_share =  itemView.findViewById(R.id.imagebutton_share);
            textView_like_num =  itemView.findViewById(R.id.textview_like_num);
            textView_comment_num =  itemView.findViewById(R.id.textview_comment_num);
            textView_views_num =  itemView.findViewById(R.id.textview_views_num);
            show_popup =  itemView.findViewById(R.id.imgbutton_popup);
            image_post =  itemView.findViewById(R.id.image_post);


            bio =  itemView.findViewById(R.id.textView_bio);
            show_likes =  itemView.findViewById(R.id.show_likes);


            button_connect =  itemView.findViewById(R.id.button_connect);

            imageView_dp =  itemView.findViewById(R.id.profile_photo);

            suggestion_view=itemView.findViewById(R.id.suggestion_view1);

            loading_circle =  itemView.findViewById(R.id.loading);






        }
    }

    private void dialogBuilder(String text, final int type, Boolean isNegative, String posText, String negText,ArrayList<UserDetails> arrayList){

        final Dialog dialog = new Dialog(activity,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.layout_dialog);

        TextView textView = (TextView) dialog.findViewById(R.id.dialogue_text);
        textView.setText(text);

        if (!(type == 2)) {
            dialog.setCancelable(false);
        }

        Button posButton = (Button) dialog.findViewById(R.id.button_positive);
        posButton.setText(posText);
        posButton.setVisibility(View.VISIBLE);
        posButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (type == 2){
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                }

            }
        });

        if (isNegative){

            Button negButton = (Button) dialog.findViewById(R.id.button_negative);
            negButton.setText(negText);
            negButton.setVisibility(View.VISIBLE);
            negButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type == 2){
                        dialog.dismiss();
                    }
                }
            });
        }


        try{
            dialog.show();
        }catch (WindowManager.BadTokenException e){

        }


    }



}
