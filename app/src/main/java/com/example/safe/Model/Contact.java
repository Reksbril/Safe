package com.example.safe.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

@Entity
public class Contact {
    @PrimaryKey
    @NonNull
    private String number;
    @ColumnInfo
    private String name;
    @ColumnInfo
    private String message;
    @ColumnInfo
    private Integer imageId;

    public Contact(@NonNull String number, String name) {
        this.number = number;
        this.name = name;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Integer getImageId() {
        return imageId;
    }


    public String getName() {
        return name;
    }

    @NonNull
    public String getNumber() {
        return number;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }
}
