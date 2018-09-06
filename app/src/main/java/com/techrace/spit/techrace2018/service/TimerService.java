package com.techrace.spit.techrace2018.service;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.techrace.spit.techrace2018.HomeFragment;
import com.techrace.spit.techrace2018.MainActivity;
import com.techrace.spit.techrace2018.R;

import static com.techrace.spit.techrace2018.MainActivity.cooldown;

//Starts a service which sends 1000 interval queues to the messagequeue via handler acting as a timer.
//Also broadcasts this information via broacster so other activities like main can receive it and updateUI.
public class TimerService extends Service {
    public static final String TAG = TimerService.class.getSimpleName();
    private final Handler handler = new Handler();
    //Intent filter used to receive the broadcast
    Intent intent;
    long currentTime, duration;
    long timeSinceLastOn, elapsedTimeSinceOff;
    boolean screenWasAsleep = false;
    //Thread handler uses to push to message queue. This creates a timer effect.
    private Runnable timerThread = new Runnable() {
        @Override
        public void run() {
            SystemClock.elapsedRealtime();

            if (currentTime == duration) {
                stopSelf();
                return;
            }
            //Waits until the screen is turned back on
            while (!isScreenOn()) {
                try {
                    Thread.sleep(1000);
                    screenWasAsleep = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //When the screen is on, updates timer with the elapsed time since it was off.
            if (screenWasAsleep && isScreenOn()) {
                elapsedTimeSinceOff = SystemClock.elapsedRealtime() - timeSinceLastOn;
                Log.d(TAG, " screen was off and updating current time by" + elapsedTimeSinceOff);
                currentTime += elapsedTimeSinceOff;
                screenWasAsleep = false;
            }
            timeSinceLastOn = SystemClock.elapsedRealtime();
            currentTime += 1000;
            sendTimerInfo();
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        timeSinceLastOn = SystemClock.elapsedRealtime();
        Log.d(TAG, "time since last on " + timeSinceLastOn);
        currentTime = duration = elapsedTimeSinceOff = 0L;
        intent = new Intent(Constants.ACTION.BROADCAST_ACTION);
        handler.removeCallbacks(timerThread);
        handler.postDelayed(timerThread, 0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
                if (intent.hasExtra(Constants.TIMER.DURATION)) {
                    duration = intent.getLongExtra(Constants.TIMER.DURATION, 0);
                }
                startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, createTimerNotification());
            } else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
                stopForeground(true);
                stopSelf();
            }

        }
        return START_STICKY;
    }

    private void sendTimerInfo() {
        if (isScreenOn()) {
            Log.d(TAG, "timer running: tick is " + currentTime);
            intent.putExtra(Constants.TIMER.CURRENT_TIME, currentTime);
            sendBroadcast(intent);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "timer service finished");

        handler.removeCallbacks(timerThread);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private Notification createTimerNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("TIME", "Timer Noti", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Power Card Applied on you.")
                .setTicker("Count up timer")
                .setContentText("Timer of " + cooldown + " minutes has been set.")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setChannelId("TIME")
                .setOngoing(true)
                .build();
        return notification;
    }

    public boolean isScreenOn() {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        boolean result = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH && powerManager.isInteractive() || Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH && powerManager.isScreenOn();
        return result;
    }
}
