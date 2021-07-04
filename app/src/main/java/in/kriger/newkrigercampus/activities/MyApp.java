package in.kriger.newkrigercampus.activities;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;

import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.extras.TypefaceUtil;
import io.fabric.sdk.android.Fabric;

/**
 * Created by poojanrathod on 6/29/17.
 */

public class MyApp extends Application {

    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Toasty.Config.getInstance()
                .setToastTypeface(new TypefaceUtil().getfontbold(getApplicationContext())).apply();


    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


}


