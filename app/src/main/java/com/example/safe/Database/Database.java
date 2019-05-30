package com.example.safe.Database;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomDatabase;
import android.support.annotation.NonNull;


@android.arch.persistence.room.Database(entities = {Contact.class}, version = 1)
public abstract class Database extends RoomDatabase {
    public abstract ContactDao contactDao();
}
