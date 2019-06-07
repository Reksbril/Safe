package com.example.safe.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.example.safe.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

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
    private byte[] image;

    public Contact(@NonNull String number,
                    @NonNull String name,
                    @NonNull String message,
                    @NonNull byte[] image) {
        this.number = number;
        this.name = name;
        this.message = message;
        this.image = image;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public byte[] getImage() {
        return image;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(@NonNull String number) {
        this.number = number;
    }

    @NonNull
    public String getNumber() {
        return number;
    }

    public void setImage(byte[] image) {
        this.image = image;
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

    public static Bitmap decodeImage(byte[] image) {
        ByteArrayInputStream imageStream = new ByteArrayInputStream(image);
        try {
            Bitmap result = BitmapFactory.decodeStream(imageStream);
            return result;
        } catch(OutOfMemoryError error) {
            error.printStackTrace();
        }
        return null;
    }

    public static byte[] encodeImage(Bitmap image) {
        if(image == null)
            return new byte[0];
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] result = stream.toByteArray();
        image.recycle();
        return result;
    }
}
