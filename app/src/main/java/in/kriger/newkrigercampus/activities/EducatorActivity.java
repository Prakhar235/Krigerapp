package in.kriger.newkrigercampus.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.R;

public class EducatorActivity extends AppCompatActivity {

    CheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5;
    CheckBox checkBoxEdu1, checkBoxEdu2, checkBoxEdu3, checkBoxEdu4, checkBoxEdu5, checkBoxEdu6, checkBoxEdu7, checkBoxEdu8, checkBoxEdu9, checkBoxEdu10;
    FirebaseUser user;
    Button next;

    CheckBox checkBoxCop1, checkBoxCop2, checkBoxCop3, checkBoxCop4, checkBoxCop5, checkBoxCop6, checkBoxCop7, checkBoxCop8, checkBoxCop9;

    CheckBox[] cb_learners;
    CheckBox[] cb_educators;
    CheckBox[] cb_corporate;


    ArrayList<Integer> cblist = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_educator);

        //Set Title
        setTitle("I am");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile_bar_gradient));




        cb_learners = new CheckBox[]{
                checkBox1 =  findViewById(R.id.checkBox1),
                checkBox2 = findViewById(R.id.checkBox2),
                checkBox3 =  findViewById(R.id.checkBox3),
                checkBox4 =  findViewById(R.id.checkBox4),
                checkBox5 =  findViewById(R.id.checkBox5)
        };


        cb_educators = new CheckBox[]{
                checkBoxEdu1 =  findViewById(R.id.checkBoxEdu1),
                checkBoxEdu2 =  findViewById(R.id.checkBoxEdu2),
                checkBoxEdu3 =  findViewById(R.id.checkBoxEdu3),
                checkBoxEdu4 =  findViewById(R.id.checkBoxEdu4),
                checkBoxEdu5 =  findViewById(R.id.checkBoxEdu5),
                checkBoxEdu6 =  findViewById(R.id.checkBoxEdu6),
                checkBoxEdu7 =  findViewById(R.id.checkBoxEdu7),
                checkBoxEdu8 =  findViewById(R.id.checkBoxEdu8),


        };

        cb_corporate = new CheckBox[]{
                checkBoxCop1 = findViewById(R.id.checkBoxCop1),
                checkBoxCop2 = findViewById(R.id.checkBoxCop2),
                checkBoxCop3 = findViewById(R.id.checkBoxCop3),
                checkBoxCop4 = findViewById(R.id.checkBoxCop4),
                checkBoxCop5 = findViewById(R.id.checkBoxCop5),
                checkBoxCop6 = findViewById(R.id.checkBoxCop6),
                checkBoxCop7 = findViewById(R.id.checkBoxCop7),
                checkBoxCop8 = findViewById(R.id.checkBoxCop8),
                checkBoxCop9 = findViewById(R.id.checkBoxCop9),
        };

        for (CheckBox cbl_f : cb_learners) {
            Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/roboto.light.ttf");
            cbl_f.setTypeface(font);

        }
        for (CheckBox cbe_f : cb_educators) {
            Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/roboto.light.ttf");
            cbe_f.setTypeface(font);
        }


        for (CheckBox cbl : cb_learners) {
            cbl.setOnCheckedChangeListener(cbListener);
            cbl.setTag(0);


        }

        for (CheckBox cbe : cb_educators) {
            cbe.setOnCheckedChangeListener(cbListener);
            cbe.setTag(1);
        }

        for (CheckBox cbe_f : cb_corporate) {
            Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/roboto.light.ttf");
            cbe_f.setTypeface(font);
        }


        for (CheckBox cbl : cb_corporate) {
            cbl.setOnCheckedChangeListener(cbListener);
            cbl.setTag(2);


        }


        next =  findViewById(R.id.button_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkBox1.isChecked()) {
                    cblist.add(0);
                }
                if (checkBox2.isChecked()) {
                    cblist.add(1);
                }
                if (checkBox3.isChecked()) {
                    cblist.add(2);
                }
                if (checkBox4.isChecked()) {
                    cblist.add(3);
                }

                if (checkBox5.isChecked()) {
                    cblist.add(4);
                }

                if (checkBoxEdu1.isChecked()) {
                    cblist.add(20);
                }
                if (checkBoxEdu2.isChecked()) {
                    cblist.add(21);
                }
                if (checkBoxEdu3.isChecked()) {
                    cblist.add(22);
                }
                if (checkBoxEdu4.isChecked()) {
                    cblist.add(23);
                }

                if (checkBoxEdu5.isChecked()) {
                    cblist.add(24);
                }

                if (checkBoxEdu6.isChecked()) {
                    cblist.add(25);
                }
                if (checkBoxEdu7.isChecked()) {
                    cblist.add(26);
                }
                if (checkBoxEdu8.isChecked()) {
                    cblist.add(27);
                }
                if (checkBoxCop1.isChecked()){
                    cblist.add(40);
                }
                if (checkBoxCop2.isChecked()){
                    cblist.add(41);
                }
                if (checkBoxCop3.isChecked()){
                    cblist.add(42);
                }
                if (checkBoxCop4.isChecked()){
                    cblist.add(43);
                }
                if (checkBoxCop5.isChecked()){
                    cblist.add(44);
                }
                if (checkBoxCop6.isChecked()){
                    cblist.add(45);
                }
                if (checkBoxCop7.isChecked()){
                    cblist.add(46);
                }
                if (checkBoxCop8.isChecked()){
                    cblist.add(47);
                }
                if (checkBoxCop9.isChecked()){
                    cblist.add(48);
                }


                if (checkBox1.isChecked() || checkBox2.isChecked() || checkBox3.isChecked() || checkBox4.isChecked() || checkBox5.isChecked() || checkBoxEdu1.isChecked() || checkBoxEdu2.isChecked() || checkBoxEdu3.isChecked() || checkBoxEdu4.isChecked() || checkBoxEdu5.isChecked() || checkBoxEdu6.isChecked() || checkBoxEdu7.isChecked() || checkBoxEdu8.isChecked() || checkBoxCop1.isChecked() ||checkBoxCop2.isChecked() ||checkBoxCop3.isChecked() ||checkBoxCop4.isChecked() ||checkBoxCop5.isChecked() ||checkBoxCop6.isChecked() ||checkBoxCop7.isChecked() ||checkBoxCop8.isChecked() ||checkBoxCop9.isChecked() ) {
                    Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                    intent.putExtra("checkboxlist", cblist);
                    startActivity(intent);
                } else {
                    Toasty.custom(getApplicationContext(), "Please select at least one of the fields", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                }


            }

        });


    }

    //at a time select only Learners, Educators and Corporate
    CompoundButton.OnCheckedChangeListener cbListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


            if (buttonView.isChecked()) {
                if (buttonView.getTag().equals(0)) {

                    uncheckOthers(cb_educators);
                    uncheckOthers(cb_corporate);

                } else if (buttonView.getTag().equals(1)) {
                    uncheckOthers(cb_learners);
                    uncheckOthers(cb_corporate);

                }else if (buttonView.getTag().equals(2)){
                    uncheckOthers(cb_educators);
                    uncheckOthers(cb_learners);
                }
            }
        }
    };
   //Unchecked the check box
    private void uncheckOthers(CheckBox[] cb_array) {
        for (CheckBox cb : cb_array) {
            if (cb.isChecked()) {
                cb.setChecked(false);
            }
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
