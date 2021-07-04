package in.kriger.newkrigercampus.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import in.kriger.newkrigercampus.activities.ProfileListActivity;
import in.kriger.newkrigercampus.extras.CustomTextView;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.services.KrigerConstants;

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.connectionViewHolder> {

    private List<String> user_list;

    Context context;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ArrayList<HashMap<String,String>> content_list;

    String search_text;




    public UserSearchAdapter(List<String> user_list, Context context, ArrayList<HashMap<String,String>> content_list) {
        this.user_list = user_list;
        this.context = context;
        this.content_list = content_list;
    }

    @Override
    public connectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_grouplist, parent, false);

        return new connectionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final connectionViewHolder holder, final int position) {


        holder.btn_tag.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/impact.ttf"));
        KrigerConstants.user_detailRef.child(user_list.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    holder.sname.setText(dataSnapshot.child("name").getValue().toString());
                    holder.sname.setTextToHighlight(search_text);
                    holder.sname.setTextHighlightColor("#ADD8E6");
                    holder.sname.setCaseInsensitive(true);
                    holder.sname.highlight();

                    if(dataSnapshot.child("type").exists()){

                        try {
                            for (DataSnapshot childsnapshot : dataSnapshot.child("type").getChildren()){

                                if(Integer.valueOf(childsnapshot.getValue().toString()) > 4 ){
                                    holder.sname.setTextColor(Color.parseColor("#1D85FE"));
                                    holder.btn_tag.setVisibility(View.VISIBLE);
                                    holder.btn_tag.setText("EDUCATOR");
                                    holder.btn_tag.setBackgroundResource(R.drawable.button_background_blue);
                                    holder.btn_tag.setTextColor(Color.parseColor("#3994e4"));
                                }
                            }
                        } catch (NullPointerException e) {
                        }


                    }


                }catch (NullPointerException e){}
                try{

                    for (String key : content_list.get(position).keySet()){
                        if (key.equals("key")){
                            holder.sheadline.setText(dataSnapshot.child("headline").getValue().toString());
                            holder.sheadline.setTextToHighlight(search_text);
                            holder.sheadline.setTextHighlightColor("#ADD8E6");
                            holder.sheadline.setCaseInsensitive(true);
                            holder.sheadline.highlight();
                        }else {
                            holder.sheadline.setText(key + " : " + content_list.get(position).get(key));
                            holder.sheadline.setTextToHighlight(search_text);
                            holder.sheadline.setTextHighlightColor("#ADD8E6");
                            holder.sheadline.setCaseInsensitive(true);
                            holder.sheadline.highlight();

                        }
                    }


                }catch (NullPointerException e){}

                    if (dataSnapshot.child("thumb").exists()) {
                        RequestOptions requestOption = new RequestOptions()
                                .placeholder(R.drawable.default_profile)
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


                        Glide.with(context).load(dataSnapshot.child("thumb").getValue().toString())
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .apply(requestOption)
                                .into(holder.imageView_dp);

                    }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(context, ProfileListActivity.class);
                    intent.putExtra("user_id", user_list.get(position));
                    context.startActivity(intent);


            }
        });
    }

    @Override
    public int getItemCount() {
        return user_list.size();
    }

    public void setSearch_text(String search_text) {
        this.search_text = search_text;
    }


    public static class connectionViewHolder extends  RecyclerView.ViewHolder{

        private CustomTextView sname;
        private CustomTextView sheadline;
        private ImageView imageView_dp;
        LinearLayout layout_linear;
        private Button btn_tag;




        public connectionViewHolder(View itemView) {
            super(itemView);

            sname =  itemView.findViewById(R.id.sname);
            sheadline =  itemView.findViewById(R.id.sheadline);
            imageView_dp =  itemView.findViewById(R.id.imageButton_dp);
            layout_linear =  itemView.findViewById(R.id.layout_linear);
            btn_tag = itemView.findViewById(R.id.btn_tag);


        }
    }
}