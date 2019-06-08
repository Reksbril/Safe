package com.example.safe.View.Activities.StartActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.safe.Location.AddressDecoder;
import com.example.safe.R;
import com.example.safe.View.Activities.StartActivity.StartActivity;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class SelectDestination extends Fragment {
    private GoogleMap map;
    private Location currentChosenLocation;
    private TextView addressText;
    private Marker currentMarker;


    private final String DESTINATION = "DESTINATION";
    private final String DESTINATION_AVAILABLE = "DESTINATION_AVAILABLE";
    private final String ADDRESS = "ADDRESS";


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_map, container, false);

        MapView mMapView;
        mMapView = rootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        addressText = rootView.findViewById(R.id.addressText);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(savedInstanceState != null &&
                (Boolean)savedInstanceState.get(DESTINATION_AVAILABLE)) {
            currentChosenLocation = (Location) savedInstanceState.get(DESTINATION);
            addressText.setText((String)savedInstanceState.get(ADDRESS));
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        Intent intent = new Intent(getContext(), AddressDecoder.class);
                        intent.putExtra(getString(R.string.receiver_code),
                                new AddressResultReceiver(latLng, new Handler()));

                        Location temp = new Location(LocationManager.GPS_PROVIDER);
                        temp.setLatitude(latLng.latitude);
                        temp.setLongitude(latLng.longitude);

                        intent.putExtra(getString(R.string.location_data), temp);
                        getActivity().startService(intent);
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

                if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            getResources().getInteger(R.integer.REQUEST_ACCESS_FINE_LOCATION));
                } else {
                    setFocusOnCurrentPos(false);
                }
            }
        });

        rootView.findViewById(R.id.acceptLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentChosenLocation == null) {
                    addressText.setTextColor(Color.RED);
                    addressText.setText(getString(R.string.fail_location));
                } else {
                    StartActivity activity = ((StartActivity)getActivity());
                    activity.setDestination(currentChosenLocation);
                    activity.goToNext();
                }
            }
        });



        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == getResources().getInteger(R.integer.REQUEST_ACCESS_FINE_LOCATION) ||
            requestCode == getResources().getInteger(R.integer.REQUEST_ACCESS_COARSE_LOCATION)) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //setFocusOnCurrentPos(false);
            }
        }
    }


    private void setFocusOnCurrentPos(final boolean animate) {
        try {
            LocationServices.getFusedLocationProviderClient(getContext())
                .getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle bundle) {
        if(currentChosenLocation != null) {
            bundle.putBoolean(DESTINATION_AVAILABLE, true);
            bundle.putParcelable(DESTINATION, currentChosenLocation);
            bundle.putString(ADDRESS, addressText.getText().toString());
        } else {
            bundle.putBoolean(DESTINATION_AVAILABLE, false);
        }
    }
}