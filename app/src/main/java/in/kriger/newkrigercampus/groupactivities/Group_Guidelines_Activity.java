package in.kriger.newkrigercampus.groupactivities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.classes.Group;

public class Group_Guidelines_Activity extends AppCompatActivity {

    private Button add_users;
    Group group;
    CheckBox checkBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group__guidelines_);


        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        group = (Group) getIntent().getSerializableExtra("group");
        checkBox= findViewById(R.id.accept);

        //Title of Activity
        setTitle("Group Creation Guidelines");
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile_bar_gradient));

        TextView tips_text =  findViewById(R.id.tips_text);
        String post_tips = getResources().getString(R.string.your_ideas_worth_spreading_group_creation);
        tips_text.setText(Html.fromHtml(post_tips));


        add_users=findViewById(R.id.add_users_group);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    add_users.setEnabled(true);
                }
                else
                {
                    add_users.setEnabled(false);
                    Toasty.custom(getApplicationContext(),"Please accept the guidelines", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                }
            }
        });


        add_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    Intent intent = new Intent(Group_Guidelines_Activity.this, CreateGroup.class);
                    intent.putExtra("group", group);
                    startActivity(intent);
                }else {
                    Toasty.custom(getApplicationContext(),"Please accept the guidelines", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();

                }
            }
        });

    }





    public boolean onOptionsItemSelected(MenuItem item){
        super.onBackPressed();
        return true;

    }

    }

