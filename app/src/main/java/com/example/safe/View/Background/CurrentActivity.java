package com.example.safe.View.Background;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.arch.persistence.room.Delete;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.os.Binder;
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
import java.util.HashSet;
import java.util.Set;

public class CurrentActivity extends Service {
    private ActivityInfo activity;
    private NotificationManager manager;
    String CHANNEL_ID = "my_channel_01";

    private Location destination;

    private Timer timer;
    private LocationGuard guard;

    private class TimerImpl implements Timer {
        private int delay;
        private int numberOfTicksLeft;
        private final Set<Observer> observers = new HashSet<>();

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

            synchronized (observers) {
                for (Observer obs : observers)
                    obs.notifyChange(numberOfTicksLeft * delay);
            }
            numberOfTicksLeft--;
            return true;
        }

        @Delete
        public void addObserver(Observer observer) {
            synchronized (observers) {
                observers.add(observer);
            }
        }

        @Override
        public void removeObserver(Observer observer) {
            synchronized (observers) {
                observers.remove(observer);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
       // unpackActivityInfo(intent);

        manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        Bundle bundle = intent.getExtras();
        destination = (Location)bundle.get(getString(R.string.location_data));

        Intent showActivity = new Intent(this, OngoingActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, showActivity, 0);

        int iconId = R.mipmap.ic_launcher;
        String title = "Safe";


        if (android.os.Build.VERSION.SDK_INT >= 26) {
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
                    .setContentText("Your activity is running")
                    .build();

            startForeground(1337, notification);
        } else {
            Notification notification = new Notification.Builder(this)
                    .setSmallIcon(iconId)
                    .setContentTitle(title)
                    .setContentIntent(pendingIntent)
                    .setContentText("Your activity is running")
                    .build();

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

        guard = new LocationGuardImpl(context, destination, 1000);

        Runnable onSuccess = new Runnable() {
            @Override
            public void run() {
                Notification.Builder builder;
                if (android.os.Build.VERSION.SDK_INT >= 26)
                    builder = new Notification.Builder(CurrentActivity.this, CHANNEL_ID);
                else
                    builder = new Notification.Builder(CurrentActivity.this);
                Notification notification = builder.setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Safe")
                        .setContentText("Activity successfully finished")
                        .build();
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.notify(14, notification);
            }
        };

        Runnable onFail = new Runnable() {
            @Override
            public void run() {
                for(Message message : messages)
                    try {
                        message.send(); //todo pozbyć się try catch
                    } catch(IllegalArgumentException e) {
                        e.printStackTrace();
                    }

            }
        };

        timer = new TimerImpl(1000, duration / 1000);
        activity = new ActivityInfo(
                guard,
                timer,
                onFail,
                onSuccess);

        activity.addObserver(new ActivityInfo.Observer() {
            @Override
            public void notifyFinish() {
                stopForeground(true);
            }
        });

        activity.startActivity();
    }


    @Override
    public void onDestroy() {
        activity.stopActivity();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }


    public class MyBinder extends Binder {
        public CurrentActivity getService() {
            return CurrentActivity.this;
        }
    }

    public void addLocationObserver(LocationGuard.Observer observer) {
        guard.addObserver(observer);
    }

    public void addTimeObserver(Timer.Observer observer) {
        timer.addObserver(observer);
    }

    public void removeLocationObserver(LocationGuard.Observer observer) {
        guard.removeObserver(observer);
    }

    public void removeTimeObserver(Timer.Observer observer) {
        timer.removeObserver(observer);
    }

    public Location getCurrentLocation() {
        return guard.getCurrentLocation();
    }

    public Location getDestination() {
        return destination;
    }

    public void addFinishObserver(ActivityInfo.Observer observer) {
        activity.addObserver(observer);
    }

    public void removeFinishObserver(ActivityInfo.Observer observer) {
        activity.removeObserver(observer);
    }
}
