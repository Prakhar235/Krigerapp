package in.kriger.newkrigercampus.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;
import java.util.List;
import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.activities.AnswerGroupListActivity;
import in.kriger.newkrigercampus.activities.AnswerListActivity;
import in.kriger.newkrigercampus.activities.HomeActivity;
import in.kriger.newkrigercampus.activities.JoinRequestActivity;
import in.kriger.newkrigercampus.activities.ProfileListActivity;
import in.kriger.newkrigercampus.classes.Notification;
import in.kriger.newkrigercampus.database.DatabaseHelper;
import in.kriger.newkrigercampus.extras.PrefManager;
import in.kriger.newkrigercampus.groupactivities.GroupAbout;
import in.kriger.newkrigercampus.services.KrigerConstants;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class Adapter_Notification extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Notification> list_notification;
    Context context;
    private PrefManager prefManager;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private boolean isLoadingAdded = false;
    private static final int ITEM = 0;
    private static final int LOADING = 1;

    DatabaseHelper db;



    public Adapter_Notification(List<Notification> list_names,Context context) {
        this.list_notification = list_names;
        this.context = context;
        this.prefManager = new PrefManager(context);
        this.db= new DatabaseHelper(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case ITEM:
                viewHolder = new notificationViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.notification_layout, parent, false));
                break;
            case LOADING:
                viewHolder = new loadingViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.dialog_progress, parent, false));
                break;

        }
        return viewHolder;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {
        String text = null;

        switch (getItemViewType(position)) {
            case ITEM:

                final notificationViewHolder holder = (notificationViewHolder) holder1;


                final Notification notification = list_notification.get(position);


                    //display date and month for current year and hours, day ago
                    //display date month and year for previous year


                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    if (DateUtils.getRelativeTimeSpanString(notification.getTimestamp()).toString().contains("ago")) {
                        holder.notify_time.setText(DateUtils.getRelativeTimeSpanString(notification.getTimestamp()).toString());
                    } else {
                        if (notification.getTimestamp().toString().contains(String.valueOf(year))) {
                            holder.notify_time.setText(DateUtils.formatDateTime(getApplicationContext(), notification.getTimestamp(), DateUtils.FORMAT_NO_YEAR | DateUtils.FORMAT_ABBREV_MONTH).toString());

                        } else {
                            holder.notify_time.setText(DateUtils.getRelativeTimeSpanString(notification.getTimestamp()).toString());
                        }

                    }



                try{
                    if (!notification.getPost().equals("1")){
                        holder.question_small.setText(notification.getPost());
                    }

                }catch (NullPointerException e){

                }



                    if (notification.getType().equals("comment_post")) {
                        holder.image_notification.setImageResource(R.drawable.ic_app_comment);
                        text = "<b>"+ notification.getOrigin() + "</b>" + " <b>commented</b> on your post ";


                    } else if (notification.getType().equals("like_post")) {
                        holder.image_notification.setImageResource(R.drawable.ic_app_like_r);
                        text = "<b>"+ notification.getOrigin() + "</b>" + " <b>liked</b> your post ";


                    } else if (notification.getType().equals("like_comment_post")) {

                        holder.image_notification.setImageResource(R.drawable.ic_app_like_r);
                        text = "<b>"+ notification.getOrigin() + "</b>" + " <b>liked</b> your comment ";


                    } else if (notification.getType().equals("comment_group_post")) {
                        holder.image_notification.setImageResource(R.drawable.ic_app_comment);
                        text = "<b>"+ notification.getOrigin() + "</b>" + " <b>commented</b> on your post in " + notification.getId_name() + " group";


                    } else if (notification.getType().equals("like_group_post")) {
                        holder.image_notification.setImageResource(R.drawable.ic_app_like_r);
                        text = "<b>"+ notification.getOrigin() + "</b>" + " <b>liked</b> your post in " + notification.getId_name() + " group";


                    } else if (notification.getType().equals("like_comment_group_post")) {

                        holder.image_notification.setImageResource(R.drawable.ic_app_like_r);
                        text = "<b>"+ notification.getOrigin() + "</b>" + " <b>liked</b> your comment in " + notification.getId_name() + " group";


                    } else if (notification.getType().equals("connection_request")) {

                        holder.image_notification.setImageResource(R.drawable.ic_add_circle_outline_black_24dp);
                        text = "<b>"+ notification.getOrigin() + "</b>" + " <b>sent</b> you a friend request.";

                    } else if (notification.getType().equals("accept_request")) {
                        holder.image_notification.setImageResource(R.drawable.ic_add);
                        text = "<b>"+ notification.getOrigin() + "</b>" + " <b>accepted</b> your friend request.";


                    } else if (notification.getType().equals("admin_group")) {
                        holder.image_notification.setImageResource(R.drawable.ic_add_circle_outline_black_24dp);
                        text = "<b>You</b> are the new admin of " + "<b>"+ notification.getDestination() + "</b>" + " group";
                        if(prefManager.getProfileImageUrl()!=null){
                            try{
                                RequestOptions requestOption = new RequestOptions()
                                        .placeholder(R.drawable.default_profile)
                                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


                                Glide.with(context).load(prefManager.getProfileImageUrl())
                                        .apply(requestOption)
                                        .into(holder.imageButton_photo);


                            }catch (NullPointerException e){

                            }
                        }


                    } else if (notification.getType().equals("owner_group")) {
                        holder.image_notification.setImageResource(R.drawable.ic_add_circle_outline_black_24dp);
                        text = "<b>You</b> are the new owner of " + "<b>"+ notification.getDestination() + "</b>" + " group";
                        if(prefManager.getProfileImageUrl()!=null){
                            try{
                                RequestOptions requestOption = new RequestOptions()
                                        .placeholder(R.drawable.default_profile)
                                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


                                Glide.with(context).load(prefManager.getProfileImageUrl())
                                        .apply(requestOption)
                                        .into(holder.imageButton_photo);


                            }catch (NullPointerException e){

                            }
                        }


                    } else if (notification.getType().equals("referral_score")) {
                        holder.image_notification.setImageResource(R.drawable.ic_refer_friend);
                        text = "<b>"+ notification.getOrigin() + "</b>" + " has <b>signed up</b> using your referral link";

                    } else if (notification.getType().equals("tag_post")) {
                        holder.image_notification.setImageResource(R.drawable.ic_mention_user);
                        text = "<b>"+ notification.getOrigin() + "</b>" + " <b>mentioned</b> you in a post" ;

                    } else if (notification.getType().equals("tag_post_comment")) {
                        holder.image_notification.setImageResource(R.drawable.ic_mention_user);
                        text = "<b>"+ notification.getOrigin() + "</b>" + " <b>mentioned</b> you in a comment" ;

                    } else if (notification.getType().equals("tag_group_post")) {
                        holder.image_notification.setImageResource(R.drawable.ic_mention_user);
                        text = "<b>"+ notification.getOrigin() + "</b>" + " <b>mentioned</b> you in a post of " + notification.getId_name() + " group" ;

                    } else if (notification.getType().equals("tag_group_post_comment")) {
                        holder.image_notification.setImageResource(R.drawable.ic_mention_user);
                        text = "<b>"+ notification.getOrigin() + "</b>" + " <b>mentioned</b> you in a comment of " + notification.getId_name() + " group";

                    } else if (notification.getType().equals("profile_visits")) {
                        //holder.image_notification.setImageResource(R.drawable.ic_add_circle_outline_black_24dp);
                        text = "1-9 people have visited your profile recently ";
                    }
                    else if (notification.getType().equals("resource_enquiry")) {
                        //holder.image_notification.setImageResource(R.drawable.ic_add_circle_outline_black_24dp);
                        text = "<b>"+ notification.getOrigin() + "</b>" + " <b>Enquired</b> for your " + notification.getId_name() ;
                    }

                    holder.notification_string.setText(Html.fromHtml(text));

                    try {
                      KrigerConstants.user_detailRef.child(notification.getOrigin_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    RequestOptions requestOption = new RequestOptions()
                                            .placeholder(R.drawable.default_profile)
                                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


                                    Glide.with(context).load(dataSnapshot.child("thumb").getValue().toString())
                                            .apply(requestOption)
                                            .into(holder.imageButton_photo);



                                } catch (NullPointerException e) {
                                    holder.imageButton_photo.setImageResource(R.drawable.default_profile);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    } catch (NullPointerException e) {
                    }


                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (notification.getType().equals("timeline_post_tag") || notification.getType().equals("timeline_comment_tag") || notification.getType().equals("like_post") || notification.getType().equals("comment_post") || notification.getType().equals("like_comment_post")) {
                                Intent intent = new Intent(context, AnswerListActivity.class);
                                intent.putExtra("post_id", notification.getId());
                                context.startActivity(intent);
                            } else if (notification.getType().equals("group_post_tag") || notification.getType().equals("group_comment_tag") || notification.getType().equals("like_group_post") || notification.getType().equals("comment_group_post") || notification.getType().equals("like_comment_group_post")) {
                                Intent intent = new Intent(context, AnswerGroupListActivity.class);
                                intent.putExtra("post_id", notification.getId_extra());
                                intent.putExtra("grp_id", notification.getId());
                                context.startActivity(intent);
                            } else if (notification.getType().equals("admin_group") || notification.getType().equals("owner_group")) {
                                Intent intent = new Intent(context, GroupAbout.class);
                                intent.putExtra("grp_id", notification.getDestination_id());
                                context.startActivity(intent);
                            } else if (notification.getType().equals("connection_request")) {
                                Intent intent = new Intent(context, HomeActivity.class);
                                intent.putExtra("screen", "krigers");
                                context.startActivity(intent);
                            } else if (notification.getType().equals("accept_request")) {
                                    Intent intent = new Intent(context, ProfileListActivity.class);
                                    intent.putExtra("user_id", notification.getOrigin_id());
                                    context.startActivity(intent);

                            } else if (notification.getType().equals("referral_score")) {
                                Intent intent = new Intent(context, ProfileListActivity.class);
                                intent.putExtra("user_id", notification.getId());
                                context.startActivity(intent);
                            } else if (notification.getType().equals("profile_visits")) {
                                Intent intent = new Intent(context, ProfileListActivity.class);
                                intent.putExtra("user_id", user.getUid());
                                context.startActivity(intent);
                            }else if (notification.getType().equals("tag_group_post") || notification.getType().equals("tag_group_post_comment")){
                                if (db.isGroupPresent(notification.getId())){
                                    Intent intent = new Intent(context,AnswerGroupListActivity.class);
                                    intent.putExtra("post_id", notification.getId_extra());
                                    intent.putExtra("grp_id", notification.getId());
                                    context.startActivity(intent);
                                }else{
                                    Toasty.custom(context,"You are not a member of the group.", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                            true).show();
                                    Intent intent = new Intent(getApplicationContext(), JoinRequestActivity.class);
                                    intent.putExtra("grp_id", notification.getId());
                                    context.startActivity(intent);
                                }

                            }else if (notification.getType().equals("tag_post") || notification.getType().equals("tag_post_comment")){
                                Intent intent = new Intent(context,AnswerListActivity.class);
                                intent.putExtra("post_id", notification.getId());
                                context.startActivity(intent);

                            }else if (notification.getType().equals("resource_enquiry") ){
                                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                                intent.putExtra("screen","resources");
                                intent.putExtra("tab","my");
                                context.startActivity(intent);

                            }


                        }
                    });




                break;
            case LOADING:
                break;

        }
    }





    @Override
    public int getItemCount() {
        return list_notification == null ? 0 : list_notification.size();
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;

    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

    }

    @Override
    public int getItemViewType(int position) {

        return (position == list_notification.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }





    public class notificationViewHolder extends RecyclerView.ViewHolder{

        private TextView notification_string;
        private TextView question_small;
        private ImageView imageButton_photo,image_notification;

        private TextView notify_time;


        public notificationViewHolder(final View itemView) {
            super(itemView);

            notification_string =  itemView.findViewById(in.kriger.newkrigercampus.R.id.textview_notification);
            question_small =  itemView.findViewById(in.kriger.newkrigercampus.R.id.question_small);
            imageButton_photo =  itemView.findViewById(in.kriger.newkrigercampus.R.id.imageButton_dp);
            image_notification =  itemView.findViewById(R.id.image_notification);
            notify_time =  itemView.findViewById(R.id.notify_time);


        }

    }

    protected class loadingViewHolder extends RecyclerView.ViewHolder {

        public loadingViewHolder(View itemView) {
            super(itemView);
        }
    }

}
