package in.kriger.newkrigercampus.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theartofdev.edmodo.cropper.CropImage;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.profilefragments.Profile;

public class ProfileListActivity extends AppCompatActivity {

    Fragment fragment;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/");

    String userid;
    Uri uri;

    FragmentManager fragmentManager;

    String isProfileShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_list);

        onNewIntent(getIntent());


        fragment = new Profile();
        Bundle bundle = new Bundle();
        userid = getIntent().getExtras().getString("user_id");

        bundle.putString("user_id", userid);
        try {
            isProfileShare = getIntent().getExtras().getString("is_profileShare");
            if (isProfileShare.equals("true")) {
                bundle.putString("is_profileShare", "true");
            }

        } catch (NullPointerException e) {

        }


        fragment.setArguments(bundle);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.profile, fragment, "Profile").commitAllowingStateLoss();

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile_bar_gradient));



        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
        String format = s.format(new Date());

        Date parseDate = null;
        try {
            parseDate = s.parse(format);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(!user.getUid().equals(userid)){
        mDatabase.child("Profile_View").child(getIntent().getExtras().getString("user_id")).child(mDatabase.push().getKey()).child(user.getUid()).setValue(format);
}

    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        String data = intent.getDataString();
        if (Intent.ACTION_VIEW.equals(action) && data != null) {

        }
    }


    @Override
    public void onBackPressed() {

            try{
                Log.d("Intent",getIntent().getExtras().getString("source"));
                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(intent);
            }catch (NullPointerException e){
                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(intent);
            }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.settings:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.putExtra("user_id", user.getUid());
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (user.getUid().equals(userid)) {
            getMenuInflater().inflate(R.menu.menu_settings, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                uri = result.getUri();
                try {
                    /*File file = new File(uri.getPath());
                    File compressedFile = new Compressor(this).compressToFile(file);
                    compressedUri = Uri.fromFile(compressedFile);*/
                    Profile profile = (Profile) getSupportFragmentManager().findFragmentById(R.id.profile);

                    profile.passUri(uri);

                } catch (NullPointerException e) {
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

                Toasty.custom(getApplicationContext(), error.getMessage(), R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                        true).show();
            }
        }


    }

}
