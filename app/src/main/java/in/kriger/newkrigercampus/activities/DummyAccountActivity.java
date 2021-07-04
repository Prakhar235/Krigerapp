package in.kriger.newkrigercampus.activities;

import android.app.Dialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.classes.UserId;
import in.kriger.newkrigercampus.services.FireService;

public class DummyAccountActivity extends AppCompatActivity {

    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Dialog progress;
    private Button connect_all;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<UserId,dummyAccountsViewHolder> adapter_dummyAccounts;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy_account);


        recyclerView =  findViewById(R.id.recycler_view);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(getApplicationContext());


        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //Connect All button
        connect_all = findViewById(R.id.btn_connectAll);






        //Set Recyclerview
        Query dummyRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("User_Detail");
        dummyRef.keepSynced(true);

        FirebaseRecyclerOptions<UserId> options =
                new FirebaseRecyclerOptions.Builder<UserId>()
                        .setQuery(dummyRef, UserId.class)
                        .setLifecycleOwner(this)
                        .build();

        adapter_dummyAccounts = new FirebaseRecyclerAdapter<UserId, dummyAccountsViewHolder>(options) {
            @Override
            public dummyAccountsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new dummyAccountsViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.dummy_account_list, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final dummyAccountsViewHolder holder, int position, @NonNull UserId model) {

                try {
                    holder.ck.setChecked(true);
                    FireService.showdetails(getApplicationContext(), holder.sname, holder.sheadline, holder.imageView_dp, null, adapter_dummyAccounts.getRef(position).getKey());

                    connect_all.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            try {

                                progress = new Dialog(DummyAccountActivity.this);
                                progress.setContentView(R.layout.dialog_progress);
                                progress.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                progress.show();

                                SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
                                String format = s.format(new Date());

                                Date parseDate = null;
                                try {
                                    parseDate = s.parse(format);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                final long milliseconds = parseDate.getTime();



                                final String dummy_recipient = adapter_dummyAccounts.getRef(holder.getAdapterPosition()).getKey();
                                adapter_dummyAccounts.getRef(holder.getAdapterPosition()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                            public void onSuccess(Void aVoid) {
                                            database.child("Connection").child(user.getUid()).child(dummy_recipient).child("timestamp").setValue(milliseconds).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        progress.dismiss();
                                                        Log.i("rece",String.valueOf(dummy_recipient));
                                                        Toasty.custom(getApplicationContext(),"Connections added", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                                                true).show();
                                                        Intent intent = new Intent(getApplicationContext(), ContactListActivity.class);
                                                        startActivity(intent);

                                                    }
                                                });
                                            }
                                });




                            } catch (Exception e){}


                        }
                    });


                } catch (DatabaseException e) {
                    Log.d("Key", adapter_dummyAccounts.getRef(position).getKey());
                }




                holder.sname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), ProfileListActivity.class);
                        intent.putExtra("user_id", adapter_dummyAccounts.getRef(holder.getAdapterPosition()).getKey());
                        startActivity(intent);
                    }
                });


            }

            @Override
            public void onDataChanged() {
                // If there are no chat messages, show a view that invites the user to add a message.
                //mEmptyListMessage.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        };

        recyclerView.setAdapter(adapter_dummyAccounts);


    }

    public static class dummyAccountsViewHolder extends RecyclerView.ViewHolder{

        private TextView sname;
        private TextView sheadline;
        private ImageView imageView_dp;
        private ImageView imageView_star;
        private CheckBox ck;


        public dummyAccountsViewHolder(View itemView) {
            super(itemView);

            sname =  itemView.findViewById(R.id.sname);
            sheadline =  itemView.findViewById(R.id.sheadline);
            imageView_dp =  itemView.findViewById(R.id.imageButton_dp);
            imageView_star =  itemView.findViewById(R.id.imageview_star);
            ck =  itemView.findViewById(R.id.chkAll);

        }
    }

}
