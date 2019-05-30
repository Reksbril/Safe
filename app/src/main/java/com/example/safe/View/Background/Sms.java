package com.example.safe.View.Background;

import android.content.Intent;
import android.telephony.SmsManager;

public class Sms {
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

}
