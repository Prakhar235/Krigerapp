package in.kriger.newkrigercampus.bottomfragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.SlidingTabLayout;
import in.kriger.newkrigercampus.activities.ProfileListActivity;
import in.kriger.newkrigercampus.activities.SearchActivity;
import in.kriger.newkrigercampus.adapters.Adapter_MarketPlace;
import in.kriger.newkrigercampus.extras.PrefManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class MarketPlace extends Fragment {

    Adapter_MarketPlace adapter_marketPlace;
    EditText text_search;
    ImageView imageView_profile;
    ImageView btn_text_search;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    PrefManager prefManager;

    public MarketPlace() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_market_place, container, false);


        //Clicking Search in soft Keyboard
        text_search =  view.findViewById(R.id.text_search);
        text_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Intent intent = new Intent(getContext(), SearchActivity.class);
                    intent.putExtra("search_text", text_search.getText().toString());
                    startActivity(intent);

                    return true;
                }
                return false;
            }
        });

        prefManager = new PrefManager(getContext());

        //Click on Search Button
        btn_text_search =  view.findViewById(R.id.btn_text_search);
        btn_text_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                intent.putExtra("search_text", text_search.getText().toString());
                startActivity(intent);

            }
        });

        imageView_profile =  view.findViewById(R.id.profile_photo);
        imageView_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), ProfileListActivity.class);
                intent.putExtra("user_id", user.getUid());
                startActivity(intent);

            }
        });


        final ViewPager vpPager =  view.findViewById(R.id.vpPager);

        adapter_marketPlace = new Adapter_MarketPlace(getFragmentManager(),getContext());
        SlidingTabLayout tabLayout =  view.findViewById(R.id.sliding_tabs);

        tabLayout.setDistributeEvenly(true);
        vpPager.setAdapter(adapter_marketPlace);
        tabLayout.setViewPager(vpPager);
        RequestOptions requestOption = new RequestOptions()
                .placeholder(R.drawable.default_profile)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


        Glide.with(getContext()).load(prefManager.getProfileImageUrl())
                .apply(requestOption)
                .into(imageView_profile);

        try{
            if (getArguments().getString("tab").equals("my")){
                vpPager.setCurrentItem(1);
            }
        }catch (NullPointerException e){

        }



        return view;




    }

}
