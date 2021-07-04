package in.kriger.newkrigercampus.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nex3z.flowlayout.FlowLayout;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


import es.dmoral.toasty.Toasty;
import id.zelory.compressor.Compressor;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.profilefragments.Profile;
import in.kriger.newkrigercampus.services.KrigerConstants;
import in.kriger.newkrigercampus.services.FireService;

public class CreateResourceActivity extends AppCompatActivity {

    Spinner type;

    EditText name,description;
    TextView name_count, description_count;
    ImageView photo,camera_icon;
    Button btn_publish;
    Uri uri = null;
    AutoCompleteTextView sub,exams;
    FlowLayout flow_sub,flow_exams;

    String[] sublist;
    String[] examlist;
    String[] classlist;
    ArrayList<Integer> selected_sub = new ArrayList<>();
    ArrayList<Integer> selected_exam = new ArrayList<>();

    ArrayList<HashMap<String, Object>> list_time = new ArrayList<>();
    HashMap<String, Object> map_time = new HashMap<>();

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public static final int PERMISSION_ALL = 124;
    EditText fees;

    Spinner spinner_class;
    Spinner start_time,end_time;
    ImageButton cancel_time;
    LinearLayout linear_time,layout_time;
    Button show_time;
    Spinner fees_type;

    String valid_time = null;
    int img_id =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_resource);
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        setTitle("Create Resource");

        type =  findViewById(R.id.spinner);
        String[] qualification = getResources().getStringArray(R.array.list_resource_type);
        ArrayAdapter<String> adapter_edu =
                new ArrayAdapter<String>(this, R.layout.prepration_list_item, qualification);
        type.setAdapter(adapter_edu);

        valid_time = getIntent().getExtras().getString("timestamp");

        name = findViewById(R.id.edittext_name);
        description = findViewById(R.id.edittext_description);

        name_count = findViewById(R.id.name_count);
        description_count = findViewById(R.id.description_count);

        sub = findViewById(R.id.sub);
        sublist = getResources().getStringArray(R.array.list_sub);
        ArrayAdapter<String> adapter_subjects =
                new ArrayAdapter<String>(getApplicationContext(), R.layout.prepration_list_item,sublist );
        sub.setAdapter(adapter_subjects);

        exams = findViewById(R.id.exams);
        examlist = getResources().getStringArray(R.array.list_exams);
        ArrayAdapter<String> adapter_exams =
                new ArrayAdapter<String>(getApplicationContext(), R.layout.prepration_list_item, examlist);
        exams.setAdapter(adapter_exams);

        flow_exams = findViewById(R.id.flow_exams);
        flow_sub = findViewById(R.id.flow_sub);


        sub.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                if (selected_sub.size() < 1) {

                    LinearLayout layout1 = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_flow_item, flow_sub, false);
                    TextView textView1 = layout1.findViewById(R.id.name);
                    textView1.setText(selected);
                    ImageView imageView = layout1.findViewById(R.id.buttonExitIcon);
                    int value = Arrays.asList(sublist).indexOf(selected);
                    imageView.setId(value);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                                    int index = selected_sub.indexOf(v.getId());
                                    flow_sub.removeViewAt(index);
                                    selected_sub.remove(index);


                        }
                    });
                    try {
                        flow_sub.addView(layout1);
                        sub.setText("");
                        selected_sub.add(value);
                    } catch (NullPointerException e) {

                    }

                } else {
                    Toasty.custom(getApplicationContext(), "Only 1 item allowed", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                    sub.setText("");
                }

            }

        });

        exams.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                if (selected_exam.size() < 1) {

                    LinearLayout layout1 = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_flow_item, flow_exams, false);
                    TextView textView1 = layout1.findViewById(R.id.name);
                    textView1.setText(selected);
                    ImageView imageView = layout1.findViewById(R.id.buttonExitIcon);
                    int value = Arrays.asList(examlist).indexOf(selected);
                    imageView.setId(value);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                int index = selected_exam.indexOf(v.getId());
                                flow_exams.removeViewAt(index);
                                selected_exam.remove(index);

                        }
                    });
                    try {
                        flow_exams.addView(layout1);
                        exams.setText("");
                        selected_exam.add(value);
                    } catch (NullPointerException e) {

                    }

                } else {
                    Toasty.custom(getApplicationContext(), "only 1 item allowed", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                    exams.setText("");
                }

            }
        });


        btn_publish = findViewById(R.id.btn_publish);
        btn_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (linear_time.getVisibility() == View.VISIBLE){
                    if (start_time.getSelectedItemPosition() != 0 && end_time.getSelectedItemPosition() != 0){
                        startpublish();
                    }else {
                        Toasty.custom(getApplicationContext(), "Both time needed", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                true).show();
                    }
                }else {
                    if (fees_type.getSelectedItemPosition() == 0){
                        Toasty.custom(getApplicationContext(), "Select fee type", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                true).show();
                    }else {
                        startpublish();
                    }
                }

            }
        });

        name.setFilters((new InputFilter[]{new InputFilter.LengthFilter(100)}));
        name.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                name_count.setText(String.valueOf(100 - name.getText().length()));
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


        spinner_class = findViewById(R.id.spinner_class);
        classlist = getResources().getStringArray(R.array.list_edu);
        ArrayAdapter<String> adapter_class =
                new ArrayAdapter<String>(getApplicationContext(), R.layout.prepration_list_item, classlist);
        spinner_class.setAdapter(adapter_class);

        fees = findViewById(R.id.fee);


        layout_time = findViewById(R.id.layout_time);


        show_time = findViewById(R.id.show_time);

        fees_type = findViewById(R.id.fees_type);
        ArrayAdapter<String> adapter_feestype =
                new ArrayAdapter<String>(getApplicationContext(), R.layout.prepration_list_item, getResources().getStringArray(R.array.list_fees_type));
        fees_type.setAdapter(adapter_feestype);


        show_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_time.setVisibility(View.VISIBLE);
                addLayout(layout_time);
            }
        });



        description.setFilters((new InputFilter[]{new InputFilter.LengthFilter(5000)}));
        description.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                description_count.setText(String.valueOf(5000 - description.getText().length()));
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


        photo = findViewById(R.id.profile_photo);
        camera_icon = findViewById(R.id.profile_photo_camera);

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getpermission();
            }
        });

        camera_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getpermission();
            }
        });



    }


   //Adding Multiple timing
    private void addLayout(final LinearLayout viewGroup){
        View childLayout = getLayoutInflater().inflate(R.layout.layout_time,
                viewGroup, false);

        viewGroup.addView(childLayout);

        linear_time = childLayout.findViewById(R.id.linear_time);


        start_time = childLayout.findViewById(R.id.start_time);
        ArrayAdapter<String> adapter_time =
                new ArrayAdapter<String>(getApplicationContext(), R.layout.prepration_list_item, getResources().getStringArray(R.array.list_start_time));
        start_time.setAdapter(adapter_time);

        end_time = childLayout.findViewById(R.id.end_time);
        ArrayAdapter<String> adapter_end_time =
                new ArrayAdapter<String>(getApplicationContext(), R.layout.prepration_list_item, getResources().getStringArray(R.array.list_end_time));
        end_time.setAdapter(adapter_end_time);


        cancel_time = childLayout.findViewById(R.id.cancel_time);
        cancel_time.setId(img_id);
        img_id++;

        cancel_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{

                    layout_time.removeViewAt(v.getId());

                }catch (NullPointerException e){

                }

            }
        });

    }


    private void startpublish() {

            if (name.getText().length() > 0 && description.getText().length() > 0 && uri != null && type.getSelectedItemPosition() != 0 && selected_sub.size()>0 && selected_exam.size()>0 && fees.getText().length() != 0 && spinner_class.getSelectedItemPosition() != 0)  {

                if (linear_time.getVisibility() == View.VISIBLE){
                    if (start_time.getSelectedItemPosition()<= end_time.getSelectedItemPosition()){
                        startupload();
                    }else{
                        Toasty.custom(getApplicationContext(), "Start Time can't be more than End time", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                true).show();
                    }
                }else {
                    startupload();
                }


            }else {
                Toasty.custom(getApplicationContext(), "All fields/photo are compulsory", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                        true).show();
            }



    }


    @Override
    public void onBackPressed() {
        //this is only needed if you have specific things
        //that you want to do when the user presses the back button.
        /* your specific things...*/
        super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                this.finish();
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getpermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

            ActivityCompat.requestPermissions(CreateResourceActivity.this, PERMISSIONS, PERMISSION_ALL);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(150,150)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(CreateResourceActivity.this);


        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uri = result.getUri();
                try {
                    File file = new File(uri.getPath());
                    File compressedFile = new Compressor(this).compressToFile(file);
                    uri = Uri.fromFile(compressedFile);
                    Bitmap compressedBitmap = BitmapFactory.decodeFile(compressedFile.getAbsolutePath());
                    photo.setImageBitmap(compressedBitmap);
                } catch (IOException exp) {

                } catch (NullPointerException e) {
                    Profile profile = (Profile) getSupportFragmentManager().findFragmentById(R.id.flContent);
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
   //Uploading Multiple timing
    private void startupload(){
        final Dialog progress = new Dialog(CreateResourceActivity.this);
        progress.setContentView(R.layout.dialog_progress);
        progress.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        progress.show();
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

        int count = 0;
        for (View view1 : getAllChildren(layout_time)){
            if (view1 instanceof Spinner){
                Spinner spinner = (Spinner)view1;
                if (count%2 == 0){
                    map_time.put("start_time",spinner.getSelectedItemPosition());

                    count++;
                }else{
                    map_time.put("end_time",spinner.getSelectedItemPosition());

                    count++;
                    list_time.add(map_time);

                    map_time = new HashMap<>();


                }

            }




        }

        final StorageReference squareRef = mStorageRef.child("Resource").child("new_" + user.getUid());
        UploadTask uploadTask1 = squareRef.putFile(uri);

        Task<Uri> urlTask = uploadTask1.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return squareRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    final Uri squareUri = task.getResult();
                    final String key = KrigerConstants.resourceRef.push().getKey();
                    final HashMap<String, Object> map1 = new HashMap<>();
                    map1.put("thumb", squareUri.toString());
                    map1.put("name", name.getText().toString().trim());
                    map1.put("description", description.getText().toString().trim());
                    map1.put("timestamp", FireService.getToday());
                    map1.put("owner", user.getUid());
                    map1.put("type",type.getSelectedItemPosition());
                    map1.put("exam",selected_exam.get(0));
                    map1.put("subject",selected_sub.get(0));
                    map1.put("fees",Double.valueOf(fees.getText().toString()));
                    map1.put("fees_type",fees_type.getSelectedItemPosition());
                    map1.put("class_type",spinner_class.getSelectedItemPosition());
                    map1.put("valid_till",valid_time);
                    if (linear_time.getVisibility() == View.VISIBLE){
                        map1.put("time",list_time);
                    }
                    KrigerConstants.resourceRef.child(key).updateChildren(map1).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            KrigerConstants.user_listRef.child(user.getUid()).child("resource").child(key).setValue(valid_time).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progress.dismiss();
                                    Toasty.custom(getApplicationContext(), "Listing Published", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                            true).show();
                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                    intent.putExtra("screen","resources");
                                    startActivity(intent);
                                }
                            });
                        }
                    });


                } else {
                    progress.dismiss();
                    Toasty.custom(getApplicationContext(), "Upload Failed", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();

                    // Handle failures
                    // ...
                }
            }
        });
    }

    private ArrayList<View> getAllChildren(View v) {

        if (!(v instanceof ViewGroup)) {
            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<View>();

        ViewGroup viewGroup = (ViewGroup) v;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {

            View child = viewGroup.getChildAt(i);

            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            viewArrayList.addAll(getAllChildren(child));

            result.addAll(viewArrayList);
        }
        return result;
    }




}
