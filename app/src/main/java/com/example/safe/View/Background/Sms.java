package com.example.safe.View.Background;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.telephony.SmsManager;

import com.example.safe.Model.Message;

public class Sms implements Message {
    private final String message;
    private final String number;
    private Intent sentIntent; //TODO co zrobić jak się nie uda wysłać wiadomości

    public Sms(String message, String number) {
        this.message = message;
        this.number = number;
    }

    public void send() {
        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(number, null, message, null, null);
    }

    private Sms(Parcel parcel) {
        message = parcel.readString();
        number = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeString(number);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Sms createFromParcel(Parcel in) {
            return new Sms(in);
        }

        public Sms[] newArray(int size) {
            return new Sms[size];
        }
    };
}
