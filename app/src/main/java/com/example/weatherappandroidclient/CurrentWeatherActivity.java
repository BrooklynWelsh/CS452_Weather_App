package com.example.weatherappandroidclient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherappandroidclient.classes.NWSForecast;
import com.example.weatherappandroidclient.classes.NWSLatestMeasurements;
import com.example.weatherappandroidclient.classes.OnEventListener;
import com.example.weatherappandroidclient.classes.VolleyServerRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

public class CurrentWeatherActivity extends AppCompatActivity {

    private double latitude;
    private double longitude;
    private TextView info;
    LocationRequest coordinateRequest = LocationRequest.create();
    private static final String TAG = "LocationActivity";
    private LocationCallback mCallback;
    FusedLocationProviderClient fusedClient;
    private LocationRequest mRequest;
    private final int PERMISSION_ACCESS_LOCATION = 1;
    static public String pointURL = "https://api.weather.gov/points/";    // Base URL which we will add GPS coordinates to.
    static ObjectMapper mapper = new ObjectMapper();
    String latestMeasurementsURL;
    NWSLatestMeasurements measurements;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        info = findViewById(R.id.info);

        // First we need to get GPS coordinates
        // So make sure GPS is enabled. If not, show an alert.
        final LocationManager manager = (LocationManager) getApplicationContext().getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }


        mCallback = new LocationCallback() {
            //This callback is where we get "streaming" location updates. We can check things like accuracy to determine whether
            //this latest update should replace our previous estimate.
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d(TAG, "locationResult null");
                    return;
                }
                Log.d(TAG, "received " + locationResult.getLocations().size() + " locations");
                for (Location loc : locationResult.getLocations()) {
                    latitude = loc.getLatitude();
                    longitude = loc.getLongitude();

                    // Now we can make request to NWS API
                    pointURL += latitude + "," + longitude;
                    try {
                        getPointJSON(pointURL);              // First get stations endpoint URL from our existing point URL
                     //   URL latestMeasurementsURL = getMeasurementsURL(stationsURL);    // Now, from the stations endpoint, we can get the first station URL from the "observationStations" field
                                                                                        // note that we append "/observations/latest" to that station URL to get latest measurements
                        //NWSLatestMeasurements measurements = getLatestMeasurements(latestMeasurementsURL);
                        //info.setText(measurements.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                Log.d(TAG, "locationAvailability is " + locationAvailability.isLocationAvailable());
                super.onLocationAvailability(locationAvailability);
            }
        };

        //permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //request permission.
            //However check if we need to show an explanatory UI first
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                showRationale();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE}, 2);
            }
        } else {
            //we already have the permissions.
            getLocation();
        }

    }

    public void getPointJSON(String url) throws MalformedURLException {
        // For current measurements, we want to get the "observationStations" field and return the URL
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        VolleyServerRequest stringRequest = new VolleyServerRequest(getApplicationContext(), new OnEventListener() {
            @Override
            public void onSuccess(Object object) throws IOException {
                // We now have the correct stations URL, now read it and select the first station
                JsonNode response = (JsonNode)object;
                JsonNode propertyNode = response.path("properties");					// Properties field contains the forecast URLs (and more)
                JsonNode forecastNode = propertyNode.path("observationStations");	// First get the forecast URL string
                String stationsURL = forecastNode.textValue();				// Convert to URL object and add to array
                getMeasurementsURL(stationsURL);
            }

            @Override
            public void onFailure(Exception e) {

            }
        }, url);

    }

    public void getMeasurementsURL(String url) throws IOException {
        VolleyServerRequest stringRequest = new VolleyServerRequest(getApplicationContext(), new OnEventListener() {
            @Override
            public void onSuccess(Object object) throws IOException {
                // We now have the correct stations URL, now read it and select the first station
                JsonNode response = (JsonNode)object;
                latestMeasurementsURL = response.path("observationStations").path(0).textValue() + "/observations/latest";  // Get station URL then append path end in order to get latest measures
                getLatestMeasurements(latestMeasurementsURL);
            }

            @Override
            public void onFailure(Exception e) {

            }
        }, url);
//        URL latestMeasurementsURL = null;
//        JsonNode json = mapper.readTree(url);
//        JsonNode stationsNode = json.path("observationStations");
//        Iterator<JsonNode> stations = stationsNode.elements();
//
//        if(stations.hasNext()){
//            latestMeasurementsURL = new URL(mapper.treeToValue(stations.next(), String.class).toString() + "/observations/latest");
//        }
//        else{
//            Log.d(TAG, "ERROR: We didn't get a measurements URL from stations endpoint.");
//        }
    }

    public void getLatestMeasurements(String url) throws IOException{
        VolleyServerRequest stringRequest = new VolleyServerRequest(getApplicationContext(), new OnEventListener() {
            @Override
            public void onSuccess(Object object) throws IOException {
                // We now have the measurements, so map it to the NWSLatestMeasurements POJO
                JsonNode response = (JsonNode)object;
                JsonNode propertyNode = response.path("properties");
                measurements = mapper.treeToValue(propertyNode, NWSLatestMeasurements.class);
                Log.d(TAG, measurements.toString());
                info.setText(measurements.toString());
            }

            @Override
            public void onFailure(Exception e) {

            }
        }, url);
    }

    public void startHourlyForecastActivity(View view){
        Intent intent = new Intent(this, HourlyForecastActivity.class);
        startActivity(intent);
    }

    public void startWeeklyForecastActivity(View view){
        Intent intent = new Intent(this, WeeklyForecastActivity.class);
        startActivity(intent);
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        fusedClient = LocationServices.getFusedLocationProviderClient(this);

        createLocRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mRequest);

        //This checks whether the GPS mode (high accuracy,battery saving, device only) is set appropriately for "mRequest". If the current settings cannot fulfil
        //mRequest(the Google Fused Location Provider determines these automatically), then we listen for failures and show a dialog box for the user to easily
        //change these settings.
        SettingsClient client = LocationServices.getSettingsClient(CurrentWeatherActivity.this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(CurrentWeatherActivity.this, 500);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });

        //actually start listening for updates: See on Resume(). It's done there so that conveniently we can stop listening in onPause
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {
        fusedClient.requestLocationUpdates(mRequest, mCallback, null);
    }

    protected void createLocRequest() {
        mRequest = new LocationRequest();
        mRequest.setNumUpdates(1);
        mRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode == PERMISSION_ACCESS_LOCATION){
            finish();
            startActivity(getIntent());
        }
    }


    private void showRationale() {
        AlertDialog dialog = new AlertDialog.Builder(this).setMessage("We need location in order to give you accurate weather").setPositiveButton("Sure", (dialogInterface, i) ->
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
            dialogInterface.dismiss();
        })
                .create();
        dialog.show();
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
