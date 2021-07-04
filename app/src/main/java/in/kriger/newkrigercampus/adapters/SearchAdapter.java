package in.kriger.newkrigercampus.adapters;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import in.kriger.newkrigercampus.searchfragments.GroupSearch;
import in.kriger.newkrigercampus.searchfragments.HashtagSearch;
import in.kriger.newkrigercampus.searchfragments.PostSearch;
import in.kriger.newkrigercampus.searchfragments.UserSearch;

public class SearchAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 4;

    String search_text;

    public SearchAdapter(FragmentManager fragmentManager,String search_text) {
        super(fragmentManager);
        this.search_text = search_text;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public Fragment getItem(int position) {

            Bundle bundle = new Bundle();
            bundle.putString("search_text",search_text);


        switch (position) {

            case 0:
                PostSearch postSearch = new PostSearch();
                postSearch.setArguments(bundle);
                return postSearch;
            case 1:
                UserSearch userSearch = new UserSearch();
                userSearch.setArguments(bundle);
                return userSearch;
            case 2:
                GroupSearch groupSearch = new GroupSearch();
                groupSearch.setArguments(bundle);
                return groupSearch;
            case 3:
                HashtagSearch hashtagSearch = new HashtagSearch();
                hashtagSearch.setArguments(bundle);
                return hashtagSearch;

            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0 :
                return "Posts";
            case 1 :
                return "Krigers";
            case 2 :
                return "Groups";
            case 3 :
                return "Hashtags";


                default:
                    return null;
        }
    }

}