package com.example.safe.Model;

import android.graphics.Bitmap;

public class ContactBasic {
    public String name;
    public String phoneNo;
    public Bitmap image;

    public ContactBasic(String name, String phoneNo, Bitmap image) {
        this.name = name;
        this.phoneNo = phoneNo;
        this.image = image;
    }
}
