package in.kriger.newkrigercampus.activities;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.R;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText editText_old, editText_new, editText_cnew;

    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    private Button changepass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        //Set Title
        setTitle("Change Password");

        //GUI
        editText_old =  findViewById(R.id.etext_oldpass);
        editText_new =  findViewById(R.id.etext_newpass);
        editText_cnew =  findViewById(R.id.etext_cnewpass);

        changepass = (Button) findViewById(R.id.btn_changepass);
        changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (editText_old.getText().length() == 0 || editText_new.getText().length() == 0 || editText_cnew.getText().length() == 0){
                    Toasty.custom(getApplicationContext(), "Cannot accept empty fields!", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_SHORT, false,
                            true).show();

                }else {
                    if (editText_old.getText().toString().equals(editText_new.getText().toString())) {
                        Toasty.custom(getApplicationContext(), "Old and new password are same!", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_SHORT, false,
                                true).show();


                    } else {


                        if (editText_new.getText().toString().equals(editText_cnew.getText().toString())) {

                            Toasty.custom(getApplicationContext(), "Updating password,please wait.", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_SHORT, false,
                                    true).show();


                            AuthCredential credential = EmailAuthProvider
                                    .getCredential(user.getEmail(), editText_old.getText().toString());


                            user.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            user.updatePassword(editText_new.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {

                                                                Toasty.custom(getApplicationContext(), "Password updated..", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_SHORT, false,
                                                                        true).show();

                                                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                                                startActivity(intent);


                                                            } else {
                                                                Toasty.custom(getApplicationContext(), "Please enter correct old password!", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_SHORT, false,
                                                                        true).show();


                                                            }
                                                        }
                                                    });
                                        }
                                    });

                        } else {
                            Toasty.custom(getApplicationContext(), "Passwords don't match", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                    true).show();

                        }
                    }
                }

            }
        });


        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile_bar_gradient));

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);

        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
