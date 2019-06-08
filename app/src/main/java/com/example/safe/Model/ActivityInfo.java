package com.example.safe.Model;

import android.location.Location;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Observer;

public class ActivityInfo {
    private final LocationGuard locationGuard;
    private final Timer timer;
    private final Runnable onActivitySuccess;
    private final Runnable onActivityFail;
    private volatile boolean stopped = false;

    private HandlerThread thread;

    public interface Observer {
        void notifyFinish();
    }

    private final ArrayList<Observer> observers;

    public ActivityInfo(LocationGuard locationGuard, Timer timer, Runnable onFail, Runnable onSuccess) {
        this.locationGuard = locationGuard;
        this.onActivityFail = onFail;
        this.onActivitySuccess = onSuccess;
        this.timer = timer;
        this.observers = new ArrayList<>();

        final String threadName = "ActivityInfo.ThreadName";
        thread = new HandlerThread(threadName);
    }


    public boolean startActivity() {
        if(stopped)
            return false;
        thread.start();

        final Handler handler  = new Handler(thread.getLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!stopped) {
                    if (timer.tick()) {
                        if (locationGuard.destinationReached()) {
                            onActivitySuccess.run();
                            stopActivity();
                            notifyObservers();
                        } else {
                            if(locationGuard.outOfSafeLocation()) {
                                onActivityFail.run();
                                stopActivity();
                                notifyObservers();
                            } else
                                handler.postDelayed(this, timer.getDelay());
                        }

                    } else {
                        onActivityFail.run();
                        stopActivity();
                        notifyObservers();
                    }
                }
            }
        }, timer.getDelay());

        return true;
    }

    public void stopActivity() {
        stopped = true;
    }

    public void addObserver(Observer observer) {
        synchronized (observers) {
            observers.add(observer);
        }
    }

    public void removeObserver(Observer observer) {
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    private void notifyObservers() {
        synchronized (observers) {
            for (Observer o : observers)
                o.notifyFinish();
        }
    }
}
