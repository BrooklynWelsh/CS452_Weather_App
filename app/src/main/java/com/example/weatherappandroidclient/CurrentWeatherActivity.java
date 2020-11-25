package com.example.weatherappandroidclient;


import com.example.database.CityDatabase;
import com.example.database.DatabaseHelper;
import com.example.database.LatestMeasurementsDatabase;
import com.example.database.DatabaseClient;
import com.example.weatherappandroidclient.classes.City;
import com.example.weatherappandroidclient.classes.DetailedMeasurement;
import com.example.weatherappandroidclient.classes.HelperFunctions;
import com.example.weatherappandroidclient.classes.HourlyAdapter;
import com.example.weatherappandroidclient.classes.HourlyForecastCard;
import com.example.weatherappandroidclient.classes.NWSPoint;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionMenuView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;

import com.android.volley.toolbox.Volley;
import com.example.weatherappandroidclient.classes.NWSLatestMeasurements;
import com.example.weatherappandroidclient.classes.OnEventListener;
import com.example.weatherappandroidclient.classes.VolleyServerRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CurrentWeatherActivity extends AppCompatActivity {

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
    private RecyclerView hourlyRecyclerView;
    private SwipeRefreshLayout swipeRefresh;

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
    public static int screenHeight;
    public static int screenWidth;
    Point size = new Point();
    WindowManager window;
    ConstraintLayout layout;
    public static JsonNode detailedForecastNode;
    public static ArrayList<DetailedMeasurement> dailyForecastMeasuresList = new ArrayList<>();
    public static JsonNode gridpointForecastNode;
    ConstraintLayout view;

    final int buttonPressedColor = Color.parseColor("#9CD6F9");
    boolean todayButtonClicked = true;
    boolean dailyButtonClicked = false;
    // TODO: Database for city lookup requires a link back to  https://simplemaps.com/data/us-cities. Be sure to include.
    // TODO: Cloudy sky image requires attribution, need to make a "Licenses" page

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bottom_toolbar, menu);
        return true;
    }

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
        hourlyRecyclerView = findViewById(R.id.hourlyRecyclerView);
        view = findViewById(R.id.graphView);
        swipeRefresh = findViewById(R.id.swiperefresh);

        swipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        startLocationUpdates();
                        swipeRefresh.setRefreshing(false);
                    }
                }
        );

        temp.setTypeface(ResourcesCompat.getFont(this, R.font.opensans_bold));
        temp.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_large));
        background = findViewById(R.id.background);
        window = getWindowManager();


//        Room.databaseBuilder(getApplicationContext(), CityDatabase.class, "CityDB")
//                .addMigrations(new Migration(3, 4){
//                    @Override
//                    public void migrate(@NonNull SupportSQLiteDatabase database) {
//                        database.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS 'citiesFts' USING FTS4('cityName', 'stateName', 'stateId', content='cities')");
//                        database.execSQL("INSERT INTO citiesFts(citiesFts) VALUES ('rebuild')");
//                    }
//                })
//                .build();

        // TODO: Need to create DB in to apply changes

        DatabaseHelper helper = new DatabaseHelper(this, null, null, 1);
        try {
            helper.createDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SQLiteDatabase db = SQLiteDatabase.openDatabase("data/data/com.example.weatherappandroidclient/databases/CityDB.sqlite3",null,SQLiteDatabase.OPEN_READONLY);


        
//        // Test query
//        HandlerThread handlerThread = new HandlerThread("MyHandlerThread");
//        handlerThread.start();
//        Looper looper = handlerThread.getLooper();
//        Handler handler = new Handler(looper);
//
//        handler.post(() -> {
//                    List<City> cities = DatabaseClient.getInstance(getApplicationContext()).getCitiesDatabase().CityDAO().searchByCityName("Santa Monica");
//                    for(City city : cities){
//                        Log.d("RESULT", city.toString());
//                    }
//                });
//

        ActionMenuView bottomBar = findViewById(R.id.toolbar_bottom);
        Menu bottomMenu = bottomBar.getMenu();

        getMenuInflater().inflate(R.menu.bottom_toolbar, bottomMenu);

        // Initialize toolbar buttons
        ImageButton dailyButton = bottomBar.findViewById(R.id.dailyIcon);
        TextView dailyText = bottomBar.findViewById(R.id.dailyText);
        ImageButton todayButton = bottomBar.findViewById(R.id.todayIcon);
        TextView todayText = bottomBar.findViewById(R.id.todayText);

        todayButton.getDrawable().setTint(buttonPressedColor);
        todayText.setTextColor(buttonPressedColor);

        // Listener for home/hourly button
        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CLICK", "THE TODAY BUTTON WAS CLICKED");
                if(dailyButtonClicked == true) {                       // Only do anything if user is on another activity (i.e. is on the daily forecast)
                    todayButtonClicked = true;
                    todayButton.getDrawable().setTint(buttonPressedColor);
                    todayText.setTextColor(buttonPressedColor);

                    // Reset the dailyButton's attributes
                    dailyButton.getDrawable().setTint(Color.WHITE);
                    dailyText.setTextColor(Color.WHITE);
                    dailyButtonClicked = false;
                }
            }
        });

        // Listener for daily forecast button
        dailyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CLICK", "THE DAILY BUTTON WAS CLICKED");
                if(todayButtonClicked == true) {
                    dailyButtonClicked = true;
                    dailyButton.getDrawable().setTint(buttonPressedColor);
                    dailyText.setTextColor(buttonPressedColor);

                    // Reset the todayButton's attributes
                    todayButton.getDrawable().setTint(Color.WHITE);
                    todayText.setTextColor(Color.WHITE);
                    todayButtonClicked = false;
                }
            }
        });

        // We can just set the date now
        LocalDateTime currentDate = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, LLL dd hh:mm a");
        String dateString = currentDate.format(formatter);
        date.setText(dateString);

        // Get screen width and height (backwards compatible if else statement)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            window.getDefaultDisplay().getSize(size);
            screenWidth = size.x;
            screenHeight = size.y;
        } else {
            Display d = window.getDefaultDisplay();
            screenWidth = d.getWidth();
            screenHeight = d.getHeight();
        }

        // Use picasso library to resize background image appropriately (avoid bitmap too large errors)
        Picasso.get().load(R.drawable.weather_app_background).resize(screenWidth, screenHeight).onlyScaleDown().into(background);

        // Module needed for Jackson to construct Java OffsetDateTime fields
        mapper.registerModule(new JavaTimeModule());

        // First we need to get GPS coordinates
        // So make sure GPS is enabled. If not, show an alert.
        final LocationManager manager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
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
                    Log.d("LOCATION ERROR: ", "locationResult null");
                    return;
                }
                Log.d("LOCATION UPDATE: ", "requestLocationUpdates has received a new location. Calling \"onLocationResult\" callback.");
                Log.d(TAG, "received " + locationResult.getLocations().size() + " locations");
                for (Location loc : locationResult.getLocations()) {
                    latitude = loc.getLatitude();
                    longitude = loc.getLongitude();

                    // Now we can make request to NWS API
                    pointURL = "https://api.weather.gov/points/" + latitude + "," + longitude;
                    try {
                        getPointJSON(pointURL, true);              // First get stations endpoint URL from our existing point URL
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

    public void getPointJSON(String url, boolean needNewMeasurements) throws MalformedURLException {
        // For current measurements, we want to get the "observationStations" field and return the URL
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        VolleyServerRequest stringRequest = new VolleyServerRequest(getApplicationContext(), new OnEventListener() {
            @Override
            public void onSuccess(Object object) throws IOException {
                // We now have the correct stations URL, now read it and create a NWS Point POJO
                JsonNode response = (JsonNode) object;
                JsonNode propertyNode = response.path("properties");                    // Properties field contains the forecast URLs (and more)
                JsonNode stationsNode = propertyNode.path("observationStations");    // First get the forecast URL string
                JsonNode forecastNode = propertyNode.path("forecast");
                JsonNode hourlyForecastNode = propertyNode.path("forecastHourly");
                JsonNode gridPointNode = propertyNode.path("forecastGridData");
                pointObject = new NWSPoint(forecastNode.textValue(), hourlyForecastNode.textValue(), stationsNode.textValue(), gridPointNode.textValue(), response.path("id").textValue());


                getForecastJSON(pointObject.getForecast());                 // Get some forecast data from the forecast (not hourly) URL
                getHourlyForecastJSON(pointObject.getGridPointURL());       // Get data for hourly forecast card ("gridpoints" endpoint actually has the most detailed forecast...)
                if (needNewMeasurements)
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
                JsonNode response = (JsonNode) object;
                latestMeasurementsURL = response.path("observationStations").path(0).textValue() + "/observations/latest";  // Get station URL then append path end in order to get latest measures
                getLatestMeasurements(latestMeasurementsURL);

            }

            @Override
            public void onFailure(Exception e) {

            }
        }, url);
    }

    public void getLatestMeasurements(String url) throws IOException {
        VolleyServerRequest stringRequest = new VolleyServerRequest(getApplicationContext(), new OnEventListener() {
            @Override
            public void onSuccess(Object object) throws IOException {
                // We now have the measurements, so map it to the NWSLatestMeasurements POJO
                JsonNode response = (JsonNode) object;
                JsonNode propertyNode = response.path("properties");
                measurements = mapper.treeToValue(propertyNode, NWSLatestMeasurements.class);
                HandlerThread handlerThread = new HandlerThread("SaveMeasurementsToDBThread");
                handlerThread.start();
                Looper DBInsertLooper = handlerThread.getLooper();
                Handler DBInsertHandler = new Handler(DBInsertLooper);

                DBInsertHandler.post(() -> {
                    HelperFunctions.saveNWSLatestMeasurement(getApplicationContext(), measurements);
                });
                handlerThread.quitSafely();
                measurements.setNode(propertyNode);
                measurements.setCloudLayers();
                updateLatestMeasurementViews(measurements);
            }


            @Override
            public void onFailure(Exception e) {

            }
        }, url);
    }

    public void getForecastJSON(String url) {
        VolleyServerRequest stringRequest = new VolleyServerRequest(getApplicationContext(), new OnEventListener() {
            @Override
            public void onSuccess(Object object) throws IOException {
                gridpointForecastNode = (JsonNode) object;
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
                detailedForecastNode = (JsonNode) object;
                JsonNode propertiesNode = ((JsonNode) object).path("properties");
                updateHourlyViews(propertiesNode);


                // Now begin working on the daily forecast view
                int tempSize = propertiesNode.path("maxTemperature").path("values").size();
                Iterator<JsonNode> minTempIterator = propertiesNode.path("minTemperature").path("values").elements();
                XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
                mRenderer.setXLabels(0);
                final Typeface opensans = ResourcesCompat.getFont(getApplicationContext(), R.font.opensans_bold);
                mRenderer.setTextTypeface(opensans);
                RangeCategorySeries series =
                        new RangeCategorySeries("High low temperature");
                int highestTemp = 0;
                int lowestTemp = 100;
                for (int i = 0; i < tempSize; i++) {
                    // For each max temp, get the matching min temp, then adjust the view accordingly
                    int indexOfDuration = propertiesNode.path("maxTemperature").path("values").path(i).path("validTime").textValue().indexOf("/");
                    OffsetDateTime maxTempTimestamp = OffsetDateTime.parse(propertiesNode.path("maxTemperature").path("values").path(i).path("validTime").textValue().substring(0, indexOfDuration));
                    DayOfWeek day = maxTempTimestamp.getDayOfWeek();
                    int maxTemp = HelperFunctions.convertToFahrenheit(Math.round(propertiesNode.path("maxTemperature").path("values").path(i).path("value").asDouble()));
                    int minTemp = HelperFunctions.convertToFahrenheit(Math.round(propertiesNode.path("minTemperature").path("values").path(i).path("value").asDouble()));
                    if (maxTemp > highestTemp) highestTemp = maxTemp;
                    if (minTemp < lowestTemp) lowestTemp = minTemp;
                    series.add(maxTemp, minTemp);
                    mRenderer.addXTextLabel(i, day.toString());
                }

                if(view.getChildAt(1) == null) {    // A check to make sure we haven't already drawn the graph
                    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

                    dataset.addSeries(series.toXYSeries());

                    XYSeriesRenderer renderer = new XYSeriesRenderer();
                    mRenderer.addSeriesRenderer(renderer);
                    mRenderer.setPanEnabled(true, false);
                    mRenderer.setZoomEnabled(false, false);
                    mRenderer.setXAxisMin(1.5);
                    mRenderer.setXAxisMax(7.5);
                    double[] panLimits = {0, 7.9, 0, 0};
                    mRenderer.setPanLimits(panLimits);
                    mRenderer.setBarWidth(HelperFunctions.dpToPx(15, getApplicationContext()));
                    mRenderer.setLabelsTextSize(HelperFunctions.dpToPx(10, getApplicationContext()));
                    mRenderer.setAntialiasing(true);
                    mRenderer.setTextTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.opensans));
                    renderer.setShowLegendItem(false);
                    mRenderer.setShowLegend(false);
                    renderer.setChartValuesFormat(new DecimalFormat("#"));
                    renderer.setColor(Color.WHITE);
                    renderer.setChartValuesTextSize(HelperFunctions.dpToPx(9, getApplicationContext()));
                    mRenderer.setXLabelsColor(Color.WHITE);
                    mRenderer.setShowGridY(false);
                    mRenderer.setChartTitle("Weekly High-Low Temperatures");
                    int sideMargins = HelperFunctions.dpToPx(7, getApplicationContext());
                    mRenderer.setMargins(new int[]{HelperFunctions.dpToPx(25, getApplicationContext()),sideMargins,0,sideMargins});
                    mRenderer.setChartTitleTextSize(HelperFunctions.dpToPx(14, getApplicationContext()));
                    GraphicalView chartView = ChartFactory.getRangeBarChartView(
                            getApplicationContext(), dataset,
                            mRenderer, BarChart.Type.DEFAULT);
                    chartView.setId(View.generateViewId());
                    //ConstraintLayout view = findViewById(R.id.graphView);
                    view.setId(View.generateViewId());
                    chartView.setMinimumHeight(HelperFunctions.dpToPx(200, getApplicationContext()));
                    chartView.setMinimumWidth(HelperFunctions.dpToPx(200, getApplicationContext()));
                    view.addView(chartView);
                    view.removeView(findViewById(R.id.progress_bar));
                    renderer.setGradientEnabled(true);
                    renderer.setGradientStart(lowestTemp, Color.rgb(0, 57, 235));
                    renderer.setGradientStop(highestTemp + 5, Color.rgb(242, 96, 1));
                    renderer.setDisplayChartValues(true);
                }
            }


            @Override
            public void onFailure(Exception e) {

            }
        }, url);
    }

    private void startHourlyForecastActivity() {
        Intent intent = new Intent(this, HourlyForecastActivity.class);
        startActivity(intent);
    }

    private void startDailyForecastActivity() {
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

    public void updateHourlyViews(JsonNode propertiesNode) {
        // For now we'll get hourly forecast data for rest of today and tomorrow
        // So, while the timestamp isn't from two days from now, create a new measurement object

        // TODO: convert to horizontal cards and vertical scroll view (using RecyclerView?)
        // Use https://api.weather.gov/gridpoints/RNK/101,78 for more data, such as precip probability
        LocalDate currentDate = LocalDate.now();    // Get current date
        boolean pastLimit = false;

        // Get  "temperature" node
        // If timestamp is not more than a day out, go get "probabilityOfPrecipitation" node with matching timestamp
        // If precip node with matching timestamp doesn't exist, get value from last node

        Iterator<JsonNode> tempIterator = propertiesNode.path("temperature").path("values").elements();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("hh:mm a");

        // NEW FOR RECYCLER VIEW
        // We need to make a list of all of the card views first.
        ArrayList<HourlyForecastCard> cards = new ArrayList<HourlyForecastCard>();


        while (!pastLimit && tempIterator.hasNext()) {
            // Parse date from the next node
            JsonNode thisTempNode = tempIterator.next();
            int indexOfDuration = thisTempNode.path("validTime").textValue().indexOf("/");
            OffsetDateTime tempTime = OffsetDateTime.parse(thisTempNode.path("validTime").textValue().substring(0, indexOfDuration));
            if (ChronoUnit.DAYS.between(currentDate, tempTime) >= 2) pastLimit = true;
                // If date is not more than a day out, get probabilityOfPrecipitation
            else {
                int temperature = HelperFunctions.convertToFahrenheit((int) Math.round(thisTempNode.path("value").asDouble()));
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
                boolean foundSkyCover = false;
                while (skyCoverIterator.hasNext() && !foundSkyCover) {
                    JsonNode skyNode = skyCoverIterator.next();
                    indexOfDuration = skyNode.path("validTime").textValue().indexOf("/");
                    if (ChronoUnit.HOURS.between(tempTime, OffsetDateTime.parse(skyNode.path("validTime").textValue().substring(0, indexOfDuration))) > 0) {
                        // If we've went past the time for the temp node we are trying to match, just use the last known node
                        if (previousNode == null) {
                            skyCover = 0;
                        } else {
                            skyCover = previousNode.path("value").intValue();
                        }
                        foundSkyCover = true;
                    } else if (ChronoUnit.HOURS.between(tempTime, OffsetDateTime.parse(skyNode.path("validTime").textValue().substring(0, indexOfDuration))) == 0) {
                        skyCover = skyNode.path("value").intValue();
                        foundSkyCover = true;
                    }
                }

                // Visibility
//                Iterator<JsonNode> visibilityIterator = propertiesNode.path("visibility").path("values").elements();
//                int visibility;
//                OffsetDateTime lastValidTime = null;
//                boolean foundValue = false;
//
//                if(!visibilityIterator.hasNext()) {
//                    visibility = -1;    // Will put '???' in card view, no values available from NWS
//                    foundValue = true;
//                }
//
//                while(visibilityIterator.hasNext() && foundValue == false){
//                    JsonNode visibilityNode = visibilityIterator.next();
//                    indexOfDuration = visibilityNode.path("validTime").textValue().indexOf("/");
//                    OffsetDateTime thisNodeValidTime = OffsetDateTime.parse(visibilityNode.path("validTime").textValue().substring(0, indexOfDuration));
//                    if(!visibilityIterator.hasNext()) visibility = visibilityNode.path("value").intValue(); // No other nodes, get this node
//                    else if(lastValidTime != null && thisNodeValidTime.compareTo(lastValidTime) > 0){       // Else if we already have a previous value and this one is too far...
//                        visibility = visibilityNode.path("value").intValue();
//                        foundValue = true;
//                    }
//                    else lastValidTime = thisNodeValidTime;                                                   // Else, save this validTime and loop again
//                }

                // Try using generic helperFunction
                int visibility = (int)HelperFunctions.getHourlyValue(propertiesNode.path("visibility").path("values").elements(), tempTime);
                int apparentTemp = HelperFunctions.convertToFahrenheit((int)HelperFunctions.getHourlyValue(propertiesNode.path("apparentTemperature").path("values").elements(), tempTime));
                int humidity = (int)HelperFunctions.getHourlyValue(propertiesNode.path("relativeHumidity").path("values").elements(), tempTime);
                double pressure = HelperFunctions.getHourlyValue(propertiesNode.path("pressure").path("values").elements(), tempTime);
                int windSpeed = (int)HelperFunctions.getHourlyValue(propertiesNode.path("windSpeed").path("values").elements(), tempTime);
                int windDirection = (int)HelperFunctions.getHourlyValue(propertiesNode.path("windDirection").path("values").elements(), tempTime);
                int windChill = (int)HelperFunctions.getHourlyValue(propertiesNode.path("windChill").path("values").elements(), tempTime);
                int dewPoint = (int)HelperFunctions.getHourlyValue(propertiesNode.path("dewpoint").path("values").elements(), tempTime);

                HourlyForecastCard thisCard = new HourlyForecastCard(tempTime, temperature, apparentTemp, rainProb, windSpeed, windDirection, dewPoint, visibility, humidity, windChill,
                                                                        pressure, skyCover);
                cards.add(thisCard);
            }
        }

        // Now we have a complete list of card views
        hourlyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        HourlyAdapter adapter = new HourlyAdapter(cards);
        hourlyRecyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }

    public void updateLatestMeasurementViews(NWSLatestMeasurements measurementObject){

        // Add the measurements to the SQLite DB
        String cloudCover = measurementObject.getCloudLayers();
        HelperFunctions.setBackgroundImage(background, cloudCover, measurementObject.getPrecipitationLastHour(), isDaytime, screenWidth, screenHeight);
        Log.d(TAG, measurementObject.toString());

        // Want to make sure that degree symbol and unit are in smaller text and in superscript
        int fahrenheitTemp = (int) (measurementObject.getTemperature() / 5) * 9 + 32;
        String initialTempString = getString(R.string.temp, fahrenheitTemp, "F");
        int indexOfDegree = initialTempString.indexOf("°");
        SpannableString tempSpannable = SpannableString.valueOf(initialTempString);
        tempSpannable.setSpan(new SuperscriptSpan(), indexOfDegree, tempSpannable.length(), 0);
        tempSpannable.setSpan(new RelativeSizeSpan(0.3f), indexOfDegree, tempSpannable.length(), 0);
        temp.setText(tempSpannable);

        // Show heat index (feels like...)
        int heatIndexFahrenheit = (int) (measurementObject.getHeatIndex() / 5) * 9 + 32;
        String initialHeatIndexString = getString(R.string.feelsLike, heatIndexFahrenheit, "F");
        indexOfDegree = initialHeatIndexString.indexOf("°");
        SpannableString tempFeelsLikeSpannable = SpannableString.valueOf(initialHeatIndexString);
        tempFeelsLikeSpannable.setSpan(new SuperscriptSpan(), indexOfDegree, tempFeelsLikeSpannable.length(), 0);
        tempFeelsLikeSpannable.setSpan(new RelativeSizeSpan(0.5f), indexOfDegree, tempFeelsLikeSpannable.length(), 0);
        feel.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_small));
        feel.setText(tempFeelsLikeSpannable);

        // Get cloud cover and set image and text accordingly
        cloudDescription.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_small));
        cloudDescription.setText(measurementObject.getTextDescription());
        switch (measurementObject.getCloudLayers()) {
            case "OVC":
                cloudCoverIcon.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/wi_cloudy", null, getPackageName())));
                break;
            case "BKN":
                if (isDaytime)
                    cloudCoverIcon.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/wi_day_cloudy", null, getPackageName())));
                else
                    cloudCoverIcon.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/wi_night_cloudy", null, getPackageName())));
                break;
            case "FEW":
            case "SCT":
                if (isDaytime)
                    cloudCoverIcon.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/wi_day_cloudy_high", null, getPackageName())));
                else
                    cloudCoverIcon.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/wi_night_partly_cloudy", null, getPackageName())));
                break;
            default:
                if (isDaytime)
                    cloudCoverIcon.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/wi_day_sunny", null, getPackageName())));
                else
                    cloudCoverIcon.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/wi_night_clear", null, getPackageName())));
                break;

        }
        DrawableCompat.setTint(cloudCoverIcon.getDrawable(), Color.WHITE);
    }

    @Override
    protected void onResume() {
        // TODO: Graph draws a double of itself when navigated to from antoher activity via back button.
        super.onResume();
        HandlerThread handlerThread = new HandlerThread("MyHandlerThread");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        Handler handler = new Handler(looper);

        handler.post(() -> {
            NWSLatestMeasurements databaseResult = DatabaseClient.getInstance(getApplicationContext()).getLatestMeasurementsDatabase().NWSLatestMeasurementsDAO().getMeasurement();
            if (databaseResult == null || ChronoUnit.MINUTES.between(OffsetDateTime.now(), databaseResult.getTimestamp()) < 30) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                else {
                    Log.d("LOCATION REQUEST: ", "Couldn't find a valid database entry, getting GPS location.");
                    startLocationUpdates();
                }
            }
            else{
                // TODO: Could probably store latitude and longitude so that we don't need to check last known location for coordinates
                Log.d("DATABASE ENTRY FOUND: ", "Found a valid latest_measurements entry, using database info to populate views.");
                fusedClient.getLastLocation().addOnSuccessListener(CurrentWeatherActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null || ((int)location.getLongitude() == 0 && (int)location.getLatitude() == 0)) {
                            pointURL = "https://api.weather.gov/points/" + location.getLatitude() + "," + location.getLongitude();
                            //getForecastJSON(pointObject.getForecast());                 // Get some forecast data from the forecast (not hourly) URL
                            //getHourlyForecastJSON(pointObject.getGridPointURL());       // Get data for hourly forecast card ("gridpoints" endpoint actually has the most detailed forecast...)

                            CurrentWeatherActivity.this.runOnUiThread(new Runnable(){

                                @Override
                                public void run() {
                                    updateLatestMeasurementViews(databaseResult);
                                    try {
                                        getPointJSON(pointURL, false);
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                           Log.d("LOCATION ERROR: ", "GetLastLocation() returned a null or invalid location.");
                           startLocationUpdates();
                        }
                    }
                });
                // TODO: App doesn't seem to ask for GPS permissions, permenantly broken if user turns on the app without GPS on! (Keeps returning 0.0/0.0 coordinates).
                //updateLatestMeasurementViews(databaseResult);
            }
        });

    }

    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {
        fusedClient.removeLocationUpdates(mCallback);
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
