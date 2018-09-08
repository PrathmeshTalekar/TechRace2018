package com.techrace.spit.techrace2018;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().size() > 0) {
//            Map<String,String> data=remoteMessage.getData();
//            Log.i("map string",data.toString());
//            if(data.get("FEED").equals("TRUE")){
//
//            }

        }
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            sendNotification(title, body);
//            Log.i("FBMessage", body);
        }
    }

    private void sendNotification(String title, String body) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(500);
        }
        NotificationCompat.Builder buildermessage =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setOngoing(true)
                        .setLargeIcon(BitmapFactory.decodeResource(MyFirebaseMessaging.this.getResources(), R.mipmap.ic_launcher))
                        .setAutoCancel(true);
        NotificationChannel mChannel;
        NotificationManager notificationManagerforAlarm =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel("Message", "Message", NotificationManager.IMPORTANCE_DEFAULT);
            mChannel.enableVibration(true);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            notificationManagerforAlarm.createNotificationChannel(mChannel);
        }


        notificationManagerforAlarm.notify(2, buildermessage.build());
    }
}
