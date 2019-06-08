package com.example.safe.View.Activities;

import android.app.Activity;
import android.arch.persistence.room.Delete;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.safe.Model.LocationGuard;
import com.example.safe.Model.Timer;
import com.example.safe.View.Background.CurrentActivity;
import com.example.safe.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.SQLOutput;

public class OngoingActivity extends FragmentActivity implements OnMapReadyCallback {
    private ServiceConnection connection;
    private CurrentActivity myService;
    private LocationGuard.Observer locationObserver;
    private Timer.Observer timeObserver;
    private LocationSource.OnLocationChangedListener listener;
    private Location destination;
    private GoogleMap map;

    private TextView timeView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing);

        Bundle b = getIntent().getExtras();

        if(b != null)
            destination = (Location)(b.get(getString(R.string.destination)));

        timeObserver = new Timer.Observer() {
            @Override
            public void notifyChange(int millisLeft) {
                updateTime(millisLeft);
            }
        };
        locationObserver = new LocationGuard.Observer() {
            @Override
            public void notifyChange(Location loc) {
                updateLocation(loc, false);
            }
        };

        timeView = findViewById(R.id.timeView);

        if(!bindToService())
            finish();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void cancelActivity() {
        stopService(new Intent(getApplicationContext(), CurrentActivity.class));
        finish();
    }

    private boolean bindToService() {
        final ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                myService = ((CurrentActivity.MyBinder)service).getService();
                myService.addLocationObserver(locationObserver);
                myService.addTimeObserver(timeObserver);
                updateLocation(myService.getCurrentLocation(), true);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }

            @Override
            public void onBindingDied(ComponentName name) {
            }

            @Override
            public void onNullBinding(ComponentName name) {

            }
        };
        Intent intent = new Intent(this, CurrentActivity.class);
        return bindService(intent, connection, BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(myService != null) {
            myService.removeLocationObserver(locationObserver);
            myService.removeTimeObserver(timeObserver);
        }
        if(connection != null)
            unbindService(connection);
    }

    private void updateLocation(Location location, boolean first) {
        if (listener != null) {
            listener.onLocationChanged(location);
        }
        if (map != null) {
            LatLng myLoc = new LatLng(location.getLatitude(), location.getLongitude());
            if (first)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 15));
        }
    }

    private void updateTime(final int millisLeft) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                timeView.setText(Integer.toString(millisLeft));
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        googleMap.setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener onLocationChangedListener) {
                listener = onLocationChangedListener;
                if(myService != null)
                    updateLocation(myService.getCurrentLocation(), true);
            }

            @Override
            public void deactivate() {
                listener = null;
            }
        });
        try {
            map.setMyLocationEnabled(true);
        } catch(SecurityException e) {
            e.printStackTrace();
        }

        createMarker();
    }

    private void createMarker() {
        if(destination != null)
        map.addMarker(new MarkerOptions()
                .title("Destination")
                .position(new LatLng(
                        destination.getLatitude(), destination.getLongitude()))
                .draggable(false));
    }
}
