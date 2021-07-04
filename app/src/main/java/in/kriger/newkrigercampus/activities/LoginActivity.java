package in.kriger.newkrigercampus.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import androidx.annotation.NonNull;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.extras.PrefManager;
import in.kriger.newkrigercampus.extras.TypefaceUtil;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.ResendVerificationEmail;
import in.kriger.newkrigercampus.services.KrigerConstants;

public class LoginActivity extends AppCompatActivity {
    RequestQueue requestQueue;


    private String TAG;
    private FirebaseAuth mAuth;
    private EditText editText_login_email, editText_login_password;
    private Button button_login_login, button_login_signup, button_login_resend, button_login_forgot, button_login_trouble;
    private Button btn_preview;
    private Boolean verifiedemail;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private PrefManager prefManager;

    Dialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Get SharedPreference
        pref = getApplicationContext().getSharedPreferences("Preference", 0);
        editor = pref.edit();

        prefManager = new PrefManager(getApplicationContext());


        mAuth = FirebaseAuth.getInstance();
        requestQueue= Volley.newRequestQueue(this);





        progress = new Dialog(LoginActivity.this);
        progress.setContentView(R.layout.dialog_progress);
        progress.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        editText_login_email =  findViewById(R.id.editText_login_email);
        editText_login_password =  findViewById(R.id.editText_login_password);

        //Check Refer Link
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(LoginActivity.this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user == null
                                && deepLink != null
                                && deepLink.getBooleanQueryParameter("invitedby", false)) {
                            String referrerUid = deepLink.getQueryParameter("invitedby");

                            editor.putString("refer", referrerUid);
                            editor.commit();

                        }
                    }
                });


        btn_preview =  findViewById(R.id.btn_login_preview);

        btn_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {

                    progress = new Dialog(LoginActivity.this);
                    progress.setContentView(R.layout.dialog_progress);
                    progress.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    progress.show();
                    Intent intent = new Intent(getApplicationContext(),PreviewActivity.class);
                    startActivity(intent);
                    progress.dismiss();

                }catch (Exception e){

                }


            }
        });



        //Button Signup Intereactions
        button_login_signup =  findViewById(R.id.button_login_signup);
        button_login_signup.setTypeface(new TypefaceUtil().getfont(getApplicationContext()));
        button_login_signup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,EducatorActivity.class);
                startActivity(intent);
            }


        });




        //Button Login Interactions
        button_login_login =  findViewById(R.id.button_login_login);
        button_login_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {



                ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = conMgr.getActiveNetworkInfo();

                if (info != null && info.isConnected()) {
                    if (editText_login_password.getText().toString().isEmpty() || editText_login_email.getText().toString().isEmpty()) {
                        Toasty.custom(getApplicationContext(), "Please enter credentials", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                true).show();

                    }
                    else {

                        progress.show();
                        progress.setCancelable(false);

                        String email = editText_login_email.getText().toString().toLowerCase();

                        if(editText_login_email.getText().toString().toLowerCase().contains("@gmail.com")){
                            email = "";
                            String [] parts = editText_login_email.getText().toString().split("@");
                            parts[0] =  parts[0].replace(".","");

                            email = parts[0] + "@gmail.com";

                        }

                        signIn(email, editText_login_password.getText().toString());

                    }
                } else {
                    try {

                        dialogBuilder("No Internet Connection!!\n" +
                                "Please check internet connection to continue.",2,false,"OK",null,null).show();



                    } catch (Exception e) {

                    }
                }

            }
        });


        //Button Forget Password Interactions
        button_login_forgot =  findViewById(R.id.button_login_forgot);
        button_login_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final Dialog dialog = new Dialog(LoginActivity.this,android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);

                dialog.setContentView(R.layout.layout_reset_password);

                final EditText editText = (EditText) dialog.findViewById(R.id.editText_reset_email);


                Button button = (Button) dialog.findViewById(R.id.button_reset_send);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        progress.show();

                        if (editText.getText().toString().isEmpty()) {
                            progress.dismiss();
                            Toasty.custom(getApplicationContext(),"Please enter email-id", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                    true).show();

                        } else {

                            auth.sendPasswordResetEmail(editText.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progress.dismiss();

                                            if (task.isSuccessful()) {
                                                Toasty.custom(getApplicationContext(), "Password reset link sent", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                                        true).show();

                                            } else {
                                                try {
                                                    if (task.getException().getMessage().equals("The email address is badly formatted.")) {
                                                        Toasty.custom(getApplicationContext(), "Incorrect email address", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                                                true).show();
                                                    } else if (task.getException().getMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted.")) {
                                                        Toasty.custom(getApplicationContext(), "No user registered with this email id", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                                                true).show();
                                                    } else {
                                                        Toasty.custom(getApplicationContext(), "Something went wrong!", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                                                true).show();
                                                    }
                                                } catch (NullPointerException e) {
                                                    Toasty.custom(getApplicationContext(), "Something went wrong!", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                                            true).show();

                                                }



                                            }
                                        }
                                    });
                        }
                    }
                });

                dialog.show();

            }
        });

        //Trouble Logging Interactions
        button_login_trouble =  findViewById(R.id.button_login_trouble);
        button_login_trouble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog trouble = new Dialog(LoginActivity.this,android.R.style.Theme_Light_NoTitleBar_Fullscreen);
                // Set GUI of login screen
                trouble.setContentView(R.layout.dialog_havingtrouble);

                // Init button of login GUI
                Button email =  trouble.findViewById(R.id.btn_trouble_email);
                Button whatsapp =  trouble.findViewById(R.id.btn_trouble_whatsapp);
                Button verification =  trouble.findViewById(R.id.btn_trouble_verification);

                email.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(Intent.ACTION_SEND);
                        String[] recipients = {"support@kriger.in"};
                        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Issue");
                        intent.setType("text/html");
                        intent.setPackage("com.google.android.gm");
                        try {
                            startActivity(Intent.createChooser(intent, "Send mail"));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toasty.custom(getApplicationContext(), "There are no email clients installed", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                    true).show();
                        }

                    }
                });

                whatsapp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        PackageManager pm = getPackageManager();
                        try {

                            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                            try {

                                Intent intentWhatsapp = new Intent(Intent.ACTION_VIEW);
                                String url = "https://chat.whatsapp.com/https://chat.whatsapp.com/EZCcHHMWQVE32STn3OTSlJ";
                                intentWhatsapp.setData(Uri.parse(url));
                                intentWhatsapp.setPackage("com.whatsapp");
                                startActivity(intentWhatsapp);

                            }
                            catch (Exception e){
                                Toasty.custom(getApplicationContext(), e.getMessage(), R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                        true).show();
                                e.printStackTrace();
                            }

                        }
                        catch (PackageManager.NameNotFoundException e) {
                            Toasty.custom(getApplicationContext(), "Whatsapp is not installed on this device", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                    true).show();
                        }
                    }
                });


                verification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), ResendVerificationEmail.class);
                        startActivity(intent);
                    }
                });


                trouble.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                trouble.show();

            }
        });


    }


    //Signin
    private void signIn(String email, final String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {



                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {

                            progress.dismiss();
                            Log.w(TAG, "signInWithEmail", task.getException());

                            if (task.getException().getMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted.")) {
                                Toasty.custom(getApplicationContext(), "No user found", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                        true).show();
                            } else if (task.getException().getMessage().equals("The password is invalid or the user does not have a password.")) {
                                Toasty.custom(getApplicationContext(),"Incorrect password", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                        true).show();
                            } else {
                                Toasty.custom(getApplicationContext(),"Incorrect email id", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                        true).show();
                            }
                        }
                        else if(prefManager.getDeactivate()){
                            progress.dismiss();
                            dialogBuilder("Your account has been deleted.",2,false,"OK",null,null).show();
                        }

                        else  {

                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            verifiedemail = user.isEmailVerified();

                            if (verifiedemail) {
                                final String referid = pref.getString("refer", null);
                                    FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("User_Extra_Detail").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot1) {


                                            KrigerConstants.user_detailRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if(dataSnapshot.child("type").exists()){

                                                        try {

                                                                if(((Long)dataSnapshot.child("type").child("0").getValue()).intValue() > 39 ){
                                                                    prefManager.setAccountType(2);

                                                                }else if (((Long)dataSnapshot.child("type").child("0").getValue()).intValue() > 19 ){
                                                                    prefManager.setAccountType(1);
                                                                }else {
                                                                    prefManager.setAccountType(0);
                                                                }

                                                        } catch (NullPointerException e) {
                                                        }


                                                    }
                                                    progress.dismiss();
                                                    if (dataSnapshot1.child("count_visits").exists()){
                                                        prefManager.setCountVisits(((Long)dataSnapshot1.child("count_visits").getValue()).intValue());
                                                        prefManager.setFirstTimeLaunch(true);
                                                        prefManager.setTransGroups(true);
                                                        prefManager.setTransKrigers(true);
                                                        prefManager.setTransProfile(true);
                                                        prefManager.setTransMarket(true);
                                                    }

                                                    if (dataSnapshot1.child("date_of_joining").exists()){
                                                        prefManager.setDateOfJoining(dataSnapshot1.child("date_of_joining").getValue().toString());
                                                    }


                                                    if (referid == null || referid.equals(user.getUid())) {
                                                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                                        intent.putExtra("is_start", true);
                                                        startActivity(intent);
                                                    } else {
                                                        editor.remove("refer");
                                                        editor.commit();
                                                        HashMap<String, Object> map = new HashMap<>();
                                                        map.put(user.getUid(), "true");
                                                        FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("User_List").child(referid).child("referral").updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                     //hi this is line                                           sendjsonrequest();
                                                                 sendjsonrequest("http://134.209.157.104:6000/profile/getid/"+user.getUid());





                                                                progress.dismiss();
                                                                Intent intent = new Intent(getApplicationContext(),InvitationActivity.class);
                                                                intent.putExtra("user_id",referid);
                                                                startActivity(intent);
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {

                                                                progress.dismiss();
                                                                Toasty.custom(getApplicationContext(),"Please try again", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                                                        true).show();

                                                            }
                                                        });
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });



                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                            } else {
                                progress.dismiss();
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(getApplicationContext(), ResendVerificationEmail.class);
                                startActivity(intent);
                            }
                        }
                    }
                });
    }






    @Override
    public void onBackPressed() {


        dialogBuilder("Are you sure you want to exit the app?",4,true,"No","Yes",null).show();

    }
    public void newjsonrequest(String url)
    {

      //  "description",
          //      "name",
            //    "year_from",
             //   "year_to",
              //  "_id",
              //  "is_visible"
     //   "description",
           //     "name",
           //     "year_from",
           //     "year_to",
            //    "_id",
            //    "is_visible"


        JsonObjectRequest Jsonobjectrequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

          //      try {
                //    Newcertification newcertificationobj=new Newcertification();
                 //   newcertificationobj.setDescription( response.getJSONObject("certification").getString("description"));
                 //   newcertificationobj.setName( response.getJSONObject("certification").getString("name"));
                   // newcertificationobj.setYearFrom( response.getJSONObject("certification").getString("year_from"));
                 //   newcertificationobj.setIsVisible( response.getJSONObject("certification").getInt("is_available"));
                 //   newcertificationobj.setId( response.getJSONObject("certification").getString("_id"));
                    //getting the whole json object from the response
                 //   Newcollege newcollegeobj=new Newcollege();
                 //   newcollegeobj.setDescription( response.getJSONObject("college").getString("description"));
                 //   newcollegeobj.setName( response.getJSONObject("college").getInt("name"));
                 //   newcollegeobj.setYearFrom( response.getJSONObject("college").getString("year_from"));
                 //   newcollegeobj.setIsVisible( response.getJSONObject("college").getInt("is_available"));
                  //  newcollegeobj.setId( response.getJSONObject("college").getString("_id"));
                  //  newcollegeobj.setYearTo( response.getJSONObject("college").getString("year_to"));


                 //   Newcollege newcoachingobj=new Newcollege();
                 //   newcoachingobj.setDescription( response.getJSONObject("coaching").getString("description"));
                 //   newcoachingobj.setName( response.getJSONObject("coaching").getInt("name"));
                 //   newcoachingobj.setYearFrom( response.getJSONObject("coaching").getString("year_from"));
                  //  newcoachingobj.setIsVisible( response.getJSONObject("coaching").getInt("is_available"));
                   // newcoachingobj.setId( response.getJSONObject("coaching").getString("_id"));


                 //   Newcollege internshipobj=new Newcollege();
                  //  newcoachingobj.setDescription( response.getJSONObject("coaching").getString("description"));
                 //   newcoachingobj.setName( response.getJSONObject("coaching").getInt("name"));
                  //  newcoachingobj.setYearFrom( response.getJSONObject("coaching").getString("year_from"));
                  //  newcoachingobj.setIsVisible( response.getJSONObject("coaching").getInt("is_available"));
                  //  newcoachingobj.setId( response.getJSONObject("coaching").getString("_id"));
                  //  newcollegeobj.setYearTo( response.getJSONObject("coaching").getString("year_to"));
                 //   User user=new User();
                 //   user.set







                    //we have the array named hero inside the object
                    //so here we are getting that json array
                   // response.toJSONArray();

                    //now looping through all the elements of the json array
                  //  for (int i = 0; i < heroArray.length(); i++) {
                        //getting the json object of the particular index inside the array
                    //    JSONObject heroObject = heroArray.getJSONObject(i);

                        //creating a hero object and giving them the values from json object
                     //   Hero hero = new Hero(heroObject.getString("cover"), heroObject.getString("name"));
                     //   heroList.add (hero.getImageUrl()) ;



                        //adding the hero to herolist

              //      }
                //    abc=  heroList.get(0);
                //    xyz=  heroList.get(1);
                 //   pqr=  heroList.get(2);

                    //    abc = response.getString("abc");
                    //   xyz = response.getString("xyz");
                    //   pqr = response.getString("pqr");
                    //    Toast.makeText(getApplicationContext(),abc, Toast.LENGTH_LONG).show();


                 //   if(abc!=null && xyz!=null && pqr!=null)
                 //   {
                   //     Bitmap bmabc=         loadimage(abc);
                     //   Bitmap bmxyz  =   loadimage(xyz);
                     //   Bitmap bmpqr =   loadimage(pqr);
                     //   imageview.setImageBitmap(bmabc);
                     //   imageview2.setImageBitmap(bmpqr);
                     //   imageview3.setImageBitmap(bmxyz);





                }
             //   catch(JSONException e)
             //   {
                   // e.printStackTrace();


              //  }

         //   }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(Jsonobjectrequest);

    }
    String objectid;
    public void sendjsonrequest(String url)
    {
        JsonObjectRequest Jsonobjectrequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    //getting the whole json object from the response


                    //we have the array named hero inside the object
                    //so here we are getting that json array
                    objectid = response.getString("_id");
                    Toast.makeText(getApplicationContext(),objectid, Toast.LENGTH_LONG).show();


                    storelocaldata();


                  //  String newuri="http://134.209.157.104:6000/profile/all/"+objectid;
                  //  newjsonrequest(newuri);



                    //now looping through all the elements of the json array
                    //for (int i = 0; i < heroArray.length(); i++) {
                        //getting the json object of the particular index inside the array
                   //     JSONObject heroObject = heroArray.getJSONObject(i);

                        //creating a hero object and giving them the values from json object
                     ///   Hero hero = new Hero(heroobject.getString("cover"), heroObject.getString("name"));
                      //  heroList.add (hero.getImageUrl()) ;
                  //  String objectid=heroobject.getString("")



                        //adding the hero to herolist




                }
                catch(JSONException e)
                {
                    e.printStackTrace();


                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(Jsonobjectrequest);

    }
    public void storelocaldata()
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("_objectid", objectid);
        edit.apply();


    }


    private Dialog dialogBuilder(String text, final int type, Boolean isNegative, String posText, String negText,Object[] arrayList){

        final Dialog dialog = new Dialog(LoginActivity.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.layout_dialog);

        TextView textView =  dialog.findViewById(R.id.dialogue_text);
        textView.setText(text);



        Button posButton =  dialog.findViewById(R.id.button_positive);
        posButton.setText(posText);
        posButton.setVisibility(View.VISIBLE);
        posButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (type == 2){
                    dialog.dismiss();
                }else if (type == 4){
                    dialog.dismiss();
                }else if(type == 5){
                    dialog.dismiss();
                }

            }
        });

        if (isNegative){

            Button negButton =  dialog.findViewById(R.id.button_negative);
            negButton.setText(negText);
            negButton.setVisibility(View.VISIBLE);
            negButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type == 4){
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    dialog.dismiss();
                }
            });
        }





        return dialog;

    }


}


