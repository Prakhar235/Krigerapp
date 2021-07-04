package in.kriger.newkrigercampus.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;
import in.kriger.newkrigercampus.activities.JoinRequestActivity;
import in.kriger.newkrigercampus.classes.UserDetails;
import in.kriger.newkrigercampus.extras.CustomTextView;
import in.kriger.newkrigercampus.groupactivities.ShowGroup;
import in.kriger.newkrigercampus.R;

public class GroupSearchAdapter extends RecyclerView.Adapter<GroupSearchAdapter.connectionViewHolder> {

    private List<UserDetails> group_list;

    Context context;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    String search;


    public GroupSearchAdapter(List<UserDetails> group_list, Context context) {
        this.group_list = group_list;
        this.context = context;

    }

    @Override
    public connectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_grouplist, parent, false);

        return new connectionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final connectionViewHolder holder, final int position) {
        final UserDetails userDetails = group_list.get(position);

        holder.sname.setText(userDetails.getName());
        holder.sname.setTextToHighlight(search);
        holder.sname.setTextHighlightColor("#ADD8E6");
        holder.sname.setCaseInsensitive(true);
        holder.sname.highlight();

        holder.sheadline.setText(userDetails.getHeadline());
        holder.sheadline.setTextToHighlight(search);
        holder.sheadline.setTextHighlightColor("#ADD8E6");
        holder.sheadline.setCaseInsensitive(true);
        holder.sheadline.highlight();

        RequestOptions requestOption = new RequestOptions()
                .placeholder(R.drawable.default_profile)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


        Glide.with(context).load(userDetails.getImageurl())
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(requestOption)
                .into(holder.imageView_dp);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{

                    FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("User_List").child(user.getUid()).child("group").child(group_list.get(position).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()){
                                Intent intent = new Intent(context, ShowGroup.class);
                                intent.putExtra("grp_id",group_list.get(position).getUid());
                                context.startActivity(intent);
                            }
                            else {
                                Intent intent =new Intent(context, JoinRequestActivity.class);
                                intent.putExtra("grp_id",group_list.get(position).getUid());
                                context.startActivity(intent);

                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }catch (IndexOutOfBoundsException e){

                }


            }
        });
    }


    @Override
    public int getItemCount() {
        return group_list.size();
    }

    public void setSearch(String search) {
        this.search = search;
    }


    public static class connectionViewHolder extends  RecyclerView.ViewHolder{

        private CustomTextView sname;
        private CustomTextView sheadline;
        private ImageView imageView_dp;


        LinearLayout layout_linear;


        public connectionViewHolder(View itemView) {
            super(itemView);

            sname =  itemView.findViewById(R.id.sname);
            sheadline =  itemView.findViewById(R.id.sheadline);
            imageView_dp =  itemView.findViewById(R.id.imageButton_dp);
            layout_linear = itemView.findViewById(R.id.layout_linear);


        }
    }



}