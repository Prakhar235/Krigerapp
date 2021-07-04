package in.kriger.newkrigercampus;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.activities.LoginActivity;

public class ResendVerificationEmail extends AppCompatActivity {

    private EditText editText_resend_email,editText_resend_password;
    private Button button_resend_send;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG;
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resend_verification_email);


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                } else {
                    // User is signed out
                }

            }
        };



        mAuth = FirebaseAuth.getInstance();

        editText_resend_email = (EditText) findViewById(R.id.editText_resend_email);


        editText_resend_password = (EditText) findViewById(R.id.editText_resend_password);


        button_resend_send = (Button) findViewById(R.id.button_resend_send);
        button_resend_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(ResendVerificationEmail.this);
                progressDialog.setTitle("Sending Verification Mail");
                progressDialog.setMessage("Please wait");
                progressDialog.setCancelable(false);
                progressDialog.show();
                resendemail();
            }
        });
    }

    private void resendemail() {
        if (editText_resend_email.getText().toString().length() > 0 && editText_resend_password.getText().toString().length() >0){


            mAuth.signInWithEmailAndPassword(editText_resend_email.getText().toString(), editText_resend_password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {


                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail:failed", task.getException());
                                progressDialog.dismiss();
                                Toasty.custom(getApplicationContext(),"Something went wrong!", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                        true).show();
                            } else {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toasty.custom(getApplicationContext(),"Verification email sent", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                                            true).show();
                                                    FirebaseAuth.getInstance().signOut();
                                                    progressDialog.dismiss();
                                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                    startActivity(intent);
                                                }else{
                                                    FirebaseAuth.getInstance().signOut();
                                                    progressDialog.dismiss();
                                                    Toasty.custom(getApplicationContext(),"Please try again", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                                            true).show();


                                                }
                                            }
                                        });


                            }


                        }
                    });
        }else {

            Toasty.custom(getApplicationContext(),"Please enter details", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                    true).show();


        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
