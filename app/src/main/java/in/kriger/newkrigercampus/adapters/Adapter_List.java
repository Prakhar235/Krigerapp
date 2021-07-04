package in.kriger.newkrigercampus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;
import in.kriger.newkrigercampus.classes.UserDetails;
import in.kriger.newkrigercampus.R;

public class Adapter_List extends ArrayAdapter implements Filterable{

    final String TAG = "AutocompleteCustomArrayAdapter.java";

    Context mContext;
    int layoutResourceId;
    ArrayList<UserDetails> data = null;
    ArrayList<UserDetails> all ;
    ArrayList<UserDetails> suggestions;


    private Filter filter = new KNoFilter();


    public Adapter_List(Context mContext, int layoutResourceId, ArrayList<UserDetails> data) {

        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
        this.all = (ArrayList<UserDetails>) data.clone();
        this.suggestions = new ArrayList<>();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try{



            if(convertView==null){
                // inflate the layout
                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                convertView = inflater.inflate(layoutResourceId, parent, false);
            }

            // object item based on the position
            UserDetails userDetails = data.get(position);



            // get the TextView and then set the text (item name) and tag (item ID) values
            TextView name =  convertView.findViewById(R.id.sname);
            name.setText(userDetails.getName());
            TextView sheadline =  convertView.findViewById(R.id.sheadline);
            sheadline.setText(userDetails.getHeadline());
            de.hdodenhof.circleimageview.CircleImageView imageView = (de.hdodenhof.circleimageview.CircleImageView) convertView.findViewById(R.id.imageButton_dp);


            try {
                RequestOptions requestOption = new RequestOptions()
                        .placeholder(R.drawable.default_profile)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


                Glide.with(mContext).load(userDetails.getImageurl())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .apply(requestOption)
                        .into(imageView);

                //Picasso.with(mContext).load(userDetails.getImageurl()).into(imageView);
            } catch (NullPointerException e) {
                imageView.setImageResource(R.drawable.default_profile);
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;

    }

    @Override
    public Filter getFilter() {
        return filter;
    }
    private class KNoFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence arg0) {
            FilterResults result = new FilterResults();
            result.values = data;
            result.count = data.size();
            return result;
        }
        @Override
        protected void publishResults(CharSequence arg0, FilterResults arg1) {
            notifyDataSetChanged();
        }
    }

}
