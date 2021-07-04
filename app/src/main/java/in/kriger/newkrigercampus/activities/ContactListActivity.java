package in.kriger.newkrigercampus.activities;

import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;

import androidx.fragment.app.FragmentManager;
import in.kriger.newkrigercampus.classes.Contacts;
import in.kriger.newkrigercampus.extras.ContactInterface;
import in.kriger.newkrigercampus.extras.PrefManager;
import in.kriger.newkrigercampus.fragments.ContactList;
import in.kriger.newkrigercampus.R;


public class ContactListActivity extends AppCompatActivity implements ContactInterface {

    Fragment fragment = null;

    PrefManager prefManager;

    Boolean visit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);




        fragment = new ContactList();

        prefManager = new PrefManager(getApplicationContext());
        try {
            visit = getIntent().getExtras().getBoolean("visit");
        }catch (NullPointerException e){

        }





        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contactlist, fragment, "ContactList").commitAllowingStateLoss();


        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile_bar_gradient));


        setTitle("Contacts");

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
        if (visit){
            startActivity(intent);
        }else {
            try {
                Log.d("Intent", getIntent().getExtras().getString("source"));
            } catch (NullPointerException e) {
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            }
            startActivity(intent);
        }

        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTaskComplete(ArrayList<Contacts> contacts) {

    }
}
