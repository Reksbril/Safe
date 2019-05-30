package com.example.safe.View.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.example.safe.Location.AddressDecoder;
import com.example.safe.R;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class SelectDestinationActivity extends FragmentActivity implements OnMapReadyCallback {
    GoogleMap map;
    Location currentChosenLocation;
    TextView addressText;
    Marker currentMarker;


    private class AddressResultReceiver extends ResultReceiver {
        private LatLng pendingLocation;

        public AddressResultReceiver(LatLng pendingLocation, Handler handler) {
            super(handler);
            this.pendingLocation = pendingLocation;
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if(resultData == null)
                return;

            String address = resultData.getString(getString(R.string.result_data));
            currentChosenLocation = new Location(LocationManager.GPS_PROVIDER);
            currentChosenLocation.setLatitude(pendingLocation.latitude);
            currentChosenLocation.setLongitude(pendingLocation.longitude);

            if(resultCode == AddressDecoder.SUCCESS_RESULT) {
                addressText.setTextColor(Color.GRAY);
                addressText.setText(address);
                if (currentMarker != null)
                    currentMarker.setPosition(pendingLocation);
                else
                    createMarker();
            }
        }
    }


    private void createMarker() {
        currentMarker = map.addMarker(new MarkerOptions()
                .title("Chosen position")
                .position(new LatLng(
                        currentChosenLocation.getLatitude(), currentChosenLocation.getLongitude()))
                .draggable(false));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent createIntent = getIntent();

        addressText = findViewById(R.id.addressText);

        if((Boolean)createIntent.getExtras().get(getString(R.string.location_available))) {
            currentChosenLocation =
                    (Location) createIntent.getExtras().get(getString(R.string.location));
            addressText.setText(
                    (CharSequence) createIntent.getExtras().get(getString(R.string.address)));
        }

        findViewById(R.id.acceptLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentChosenLocation == null) {
                    addressText.setTextColor(Color.RED);
                    addressText.setText(getString(R.string.fail_location));
                } else {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(getString(R.string.location), currentChosenLocation);
                    returnIntent.putExtra(getString(R.string.address), addressText.getText());
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent intent = new Intent(getApplicationContext(), AddressDecoder.class);
                intent.putExtra(getString(R.string.receiver_code),
                        new AddressResultReceiver(latLng, new Handler()));

                Location temp = new Location(LocationManager.GPS_PROVIDER);
                temp.setLatitude(latLng.latitude);
                temp.setLongitude(latLng.longitude);

                intent.putExtra(getString(R.string.location_data), temp);
                startService(intent);
            }
        });

        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                setFocusOnCurrentPos(true);
                return true;
            }
        });


        if(currentChosenLocation != null)
            createMarker();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    getResources().getInteger(R.integer.REQUEST_ACCESS_FINE_LOCATION));
        } else {
            setFocusOnCurrentPos(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == getResources().getInteger(R.integer.REQUEST_ACCESS_FINE_LOCATION)) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setFocusOnCurrentPos(false);
            }
        }
    }


    private void setFocusOnCurrentPos(final boolean animate) {
        try {
            LocationServices.getFusedLocationProviderClient(this)
                .getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                LatLng myLoc = new LatLng(location.getLatitude(), location.getLongitude());
                                if(animate)
                                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 15));
                                else
                                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 15));

                                map.setMyLocationEnabled(true);
                            }
                        }
                    });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}