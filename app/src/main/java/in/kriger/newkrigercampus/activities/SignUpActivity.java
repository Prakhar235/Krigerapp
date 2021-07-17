package in.kriger.newkrigercampus.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.classes.DialogBuilder;
import in.kriger.newkrigercampus.extras.PrefManager;
import in.kriger.newkrigercampus.services.FireService;

public class SignUpActivity extends AppCompatActivity {

    Dialog progress;
    private static final String TAG = "SignupActivity";

    private FirebaseAuth mAuth;

    private PrefManager prefManager;

    ArrayList<Integer> checkboxList;

    private RadioGroup radioSexGroup;
    private RadioButton radioSexButton;

    private LinearLayout linear_name;
    private AutoCompleteTextView institute_name;
    Boolean is_corporate = false;
    EditText txtUsername, txtPassword, contact_num, currentcity, firstname, lastname;

    String key1;

    ArrayAdapter<String> listAdapter;
    List<String> nameList = new ArrayList<>();
    List<Integer> valueList = new ArrayList<>();
    int value = 0;
    String value_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        checkboxList = (ArrayList<Integer>) getIntent().getSerializableExtra("checkboxlist");

        progress = new Dialog(SignUpActivity.this);
        progress.setContentView(R.layout.dialog_progress);
        progress.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        mAuth = FirebaseAuth.getInstance();

        //set filter to disable emoticons on keyboard and accept max 50 chars
        InputFilter[] FilterArray = new InputFilter[1];


        Button btnLogin =  findViewById(R.id.button_signup);
        txtUsername =  findViewById(R.id.editText_signup_email);
        txtPassword =  findViewById(R.id.editText_signup_password);

        institute_name = findViewById(R.id.edittext_institutename);
        linear_name = findViewById(R.id.linear_name);

        listAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, nameList);
        institute_name.setThreshold(0);
        listAdapter.setNotifyOnChange(true);
        institute_name.setAdapter(listAdapter);

        institute_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                startSearch(s.toString());


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        institute_name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                value_name = ((TextView)view).getText().toString();
                value = valueList.get(i);


            }
        });


        firstname =  findViewById(R.id.edittext_firstname);
        FilterArray[0] = new InputFilter.LengthFilter(50);
        firstname.setFilters(FilterArray);

        lastname =  findViewById(R.id.edittext_lastname);
        FilterArray[0] = new InputFilter.LengthFilter(50);
        lastname.setFilters(FilterArray);

        contact_num =  findViewById(R.id.editText_signup_contact_num);
        final TextView terms_and_condition =  findViewById(R.id.terms);

        final TextView txt_account =  findViewById(R.id.account);
        currentcity =  findViewById(R.id.edittext_city);
        radioSexGroup =  findViewById(R.id.radioSex);
        RadioButton rm =  findViewById(R.id.radioMale);
        RadioButton rf =  findViewById(R.id.radioFemale);
        RadioButton ro =  findViewById(R.id.radioOther);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/roboto.light.ttf");
        rm.setTypeface(font);
        rf.setTypeface(font);
        ro.setTypeface(font);


        if (checkboxList.get(0) > 39) {
            linear_name.setVisibility(View.GONE);
            institute_name.setVisibility(View.VISIBLE);
            radioSexGroup.setVisibility(View.GONE);
            is_corporate = true;
        }


        prefManager = new PrefManager(SignUpActivity.this);

        //terms and condition in blue color
        String blue_TandC = getResources().getString(R.string.termsandconditions);
        terms_and_condition.setHint(Html.fromHtml(blue_TandC));


        final TextView link =  findViewById(R.id.terms);
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://kriger.in/term-conditions");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });


        txt_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        // Attached listener for login GUI button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedId = radioSexGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                radioSexButton =  findViewById(selectedId);


                if (txtUsername.getText().toString().length() > 0 && txtPassword.getText().toString().length() > 0 && currentcity.getText().toString().length() > 0) {

                    if (txtPassword.getText().toString().length() > 5) {

                        if (contact_num.getText().toString().length() == 10) {

                            if (is_corporate) {
                                if (institute_name.getText().length() > 0) {
                                    startSignup();
                                } else {
                                    Toasty.custom(getApplicationContext(), "Please enter all details", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                            true).show();
                                }
                            } else {
                                if (firstname.getText().length() > 0 && lastname.getText().length() > 0) {
                                    startSignup();

                                } else {
                                    Toasty.custom(getApplicationContext(), "Please enter all details", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                            true).show();
                                }
                            }


                        } else {
                            Toasty.custom(getApplicationContext(), "Contact number must have 10 digits", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                    true).show();
                        }

                    } else {
                        Toasty.custom(getApplicationContext(), "Password must have minimum 6 characters", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                true).show();

                    }


                } else {
                    Toasty.custom(getApplicationContext(), "Please enter all details", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();

                }

            }
        });

    }

    private void startSignup() {
        progress.show();
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);


        String email = txtUsername.getText().toString().toLowerCase();

        if (email.contains("@gmail.com")) {
            email = "";
            String[] parts = txtUsername.getText().toString().toLowerCase().split("@");
            parts[0] = parts[0].replace(".", "");

            email = parts[0] + "@gmail.com";

        }

        final String finalEmail = email;

            mAuth.createUserWithEmailAndPassword(finalEmail, txtPassword.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                sendverificationmail(finalEmail, txtPassword.getText().toString(), firstname.getText().toString(), lastname.getText().toString(), contact_num.getText().toString(), currentcity.getText().toString());

                            } else {
                                progress.dismiss();
                                Toasty.custom(getApplicationContext(), task.getException().getMessage(), R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                        true).show();
                            }

                        }
                    });


    }

    public void sendverificationmail(final String email, final String password, final String firstname, final String lastname, final String contact, final String city) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {



                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            FirebaseAuth.getInstance().signOut();
                        } else {

                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            HashMap<String, Object> map = new HashMap<>();
                            HashMap<String, Object> map1 = new HashMap<>();
                            map.put("password", password);
                            map.put("contact", contact);
                            map.put("email", email);
                            map.put("current_city", city);
                            if (radioSexButton.getText().equals("Male")) {
                                map.put("gender", 0);
                            } else if (radioSexButton.getText().equals("Female")) {
                                map.put("gender", 1);
                            } else {
                                map.put("gender", 2);
                            }
                            if (value != 0){
                                if (value_name.equals(institute_name.getText().toString())){
                                    map1.put("value",value);
                                }
                            }
                            map1.put("type", checkboxList);
                            if (is_corporate) {
                                map1.put("firstname", institute_name.getText().toString().trim());
                                map1.put("lastname", "");

                            } else {
                                map1.put("firstname", firstname);
                                map1.put("lastname", lastname);

                            }

                            String time = FireService.getToday();
                            DatabaseReference mUser = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/");
                            mUser.child("User").child(user.getUid()).updateChildren(map);
                            mUser.child("User_Detail").child(user.getUid()).updateChildren(map1);
                            mUser.child("User_Extra_Detail").child(user.getUid()).child("date_of_joining").setValue(time);

                            prefManager.setDateOfJoining(time);
                            String name = "";
                            if (is_corporate) {
                                name = institute_name.getText().toString().trim();
                            } else {
                                name = firstname.trim() + " " + lastname.trim();
                            }
                            final String name_upload = name;
                            mUser.child("User_Detail").child(user.getUid()).child("name").setValue(name).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    user.sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progress.dismiss();

                                                    if (task.isSuccessful()) {

                                                        DialogBuilder dialogBuilder = new DialogBuilder(SignUpActivity.this, "Verification Mail Sent!\n" +
                                                                "Kindly check your mail for verification email.\n" +
                                                                "Link is valid for 15 minutes.\n\n" + "If you don't see the mail in your inbox, please check your spam or junk folder.", 10, false, "OK", null, null);

                                                        dialogBuilder.show();

                                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                                .setDisplayName(name_upload)
                                                                .build();

                                                        user.updateProfile(profileUpdates)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                        }
                                                                        FirebaseAuth.getInstance().signOut();

                                                                    }
                                                                });
                                                    } else {
                                                        Toasty.custom(getApplicationContext(), "Verification mail sending failed", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                                                true).show();

                                                        FirebaseAuth.getInstance().signOut();
                                                    }

                                                }
                                            });

                                }
                            });


                        }

                    }
                });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

//seraching Institute name
    public void startSearch(String text) {

        key1 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").push().getKey();

        HashMap<String, Object> map = new HashMap<>();

        map.put("index", "firebase8");
        map.put("type", "corporate_name");
        map.put("/body/query/wildcard/name/value", "*" + text.trim().toLowerCase() + "*");


        FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("search").child("request").child(key1).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listensearch();
            }
        });

    }


    private void listensearch() {

        FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("search").child("response").child(key1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    try {
                        if (dataSnapshot.child("hits").child("total").getValue().toString().equals("0")) {

                        } else {
                            listAdapter.clear();
                            valueList.clear();
                            for (DataSnapshot childsnapshot : dataSnapshot.child("hits").child("hits").getChildren()) {

                                try {
                                    if (!childsnapshot.child("_source").child("uid").exists()) {

                                        if (childsnapshot.child("_source").child("name").getValue().toString().equals(institute_name.getText().toString())){
                                            value = ((Long)childsnapshot.child("_source").child("value").getValue()).intValue();
                                            value_name = childsnapshot.child("_source").child("name").getValue().toString();
                                            listAdapter.clear();
                                            listAdapter.notifyDataSetChanged();
                                            institute_name.dismissDropDown();
                                        }

                                        listAdapter.add(childsnapshot.child("_source").child("name").getValue().toString());
                                        valueList.add((((Long) childsnapshot.child("_source").child("value").getValue()).intValue()));
                                    }
                                } catch (NullPointerException e) {
                                }


                            }
                            listAdapter.notifyDataSetChanged();
                        }
                    } catch (NullPointerException e) {
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });


    }


}
