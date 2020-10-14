package com.example.weatherrappandroidclient.activities;

import android.content.IntentSender;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class CurrentWeatherActivity  extends AppCompatActivity implements LocationListener {

    public static final int REQUEST_CHECK_SETTINGS = 0x1;   // Per Google API docs, arbitrary constant for use in location on complete listener
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Callback for our location request
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        };

        // First we need to get GPS coordinates
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest coordinateRequest = LocationRequest.create();       // The location requests we want to make, create with default params
        coordinateRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);  // Low power is defined as "city level" accuracy, which should suffice
        coordinateRequest.setNumUpdates(1);                                 // We really only need one update at the start of the activity, user can refresh for more

        processRequest(coordinateRequest);


    }



    private void processRequest(LocationRequest coordinateRequest){
        // Build the request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(coordinateRequest);

        Task<LocationSettingsResponse> locationSettings =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        // Code adapted from example given at https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        locationSettings.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>(){
            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. Initialize location requests.
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        CurrentWeatherActivity.this,
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
    }
}
