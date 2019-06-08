package com.example.safe.View.Activities.StartActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.safe.Location.AddressDecoder;
import com.example.safe.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectTime extends Fragment {
    private final String TIME = "TIME";
    EditText editTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_time, container, false);

        editTime = rootView.findViewById(R.id.editTime);
        editTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!goodTime(s.toString()))
                    editTime.setTextColor(Color.RED);
                else
                    editTime.setTextColor(Color.BLACK);
            }
        });

        rootView.findViewById(R.id.pickTime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(goodTime(editTime.getText().toString())) {
                    StartActivity activity = ((StartActivity) getActivity());
                    activity.setTime(Integer.parseInt(editTime.getText().toString()) * 60 * 1000);
                    activity.hideKeyboard();
                    activity.goToNext();
                }
                else
                    Snackbar.make(v, "Correct time must be at most 4 digits long", Snackbar.LENGTH_SHORT)
                            .setAction("", null).show();
            }
        });

        if(savedInstanceState != null) {
            editTime.setText((String)savedInstanceState.get(TIME));
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle bundle) {
        bundle.putString(TIME, editTime.getText().toString());
    }


    private boolean goodTime(String time) {
        Pattern p = Pattern.compile("[0-9]|[0-9][0-9]|[0-9][0-9][0-9]|[0-9][0-9][0-9][0-9]");
        Matcher m = p.matcher(time);
        return m.matches();
    }
}
