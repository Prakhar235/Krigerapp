package in.kriger.newkrigercampus.activities;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.classes.UserIdString;
import in.kriger.newkrigercampus.services.FireService;

public class KrigerListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseRecyclerAdapter<UserIdString,connectionViewHolder> adapter_connections ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kriger_list);

        //GUI
        recyclerView =  findViewById(R.id.recycler_view);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(getApplicationContext());


        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile_bar_gradient));


        //Title of Activity
        setTitle("Krigers");

        //Recycler View
        Query connectionnRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("Connection").child(user.getUid()).orderByChild("timestamp");

        connectionnRef.keepSynced(true);

        FirebaseRecyclerOptions<UserIdString> options =
                new FirebaseRecyclerOptions.Builder<UserIdString>()
                        .setQuery(connectionnRef, UserIdString.class)
                        .setLifecycleOwner(this)
                        .build();

        adapter_connections = new FirebaseRecyclerAdapter<UserIdString, connectionViewHolder>(options) {
            @Override
            public connectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new connectionViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.kriger_list, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull connectionViewHolder holder, final int position, @NonNull UserIdString model) {

                FireService.showdetails(getApplicationContext(),holder.sname,holder.sheadline,holder.imageView_dp,holder.btn_tag,adapter_connections.getRef(position).getKey());

                holder.sname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), ProfileListActivity.class);
                        intent.putExtra("user_id",adapter_connections.getRef(position).getKey());
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onDataChanged() {

            }
        };


        recyclerView.setAdapter(adapter_connections);




    }

    //UI of a item
    public static class connectionViewHolder extends  RecyclerView.ViewHolder{

        private TextView sname;
        private TextView sheadline;
        private ImageView imageView_dp;
        private Button btn_tag;




        public connectionViewHolder(View itemView) {
            super(itemView);

            sname = itemView.findViewById(R.id.sname);
            sheadline = itemView.findViewById(R.id.sheadline);
            imageView_dp = itemView.findViewById(R.id.imageButton_dp);
            btn_tag = itemView.findViewById(R.id.btn_tag);

        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);

        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
