package in.kriger.newkrigercampus.groupactivities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nex3z.flowlayout.FlowLayout;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.activities.HomeActivity;
import in.kriger.newkrigercampus.classes.Group;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.services.KrigerConstants;

public class CreateGroup extends AppCompatActivity {


    EditText group_name,group_about;

    Group group;
    Button add_users;

    ImageView group_photo,profile_image_camera;

    String grp_id = null;


    Uri url;

    String old_name,old_timestamp;

    public static final int PERMISSION_ALL = 124;
    TextView text_lim,text_lim_about;

    FlowLayout flow_sub,flow_exams;
    AutoCompleteTextView exams_choice,subjects_choice;
    String[] exams;
    String[] subjects;

    final ArrayList<Integer> selected_choices_exam = new ArrayList<>();
    final ArrayList<Integer> selected_choices_sub = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile_bar_gradient));




        exams_choice = findViewById(R.id.exams_choice);
        subjects_choice = findViewById(R.id.subjects_choice);
        flow_exams = findViewById(R.id.flow_exams);
        flow_sub = findViewById(R.id.flow_sub);

        exams = getResources().getStringArray(R.array.list_exams);
        subjects = getResources().getStringArray(R.array.list_sub);


        group_name = findViewById(R.id.grp_name);

        String req3 = getResources().getString(R.string.grp_name);
        group_name.setHint(Html.fromHtml(req3));

        group_about =  findViewById(R.id.grp_about);

        String req4 = getResources().getString(R.string.grp_desc);
        group_about.setHint(Html.fromHtml(req4));

        text_lim =  findViewById(R.id.textLim);
        text_lim_about =  findViewById(R.id.textLim_about);


        group_name.setFilters((new InputFilter[]{new InputFilter.LengthFilter(60)}));
        group_name.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                text_lim.setText(String.valueOf(60 - group_name.getText().length()));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        group_about.setFilters((new InputFilter[]{new InputFilter.LengthFilter(200)}));
        group_about.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                text_lim_about.setText(String.valueOf(200 - group_about.getText().length()));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });




        //Fill Exam and subject
        ArrayAdapter<String> adapter_subjects =
                new ArrayAdapter<String>(getApplicationContext(), R.layout.prepration_list_item, subjects);
        subjects_choice.setAdapter(adapter_subjects);
        subjects_choice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                if (!selected_choices_sub.contains(Arrays.asList(subjects).indexOf(selected))) {
                    LinearLayout layout1 = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_flow_item, flow_sub, false);
                    TextView textView1 = layout1.findViewById(R.id.name);
                    textView1.setText(selected);
                    ImageView imageView = layout1.findViewById(R.id.buttonExitIcon);
                    int value = Arrays.asList(subjects).indexOf(selected);
                    imageView.setId(value);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                    int index = selected_choices_sub.indexOf(v.getId());
                                    flow_sub.removeViewAt(index);
                                    selected_choices_sub.remove(index);

                        }
                    });
                    try {
                        flow_sub.addView(layout1);
                        subjects_choice.setText("");
                        selected_choices_sub.add(value);
                    }catch (NullPointerException e){

                    }

                } else {
                    Toasty.custom(getApplicationContext(), "Item already selected", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_SHORT, false,
                            true).show();

                    subjects_choice.setText("");
                }

            }
        });

        ArrayAdapter<String> adapter_exams =
                new ArrayAdapter<String>(getApplicationContext(), R.layout.prepration_list_item, exams);
        exams_choice.setAdapter(adapter_exams);
        exams_choice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                if (!selected_choices_exam.contains(Arrays.asList(exams).indexOf(selected))) {
                    LinearLayout layout1 = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_flow_item, flow_exams, false);
                    TextView textView1 = layout1.findViewById(R.id.name);
                    textView1.setText(selected);
                    ImageView imageView = layout1.findViewById(R.id.buttonExitIcon);
                    int value = Arrays.asList(exams).indexOf(selected);
                    imageView.setId(value);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                                int index = selected_choices_exam.indexOf(v.getId());
                                flow_exams.removeViewAt(index);
                                selected_choices_exam.remove(index);

                        }
                    });
                    try {
                        flow_exams.addView(layout1);
                        exams_choice.setText("");
                        selected_choices_exam.add(value);
                    }catch (NullPointerException e){

                    }

                } else {
                    Toasty.custom(getApplicationContext(), "Item already selected", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_SHORT, false,
                            true).show();

                    exams_choice.setText("");
                }

            }
        });





        group = new Group();


        try {
            grp_id = getIntent().getExtras().getString("grp_id");
        }catch (NullPointerException e){

        }

        if (grp_id == null){
            setTitle("Create a new group");
        }else{
            setTitle("Edit Group");
        }


        final TextView txt_req =  findViewById(R.id.textview_req);
        String req = getResources().getString(R.string.mandtory_fields);
        txt_req.setText(Html.fromHtml(req));

        final TextView grp_text = findViewById(R.id.grp_text);
        String req1 = getResources().getString(R.string.upload_group_picture);
        grp_text.setText(Html.fromHtml(req1));

        add_users =  findViewById(R.id.btn_next);

        add_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!group_name.getText().toString().isEmpty()){
                    if (!group_about.getText().toString().isEmpty()){


                        if (url != null) {

                            group.setGroup_about(group_about.getText().toString().trim());
                            group.setGroup_name(group_name.getText().toString().trim());
                            group.setUrl(url.toString());
                            group.setPrepration_exam(selected_choices_exam);
                            group.setPrepration_subject(selected_choices_sub);


                            if (grp_id != null) {

                                updateotherdata();
                            } else {
                                Intent intent = new Intent(CreateGroup.this, SearchFriends.class);
                                intent.putExtra("group", group);
                                startActivity(intent);
                            }

                        } else {
                            Toasty.custom(getApplicationContext(),"Group picture is missing", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                    true).show();
                        }



                    }else{
                        Toasty.custom(getApplicationContext(),"Group description is missing", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                true).show();
                    }
                }else{
                    Toasty.custom(getApplicationContext(),"Group name is missing", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                }




            }
        });


        group = new Group();

        group_photo =  findViewById(R.id.group_photo);
        group_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getpermission();
            }
        });

        profile_image_camera =  findViewById(R.id.profile_photo_camera);
        profile_image_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getpermission();


            }
        });

        if (grp_id != null){
            KrigerConstants.group_nameRef.child(grp_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        old_name = dataSnapshot.child("name").getValue().toString();
                        try {

                            RequestOptions requestOption = new RequestOptions()
                                    .placeholder(R.drawable.default_profile)
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


                            Glide.with(getApplicationContext()).load(dataSnapshot.child("thumb").getValue().toString())
                                    .apply(requestOption)
                                    .into(group_photo);



                            url = Uri.parse(dataSnapshot.child("thumb").getValue().toString());
                        } catch (NullPointerException e) {
                            group_photo.setImageResource(R.drawable.default_groups);
                        }
                        old_timestamp = dataSnapshot.child("timestamp").getValue().toString();
                        group_name.setText(dataSnapshot.child("name").getValue().toString());
                        group_about.setText(dataSnapshot.child("about").getValue().toString());
                        group_name.setSelection(group_name.getText().length());
                        group_about.setSelection(group_about.getText().length());

                        if (dataSnapshot.child("exams").exists()) {

                            for (DataSnapshot childsnapshot : dataSnapshot.child("exams").getChildren()) {
                                if (childsnapshot.exists()) {

                                    try {

                                        LinearLayout layout1 = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_flow_item, flow_exams, false);
                                        TextView textView = layout1.findViewById(R.id.name);
                                        Integer value = Integer.valueOf(childsnapshot.getValue().toString());
                                        textView.setText(exams[value]);
                                        ImageView imageView = layout1.findViewById(R.id.buttonExitIcon);
                                        imageView.setId(value);
                                        imageView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                int index = selected_choices_exam.indexOf(v.getId());
                                                flow_exams.removeViewAt(index);
                                                selected_choices_exam.remove(index);

                                            }
                                        });
                                        try {
                                            flow_exams.addView(layout1);
                                            exams_choice.setText("");
                                            selected_choices_exam.add(value);
                                        }catch (NullPointerException e){

                                        }


                                    } catch (NullPointerException e) {

                                    }
                                }

                            }
                        }

                        if (dataSnapshot.child("subjects").exists()) {

                            for (DataSnapshot childsnapshot : dataSnapshot.child("subjects").getChildren()) {
                                if (childsnapshot.exists()) {

                                    try {

                                        LinearLayout layout1 = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_flow_item, flow_sub, false);
                                        TextView textView = layout1.findViewById(R.id.name);
                                        Integer value = Integer.valueOf(childsnapshot.getValue().toString());
                                        textView.setText(subjects[value]);
                                        ImageView imageView = layout1.findViewById(R.id.buttonExitIcon);
                                        imageView.setId(value);
                                        imageView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                int index = selected_choices_sub.indexOf(v.getId());
                                                flow_sub.removeViewAt(index);
                                                selected_choices_sub.remove(index);

                                            }
                                        });
                                        try {
                                            flow_sub.addView(layout1);
                                            subjects_choice.setText("");
                                            selected_choices_sub.add(value);
                                        }catch (NullPointerException e){

                                        }


                                    } catch (NullPointerException e) {

                                    }
                                }

                            }
                        }






                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            add_users.setText("Update");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        try {
            Log.d("sjklngskn",getIntent().getExtras().getString("source"));
        }catch (NullPointerException e){
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        }
        intent.putExtra("screen","groups");
        startActivity(intent);
        super.onBackPressed();
    }


    public void updateotherdata(){

        HashMap<String,Object> map = new HashMap<>();
        map.put("name",old_name);
        map.put("timestamp",old_timestamp);
        String key = KrigerConstants.group_dataRef.push().getKey();

        KrigerConstants.group_nameRef.child(grp_id).child("names").child(key).setValue(map);


        HashMap<String,Object> map1 = new HashMap<>();
        map1.put("name",group_name.getText().toString());
        map1.put("about",group_about.getText().toString());
        map1.put("thumb",url.toString());
        map1.put("exams",selected_choices_exam);
        map1.put("subjects",selected_choices_sub);




       KrigerConstants.group_nameRef.child(grp_id).updateChildren(map1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent=new Intent(CreateGroup.this,GroupAbout.class);
                intent.putExtra("grp_id",grp_id);
                startActivity(intent);
            }
        });
    }





    public boolean onOptionsItemSelected(MenuItem item){
        onBackPressed();
        return true;

    }



    public void getpermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

            ActivityCompat.requestPermissions(CreateGroup.this, PERMISSIONS, PERMISSION_ALL);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(150,150)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(CreateGroup.this);


        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == PERMISSION_ALL){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getpermission();
            }else {

                String permission = permissions[0];

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
                    boolean showRationale = shouldShowRequestPermissionRationale( permission );
                    if (! showRationale){

                        dialogBuilder("You have denied the media permission. Please enable it from the 'Settings' of your phone.",3,false,"Go to settings",null,null);


                    }
                    else {

                        dialogBuilder("Oops!No media access?\n" +
                                "Please allow access to use our app better! ",1,false,"OK",null,null);

                    }


                }



            }


        }


    }

    private void dialogBuilder(String text, final int type, Boolean isNegative, String posText, String negText, Object[] arrayList){

        final Dialog dialog = new Dialog(CreateGroup.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.layout_dialog);

        TextView textView =  dialog.findViewById(R.id.dialogue_text);
        textView.setText(text);

        Button posButton = dialog.findViewById(R.id.button_positive);
        posButton.setText(posText);
        posButton.setVisibility(View.VISIBLE);
        posButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(type==1)
                {
                    dialog.dismiss();
                    getpermission();

                }else if(type ==3)
                {
                    // navigate to settings
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    dialog.dismiss();
                }

            }
        });

        if (isNegative){

            Button negButton = (Button) dialog.findViewById(R.id.button_negative);
            negButton.setText(negText);
            negButton.setVisibility(View.VISIBLE);
            negButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }



        try {
            dialog.show();
        }catch (WindowManager.BadTokenException e){

        }
    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                url = result.getUri();
                group_photo.setImageURI(url);
                try {
                    updateimage();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }



    public void updateimage() throws IOException {

        if (grp_id !=null) {
            SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
            final String format = s.format(new Date());


            StorageReference childref = FirebaseStorage.getInstance().getReference().child("Group").child("group_" + grp_id + "_" + format);

            Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), url);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] data = baos.toByteArray();

            childref.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getUploadSessionUri();

                            Toasty.custom(getApplicationContext(),"Group picture updated", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                    true).show();


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toasty.custom(getApplicationContext(),"Upload failed", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();

                }
            });
        }
    }
}