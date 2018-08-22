package com.techrace.spit.techrace2018;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import static com.techrace.spit.techrace2018.MainActivity.cooldown;
import static com.techrace.spit.techrace2018.MainActivity.timerOn;
import static com.techrace.spit.techrace2018.HomeFragment.level;
import static com.techrace.spit.techrace2018.HomeFragment.UID;
import static com.techrace.spit.techrace2018.HomeFragment.UserDatabaseReference;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("time done", "in recieve");
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Done!")
                        .setContentText("Timer is set off")
                        .setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
        UserDatabaseReference = FirebaseDatabase.getInstance().getReference();
        UserDatabaseReference.child("Users").child(UID).child("cooldown").setValue(0);
        MainActivity.prefEditor = MainActivity.pref.edit().putInt("Cooldown", 0);
        MainActivity.prefEditor.apply();
        UserDatabaseReference.child("Users").child(UID).child("level").setValue(level + 1);
        UserDatabaseReference.child("Users").child(UID).child("points").setValue(MainActivity.points + 5);
        Date d = new Date();
        long l = d.getTime();
        UserDatabaseReference.child("Users").child(UID).child("Time" + String.valueOf(level)).setValue(l);
        UserDatabaseReference.child("Leaderboard").child(UID).setValue(new LeaderBoardOBject(HomeFragment.name, level, MainActivity.points, l, cooldown, UID));
        MainActivity.beacon = true;
        timerOn = false;
        new HomeFragment().onResume();
//        new HomeFragment().onResume();
    }
}
