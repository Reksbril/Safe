package com.example.safe.View.Background;

import android.app.Activity;
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
import android.support.annotation.IntRange;

import com.example.safe.Model.ActivityInfo;
import com.example.safe.Model.LocationGuard;
import com.example.safe.Model.Message;
import com.example.safe.Model.Timer;
import com.example.safe.View.Activities.OngoingActivity;
import com.example.safe.R;

import java.util.ArrayList;

public class CurrentActivity extends Service {
    private ActivityInfo activity;
    private NotificationManager manager;

    private class TimerImpl implements Timer {
        private int delay;
        private int numberOfTicksLeft;

        TimerImpl(@IntRange(from = 1) int delay, @IntRange(from = 0) int numberOfTicks) {
            this.delay = delay;
            this.numberOfTicksLeft = numberOfTicks;
        }

        @Override
        public int getDelay() {
            return delay;
        }

        @Override
        public boolean tick() {
            if(numberOfTicksLeft == 0)
                return false;
            numberOfTicksLeft--;
            return true;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
       // unpackActivityInfo(intent);

        manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        //todo ogarnąć dobrze to powiadomienie (napisy itd)

        Intent showActivity = new Intent(this, OngoingActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, showActivity, 0);

        int iconId = R.mipmap.ic_launcher;
        String title = "My awesome app";


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

            Notification notification = new Notification.Builder(this, CHANNEL_ID)
                    .setSmallIcon(iconId)
                    .setContentTitle(title)
                    .setContentIntent(pendingIntent)
                    .setContentText("").build();

            startForeground(1337, notification);
        } else {
            Notification notification = new Notification.Builder(this)
                    .setSmallIcon(iconId)
                    .setContentTitle(title)
                    .setContentIntent(pendingIntent)
                    .setContentText("").build();

            startForeground(1337, notification);
        }

        init(intent);

        return START_STICKY;
    }

    private void init(Intent intent) {
        Context context = getApplicationContext();
        Bundle bundle = intent.getExtras();
        Location destination = (Location)bundle.get(context.getString(R.string.location_data));
        int duration = bundle.getInt(context.getString(R.string.duration));
        final ArrayList<Message> messages = (ArrayList<Message>)bundle.get(context.getString(R.string.messages));

        LocationGuard guard = new LocationGuardImpl(context, destination, 1000);

        Runnable onSuccess = new Runnable() {
            @Override
            public void run() {
                //todo
            }
        };

        Runnable onFail = new Runnable() {
            @Override
            public void run() {
                for(Message message : messages)
                    message.send();
            }
        };

        activity = new ActivityInfo(
                guard,
                new TimerImpl(1000, duration / 1000),
                onFail,
                onSuccess);

        activity.addObserver(new ActivityInfo.Observer() {
            @Override
            public void notifyFinish() {
                stopService(new Intent(getApplicationContext(), CurrentActivity.class));
            }
        });

        activity.startActivity();
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
        activity.stopActivity();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
