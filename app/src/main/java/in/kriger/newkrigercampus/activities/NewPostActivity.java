package in.kriger.newkrigercampus.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.bumptech.glide.Glide;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import es.dmoral.toasty.Toasty;
import id.zelory.compressor.Compressor;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.adapters.MentionAdapter;
import in.kriger.newkrigercampus.classes.Post;
import in.kriger.newkrigercampus.classes.UserDetails;
import in.kriger.newkrigercampus.extras.PrefManager;
import in.kriger.newkrigercampus.extras.SpecialAutoCompleteTextView;
import in.kriger.newkrigercampus.services.KrigerConstants;
import in.kriger.newkrigercampus.services.FireService;

import static in.kriger.newkrigercampus.activities.SplashScreen.PERMISSION_ALL;

public class NewPostActivity extends AppCompatActivity {

    //abstract add
    private static final String TAG = NewPostActivity.class.getSimpleName();
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/");
    private Uri downloadUrl;
    private String key1;
    private String store_search;
    private HashMap<String, Object> map;
    private MentionAdapter mentionAdapter;
    private Boolean is_mention = false;
    private Boolean is_hashtag = false;
    private Boolean character_lock = false;
    private ArrayList<String> userlist = new ArrayList<>();
    private int number_tag = 1;
    private Button button_hashtag;
    private int previousLength;
    private List<String> previous_mentions = new ArrayList<>();
    private List<String> after_mentions = new ArrayList<>();
    private Boolean backSpace = false;
    private Boolean is_preview = false;
    private Boolean is_pdf = false;
    private Boolean is_image = false;
    private ScrollView scrollView;
    private PrefManager prefManager;
    private TextView textView_pdf;
    private SpecialAutoCompleteTextView editText_newpost;
    private Button button_submit;
    private TextView textView_wordcount;
    private Button button_mention, button_tips;
    private ImageView image_upload, edit_addimage, edit_addpdf;
    private LinearLayout layout_pdf;
    private RelativeLayout layout_image;
    private Button button_remove;
    private ImageButton pdf_delete;
    private Uri resultUri, imgUri, uri;
    private Uri downloadpdfUrl = null, compressedUri;
    private Boolean emailLock =false;
    private int hyperlinks_count = 0;
    public static void getpermission(Context c, Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && c.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && c.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

                ActivityCompat.requestPermissions(activity, PERMISSIONS, PERMISSION_ALL);
                //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
            } else {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(activity);

            }
        } else {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(activity);

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_question);

        //Set Title
        setTitle("Post");


        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile_bar_gradient));

        prefManager = new PrefManager(this);

        editText_newpost = findViewById(R.id.editText_newpost);
        editText_newpost.setMentionEnabled(true);
        mentionAdapter = new MentionAdapter(NewPostActivity.this, R.layout.kriger_list);
        scrollView = findViewById(R.id.newpost_scrolllayout);

        editText_newpost.setMentionAdapter(mentionAdapter);

        textView_wordcount = findViewById(R.id.textView_wordcount);

        final TextCrawler textCrawler = new TextCrawler();

        editText_newpost.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (is_mention) {
                    editText_newpost.setMaxLines(Integer.MAX_VALUE);
                    UserDetails userDetails = (UserDetails) mentionAdapter.getItem(position);

                    int temp_start = editText_newpost.getText().toString().lastIndexOf('@');
                    int start = editText_newpost.getText().toString().substring(0, temp_start - 1).lastIndexOf("@");

                    StringBuilder builder = new StringBuilder();
                    builder.append(editText_newpost.getText().toString().substring(0, start));

                    String username = userDetails.getName().trim().replace(" ", "_");
                    builder.append("@" + username + " ");
                    editText_newpost.setText(builder);
                    editText_newpost.setSelection(builder.length());

                    character_lock = false;
                    userlist.add(userDetails.getUid());

                    List<String> mentions = editText_newpost.getMentions();
                    int stop = editText_newpost.getText().toString().lastIndexOf('@');

                    if (userDetails.getUid().equals(user.getUid())) {
                        editText_newpost.setText(editText_newpost.getText().toString().substring(0, stop));
                        editText_newpost.setSelection(editText_newpost.length());
                        try {
                            userlist.remove(mentions.size() - 1);
                            Toasty.custom(getApplicationContext(),"You cannot mention yourself", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                    true).show();
                        } catch (IndexOutOfBoundsException e) {

                        }
                    }

                    for (int i = 0; i < userlist.size() - 1; i++) {
                        if (mentions.get(i).trim().equals(username)) {
                            editText_newpost.setText(editText_newpost.getText().toString().substring(0, stop) + "@" + username + "_" + number_tag + " ");
                            editText_newpost.setSelection(editText_newpost.length());
                            number_tag++;
                        }
                        if (userlist.get(i).equals(userDetails.getUid())) {
                            editText_newpost.setText(editText_newpost.getText().toString().substring(0, stop));
                            editText_newpost.setSelection(editText_newpost.length());
                            try {
                                userlist.remove(mentions.size() - 1);
                                Toasty.custom(getApplicationContext(),"You cannot mention same user twice", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                        true).show();
                            } catch (IndexOutOfBoundsException e) {

                            }
                        }

                    }
                    is_mention = false;
                    mentionAdapter.clear();

                } else {

                }

            }
        });


        //editText_newpost accept max 5000 chars
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(5000);
        editText_newpost.setFilters(FilterArray);


        String newpost_hint = getResources().getString(R.string.post_hint);
        editText_newpost.setHint(Html.fromHtml("<big>" + newpost_hint + "</big>"));

        layout_image = findViewById(R.id.layout_image);

        image_upload = findViewById(R.id.image_upload);

        button_remove = findViewById(R.id.button_remove);
        button_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUri = null;
                resultUri = null;
                is_image = false;
                image_upload.setImageDrawable(null);
                layout_image.setVisibility(View.GONE);
                button_remove.setVisibility(View.GONE);
                Toasty.custom(getApplicationContext(),"Image removed", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                        true).show();

            }
        });


        edit_addimage = findViewById(R.id.edit_addimage);
        edit_addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_preview) {
                    Toasty.custom(getApplicationContext(),"Unable to add image. Kindly remove Pdf to proceed", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                } else if (is_pdf) {
                    Toasty.custom(getApplicationContext(),"Unable to add image. Kindly remove Pdf to proceed", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                } else {
                    getpermission(getApplicationContext(), NewPostActivity.this);
                }


            }
        });

        layout_pdf = findViewById(R.id.layout_pdf);
        textView_pdf = findViewById(R.id.textview_pdf);

        edit_addpdf = findViewById(R.id.edit_addpdf);
        edit_addpdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_preview) {
                    Toasty.custom(getApplicationContext(),"Unable to attach pdf. Kindly remove Image to proceed", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                } else if (is_image) {
                    Toasty.custom(getApplicationContext(),"Unable to attach pdf. Kindly remove Image to proceed", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                } else {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/pdf");
                    startActivityForResult(intent, 1212);
                }

            }
        });


        pdf_delete = findViewById(R.id.delete_icon);
        pdf_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uri = null;
                textView_pdf.setText(null);
                layout_pdf.setVisibility(View.GONE);
                Toasty.custom(getApplicationContext(),"Pdf file removed", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                        true).show();
                is_pdf = false;

            }
        });


        button_submit = findViewById(R.id.button_submit_post);
        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (editText_newpost.getText().toString().trim().isEmpty()) {
                    Toasty.custom(getApplicationContext(),"Post can't be blank. Please enter some text.", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                } else {

                    final Dialog progress = new Dialog(NewPostActivity.this);


                    progress.setContentView(R.layout.dialog_progress);
                    progress.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    progress.setCancelable(false);
                    progress.show();

                    final String key = mDatabase.child("Post").push().getKey();
                    if (imgUri != null || resultUri != null) {

                        final StorageReference childref = FirebaseStorage.getInstance().getReference().child("Posts").child("new_" + key);

                        UploadTask uploadTask = childref.putFile(compressedUri);

                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                // Continue with the task to get the download URL
                                return childref.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    downloadUrl = task.getResult();
                                    updatedata(key);
                                } else {
                                    // Handle failures
                                    // ...
                                }
                            }
                        });
                    } else if (uri != null) {
                        final StorageReference childref = FirebaseStorage.getInstance().getReference().child("Pdfs").child("pdf#" + key);
                        UploadTask uploadTask = childref.putFile(uri);

                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                // Continue with the task to get the download URL
                                return childref.getDownloadUrl();

                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    downloadpdfUrl = task.getResult();

                                    updatedata(key);
                                } else {
                                    Toasty.custom(getApplicationContext(),"Upload failed", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                            true).show();
                                }
                            }
                        });

                    } else {
                        updatedata(key);
                    }


                }


            }

        });


        //@ mention button implementation
        button_mention = findViewById(R.id.button_mention_friend);
        button_mention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_mention = true;
                editText_newpost.setText(editText_newpost.getText().toString() + "@");
                editText_newpost.setSelection(editText_newpost.getText().length());

            }

        });

        button_hashtag = findViewById(R.id.button_hashtag);
        button_hashtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_hashtag = true;
                editText_newpost.setText(editText_newpost.getText().toString() + "#");
                editText_newpost.setSelection(editText_newpost.getText().length());

            }
        });

        final LinkPreviewCallback linkPreviewCallback = new LinkPreviewCallback() {
            @Override
            public void onPre() {
                if (!is_preview) {
                    View child = getLayoutInflater().inflate(R.layout.layout_urlpreview, null);
                    layout_image.addView(child, 0);
                    layout_image.setVisibility(View.VISIBLE);
                    is_preview = true;
                }
            }

            @Override
            public void onPos(SourceContent sourceContent, boolean b) {

                if (sourceContent.isSuccess()) {
                    View view = layout_image.getChildAt(0);
                    TextView title = view.findViewById(R.id.sname);
                    try {
                        title.setText(sourceContent.getTitle());
                        TextView description = view.findViewById(R.id.sheadline);
                        description.setText(sourceContent.getDescription());
                        if (sourceContent.getImages().size() > 0) {
                            ImageView imageView = view.findViewById(R.id.imageButton_dp);
                            Glide
                                    .with(getApplicationContext())
                                    .load(sourceContent.getImages().get(0))
                                    .into(imageView);


                        }
                        ImageButton imageButton = view.findViewById(R.id.imageButton_cancel);
                        imageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                layout_image.removeViewAt(0);
                                is_preview = false;
                            }
                        });
                    }catch (NullPointerException e){
                        layout_image.removeViewAt(0);
                    }
                } else {
                    try {

                        if (layout_image.getChildAt(0).getId() == R.id.layout_urlpreview) {
                            layout_image.removeViewAt(0);
                        }

                    } catch (NullPointerException e) {

                    }
                    is_preview = false;
                }


            }
        };


        //listening every char
        editText_newpost.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (count > 0) {
                    if (s.charAt(s.length() - 1) == '@') {
                        try {
                            if (s.charAt(s.length() - 2) == ' ') {
                                editText_newpost.setMaxLines(1);
                                is_mention = true;
                                character_lock = true;
                            } else {
                                emailLock = true;
                            }
                        }catch (IndexOutOfBoundsException e){
                            is_mention = true;
                            character_lock = true;
                        }
                        return;
                    }
                    if (s.charAt(s.length() - 1) == '#') {
                        is_hashtag = true;
                        character_lock = true;
                        return;
                    }
                    if (s.charAt(s.length() - 1) == ' ') {
                        if (character_lock) {
                            character_lock = false;
                        }
                        if (is_hashtag) {
                            is_hashtag = false;
                        }if (emailLock){
                            userlist.add("1");
                            emailLock = false;
                        }

                    }
                    if (character_lock) {
                        if (is_mention) {
                            startSearch(s.toString().substring(s.toString().lastIndexOf("@") + 1));
                        } else if (is_hashtag) {
                            startSearch(s.toString().substring(s.toString().lastIndexOf("#") + 1));
                        }

                    }

                    textView_wordcount.setText(s.length() + "/5000");
                }


            }


            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                previousLength = s.length();
                if (count > 0) {
                    if (s.charAt(s.length() - 1) == '@' || s.charAt(s.length() - 1) == '#') {
                        character_lock = false;
                    }
                }

                previous_mentions = editText_newpost.getMentions();

            }

            public void afterTextChanged(Editable s) {

                textView_wordcount.setText(s.length() + "/5000");
                backSpace = previousLength > s.length();
                if (backSpace) {

                    after_mentions = editText_newpost.getMentions();

                    if (previous_mentions.size() == after_mentions.size()) {
                        if (!after_mentions.containsAll(previous_mentions)) {

                            for (int i = 0; i < previous_mentions.size(); i++) {
                                try {
                                    if (!(previous_mentions.get(i).equals(after_mentions.get(i)))) {
                                        try {
                                            userlist.remove(i);
                                        } catch (IndexOutOfBoundsException e) {

                                        }

                                        editText_newpost.setText(editText_newpost.getText().toString().replace("@" + after_mentions.get(i), ""));
                                        editText_newpost.setSelection(editText_newpost.length());
                                        editText_newpost.setMaxLines(Integer.MAX_VALUE);
                                        break;


                                    }
                                } catch (IndexOutOfBoundsException e) {
                                }
                            }
                        }


                    }
                }

                if (imgUri == null && resultUri == null && uri == null) {

                    List<String> hyperlink = editText_newpost.getHyperlinks();
                    if (hyperlink.size() > 0) {
                        Pattern urlPattern = Pattern.compile(
                                "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                                        + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                                        + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
                                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

                        if (urlPattern.matcher(hyperlink.get(0)).matches()) {

                            if(hyperlinks_count != hyperlink.get(0).length()) {
                                hyperlinks_count = hyperlink.get(0).length();
                                textCrawler.makePreview(linkPreviewCallback, hyperlink.get(0));
                            }
                        }
                    } else {
                        if (is_preview) {
                            try {
                                layout_image.removeViewAt(0);
                                layout_image.setVisibility(View.VISIBLE);
                            } catch (NullPointerException e) {

                            }
                        }
                        is_preview = false;

                    }
                }


            }
        });

        Intent i = getIntent();
        Bundle extras = i.getExtras();
        String action = i.getAction();

        // if this is from the share menu
        if (Intent.ACTION_SEND.equals(action)) {
            if (extras.containsKey(Intent.EXTRA_STREAM)) {
                imgUri = i.getParcelableExtra(Intent.EXTRA_STREAM);
                layout_image.setVisibility(View.VISIBLE);
                button_remove.setVisibility(View.VISIBLE);
                try {
                    File file = new File(imgUri.getPath());
                    File compressedFile = new Compressor(this).compressToFile(file);
                    compressedUri = Uri.fromFile(compressedFile);
                    Bitmap compressedBitmap = BitmapFactory.decodeFile(compressedFile.getAbsolutePath());
                    image_upload.setImageBitmap(compressedBitmap);
                } catch (IOException e) {
                }
            }
            if (extras.containsKey(Intent.EXTRA_TEXT)) {
                String sharedText = i.getStringExtra(Intent.EXTRA_TEXT);
                editText_newpost.setText(sharedText);
            }

        }


        //post guidelines button
        button_tips = findViewById(R.id.button_tips);
        Button customButton = (Button) getLayoutInflater().inflate(R.layout.view_custom_button, null);
        customButton.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/roboto.light.ttf"));

        if (!prefManager.getTransPostTips()) {
            prefManager.setTransPostTips(true);


            TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            paint.setTextSize(getResources().getDimension(R.dimen.abc_text_size_body_1_material));
            paint.setColor(Color.WHITE);
            paint.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/roboto.light.ttf"));

            TextPaint title = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            title.setTextSize(getResources().getDimension(R.dimen.abc_text_size_headline_material));
            title.setColor(Color.RED);
            title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/roboto.light.ttf"));


            Target viewTarget = new ViewTarget(R.id.button_tips, this);
            new ShowcaseView.Builder(this)
                    .withMaterialShowcase()
                    .setStyle(R.style.CustomShowcaseTheme)
                    .setTarget(viewTarget)
                    .setContentTitle("\n \n \nPost guidelines!")
                    .setContentText("\nHere are few guidelines to create engaging and useful posts.")
                    .setContentTitlePaint(title)
                    .setContentTextPaint(paint)
                    .setShowcaseEventListener(
                            new SimpleShowcaseEventListener() {
                                @Override
                                public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                                    Intent intent = new Intent(getApplicationContext(), PostTipsActivity.class);
                                    startActivity(intent);
                                }
                            }
                    ).replaceEndButton(customButton)
                    .build();
        }

        button_tips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), PostTipsActivity.class);
                startActivity(intent);

            }

        });


    }

    //searching users
    public void startSearch(String text) {

        key1 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").push().getKey();
        if (is_mention) {
            store_search = text.trim();

            HashMap<String, Object> map = new HashMap<>();

            map.put("index", "firebase1");
            map.put("type", "user");
            map.put("/body/query/wildcard/name/value", "*" + text.trim().toLowerCase() + "*");


            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                try {
                    imm.showSoftInputFromInputMethod(view.getWindowToken(), 0);
                } catch (NullPointerException e) {

                }
            }


            FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("search").child("request").child(key1).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    listensearch();
                }
            });
        } else if (is_hashtag) {

        }
    }

    private void listensearch() {

        FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("search").child("response").child(key1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()) {
                    if (is_mention) {
                        try {
                            if (dataSnapshot.child("hits").child("total").getValue().toString().equals("0")) {

                            } else {

                                mentionAdapter.clear();
                                for (DataSnapshot childsnapshot : dataSnapshot.child("hits").child("hits").getChildren()) {
                                    if (childsnapshot.child("_source").child("name").exists() && childsnapshot.child("_source").child("headline").exists()) {

                                        try {
                                            UserDetails userDetails = new UserDetails();
                                            userDetails.setName(childsnapshot.child("_source").child("name").getValue().toString());
                                            userDetails.setUid(childsnapshot.child("_id").getValue().toString());
                                            userDetails.setHeadline(childsnapshot.child("_source").child("headline").getValue().toString());
                                            userDetails.setImageurl(childsnapshot.child("_source").child("thumb").getValue().toString());
                                            mentionAdapter.add(userDetails);
                                        } catch (NullPointerException e) {
                                        }

                                    }
                                }
                                mentionAdapter.notifyDataSetChanged();
                            }
                        } catch (NullPointerException e) {
                        }
                    } else if (is_hashtag) {

                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //Submit Post Contd.
    public void updatedata(final String key) {


        HashMap<String, Object> tempmap = new HashMap<>();

        tempmap.put("uid", user.getUid());
        tempmap.put("timestamp", FireService.getToday());

        Post post;
        String a=android.text.TextUtils.join(",", userlist);


        try {
            post = new Post(user.getUid(), FireService.getToday(), editText_newpost.getText().toString().trim(), downloadUrl.toString(), null, null);

        } catch (NullPointerException e) {
            try {

                post = new Post(user.getUid(), FireService.getToday(), editText_newpost.getText().toString().trim(), null, null, downloadpdfUrl.toString());
            } catch (NullPointerException exp) {

                post = new Post(user.getUid(), FireService.getToday(), editText_newpost.getText().toString().trim(), null, null, null);
            }
        }


        mDatabase.child("Post").child(key).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                HashMap<String, Object> map = new HashMap<>();

                if (userlist.size() > 0) {
                    map.put("mention_uid", android.text.TextUtils.join(",", userlist));
                }
                List<String> hashtags = editText_newpost.getHashtags();
                if (hashtags.size() > 0) {
                    map.put("mention_tag", android.text.TextUtils.join(",", hashtags));
                }

                if (!map.isEmpty()) {
                    KrigerConstants.postRef.child(key).updateChildren(map);
                }

                prefManager.setIsnewconnection(true);


                Toasty.custom(getApplicationContext(),"Posted successfully. Kindly pull down to refresh ", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                        true).show();
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);


            }
        });


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {

        if (!editText_newpost.getText().toString().trim().isEmpty()) {

            dialogBuilder("This post will be lost.\n" +
                    "Kindly finish your post before leaving.", 1, true, "Complete", "Leave", null).show();


        } else {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            try {
                Log.d("Intent", getIntent().getExtras().getString("source"));
            } catch (NullPointerException e) {
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            }
            startActivity(intent);

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_createpost, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == PERMISSION_ALL) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getpermission(getApplicationContext(), NewPostActivity.this);
            } else {

                String permission = permissions[0];

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    boolean showRationale = shouldShowRequestPermissionRationale(permission);
                    if (!showRationale) {

                        dialogBuilder("You have denied the media permission. Please enable it from the 'Settings' of your phone.", 3, false, "Go to settings", null, null).show();


                    } else {

                        dialogBuilder("Oops!No media access?\n" +
                                "Please allow access to use our app better! ", 2, false, "OK", null, null).show();

                    }


                }


            }


        }


    }


    private Dialog dialogBuilder(String text, final int type, Boolean isNegative, String posText, String negText, Object[] arrayList) {

        final Dialog dialog = new Dialog(NewPostActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.layout_dialog);

        TextView textView = dialog.findViewById(R.id.dialogue_text);
        textView.setText(text);


        Button posButton = dialog.findViewById(R.id.button_positive);
        posButton.setText(posText);
        posButton.setVisibility(View.VISIBLE);
        posButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (type == 1) {
                    dialog.dismiss();

                } else if (type == 2) {
                    dialog.dismiss();
                    getpermission(getApplicationContext(), NewPostActivity.this);

                } else if (type == 3) {
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

        if (isNegative) {

            Button negButton = dialog.findViewById(R.id.button_negative);
            negButton.setText(negText);
            negButton.setVisibility(View.VISIBLE);
            negButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    try {
                        Log.d("Intent", getIntent().getExtras().getString("source"));
                    } catch (NullPointerException e) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    }
                    startActivity(intent);
                }
            });
        }


        return dialog;

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                resultUri = result.getUri();
                try {

                    layout_image.setVisibility(View.VISIBLE);
                    button_remove.setVisibility(View.VISIBLE);
                    File file = new File(resultUri.getPath());
                    File compressedFile = new Compressor(this).compressToFile(file);
                    compressedUri = Uri.fromFile(compressedFile);
                    Bitmap compressedBitmap = BitmapFactory.decodeFile(compressedFile.getAbsolutePath());
                    image_upload.setImageBitmap(compressedBitmap);
                } catch (IOException e) {

                }


                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.scrollTo(0, scrollView.getBottom());
                    }
                });
                is_image = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toasty.custom(getApplicationContext(),error.toString(), R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                        true).show();
            }
        }

        if (requestCode == 1212) {

            if (resultCode == RESULT_OK) {
                // Get the Uri of the selected file
                uri = data.getData();
                String uriString = uri.toString();
                File myFile = new File(uriString);
                String path = myFile.getAbsolutePath();
                String displayName = null;

                if (uriString.startsWith("content://")) {
                    Cursor cursor = null;
                    try {
                        cursor = getApplicationContext().getContentResolver().query(uri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        }
                    } finally {
                        cursor.close();
                    }
                } else if (uriString.startsWith("file://")) {
                    displayName = myFile.getName();
                }

                layout_pdf.setVisibility(View.VISIBLE);
                textView_pdf.setText(displayName);
                is_pdf = true;


            }

        }


    }


}


