package in.kriger.newkrigercampus.profilefragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nex3z.flowlayout.FlowLayout;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.whiteelephant.monthpicker.MonthPickerDialog;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import es.dmoral.toasty.Toasty;
import id.zelory.compressor.Compressor;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.activities.HomeActivity;
import in.kriger.newkrigercampus.bottomfragments.Home;
import in.kriger.newkrigercampus.classes.DataCounters;
import in.kriger.newkrigercampus.classes.User;
import in.kriger.newkrigercampus.database.DatabaseHelper;
import in.kriger.newkrigercampus.extras.CityStateCountry;
import in.kriger.newkrigercampus.extras.CustomAutoCompleteTextView;
import in.kriger.newkrigercampus.extras.CustomTextViewBold;
import in.kriger.newkrigercampus.extras.PrefManager;
import in.kriger.newkrigercampus.services.KrigerConstants;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class CorporateAbout extends Fragment implements View.OnClickListener {

    private ImageView profile_image;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();



    DatabaseHelper db;

    Bundle bundle;


    private Uri uri;

    String url;

    private Button btn_tag;

    private TextView profile_name, bio;

    private TextView birthday, contact, residence, summary,  address,  presence1, presence2, presence3, presence4, presence5,presence6;
    private View line_above_summary;
    TextView text_progessbar;
    Boolean edited_birthday = false;


    private String userid;
    private Boolean show_connect = false;

    private Button button_connect;
    private LinearLayout btn_connect;



    private LinearLayout  linear_prepration_exam, linear_prepration_sub, linear_teaching, linear_clg, linear_cochng, linear_awds, linear_cert, linear_intern, linear_publication, linear_specialisation, lnr_address, lnr_presence, display_awards, display_certifications, display_internships, display_prepration_exam, display_prepration_exam_header, display_prepration_sub, display_prepration_sub_header, display_teaching, display_teaching_header, display_college, display_college_header, display_coaching, display_publication, display_specialisation;

    ProgressBar profile_progress;
    Button link;

    LinearLayout layout_toberefer, layout_name;

    private static final int REQUEST_INVITE = 0;

    LinearLayout layout_screen;

    Button card;

    TextView refer_score, connections_ans, posts_ans, groups_ans, visitors_ans;

    LinearLayout layout_refer, visitors_layout, profile_visitors, address_layout;

    PrefManager prefManager;


    ProgressDialog progressDialog;

    Boolean isFacebookShare = false;



    private TextView textView_dash;

    ShareDialog shareDialog;

    Button btn_edit_prepration_exam, btn_edit_prepration_sub, btn_edit_teaching, btn_edit_college, btn_edit_awards, btn_edit_coaching, btn_edit_certificate, btn_edit_internship, btn_edit_publication, btn_edit_specialisation, btn_edit_intro, btn_edit_summary, btn_edit_address, btn_edit_presence;

    private LinearLayout add_save_prepration_exam, add_save_prepration_sub, add_save_teaching, add_save_college, add_save_awards, add_save_coaching, add_save_certifications, add_save_internships, add_save_publication, add_save_specialisation;

    private Button  btn_save_prepration_exam,  btn_save_prepration_sub, btn_add_teaching, btn_save_teaching, btn_add_college, btn_save_college, btn_add_awards, btn_save_awards, btn_add_coaching, btn_save_coaching, btn_add_certification, btn_save_certification, btn_add_internships, btn_save_internships, btn_add_publication, btn_save_publication, btn_add_specialisation, btn_save_specialisation, btn_cancel_intro, btn_save_intro, btn_save_summary, btn_cancel_summary, btn_save_address, btn_cancel_address, btn_save_presence, btn_cancel_presence;

    private String[] teaching_hint = {"Institute (ex: ALLEN/AKASH)", "Description", "Year (From)", "Year (To)"};
    private String[] college_hint = {"School/College/Institute (ex: DPS/IIT/NIFT)", "Degree/Class (ex: 10th/Higher secondary/B.Tech)", "Year (From)", "Year (To)"};
    private String[] coaching_hint = {"Institute (ex: FITJEE/T.I.M.E.)", "Competitive Exam (ex: IIT-JEE/NEET)", "Year (From)", "Year (To)"};
    private String[] awards_hint = {"Name (ex: NTSE/KVPY)", "Description", "Year ", "Year (To)"};
    private String[] certification_hint = {"Name (ex: PMP/NCC ‘A’)", "Description", "Year ", "Year (To)"};
    private String[] internships_hint = {"Organisation (ex: Google/IISC Bangalore)", "Description", "Year (From)", "Year (To)"};
    private String[] publication_hint = {"Organisation", "Desciption", "Year ", "Year (To)"};
    private String[] specialisation_hint = {"Organisation", "Description", "Year (From)", "Year (To)"};


    private LinearLayout layout_current = null, layout_current_text = null, linearLayout_bio, linearLayout_editbio, linearlayout_summary, linearlayout_address, linearlayout_presence, linearlayout_presence_textview;
    Button btn_current_add = null, btn_current_save = null;

    EditText editText_bio,  editText_contact, editText_birthday, edittext_summary, edittext_house, edittext_street, edittext_landmark, edittext_pincode,  edittext_presence1, edittext_presence2, edittext_presence3, edittext_presence4, edittext_presence5,edittext_presence6, editText_teaching_since,editText_teaching_at;
    EditText editText_address_city;
    EditText editText_city;
    private AutoCompleteTextView editText_latest_edu, editText_exam, editText_subject;
    private Spinner editText_country, editText_state, editText_address_country, editText_address_state;

    User currentuser;
    ImageView profile_image_camera;

    final Calendar myCalendar = Calendar.getInstance();

    String[] exams;
    String[] subjects;
    LinearLayout llayout;

    ArrayList<String> examlist = new ArrayList<>();
    ArrayList<String> sublist = new ArrayList<>();

    final ArrayList<Integer> selected_choices_exam = new ArrayList<>();

    final ArrayList<Integer> selected_choices_sub = new ArrayList<>();
    Context context;
    Uri compressedUri;
    File squareFile;

    ArrayAdapter<String> listAdapter;
    List<Integer> valueList = new ArrayList<>();
    String key1;


    CityStateCountry cityStateCountry;

    TextView count_educators,count_learners,count_alumini;

    public CorporateAbout(Context context) {
        this.context = context;

        // Required empty public constructor
    }


    public CorporateAbout() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_corporate_about, container, false);





        layout_refer = rootView.findViewById(R.id.layout_refer);
        visitors_layout =  rootView.findViewById(R.id.visitors_layout);

        address_layout = rootView.findViewById(R.id.address_layout);


        try {
            Log.d("User", getArguments().get("user_id").toString());
            userid = getArguments().get("user_id").toString();
            if (!userid.equals(user.getUid())) {
                show_connect = true;
            }
        } catch (NullPointerException e) {
            userid = user.getUid();
            show_connect = false;
        }

        try {
            Log.d("Hello", getArguments().getString("is_profileShare"));
            profileshare();
        } catch (NullPointerException e) {

        }


        shareDialog = new ShareDialog(getActivity());

        db = new DatabaseHelper(getActivity());

        btn_tag =rootView.findViewById(R.id.btn_tag);
        profile_name =  rootView.findViewById(R.id.profile_name);
        bio =  rootView.findViewById(R.id.bio);

        prefManager = new PrefManager(context);
        refer_score =  rootView.findViewById(R.id.refer_score);
        connections_ans =  rootView.findViewById(R.id.connections_ans);
        posts_ans =  rootView.findViewById(R.id.posts_ans);
        groups_ans =  rootView.findViewById(R.id.groups_ans);
        visitors_ans =  rootView.findViewById(R.id.visitors_ans);

        layout_toberefer =  rootView.findViewById(R.id.layout_toberefer);
        profile_visitors =  rootView.findViewById(R.id.profile_visitors);
        layout_name =  rootView.findViewById(R.id.layout_name);


        btn_connect =  rootView.findViewById(R.id.btn_connect);

        layout_screen =  rootView.findViewById(R.id.layout_screen);

        exams = getResources().getStringArray(R.array.list_exams);
        subjects = getResources().getStringArray(R.array.list_sub);


        add_save_prepration_exam = rootView.findViewById(R.id.layout_save_add_prepration_exam);

        count_alumini = rootView.findViewById(R.id.alumini_ans);
        count_educators = rootView.findViewById(R.id.educators_ans);
        count_learners = rootView.findViewById(R.id.learners_ans);



        btn_save_prepration_exam =  rootView.findViewById(R.id.btn_save_prepration_exam);
        btn_save_prepration_exam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                layout_current = null;
                layout_current_text = null;
                btn_current_save = null;
                btn_current_add = null;
                save_flow(display_prepration_exam_header);
            }
        });

        add_save_prepration_sub =  rootView.findViewById(R.id.layout_save_add_prepration_sub);


        btn_save_prepration_sub =  rootView.findViewById(R.id.btn_save_prepration_sub);
        btn_save_prepration_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                layout_current = null;
                layout_current_text = null;
                btn_current_save = null;
                btn_current_add = null;
                save_flow(display_prepration_sub_header);
            }
        });


        add_save_teaching =  rootView.findViewById(R.id.layout_save_add_teaching);
        btn_add_teaching =  rootView.findViewById(R.id.btn_add_teaching);
        btn_add_teaching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLayout(display_teaching);
            }
        });

        btn_save_teaching =  rootView.findViewById(R.id.btn_save_teaching);
        btn_save_teaching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                layout_current.setVisibility(View.GONE);
                layout_current = null;
                layout_current_text = null;
                btn_current_save = null;
                btn_current_add = null;
                if (btn_save_teaching.getText().toString().equals("Save")) {
                    savealldetails(display_teaching);
                } else {
                    cancelAll(display_teaching);
                }
            }
        });


        add_save_college =  rootView.findViewById(R.id.layout_save_add_college);
        btn_add_college =  rootView.findViewById(R.id.btn_add_college);
        btn_add_college.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLayout(display_college);
            }
        });

        btn_save_college =  rootView.findViewById(R.id.btn_save_college);
        btn_save_college.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                layout_current.setVisibility(View.GONE);
                layout_current = null;
                layout_current_text = null;
                btn_current_save = null;
                btn_current_add = null;
                if (btn_save_college.getText().toString().equals("Save")) {
                    savealldetails(display_college);
                } else {
                    cancelAll(display_college);
                }
            }
        });

        add_save_awards = rootView.findViewById(R.id.layout_save_add_awards);
        btn_add_awards =  rootView.findViewById(R.id.btn_add_awards);
        btn_add_awards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLayout(display_awards);

            }
        });

        btn_save_awards =  rootView.findViewById(R.id.btn_save_awards);
        btn_save_awards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                layout_current.setVisibility(View.GONE);
                layout_current = null;
                layout_current_text = null;
                btn_current_save = null;
                btn_current_add = null;
                if (btn_save_awards.getText().toString().equals("Save")) {
                    savealldetails(display_awards);
                } else {
                    cancelAll(display_awards);
                }

            }
        });

        add_save_certifications =  rootView.findViewById(R.id.layout_save_add_certification);
        btn_add_certification = rootView.findViewById(R.id.btn_add_certification);
        btn_add_certification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLayout(display_certifications);

            }
        });

        btn_save_certification =  rootView.findViewById(R.id.btn_save_certification);
        btn_save_certification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                layout_current.setVisibility(View.GONE);
                layout_current = null;
                layout_current_text = null;
                btn_current_save = null;
                btn_current_add = null;
                if (btn_save_certification.getText().toString().equals("Save")) {
                    savealldetails(display_certifications);
                } else {
                    cancelAll(display_certifications);
                }

            }
        });

        add_save_coaching =  rootView.findViewById(R.id.layout_save_add_coaching);
        btn_add_coaching =  rootView.findViewById(R.id.btn_add_coaching);
        btn_add_coaching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLayout(display_coaching);

            }
        });

        btn_save_coaching =  rootView.findViewById(R.id.btn_save_coaching);
        btn_save_coaching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                layout_current.setVisibility(View.GONE);
                layout_current = null;
                layout_current_text = null;
                btn_current_save = null;
                btn_current_add = null;
                if (btn_save_coaching.getText().toString().equals("Save")) {
                    savealldetails(display_coaching);
                } else {
                    cancelAll(display_coaching);
                }

            }
        });

        add_save_internships =  rootView.findViewById(R.id.layout_save_add_internships);
        btn_add_internships =  rootView.findViewById(R.id.btn_add_internships);
        btn_add_internships.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLayout(display_internships);

            }
        });

        btn_save_internships =  rootView.findViewById(R.id.btn_save_internships);
        btn_save_internships.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                layout_current.setVisibility(View.GONE);
                layout_current = null;
                layout_current_text = null;
                btn_current_save = null;
                btn_current_add = null;
                if (btn_save_internships.getText().toString().equals("Save")) {
                    savealldetails(display_internships);
                } else {
                    cancelAll(display_internships);
                }

            }
        });

        add_save_publication =  rootView.findViewById(R.id.layout_save_add_publication);
        btn_add_publication = rootView.findViewById(R.id.btn_add_publication);
        btn_add_publication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLayout(display_publication);

            }
        });

        btn_save_publication =  rootView.findViewById(R.id.btn_save_publication);
        btn_save_publication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                layout_current.setVisibility(View.GONE);
                layout_current = null;
                layout_current_text = null;
                btn_current_save = null;
                btn_current_add = null;
                if (btn_save_publication.getText().toString().equals("Save")) {
                    savealldetails(display_publication);
                } else {
                    cancelAll(display_publication);
                }

            }
        });

        add_save_specialisation =  rootView.findViewById(R.id.layout_save_add_specialisation);
        btn_add_specialisation =  rootView.findViewById(R.id.btn_add_specialisation);
        btn_add_specialisation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLayout(display_specialisation);

            }
        });

        btn_save_specialisation =  rootView.findViewById(R.id.btn_save_specialisation);
        btn_save_specialisation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                layout_current.setVisibility(View.GONE);
                layout_current = null;
                layout_current_text = null;
                btn_current_save = null;
                btn_current_add = null;

                if (btn_save_specialisation.getText().toString().equals("Save")) {
                    savealldetails(display_specialisation);
                } else {
                    cancelAll(display_specialisation);
                }

            }
        });


        btn_edit_prepration_exam =  rootView.findViewById(R.id.btn_edit_prepration_exam);
        btn_edit_prepration_exam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout_current == null) {
                    linear_prepration_exam.setVisibility(View.GONE);
                    layout_current_text = display_prepration_exam;
                    layout_current = add_save_prepration_exam;
                    edit_flow(display_prepration_exam_header);
                } else {
                    Toasty.custom(context, "Please Save/Cancel before proceeding.", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_SHORT, false,
                            true).show();

                }
            }
        });


        btn_edit_prepration_sub = rootView.findViewById(R.id.btn_edit_prepration_sub);
        btn_edit_prepration_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout_current == null) {
                    linear_prepration_sub.setVisibility(View.GONE);
                    layout_current_text = display_prepration_sub;
                    layout_current = add_save_prepration_sub;
                    edit_flow(display_prepration_sub_header);
                } else {
                    Toasty.custom(context, "Please Save/Cancel before proceeding.", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_SHORT, false,
                            true).show();

                }
            }
        });


        btn_edit_teaching = rootView.findViewById(R.id.btn_edit_teaching);
        btn_edit_teaching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout_current == null) {
                    linear_teaching.setVisibility(View.GONE);
                    layout_current_text = display_teaching;
                    layout_current = add_save_teaching;
                    btn_current_add = btn_add_teaching;
                    btn_current_save = btn_save_teaching;
                    edit_layout(display_teaching);
                } else {
                    Toasty.custom(getContext(), "Please save/cancel before proceeding", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                }
            }
        });


        btn_edit_college = rootView.findViewById(R.id.btn_edit_school);
        btn_edit_college.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout_current == null) {
                    linear_clg.setVisibility(View.GONE);
                    layout_current_text = display_college;
                    layout_current = add_save_college;
                    btn_current_add = btn_add_college;
                    btn_current_save = btn_save_college;
                    edit_layout(display_college);
                } else {
                    Toasty.custom(getContext(), "Please save/cancel before proceeding", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                }
            }
        });

        btn_edit_awards =  rootView.findViewById(R.id.btn_edit_awards);
        btn_edit_awards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout_current == null) {

                    linear_awds.setVisibility(View.GONE);
                    layout_current_text = display_awards;
                    layout_current = add_save_awards;
                    btn_current_add = btn_add_awards;
                    btn_current_save = btn_save_awards;
                    edit_layout(display_awards);
                } else {
                    Toasty.custom(getContext(), "Please save/cancel before proceeding", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                }

            }
        });

        btn_edit_coaching =  rootView.findViewById(R.id.btn_edit_coaching);
        btn_edit_coaching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout_current == null) {
                    linear_cochng.setVisibility(View.GONE);
                    layout_current_text = display_coaching;
                    layout_current = add_save_coaching;
                    btn_current_add = btn_add_coaching;
                    btn_current_save = btn_save_coaching;
                    edit_layout(display_coaching);
                } else {
                    Toasty.custom(getContext(), "Please save/cancel before proceeding", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                }
            }
        });

        btn_edit_certificate = rootView.findViewById(R.id.btn_edit_certifications);
        btn_edit_certificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout_current == null) {
                    linear_cert.setVisibility(View.GONE);
                    layout_current_text = display_certifications;
                    layout_current = add_save_certifications;
                    btn_current_add = btn_add_certification;
                    btn_current_save = btn_save_certification;
                    edit_layout(display_certifications);
                } else {
                    Toasty.custom(getContext(), "Please save/cancel before proceeding", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                }
            }
        });

        btn_edit_internship =  rootView.findViewById(R.id.btn_edit_internships);
        btn_edit_internship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout_current == null) {
                    linear_intern.setVisibility(View.GONE);
                    layout_current_text = display_internships;
                    layout_current = add_save_internships;
                    btn_current_add = btn_add_internships;
                    btn_current_save = btn_save_internships;
                    edit_layout(display_internships);
                } else {
                    Toasty.custom(getContext(), "Please save/cancel before proceeding", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                }
            }
        });

        btn_edit_publication = rootView.findViewById(R.id.btn_edit_publication);
        btn_edit_publication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout_current == null) {
                    linear_publication.setVisibility(View.GONE);
                    layout_current_text = display_publication;
                    layout_current = add_save_publication;
                    btn_current_add = btn_add_publication;
                    btn_current_save = btn_save_publication;
                    edit_layout(display_publication);
                } else {
                    Toasty.custom(getContext(), "Please save/cancel before proceeding", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                }
            }
        });

        btn_edit_specialisation = rootView.findViewById(R.id.btn_edit_specialisation);
        btn_edit_specialisation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout_current == null) {
                    linear_specialisation.setVisibility(View.GONE);
                    layout_current_text = display_specialisation;
                    layout_current = add_save_specialisation;
                    btn_current_add = btn_add_specialisation;
                    btn_current_save = btn_save_specialisation;
                    edit_layout(display_specialisation);
                } else {
                    Toasty.custom(getContext(), "Please save/cancel before proceeding", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                }
            }
        });


        btn_edit_intro =  rootView.findViewById(R.id.btn_edit_intro);
        btn_edit_summary =  rootView.findViewById(R.id.btn_edit_summary);
        btn_edit_address =  rootView.findViewById(R.id.btn_edit_address);
        btn_edit_presence =  rootView.findViewById(R.id.btn_edit_presence);


        profile_image_camera =  rootView.findViewById(R.id.profile_photo_camera);

        try {
            if (!userid.equals(user.getUid())) {
                btn_edit_intro.setVisibility(View.GONE);
                btn_edit_awards.setVisibility(View.GONE);
                btn_edit_certificate.setVisibility(View.GONE);
                btn_edit_prepration_exam.setVisibility(View.GONE);
                btn_edit_prepration_sub.setVisibility(View.GONE);
                btn_edit_coaching.setVisibility(View.GONE);
                btn_edit_teaching.setVisibility(View.GONE);
                btn_edit_college.setVisibility(View.GONE);
                btn_edit_summary.setVisibility(View.GONE);
                btn_edit_address.setVisibility(View.GONE);
                btn_edit_presence.setVisibility(View.GONE);
                btn_edit_internship.setVisibility(View.GONE);
                btn_edit_publication.setVisibility(View.GONE);
                profile_image_camera.setVisibility(View.GONE);
                btn_edit_specialisation.setVisibility(View.GONE);
            }
        } catch (NullPointerException e) {

        }


        btn_tag.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/impact.ttf"));


        if (db.checkDataCounterValue(userid, DataCounters.INFLUENCER)) {
            btn_tag.setVisibility(View.VISIBLE);
            btn_tag.setText("INFLUENCER");
            btn_tag.setBackgroundResource(R.drawable.button_background_red);
            btn_tag.setTextColor(Color.parseColor("#FF0000"));

        } else if (db.checkDataCounterValue(userid, DataCounters.CONNECTOR)) {
            btn_tag.setVisibility(View.VISIBLE);
            btn_tag.setText("CONNECTOR");
            btn_tag.setBackgroundResource(R.drawable.button_background_green);
            btn_tag.setTextColor(Color.parseColor("#a4c639"));

        } else if (db.checkDataCounterValue(userid, DataCounters.EXPERT)) {
            btn_tag.setVisibility(View.VISIBLE);
            btn_tag.setText("EXPERT");
            btn_tag.setBackgroundResource(R.drawable.button_background_blue);
            btn_tag.setTextColor(Color.parseColor("#3994e4"));

        } else {
            btn_tag.setVisibility(View.VISIBLE);
            btn_tag.setText("EDUCATOR");
            btn_tag.setBackgroundResource(R.drawable.button_background_blue);
            btn_tag.setTextColor(Color.parseColor("#3994e4"));
        }


        linearLayout_bio =  rootView.findViewById(R.id.linearlayout_bio);
        linearLayout_editbio = rootView.findViewById(R.id.linearlayout_editintro);
        linearlayout_summary =  rootView.findViewById(R.id.linearlayout_summary);
        linearlayout_address =  rootView.findViewById(R.id.linearlayout_address);

        linearlayout_presence =  rootView.findViewById(R.id.linearlayout_presence);
        linearlayout_presence_textview =  rootView.findViewById(R.id.linearlayout_presence_textview);

        residence =  rootView.findViewById(R.id.residence);
        summary = rootView.findViewById(R.id.summary);
        address =  rootView.findViewById(R.id.address);
        presence1 =  rootView.findViewById(R.id.presence1);
        presence2 = rootView.findViewById(R.id.presence2);
        presence3 = rootView.findViewById(R.id.presence3);
        presence4 =  rootView.findViewById(R.id.presence4);
        presence5 =  rootView.findViewById(R.id.presence5);
        presence6 =  rootView.findViewById(R.id.presence6);
        line_above_summary =  rootView.findViewById(R.id.line_above_summary);
        birthday =  rootView.findViewById(R.id.birthday);
        contact = rootView.findViewById(R.id.contact);
        text_progessbar =  rootView.findViewById(R.id.text_progressbar);

        editText_bio =  rootView.findViewById(R.id.edittext_bio);
        editText_birthday = rootView.findViewById(R.id.edittext_birthday);
        editText_contact =  rootView.findViewById(R.id.edittext_contact);
        editText_latest_edu = (CustomAutoCompleteTextView) rootView.findViewById(R.id.edittext_latest_edu);
        editText_teaching_since =  rootView.findViewById(R.id.edittext_teaching_since);
        editText_teaching_at =  rootView.findViewById(R.id.edittext_teaching_at);
        editText_country =  rootView.findViewById(R.id.edittext_country);
        editText_state =  rootView.findViewById(R.id.edittext_state);
        editText_city =  rootView.findViewById(R.id.edittext_city);
        edittext_summary =  rootView.findViewById(R.id.edittext_summary);
        edittext_house = rootView.findViewById(R.id.edittext_house);
        edittext_street =  rootView.findViewById(R.id.edittext_street);
        edittext_landmark =  rootView.findViewById(R.id.edittext_landmark);
        edittext_pincode = rootView.findViewById(R.id.edittext_pincode);
        editText_address_country =  rootView.findViewById(R.id.edittext_address_country);
        editText_address_state = rootView.findViewById(R.id.edittext_address_state);
        editText_address_city = rootView.findViewById(R.id.edittext_address_city);
        edittext_presence1 =  rootView.findViewById(R.id.edittext_presence1);
        edittext_presence2 =  rootView.findViewById(R.id.edittext_presence2);
        edittext_presence3 = rootView.findViewById(R.id.edittext_presence3);
        edittext_presence4 =  rootView.findViewById(R.id.edittext_presence4);
        edittext_presence5 = rootView.findViewById(R.id.edittext_presence5);
        edittext_presence6 = rootView.findViewById(R.id.edittext_presence6);

        cityStateCountry = new CityStateCountry(context);


        editText_country.setPrompt("Country");

        final ArrayAdapter<String> adapter_country =
                new ArrayAdapter<String>(getContext(), R.layout.country_state_list_item, cityStateCountry.getCountries());
        adapter_country.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editText_country.setAdapter(adapter_country);
        editText_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<String> adapter_states1 =
                        new ArrayAdapter<String>(getContext(), R.layout.country_state_list_item, cityStateCountry.getStates(String.valueOf(cityStateCountry.getCountryCode(position))));
                editText_state.setAdapter(adapter_states1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        final ArrayAdapter<String> adapter_states =
                new ArrayAdapter<String>(getContext(), R.layout.country_state_list_item, cityStateCountry.getStates("0"));
        adapter_states.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editText_state.setAdapter(adapter_states);
        editText_address_country.setAdapter(adapter_country);
        editText_address_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<String> adapter_states1 =
                        new ArrayAdapter<String>(getContext(), R.layout.country_state_list_item, cityStateCountry.getStates(String.valueOf(cityStateCountry.getCountryCode(position))));
                editText_address_state.setAdapter(adapter_states1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        editText_address_state.setAdapter(adapter_states);


        editText_latest_edu.setThreshold(0);
        final String[] latesteducation = getResources().getStringArray(R.array.list_edu);
        ArrayAdapter<String> adapter_latestedu =
                new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, latesteducation);
        editText_latest_edu.setAdapter(adapter_latestedu);

        editText_latest_edu.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focused) {
                if (focused) {
                    editText_latest_edu.showDropDown();

                }
            }
        });


        btn_cancel_intro = (Button) rootView.findViewById(R.id.btn_cancel_intro);
        btn_save_intro = (Button) rootView.findViewById(R.id.btn_save_intro);
        btn_save_summary = (Button) rootView.findViewById(R.id.btn_save_summary);
        btn_cancel_summary = (Button) rootView.findViewById(R.id.btn_cancel_summary);
        btn_save_address = (Button) rootView.findViewById(R.id.btn_save_address);
        btn_cancel_address = (Button) rootView.findViewById(R.id.btn_cancel_address);
        btn_save_presence = (Button) rootView.findViewById(R.id.btn_save_presence);
        btn_cancel_presence = (Button) rootView.findViewById(R.id.btn_cancel_presence);


        editText_birthday.setFocusable(false);

        editText_birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        editText_teaching_since.setFocusable(false);

        editText_teaching_since.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(getContext(),
                        new MonthPickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(int selectedMonth, int selectedYear) { // on date set
                                editText_teaching_since.setText(String.valueOf(selectedYear));
                            }
                        }, 2018, 11);

                builder.setActivatedMonth(Calendar.JULY)
                        .setMinYear(1950)
                        .setActivatedYear(2019)
                        .setMaxYear(2019)
                        .showYearOnly()
                        .build().show();


            }
        });


        btn_edit_intro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout_bio.setVisibility(View.GONE);
                linearLayout_editbio.setVisibility(View.VISIBLE);
                editText_bio.setText(bio.getText().toString());
                editText_country.setSelection(currentuser.getCountry());
                ArrayAdapter<String> adapter_states2 =
                        new ArrayAdapter<String>(getContext(), R.layout.country_state_list_item, cityStateCountry.getStates(String.valueOf(currentuser.getCountry())));
                editText_state.setAdapter(adapter_states2);


                /*ArrayAdapter<String> adapter_cities2 =
                        new ArrayAdapter<String>(getContext(), R.layout.country_state_list_item, cityStateCountry.getCities(String.valueOf(currentuser.getState())));
                editText_city.setAdapter(adapter_cities2);*/

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        editText_state.setSelection(cityStateCountry.getStateId(currentuser.getState()),false);

                    }
                }, 500);

                /*new Handler().postDelayed(new Runnable() {
                    public void run() {
                        editText_city.setSelection(cityStateCountry.getCityId(currentuser.getHometown()),false);

                    }
                }, 1000);*/

                editText_contact.setText(currentuser.getContact());
                //editText_teaching_since.setText(currentuser.getTeachingsince());
                //editText_teaching_at.setText(currentuser.getTeachingat());
               // editText_latest_edu.setText(currentuser.getLatestedu());
                editText_city.setText(currentuser.getHometown());
                editText_birthday.setText(birthday.getText().toString());
                btn_edit_intro.setEnabled(false);
                editText_bio.setSelection(editText_bio.getText().length());
                editText_contact.setSelection(9);
                /*editText_country.setSelection(residence_text[2].length());
                editText_city.setSelection(residence_text[0].length());
                editText_state.setSelection(residence_text[1].length());*/

            }
        });

        btn_save_intro = (Button) rootView.findViewById(R.id.btn_save_intro);
        btn_save_intro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> userdetailmap = new HashMap();
                userdetailmap.put("headline", editText_bio.getText().toString());
                KrigerConstants.user_detailRef.child(user.getUid()).updateChildren(userdetailmap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        HashMap<String, Object> usermap = new HashMap<>();
                        usermap.put("country", cityStateCountry.getCountryCode(editText_country.getSelectedItemPosition()));
                        usermap.put("state", cityStateCountry.getStateCode(editText_state.getSelectedItemPosition()));
                        usermap.put("hometown", editText_city.getText().toString());
                        //usermap.put("teachingsince", editText_teaching_since.getText().toString());
                        //usermap.put("teachingat", editText_teaching_at.getText().toString());
                        //usermap.put("latestedu", editText_latest_edu.getText().toString());
                        usermap.put("contact", editText_contact.getText().toString());
                        currentuser.setHometown(editText_city.getText().toString());
                        currentuser.setState(cityStateCountry.getStateCode(editText_state.getSelectedItemPosition()));
                        currentuser.setCountry(cityStateCountry.getCountryCode(editText_country.getSelectedItemPosition()));

                        if (!editText_birthday.getText().toString().equals(birthday.getText().toString())) {
                            String myFormat = "dd/MM/yyyy";
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            usermap.put("birthday", sdf.format(myCalendar.getTime()));
                        }

                        KrigerConstants.userRef.child(user.getUid()).updateChildren(usermap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toasty.custom(getContext(), "Intro updated", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                        true).show();
                                birthday.setText(editText_birthday.getText().toString());
                                bio.setText(editText_bio.getText().toString());
                               // teachingsince.setText("Teaching since : " + editText_teaching_since.getText().toString());
                               // teachingat.setText("Teaching at : " + editText_teaching_at.getText().toString());
                               // latestedu.setText(editText_latest_edu.getText().toString());
                                Drawable contact_num = getContext().getResources().getDrawable(R.drawable.contact_num);
                                contact.setCompoundDrawablesWithIntrinsicBounds(contact_num, null, null, null);
                                String contact1 = editText_contact.getText().toString().substring(0, 5);
                                String contact2 = editText_contact.getText().toString().substring(5, 10);
                                contact.setText("+91 " + contact1 + " - " + contact2);
                                residence.setText(editText_city.getText().toString() + ", " + editText_state.getSelectedItem().toString() + ", " + editText_country.getSelectedItem().toString());
                                linearLayout_editbio.setVisibility(View.GONE);
                                linearLayout_bio.setVisibility(View.VISIBLE);
                                btn_edit_intro.setEnabled(true);


                            }
                        });
                    }
                });
            }
        });

        btn_cancel_intro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout_bio.setVisibility(View.VISIBLE);
                linearLayout_editbio.setVisibility(View.GONE);
                btn_edit_intro.setEnabled(true);
            }
        });

        btn_edit_summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                summary.setVisibility(View.GONE);
                linearlayout_summary.setVisibility(View.VISIBLE);
                edittext_summary.setText(summary.getText().toString());
                edittext_summary.setSelection(summary.getText().length());
                btn_edit_summary.setEnabled(false);

            }
        });

        btn_cancel_summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                summary.setVisibility(View.VISIBLE);
                linearlayout_summary.setVisibility(View.GONE);
                btn_edit_summary.setEnabled(true);
            }
        });

        btn_save_summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("summary", edittext_summary.getText().toString());
                KrigerConstants.userRef.child(user.getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toasty.custom(getContext(), "Summary updated", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                true).show();
                        linearlayout_summary.setVisibility(View.GONE);
                        summary.setVisibility(View.VISIBLE);
                        summary.setText(edittext_summary.getText().toString());
                        btn_edit_summary.setEnabled(true);
                    }
                });

            }
        });


        btn_edit_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                address.setVisibility(View.GONE);
                lnr_address.setVisibility(View.GONE);
                linearlayout_address.setVisibility(View.VISIBLE);

                try {
                    edittext_house.setText(currentuser.getHouse().trim());
                    edittext_house.setSelection(currentuser.getHouse().length());
                    edittext_street.setText(currentuser.getStreet().trim());
                    edittext_street.setSelection(currentuser.getStreet().length());
                    edittext_landmark.setText(currentuser.getLandmark().trim());
                    edittext_landmark.setSelection(currentuser.getLandmark().length());
                    editText_address_country.setSelection(currentuser.getAddress_country());
                    editText_address_city.setText(currentuser.getAddress_city());

                    ArrayAdapter<String> adapter_states2 =
                            new ArrayAdapter<String>(getContext(), R.layout.country_state_list_item, cityStateCountry.getStates(String.valueOf(currentuser.getAddress_country())));
                    editText_address_state.setAdapter(adapter_states2);


                    /*ArrayAdapter<String> adapter_cities2 =
                            new ArrayAdapter<String>(getContext(), R.layout.country_state_list_item, cityStateCountry.getCities(String.valueOf(currentuser.getAddress_state())));
                    editText_address_city.setAdapter(adapter_cities2);*/

                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            editText_address_state.setSelection(cityStateCountry.getStateId(currentuser.getAddress_state()),false);

                        }
                    }, 500);

                    /*new Handler().postDelayed(new Runnable(){
                        public void run(){
                            editText_address_city.setSelection(cityStateCountry.getCityId(currentuser.getAddress_city()),false);
                        }
                    },1000);*/

                    edittext_pincode.setText(currentuser.getPincode());
                    edittext_pincode.setSelection(currentuser.getPincode().length());
                    btn_edit_address.setEnabled(false);

                } catch (IndexOutOfBoundsException e) {

                }catch (NullPointerException e){

                }

            }
        });

        btn_cancel_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edittext_house.getText().toString().isEmpty() || edittext_street.getText().toString().isEmpty() || edittext_landmark.getText().toString().isEmpty() || cityStateCountry.getCountryCode(editText_address_country.getSelectedItemPosition()) == 0 || cityStateCountry.getStateCode(editText_address_state.getSelectedItemPosition()) == 0 || editText_address_city.getText().length() == 0 || edittext_pincode.getText().toString().isEmpty()) {
                    lnr_address.setVisibility(View.VISIBLE);
                    linearlayout_address.setVisibility(View.GONE);
                    btn_edit_address.setEnabled(true);
                } else {
                    address.setVisibility(View.VISIBLE);
                    linearlayout_address.setVisibility(View.GONE);
                    btn_edit_address.setEnabled(true);
                }


            }
        });

        btn_save_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edittext_house.getText().toString().isEmpty() || edittext_street.getText().toString().isEmpty() || edittext_landmark.getText().toString().isEmpty() || cityStateCountry.getCountryCode(editText_address_country.getSelectedItemPosition()) == 0 || cityStateCountry.getStateCode(editText_address_state.getSelectedItemPosition()) == 0 || editText_address_city.getText().length() == 0 || edittext_pincode.getText().toString().isEmpty()) {
                    Toasty.custom(getContext(), "Kindly fill the details", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                } else {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("house", edittext_house.getText().toString());
                    map.put("street", edittext_street.getText().toString());
                    map.put("landmark", edittext_landmark.getText().toString());
                    map.put("address_country",cityStateCountry.getCountryCode(editText_address_country.getSelectedItemPosition()));
                    map.put("address_state", cityStateCountry.getStateCode(editText_address_state.getSelectedItemPosition()));
                    map.put("address_city", editText_address_city.getText().toString());
                    map.put("pincode", edittext_pincode.getText().toString());
                    currentuser.setHouse(edittext_house.getText().toString());
                    currentuser.setStreet(edittext_street.getText().toString());
                    currentuser.setLandmark(edittext_landmark.getText().toString());
                    currentuser.setAddress_country(cityStateCountry.getCountryCode(editText_address_country.getSelectedItemPosition()));
                    currentuser.setAddress_state(cityStateCountry.getStateCode(editText_address_state.getSelectedItemPosition()));
                    currentuser.setAddress_city(editText_address_city.getText().toString());
                    currentuser.setPincode(edittext_pincode.getText().toString());

                    KrigerConstants.userRef.child(user.getUid()).child("address").updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toasty.custom(getContext(), "Address updated", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                    true).show();
                            linearlayout_address.setVisibility(View.GONE);
                            address.setVisibility(View.VISIBLE);
                            address.setText(edittext_house.getText().toString() + "," + edittext_street.getText().toString() + "," + edittext_landmark.getText().toString() + "," + editText_address_city.getText().toString() + "," + editText_address_state.getSelectedItem().toString() + "," + editText_address_country.getSelectedItem().toString() + "," + edittext_pincode.getText().toString());
                            btn_edit_address.setEnabled(true);
                        }
                    });

                }

            }
        });


        btn_edit_presence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lnr_presence.setVisibility(View.GONE);
                presence1.setVisibility(View.GONE);
                presence2.setVisibility(View.GONE);
                presence3.setVisibility(View.GONE);
                presence4.setVisibility(View.GONE);
                presence5.setVisibility(View.GONE);
                presence6.setVisibility(View.GONE);
                linearlayout_presence.setVisibility(View.VISIBLE);
                edittext_presence1.setText(presence1.getText().toString());
                edittext_presence1.setSelection(presence1.getText().length());
                edittext_presence2.setText(presence2.getText().toString());
                edittext_presence2.setSelection(presence2.getText().length());
                edittext_presence3.setText(presence3.getText().toString());
                edittext_presence3.setSelection(presence3.getText().length());
                edittext_presence4.setText(presence4.getText().toString());
                edittext_presence4.setSelection(presence4.getText().length());
                edittext_presence5.setText(presence5.getText().toString());
                edittext_presence5.setSelection(presence5.getText().length());
                edittext_presence6.setText(presence6.getText().toString());
                edittext_presence6.setSelection(presence6.getText().length());
                btn_edit_presence.setEnabled(false);

            }
        });

        btn_cancel_presence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edittext_presence1.getText().toString().isEmpty() || edittext_presence2.getText().toString().isEmpty() || edittext_presence3.getText().toString().isEmpty() || edittext_presence4.getText().toString().isEmpty() || edittext_presence5.getText().toString().isEmpty() || edittext_presence6.getText().toString().isEmpty()) {
                    lnr_presence.setVisibility(View.VISIBLE);
                    linearlayout_presence.setVisibility(View.GONE);
                    btn_edit_presence.setEnabled(true);
                } else {
                    presence1.setVisibility(View.VISIBLE);
                    presence2.setVisibility(View.VISIBLE);
                    presence3.setVisibility(View.VISIBLE);
                    presence4.setVisibility(View.VISIBLE);
                    presence5.setVisibility(View.VISIBLE);
                    presence6.setVisibility(View.VISIBLE);
                    linearlayout_presence.setVisibility(View.GONE);
                    btn_edit_presence.setEnabled(true);
                }

            }
        });



        btn_save_presence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Pattern urlPattern = Pattern.compile(
                        "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                                + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                                + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
                        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

                HashMap<String, Object> map = new HashMap<>();
                if (urlPattern.matcher(edittext_presence1.getText().toString()).matches() || edittext_presence1.getText().toString().isEmpty()) {
                    if (urlPattern.matcher(edittext_presence2.getText().toString()).matches() || edittext_presence2.getText().toString().isEmpty()){
                        if (urlPattern.matcher(edittext_presence3.getText().toString()).matches() || edittext_presence3.getText().toString().isEmpty()){
                            if (urlPattern.matcher(edittext_presence4.getText().toString()).matches() || edittext_presence4.getText().toString().isEmpty()){
                                if (urlPattern.matcher(edittext_presence5.getText().toString()).matches() || edittext_presence5.getText().toString().isEmpty()){
                                    if (urlPattern.matcher(edittext_presence6.getText().toString()).matches() || edittext_presence6.getText().toString().isEmpty()) {
                                        map.put("website", edittext_presence1.getText().toString());
                                        map.put("facebook", edittext_presence2.getText().toString());
                                        map.put("instagram", edittext_presence3.getText().toString());
                                        map.put("linkedin", edittext_presence4.getText().toString());
                                        map.put("twitter", edittext_presence5.getText().toString());
                                        map.put("youtube", edittext_presence6.getText().toString());
                                        KrigerConstants.userRef.child(user.getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toasty.custom(getContext(), "Updated", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                                        true).show();
                                                linearlayout_presence.setVisibility(View.GONE);
                                                linearlayout_presence_textview.setVisibility(View.VISIBLE);
                                                presence1.setVisibility(View.VISIBLE);
                                                presence1.setText(edittext_presence1.getText().toString());
                                                presence2.setVisibility(View.VISIBLE);
                                                presence2.setText(edittext_presence2.getText().toString());
                                                presence3.setVisibility(View.VISIBLE);
                                                presence3.setText(edittext_presence3.getText().toString());
                                                presence4.setVisibility(View.VISIBLE);
                                                presence4.setText(edittext_presence4.getText().toString());
                                                presence5.setVisibility(View.VISIBLE);
                                                presence5.setText(edittext_presence5.getText().toString());
                                                presence6.setVisibility(View.VISIBLE);
                                                presence6.setText(edittext_presence6.getText().toString());
                                                btn_edit_presence.setEnabled(true);
                                            }
                                        });
                                    }
                                    else {
                                        Toasty.custom(context,"Invalid Youtube Link", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                                true).show();
                                    }
                                }else {
                                    Toasty.custom(context,"Invalid Twitter Link", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                            true).show();
                                }
                            }else {
                                Toasty.custom(context,"Invalid Linkedin Link", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                        true).show();
                            }
                        }else {
                            Toasty.custom(context,"Invalid Instagram Link", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                    true).show();
                        }
                    }else {
                        Toasty.custom(context,"Invalid Facebook Link", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                true).show();
                    }
                }else {
                    Toasty.custom(context,"Invalid Website Link", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                }



            }
        });


        display_prepration_exam_header =  rootView.findViewById(R.id.display_prepration_exam_header);
        display_prepration_exam =  rootView.findViewById(R.id.display_prepration_exam);
        display_prepration_sub_header =  rootView.findViewById(R.id.display_prepration_sub_header);
        display_prepration_sub =  rootView.findViewById(R.id.display_prepration_sub);
        display_college_header =  rootView.findViewById(R.id.display_college_header);
        display_college =  rootView.findViewById(R.id.display_college);
        display_teaching =  rootView.findViewById(R.id.display_teaching);
        display_teaching_header =  rootView.findViewById(R.id.display_teaching_header);
        display_awards =  rootView.findViewById(R.id.display_awards);
        display_certifications =  rootView.findViewById(R.id.display_certifications);
        display_internships =  rootView.findViewById(R.id.display_internships);
        display_coaching =  rootView.findViewById(R.id.display_coaching);
        display_publication =  rootView.findViewById(R.id.display_publication);
        display_specialisation = rootView.findViewById(R.id.display_specialisation);



        linear_prepration_exam =  rootView.findViewById(R.id.lnr_prepration_exam);
        linear_prepration_sub =  rootView.findViewById(R.id.lnr_prepration_sub);
        linear_clg =  rootView.findViewById(R.id.lnr_clg);
        linear_teaching =  rootView.findViewById(R.id.lnr_teaching);
        linear_cochng =  rootView.findViewById(R.id.lnr_cochng);
        linear_awds =  rootView.findViewById(R.id.lnr_awds);
        linear_cert =  rootView.findViewById(R.id.lnr_cert);
        linear_intern =  rootView.findViewById(R.id.lnr_intern);
        linear_publication =  rootView.findViewById(R.id.lnr_publication);
        linear_specialisation =  rootView.findViewById(R.id.lnr_specialisation);
        lnr_address =  rootView.findViewById(R.id.lnr_address);
        lnr_presence =  rootView.findViewById(R.id.lnr_presence);


        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.custom_progressbar);
        profile_progress =  rootView.findViewById(R.id.determinateBar);

        profile_progress.setProgressDrawable(drawable);
        profile_progress.incrementProgressBy(10);
        link = rootView.findViewById(R.id.btn_refer);


        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileshare();

            }
        });

        try {
            bundle = getArguments();
            if (bundle.getString("Invite friend").equals("true")) {
                profileshare();
                FragmentTransaction transection = getFragmentManager().beginTransaction();
                Home mfragment = new Home();
                transection.replace(R.id.frag_pro, mfragment);
                transection.commit();


            }
        } catch (NullPointerException e) {

        }


        button_connect =  rootView.findViewById(R.id.button_connect);
        button_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/");
                SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
                final String format = s.format(new Date());

                mDatabase.child("User_Suggestion").child(user.getUid()).child(userid).removeValue();
                mDatabase.child("Invitation").child(userid).child(user.getUid()).setValue(format);
                mDatabase.child("Sent_Connection").child(user.getUid()).child(userid).setValue(format).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toasty.custom(getContext(), "Invitation sent", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                true).show();
                        button_connect.setVisibility(View.GONE);
                    }
                });

            }
        });


        profile_image =  rootView.findViewById(R.id.imageButton_photo);


        ScrollView scrollView =  rootView.findViewById(R.id.scroll_view);

        if (show_connect) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 0);
            scrollView.setLayoutParams(params);


        }


        try {
            if (!(getArguments().getString("user_id").equals(user.getUid()))) {

                //logout.setVisibility(View.GONE);
                layout_refer.setVisibility(View.GONE);
                address_layout.setVisibility(View.GONE);

            }
        } catch (NullPointerException e) {
        }

        profile_image_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(150, 150)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(getActivity());


            }
        });


        getuserdetails();
        getdetails();
        getusercount();
        getconnections();


        return rootView;


    }

    private void save_flow(LinearLayout layout) {
        if (layout == display_prepration_exam_header) {
            selected_choices_exam.clear();
            for (View view1 : getAllChildren(display_prepration_exam_header)) {
                if (view1 instanceof FlowLayout) {
                    for (View view : getAllChildren(view1)) {
                        if (view instanceof ImageView) {
                            if (!selected_choices_exam.contains(view.getId())) {
                                selected_choices_exam.add(view.getId());
                                view.setVisibility(View.GONE);
                            }
                        }

                    }
                }

            }
            KrigerConstants.userRef.child(user.getUid()).child("exam").setValue(selected_choices_exam);
            add_save_prepration_exam.setVisibility(View.GONE);

        } else {
            selected_choices_sub.clear();
            for (View view1 : getAllChildren(display_prepration_sub_header)) {
                if (view1 instanceof FlowLayout) {
                    for (View view : getAllChildren(view1)) {
                        if (view instanceof ImageView) {
                            if (!selected_choices_sub.contains(view.getId())) {
                                selected_choices_sub.add(view.getId());
                                view.setVisibility(View.GONE);
                            }
                        }

                    }
                }

            }
            KrigerConstants.userRef.child(user.getUid()).child("subject").setValue(selected_choices_sub);
            add_save_prepration_sub.setVisibility(View.GONE);
        }
    }

    private void edit_flow(final LinearLayout layout) {
        layout_current.setVisibility(View.VISIBLE);
        ArrayList<View> viewlist = getAllChildren(layout);
        final String[] choicelist;

        final ArrayList<Integer> selected_choices;
        final Boolean sub;
        if (layout == display_prepration_exam_header) {
            choicelist = getResources().getStringArray(R.array.list_exams);
            selected_choices = selected_choices_exam;
            sub = false;
        } else {
            choicelist = getResources().getStringArray(R.array.list_sub);
            selected_choices = selected_choices_sub;
            sub = true;
        }

        FlowLayout flow = null;
        for (View view : viewlist) {
            if (view instanceof FlowLayout) {
                flow = (FlowLayout) view;
            }
        }
        final FlowLayout flowLayout = flow;


        for (View view : viewlist) {
            if (view instanceof CustomAutoCompleteTextView) {
                final AutoCompleteTextView subjects_choice = (AutoCompleteTextView) view;
                ArrayAdapter<String> adapter_subjects =
                        new ArrayAdapter<String>(getContext(), R.layout.prepration_list_item, choicelist);
                subjects_choice.setAdapter(adapter_subjects);
                subjects_choice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String selected = (String) parent.getItemAtPosition(position);
                        if (!selected_choices.contains(Arrays.asList(choicelist).indexOf(selected))) {

                            LinearLayout layout1 = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_flow_item, flowLayout, false);
                            TextView textView1 = layout1.findViewById(R.id.name);
                            textView1.setText(selected);
                            ImageView imageView = layout1.findViewById(R.id.buttonExitIcon);
                            int value = Arrays.asList(choicelist).indexOf(selected);
                            imageView.setId(value);
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (sub) {
                                        if (selected_choices.size() > 1 || selected_choices_sub.size() > 1) {
                                            int index = selected_choices.indexOf(v.getId());
                                            flowLayout.removeViewAt(index);
                                            selected_choices.remove(index);
                                        } else {
                                            Toasty.custom(getContext(), "Minimum 1 entry required", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                                    true).show();
                                        }
                                    } else {
                                        if (selected_choices.size() > 1 || selected_choices_exam.size() > 1) {
                                            int index = selected_choices.indexOf(v.getId());
                                            flowLayout.removeViewAt(index);
                                            selected_choices.remove(index);
                                        } else {

                                            Toasty.custom(getContext(), "Minimum 1 entry required", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                                    true).show();
                                        }
                                    }
                                }
                            });
                            try {
                                flowLayout.addView(layout1);
                                subjects_choice.setText("");
                                selected_choices.add(value);
                            } catch (NullPointerException e) {
                                if (layout == display_prepration_exam_header) {
                                    View childLayout = getLayoutInflater().inflate(R.layout.layout_exams_display,
                                            display_prepration_exam, false);
                                    ImageView sub = (ImageView) childLayout.findViewById(R.id.card_image);
                                    sub.setImageResource(R.drawable.prepration_exam);

                                    display_prepration_exam.addView(childLayout);
                                    llayout = (LinearLayout) childLayout.findViewById(R.id.llayout);
                                    final FlowLayout flowLayout = childLayout.findViewById(R.id.layout_flow);
                                    flowLayout.addView(layout1);
                                    subjects_choice.setText("");
                                    selected_choices.add(value);
                                } else {
                                    View childLayout = getLayoutInflater().inflate(R.layout.layout_exams_display,
                                            display_prepration_sub, false);
                                    ImageView sub = (ImageView) childLayout.findViewById(R.id.card_image);
                                    sub.setImageResource(R.drawable.prepration_sub);

                                    display_prepration_sub.addView(childLayout);

                                    llayout = (LinearLayout) childLayout.findViewById(R.id.llayout);
                                    final FlowLayout flowLayout = childLayout.findViewById(R.id.layout_flow);
                                    flowLayout.addView(layout1);
                                    subjects_choice.setText("");
                                    selected_choices.add(value);
                                }

                            }

                        } else {
                            Toasty.custom(getContext(), "Item already selected", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                    true).show();
                            subjects_choice.setText("");
                        }

                    }
                });
            }
            if (view instanceof ImageView) {
                view.setVisibility(View.VISIBLE);
            }
        }

    }


    public void updateLabel() {

        String myFormat = "MMM dd, yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editText_birthday.setText(sdf.format(myCalendar.getTime()));
        edited_birthday = true;

    }


    //refer
    public void pushreferral(final String shortLink) {


        View inflatedFrame = getLayoutInflater().inflate(R.layout.layout_referal, null);
        RelativeLayout relativeLayout = inflatedFrame.findViewById(R.id.referal);

        ImageView imageView =  inflatedFrame.findViewById(R.id.bio);
        final ImageView imageView1 =  inflatedFrame.findViewById(R.id.school_ref);
        final ImageView imageView2 =  inflatedFrame.findViewById(R.id.college_ref);
        ImageView profilepic =  inflatedFrame.findViewById(R.id.profile_photo);
        profilepic.setImageDrawable(profile_image.getDrawable());
        ImageView imageView3 =  inflatedFrame.findViewById(R.id.profile_name);
        ImageView share_bottom =  inflatedFrame.findViewById(R.id.kc_bottombar);
        ImageView share_top =  inflatedFrame.findViewById(R.id.kc_topbar);


        line_above_summary.setVisibility(View.VISIBLE);

        if (isFacebookShare) {
            contact.setVisibility(View.GONE);
            birthday.setVisibility(View.GONE);
        }

        //screenshot of intro
        View v2 = layout_name;
        v2.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(v2.getDrawingCache());
        v2.setDrawingCacheEnabled(false);
        imageView3.setImageBitmap(bitmap);


        View v1 = layout_toberefer;
        v1.setDrawingCacheEnabled(true);
        Bitmap bit = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);
        imageView.setImageBitmap(bit);


        //screenshot of dashboard
        View v4 = profile_visitors;
        v4.setDrawingCacheEnabled(true);
        Bitmap bit1 = Bitmap.createBitmap(v4.getDrawingCache());
        v4.setDrawingCacheEnabled(false);
        imageView1.setImageBitmap(bit1);


        btn_edit_college.setVisibility(View.GONE);
        display_college_header.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        Bitmap bit2 = Bitmap.createBitmap(bit.getWidth(), display_college_header.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bit2);
        display_college_header.layout(0, 0, bit.getWidth(), display_college_header.getMeasuredHeight());
        display_college_header.draw(c);
        imageView2.setImageBitmap(bit2);
        imageView2.setVisibility(View.GONE);


        share_bottom.getLayoutParams().width = bit.getWidth();
        share_top.getLayoutParams().width = bit.getWidth();

        relativeLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        Bitmap b = Bitmap.createBitmap(relativeLayout.getMeasuredWidth(), relativeLayout.getMeasuredHeight(), Bitmap.Config.RGB_565);
        Canvas c1 = new Canvas(b);
        relativeLayout.layout(0, 0, relativeLayout.getMeasuredWidth(), relativeLayout.getMeasuredHeight());
        relativeLayout.draw(c1);


        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        try {

            File cachePath = new File(getContext().getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            b.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

        String text = "corporate\n"+"Join me in making INDIA 100% literate : " + shortLink +
                "\n" +
                "Sign up to\n" +
                "Connect with Students, Teachers, Colleges, Coaching \n" +
                "Purchase Study material, Classes, Practice papers\n" +
                "Make or join pan INDIA study groups";

        if (isFacebookShare) {


            Boolean hasPackage = HomeActivity.isPackageInstalled(getActivity(), "com.facebook.katana");
            if (hasPackage) {

                try {

                    text = "Join me in making INDIA 100% literate : " + shortLink +
                            "\n" +
                            "Sign up to\n" +
                            "Connect with Students, Teachers, Colleges, Coaching \n" +
                            "Purchase Study material, Classes, Practice papers\n" +
                            "Make or join pan INDIA study groups" + "\n#krigercampus #100%LiterateIndia";


                    ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("text", text);
                    clipboard.setPrimaryClip(clip);


                    SharePhoto photo = new SharePhoto.Builder()
                            .setBitmap(b)
                            .build();

                    SharePhotoContent content = new SharePhotoContent.Builder()
                            .addPhoto(photo)
                            .build();

                    dialogBuilder("Text has been copied to clipboard. Long press Textbox and click \"paste\".", 2, false, "OK", null, content).show();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toasty.custom(getContext(), "Facebook not installed", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                        true).show();
            }


        } else {

            File imagePath = new File(getContext().getCacheDir(), "images");
            File newFile = new File(imagePath, "image.png");
            Uri contentUri = FileProvider.getUriForFile(getContext(), "in.kriger.krigercampus.fileprovider", newFile);
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.setType("image/jpeg");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try {
                startActivity(shareIntent);
            } catch (android.content.ActivityNotFoundException ex) {

            }


        }


        contact.setVisibility(View.VISIBLE);
        birthday.setVisibility(View.VISIBLE);
        line_above_summary.setVisibility(View.VISIBLE);
        btn_edit_college.setVisibility(View.VISIBLE);






    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {

                }
            } else {
                // Sending failed or it was canceled, show failure message to the user
                // ...
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                uri = result.getUri();
                try {
                    File file = new File(uri.getPath());
                    File compressedFile = new Compressor(context).compressToFile(file);
                    compressedUri = Uri.fromFile(compressedFile);


                    Bitmap compressedBitmap = BitmapFactory.decodeFile(compressedFile.getAbsolutePath());
                    profile_image.setImageBitmap(compressedBitmap);

                    squareFile = new Compressor(getContext()).setMaxHeight(50).setMaxWidth(50).setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES).getAbsolutePath()).compressToFile(file);
                    try {
                        updateprofile(compressedUri, Uri.fromFile(squareFile));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } catch (IOException exp) {

                } catch (NullPointerException e) {

                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toasty.custom(context, error.getMessage(), R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                        true).show();


            }
        }
    }

//set Name and bio

    public void getuserdetails() {


        DatabaseReference userRef = KrigerConstants.user_detailRef.child(userid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    try {
                        profile_name.setText(dataSnapshot.child("name").getValue().toString());
                        bio.setText(dataSnapshot.child("headline").getValue().toString());


                        if (userid.equals(user.getUid())) {
                            if (prefManager.getProfileImageUrl() == null) {


                                if (dataSnapshot.child("original").exists()) {
                                    if (getContext() != null) {

                                        RequestOptions requestOption = new RequestOptions()
                                                .placeholder(R.drawable.default_profile)
                                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


                                        Glide.with(getContext()).load(dataSnapshot.child("original").getValue().toString())

                                                .apply(requestOption)
                                                .into(profile_image);



                                        prefManager.setProfileImageUrl(dataSnapshot.child("thumb").getValue().toString()); //thumbnail
                                    }
                                } else {
                                    profile_image.setImageResource(R.drawable.default_profile);
                                }




                            } else {
                                RequestOptions requestOption = new RequestOptions()
                                        .placeholder(R.drawable.default_profile)
                                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


                                Glide.with(getContext()).load(prefManager.getProfileImageUrl())
                                        .apply(requestOption)
                                        .into(profile_image);


                            }

                        }
                        else{
                            DatabaseReference picref = KrigerConstants.user_detailRef.child(userid);
                            picref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    try {
                                        url = dataSnapshot.child("thumb").getValue().toString();

                                        RequestOptions requestOption = new RequestOptions()
                                                .placeholder(R.drawable.default_profile)
                                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


                                        Glide.with(getContext()).load(url)

                                                .apply(requestOption)
                                                .into(profile_image);


                                    }catch (NullPointerException e){
                                        profile_image.setImageResource(R.drawable.default_profile);
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }






                    } catch (NullPointerException e) {
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getusercount() {


        try {
            if (!(getArguments().getString("user_id").equals(user.getUid()))) {
                visitors_layout.setVisibility(View.GONE);
            }
        } catch (NullPointerException e) {
        }

        KrigerConstants.user_counterRef.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("count_connections").exists()) {
                    connections_ans.setText(dataSnapshot.child("count_connections").getValue().toString());

                }
                if (dataSnapshot.child("count_posts").exists()) {
                    posts_ans.setText(dataSnapshot.child("count_posts").getValue().toString());
                }
                if (dataSnapshot.child("count_groups").exists()) {
                    groups_ans.setText(dataSnapshot.child("count_groups").getValue().toString());
                }
                int real = 0, fake = 0;

                if (dataSnapshot.child("count_profileviews").exists()) {
                    real = Integer.valueOf(dataSnapshot.child("count_profileviews").getValue().toString());
                }
                if (dataSnapshot.child("count_profileviews_fake").exists()) {
                    fake = Integer.valueOf(dataSnapshot.child("count_profileviews_fake").getValue().toString());
                }

                visitors_ans.setText(String.valueOf(real + fake));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //refer score
    public void getdetails() {


        KrigerConstants.userRef.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    if (dataSnapshot.child("count_referrals").exists()) {
                        refer_score.setText(dataSnapshot.child("count_referrals").getValue().toString());
                    } else {
                        refer_score.setText("0");
                    }


                    try {
                        currentuser = dataSnapshot.getValue(User.class);
                        try {

                            String date = currentuser.getBirthday();
                            SimpleDateFormat spf = new SimpleDateFormat("dd/MM/yyyy");
                            Date newDate = null;
                            try {
                                newDate = spf.parse(date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            spf = new SimpleDateFormat("MMM dd, yyyy");
                            date = spf.format(newDate);

                            try {
                                Drawable birthday_img = getContext().getResources().getDrawable(R.drawable.birthday_img);
                                birthday.setCompoundDrawablesWithIntrinsicBounds(birthday_img, null, null, null);

                                    //displaying to other users also
                                    birthday.setText(date);


                            } catch (NullPointerException e) {
                            }

                        } catch (NullPointerException e) {

                        }

                        try {
                            Drawable contact_num = getContext().getResources().getDrawable(R.drawable.contact_num);
                            contact.setCompoundDrawablesWithIntrinsicBounds(contact_num, null, null, null);

                            if (userid.equals(user.getUid())) {
                                //not displaying to other users
                                String contact1 = currentuser.getContact().substring(0, 5);
                                String contact2 = currentuser.getContact().substring(5, 10);
                                contact.setText("+91 " + contact1 + " - " + contact2);
                            } else {
                                contact.setText("Private Information");
                            }


                        } catch (NullPointerException e) {


                        }
                        try {
                            if (currentuser.getState()!= 0 && currentuser.getHometown().length() != 0) {

                                residence.setText(currentuser.getHometown() + ", " + cityStateCountry.getState(currentuser.getState())+ ", " + cityStateCountry.getCountry(currentuser.getCountry()));
                            }

                        } catch (NullPointerException e) {
                            residence.setVisibility(View.VISIBLE);
                        }


                        try {
                            if (!currentuser.getSummary().equals(null)) {

                                summary.setText(currentuser.getSummary());
                            }

                        } catch (NullPointerException e) {
                            summary.setVisibility(View.GONE);
                            line_above_summary.setVisibility(View.GONE);
                        }


                        try {

                            if(!currentuser.getWebsite().equals(null)){
                                lnr_presence.setVisibility(View.GONE);
                                linearlayout_presence_textview.setVisibility(View.VISIBLE);
                                presence1.setText(currentuser.getWebsite());
                            }

                            if(!currentuser.getFacebook().equals(null)){
                                lnr_presence.setVisibility(View.GONE);
                                linearlayout_presence_textview.setVisibility(View.VISIBLE);
                                presence2.setText(currentuser.getFacebook());
                            }

                            if(!currentuser.getInstagram().equals(null)){
                                lnr_presence.setVisibility(View.GONE);
                                linearlayout_presence_textview.setVisibility(View.VISIBLE);
                                presence3.setText(currentuser.getInstagram());
                            }
                            if(!currentuser.getLinkedin().equals(null)){
                                lnr_presence.setVisibility(View.GONE);
                                linearlayout_presence_textview.setVisibility(View.VISIBLE);
                                presence4.setText(currentuser.getLinkedin());
                            }
                            if(currentuser.getTwitter().equals(null)){
                                lnr_presence.setVisibility(View.GONE);
                                linearlayout_presence_textview.setVisibility(View.VISIBLE);
                                presence5.setText(currentuser.getTwitter());
                            }
                            if(currentuser.getYoutube().equals(null)){
                                lnr_presence.setVisibility(View.GONE);
                                linearlayout_presence_textview.setVisibility(View.VISIBLE);
                                presence6.setText(currentuser.getYoutube());
                            }

                        } catch (NullPointerException e) {
                            lnr_presence.setVisibility(View.VISIBLE);
                            linearlayout_presence_textview.setVisibility(View.GONE);
                        }




                    } catch (DatabaseException e) {

                    }


                    try {
                        LayoutInflater inflater = getLayoutInflater();

                        if (dataSnapshot.child("address").exists()) {


                            address.setVisibility(View.VISIBLE);
                            lnr_address.setVisibility(View.GONE);
                            currentuser.setHouse(dataSnapshot.child("address").child("house").getValue().toString());
                            currentuser.setStreet(dataSnapshot.child("address").child("street").getValue().toString());
                            currentuser.setLandmark(dataSnapshot.child("address").child("landmark").getValue().toString());
                            currentuser.setAddress_country(((Long)dataSnapshot.child("address").child("address_country").getValue()).intValue());
                            currentuser.setAddress_state(((Long)dataSnapshot.child("address").child("address_state").getValue()).intValue());
                            currentuser.setAddress_city(dataSnapshot.child("address").child("address_city").getValue().toString());
                            currentuser.setPincode(dataSnapshot.child("address").child("pincode").getValue().toString());
                            address.setText(dataSnapshot.child("address").child("house").getValue().toString() + "," + dataSnapshot.child("address").child("street").getValue().toString() + "," + dataSnapshot.child("address").child("landmark").getValue().toString() + "," +  cityStateCountry.getCountry(currentuser.getAddress_country())+ "," + cityStateCountry.getState(currentuser.getAddress_state())+ "," + currentuser.getAddress_city() + "," + dataSnapshot.child("address").child("pincode").getValue().toString());



                        } else {
                            address.setVisibility(View.GONE);
                        }


                        if (dataSnapshot.child("exam").exists()) {

                            profile_progress.incrementProgressBy(13);
                            View childLayout = inflater.inflate(R.layout.layout_exams_display,
                                    display_prepration_exam, false);

                            display_prepration_exam.addView(childLayout);

                            llayout =  childLayout.findViewById(R.id.llayout);


                            for (DataSnapshot childsnapshot : dataSnapshot.child("exam").getChildren()) {
                                if (childsnapshot.exists()) {


                                    ImageView exam =  childLayout.findViewById(R.id.card_image);
                                    try {

                                        exam.setImageResource(R.drawable.prepration_exam);

                                        final FlowLayout flowLayout = childLayout.findViewById(R.id.layout_flow);
                                        LinearLayout layout1 = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_flow_item, flowLayout, false);
                                        TextView textView = layout1.findViewById(R.id.name);
                                        Integer value = Integer.valueOf(childsnapshot.getValue().toString());
                                        selected_choices_exam.add(value);
                                        textView.setText(exams[value]);
                                        ImageView imageView = layout1.findViewById(R.id.buttonExitIcon);
                                        imageView.setId(value);
                                        imageView.setVisibility(View.GONE);
                                        imageView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (selected_choices_exam.size() > 1) {
                                                    int index = selected_choices_exam.indexOf(v.getId());
                                                    flowLayout.removeViewAt(index);
                                                    selected_choices_exam.remove(index);
                                                } else {
                                                    Toasty.custom(getContext(), "Minimum 1 entry required", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                                            true).show();
                                                }


                                            }
                                        });
                                        flowLayout.addView(layout1);

                                        linear_prepration_exam.setVisibility(View.GONE);

                                    } catch (NullPointerException e) {

                                    } catch (ArrayIndexOutOfBoundsException e) {
                                        linear_prepration_exam.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    linear_prepration_exam.setVisibility(View.VISIBLE);
                                }


                            }
                        }

                        if (dataSnapshot.child("subject").exists()) {

                            profile_progress.incrementProgressBy(13);

                            View childLayout = inflater.inflate(R.layout.layout_exams_display,
                                    display_prepration_sub, false);

                            display_prepration_sub.addView(childLayout);

                            llayout =  childLayout.findViewById(R.id.llayout);


                            for (DataSnapshot childsnapshot : dataSnapshot.child("subject").getChildren()) {
                                if (childsnapshot.exists()) {


                                    ImageView sub =  childLayout.findViewById(R.id.card_image);
                                    try {
                                        sub.setImageResource(R.drawable.prepration_sub);

                                        final FlowLayout flowLayout1 = childLayout.findViewById(R.id.layout_flow);
                                        LinearLayout layout1 = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_flow_item, flowLayout1, false);
                                        TextView textView = layout1.findViewById(R.id.name);
                                        Integer value = Integer.valueOf(childsnapshot.getValue().toString());
                                        selected_choices_sub.add(value);
                                        textView.setText(subjects[value]);
                                        ImageView imageView = layout1.findViewById(R.id.buttonExitIcon);
                                        imageView.setId(value);
                                        imageView.setVisibility(View.GONE);
                                        imageView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                if (selected_choices_sub.size() > 1) {
                                                    int index = selected_choices_sub.indexOf(v.getId());
                                                    flowLayout1.removeViewAt(index);
                                                    selected_choices_sub.remove(index);
                                                } else {
                                                    Toasty.custom(getContext(), "Minimum 1 entry required", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                                            true).show();
                                                }


                                            }
                                        });
                                        flowLayout1.addView(layout1);

                                        linear_prepration_sub.setVisibility(View.GONE);
                                    } catch (NullPointerException e) {

                                    } catch (ArrayIndexOutOfBoundsException e) {
                                        linear_prepration_sub.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    linear_prepration_sub.setVisibility(View.VISIBLE);
                                }


                            }
                        }


                        if (dataSnapshot.child("teaching").exists()) {

                            profile_progress.incrementProgressBy(13);


                            for (DataSnapshot childsnapshot : dataSnapshot.child("teaching").getChildren()) {
                                if (childsnapshot.exists()) {
                                    View childLayout = inflater.inflate(R.layout.layout_display_autocomplete,
                                            display_teaching, false);

                                    display_teaching.addView(childLayout);

                                    ImageView teaching =  childLayout.findViewById(R.id.card_image);
                                    try {

                                        teaching.setImageResource(R.drawable.teaching);

                                    } catch (NullPointerException e) {

                                    }


                                    TextView name =  childLayout.findViewById(R.id.name);
                                    name.setTag(1);
                                    try {
                                        name.setText(childsnapshot.child("name").getValue().toString());
                                    } catch (NullPointerException e) {
                                    }

                                    TextView year =  childLayout.findViewById(R.id.year_from);
                                    year.setTag(2);
                                    try {
                                        year.setText(childsnapshot.child("year_from").getValue().toString());
                                    } catch (NullPointerException e) {
                                    }


                                    TextView year_to =  childLayout.findViewById(R.id.year_to);
                                    year_to.setTag(2);
                                    try {
                                        year_to.setText(childsnapshot.child("year_to").getValue().toString());
                                    } catch (NullPointerException e) {
                                    }

                                    TextView degree =  childLayout.findViewById(R.id.description);
                                    degree.setTag(1);
                                    try {
                                        degree.setText(childsnapshot.child("description").getValue().toString());
                                    } catch (NullPointerException e) {
                                    }

                                    linear_teaching.setVisibility(View.GONE);
                                } else {
                                    linear_teaching.setVisibility(View.VISIBLE);
                                }


                            }
                        }


                        if (dataSnapshot.child("college").exists()) {

                            profile_progress.incrementProgressBy(13);


                            for (DataSnapshot childsnapshot : dataSnapshot.child("college").getChildren()) {
                                if (childsnapshot.exists()) {
                                    View childLayout = inflater.inflate(R.layout.layout_display_autocomplete,
                                            display_college, false);

                                    display_college.addView(childLayout);

                                    ImageView college = childLayout.findViewById(R.id.card_image);
                                    try {

                                        college.setImageResource(R.drawable.college);

                                    } catch (NullPointerException e) {

                                    }


                                    TextView name = childLayout.findViewById(R.id.name);
                                    name.setTag(1);
                                    try {
                                        name.setText(childsnapshot.child("name").getValue().toString());
                                    } catch (NullPointerException e) {
                                    }

                                    TextView year =  childLayout.findViewById(R.id.year_from);
                                    year.setTag(2);
                                    try {
                                        year.setText(childsnapshot.child("year_from").getValue().toString());
                                    } catch (NullPointerException e) {
                                    }


                                    TextView year_to =  childLayout.findViewById(R.id.year_to);
                                    year_to.setTag(2);
                                    try {
                                        year_to.setText(childsnapshot.child("year_to").getValue().toString());
                                    } catch (NullPointerException e) {
                                    }

                                    TextView degree =  childLayout.findViewById(R.id.description);
                                    degree.setTag(1);
                                    try {
                                        degree.setText(childsnapshot.child("description").getValue().toString());
                                    } catch (NullPointerException e) {
                                    }

                                    linear_clg.setVisibility(View.GONE);
                                } else {
                                    linear_clg.setVisibility(View.VISIBLE);
                                }


                            }
                        }

                        if (dataSnapshot.child("coaching").exists()) {

                            profile_progress.incrementProgressBy(13);


                            for (DataSnapshot childsnapshot : dataSnapshot.child("coaching").getChildren()) {
                                if (childsnapshot.exists()) {
                                    View childLayout = inflater.inflate(R.layout.layout_display_autocomplete,
                                            display_coaching, false);

                                    display_coaching.addView(childLayout);

                                    ImageView coaching = childLayout.findViewById(R.id.card_image);
                                    try {

                                        coaching.setImageResource(R.drawable.coaching);

                                    } catch (NullPointerException e) {

                                    }

                                    TextView name =  childLayout.findViewById(R.id.name);
                                    try {
                                        name.setText(childsnapshot.child("name").getValue().toString());
                                    } catch (NullPointerException e) {
                                    }

                                    TextView year =  childLayout.findViewById(R.id.year_from);
                                    try {
                                        year.setText(childsnapshot.child("year_from").getValue().toString());
                                    } catch (NullPointerException e) {
                                    }

                                    TextView year_to =  childLayout.findViewById(R.id.year_to);
                                    try {
                                        year_to.setText(childsnapshot.child("year_to").getValue().toString());
                                    } catch (NullPointerException e) {
                                    }

                                    TextView degree = childLayout.findViewById(R.id.description);
                                    try {
                                        degree.setText(childsnapshot.child("description").getValue().toString());
                                    } catch (NullPointerException e) {
                                    }

                                    linear_cochng.setVisibility(View.GONE);
                                } else {
                                    linear_cochng.setVisibility(View.VISIBLE);
                                }


                            }
                        }

                        if (dataSnapshot.child("award").exists()) {


                            profile_progress.incrementProgressBy(13);



                            for (DataSnapshot childsnapshot : dataSnapshot.child("award").getChildren()) {
                                if (childsnapshot.exists()) {
                                    View childLayout = inflater.inflate(R.layout.layout_display,
                                            display_awards, false);

                                    display_awards.addView(childLayout);

                                    textView_dash =  childLayout.findViewById(R.id.textview_dash);
                                    textView_dash.setVisibility(View.GONE);


                                    ImageView awards =  childLayout.findViewById(R.id.card_image);
                                    try {

                                        awards.setImageResource(R.drawable.awards);

                                    } catch (NullPointerException e) {

                                    }

                                    TextView awardsname =  childLayout.findViewById(R.id.name);
                                    try {
                                        awardsname.setText(childsnapshot.child("name").getValue().toString());
                                    } catch (NullPointerException e) {
                                    }

                                    TextView awardsyear = childLayout.findViewById(R.id.year_from);
                                    try {
                                        awardsyear.setText(childsnapshot.child("year_from").getValue().toString());
                                    } catch (NullPointerException e) {
                                    }

                                    TextView awardsdescription = childLayout.findViewById(R.id.description);
                                    try {
                                        awardsdescription.setText(childsnapshot.child("description").getValue().toString());
                                    } catch (NullPointerException e) {
                                    }

                                    linear_awds.setVisibility(View.GONE);
                                } else {
                                    linear_awds.setVisibility(View.VISIBLE);
                                }


                            }

                        }

                        if (dataSnapshot.child("internship").exists()) {


                            profile_progress.incrementProgressBy(13);


                            for (DataSnapshot childsnapshot : dataSnapshot.child("internship").getChildren()) {
                                if (childsnapshot.exists()) {
                                    View childLayout = inflater.inflate(R.layout.layout_display,
                                            display_internships, false);

                                    display_internships.addView(childLayout);

                                    ImageView internships =  childLayout.findViewById(R.id.card_image);
                                    try {
                                        internships.setImageResource(R.drawable.internships);
                                    } catch (NullPointerException e) {

                                    }

                                    TextView awardsname = childLayout.findViewById(R.id.name);
                                    try {
                                        awardsname.setText(childsnapshot.child("name").getValue().toString());
                                    } catch (NullPointerException e) {
                                    }

                                    TextView awardsyear =  childLayout.findViewById(R.id.year_from);
                                    try {
                                        awardsyear.setText(childsnapshot.child("year_from").getValue().toString());
                                    } catch (NullPointerException e) {
                                    }


                                    TextView awardsyear_to = childLayout.findViewById(R.id.year_to);
                                    try {
                                        awardsyear_to.setText(childsnapshot.child("year_to").getValue().toString());
                                    } catch (NullPointerException e) {
                                    }


                                    TextView awardsdescription =  childLayout.findViewById(R.id.description);
                                    try {
                                        awardsdescription.setText(childsnapshot.child("description").getValue().toString());
                                    } catch (NullPointerException e) {
                                    }

                                    linear_intern.setVisibility(View.GONE);
                                } else {
                                    linear_intern.setVisibility(View.VISIBLE);
                                }


                            }
                        }


                        if (dataSnapshot.child("publication").exists()) {




                            for (DataSnapshot childsnapshot : dataSnapshot.child("publication").getChildren()) {
                                if (childsnapshot.exists()) {
                                    View childLayout = inflater.inflate(R.layout.layout_display,
                                            display_publication, false);

                                    display_publication.addView(childLayout);

                                    textView_dash =  childLayout.findViewById(R.id.textview_dash);
                                    textView_dash.setVisibility(View.GONE);

                                    ImageView publication =  childLayout.findViewById(R.id.card_image);
                                    try {

                                        publication.setImageResource(R.drawable.publication);

                                    } catch (NullPointerException e) {

                                    }

                                    TextView awardsname =  childLayout.findViewById(R.id.name);
                                    try {
                                        awardsname.setText(childsnapshot.child("name").getValue().toString());
                                    } catch (NullPointerException e) {
                                    }

                                    TextView awardsyear =  childLayout.findViewById(R.id.year_from);
                                    try {
                                        awardsyear.setText(childsnapshot.child("year_from").getValue().toString());
                                    } catch (NullPointerException e) {
                                    }

                                    TextView awardsdescription =  childLayout.findViewById(R.id.description);
                                    try {
                                        awardsdescription.setText(childsnapshot.child("description").getValue().toString());
                                    } catch (NullPointerException e) {
                                    }

                                    linear_publication.setVisibility(View.GONE);
                                } else {
                                    linear_publication.setVisibility(View.VISIBLE);
                                }


                            }
                        }


                        if (dataSnapshot.child("specialisation").exists()) {


                            for (DataSnapshot childsnapshot : dataSnapshot.child("specialisation").getChildren()) {
                                if (childsnapshot.exists()) {
                                    View childLayout = inflater.inflate(R.layout.layout_display,
                                            display_specialisation, false);

                                    display_specialisation.addView(childLayout);

                                    ImageView specialisation =  childLayout.findViewById(R.id.card_image);
                                    try {

                                        specialisation.setImageResource(R.drawable.specialisation);

                                    } catch (NullPointerException e) {

                                    }

                                    TextView awardsname = childLayout.findViewById(R.id.name);
                                    try {
                                        awardsname.setText(childsnapshot.child("name").getValue().toString());
                                    } catch (NullPointerException e) {
                                    }


                                    TextView awardsyear = childLayout.findViewById(R.id.year_from);
                                    try {
                                        awardsyear.setText(childsnapshot.child("year_from").getValue().toString());
                                    } catch (NullPointerException e) {
                                    }


                                    TextView awardsyear_to = childLayout.findViewById(R.id.year_to);
                                    try {
                                        awardsyear_to.setText(childsnapshot.child("year_to").getValue().toString());
                                    } catch (NullPointerException e) {
                                    }

                                    TextView awardsdescription =  childLayout.findViewById(R.id.description);
                                    try {
                                        awardsdescription.setText(childsnapshot.child("description").getValue().toString());
                                    } catch (NullPointerException e) {
                                    }

                                    linear_specialisation.setVisibility(View.GONE);
                                } else {
                                    linear_specialisation.setVisibility(View.VISIBLE);
                                }


                            }
                        }


                        if (dataSnapshot.child("certification").exists()) {

                            profile_progress.incrementProgressBy(13);


                            for (DataSnapshot childsnapshot : dataSnapshot.child("certification").getChildren()) {
                                if (childsnapshot.exists()) {
                                    View childLayout = inflater.inflate(R.layout.layout_display,
                                            display_certifications, false);

                                    display_certifications.addView(childLayout);

                                    textView_dash =  childLayout.findViewById(R.id.textview_dash);
                                    textView_dash.setVisibility(View.GONE);


                                    ImageView certifications =  childLayout.findViewById(R.id.card_image);
                                    try {

                                        certifications.setImageResource(R.drawable.certifications);

                                    } catch (NullPointerException e) {

                                    }


                                    TextView awardsname =  childLayout.findViewById(R.id.name);
                                    try {
                                        awardsname.setText(childsnapshot.child("name").getValue().toString());
                                    } catch (NullPointerException e) {
                                    }

                                    TextView awardsyear =  childLayout.findViewById(R.id.year_from);
                                    try {
                                        awardsyear.setText(childsnapshot.child("year_from").getValue().toString());
                                    } catch (NullPointerException e) {
                                    }

                                    TextView awardsdescription =  childLayout.findViewById(R.id.description);
                                    try {
                                        awardsdescription.setText(childsnapshot.child("description").getValue().toString());
                                    } catch (NullPointerException e) {
                                    }

                                    linear_cert.setVisibility(View.GONE);

                                } else {
                                    linear_cert.setVisibility(View.VISIBLE);
                                }


                            }
                        }
                    } catch (IllegalStateException e) {
                    }

                }

                if (profile_progress.getProgress() == 100) {
                    profile_progress.setProgress(100);

                }

                FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("Corporate_List_Counter").orderByChild("uid").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot childsnap : dataSnapshot.getChildren()){
                            if (childsnap.child("count_educators").exists()){
                                count_educators.setText(childsnap.child("count_educators").getValue().toString());
                            }
                            if (childsnap.child("count_learners").exists()){
                                count_educators.setText(childsnap.child("count_learners").getValue().toString());
                            }
                            if (childsnap.child("count_alumini").exists()){
                                count_educators.setText(childsnap.child("count_alumini").getValue().toString());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    private void getconnections() {

        if (show_connect) {
            DatabaseReference connectionsRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("Connection").child(user.getUid()).child(userid);

            connectionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Boolean isthere = false;

                    if (dataSnapshot.exists()) {
                        isthere = true;
                    }


                    if (!isthere) {
                        DatabaseReference inviteref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("Invitation").child(userid).child(user.getUid());
                        inviteref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    btn_connect.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }


    private Dialog dialogBuilder(String text, final int type, Boolean isNegative, String posText, String negText, final Object content) {

        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);


        if (type == 1) {
            dialog.setContentView(R.layout.layout_dialog_benefits);
            dialog.setCancelable(false);

        } else {
            dialog.setContentView(R.layout.layout_dialog);
            dialog.setCancelable(false);
        }


        TextView textView =  dialog.findViewById(R.id.dialogue_text);
        textView.setText(text);


        Button posButton =  dialog.findViewById(R.id.button_positive);
        posButton.setText(posText);
        posButton.setVisibility(View.VISIBLE);
        posButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (type == 1) {
                    dialog.dismiss();
                    contact.setVisibility(View.GONE);
                    birthday.setVisibility(View.GONE);
                    line_above_summary.setVisibility(View.VISIBLE);
                    share();

                } else if (type == 2) {
                    dialog.dismiss();
                    if (content instanceof SharePhotoContent) {
                        shareDialog.show((SharePhotoContent) content, ShareDialog.Mode.AUTOMATIC);
                    }
                } else if (type == 3) {
                    dialog.dismiss();

                } else if (type == 4) {
                    if (content instanceof ImageView) {
                        layout_current_text.removeView(layout_current_text.getChildAt(((ImageView) content).getId()));
                        edit_layout(layout_current_text);
                        btn_current_save.setVisibility(View.VISIBLE);
                        btn_current_save.setText("Save");
                        dialog.dismiss();
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
                    if (type == 3) {
                        dialog.dismiss();
                        contact.setVisibility(View.GONE);
                        birthday.setVisibility(View.GONE);
                        line_above_summary.setVisibility(View.VISIBLE);
                        share();

                    } else if (type == 4) {
                        dialog.dismiss();
                    }

                }
            });
        }

        return dialog;

    }


    public void shareProfileFacebook() {
        isFacebookShare = true;
        share();
    }


    private void share() {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading..."); // Setting Message
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        }).start();


        String uid = user.getUid();
        String link = "https://kriger.in/?invitedby=" + uid;
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link))
                .setDomainUriPrefix("https://kriger.page.link")
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder("in.kriger.krigercampus")
                                .setMinimumVersion(1)
                                .build()).
                /*.setIosParameters(
                        new DynamicLink.IosParameters.Builder("com.example.ios")
                                .setAppStoreId("123456789")
                                .setMinimumVersion("1.0.1")
                                .build())*/
                        buildShortDynamicLink()
                .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                    @Override
                    public void onSuccess(ShortDynamicLink shortDynamicLink) {
                        pushreferral(shortDynamicLink.getShortLink().toString());
                    }
                });


    }

    public void profileshare() {


        KrigerConstants.userRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("college").exists()) {

                    contact.setVisibility(View.GONE);
                    birthday.setVisibility(View.GONE);
                    line_above_summary.setVisibility(View.VISIBLE);
                    share();

                } else {
                    dialogBuilder("Your educational details are unfilled.\n" + "Filled profiles get 50% more responses.\n\n" + "Let's fill your profile!", 3, true, "OK", "Proceed anyway", null).show();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    private void edit_layout(LinearLayout viewgroup) {

        LayoutInflater inflater = getLayoutInflater();

        layout_current.setVisibility(View.VISIBLE);

        btn_current_add.setVisibility(View.VISIBLE);

        btn_current_save.setText("Cancel");

        int id_count = 0;
        int image_count = 0;

        if (viewgroup.getChildCount() > 1) {


            ArrayList<View> arrayList = getAllChildren(viewgroup);

            for (int i = 0; i < arrayList.size(); i++) {

                View v = arrayList.get(i);

                if (v instanceof FlowLayout) {


                    ArrayList<View> al = getAllChildren(v);
                    for (int p = 0; p < al.size(); p++) {

                        View vf = al.get(p);



                        if (vf instanceof CustomTextViewBold) {


                            CustomTextViewBold textView = (CustomTextViewBold) vf;

                            textView.setVisibility(View.GONE);

                            View childLayout = inflater.inflate(R.layout.autocomplete_layout,
                                    llayout, false);

                            llayout.addView(childLayout);


                            AutoCompleteTextView autotext =  childLayout.findViewById(R.id.edittext_auto);
                            autotext.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
                            autotext.setVisibility(View.VISIBLE);
                            autotext.setText(textView.getText().toString());


                            ImageView imageView =  childLayout.findViewById(R.id.card_image_auto);
                            imageView.setImageResource(R.drawable.cancel_request);
                            imageView.setVisibility(View.VISIBLE);

                            imageView.setOnClickListener(this);

                        }

                    }
                    break;
                }

                if ((viewgroup == display_prepration_exam) || (viewgroup == display_prepration_sub)) {

                    if (v instanceof ImageView) {

                        ImageView imageView = (ImageView) v;
                        ((ImageView) v).setVisibility(View.GONE);
                    }

                }


                if (!(viewgroup == display_prepration_exam) && !(viewgroup == display_prepration_sub)) {

                    if (v instanceof ImageView) {
                        ImageView imageView = (ImageView) v;
                        ((ImageView) v).setImageResource(R.drawable.cancel_request);
                        imageView.setId(image_count);
                        image_count++;
                        imageView.setOnClickListener(this);
                    }

                    if (v instanceof TextView) {
                        TextView textView = (TextView) v;



                        textView.setVisibility(View.GONE);
                        int count = 0;
                        for (int j = i; j < arrayList.size(); j++) {

                            View view = arrayList.get(j);
                            if (count == 0) {

                                if (view instanceof EditText) {
                                    count = 1;
                                    EditText editText = (EditText) view;
                                    editText.setId(id_count);

                                    if (id_count != 0) {
                                        if ((id_count % 9 == 0) || (((id_count % 6) == 0) && (((id_count / 6) - 1) % 3 == 0)) || (((id_count - 3) % 6 == 0) && ((((id_count - 3) / 6) + 1) % 3 == 0))) {
                                            editText.setFocusable(false);
                                            editText.setOnClickListener(this);
                                        }
                                    }

                                    editText.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
                                    editText.setVisibility(View.VISIBLE);
                                    editText.setText(textView.getText());


                                    if (viewgroup == display_teaching || viewgroup == display_college || viewgroup == display_coaching) {
                                        if ((id_count-1)%9 ==0 || ((id_count-2)%9)==0) {
                                            ((AutoCompleteTextView)view).setThreshold(0);
                                            ((AutoCompleteTextView)view).setAdapter(listAdapter);
                                            ((AutoCompleteTextView)view).addTextChangedListener(new TextWatcher() {
                                                @Override
                                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                                }

                                                @Override
                                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                                    startSearch(charSequence.toString());
                                                }

                                                @Override
                                                public void afterTextChanged(Editable editable) {

                                                }
                                            });

                                        }
                                    }


                                    if (((viewgroup == display_awards) || (viewgroup == display_publication) || viewgroup == display_certifications) && id_count % 9 == 0) {
                                        editText.setText("#");
                                        editText.setVisibility(View.GONE);
                                    }

                                    id_count++;
                                }
                            }
                        }


                    }


                }

            }

            attachWatcher(viewgroup);

        }
    }

    private void attachWatcher(LinearLayout viewgroup) {
        ArrayList<View> views = getAllChildren(viewgroup);
        for (int i = 0; i < views.size(); i++) {
            if (views.get(i) instanceof EditText) {
                EditText editText = (EditText) views.get(i);

                editText.addTextChangedListener(textWatcher);
            }
        }

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


    private TextWatcher textWatcher = new TextWatcher() {


        public void afterTextChanged(Editable s) {

            checkallEdittext();
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }
    };


    private void checkallEdittext() {
        ArrayList<View> views = getAllChildren(layout_current_text);

        Boolean isText = true;

        for (int i = 0; i < views.size(); i++) {
            View view = views.get(i);

            if (view instanceof EditText) {
                EditText editText = (EditText) view;


                if (editText.getVisibility() == View.VISIBLE) {
                    if (editText.getText().toString().length() == 0) {
                        isText = false;
                    }
                }


            }
        }

        if (isText) {
            try {
                btn_current_add.setVisibility(View.VISIBLE);
                btn_current_save.setText("Save");
            } catch (NullPointerException e) {
            }
        } else {
            try {
                btn_current_add.setVisibility(View.GONE);
            } catch (NullPointerException e) {
            }
        }


    }


    private void cancelAll(LinearLayout viewgroup) {

        Boolean is_Cancel = false;


        if (viewgroup.getChildCount() > 1) {


            ArrayList<View> arrayList = getAllChildren(viewgroup);

            for (int i = 0; i < arrayList.size(); i++) {

                View v = arrayList.get(i);

                if (v instanceof TextView) {

                    TextView textView = (TextView) v;

                    if (!(textView.getText().toString().equals(" - "))) {

                        textView.setVisibility(View.VISIBLE);
                        int count = 0;
                        for (int j = i; j < arrayList.size(); j++) {

                            View view = arrayList.get(j);
                            if (count == 0) {

                                if (view instanceof EditText) {
                                    count = 1;
                                    EditText editText = (EditText) view;


                                    if (editText.getText().toString().length() == 0 && !(editText.getText().toString().equals("#"))) {
                                        is_Cancel = true;
                                    }


                                    editText.setVisibility(View.GONE);
                                    editText.removeTextChangedListener(textWatcher);
                                    if (!editText.getText().toString().equals("#")) {
                                        textView.setText(editText.getText().toString());
                                    }

                                }
                            }
                        }

                    } else {
                        if (!(((viewgroup == display_awards) || (viewgroup == display_publication) || viewgroup == display_certifications) && textView.getText().toString().equals(" - "))) {
                            textView.setVisibility(View.VISIBLE);
                        }

                    }

                }
                if (v instanceof ImageView) {

                    if (viewgroup == display_prepration_exam) {
                        ((ImageView) v).setImageResource(R.drawable.prepration_exam);
                    } else if (viewgroup == display_prepration_sub) {
                        ((ImageView) v).setImageResource(R.drawable.prepration_sub);
                    } else if (viewgroup == display_teaching) {
                        ((ImageView) v).setImageResource(R.drawable.teaching);
                    } else if (viewgroup == display_college) {
                        ((ImageView) v).setImageResource(R.drawable.college);
                    } else if (viewgroup == display_awards) {
                        ((ImageView) v).setImageResource(R.drawable.awards);
                    } else if (viewgroup == display_certifications) {
                        ((ImageView) v).setImageResource(R.drawable.certifications);
                    } else if (viewgroup == display_coaching) {
                        ((ImageView) v).setImageResource(R.drawable.coaching);
                    } else if (viewgroup == display_internships) {
                        ((ImageView) v).setImageResource(R.drawable.internships);
                    } else if (viewgroup == display_publication) {
                        ((ImageView) v).setImageResource(R.drawable.publication);
                    } else if (viewgroup == display_specialisation) {
                        ((ImageView) v).setImageResource(R.drawable.specialisation);
                    }
                }

            }

            if (is_Cancel) {
                removeViews(viewgroup);
            }


        } else {

            display_default(viewgroup);


        }
    }

    private void removeViews(LinearLayout viewgroup) {

        viewgroup.removeView(viewgroup.getChildAt(viewgroup.getChildCount() - 1));
        if (!(viewgroup.getChildCount() > 0)) {
            display_default(viewgroup);
        }

    }

    private void addLayout(LinearLayout viewGroup) {
        View childLayout = null;
        if (viewGroup == display_teaching || viewGroup == display_coaching || viewGroup == display_college){

            childLayout = getLayoutInflater().inflate(R.layout.layout_display_autocomplete,
                    viewGroup, false);

        }else{
            childLayout = getLayoutInflater().inflate(R.layout.layout_display,
                    viewGroup, false);


        }
        viewGroup.addView(childLayout);

        int count = 0;

        ArrayList<View> views = getAllChildren(childLayout);
        for (int i = 0; i < views.size(); i++) {
            View view = views.get(i);

            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                textView.setVisibility(View.GONE);
            }
            if (view instanceof EditText) {
                final EditText editText = (EditText) view;
                editText.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
                if (viewGroup == display_teaching) {
                    editText.setHint(teaching_hint[count]);
                    if (count ==0 ) {
                        setAutoComplete(view);
                    }
                } else if (viewGroup == display_college) {
                    editText.setHint(college_hint[count]);
                    if (count ==0 ) {
                        setAutoComplete(view);
                    }
                } else if (viewGroup == display_awards) {
                    editText.setHint(awards_hint[count]);
                } else if (viewGroup == display_certifications) {
                    editText.setHint(certification_hint[count]);
                } else if (viewGroup == display_coaching) {
                    editText.setHint(coaching_hint[count]);
                    if (count ==0 ) {
                        setAutoComplete(view);
                    }
                } else if (viewGroup == display_internships) {
                    editText.setHint(internships_hint[count]);
                } else if (viewGroup == display_publication) {
                    editText.setHint(publication_hint[count]);
                } else if (viewGroup == display_specialisation) {
                    editText.setHint(specialisation_hint[count]);
                }

                if (count == 2 || count == 3) {

                    editText.setOnClickListener(this);
                    editText.setFocusable(false);

                }


                editText.addTextChangedListener(textWatcher);
                editText.setVisibility(View.VISIBLE);

                if ((viewGroup == display_awards || viewGroup == display_publication || viewGroup == display_certifications) && (count == 3)) {
                    editText.setText("#");
                    editText.setVisibility(View.GONE);
                }

                count++;
            }
            if (view instanceof ImageView) {

                ((ImageView) view).setImageResource(R.drawable.cancel_request);

                ((ImageView) view).setOnClickListener(this);
            }
        }

        btn_current_add.setVisibility(View.GONE);
        btn_current_save.setText("Cancel");


    }

    private void setAutoComplete(View view){


        ((AutoCompleteTextView)view).setThreshold(0);
        ((AutoCompleteTextView)view).setAdapter(listAdapter);
        ((AutoCompleteTextView)view).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                startSearch(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }



    private void addPreprationLayout(final LinearLayout viewGroup) {
        View childLayout = getLayoutInflater().inflate(R.layout.layout_prepration_display,
                viewGroup, false);

        viewGroup.addView(childLayout);


        ArrayList<View> views = getAllChildren(childLayout);
        for (int i = 0; i < views.size(); i++) {
            View view = views.get(i);

            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                textView.setVisibility(View.GONE);
            }
            if (view instanceof AutoCompleteTextView) {
                final AutoCompleteTextView autoText = (AutoCompleteTextView) view;
                autoText.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
                if (viewGroup == display_prepration_exam) {
                    autoText.setHint("Exams I am preparing for");
                } else if (viewGroup == display_prepration_sub) {
                    autoText.setHint("Subjects I study");
                }


                autoText.setVisibility(View.VISIBLE);


                autoText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {

                            if (viewGroup == display_prepration_exam) {
                                final ArrayAdapter<String> adapter_exams =
                                        new ArrayAdapter<String>(getContext(), R.layout.prepration_list_item, exams);
                                autoText.setAdapter(adapter_exams);

                                for (int i = 0; i < adapter_exams.getCount(); i++) {
                                    examlist.add(adapter_exams.getItem(i));

                                }


                            } else if (viewGroup == display_prepration_sub) {
                                ArrayAdapter<String> adapter_subjects =
                                        new ArrayAdapter<String>(getContext(), R.layout.prepration_list_item, subjects);
                                autoText.setAdapter(adapter_subjects);

                                for (int l = 0; l < adapter_subjects.getCount(); l++) {
                                    sublist.add(adapter_subjects.getItem(l));
                                }
                            }

                        }


                    }
                });


                autoText.addTextChangedListener(textWatcher);


            }
            if (view instanceof ImageView) {

                ((ImageView) view).setImageResource(R.drawable.cancel_request);
            }
        }

        btn_current_add.setVisibility(View.GONE);
        btn_current_save.setText("Cancel");


    }


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


    @Override
    public void onClick(final View v) {


        if (v instanceof EditText) {

            final EditText editText = (EditText) v;

            MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(getContext(),
                    new MonthPickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(int selectedMonth, int selectedYear) { // on date set
                            editText.setText(String.valueOf(selectedYear));
                        }
                    }, 2018, 11);

            builder.setActivatedMonth(Calendar.JULY)
                    .setMinYear(1900)
                    .setActivatedYear(2019)
                    .setMaxYear(2050)
                    .showYearOnly()
                    .build().show();

        }
        if (v instanceof ImageView) {

            final ImageView imageView = (ImageView) v;

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            // Setting Dialog Message
            alertDialog.setMessage("This entry would be removed. Are you sure you want to remove?");

            // On pressing Settings button
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {

                    layout_current_text.removeView(layout_current_text.getChildAt(imageView.getId()));
                    edit_layout(layout_current_text);
                    btn_current_save.setVisibility(View.VISIBLE);
                    btn_current_save.setText("Save");
                }
            });

            // on pressing cancel button
            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            // Showing Alert Message
            alertDialog.show();


        }
    }


    private void savealldetails(LinearLayout viewgroup) {
        ArrayList<HashMap<String, Object>> arrayList = new ArrayList<>();

        ArrayList<View> alleditText = getAllChildren(viewgroup);

        HashMap<String, Object> map = new HashMap<>();

        int count = 0;


        DatabaseReference edit_value = null;

        if (viewgroup == display_teaching) {
            edit_value = KrigerConstants.userRef.child(user.getUid()).child("teaching");
        } else if (viewgroup == display_college) {
            edit_value = KrigerConstants.userRef.child(user.getUid()).child("college");
        } else if (viewgroup == display_coaching) {
            edit_value = KrigerConstants.userRef.child(user.getUid()).child("coaching");
        } else if (viewgroup == display_awards) {
            edit_value = KrigerConstants.userRef.child(user.getUid()).child("award");
        } else if (viewgroup == display_internships) {
            edit_value = KrigerConstants.userRef.child(user.getUid()).child("internship");
        } else if (viewgroup == display_publication) {
            edit_value = KrigerConstants.userRef.child(user.getUid()).child("publication");
        } else if (viewgroup == display_specialisation) {
            edit_value = KrigerConstants.userRef.child(user.getUid()).child("specialisation");
        } else if (viewgroup == display_certifications) {
            edit_value = KrigerConstants.userRef.child(user.getUid()).child("certification");
        }
        try {
            edit_value.removeValue();
        } catch (NullPointerException e) {
        }

        for (int i = 0; i < alleditText.size(); i++) {

            if (alleditText.get(i) instanceof EditText) {

                EditText editText = (EditText) alleditText.get(i);

                if (count % 4 == 0) {
                    map.put("name", editText.getText().toString());
                    count++;
                } else if (count % 4 == 1) {
                    map.put("description", editText.getText().toString());
                    count++;
                } else if (count % 4 == 2) {
                    map.put("year_from", editText.getText().toString());
                    count++;
                } else if (count % 4 == 3) {
                    count++;
                    if (!(viewgroup == display_awards || viewgroup == display_publication || viewgroup == display_certifications)) {
                        map.put("year_to", editText.getText().toString());
                    }
                    arrayList.add(map);
                    edit_value.child(String.valueOf(arrayList.size() - 1)).setValue(map);
                    map.clear();
                }


            }

        }

        if (arrayList.size() == 0) {
            removeViews(viewgroup);
        }

        cancelAll(viewgroup);
    }


    private void saveallpreprationdetails(LinearLayout viewgroup) {

        ArrayList<HashMap<String, Object>> arrayList = new ArrayList<>();

        ArrayList<View> allautoText = getAllChildren(viewgroup);

        HashMap<String, Object> map = new HashMap<>();


        DatabaseReference edit_value = null;

        if (viewgroup == display_prepration_exam) {
            edit_value = KrigerConstants.userRef.child(user.getUid()).child("exam");
        } else if (viewgroup == display_prepration_sub) {
            edit_value = KrigerConstants.userRef.child(user.getUid()).child("subject");
        }
        try {
            edit_value.removeValue();
        } catch (NullPointerException e) {
        }


        for (int i = 0; i < allautoText.size(); i++) {

            if (allautoText.get(i) instanceof AutoCompleteTextView) {

                AutoCompleteTextView autoText = (AutoCompleteTextView) allautoText.get(i);

                if (viewgroup == display_prepration_exam) {

                    if (examlist.indexOf(autoText.getText().toString().toUpperCase()) == -1) {
                        autoText.setError("Invalid name.");

                        Toasty.custom(getContext(), "Not selected entry will be removed, Kindly add from the suggested list", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                true).show();

                    } else {
                        map.put("exam", String.valueOf(Arrays.asList(exams).indexOf(autoText.getText().toString().toUpperCase())));
                    }


                } else if (viewgroup == display_prepration_sub) {

                    if (sublist.indexOf(autoText.getText().toString().toUpperCase()) == -1) {
                        autoText.setError("Invalid name.");
                        Toasty.custom(getContext(), "Not selected entry will be removed, Kindly add from the suggested list", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                true).show();

                    } else {
                        map.put("subject", String.valueOf(Arrays.asList(subjects).indexOf(autoText.getText().toString().toUpperCase())));

                    }

                }
                arrayList.add(map);
                edit_value.child(String.valueOf(arrayList.size() - 1)).setValue(map);
                map.clear();



            }

        }


        if (arrayList.size() == 0) {
            removeViews(viewgroup);
        }


        cancelAll(viewgroup);


    }


    private void display_default(LinearLayout viewgroup) {

        if (viewgroup == display_prepration_exam) {
            linear_prepration_exam.setVisibility(View.VISIBLE);
        } else if (viewgroup == display_prepration_sub) {
            linear_prepration_sub.setVisibility(View.VISIBLE);
        } else if (viewgroup == display_teaching) {
            linear_teaching.setVisibility(View.VISIBLE);
        } else if (viewgroup == display_college) {
            linear_clg.setVisibility(View.VISIBLE);
        } else if (viewgroup == display_awards) {
            linear_awds.setVisibility(View.VISIBLE);
        } else if (viewgroup == display_certifications) {
            linear_cert.setVisibility(View.VISIBLE);
        } else if (viewgroup == display_coaching) {
            linear_cochng.setVisibility(View.VISIBLE);
        } else if (viewgroup == display_internships) {
            linear_intern.setVisibility(View.VISIBLE);
        } else if (viewgroup == display_publication) {
            linear_publication.setVisibility(View.VISIBLE);
        } else if (viewgroup == display_specialisation) {
            linear_specialisation.setVisibility(View.VISIBLE);
        }

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


    public void updateprofile(Uri uri, Uri squareUri) throws IOException {


        final Dialog progress = new Dialog(getContext());
        progress.setContentView(R.layout.dialog_progress);
        progress.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        progress.show();

        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
        final String format = s.format(new Date());


        final StorageReference squareRef = FirebaseStorage.getInstance().getReference().child("Your Photo").child("new_small" + user.getUid());
        final StorageReference childref = FirebaseStorage.getInstance().getReference().child("Your Photo").child("new_" + user.getUid());


        UploadTask uploadTask1 = squareRef.putFile(squareUri);

        final UploadTask uploadTask2 = childref.putFile(uri);

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
                                final HashMap<String, Object> map1 = new HashMap<>();
                                map1.put("thumb", squareUri.toString());
                                map1.put("original", downloadUri.toString());
                                KrigerConstants.user_detailRef.child(user.getUid()).updateChildren(map1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        prefManager.clearProfileImageUrl();
                                        progress.dismiss();
                                        Toasty.custom(getContext(), "Profile picture updated", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                                true).show();


                                    }
                                });

                            } else {
                                progress.dismiss();
                                Toasty.custom(getContext(), "Upload failed2", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                        true).show();


                                // Handle failures
                                // ...
                            }
                        }
                    });


                } else {
                    progress.dismiss();
                    Toasty.custom(getContext(), "Upload failed1", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();


                    // Handle failures
                    // ...
                }
            }
        });


    }


    public void setUri(Uri uri) {

        try {
            File file = new File(uri.getPath());
            File compressedFile = new Compressor(getContext()).compressToFile(file);
            compressedUri = Uri.fromFile(compressedFile);

            Bitmap compressedBitmap = BitmapFactory.decodeFile(compressedFile.getAbsolutePath());
            profile_image.setImageBitmap(compressedBitmap);

            squareFile = new Compressor(getContext()).setMaxHeight(50).setMaxWidth(50).setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES).getAbsolutePath()).compressToFile(file);


        } catch (IOException exp) {

        }
        try {
            updateprofile(compressedUri, Uri.fromFile(squareFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

                                listAdapter.add(childsnapshot.child("_source").child("name").getValue().toString());
                                valueList.add((((Long) childsnapshot.child("_source").child("value").getValue()).intValue()));

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
