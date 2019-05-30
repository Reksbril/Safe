package com.example.safe.View.Background;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;

import com.example.safe.Model.ActivityInfo;
import com.example.safe.Model.Message;
import com.example.safe.View.Activities.OngoingActivity;
import com.example.safe.R;

import java.util.ArrayList;

public class CurrentActivity extends Service {
    private ActivityInfo activity;


    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
       // unpackActivityInfo(intent);

        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);


        if (android.os.Build.VERSION.SDK_INT >= 26) { //TODO niższe wersje (bez notification channel)
            String CHANNEL_ID = "my_channel_01";
            CharSequence name = "my_channel";
            String Description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            manager.createNotificationChannel(mChannel);


            Intent showActivity = new Intent(this, OngoingActivity.class);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(this, 0, showActivity, 0);

            Notification notification = new Notification.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("My Awesome App")
                    .setContentIntent(pendingIntent)
                    .setContentText("Doing some work...").build();

            startForeground(1337, notification);
        }


        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                if(stopped)
                    this.cancel();
                System.out.println("working...");
            }

            public void onFinish() {
                System.out.println("finish");
            }
        }.start();
/*
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!stopped) {
                    System.out.println("Working...");
                    if (!checkConditions()) {
                        notifyUser();
                    }
                    handler.postDelayed(this, 2000);
                }
            }
        }, 2000);*/

        return START_STICKY;
    }

    private void init(Intent intent) {
        Context context = getApplicationContext();
        Bundle bundle = intent.getExtras();
        Location destination = (Location)bundle.get(context.getString(R.string.location_data));
        int duration = bundle.getInt(context.getString(R.string.duration));
        ArrayList<Message> messages = (ArrayList<Message>)bundle.get(context.getString(R.string.messages));



        activity = new ActivityInfo()
    }

    private boolean checkConditions() {
        //todo
        return true;
    }

    private void notifyUser() {
        //todo
    }


    @Override
    public void onDestroy() {
        stopped = true;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
