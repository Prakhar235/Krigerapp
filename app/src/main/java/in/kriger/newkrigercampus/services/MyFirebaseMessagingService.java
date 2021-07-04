package in.kriger.newkrigercampus.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import in.kriger.newkrigercampus.activities.SplashScreen;
import in.kriger.newkrigercampus.R;

import java.util.Map;

/**
 * Created by poojanrathod on 7/25/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
        }

        String click_action = remoteMessage.getNotification().getClickAction();

        if (remoteMessage.getNotification() != null) {
            sendNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody(),remoteMessage.getData(),click_action);
        }

    }
    private void sendNotification(String messageTitle, String messageBody, Map<String,String> dataload,String click_action) {

        Intent notificationIntent = null;


        notificationIntent = new Intent(this, SplashScreen.class);
        if (!dataload.get("type").equals("connection_request") && !dataload.get("type").equals("accept_request")){
            notificationIntent.putExtra("question_id",dataload.get("id"));

        }


        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_kc)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(contentIntent);
        //Vibration
        mBuilder.setVibrate(new long[] { 1000});

        //LED
        mBuilder.setLights(Color.RED, 3000, 3000);


        mBuilder.setContentIntent(contentIntent);
        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0,mBuilder.build());

    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        try {

            KrigerConstants.userRef.child(user.getUid()).child("profile_token").setValue(s);

        }catch (NullPointerException e){

        }
    }
}