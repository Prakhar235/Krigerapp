package in.kriger.newkrigercampus.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import java.util.List;
import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.classes.UserDetails;
import in.kriger.newkrigercampus.R;

public class Adapter_SearchFriends extends RecyclerView.Adapter<Adapter_SearchFriends.ViewHolder> {

    private List<UserDetails> list_names;
    Context context;

    TextView selected;

    class ViewHolder extends RecyclerView.ViewHolder{

        private final Context context;


        private TextView sname;
        private ImageView imageView_pic;
        LinearLayout layout_suggestgroups;
        ImageButton delete_icon;




        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();

            sname =  itemView.findViewById(R.id.grp_name);
            imageView_pic =  itemView.findViewById(R.id.grp_image);
            delete_icon =  itemView.findViewById(R.id.delete_icon);

            layout_suggestgroups =  itemView.findViewById(R.id.linear_suggestedgroups);


        }
    }


    public Adapter_SearchFriends(List<UserDetails> list_names,Context context,TextView selected) {
        this.list_names = list_names;
        this.context = context;
        this.selected = selected;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_suggestiongroup, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {


        try {
            RequestOptions requestOption = new RequestOptions()
                    .placeholder(R.drawable.default_profile)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


            Glide.with(context).load(list_names.get(position).getImageurl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(requestOption)
                    .into(holder.imageView_pic);


        } catch (NullPointerException e) {
            holder.imageView_pic.setImageResource(R.drawable.default_profile);
        }

        holder.sname.setText(list_names.get(position).getName());

        holder.delete_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAt(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list_names.size() ;
    }

    public void removeAt(int position) {
        try {
            list_names.remove(position);
            notifyItemRemoved(position);
            selected.setText("Selected Members (" + String.valueOf(list_names.size()) + ")");
            notifyItemRangeChanged(position, list_names.size());
        }catch (IndexOutOfBoundsException e){
            Toasty.custom(context,"You need at least one member in the group", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                    true).show();
        }
    }
}
