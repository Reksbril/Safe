package com.example.safe.Model;

import android.location.Location;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ActivityInfo {
    private final LocationGuard locationGuard;
    private final Timer timer;
    private final Runnable onActivitySuccess;
    private final Runnable onActivityFail;
    private volatile boolean stopped = false;

    private HandlerThread thread;


    public ActivityInfo(LocationGuard locationGuard, Timer timer, Runnable onFail, Runnable onSuccess) {
        this.locationGuard = locationGuard;
        this.onActivityFail = onFail;
        this.onActivitySuccess = onSuccess;
        this.timer = timer;

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
                            stopped = true;
                        } else {
                            if(locationGuard.outOfSafeLocation()) {
                                onActivityFail.run();
                                stopped = true;
                            } else
                                handler.postDelayed(this, timer.getDelay());
                        }

                    } else {
                        onActivityFail.run();
                        stopped = true;
                    }
                }
            }
        }, timer.getDelay());

        return true;
    }

    public void stopActivity() {
        stopped = true;
    }
}
