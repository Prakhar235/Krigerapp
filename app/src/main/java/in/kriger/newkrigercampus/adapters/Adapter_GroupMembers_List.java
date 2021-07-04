package in.kriger.newkrigercampus.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.HashMap;
import java.util.List;
import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.activities.ProfileListActivity;
import in.kriger.newkrigercampus.classes.DataCounters;
import in.kriger.newkrigercampus.services.KrigerConstants;
import in.kriger.newkrigercampus.services.FireService;

public class Adapter_GroupMembers_List extends RecyclerView.Adapter<Adapter_GroupMembers_List.groupmemberViewHolder> {

    List<DataCounters> list_members;
    Context context;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    String me,grp_id;




    public Adapter_GroupMembers_List(List<DataCounters> list_names, Context context,String me,String grpid) {
        this.list_members = list_names;
        this.context = context;
        this.me = me;
        this.grp_id = grpid;
    }

    @Override
    public groupmemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        groupmemberViewHolder viewHolder = new groupmemberViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.group_list, parent, false));


        return viewHolder;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(@NonNull final groupmemberViewHolder holder, final int position) {

        holder.btn_admin.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/impact.ttf"));
        holder.btn_owner.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/impact.ttf"));

        FireService.showdetails(context,holder.sname,holder.sheadline,holder.imageView,holder.btn_tag,list_members.get(position).getUid());
        if (list_members.get(position).getType().equals("admins")){
            holder.btn_admin.setVisibility(View.VISIBLE);
        }else if (list_members.get(position).getType().equals("owner")){
            holder.btn_owner.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.layout_group_permission);
                final Button make_admin =  dialog.findViewById(R.id.make_admin);
                final Button remove_admin =  dialog.findViewById(R.id.remove_admin);
                final Button remove_member =  dialog.findViewById(R.id.remove_member);
                final Button info =  dialog.findViewById(R.id.user_info);


                if (list_members.get(position).getType().equals("members")){
                    if (me.equals("members")){
                        make_admin.setVisibility(View.GONE);
                        remove_admin.setVisibility(View.GONE);
                        remove_member.setVisibility(View.GONE);
                        dialog.show();
                    }else {
                        remove_admin.setVisibility(View.GONE);
                        dialog.show();
                    }


                }else if (list_members.get(position).getType().equals("admins")){
                    if (me.equals("members")){
                        make_admin.setVisibility(View.GONE);
                        remove_admin.setVisibility(View.GONE);
                        remove_member.setVisibility(View.GONE);
                        dialog.show();
                    }else{
                        remove_member.setVisibility(View.GONE);
                        make_admin.setVisibility(View.GONE);
                        dialog.show();
                    }

                }else if (list_members.get(position).getType().equals("owner")){

                        Intent intent = new Intent(context,ProfileListActivity.class);
                        intent.putExtra("user_id",list_members.get(position).getUid());
                        context.startActivity(intent);

                }

                make_admin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            HashMap<String,Object> map = new HashMap<>();
                            map.put("timestamp",FireService.getToday());
                            map.put("added_by",user.getUid());
                            KrigerConstants.group_dataRef.child(grp_id).child("admins").child(list_members.get(position).getUid()).updateChildren(map);
                            KrigerConstants.group_dataRef.child(grp_id).child("members").child(list_members.get(position).getUid()).removeValue();
                            list_members.get(position).setType("admins");
                            notifyItemChanged(position);
                            dialog.dismiss();
                    }
                });

                remove_admin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("timestamp",FireService.getToday());
                        map.put("added_by",user.getUid());
                        KrigerConstants.group_dataRef.child(grp_id).child("members").child(list_members.get(position).getUid()).updateChildren(map);
                        KrigerConstants.group_dataRef.child(grp_id).child("admins").child(list_members.get(position).getUid()).removeValue();
                        list_members.get(position).setType("members");
                        notifyItemChanged(position);
                        dialog.dismiss();
                    }
                });

                remove_member.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (list_members.size() > 3) {
                            KrigerConstants.group_dataRef.child(grp_id).child("members").child(list_members.get(position).getUid()).removeValue();
                            KrigerConstants.user_listRef.child(list_members.get(position).getUid()).child("group").child(grp_id).removeValue();
                            list_members.remove(position);
                            notifyItemRemoved(position);

                            dialog.dismiss();
                        }else{
                            Toasty.custom(context, "You cannot have a group with only 2 members", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_SHORT, false,
                                    true).show();
                        }
                    }
                });

                info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            dialog.dismiss();
                            Intent intent = new Intent(context,ProfileListActivity.class);
                            intent.putExtra("user_id",list_members.get(position).getUid());
                            context.startActivity(intent);


                    }
                });



            }
        });


    }


    @Override
    public int getItemCount() {
        if(list_members == null){
            return 0;
        }else if(list_members.size()>10)
        {
            return 10;
        }else {
            return list_members.size();
        }
    }




    public class groupmemberViewHolder extends RecyclerView.ViewHolder{

        private TextView sname,sheadline;
        private ImageView imageView;
        private Button btn_admin,btn_owner,btn_tag;


        public groupmemberViewHolder(final View itemView) {
            super(itemView);

            sname = itemView.findViewById(R.id.sname);
            sheadline = itemView.findViewById(R.id.sheadline);
            imageView = itemView.findViewById(R.id.imageButton_dp);
            btn_admin = itemView.findViewById(R.id.btn_admin);
            btn_owner = itemView.findViewById(R.id.btn_owner);
            btn_tag = itemView.findViewById(R.id.btn_tag);



        }

    }


}
