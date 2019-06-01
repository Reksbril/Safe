package com.example.safe.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.example.safe.R;

@Entity
public class Contact {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    @NonNull
    private String number;
    @ColumnInfo
    private String name;
    @ColumnInfo
    private String message;
    @ColumnInfo
    private Integer imageId;

    public Contact(@NonNull String number, @NonNull String name, @NonNull String message) {
        this(number, name, message, R.drawable.ic_launcher_foreground);
    }

    private Contact(@NonNull String number,
                    @NonNull String name,
                    @NonNull String message,
                    @NonNull Integer imageId) {
        this.number = number;
        this.name = name;
        this.message = message;
        this.imageId = imageId;
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

    @Override
    public boolean equals(Object o) {
        if(o == this)
            return true;

        if(o == null)
            return false;

        if(getClass() != o.getClass())
            return false;

        Contact c = (Contact)o;

        return c.number.equals(number) && c.message.equals(message);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
