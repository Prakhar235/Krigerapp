package in.kriger.newkrigercampus.activities;

import android.app.Dialog;
import android.content.Intent;
import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.adapters.Adapter_Preview_Post;
import in.kriger.newkrigercampus.bottomfragments.Home;
import in.kriger.newkrigercampus.classes.PreviewPost;
import in.kriger.newkrigercampus.classes.UserDetails;

public class PreviewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    ImageView imageView_profile;
    EditText text_search;
    ImageView btn_text_search;
    public List<PreviewPost> previewpostList = new ArrayList<>();
    public Adapter_Preview_Post adapter_preview_post;
    BottomBar bottomBar;
    int tabID=0;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        //Title of Activity
        setTitle("Preview");


        recyclerView =  findViewById(R.id.recycler_view);
        bottomBar =  findViewById(R.id.bottomBar);

        final LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        int i = 0;
       //Read from posts.txt raw file
        InputStream databaseInputStream = getResources().openRawResource(R.raw.posts);

        BufferedReader r = new BufferedReader(new InputStreamReader(databaseInputStream));

        try {
            for (String line; (line = r.readLine()) != null; ) {
               //split from ::
                String[] separated = line.split("::");
                PreviewPost previewPost = new PreviewPost(separated[0], separated[1], separated[2], separated[3], separated[4], separated[5], separated[6]);
                try{

                     previewPost.setImage_url(separated[7]);
                }catch (ArrayIndexOutOfBoundsException e){

                }

                previewpostList.add(previewPost);


            }
        } catch (IOException e) {
        }


        adapter_preview_post = new Adapter_Preview_Post(PreviewActivity.this, previewpostList, PreviewActivity.this);

        recyclerView.setAdapter(adapter_preview_post);

        //On bottom tab selection
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {

                Fragment fragment = null;
                tabID = tabId;


                if (!(tabId == R.id.menu_home)){

                    dialogBuilder("Kindly register to explore all the feature and connect with 10000+ Indians to make India 100% literate.",2,true,"Yes","No",null).show();

                }else{
                    fragment = new Home();

                }



            }

        });

        imageView_profile =  findViewById(R.id.profile_photo);
        imageView_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder("Kindly register to explore all the feature and connect with 10000+ Indians to make India 100% literate.",2,true,"Yes","No",null).show();

            }
        });


        text_search =  findViewById(R.id.text_search);
        text_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    dialogBuilder("Kindly register to explore all the feature and connect with 10000+ Indians to make India 100% literate.",2,true,"Yes","No",null).show();

                    return true;
                }
                return false;
            }
        });


        btn_text_search = findViewById(R.id.btn_text_search);
        btn_text_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder("Kindly register to explore all the feature and connect with 10000+ Indians to make India 100% literate.",2,true,"Yes","No",null).show();

            }
        });





    }

    private Dialog dialogBuilder(String text, final int type, Boolean isNegative, String posText, String negText, ArrayList<UserDetails> arrayList){

        final Dialog dialog = new Dialog(PreviewActivity.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.layout_dialog);

        TextView textView =  dialog.findViewById(R.id.dialogue_text);
        textView.setText(text);

        if (!(type == 2)) {
            dialog.setCancelable(false);
        }

        Button posButton =  dialog.findViewById(R.id.button_positive);
        posButton.setText(posText);
        posButton.setVisibility(View.VISIBLE);
        posButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (type == 2){

                        Intent intent = new Intent(PreviewActivity.this, LoginActivity.class);
                        startActivity(intent);


                }

            }
        });

        if (isNegative){

            Button negButton =  dialog.findViewById(R.id.button_negative);
            negButton.setText(negText);
            negButton.setVisibility(View.VISIBLE);
            negButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type == 2){
                        dialog.dismiss();
                    }
                }
            });
        }




        return dialog;

    }




}
