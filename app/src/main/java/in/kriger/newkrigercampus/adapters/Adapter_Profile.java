package in.kriger.newkrigercampus.adapters;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import in.kriger.newkrigercampus.profilefragments.About;
import in.kriger.newkrigercampus.profilefragments.CorporateAbout;
import in.kriger.newkrigercampus.profilefragments.EducatorAbout;
import in.kriger.newkrigercampus.profilefragments.Groups;
import in.kriger.newkrigercampus.profilefragments.Posts;

public class Adapter_Profile extends FragmentStatePagerAdapter {

    String userid;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Context context;
    int account_type;
    Boolean is_share = false;

    public Adapter_Profile(FragmentManager fragmentManager, String userid, Context context, int account_type,Boolean is_share) {
        super(fragmentManager);
        this.userid = userid;
        this.context = context;
        this.account_type = account_type;
        this.is_share = is_share;

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("user_id", userid);
        if (is_share){
            bundle.putString("is_profileShare","true");
        }

        switch (position) {

            case 0:
                if (account_type == 1) {
                    EducatorAbout educatorAbout = new EducatorAbout(context);
                    educatorAbout.setArguments(bundle);
                    return educatorAbout;
                } else if( account_type == 0){
                    About about = new About(context);
                    about.setArguments(bundle);
                    return about;
                }else if (account_type == 2){
                    CorporateAbout corporateAbout = new CorporateAbout(context);
                    corporateAbout.setArguments(bundle);
                    return corporateAbout;

                }
            case 1:
                Posts posts = new Posts();
                posts.setArguments(bundle);
                return posts;
            case 2:
                if (!user.getUid().equals(userid)) {
                    Groups groups = new Groups(context);
                    groups.setArguments(bundle);
                    return groups;
                }


            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        if (user.getUid().equals(userid)) {

            return 2;

        } else {
            return 3;

        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (user.getUid().equals(userid)) {

            switch (position) {
                case 0:
                    return "About";
                case 1:
                    return "Posts";
                default:
                    return null;
            }

        } else {

            switch (position) {
                case 0:
                    return "About";
                case 1:
                    return "Posts";
                case 2:
                    return "Groups";
                default:
                    return null;
            }
        }


    }
}
