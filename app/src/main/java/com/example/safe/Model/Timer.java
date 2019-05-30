package com.example.safe.Model;

public interface Timer {
    int getDelay(); //delay between two ticks
    boolean tick(); //returns false if timer stopped
}
