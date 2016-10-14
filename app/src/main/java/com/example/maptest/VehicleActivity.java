package com.example.maptest;

import android.*;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public class VehicleActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String TAG = VehicleActivity.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private LatLng previousLatLng = null;
    private long previousTime;
    private float x = 0;
    private float y = 0;
    private float z = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1 * 1000) // 1 seconds
                .setFastestInterval(1 * 1000); // 1 second

        previousTime = SystemClock.elapsedRealtime();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    /*
    * GoogleApiClient Method
    */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        Location location = null;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            handleNewLocation(location);
        }
    }

    /*
    * GoogleApiClient Method
    */
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    /*
    * GoogleApiClient Method
    */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start Activity that tries to resolve error
                connectionResult.startResolutionForResult(this, 9000);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    public void handleNewLocation(Location location) {
        double currentLat = location.getLatitude();
        double currentLong = location.getLongitude();
        LatLng latLng = new LatLng(currentLat, currentLong);

        // exit if no previous location was stored
        if (previousLatLng == null) {
            previousLatLng = latLng;
            previousTime = SystemClock.elapsedRealtime();
            return;
        }

        // Calculate speed
        double speed = getSpeed(latLng);

        // Display
        TextView speedText = (TextView) findViewById(R.id.vehicleSpeed);
        String cSpeedInString = String.valueOf(speed);
        String cToDisplay = "Speed by Calculation: " + cSpeedInString.substring(0, cSpeedInString.indexOf(".") + 2);
        speedText.setText(cToDisplay);

        TextView formulaText = (TextView) findViewById(R.id.formulaSpeed);
        String fSpeedInString = String.valueOf(location.getSpeed());
        String fToDisplay = "Speed by Formula: " + fSpeedInString.substring(0, fSpeedInString.indexOf(".") + 2);
        formulaText.setText(fToDisplay);
    }

    public double getSpeed(LatLng currentLatLng) {
        // radius of earth in metres
        double r = 6371000;

        // P
        double lat1 = Math.toRadians(previousLatLng.latitude);
        double lon1 = Math.toRadians(previousLatLng.longitude);
        double rho1 = r * Math.cos(lat1);
        double z1 = r * Math.sin(lat1);
        double x1 = rho1 * Math.cos(lon1);
        double y1 = rho1 * Math.sin(lon1);

        // Q
        double lat2 = Math.toRadians(currentLatLng.latitude);
        double lon2 = Math.toRadians(currentLatLng.longitude);
        double rho2 = r * Math.cos(lat2);
        double z2 = r * Math.sin(lat2);
        double x2 = rho2 * Math.cos(lon2);
        double y2 = rho2 * Math.sin(lon2);

        // Dot product
        double dot = (x1 * x2 + y1 * y2 + z1 * z2);
        double cos_theta = dot / (r * r);

        double theta = Math.acos(cos_theta);

        // calculate speed
        double dist = r * theta;
        long time_s = (SystemClock.elapsedRealtime() - previousTime) / 1000;
        double speed_mps = dist / time_s;

        // update frame values
        previousLatLng = currentLatLng;
        previousTime = SystemClock.elapsedRealtime();

        return speed_mps * 3.6;
    }

    /////////////
    // BUTTONS //
    /////////////

    public void healthPressed(View view) {
        Intent intent = new Intent(this, HealthActivity.class);
        startActivity(intent);
    }

    public void mapsPressed(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void mediaPressed(View view) {
        Intent intent = new Intent(this, MediaActivity.class);
        startActivity(intent);
    }

    public void infoPressed(View view) {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }

    public void settingsPressed(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

}
