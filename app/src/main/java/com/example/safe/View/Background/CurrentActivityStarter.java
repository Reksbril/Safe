package com.example.safe.View.Background;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.example.safe.Model.ActivityInfo;
import com.example.safe.Model.Message;
import com.example.safe.R;

import java.util.ArrayList;

public class CurrentActivityStarter {

    public static void start(
            @NonNull Context context,
            @IntRange(from=0) int duration,
            @NonNull Location destination,
            @NonNull ArrayList<Message> messages) {
        Intent intent = new Intent(context, CurrentActivity.class);

        intent.putExtra(context.getString(R.string.location_data), destination);
        intent.putExtra(context.getString(R.string.duration), duration);
        intent.putExtra(context.getString(R.string.messages), messages);

        context.startService(intent);
    }
}
