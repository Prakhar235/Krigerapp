package in.kriger.newkrigercampus.adapters;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import in.kriger.newkrigercampus.extras.PrefManager;
import in.kriger.newkrigercampus.resourcefragments.MyEnquires;
import in.kriger.newkrigercampus.resourcefragments.MyResources;
import in.kriger.newkrigercampus.resourcefragments.Resources;

public class Adapter_MarketPlace extends FragmentStatePagerAdapter {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Context context;
        PrefManager prefManager;
        int account_type = 0;

    public Adapter_MarketPlace(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        this.context = context;
        prefManager = new PrefManager(context);
        account_type = prefManager.getAccountType();

    }
        @NonNull
        @Override
        public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        bundle.putString("user_id", user.getUid());

        switch (position) {

            case 0:
                Resources resourcesfrag = new Resources(context);
                return  resourcesfrag;

            case 1:
                if (account_type > 0) {
                    MyResources myResources = new MyResources(context);
                    myResources.setArguments(bundle);
                    return myResources;
                }else{
                    MyEnquires myEnquires = new MyEnquires();
                    return myEnquires;
                }


            case 2:
                MyEnquires myEnquires = new MyEnquires();
                return myEnquires;

            default:
                return null;
        }
    }

        @Override
        public int getCount() {
            if (account_type > 0){
                return 3;
            }else{
                return 2;
            }
    }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0 :
                    return "MarketPlace";
                case 1 :
                    if (account_type > 0) {
                        return "My Resources";
                    }else{
                        return "My Enquiries";
                    }
                case 2 :
                    return "My Enquiries";

                default:
                    return null;
            }

    }
    }
