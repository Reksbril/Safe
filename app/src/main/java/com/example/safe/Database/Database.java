package com.example.safe.Database;

import android.arch.persistence.room.RoomDatabase;

import com.example.safe.Model.Contact;


@android.arch.persistence.room.Database(entities = {Contact.class}, version = 1)
public abstract class Database extends RoomDatabase {
    public abstract ContactDao contactDao();
}
