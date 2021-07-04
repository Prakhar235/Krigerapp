package in.kriger.newkrigercampus.resourcefragments;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Query;
import com.nex3z.flowlayout.FlowLayout;

import java.util.Arrays;

import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.classes.Resource;
import in.kriger.newkrigercampus.extras.CustomTypeFaceSpan;
import in.kriger.newkrigercampus.extras.PrefManager;
import in.kriger.newkrigercampus.services.KrigerConstants;

/**
 * A simple {@link Fragment} subclass.
 */
public class Resources extends Fragment {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Context context;
    RecyclerView recyclerView;

    TextView no_resouces;

    private FirebaseRecyclerAdapter<Resource, resourceViewHolder> adapter_resource;

    ProgressDialog progressDialog;

    Query resourceRef;


    int resource_value = 0;
    Button btn_sort, btn_filter;

    LinearLayoutManager mLayoutManager;
    int sort_value = 0;
    Spinner type, spinner_class, fees_type;
    AutoCompleteTextView auto_sub, auto_exams;
    String[] classlist;
    EditText fees;
    String[] exams, subs,fees_typelist;

    Long count_review = Long.valueOf(0);

    private PrefManager preferenceManager;


    public Resources(Context context) {
        this.context = context;
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_resources, container, false);

        preferenceManager = new PrefManager(context);
        exams = getResources().getStringArray(R.array.list_exams);
        subs = getResources().getStringArray(R.array.list_sub);
        fees_typelist = getResources().getStringArray(R.array.list_fees_type);
        //Filter resources
        btn_filter = view.findViewById(R.id.btn_filter);
        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final Dialog popupView = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                popupView.setCancelable(true);
                popupView.setContentView(R.layout.layout_filter);
                popupView.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


                type = (Spinner) popupView.findViewById(R.id.resource_type);
                        String[] qualification = getResources().getStringArray(R.array.list_resource_type);
                        ArrayAdapter<String> adapter_edu =
                                new ArrayAdapter<String>(context, R.layout.prepration_list_item, qualification);
                        type.setAdapter(adapter_edu);
                        

                    auto_sub = popupView.findViewById(R.id.sub);
                    ArrayAdapter<String> adapter_subjects =
                            new ArrayAdapter<String>(context, R.layout.prepration_list_item, subs);
                    auto_sub.setAdapter(adapter_subjects);
                    auto_sub.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String selected = (String) parent.getItemAtPosition(position);
                            position = Arrays.asList(subs).indexOf(selected);
                            //popupWindow.dismiss();
                            popupView.dismiss();
                            resourceRef = KrigerConstants.resourceRef.orderByChild("subject").equalTo(position);
                            mLayoutManager.setReverseLayout(true);
                            mLayoutManager.setStackFromEnd(true);
                            setAdapter();
                        }
                    });


                    auto_exams = popupView.findViewById(R.id.exams);
                    ArrayAdapter<String> adapter_exams =
                            new ArrayAdapter<String>(context, R.layout.prepration_list_item, exams);
                    auto_exams.setAdapter(adapter_exams);
                    auto_exams.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String selected = (String) parent.getItemAtPosition(position);
                            position = Arrays.asList(exams).indexOf(selected);

                            popupView.dismiss();
                            resourceRef = KrigerConstants.resourceRef.orderByChild("exam").equalTo(position);
                            mLayoutManager.setReverseLayout(true);
                            mLayoutManager.setStackFromEnd(true);
                            setAdapter();
                        }
                    });


                    spinner_class = popupView.findViewById(R.id.spinner_class);
                    classlist = getResources().getStringArray(R.array.list_edu);
                    ArrayAdapter<String> adapter_class =
                            new ArrayAdapter<String>(context, R.layout.prepration_list_item, classlist);
                    spinner_class.setAdapter(adapter_class);
                    spinner_class.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position != 0) {

                                popupView.dismiss();
                                resourceRef = KrigerConstants.resourceRef.orderByChild("class").equalTo(position);
                                mLayoutManager.setReverseLayout(true);
                                mLayoutManager.setStackFromEnd(true);
                                setAdapter();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    fees = popupView.findViewById(R.id.fees);
                    fees.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (s.length() > 0){
                                fees_type.setVisibility(View.VISIBLE);
                            }else{
                                fees_type.setVisibility(View.GONE);
                            }
                        }
                    });
                    fees_type = popupView.findViewById(R.id.fees_type);
                    ArrayAdapter<String> adapter_feestype =
                            new ArrayAdapter<String>(context, R.layout.prepration_list_item, getResources().getStringArray(R.array.list_fees_category));
                    fees_type.setAdapter(adapter_feestype);
                    fees_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position != 0) {
                                if (fees.getText().length() > 0) {
                                    popupView.dismiss();
                                    if (position == 1) {
                                        resourceRef = KrigerConstants.resourceRef.orderByChild("fees").startAt(Double.valueOf(fees.getText().toString()));
                                        mLayoutManager.setReverseLayout(true);
                                        mLayoutManager.setStackFromEnd(true);
                                        setAdapter();
                                    } else if (position == 2) {
                                        resourceRef = KrigerConstants.resourceRef.orderByChild("fees").endAt(Double.valueOf(fees.getText().toString()));
                                        mLayoutManager.setReverseLayout(true);
                                        mLayoutManager.setStackFromEnd(true);
                                        setAdapter();
                                    } else if (position == 3) {
                                        resourceRef = KrigerConstants.resourceRef.orderByChild("fees").equalTo(Double.valueOf(fees.getText().toString()));
                                        mLayoutManager.setReverseLayout(true);
                                        mLayoutManager.setStackFromEnd(true);
                                        setAdapter();

                                    }
                                } else {
                                    Toasty.custom(context,"Cannnot accept empty value in fee amount", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                            true).show();

                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position != 0) {

                                popupView.dismiss();
                                resourceRef = KrigerConstants.resourceRef.orderByChild("type").equalTo(position);
                                mLayoutManager.setReverseLayout(true);
                                mLayoutManager.setStackFromEnd(true);
                                setAdapter();
                            }


                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    popupView.show();





            }
        });

        //sort resources
        btn_sort = view.findViewById(R.id.btn_sort);
        btn_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, btn_sort);
                final Menu menu = popup.getMenu();
                popup.getMenuInflater().inflate(R.menu.menu_sort, menu);

                for (int i = 0; i < menu.size(); i++) {
                    MenuItem mi = menu.getItem(i);

                    applyFontToMenuItem(mi);
                }


                menu.getItem(sort_value).setChecked(true);


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.new_old:
                                resourceRef = KrigerConstants.resourceRef;
                                mLayoutManager.setReverseLayout(true);
                                mLayoutManager.setStackFromEnd(true);
                                setAdapter();
                                menu.getItem(sort_value).setChecked(false);
                                sort_value = 0;
                                item.setChecked(true);
                                break;
                            case R.id.old_new:
                                resourceRef = KrigerConstants.resourceRef;
                                mLayoutManager.setReverseLayout(false);
                                mLayoutManager.setStackFromEnd(false);
                                setAdapter();
                                menu.getItem(sort_value).setChecked(false);
                                sort_value = 1;
                                item.setChecked(true);
                                break;
                            case R.id.high_low:
                                resourceRef = KrigerConstants.resourceRef.orderByChild("rate");
                                mLayoutManager.setReverseLayout(true);
                                mLayoutManager.setStackFromEnd(true);
                                setAdapter();
                                menu.getItem(sort_value).setChecked(false);
                                sort_value = 2;
                                item.setChecked(true);
                                break;
                            case R.id.low_high:

                                resourceRef = KrigerConstants.resourceRef.orderByChild("rate");
                                mLayoutManager.setReverseLayout(false);
                                mLayoutManager.setStackFromEnd(false);
                                setAdapter();
                                menu.getItem(sort_value).setChecked(false);
                                sort_value = 3;
                                item.setChecked(true);
                                break;
                            case R.id.a_z:
                                resourceRef = KrigerConstants.resourceRef.orderByChild("name");
                                mLayoutManager.setReverseLayout(false);
                                mLayoutManager.setStackFromEnd(false);
                                setAdapter();
                                menu.getItem(sort_value).setChecked(false);
                                sort_value = 4;
                                item.setChecked(true);
                                break;
                            case R.id.z_a:
                                resourceRef = KrigerConstants.resourceRef.orderByChild("name");
                                mLayoutManager.setReverseLayout(true);
                                mLayoutManager.setStackFromEnd(true);
                                setAdapter();
                                menu.getItem(sort_value).setChecked(false);
                                sort_value = 5;
                                item.setChecked(true);
                                break;

                        }

                        return true;
                    }
                });

                popup.show();
            }
        });

        recyclerView =  view.findViewById(R.id.recycler_view);
        mLayoutManager =
                new LinearLayoutManager(getContext());

        no_resouces = view.findViewById(R.id.no_resources);


        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        resourceRef = KrigerConstants.resourceRef;

        setAdapter();


        return view;
    }
 public void  setAdapter()
    {

    }



    public static class resourceViewHolder extends RecyclerView.ViewHolder {

        private TextView name, rating, review_text,ownername;
        private TextView fee;
        private ImageView imageView_dp;
        private Button btn_enquire;
        private ImageButton popup, share;
        private FlowLayout flow_subs;
        private FlowLayout flow_exams;
        RatingBar rate;


        public resourceViewHolder(View itemView) {
            super(itemView);

            name =  itemView.findViewById(R.id.name);
            ownername =  itemView.findViewById(R.id.ownername);
            fee =  itemView.findViewById(R.id.fee);
            imageView_dp =  itemView.findViewById(R.id.profile_photo);
            btn_enquire =  itemView.findViewById(R.id.btn_enquire);
            share = itemView.findViewById(R.id.btn_share);
            popup = itemView.findViewById(R.id.imgbutton_popup);
            flow_exams = itemView.findViewById(R.id.flow_exams);
            flow_subs = itemView.findViewById(R.id.flow_sub);
            rating = itemView.findViewById(R.id.rating);
            rate = itemView.findViewById(R.id.rate);
            review_text = itemView.findViewById(R.id.review_text);


        }


    }



    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/roboto.light.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypeFaceSpan("", font, Color.BLACK), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }






}
