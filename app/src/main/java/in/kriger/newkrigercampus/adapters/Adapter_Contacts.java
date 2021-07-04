package in.kriger.newkrigercampus.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.classes.Contacts;
import in.kriger.newkrigercampus.R;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by poojanrathod on 12/1/17.
 */

public class Adapter_Contacts extends RecyclerView.Adapter<Adapter_Contacts.ViewHolder> {


    private static LayoutInflater inflater=null;


    private ArrayList<Contacts> contactslist = new ArrayList<>();
    private ArrayList<Contacts> contactListFiltered = new ArrayList<>();

    private Context context;


    public Adapter_Contacts(ArrayList<Contacts> contactslist, Context context) {

        this.contactListFiltered = contactslist;
        this.contactslist = contactslist;
        try {
            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }catch (NullPointerException e){}
        this.context = context;



    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered = contactslist;
                } else {
                    ArrayList<Contacts> filteredList = new ArrayList<>();
                    for (Contacts row : contactslist) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getEph().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    contactListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactListFiltered = (ArrayList<Contacts>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }




    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contacts_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Contacts contacts = contactListFiltered.get(position);

        holder.name.setText(contacts.getName());
        holder.eph.setText(contacts.getEph());


        holder.btn_invite.setVisibility(View.GONE);
        holder.btn_invite_email.setVisibility(View.GONE);
        if (contacts.getEmail()){
            holder.btn_invite_email.setVisibility(View.VISIBLE);
        }else {
            holder.btn_invite.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return (contactListFiltered.size());
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        private final Context context;


        private TextView name;
        private TextView eph;
        private Button btn_invite;
        private Button btn_invite_email;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();

            name = itemView.findViewById(R.id.contact_name);
            eph =  itemView.findViewById(R.id.contact_ph);
            btn_invite = itemView.findViewById(R.id.btn_invite);
            btn_invite_email = itemView.findViewById(R.id.btn_invite_email);

            btn_invite_email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    final Contacts contacts = contactListFiltered.get(getAdapterPosition());
                    DatabaseReference sentinviteRef;
                    sentinviteRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("Sent_Invite").child(user.getUid()).child("email");

                    final ProgressDialog progress;
                    progress = ProgressDialog.show(context,"Sending Invite","Please Wait");
                    progress.setCancelable(false);

                    String key = sentinviteRef.push().getKey();

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("name", contacts.getName());
                    childUpdates.put("contact", contacts.getEph());

                    sentinviteRef.child(key).setValue(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progress.dismiss();
                            Toasty.custom(context, "Invitation Sent", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                    true).show();

                        }
                    });

                }
            });

            btn_invite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    final Contacts contacts = contactListFiltered.get(getAdapterPosition());
                    DatabaseReference sentinviteRef;
                    sentinviteRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("Sent_Invite").child(user.getUid()).child("phone");


                    final ProgressDialog progress;
                    progress = ProgressDialog.show(context,"Sending Invite","Please Wait");
                    progress.setCancelable(false);

                    String key = sentinviteRef.push().getKey();

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("name", contacts.getName());
                    childUpdates.put("contact", contacts.getEph());



                    sentinviteRef.child(key).setValue(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progress.dismiss();
                            if (contacts.getEmail()){
                                Toasty.custom(context, "Invitation sent", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                        true).show();

                            }else {

                                String uid = user.getUid();
                                String link = "https://kriger.in/?invitedby=" + uid;
                                FirebaseDynamicLinks.getInstance().createDynamicLink()
                                        .setLink(Uri.parse(link))
                                        .setDomainUriPrefix("https://kriger.page.link")
                                        .setAndroidParameters(
                                                new DynamicLink.AndroidParameters.Builder("in.kriger.krigercampus")
                                                        .setMinimumVersion(1)
                                                        .build()).
                                                buildShortDynamicLink()
                                        .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                                            @Override
                                            public void onSuccess(ShortDynamicLink shortDynamicLink) {
                                                PackageManager packageManager = context.getPackageManager();
                                                Intent i = new Intent(Intent.ACTION_VIEW);

                                                String message = "Join me in making INDIA 100% literate : " +shortDynamicLink.getShortLink().toString() +
                                                        "\n" +
                                                        "Sign up to\n" +
                                                        "Connect with Students, Teachers, Colleges, Coaching \n" +
                                                        "Purchase Study material, Classes, Practice papers\n" +
                                                        "Make or join pan INDIA study groups";


                                                PackageManager pm = context.getPackageManager();
                                                try {

                                                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                                                    try {

                                                        String number  =  contacts.getEph().replaceAll("\\s+", "");
                                                        number = number.replaceAll("-","");
                                                        number = number.substring(number.length() - 10);
                                                        number = "+91" + number;
                                                        String url = "https://api.whatsapp.com/send?phone="+ number +"&text=" + URLEncoder.encode(message, "UTF-8");
                                                        i.setPackage("com.whatsapp");
                                                        i.setData(Uri.parse(url));
                                                        if (i.resolveActivity(packageManager) != null) {
                                                            context.startActivity(i);
                                                        }
                                                    }
                                                    catch (Exception e){
                                                        Toasty.custom(context, e.getMessage(), R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                                                true).show();
                                                        e.printStackTrace();
                                                    }

                                                }
                                                catch (PackageManager.NameNotFoundException e) {
                                                    Toasty.custom(context, "Whatsapp is not installed on this device", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                                            true).show();
                                                }


                                            }
                                        });



                            }

                        }
                    });

                }
            });


        }
    }




}
