package in.kriger.newkrigercampus.activities;
import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.firebase.ui.database.BuildConfig;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nex3z.flowlayout.FlowLayout;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabSelectListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import es.dmoral.toasty.Toasty;
import id.zelory.compressor.Compressor;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.adapters.Adapter_List;
import in.kriger.newkrigercampus.bottomfragments.Groups;
import in.kriger.newkrigercampus.bottomfragments.Home;
import in.kriger.newkrigercampus.bottomfragments.Krigers;
import in.kriger.newkrigercampus.bottomfragments.MarketPlace;
import in.kriger.newkrigercampus.bottomfragments.Notifications;
import in.kriger.newkrigercampus.extras.CityStateCountry;
import in.kriger.newkrigercampus.classes.Contacts;
import in.kriger.newkrigercampus.classes.DataCounters;
import in.kriger.newkrigercampus.classes.UserDetails;
import in.kriger.newkrigercampus.database.DatabaseHelper;
import in.kriger.newkrigercampus.extras.ContactInterface;
import in.kriger.newkrigercampus.extras.PrefManager;
import in.kriger.newkrigercampus.extras.TypefaceUtil;
import in.kriger.newkrigercampus.fragments.ContactList;
import in.kriger.newkrigercampus.services.KrigerConstants;
import in.kriger.newkrigercampus.services.FireService;

import static in.kriger.newkrigercampus.activities.SplashScreen.PERMISSION_ALL;


public class HomeActivity extends AppCompatActivity implements ContactInterface {


    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final Calendar myCalendar = Calendar.getInstance();


    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private int totalCount;

    private static final String TAG = HomeActivity.class.getSimpleName();

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    public static final int OPEN_SETTINGS = 101;

    BottomBar bottomBar;

    private ArrayList<String> resultList = new ArrayList<>();

    int tabID = 0;

    int k = 0;


    HashMap<DatabaseReference, String> tosync = new HashMap<>();

    private Uri uri = null;
    private ImageView profile_pic;

    EditText bio, birthday;
    EditText hometown;
    private Spinner state, country;
    Spinner latestedu;
    private TextView textlim_bio;

    private Button button_login_trouble;


    final DatabaseReference database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/");
    DatabaseReference myRef = database.child("TagList");

    Typeface typeface;



    private PrefManager prefManager;

    DatabaseHelper db;

    int count = 0;

    Dialog progress;

    Boolean is_start;

    private long startTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;

    private Boolean connection = false;

    Uri compressedUri;
    File squarefile;

    Boolean first_page = true;

    Boolean is_photo = false;

    CityStateCountry cityStateCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = new DatabaseHelper(getApplicationContext());

        myRef.keepSynced(true);
        typeface = new TypefaceUtil().getfontbold(getApplicationContext());

        progress = new Dialog(HomeActivity.this);
        progress.setContentView(R.layout.dialog_progress);
        progress.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        prefManager = new PrefManager(getApplicationContext());


        bottomBar =  findViewById(R.id.bottomBar);



        try {

            //Set Active Page on Intent
            if (getIntent().getExtras().getString("screen").equals("krigers")) {
                bottomBar.selectTabAtPosition(3);
            } else if (getIntent().getExtras().getString("screen").equals("groups")) {
                bottomBar.selectTabAtPosition(2);
            } else if (getIntent().getExtras().getString("screen").equals("notification")) {
                bottomBar.selectTabAtPosition(4);
            }else if (getIntent().getExtras().getString("screen").equals("resources")){
                bottomBar.selectTabAtPosition(1);
            }

        } catch (NullPointerException e) {
        }


        try {
            is_start = getIntent().getExtras().getBoolean("is_start");
        } catch (NullPointerException e) {
            is_start = false;
        }


        try{
            int visit = getIntent().getExtras().getInt("visit");
            if (visit == 2) {
                showDialog();
            }
        }catch (NullPointerException e){

        }


        checkDataSync();
        checkrefreshweek();





        //Check for latest version
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        final FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);


        long cacheExpiration = 1800;
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            mFirebaseRemoteConfig.activateFetched();
                            try {

                                int playStoreVersionCode = Integer.valueOf(FirebaseRemoteConfig.getInstance().getString(
                                        "android_latest_version_code"));

                                try {
                                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                                    int currentAppVersionCode = pInfo.versionCode;
                                    Log.d("force update current", String.valueOf(currentAppVersionCode));
                                    if (playStoreVersionCode > currentAppVersionCode) {

                                        FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("Latest_Feature").child("1").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                String features = dataSnapshot.getValue().toString();
                                                dialogBuilder("Kindly update the app to continue.\n\nNew features are \n" + features, 16, false, "OK", "null", null);

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });


                                    }
                                } catch (PackageManager.NameNotFoundException e) {

                                }

                                final int databaseVersionCode = Integer.valueOf(FirebaseRemoteConfig.getInstance().getString("database_latest_version_code"));

                                if (databaseVersionCode != prefManager.getDatabaseVersion()) {
                                    FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("Database_Code").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                ArrayList<String> list = new ArrayList<>();
                                                for (DataSnapshot childsnap : dataSnapshot.child("expert").getChildren()) {
                                                    list.add(childsnap.getValue().toString());
                                                }
                                                db.insertAllCounters(list, DataCounters.EXPERT);
                                            }

                                            prefManager.setDatabaseVersion(databaseVersionCode);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }


                            } catch (NumberFormatException e) {

                            }

                        }
                    }
                });


        if (is_start) {

            //Update Notification Token
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    String deviceToken = instanceIdResult.getToken();
                    KrigerConstants.userRef.child(user.getUid()).child("profile_token").setValue(deviceToken);

                }
            });

            database.child("User_Per_Day").child(FireService.getToday().substring(0, 8)).child(user.getUid()).setValue(FireService.getToday());

        }


        //On bottom tab selection
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {


                    Fragment fragment = null;
                    tabID = tabId;

                    Button customButton = (Button) getLayoutInflater().inflate(R.layout.view_custom_button, null);
                    customButton.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/roboto.light.ttf"));


                    if (tabId == R.id.menu_home) {

                        if (!prefManager.isFirstTimeLaunch()) {
                            prefManager.setFirstTimeLaunch(true);

                            TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
                            paint.setTextSize(getResources().getDimension(R.dimen.abc_text_size_body_1_material));
                            paint.setColor(Color.WHITE);
                            paint.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/roboto.light.ttf"));

                            TextPaint title = new TextPaint(Paint.ANTI_ALIAS_FLAG);
                            title.setTextSize(getResources().getDimension(R.dimen.abc_text_size_headline_material));
                            title.setColor(Color.RED);
                            title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/roboto.light.ttf"));


                            Target viewTarget = new ViewTarget(R.id.menu_home, HomeActivity.this);
                            new ShowcaseView.Builder(HomeActivity.this)
                                    .withMaterialShowcase()
                                    .setStyle(R.style.CustomShowcaseTheme)
                                    .setTarget(viewTarget)
                                    .setContentTitle("\n \n \nPersonalised Timeline!")
                                    .setContentText("\nSee the posts from your network.\n" +
                                            "Share useful posts on other social networks.")
                                    .setContentTitlePaint(title)
                                    .setContentTextPaint(paint)
                                    .setShowcaseEventListener(
                                            new SimpleShowcaseEventListener() {
                                                @Override
                                                public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                                                    bottomBar.selectTabAtPosition(1);

                                                }
                                            }
                                    ).replaceEndButton(customButton)
                                    .build();
                        }

                        fragment = new Home();
                        // For moving form other tabs
                        Bundle bundle = new Bundle();
                        bundle.putChar("is_open", 'y');
                        fragment.setArguments(bundle);


                    } else if (tabId == R.id.menu_krigers) {

                        if (!prefManager.getTransKrigers()) {
                            prefManager.setTransKrigers(true);

                            TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
                            paint.setTextSize(getResources().getDimension(R.dimen.abc_text_size_body_1_material));
                            paint.setColor(Color.WHITE);
                            paint.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/roboto.light.ttf"));

                            TextPaint title = new TextPaint(Paint.ANTI_ALIAS_FLAG);
                            title.setTextSize(getResources().getDimension(R.dimen.abc_text_size_headline_material));
                            title.setColor(Color.RED);
                            title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/roboto.light.ttf"));


                            RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                            lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                            int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
                            lps.setMargins(margin, margin, margin, margin);


                            if(prefManager.getAccountType() > 0){
                                Target viewTarget = new ViewTarget(R.id.menu_krigers, HomeActivity.this);
                                ShowcaseView sv = new ShowcaseView.Builder(HomeActivity.this)
                                        .withMaterialShowcase()
                                        .setStyle(R.style.CustomShowcaseTheme)
                                        .setTarget(viewTarget)
                                        .setContentTitle("\n \n \nYour Network!")
                                        .setContentText("\nConnect with Students and Educators.\n"+"Connect & Earn.")
                                        .setContentTitlePaint(title)
                                        .setContentTextPaint(paint)
                                        .setShowcaseEventListener(
                                                new SimpleShowcaseEventListener() {
                                                    @Override
                                                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                                                        bottomBar.selectTabAtPosition(0);
                                                        transProfile();
                                                    }
                                                }
                                        ).replaceEndButton(customButton)
                                        .build();
                                sv.setButtonPosition(lps);

                            }else {
                                Target viewTarget = new ViewTarget(R.id.menu_krigers, HomeActivity.this);
                                ShowcaseView sv = new ShowcaseView.Builder(HomeActivity.this)
                                        .withMaterialShowcase()
                                        .setStyle(R.style.CustomShowcaseTheme)
                                        .setTarget(viewTarget)
                                        .setContentTitle("\n \n \nYour Network!")
                                        .setContentText("\nConnect with Educators and Students.\n"+"Connect & Learn.")
                                        .setContentTitlePaint(title)
                                        .setContentTextPaint(paint)
                                        .setShowcaseEventListener(
                                                new SimpleShowcaseEventListener() {
                                                    @Override
                                                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                                                        bottomBar.selectTabAtPosition(0);
                                                        transProfile();
                                                    }
                                                }
                                        ).replaceEndButton(customButton)
                                        .build();
                                sv.setButtonPosition(lps);

                            }

                        }

                        BottomBarTab bottomBarTab = bottomBar.getTabAtPosition(3);
                        bottomBarTab.removeBadge();
                        fragment = new Krigers();

                        Bundle bundle = new Bundle();
                        bundle.putInt("added_connections", totalCount);
                        fragment.setArguments(bundle);


                    } else if (tabId == R.id.menu_groups) {

                        if (!prefManager.getTransGroups()) {
                            prefManager.setTransGroups(true);

                            TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
                            paint.setTextSize(getResources().getDimension(R.dimen.abc_text_size_body_1_material));
                            paint.setColor(Color.WHITE);
                            paint.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/roboto.light.ttf"));

                            TextPaint title = new TextPaint(Paint.ANTI_ALIAS_FLAG);
                            title.setTextSize(getResources().getDimension(R.dimen.abc_text_size_headline_material));
                            title.setColor(Color.RED);
                            title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/roboto.light.ttf"));

                            if(prefManager.getAccountType() > 0){
                                Target viewTarget = new ViewTarget(R.id.menu_groups, HomeActivity.this);
                                new ShowcaseView.Builder(HomeActivity.this)
                                        .withMaterialShowcase()
                                        .setStyle(R.style.CustomShowcaseTheme)
                                        .setTarget(viewTarget)
                                        .setContentTitle("\n \n \nAcademic Groups!")
                                        .setContentText("\nCreate group.\n" +
                                                "Discuss & Engage.\n" + "Build your Brand.")
                                        .setContentTitlePaint(title)
                                        .setContentTextPaint(paint)
                                        .setShowcaseEventListener(
                                                new SimpleShowcaseEventListener() {
                                                    @Override
                                                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                                                        bottomBar.selectTabAtPosition(3);
                                                    }
                                                }
                                        ).replaceEndButton(customButton)
                                        .build();
                            }else {
                                Target viewTarget = new ViewTarget(R.id.menu_groups, HomeActivity.this);
                                new ShowcaseView.Builder(HomeActivity.this)
                                        .withMaterialShowcase()
                                        .setStyle(R.style.CustomShowcaseTheme)
                                        .setTarget(viewTarget)
                                        .setContentTitle("\n \n \nAcademic Groups!")
                                        .setContentText("\nCreate or Join Academic Groups with unlimited membership.\n" +
                                                "Discuss, learn and succeed together.")
                                        .setContentTitlePaint(title)
                                        .setContentTextPaint(paint)
                                        .setShowcaseEventListener(
                                                new SimpleShowcaseEventListener() {
                                                    @Override
                                                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                                                        bottomBar.selectTabAtPosition(3);
                                                    }
                                                }
                                        ).replaceEndButton(customButton)
                                        .build();
                            }



                        }


                        BottomBarTab bottomBarTab = bottomBar.getTabAtPosition(2);
                        bottomBarTab.removeBadge();
                        fragment = new Groups();

                    } else if (tabId == R.id.menu_notification) {

                        BottomBarTab bottomBarTab = bottomBar.getTabAtPosition(4);
                        bottomBarTab.removeBadge();
                        fragment = new Notifications();

                    } else if (tabId == R.id.menu_market) {

                        if (!prefManager.getTransMarket()) {
                            prefManager.setTransMarket(true);

                            TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
                            paint.setTextSize(getResources().getDimension(R.dimen.abc_text_size_body_1_material));
                            paint.setColor(Color.WHITE);
                            paint.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/roboto.light.ttf"));

                            TextPaint title = new TextPaint(Paint.ANTI_ALIAS_FLAG);
                            title.setTextSize(getResources().getDimension(R.dimen.abc_text_size_headline_material));
                            title.setColor(Color.RED);
                            title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/roboto.light.ttf"));

                            if(prefManager.getAccountType() > 0){
                                Target viewTarget = new ViewTarget(R.id.menu_market, HomeActivity.this);
                                new ShowcaseView.Builder(HomeActivity.this)
                                        .withMaterialShowcase()
                                        .setStyle(R.style.CustomShowcaseTheme)
                                        .setTarget(viewTarget)
                                        .setContentTitle("\n \n \nEducational Resources!")
                                        .setContentText("\nSpread education & you brand.\n" + "List your classes, materials, books.\n" + "Get enquiries from Students & Parents across India.")
                                        .setContentTitlePaint(title)
                                        .setContentTextPaint(paint)
                                        .setShowcaseEventListener(
                                                new SimpleShowcaseEventListener() {
                                                    @Override
                                                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                                                        bottomBar.selectTabAtPosition(2);
                                                    }
                                                }
                                        ).replaceEndButton(customButton)
                                        .build();
                            }else{
                                Target viewTarget = new ViewTarget(R.id.menu_market, HomeActivity.this);
                                new ShowcaseView.Builder(HomeActivity.this)
                                        .withMaterialShowcase()
                                        .setStyle(R.style.CustomShowcaseTheme)
                                        .setTarget(viewTarget)
                                        .setContentTitle("\n \n \nEducational Resources!")
                                        .setContentText("\nEnquire for classes, books, mentorship.\n")
                                        .setContentTitlePaint(title)
                                        .setContentTextPaint(paint)
                                        .setShowcaseEventListener(
                                                new SimpleShowcaseEventListener() {
                                                    @Override
                                                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                                                        bottomBar.selectTabAtPosition(2);
                                                    }
                                                }
                                        ).replaceEndButton(customButton)
                                        .build();
                            }


                        }


                        BottomBarTab bottomBarTab = bottomBar.getTabAtPosition(1);
                        bottomBarTab.removeBadge();
                        fragment = new MarketPlace();
                        try{
                            if(getIntent().getExtras().getString("tab").equals("my")){
                                Bundle bundle = new Bundle();
                                bundle.putString("tab","my");
                                fragment.setArguments(bundle);
                            }
                        }catch (NullPointerException e){}


                    }


                    try {
                        // Insert the fragment by replacing any existing fragment
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commitAllowingStateLoss();
                    } catch (NullPointerException e) {

                    }


            }


        });

    }

    private  void transProfile(){


        if (!prefManager.getTransProfile()) {
            prefManager.setTransProfile(true);


            Button customButton1 = (Button) getLayoutInflater().inflate(R.layout.view_custom_button1, null);
            customButton1.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/roboto.light.ttf"));


            TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            paint.setTextSize(getResources().getDimension(R.dimen.abc_text_size_body_1_material));
            paint.setColor(Color.WHITE);
            paint.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/roboto.light.ttf"));

            TextPaint title = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            title.setTextSize(getResources().getDimension(R.dimen.abc_text_size_headline_material));
            title.setColor(Color.RED);
            title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/roboto.light.ttf"));

            RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);



            View view = getLayoutInflater().inflate(R.layout.fragment_home,null);

            ShowcaseView sv = new ShowcaseView.Builder(HomeActivity.this)
                    .withMaterialShowcase()
                    .setStyle(R.style.CustomShowcaseTheme)
                    .setTarget(new ViewTarget(view.findViewById(R.id.profile_photo)))
                    .setContentTitle("\n \n \nProfile!")
                    .setContentText("\nSee the profile.\n")
                    .setContentTitlePaint(title)
                    .setContentTextPaint(paint)
                    .setShowcaseEventListener(
                            new SimpleShowcaseEventListener() {
                                @Override
                                public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                                    bottomBar.selectTabAtPosition(0);
                                    initiateFirstVisit();

                                }
                            }
                    ).replaceEndButton(customButton1)
                    .build();
            sv.setButtonPosition(lps);

        }

    }

    //fill Profile and bio of First visit
    public void initiateFirstVisit() {


        final Dialog dialog = new Dialog(HomeActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_profilebio_update1);
        dialog.setCancelable(false);

        profile_pic =  dialog.findViewById(R.id.profile_photo);
        bio =  dialog.findViewById(R.id.edittext_bio);
        String req3 = getResources().getString(R.string.about_me);
        bio.setHint(Html.fromHtml(req3));
        textlim_bio = dialog.findViewById(R.id.textlim_bio);
        button_login_trouble =  dialog.findViewById(R.id.button_login_trouble);

        birthday = dialog.findViewById(R.id.edittext_birthday);
        String req7 = "";
        if (prefManager.getAccountType() > 1){
            req7 = getResources().getString(R.string.date_of_foundation);
        }else{
            req7 = getResources().getString(R.string.birthday);
        }

        birthday.setHint(Html.fromHtml(req7));


        final TextView pro_text =  dialog.findViewById(R.id.dialogue_text);
        String req1 = getResources().getString(R.string.upload_profile_picture);
        pro_text.setText(Html.fromHtml(req1));

        final TextView txt_req =  dialog.findViewById(R.id.textview_req);
        String req = getResources().getString(R.string.mandtory_fields);
        txt_req.setText(Html.fromHtml(req));

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getpermission();
            }
        });

        //set filter to disable emoticons on keyboard and accept max 50 chars
        InputFilter[] FilterArray = new InputFilter[1];
        bio.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(50), new EmojiExcludeFilter()});

        bio.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                textlim_bio.setText(String.valueOf(50 - bio.getText().length()));
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

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };


        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new DatePickerDialog(HomeActivity.this, android.R.style.Theme_Holo_Light_Dialog, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });



//        //Trouble Logging Interactions
        button_login_trouble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                havingTrouble();
            }
        });



        Button posButton =  dialog.findViewById(R.id.button_positive);
        posButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                if (uri == null) {
                    Toasty.custom(getApplicationContext(), "Kindly upload your Profile Picture", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                } else if (bio.getText().toString().trim().isEmpty() || birthday.getText().toString().trim().isEmpty()) {
                    Toasty.custom(getApplicationContext(), "Please fill required fields", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                } else {
                    secondFirstVisit();
                    dialog.dismiss();
                }
            }
        });

        dialog.show();


    }


 //Fill state, country, hometown of first visit
    private void secondFirstVisit() {


        final Dialog dialog = new Dialog(HomeActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_profilebio_update2);
        dialog.setCancelable(false);



        button_login_trouble =  dialog.findViewById(R.id.button_login_trouble);

        final TextView txt_req =  dialog.findViewById(R.id.textview_req);
        String req = getResources().getString(R.string.mandtory_fields);
        txt_req.setText(Html.fromHtml(req));


        hometown =  dialog.findViewById(R.id.edittext_hometown);
        hometown.setHint(Html.fromHtml(getResources().getString(R.string.city)));
        cityStateCountry = new CityStateCountry(getApplicationContext());
        country =  dialog.findViewById(R.id.edittext_country);
        ArrayAdapter<String> adapter_country =
                new ArrayAdapter<String>(this, R.layout.country_state_list_item, cityStateCountry.getCountries());
        adapter_country.setDropDownViewResource(R.layout.spinner_item);
        country.setAdapter(adapter_country);
        country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<String> adapter_states1 =
                        new ArrayAdapter<String>(getApplicationContext(), R.layout.country_state_list_item, cityStateCountry.getStates(String.valueOf(cityStateCountry.getCountryCode(position))));
                state.setAdapter(adapter_states1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        state =  dialog.findViewById(R.id.edittext_state);
        final ArrayAdapter<String> adapter_states =
                new ArrayAdapter<String>(getApplicationContext(), R.layout.country_state_list_item, cityStateCountry.getStates("0"));
        adapter_states.setDropDownViewResource(R.layout.spinner_item);
        state.setAdapter(adapter_states);
        //Trouble Logging Interactions
        button_login_trouble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                havingTrouble();

            }
        });


        Button posButton =  dialog.findViewById(R.id.button_positive);
        posButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                if (hometown.getText().length() == 0 || cityStateCountry.getStateCode(state.getSelectedItemPosition()) == 0 || country.getSelectedItemPosition() == 0) {
                    Toasty.custom(getApplicationContext(), "Please fill required fields", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                } else {

                    thirdFirstVisit();
                    dialog.dismiss();
                }
            }
        });

        dialog.show();


    }


 //Fill latest education and birthday of first visit
    private void thirdFirstVisit() {


        final Dialog dialog = new Dialog(HomeActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_profilebio_update3);
        dialog.setCancelable(false);


        final TextView pro_text =  dialog.findViewById(R.id.dialogue_text);
        String req1 = getResources().getString(R.string.upload_current_qualification);
        pro_text.setText(Html.fromHtml(req1));


        button_login_trouble =  dialog.findViewById(R.id.button_login_trouble);


        final TextView txt_req =  dialog.findViewById(R.id.textview_req);
        String req = getResources().getString(R.string.mandtory_fields);
        txt_req.setText(Html.fromHtml(req));


        RelativeLayout qualification_layout = dialog.findViewById(R.id.qualification_layout);
        latestedu =  dialog.findViewById(R.id.edittext_qualification);
        latestedu.setPrompt("Current Qualification");
        if (prefManager.getAccountType() > 1){
            qualification_layout.setVisibility(View.GONE);
        }
        String[] qualification = getResources().getStringArray(R.array.list_edu);
        ArrayAdapter<String> adapter_edu =
                new ArrayAdapter<String>(this, R.layout.country_state_list_item, qualification);
        adapter_edu.setDropDownViewResource(R.layout.spinner_item);
        latestedu.setAdapter(adapter_edu);

        final ArrayList<Integer> selected_subject_choices = new ArrayList<>();

        final ArrayList<Integer> selected_exam_choices = new ArrayList<>();

        final String[] choicelist_exams,choicelist_subjects;

        choicelist_subjects = getResources().getStringArray(R.array.list_sub);
        choicelist_exams = getResources().getStringArray(R.array.list_exams);

        /*TextView textView = dialog.findViewById(R.id.tool);
        TextView notice = dialog.findViewById(R.id.notice);
        TextView notice1 = dialog.findViewById(R.id.notice1);

        if (prefManager.getAccountType() > 0) {
            if (first_page) {
                textView.setText("Subjects you teach");
                notice.setText("Select all subjects you teach.");
                notice1.setText("Atleast one entry is required.");

            } else {
                textView.setText("Exams you teach");
                notice.setText("Select all exams you teach for.");
                notice1.setText("Atleast one entry is required.");
            }
        } else {
            if (first_page) {
                textView.setText("Subjects you study");
                notice.setText("Select all subjects you are interested in.");
                notice1.setText("Atleast one entry is required.");
            } else {
                textView.setText("Exams you prepare for");
                notice.setText("Select all exams you are preparing for.");
                notice1.setText("Atleast one entry is required.");
            }
        }*/

        final AutoCompleteTextView subjects_choice = dialog.findViewById(R.id.subjects_choice);
        ArrayAdapter<String> adapter_subjects =
                new ArrayAdapter<String>(getApplicationContext(), R.layout.prepration_list_item, choicelist_subjects);
        subjects_choice.setAdapter(adapter_subjects);
        subjects_choice.setHint("Search any subjects");


        final AutoCompleteTextView exams_choice = dialog.findViewById(R.id.exams_choice);
        ArrayAdapter<String> adapter_exams =
                new ArrayAdapter<String>(getApplicationContext(), R.layout.prepration_list_item, choicelist_exams);
        exams_choice.setAdapter(adapter_exams);
        exams_choice.setHint("Search any exams");

        TextView text_subjects = dialog.findViewById(R.id.text_subjects);
        TextView text_exams = dialog.findViewById(R.id.text_exams);

        if (prefManager.getAccountType() > 0){
            text_subjects.setText("Subjects you teach:");
            text_exams.setText("Exams you teach:");
        }else{
            text_subjects.setText("Subjects you study:");
            text_exams.setText("Exams you prepare for:");
        }


        final FlowLayout layout_exams = dialog.findViewById(R.id.layout_flow_exams);
        final FlowLayout layout_subjects = dialog.findViewById(R.id.layout_flow_subject);

        subjects_choice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                if (!selected_subject_choices.contains(Arrays.asList(choicelist_subjects).indexOf(selected))) {

                    LinearLayout layout1 = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_flow_item, layout_subjects, false);
                    TextView textView1 = layout1.findViewById(R.id.name);
                    textView1.setText(selected);
                    ImageView imageView = layout1.findViewById(R.id.buttonExitIcon);
                    int value = Arrays.asList(choicelist_subjects).indexOf(selected);
                    imageView.setId(value);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int index = selected_subject_choices.indexOf(v.getId());
                            layout_subjects.removeViewAt(index);
                            selected_subject_choices.remove(index);

                        }
                    });
                    layout_subjects.addView(layout1);
                    subjects_choice.setText("");
                    selected_subject_choices.add(value);

                } else {
                    Toasty.custom(getApplicationContext(), "Item already selected", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                    subjects_choice.setText("");
                }
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });

        exams_choice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                if (!selected_exam_choices.contains(Arrays.asList(choicelist_exams).indexOf(selected))) {

                    LinearLayout layout1 = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_flow_item, layout_exams, false);
                    TextView textView1 = layout1.findViewById(R.id.name);
                    textView1.setText(selected);
                    ImageView imageView = layout1.findViewById(R.id.buttonExitIcon);
                    int value = Arrays.asList(choicelist_exams).indexOf(selected);
                    imageView.setId(value);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int index = selected_exam_choices.indexOf(v.getId());
                            layout_exams.removeViewAt(index);
                            selected_exam_choices.remove(index);

                        }
                    });
                    layout_exams.addView(layout1);
                    exams_choice.setText("");
                    selected_exam_choices.add(value);

                } else {
                    Toasty.custom(getApplicationContext(), "Item already selected", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                    exams_choice.setText("");
                }

                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


            }
        });






//        //Trouble Logging Interactions
        button_login_trouble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                havingTrouble();

            }
        });


        Button posButton = (Button) dialog.findViewById(R.id.button_positive);
        posButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(latestedu.getSelectedItemPosition() == 0 && prefManager.getAccountType() < 2){
                    Toasty.custom(getApplicationContext(), "Please select current qualification", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                } else if (selected_exam_choices.size()==0 || selected_subject_choices.size() == 0){
                    Toasty.custom(getApplicationContext(), "Atleast one entry required in exams and subjects", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                } else {
                    updateProfile(selected_exam_choices,selected_subject_choices);
                    dialog.dismiss();
                }
            }
        });

        dialog.show();


    }



    @Override
    public void onBackPressed() {


        if (tabID == R.id.menu_home) {


            dialogBuilder("Are you sure you want to exit the app?", 3, true, "No", "Yes", null);

        } else {
            bottomBar.selectTabAtPosition(0);
        }
    }


    private void checkDataSync() {


        KrigerConstants.user_counterRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    totalCount = 0;
                    Boolean isKriger = false;
                    if (dataSnapshot.child("count_connections").exists()) {
                        if (Integer.valueOf(dataSnapshot.child("count_connections").getValue().toString()) != prefManager.getCounterConnections()) {
                            totalCount = (Integer.valueOf(dataSnapshot.child("count_connections").getValue().toString())) - prefManager.getCounterConnections();
                            isKriger = true;
                            tosync.put(KrigerConstants.connectionRef.child(user.getUid()), DataCounters.CONNECTIONS);
                            prefManager.setCounter(Integer.valueOf(dataSnapshot.child("count_connections").getValue().toString()), DataCounters.CONNECTIONS);


                        }
                    }
                    if (dataSnapshot.child("count_connectRequestSent").exists()) {
                        if (Integer.valueOf(dataSnapshot.child("count_connectRequestSent").getValue().toString()) != prefManager.getCounterSentRequest()) {
                            tosync.put(KrigerConstants.sent_connectionRef.child(user.getUid()), DataCounters.CONNECTREQUESTSENT);
                            prefManager.setCounter(Integer.valueOf(dataSnapshot.child("count_connectRequestSent").getValue().toString()), DataCounters.CONNECTREQUESTSENT);
                        }
                    }

                    int countconnectrequest = 0;
                    if (dataSnapshot.child("count_connectRequest").exists()) {
                        if (Integer.valueOf(dataSnapshot.child("count_connectRequest").getValue().toString()) != prefManager.getCounterConnectRequest()) {
                            isKriger = true;
                            countconnectrequest = Integer.valueOf(dataSnapshot.child("count_connectRequest").getValue().toString()) - prefManager.getCounterConnectRequest();
                            prefManager.setCounter((Integer.valueOf(dataSnapshot.child("count_connectRequest").getValue().toString())), DataCounters.CONNECTREQUEST);
                        }
                    }
                    if (isKriger) {
                        if (countconnectrequest + totalCount > 0) {
                            setBadge(3, countconnectrequest + totalCount);
                        }
                    }


                    if (dataSnapshot.child("count_notifications").exists()) {
                        if (Integer.valueOf(dataSnapshot.child("count_notifications").getValue().toString()) != prefManager.getCounterNotifications()) {
                            int notifications = Integer.valueOf(dataSnapshot.child("count_notifications").getValue().toString()) - prefManager.getCounterNotifications();

                            prefManager.setCounter((Integer.valueOf(dataSnapshot.child("count_notifications").getValue().toString())), DataCounters.NOTIFICATIONS);

                            if (notifications > 0) {
                                setBadge(4, notifications);
                            }
                        }
                    }


                    int countgroupinvites = 0, countgroupposts = 0, countgroups = 0;
                    Boolean isGroupBadge = false;
                    if (dataSnapshot.child("count_group_invites").exists()) {
                        if (Integer.valueOf(dataSnapshot.child("count_group_invites").getValue().toString()) != prefManager.getCounterGroupinvites()) {

                            countgroupinvites = Integer.valueOf(dataSnapshot.child("count_group_invites").getValue().toString()) - prefManager.getCounterGroupinvites();
                            isGroupBadge = true;
                            prefManager.setCounter((Integer.valueOf(dataSnapshot.child("count_group_invites").getValue().toString())), DataCounters.GROUPINVITES);
                        }
                    }
                    if (dataSnapshot.child("count_group_posts").exists()) {
                        if (Integer.valueOf(dataSnapshot.child("count_group_posts").getValue().toString()) != prefManager.getCounterGroupposts()) {

                            countgroupposts = Integer.valueOf(dataSnapshot.child("count_group_posts").getValue().toString()) - prefManager.getCounterGroupposts();
                            isGroupBadge = true;
                            prefManager.setCounter((Integer.valueOf(dataSnapshot.child("count_group_posts").getValue().toString())), DataCounters.GROUPPOSTS);
                        }
                    }
                    if (dataSnapshot.child("count_groups").exists()) {
                        if (Integer.valueOf(dataSnapshot.child("count_groups").getValue().toString()) != prefManager.getCounterGroups()) {

                            countgroups = Integer.valueOf(dataSnapshot.child("count_groups").getValue().toString()) - prefManager.getCounterGroups();
                            isGroupBadge = true;
                            prefManager.setCounter((Integer.valueOf(dataSnapshot.child("count_groups").getValue().toString())), DataCounters.GROUPS);
                        }
                    }
                    if (isGroupBadge) {
                        int count = countgroupinvites + countgroups + countgroupposts;
                        if (count > 0) {
                            setBadge(2, countgroupinvites + countgroups + countgroupposts);
                        }
                    }


                    k = 0;
                    callData();
                } else {

                    callHomeFragment();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void callData() {
        count = 0;

        if (tosync.size() > 0) {

            for (final Map.Entry<DatabaseReference, String> e : tosync.entrySet()) {

                if (k == count) {


                    final ArrayList<String> uidlist = new ArrayList<>();
                    e.getKey().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot childsnap : dataSnapshot.getChildren()) {

                                uidlist.add(childsnap.getKey());

                            }

                            db.insertAllCounters(uidlist, e.getValue());
                            prefManager.setCounter(uidlist.size(), e.getValue());

                            incrementk();


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                count++;
            }
        } else {

            callHomeFragment();
        }

    }

    private void incrementk() {

        if (k < tosync.size() - 1) {
            k++;
            callData();
        } else {
            callHomeFragment();
        }
    }


    private void checkrefreshweek() {


        try {
            if (prefManager.getRefreshWeek() == null || !(prefManager.getRefreshWeek().equals(FireService.getMonday()))) {
                FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("Badge").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        ArrayList<String> connector = new ArrayList<>();
                        for (DataSnapshot childsnap : dataSnapshot.child("Connector").getChildren()) {
                            connector.add(childsnap.getValue().toString());
                        }

                        db.insertAllCounters(connector, DataCounters.CONNECTOR);

                        ArrayList<String> influencer = new ArrayList<>();
                        for (DataSnapshot childsnap : dataSnapshot.child("Influencer").getChildren()) {
                            influencer.add(childsnap.getValue().toString());
                        }
                        db.insertAllCounters(influencer, DataCounters.INFLUENCER);

                        prefManager.setRefreshWeek(FireService.getMonday());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

        } catch (NullPointerException e) {

        }


    }

    private void setBadge(int id, int count) {

        try {
            final BottomBarTab tab = bottomBar.getTabAtPosition(id);
            tab.setBadgeCount(1);

            final Field badgeFieldDefinition;

            badgeFieldDefinition = tab.getClass().getDeclaredField("badge");
            badgeFieldDefinition.setAccessible(true);


            final TextView badgeTextView = (TextView) badgeFieldDefinition.get(tab);

            badgeTextView.setText(String.valueOf(count));
            badgeTextView.setTextSize(10);

        } catch (NoSuchFieldException | NullPointerException | IllegalAccessException ignored) {
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showDialog();
            } else {

                String permission = permissions[0];

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    boolean showRationale = shouldShowRequestPermissionRationale(permission);
                    if (!showRationale) {

                        dialogBuilder("You have denied the contact permission. Please enable it from the 'Settings' of your phone.", 10, false, "Go to settings", null, null);

                    } else {

                        Intent intent = new Intent(getApplicationContext(), ContactDenyActivity.class);
                        startActivity(intent);
                    }


                }
            }
        }


        if (requestCode == PERMISSION_ALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getpermission();
            } else {

                String permission = permissions[0];

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    boolean showRationale = shouldShowRequestPermissionRationale(permission);
                    if (!showRationale) {

                        dialogBuilder("You have denied the media permission. Please enable it from the 'Settings' of your phone.", 13, false, "Go to settings", null, null);


                    } else {

                        dialogBuilder("Oops!No media access?\n" +
                                "Please allow access to use our app better! ", 12, false, "OK", null, null);

                    }


                }


            }


        }

    }

    private void showDialog() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);

            } else {
                getContacts();
            }
        } else {
            getContacts();
        }


    }

 //contacts fetch

    private void getContacts() {

        progress.show();

        ContactList contactList = new ContactList();

        contactList.checksync();

        ContactList.LoadContactsAyscn loadContactsAyscn = contactList.new LoadContactsAyscn(HomeActivity.this);
        loadContactsAyscn.execute();


    }


    @Override
    public void onTaskComplete(ArrayList<Contacts> contacts) {

        HashMap<String, Object> map = new HashMap<>();


        map.put("index", "firebase6");
        map.put("type", "user_intro");


        int j = 0;

        HashMap<String, Object> temp = new HashMap<>();
        for (int i = 0; i < contacts.size(); i++) {

            if (!contacts.get(i).getEmail()) {

                temp.put(String.valueOf(j), contacts.get(i).getEph());
                j++;


            }
        }

        final String key = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").push().getKey();
        try {

            HashMap<String, Object> map1 = new HashMap<>();

            map1.put("contact", temp);
            HashMap<String, Object> map2 = new HashMap<>();
            map2.put("terms", map1);

            HashMap<String, Object> map3 = new HashMap<>();
            map3.put("query", map2);

            map.put("body", map3);


            FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("search").child("request").child(key).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    listensearch(key);
                }
            });
        } catch (StringIndexOutOfBoundsException e) {
            listensearch(key);
        }

    }


    private void listensearch(String key) {


        FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("search").child("response").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progress.dismiss();
                ArrayList<String> idlist = new ArrayList<>();
                for (DataSnapshot childsnap : dataSnapshot.child("hits").child("hits").getChildren()) {
                    resultList.add(childsnap.child("_id").getValue().toString());
                    if (childsnap.child("_source").exists()) {
                        idlist.add(childsnap.child("_source").child("contact").getValue().toString());
                    }

                }

                db.insertAllCounters(idlist, DataCounters.CONTACT);

                try {
                    showCount(dataSnapshot.child("hits").child("total").getValue().toString());
                } catch (NullPointerException e) {
                    showCount("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void showCount(String count) {

        if (count.equals("0")) {
            dialogBuilder(count + " of your contacts are on Kriger Campus.", 0, false, "OK", null, null);

        } else {
            dialogBuilder(count + " of your contacts are on Kriger Campus.\n" +
                    "We are adding them to your network.", 0, false, "OK", null, null);

        }
    }


    //Connecting with contacts
    private void startContactConnection() {

        Map<String, Object> childUpdates = new HashMap<>();

        for (int i = 0; i < resultList.size(); i++) {
            childUpdates.put("/" + resultList.get(i) + "/" + user.getUid() + "/timestamp", FireService.getToday());
            childUpdates.put("/" + resultList.get(i) + "/" + user.getUid() + "/own", "false");
        }
        KrigerConstants.connectionRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                updateMyContacts();
            }
        });


    }


    private void updateMyContacts() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("timestamp", FireService.getToday());
        map.put("own", "true");

        HashMap<String, Object> map1 = new HashMap<>();

        for (int i = 0; i < resultList.size(); i++) {
            if (!resultList.get(i).equals(user.getUid())) {
                map1.put(resultList.get(i), map);
            }

        }

       KrigerConstants.connectionRef.child(user.getUid()).updateChildren(map1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progress.dismiss();
                prefManager.setCountVisits(2);
                database.child("User_Extra_Detail").child(user.getUid()).child("count_visits").setValue(2);

            }
        });
    }

    private void callHomeFragment() {
        try {
            Home home = (Home) getSupportFragmentManager().findFragmentById(R.id.flContent);
            home.startDatabase();
        } catch (ClassCastException e) {
        } catch (NullPointerException e) {
        }

    }


    private void dialogBuilder(String text, final int type, Boolean isNegative, String posText, String negText, ArrayList<UserDetails> arrayList) {

        final Dialog dialog = new Dialog(HomeActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.layout_dialog);

        TextView textView =  dialog.findViewById(R.id.dialogue_text);
        textView.setText(text);

        if (!(type == 2)) {
            dialog.setCancelable(false);
        }
        if (type == 16) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        }

        Button posButton =  dialog.findViewById(R.id.button_positive);
        posButton.setText(posText);
        posButton.setVisibility(View.VISIBLE);
        posButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (type == 0) {
                    k = 0;
                    startContactConnection();
                    dialog.dismiss();
                    progress.show();
                } else if (type == 1) {
                    dialog.dismiss();
                    showDialog();
                } else if (type == 2) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                } else if (type == 3) {
                    dialog.dismiss();
                } else if (type == 4) {
                    dialog.dismiss();
                } else if (type == 6) {
                    dialog.dismiss();
                    //showChoices();

                } else if (type == 7) {
                    database.child("User_Extra_Detail").child(user.getUid()).child("count_visits").setValue(3);
                    dialog.dismiss();
                    bottomBar.selectTabAtPosition(4, true);


                } else if (type == 8) {
                    dialog.dismiss();



                } else if (type == 9) {
                    final ProgressDialog progressDialog = new ProgressDialog(HomeActivity.this);
                    progressDialog.setMessage("Please wait");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    database.child("Post").orderByChild("uid").equalTo("DAqhLs3rLqh4hTdppkTqJYpmPMJ3").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot childsnap : dataSnapshot.getChildren()) {
                                progressDialog.dismiss();
                                Intent intent = new Intent(getApplicationContext(), AnswerListActivity.class);
                                intent.putExtra("post_id", childsnap.getKey());
                                intent.putExtra("type", "profile_visits");
                                startActivity(intent);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    dialog.dismiss();
                } else if (type == 10) {
                    // navigate to settings
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, OPEN_SETTINGS);

                } else if (type == 12) {
                    dialog.dismiss();
                    getpermission();

                } else if (type == 13) {
                    // navigate to settings
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    dialog.dismiss();
                } else if (type == 14) {
                    dialog.dismiss();

                } else if (type == 15) {
                    dialog.dismiss();
                    Intent intent = new Intent(getApplicationContext(), ContactListActivity.class);
                    startActivity(intent);
                } else if (type == 16) {

                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }

                    int playStoreVersionCode = Integer.valueOf(FirebaseRemoteConfig.getInstance().getString(
                            "android_latest_version_code"));

                    try {

                        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        int currentAppVersionCode = pInfo.versionCode;

                        if (playStoreVersionCode == currentAppVersionCode) {
                            dialog.dismiss();
                        }
                    } catch (PackageManager.NameNotFoundException e) {

                    }


                }


            }
        });

        if (isNegative) {

            Button negButton =  dialog.findViewById(R.id.button_negative);
            negButton.setText(negText);
            negButton.setVisibility(View.VISIBLE);
            negButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type == 4) {
                        KrigerConstants.userRef.child(user.getUid()).child("profile_token").removeValue();
                        FirebaseAuth.getInstance().signOut();
                        prefManager.clearPreferences();
                        db.deleteAllTables();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    } else if (type == 3) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    dialog.dismiss();

                    if (type == 8) {
                        dialogBuilder("Are you sure, You don't want to be an inspiration?", 14, true, "Share", "Never mind", null);
                    }

                }
            });
        }

        if (type == 5) {
            ListView listView =  dialog.findViewById(R.id.listview_connect);
            listView.setVisibility(View.VISIBLE);


            Adapter_List adapter = new Adapter_List(this, R.layout.kriger_list, arrayList);

            listView.setAdapter(adapter);

        }

        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {

        }

    }

    //select Subjects and exams in first visit



    @RequiresApi(api = Build.VERSION_CODES.M)
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
                    compressedUri = Uri.fromFile(compressedFile);
                    squarefile = new Compressor(this).setMaxHeight(50).setMaxWidth(50).setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES).getAbsolutePath()).compressToFile(file);


                    Bitmap compressedBitmap = BitmapFactory.decodeFile(compressedFile.getAbsolutePath());
                    profile_pic.setImageBitmap(compressedBitmap);
                } catch (IOException exp) {

                } catch (NullPointerException e) {

                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

                Toasty.custom(getApplicationContext(), error.getMessage(), R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                        true).show();
            }
        } else if (requestCode == OPEN_SETTINGS) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                } else {
                    getContacts();
                }
            }

        }


    }

    public void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        birthday.setText(sdf.format(myCalendar.getTime()));

    }

    private void havingTrouble(){
        final Dialog trouble = new Dialog(HomeActivity.this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        // Set GUI of login screen
        trouble.setContentView(R.layout.dialog_havingtrouble);

        // Init button of login GUI
        Button email =  trouble.findViewById(R.id.btn_trouble_email);
        Button whatsapp =  trouble.findViewById(R.id.btn_trouble_whatsapp);
        Button verification =  trouble.findViewById(R.id.btn_trouble_verification);
        verification.setVisibility(View.GONE);

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
                    Toasty.custom(getApplicationContext(),"There are no email clients installed", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                }

            }
        });

        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PackageManager pm = getPackageManager();
                boolean app_installed;
                try {

                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                    try {

                        Intent intentWhatsapp = new Intent(Intent.ACTION_VIEW);
                        //Linked to tech issue group
                        String url = "https://chat.whatsapp.com/https://chat.whatsapp.com/EZCcHHMWQVE32STn3OTSlJ";
                        intentWhatsapp.setData(Uri.parse(url));
                        intentWhatsapp.setPackage("com.whatsapp");
                        startActivity(intentWhatsapp);

                    } catch (Exception e) {
                        Toasty.custom(getApplicationContext(),e.getMessage(), R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                true).show();
                        e.printStackTrace();
                    }

                } catch (PackageManager.NameNotFoundException e) {
                    Toasty.custom(getApplicationContext(),"Whatsapp is not installed on this device", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                }

            }
        });

        trouble.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        trouble.show();

    }


    private void updateProfile(ArrayList<Integer> selected_exam_choices, ArrayList<Integer> selected_subject_choices) {
        final Dialog progress = new Dialog(HomeActivity.this);
        progress.setContentView(R.layout.dialog_progress);
        progress.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        progress.setCancelable(false);
        progress.show();

        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
        final String format = s.format(new Date());

        final HashMap<String, Object> map = new HashMap<>();

        map.put("/User/" + user.getUid() + "/country", cityStateCountry.getCountryCode(country.getSelectedItemPosition()));
        map.put("/User/" + user.getUid() + "/hometown",hometown.getText().toString());
        map.put("/User/" + user.getUid() + "/state", cityStateCountry.getStateCode(state.getSelectedItemPosition()));
        map.put("/User/" + user.getUid() + "/birthday", birthday.getText().toString());
        map.put("User/" + user.getUid() + "/latestedu", latestedu.getSelectedItem().toString());



        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

        final StorageReference squareRef = mStorageRef.child("Your Photo").child("new_small" + user.getUid());
        final StorageReference childref = mStorageRef.child("Your Photo").child("new_" + user.getUid());

        UploadTask uploadTask1 = squareRef.putFile(Uri.fromFile(squarefile));

        final UploadTask uploadTask2 = childref.putFile(compressedUri);

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

                    Task<Uri> urlTask = uploadTask2.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                                Uri downloadUri = task.getResult();
                                map.put("User_Detail/" + user.getUid() + "/thumb", squareUri.toString());
                                map.put("User_Detail/" + user.getUid() +  "/original", downloadUri.toString());
                                map.put("User_Detail/" + user.getUid() + "/headline", bio.getText().toString().trim());
                                map.put("/User_Extra_Detail/"+ user.getUid() + "/count_visits/",1);
                                map.put("/Visit_Notification/" + user.getUid() + "/timestamp",FireService.getToday());
                                map.put("/Visit_Notification/" + user.getUid() + "/user_type",prefManager.getAccountType());
                                map.put("/Visit_Notification/" + user.getUid() + "/count_visits",1);
                                map.put("/User/" + user.getUid() + "/exam",selected_exam_choices);
                                map.put("/User/" + user.getUid() + "/subject",selected_subject_choices);


                                FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        KrigerConstants.userRef.child(user.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progress.dismiss();
                                                prefManager.setCountVisits(1);
                                                prefManager.clearProfileImageUrl();
                                                Toasty.custom(getApplicationContext(),"Updated", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                                        true).show();
                                                startFirstVisit();
                                            }
                                        });
                                    }
                                });

                            } else {
                                progress.dismiss();
                                Toasty.custom(getApplicationContext(),"Upload failed", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                        true).show();

                                // Handle failures
                                // ...
                            }
                        }
                    });


                } else {
                    progress.dismiss();
                    Toasty.custom(getApplicationContext(),"Upload failed", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();

                    // Handle failures
                    // ...
                }
            }
        });

    }

    private void startFirstVisit() {


        dialogBuilder("Your preferences have been filled. Let's take you to your personal timeline.", 6, false, "OK", null, null);


    }

    public void getpermission() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if ((checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {


                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_ALL);
            } else {
                is_photo = true;
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(150, 150)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(HomeActivity.this);

            }
        } else {
            is_photo = true;
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(150, 150)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(HomeActivity.this);

        }

    }


    public static boolean isPackageInstalled(Context c, String targetPackage) {
        PackageManager pm = c.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    //class for disable emoticons on keyboard
    private class EmojiExcludeFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                int type = Character.getType(source.charAt(i));
                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                    return "";
                }
            }
            return null;
        }
    }


    @Override
    public void onResume() {

        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 1000);

        DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
        offsetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double offset = snapshot.getValue(Double.class);
                connection = true;
                customHandler.removeCallbacks(updateTimerThread);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Listener was cancelled");
            }
        });

        if (prefManager.getCountVisits() == 0  && !is_photo && prefManager.getTransKrigers()){
            initiateFirstVisit();
        }


        super.onResume();

    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            if (timeInMilliseconds > 4000) {
                if (!connection) {
                    Toasty.custom(getApplicationContext(),"Check your internet connection", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                    customHandler.removeCallbacks(this);
                    connection = true;
                }
            }

            customHandler.postDelayed(this, 1000);

        }

    };


    public BottomBar getBottomBar() {
        return this.bottomBar;
    }

    @Override
    protected void onPause() {

        super.onPause();
    }
}

