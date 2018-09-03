package com.techrace.spit.techrace2018;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().size() > 0) {

        }
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            sendNotification(title, body);
        }
    }

    private void sendNotification(String title, String body) {
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
            notificationManagerforAlarm.createNotificationChannel(mChannel);
        }


        notificationManagerforAlarm.notify(2, buildermessage.build());

    }
}
