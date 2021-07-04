package in.kriger.newkrigercampus.activities;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.SlidingTabLayout;
import in.kriger.newkrigercampus.adapters.SearchAdapter;
import in.kriger.newkrigercampus.searchfragments.GroupSearch;
import in.kriger.newkrigercampus.searchfragments.PostSearch;
import in.kriger.newkrigercampus.searchfragments.UserSearch;

public class Hashtag_SearchActivity extends AppCompatActivity {

    SearchAdapter adapterViewPager;

    String search_text;

    EditText text_search;

    ImageView btn_textsearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hashtag_search);

        search_text = getIntent().getExtras().getString("search_text");

        text_search = findViewById(R.id.text_search);
        text_search.setText(search_text);
        text_search.setSelection(text_search.getText().length());


        final ViewPager vpPager =  findViewById(R.id.vpPager);

        adapterViewPager = new SearchAdapter(getSupportFragmentManager(),search_text);


        vpPager.setAdapter(adapterViewPager);


        SlidingTabLayout tabLayout =  findViewById(R.id.sliding_tabs);

        tabLayout.setDistributeEvenly(true);
        tabLayout.setViewPager(vpPager);


        try{
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }catch (NullPointerException e){

        }


        //Button to Start Search
        btn_textsearch = findViewById(R.id.btn_text_search);
        btn_textsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (vpPager.getCurrentItem() == 0) {
                    PostSearch page = (PostSearch) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.vpPager + ":" + vpPager.getCurrentItem());
                    page.startSearch(text_search.getText().toString());
                }else if (vpPager.getCurrentItem() == 1) {
                    UserSearch page = (UserSearch) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.vpPager + ":" + vpPager.getCurrentItem());
                    page.startSearch(text_search.getText().toString());

                }else {
                    GroupSearch page = (GroupSearch) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.vpPager + ":" + vpPager.getCurrentItem());
                    page.startSearch(text_search.getText().toString());
                }

            }
        });

        //Enter Button on Soft Keyboard
        text_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (vpPager.getCurrentItem() == 0) {
                        PostSearch page = (PostSearch) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.vpPager + ":" + vpPager.getCurrentItem());
                        page.startSearch(text_search.getText().toString());
                    }else if (vpPager.getCurrentItem() == 1) {
                        UserSearch page = (UserSearch) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.vpPager + ":" + vpPager.getCurrentItem());
                        page.startSearch(text_search.getText().toString());
                    }else {
                        GroupSearch page = (GroupSearch) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.vpPager + ":" + vpPager.getCurrentItem());
                        page.startSearch(text_search.getText().toString());
                    }
                    return true;
                }
                return false;
            }
        });




        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


                if (position == 0) {
                    PostSearch page = (PostSearch) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.vpPager + ":" + vpPager.getCurrentItem());
                    page.startSearch(text_search.getText().toString());

                }else if (position == 1) {
                    UserSearch page = (UserSearch) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.vpPager + ":" + vpPager.getCurrentItem());
                    page.startSearch(text_search.getText().toString());

                }else {
                    GroupSearch page = (GroupSearch) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.vpPager + ":" + vpPager.getCurrentItem());
                    page.startSearch(text_search.getText().toString());

                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });




    }



}
