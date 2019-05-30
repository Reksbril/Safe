package com.example.safe.Database;

import android.arch.persistence.room.Room;
import android.content.Context;

public class DbSingleton {
    public static volatile Context appContext;
    public final Database db;

    private DbSingleton() {
        db = Room.databaseBuilder(appContext, Database.class, "name").build();
    }

    private static class SingletonHolder {
        public static final DbSingleton instance = new DbSingleton();
    }

    public static DbSingleton getInstance() {
        if(appContext == null)
            throw new RuntimeException("Application context must be set first!");
        return SingletonHolder.instance;
    }

    public static void setAppContext(Context context) {
        appContext = context;
    }
}
