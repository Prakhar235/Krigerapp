package in.kriger.newkrigercampus.services;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.activities.ProfileListActivity;

/**
 * Created by poojanrathod on 12/31/17.
 */

public class FireService {


    public static void showdetails(final Context context, final TextView name, final TextView headline, final ImageView image_dp, final TextView star, final String uid) {

        try {
            KrigerConstants.user_detailRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        name.setText(dataSnapshot.child("name").getValue().toString());
                    } catch (NullPointerException e) {
                    }

                    if (dataSnapshot.child("type").exists()) {

                        try {
                            for (DataSnapshot childsnapshot : dataSnapshot.child("type").getChildren()) {

                                if (Integer.valueOf(childsnapshot.getValue().toString()) > 19) {
                                    name.setTextColor(Color.parseColor("#1D85FE"));
                                }
                            }
                        } catch (NullPointerException e) {
                        }


                   }


                    try {
                        headline.setText(dataSnapshot.child("headline").getValue().toString());
                    } catch (NullPointerException e) {
                    }
                    try {
                        RequestOptions requestOption = new RequestOptions()
                                .placeholder(R.drawable.default_profile)
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

                        Glide.with(context).load(dataSnapshot.child("thumb").getValue().toString())
                                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                                //.transition(DrawableTransitionOptions.withCrossFade())
                                .apply(requestOption)
                                .into(image_dp);


                        //Picasso.with(context).load(dataSnapshot.child("thumb").getValue().toString()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_profile).into(image_dp);
                    } catch (NullPointerException | IllegalArgumentException e) {
                        image_dp.setImageResource(R.drawable.default_profile);
                    }

                    if (star != null) {
                        star.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/impact.ttf"));
                        if (dataSnapshot.child("type").exists()) {

                            try {
                                for (DataSnapshot childsnapshot : dataSnapshot.child("type").getChildren()) {

                                    if (Integer.valueOf(childsnapshot.getValue().toString()) > 19) {
                                        star.setVisibility(View.VISIBLE);
                                        star.setText("EDUCATOR");
                                        star.setBackgroundResource(R.drawable.button_background_blue);
                                        star.setTextColor(Color.parseColor("#3994e4"));
                                    }
                                }
                            } catch (NullPointerException e) {
                            }

                        }

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            Intent intent = new Intent(context, ProfileListActivity.class);
                            intent.putExtra("user_id", uid);
                            context.startActivity(intent);



                    }
                });

                headline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(context, ProfileListActivity.class);
                        intent.putExtra("user_id",uid);
                        context.startActivity(intent);



                    }
                });

               image_dp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(context, ProfileListActivity.class);
                        intent.putExtra("user_id", uid);
                        context.startActivity(intent);

                    }
                });


            } catch (NullPointerException e) {

        }
    }

    public static String getMonday() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return new SimpleDateFormat("yyyyMMdd").format(c.getTime());
    }


    public static String getToday() {

        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    public static String getPreviewToday(int subtact) {

        Date date = new Date();
        date.setDate(date.getDate() - subtact);
        return new SimpleDateFormat("MMM dd").format(date);
    }


    public static String getTodayShort() {
        return new SimpleDateFormat("yyyyMMdd").format(new Date());
    }

    public static boolean diffDates(String date) {
       try {
           return new SimpleDateFormat("yyyyMMddHHmmss").parse(date).before(new Date());
       }catch (ParseException e){
           e.printStackTrace();
           return false;
       }


    }

    public static String getValidity() {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            Calendar c = Calendar.getInstance();

            c.add(Calendar.DATE, 56);
            Date resultdate = new Date(c.getTimeInMillis());
            String dateInString = sdf.format(resultdate);
            return dateInString;

    }

    public static String diff28days(String time) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(time));
        }catch (ParseException e){}
        c.add(Calendar.DATE, -56);
        Date resultdate = new Date(c.getTimeInMillis());
        String dateInString = sdf.format(resultdate);

        return dateInString;

    }

    public static int getDiffDays(String date){
        try {
            Date userDob = new SimpleDateFormat("yyyyMMdd").parse(date);
            Date today = new Date();
            long diff = today.getTime() - userDob.getTime();
            int numOfDays = (int) (diff / (1000 * 60 * 60 * 24));
            return numOfDays;
        }catch (ParseException e){
            return 0;
        }
    }

    public static int getDiffMins(String date){
        try {
            Date userDob = new SimpleDateFormat("yyyyMMddHHmmss").parse(date);
            Date today = new Date();
            long diff = today.getTime() - userDob.getTime();
            int minutes = (int) (diff / (1000 * 60));
            return minutes;
        }catch (ParseException e){
            return 0;
        }
    }

}
