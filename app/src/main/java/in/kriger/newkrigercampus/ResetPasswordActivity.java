package in.kriger.newkrigercampus;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.activities.LoginActivity;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText editText_reset_email;
    private Button button_reset_send;
    private String TAG;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_reset_password);
        Toasty.custom(getApplicationContext(),"Please enter email to receive to password-reset mail", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                true).show();
        editText_reset_email = (EditText) findViewById(R.id.editText_reset_email);

        button_reset_send = (Button) findViewById(R.id.button_reset_send);
        button_reset_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(ResetPasswordActivity.this);
                progressDialog.setMessage("Please wait");
                progressDialog.setCancelable(false);
                progressDialog.show();
               resetpasswordmail();
            }
        });
    }

    private void resetpasswordmail() {
        FirebaseAuth auth = FirebaseAuth.getInstance();


        if (editText_reset_email.getText().toString().isEmpty()){
            progressDialog.dismiss();
            Toasty.custom(getApplicationContext(),"Please enter valid email id", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                    true).show();

        }else {

            auth.sendPasswordResetEmail(editText_reset_email.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();

                            if (task.isSuccessful()) {

                                Toasty.custom(getApplicationContext(),"Password reset mail sent!", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                        true).show();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);

                            } else {
                                Toasty.custom(getApplicationContext(),"Something went wrong!", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                        true).show();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);

                            }
                        }
                    });
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
