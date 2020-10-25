package com.example.weatherappandroidclient;


import com.example.weatherappandroidclient.classes.HelperFunctions;
import com.example.weatherappandroidclient.classes.NWSPoint;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.RequestQueue;

import com.android.volley.toolbox.Volley;
import com.example.weatherappandroidclient.classes.NWSLatestMeasurements;
import com.example.weatherappandroidclient.classes.OnEventListener;
import com.example.weatherappandroidclient.classes.VolleyServerRequest;
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
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.RangeCategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;

public class CurrentWeatherActivity extends Activity {

    private double latitude;
    private double longitude;

    // Views
    private View info;
    private TextView temp;
    private TextView feel;
    private TextView date;
    private TextView glance;
    private ImageView cloudCoverIcon;
    private TextView cloudDescription;
    private HorizontalScrollView hourlyScrollView;
    private ConstraintLayout hourlyScrollLayout;

    // Classes
    public static NWSPoint pointObject = new NWSPoint();

    private boolean isDaytime;
    private static final String TAG = "LocationActivity";
    private LocationCallback mCallback;
    FusedLocationProviderClient fusedClient;
    private LocationRequest mRequest;
    private final int PERMISSION_ACCESS_LOCATION = 1;
    static public String pointURL;
    static ObjectMapper mapper = new ObjectMapper();
    String latestMeasurementsURL;
    NWSLatestMeasurements measurements;
    static public ImageView background;
    int screenHeight;
    int screenWidth;
    Point size = new Point();
    WindowManager window;
    ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Picasso.get().setLoggingEnabled(true);

        // Get views for editing later
        setContentView(R.layout.activity_main);
        info = findViewById(R.id.info);
        temp = findViewById(R.id.temp);
        feel = findViewById(R.id.feel);
        date = findViewById(R.id.date);
        glance = findViewById(R.id.atAGlance);
        cloudCoverIcon = findViewById(R.id.cloudCoverIcon);
        cloudDescription = findViewById(R.id.cloudDescription);
        layout = findViewById(R.id.mainLayout);
        hourlyScrollView = findViewById(R.id.hourlyScrollView);
        hourlyScrollLayout = findViewById(R.id.hourlyScrollLayout);

        temp.setTypeface(ResourcesCompat.getFont(this, R.font.opensans_bold));
        temp.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_large));
        background = findViewById(R.id.background);
        window = getWindowManager();

        // We can just set the date now
        LocalDateTime currentDate = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, LLL dd hh:mm a");
        String dateString = currentDate.format(formatter);
        date.setText(dateString);

        // Get screen width and height (backwards compatible if else statement)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)    {
            window.getDefaultDisplay().getSize(size);
            screenWidth = size.x;
            screenHeight = size.y;
        }else{
            Display d = window.getDefaultDisplay();
            screenWidth = d.getWidth();
            screenHeight = d.getHeight();
        }

        // Use picasso library to resize background image appropriately (avoid bitmap too large errors)
        Picasso.get().load(R.drawable.weather_app_background).resize(screenWidth, screenHeight).onlyScaleDown().into(background);

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
                    pointURL = "https://api.weather.gov/points/" + latitude + "," + longitude;
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
                // We now have the correct stations URL, now read it and create a NWS Point POJO
                JsonNode response = (JsonNode)object;
                JsonNode propertyNode = response.path("properties");					// Properties field contains the forecast URLs (and more)
                JsonNode stationsNode = propertyNode.path("observationStations");	// First get the forecast URL string
                JsonNode forecastNode = propertyNode.path("forecast");
                JsonNode hourlyForecastNode = propertyNode.path("forecastHourly");
                JsonNode gridPointNode = propertyNode.path("forecastGridData");
                pointObject = new NWSPoint(forecastNode.textValue(), hourlyForecastNode.textValue(), stationsNode.textValue(), gridPointNode.textValue(), response.path("id").textValue());

                getForecastJSON(pointObject.getForecast());                 // Get some forecast data from the forecast (not hourly) URL
                getHourlyForecastJSON(pointObject.getGridPointURL());       // Get data for hourly forecast card ("gridpoints" endpoint actually has the most detailed forecast...)
                getMeasurementsURL(pointObject.getStationsURL());           // Get latest measurements URL from stations list



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
                // We now have the correct stations URL, now read it and select the first station for latest measurements
                JsonNode response = (JsonNode)object;
                latestMeasurementsURL = response.path("observationStations").path(0).textValue() + "/observations/latest";  // Get station URL then append path end in order to get latest measures
                getLatestMeasurements(latestMeasurementsURL);

            }

            @Override
            public void onFailure(Exception e) {

            }
        }, url);
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

                // Want to make sure that degree symbol and unit are in smaller text and in superscript
                int fahrenheitTemp = (int)(measurements.getTemperature() / 5) * 9 + 32;
                String initialTempString = getString(R.string.temp,fahrenheitTemp,"F");
                int indexOfDegree = initialTempString.indexOf("°");
                SpannableString tempSpannable = SpannableString.valueOf(initialTempString);
                tempSpannable.setSpan(new SuperscriptSpan(), indexOfDegree, tempSpannable.length(),0);
                tempSpannable.setSpan(new RelativeSizeSpan(0.3f), indexOfDegree, tempSpannable.length(),0);
                temp.setText(tempSpannable);

                // Show heat index (feels like...)
                int heatIndexFahrenheit = (int)(measurements.getHeatIndex() / 5) * 9 + 32;
                String initialHeatIndexString = getString(R.string.feelsLike,heatIndexFahrenheit,"F");
                indexOfDegree = initialHeatIndexString.indexOf("°");
                SpannableString tempFeelsLikeSpannable = SpannableString.valueOf(initialHeatIndexString);
                tempFeelsLikeSpannable.setSpan(new SuperscriptSpan(), indexOfDegree, tempFeelsLikeSpannable.length(),0);
                tempFeelsLikeSpannable.setSpan(new RelativeSizeSpan(0.5f), indexOfDegree, tempFeelsLikeSpannable.length(),0);
                feel.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_small));
                feel.setText(tempFeelsLikeSpannable);

                // Get cloud cover and set image and text accordingly
                cloudDescription.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_small));
                cloudDescription.setText(propertyNode.path("textDescription").textValue());
                switch(propertyNode.path("cloudLayers").path(0).path("amount").textValue()){
                    case "OVC":
                        cloudCoverIcon.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/wi_cloudy", null, getPackageName())));
                        break;
                    case "BKN":
                        if(isDaytime)   cloudCoverIcon.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/wi_day_cloudy", null, getPackageName())));
                        else            cloudCoverIcon.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/wi_night_cloudy", null, getPackageName())));
                        break;
                    case "FEW": case "SCT":
                        if(isDaytime)   cloudCoverIcon.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/wi_day_cloudy_high", null, getPackageName())));
                        else            cloudCoverIcon.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/wi_night_partly_cloudy", null, getPackageName())));
                        break;
                    default:
                        if(isDaytime)   cloudCoverIcon.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/wi_day_sunny", null, getPackageName())));
                        else            cloudCoverIcon.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/wi_night_clear", null, getPackageName())));
                        break;

                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        }, url);
    }

    public void getForecastJSON(String url){
        VolleyServerRequest stringRequest = new VolleyServerRequest(getApplicationContext(), new OnEventListener() {
            @Override
            public void onSuccess(Object object) throws IOException {
                JsonNode periodsNode = ((JsonNode) object).path("properties").path("periods");

                // Update isDaytime variable
                isDaytime = periodsNode.path(0).path("isDaytime").asBoolean();

                // Update the "atAGlance" textView with info from the first forecast node
                glance.setText(periodsNode.path(0).path("detailedForecast").textValue());

            }

            @Override
            public void onFailure(Exception e) {

            }
        }, url);
    }

    public void getHourlyForecastJSON(String url) {
        VolleyServerRequest stringRequest = new VolleyServerRequest(getApplicationContext(), new OnEventListener() {
                @Override
                public void onSuccess(Object object) throws IOException {
                    JsonNode propertiesNode = ((JsonNode) object).path("properties");

                    // For now we'll get hourly forecast data for rest of today and tomorrow
                    // So, while the timestamp isn't from two days from now, create a new measurement object

                    // Use https://api.weather.gov/gridpoints/RNK/101,78 for more data, such as precip probability
                    LocalDate currentDate = LocalDate.now();    // Get current date
                    boolean pastLimit = false;
                    // Get  "temperature" node
                    // If timestamp is not more than a day out, go get "probabilityOfPrecipitation" node with matching timestamp
                    // If precip node with matching timestamp doesn't exist, get value from last node

                    Iterator<JsonNode> tempIterator = propertiesNode.path("temperature").path("values").elements();
                    //int rainProb = 0;
                    TextView lastTempView = null;
                    TextView lastTimestampView = null;
                    ImageView lastCloudView = null;
                    TextView lastRainView = null;
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("hh:mm a");

                    while (!pastLimit && tempIterator.hasNext()) {
                        // Parse date from the next node
                        JsonNode thisTempNode = tempIterator.next();
                        int indexOfDuration = thisTempNode.path("validTime").textValue().indexOf("/");
                        OffsetDateTime tempTime = OffsetDateTime.parse(thisTempNode.path("validTime").textValue().substring(0, indexOfDuration));

                        if (ChronoUnit.DAYS.between(currentDate, tempTime) >= 2) pastLimit = true;
                            // If date is not more than a day out, get probabilityOfPrecipitation
                        else {
                            int temperature = (int) Math.round(thisTempNode.path("value").asDouble());
                            Iterator<JsonNode> precipitationIterator = propertiesNode.path("probabilityOfPrecipitation").path("values").elements();
                            JsonNode previousNode = null;
                            int rainProb = -1;
                            while (rainProb == -1 && precipitationIterator.hasNext()) {
                                JsonNode thisNode = precipitationIterator.next();
                                indexOfDuration = thisNode.path("validTime").textValue().indexOf("/");
                                if (ChronoUnit.HOURS.between(tempTime, OffsetDateTime.parse(thisNode.path("validTime").textValue().substring(0, indexOfDuration))) > 0) {
                                    // If we've went past the time for the temp node we are trying to match, just use the last known node
                                    if (previousNode == null) rainProb = 0;
                                    else rainProb = previousNode.path("value").intValue();
                                } else if (ChronoUnit.HOURS.between(tempTime, OffsetDateTime.parse(thisNode.path("validTime").textValue().substring(0, indexOfDuration))) == 0) {
                                    rainProb = thisNode.path("value").intValue();
                                }
                            }

                            // Now let's get cloud cover ("skyCover" node)
                            // 0-10% = Clear, 10 - 50% = Scattered, 50 - 90% = Broken, 90 - 100% = Overcast
                            Iterator<JsonNode> skyCoverIterator = propertiesNode.path("skyCover").path("values").elements();
                            int skyCover = 0;
                            while (skyCoverIterator.hasNext()) {
                                JsonNode skyNode = skyCoverIterator.next();
                                indexOfDuration = skyNode.path("validTime").textValue().indexOf("/");
                                if (ChronoUnit.HOURS.between(tempTime, OffsetDateTime.parse(skyNode.path("validTime").textValue().substring(0, indexOfDuration))) > 0) {
                                    // If we've went past the time for the temp node we are trying to match, just use the last known node
                                    if (previousNode == null) skyCover = 0;
                                    else skyCover = previousNode.path("value").intValue();
                                } else if (ChronoUnit.HOURS.between(tempTime, OffsetDateTime.parse(skyNode.path("validTime").textValue().substring(0, indexOfDuration))) == 0) {
                                    skyCover = skyNode.path("value").intValue();
                                }
                            }
                            // Finally, create a view with all of the info we just gathered
                            // Store last created view in a variable, so that we can constrain the next one to it
                            TextView timeStamp = new TextView(CurrentWeatherActivity.this);
                            timeStamp.setId(View.generateViewId());
                            ImageView skyCoverIcon = new ImageView(CurrentWeatherActivity.this);
                            skyCoverIcon.setId(View.generateViewId());
                            TextView precipitationChanceView = new TextView(CurrentWeatherActivity.this);
                            precipitationChanceView.setId(View.generateViewId());
                            if (lastTimestampView == null) {
                                // If that variable is null, the view must be the first, so we know to position it to the left
                                // Temperature text view
                                ConstraintSet constraints = new ConstraintSet();
                                hourlyScrollLayout.addView(timeStamp);
                                constraints.clone(hourlyScrollLayout);

                                timeStamp.setText(fmt.format(tempTime));
                                constraints.connect(timeStamp.getId(), ConstraintSet.LEFT, findViewById(R.id.hourlyScrollView).getId(), ConstraintSet.LEFT, dpToPx(2, CurrentWeatherActivity.this));
                                constraints.connect(timeStamp.getId(), ConstraintSet.TOP, findViewById(R.id.hourlyScrollView).getId(), ConstraintSet.TOP, dpToPx(2, CurrentWeatherActivity.this));
                                lastTimestampView = timeStamp;

                                // Cloud cover icon ImageView
                                hourlyScrollLayout.addView(skyCoverIcon);
                                skyCoverIcon.setScaleType(ImageView.ScaleType.FIT_XY);
                                skyCoverIcon.getLayoutParams().height = dpToPx(70, CurrentWeatherActivity.this);
                                skyCoverIcon.getLayoutParams().width = dpToPx(70, CurrentWeatherActivity.this);
                                constraints.clone(hourlyScrollLayout);
                                setCloudIcon(skyCoverIcon, skyCover, tempTime);
                                constraints.connect(skyCoverIcon.getId(), ConstraintSet.LEFT, findViewById(R.id.hourlyScrollView).getId(), ConstraintSet.LEFT, dpToPx(2, CurrentWeatherActivity.this));
                                constraints.connect(skyCoverIcon.getId(), ConstraintSet.TOP, timeStamp.getId(), ConstraintSet.BOTTOM, dpToPx(1, CurrentWeatherActivity.this));
                                constraints.applyTo(hourlyScrollLayout);
                                lastCloudView = skyCoverIcon;

                                // Finally, the  precipitation probability TextView
                                hourlyScrollLayout.addView(precipitationChanceView);
                                constraints.clone(hourlyScrollLayout);
                                precipitationChanceView.setText(getString(R.string.rain_chance,String.valueOf(rainProb)));
                                precipitationChanceView.setWidth(skyCoverIcon.getLayoutParams().width);
                                precipitationChanceView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                constraints.connect(precipitationChanceView.getId(), ConstraintSet.LEFT, skyCoverIcon.getId(), ConstraintSet.LEFT, 0);
                                constraints.connect(precipitationChanceView.getId(), ConstraintSet.TOP, skyCoverIcon.getId(), ConstraintSet.BOTTOM, dpToPx(1, CurrentWeatherActivity.this));
                                lastRainView = precipitationChanceView;
                                constraints.applyTo(hourlyScrollLayout);
                            } else {
                                // Else, we need to constrain our next views relative to the last view (to the right of last view, as opposed to constraining to the beginning of the scrollView)
                                ConstraintSet constraints = new ConstraintSet();

                                hourlyScrollLayout.addView(timeStamp);
                                constraints.clone(hourlyScrollLayout);
                                timeStamp.setText(fmt.format(tempTime));
                                constraints.connect(timeStamp.getId(), ConstraintSet.LEFT, lastCloudView.getId(), ConstraintSet.RIGHT, dpToPx(50, CurrentWeatherActivity.this));
                                constraints.connect(timeStamp.getId(), ConstraintSet.TOP, findViewById(R.id.hourlyScrollView).getId(), ConstraintSet.TOP, dpToPx(2, CurrentWeatherActivity.this));
                                constraints.applyTo(hourlyScrollLayout);
                                lastTimestampView = timeStamp;

                                hourlyScrollLayout.addView(skyCoverIcon);
                                skyCoverIcon.setScaleType(ImageView.ScaleType.FIT_XY);
                                skyCoverIcon.getLayoutParams().height = dpToPx(70, CurrentWeatherActivity.this);
                                skyCoverIcon.getLayoutParams().width = dpToPx(70, CurrentWeatherActivity.this);
                                constraints.clone(hourlyScrollLayout);
                                setCloudIcon(skyCoverIcon, skyCover, tempTime);
                                constraints.connect(skyCoverIcon.getId(), ConstraintSet.LEFT, lastCloudView.getId(), ConstraintSet.RIGHT, dpToPx(50, CurrentWeatherActivity.this));
                                constraints.connect(skyCoverIcon.getId(), ConstraintSet.TOP, timeStamp.getId(), ConstraintSet.BOTTOM, dpToPx(1, CurrentWeatherActivity.this));
                                constraints.applyTo(hourlyScrollLayout);
                                lastCloudView = skyCoverIcon;

                                hourlyScrollLayout.addView(precipitationChanceView);
                                constraints.clone(hourlyScrollLayout);
                                precipitationChanceView.setText(getString(R.string.rain_chance,String.valueOf(rainProb)));
                                precipitationChanceView.setWidth(skyCoverIcon.getLayoutParams().width);
                                precipitationChanceView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                constraints.connect(precipitationChanceView.getId(), ConstraintSet.LEFT, skyCoverIcon.getId(), ConstraintSet.LEFT, 0);
                                constraints.connect(precipitationChanceView.getId(), ConstraintSet.TOP, skyCoverIcon.getId(), ConstraintSet.BOTTOM, dpToPx(1, CurrentWeatherActivity.this));
                                lastRainView = precipitationChanceView;
                                constraints.applyTo(hourlyScrollLayout);
                            }

                        }
                    }

                    // Now begin working on the daily forecast view
                    int tempSize = propertiesNode.path("maxTemperature").path("values").size();
                    Iterator<JsonNode> minTempIterator = propertiesNode.path("minTemperature").path("values").elements();

                    XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
                    RangeCategorySeries series =
                            new RangeCategorySeries("High low temperature");
                    int highestTemp = 0;
                    int lowestTemp = 100;
                    for(int i = 0; i < tempSize; i++){
                        // For each max temp, get the matching min temp, then adjust the view accordingly
                        int indexOfDuration = propertiesNode.path("maxTemperature").path("values").path(i).path("validTime").textValue().indexOf("/");
                        OffsetDateTime maxTempTimestamp = OffsetDateTime.parse(propertiesNode.path("maxTemperature").path("values").path(i).path("validTime").textValue().substring(0, indexOfDuration));
                        DayOfWeek day = maxTempTimestamp.getDayOfWeek();
                        int maxTemp = HelperFunctions.convertToFahrenheit(Math.round(propertiesNode.path("maxTemperature").path("values").path(i).path("value").asDouble()));
                        int minTemp = HelperFunctions.convertToFahrenheit(Math.round(propertiesNode.path("minTemperature").path("values").path(i).path("value").asDouble()));
                        if(maxTemp > highestTemp) highestTemp = maxTemp;
                        if(minTemp < lowestTemp) lowestTemp = minTemp;
                        series.add(maxTemp,minTemp);
                        mRenderer.addXTextLabel(i, day.toString());
                    }

                    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

                    dataset.addSeries(series.toXYSeries());

                    XYSeriesRenderer renderer = new XYSeriesRenderer();
                    mRenderer.addSeriesRenderer(renderer);
                    mRenderer.setPanEnabled(true, false);
                    double[] panLimits = {0, 7.9, 0,0};
                    mRenderer.setPanLimits(panLimits);
                    mRenderer.setBarWidth(HelperFunctions.dpToPx(15, getApplicationContext()));
             //       mRenderer.setInScroll(true);
                    mRenderer.setLabelsTextSize(dpToPx(8, getApplicationContext()));
                    renderer.setChartValuesTextSize(12);
                    mRenderer.setAntialiasing(true);
                    mRenderer.setTextTypeface(ResourcesCompat.getFont(getApplicationContext(),R.font.opensans));
                    renderer.setShowLegendItem(false);
                    mRenderer.setShowLegend(false);
                    renderer.setChartValuesFormat(new DecimalFormat("#"));
                    renderer.setColor(Color.WHITE);
                    renderer.setChartValuesTextSize(dpToPx(9, getApplicationContext()));
                    mRenderer.setXLabelsColor(Color.WHITE);
                    mRenderer.setShowGridY(false);
                    mRenderer.setShowGridY(false);
                    mRenderer.setChartTitle("Weekly High-Low Temperatures");
                    mRenderer.setChartTitleTextSize(dpToPx(14, getApplicationContext()));
                    GraphicalView chartView = ChartFactory.getRangeBarChartView(
                            getApplicationContext(), dataset,
                            mRenderer, BarChart.Type.DEFAULT);

                    ConstraintLayout view = findViewById(R.id.graphView);
                    chartView.setMinimumHeight(dpToPx(200, getApplicationContext()));
                    chartView.setMinimumWidth(dpToPx(200, getApplicationContext()));
                    view.addView(chartView);
                    view.removeView(findViewById(R.id.progress_bar));
                    renderer.setGradientEnabled(true);
                    renderer.setGradientStart(lowestTemp + 10, Color.rgb(52,52,235));
                    renderer.setGradientStop(highestTemp + 10, Color.rgb(255,20,20));
                    renderer.setDisplayChartValues(true);
                }

                    @Override
                    public void onFailure(Exception e) {

                    }
                }, url);
        }

        private void setCloudIcon(ImageView cloudImageView, int cloudCover, OffsetDateTime time) {
            // 0-10% = Clear, 10 - 50% = Scattered, 50 - 90% = Broken, 90 - 100% = Overcast

                if(cloudCover > 90) cloudImageView.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/wi_cloudy", null, getPackageName())));
                else if(cloudCover < 90 && cloudCover >= 50) {
                    if (time.getHour() < 18 && time.getHour() > 6)
                        cloudImageView.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/wi_day_cloudy", null, getPackageName())));
                    else
                        cloudImageView.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/wi_night_cloudy", null, getPackageName())));
                }
                if(cloudCover < 50 && cloudCover >= 10) {
                    if (time.getHour() < 18 && time.getHour() > 6)
                        cloudImageView.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/wi_day_cloudy_high", null, getPackageName())));
                    else
                        cloudImageView.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/wi_night_partly_cloudy", null, getPackageName())));
                }
                else {
                    if (time.getHour() < 18 && time.getHour() > 6)
                        cloudImageView.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/wi_day_sunny", null, getPackageName())));
                    else
                        cloudImageView.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/wi_night_clear", null, getPackageName())));
                }
        }

    private int dpToPx(int dp, Context context) {
            // Java functions only take pixel measurements, this is for converting to the more useful dp measurement
            float density = context.getResources()
                    .getDisplayMetrics()
                    .density;
            return Math.round((float) dp * density);
        }

        private int convertToFahrenheit(double celsius){
            int fahrenheit = (int)(celsius / 5) * 9 + 32;
            return fahrenheit;
        }


    private void startHourlyForecastActivity(View view){
        Intent intent = new Intent(this, HourlyForecastActivity.class);
        startActivity(intent);
    }

    private void startWeeklyForecastActivity(View view){
        Intent intent = new Intent(this, DailyForecastActivity.class);
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
