package in.kriger.newkrigercampus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hendraanggrian.appcompat.socialview.Mentionable;
import com.hendraanggrian.appcompat.widget.SuggestionArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.classes.UserDetails;

public class MentionAdapter<T extends Mentionable> extends SuggestionArrayAdapter<T> {

    Context context;

    public MentionAdapter(@NonNull Context context, int resource) {
        super(context, R.layout.kriger_list,R.id.sname);
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        UserDetails user = (UserDetails)getItem(position);
        final ViewHolder holder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.kriger_list, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            holder.usernameView.setText(user.getName());
            holder.displaynameView.setText(user.getHeadline());
            RequestOptions requestOption = new RequestOptions()
                    .placeholder(R.drawable.default_profile)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


            Glide.with(context).load(user.getImageurl())
                    .apply(requestOption)
                    .into(holder.avatarView);

        }

        // Return the completed view to render on screen
        return convertView;
    }

    private static class ViewHolder {
        private final ImageView avatarView;
        //private final ProgressBar loadingView;
        private final TextView usernameView;

        private final TextView displaynameView;

        ViewHolder(View itemView) {
            avatarView = itemView.findViewById(R.id.imageButton_dp);
            usernameView = itemView.findViewById(R.id.sname);
            displaynameView = itemView.findViewById(R.id.sheadline);


        }



    }

}
