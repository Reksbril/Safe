package com.example.safe.Database;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.ContextWrapper;

//todo zrobić singleton


public class DbSingleton {
    public final Database database;

    public DbSingleton(Context context) {
        database = Room.databaseBuilder(context, Database.class, "name").build();
    }
}
