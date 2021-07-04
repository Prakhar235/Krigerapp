package in.kriger.newkrigercampus;

import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.activities.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PhoneVerification extends AppCompatActivity {

    private EditText mobile_number;

    private Button send;



    private LinearLayout verify_layout;
    private Button verify_otp;

    static final String SEND_URL = "https://control.msg91.com/api/sendotp.php?authkey=163516AkOETdz9SG5958c69d&mobile=";

    static final String VERIFY_URL = "https://control.msg91.com/api/verifyRequestOTP.php?authkey=163516AkOETdz9SG5958c69d&mobile=";
    private EditText editText_verify;
    private DatabaseReference mDatabase;
    private String TAG = "TAG";
    Boolean first;
    String role;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
        mobile_number = (EditText) findViewById(R.id.mobile_number);

        mobile_number.setText(getIntent().getExtras().get("mobile_number").toString());

        role = getIntent().getExtras().getString("role");


        verify_layout = (LinearLayout) findViewById(R.id.verify_layout);
        editText_verify = (EditText) findViewById(R.id.editText_verify_phone);

        first = getIntent().getExtras().getBoolean("first");

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);




        verify_otp = (Button) findViewById(R.id.verify_otp);
        verify_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new VerifyOTP().execute(editText_verify.getText().toString(),mobile_number.getText().toString());

            }
        });

        setTitle("Send OTP");

        send = (Button) findViewById(R.id.send_otp);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verify_layout.setVisibility(View.VISIBLE);
                if (mobile_number.getText().toString().length() != 10) {
                    Toasty.custom(getApplicationContext(),"Phone number should be 10 digits long", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                } else {

                    new SendOTP().execute(mobile_number.getText().toString());
                }



            }


        });



    }

    class SendOTP extends AsyncTask<String, Void, String> {

        private Exception exception;



        protected String doInBackground(String... params) {


            String number = params[0];
            // Do some validation here
            try {
                URL url = new URL(SEND_URL + "91"+ number );
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
                Toasty.custom(getApplicationContext(),"There was an error", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                        true).show();
            }else{
                Toasty.custom(getApplicationContext(),"OTP sent successfully!", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                        true).show();
            }
            Log.i("INFO", response);
        }
    }

    class VerifyOTP extends AsyncTask<String, Void, String> {

        private Exception exception;


        @Override
        protected String doInBackground(String... params) {
            String number = params[1];

            String otp = params[0];
            // Do some validation here
            try {
                URL url = new URL(VERIFY_URL + "91"+ number + "&otp=" + otp);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }

        }

        protected void onPostExecute(String response) {
            String responseString = null;

            try {
                JSONObject jObj = new JSONObject(response);
                responseString = jObj.getString("type");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (responseString.equals("success")){

                Toasty.custom(getApplicationContext(),"Mobile verification successful", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                        true).show();
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = user.getUid();



                mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/");

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/profile_phone-verified/" , true);
                childUpdates.put("/profile_number/"  , mobile_number.getText().toString());


                mDatabase.child("User").child(uid).updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        backtohome();

                    }
                });

            }else {

                Toasty.custom(getApplicationContext(),"Verification unsuccessful,Please try again!", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                        true).show();
                 editText_verify.getText().clear();

            }
        }
    }

    @Override
    public void onBackPressed() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        Toasty.custom(getApplicationContext(),"Signing out to avoid issues", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                true).show();
        startActivity(intent);

        super.onBackPressed();
    }

    public void backtohome(){


        super.onBackPressed();

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                Toasty.custom(getApplicationContext(),"Signing out to avoid issues", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                        true).show();
                startActivity(intent);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }








}

