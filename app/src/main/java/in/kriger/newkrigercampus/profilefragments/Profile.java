package in.kriger.newkrigercampus.profilefragments;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.SlidingTabLayout;
import in.kriger.newkrigercampus.adapters.Adapter_Profile;
import in.kriger.newkrigercampus.extras.PrefManager;
import in.kriger.newkrigercampus.services.KrigerConstants;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {

    Adapter_Profile adapter_profile;
    String userid;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    int account_type = 0;
    ImageView btn_settings;
    Context context;

    ViewPager vpPager;
    PrefManager prefManager;
    SlidingTabLayout tabLayout;
    Boolean is_share = false;

    public Profile() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);


        prefManager = new PrefManager(getContext());
        try{
            Log.d("jksgsjl",getArguments().getString("is_profileShare"));
           // is_share = true;
        }catch (NullPointerException e){

        }
        try {
            Log.d("User", getArguments().get("user_id").toString());
            userid = getArguments().get("user_id").toString();
            if (!userid.equals(user.getUid())) {

                getActivity().setTitle("Profile");
                KrigerConstants.user_detailRef.child(userid).child("type").child("0").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (Integer.valueOf(dataSnapshot.getValue().toString()) > 39) {
                                account_type = 2;
                                callInit(view);

                            } else if (Integer.valueOf(dataSnapshot.getValue().toString()) > 19){
                                account_type = 1;
                                callInit(view);
                            }else {
                                callInit(view);
                            }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                getActivity().setTitle("My Profile");
                if (prefManager.getAccountType() == 1) {
                    account_type = 1;
                }if (prefManager.getAccountType() == 2){
                    account_type = 2;
                }
                callInit(view);
            }
        } catch (NullPointerException e) {
            userid = user.getUid();
            getActivity().setTitle("My Profile");
            if (prefManager.getAccountType() == 1) {
                account_type = 1;
            }if (prefManager.getAccountType() == 2){
                account_type = 2;
            }
            callInit(view);

        }


        return view;
    }

    private void callInit(View view) {

        vpPager =  view.findViewById(R.id.vpPager);


        adapter_profile = new Adapter_Profile(getFragmentManager(), userid, getActivity(), account_type,is_share);
        tabLayout =  view.findViewById(R.id.sliding_tabs);

        tabLayout.setDistributeEvenly(true);
        vpPager.setAdapter(adapter_profile);
        vpPager.setOffscreenPageLimit(0);
        tabLayout.setViewPager(vpPager);

    }

    public void passUri(Uri uri) {

        if (vpPager.getCurrentItem() == 0) {
            if (account_type == 1) {

                EducatorAbout educatorAbout = (EducatorAbout) vpPager
                        .getAdapter()
                        .instantiateItem(vpPager, vpPager.getCurrentItem());
                educatorAbout.setUri(uri);
            } else if (account_type == 2){
                CorporateAbout corporateAbout = (CorporateAbout) vpPager
                        .getAdapter()
                        .instantiateItem(vpPager, vpPager.getCurrentItem());
                corporateAbout.setUri(uri);
            }else{
                About about = (About) vpPager.getAdapter().instantiateItem(vpPager, vpPager.getCurrentItem());
                about.setUri(uri);
            }
        }

    }


}