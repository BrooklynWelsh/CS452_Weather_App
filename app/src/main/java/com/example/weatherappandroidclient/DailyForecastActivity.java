package com.example.weatherappandroidclient;

import android.app.Activity;
import android.app.usage.ConfigurationStats;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.solver.widgets.Helper;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;

import com.example.weatherappandroidclient.classes.HelperFunctions;
import com.example.weatherappandroidclient.classes.NWSForecast;
import com.example.weatherappandroidclient.classes.OnEventListener;
import com.example.weatherappandroidclient.classes.VolleyServerRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.picasso.Picasso;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.RangeCategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;

public class DailyForecastActivity extends Activity {

    public static String pointURL = CurrentWeatherActivity.pointObject.getGridPointURL();    // Get pointURL from previous activity
    private TextView forecastText;
    public static String forecastURL;
    ObjectMapper mapper = new ObjectMapper();
    ArrayList<NWSForecast> forecasts = new ArrayList();
    JsonNode gridpointForecastNode = CurrentWeatherActivity.gridpointForecastNode;
    JsonNode detailedForecastNode = CurrentWeatherActivity.detailedForecastNode;
    public final int HOURS_IN_DAY = 24;
    public View lastCardView =  null;
    public String tempUnit = "F";
    public String speedUnit = "kmh";

    int screenHeight = CurrentWeatherActivity.screenHeight;
    int screenWidth = CurrentWeatherActivity.screenWidth;
    WindowManager window;
    Point size = new Point();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);
        window = getWindowManager();
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
        Picasso.get().load(R.drawable.weather_app_background).resize(screenWidth, screenHeight).onlyScaleDown().into(this.<ImageView>findViewById(R.id.background));
        getDailyForecastJSON(pointURL);
    }

    public void getDailyForecastJSON(String url) {
                JsonNode propertiesNode = detailedForecastNode.path("properties");
                // Now begin working on the daily forecast view
                int tempSize = propertiesNode.path("maxTemperature").path("values").size();
                Iterator<JsonNode> minTempIterator = propertiesNode.path("minTemperature").path("values").elements();

                XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
                RangeCategorySeries series =
                        new RangeCategorySeries("High low temperature");
                int highestTemp = 0;
                int lowestTemp = 100;
                int indexOfDuration = 0;
                for(int i = 0; i < tempSize; i++){
                    // For each max temp, get the matching min temp, then adjust the view accordingly
                    indexOfDuration = propertiesNode.path("maxTemperature").path("values").path(i).path("validTime").textValue().indexOf("/");
                    OffsetDateTime maxTempTimestamp = OffsetDateTime.parse(propertiesNode.path("maxTemperature").path("values").path(i).path("validTime").textValue().substring(0, indexOfDuration));
                    DateTimeFormatter format = DateTimeFormatter.ofPattern("EEEE MMMM,dd");
                    String formattedDate = format.format(maxTempTimestamp);
                    int maxTemp = HelperFunctions.convertToFahrenheit(Math.round(propertiesNode.path("maxTemperature").path("values").path(i).path("value").asDouble()));
                    int minTemp = HelperFunctions.convertToFahrenheit(Math.round(propertiesNode.path("minTemperature").path("values").path(i).path("value").asDouble()));

                    // Get more info for the day
                    // Get humidity
                    // Iterate through all humidity nodes for a 24 hr period, get average
                    // Be sure to parse durations for each timestamp to get accurate measure

                    Iterator<JsonNode> humidityValues = propertiesNode.path("relativeHumidity").path("values").elements();
                    int humidityAverage = (int) HelperFunctions.getDailyAverage(humidityValues, maxTempTimestamp);

                    Iterator<JsonNode> skyCoverIterator = propertiesNode.path("skyCover").path("values").elements();
                    int skyCover = (int) HelperFunctions.getDailyAverage(skyCoverIterator, maxTempTimestamp);

                    // Precipitation chance
                    Iterator<JsonNode> precipitationProbabilityIterator = propertiesNode.path("probabilityOfPrecipitation").path("values").elements();
                    int precipitationProbability = (int) HelperFunctions.getDailyAverage(precipitationProbabilityIterator, maxTempTimestamp);

                    // Precipitation quantity
                    Iterator<JsonNode> precipitationQuanityIterator = propertiesNode.path("quantitativePrecipitation").path("values").elements();
                    double precipitationQuantity = HelperFunctions.getDailyAverage(precipitationQuanityIterator, maxTempTimestamp);

                    // Wind speed
                    Iterator<JsonNode> windSpeedIterator = propertiesNode.path("windSpeed").path("values").elements();
                    double windSpeed = HelperFunctions.getDailyAverage(windSpeedIterator, maxTempTimestamp);

                    // Wind chill
                    Iterator<JsonNode> windChillIterator = propertiesNode.path("windChill").path("values").elements();
                    double windChill = HelperFunctions.getDailyAverage(windChillIterator, maxTempTimestamp);

                    // Dew point
                    Iterator<JsonNode> dewPointIterator = propertiesNode.path("dewpoint").path("values").elements();
                    double dewPoint = HelperFunctions.getDailyAverage(dewPointIterator, maxTempTimestamp);

                    // Wind gust
                    Iterator<JsonNode> windGustIterator =  propertiesNode.path("windGust").path("values").elements();
                    int windGust = (int) HelperFunctions.getDailyAverage(windGustIterator, maxTempTimestamp);

                    // Get highest and lowest apparent temperature
                    Iterator<JsonNode> apparentTemperatureIterator = propertiesNode.path("apparentTemperature").path("values").elements();
                    int lowestApparentTemp = HelperFunctions.convertToFahrenheit((int)HelperFunctions.getMin(apparentTemperatureIterator, maxTempTimestamp));
                    apparentTemperatureIterator = propertiesNode.path("apparentTemperature").path("values").elements(); // Have to reset the iterator
                    int highestApparentTemp = HelperFunctions.convertToFahrenheit((int)HelperFunctions.getMax(apparentTemperatureIterator, maxTempTimestamp));

                    // TODO: Also check for snowfall amount, ice accumulation

                    // TODO: check the "weather" node for short blurbs of weather events (scattered rain, chance of rain, etc.)

                    // TODO: Add code to decide on a weather icon for each day

                    // Create a card view to contain each day
                    LayoutInflater inflater = getLayoutInflater();
                    ViewGroup parent = findViewById(R.id.cardConstraintLayout);
                    ConstraintLayout layout = findViewById(R.id.cardConstraintLayout);
                    ConstraintSet constraints = new ConstraintSet();


                    if(lastCardView == null){
                        ConstraintLayout thisCard = (ConstraintLayout)inflater.inflate(R.layout.daily_forecast_card, layout, false);
                        thisCard.setId(View.generateViewId());
                        layout.addView(thisCard);
                        lastCardView = thisCard;

                        // Begin editing the views for this card
                        TextView dateView = thisCard.findViewById(R.id.dateView);
                        dateView.setText(formattedDate);

                        TextView lowView = thisCard.findViewById(R.id.lowTempView);
                        lowView.setText(String.valueOf(minTemp));

                        TextView highView = thisCard.findViewById(R.id.highTempView);
                        highView.setText(String.valueOf(maxTemp));

                        TextView rainProbabilityView = thisCard.findViewById(R.id.rainProbabilityView);
                        rainProbabilityView.setText(getString(R.string.rain_probability_daily, String.valueOf(precipitationProbability)));

                        TextView rainQuantityView = thisCard.findViewById(R.id.precipitationQuantityView);
                        rainQuantityView.setText(getString(R.string.rain_quantity_daily, Double.toString(precipitationQuantity)));

                        TextView cloudCoverView = thisCard.findViewById(R.id.cloudCoverView);
                        cloudCoverView.setText(getString(R.string.cloud_cover_daily, String.valueOf(skyCover)));

                        TextView highFeelsLikeView = thisCard.findViewById(R.id.highFeelsLikeView);
                        highFeelsLikeView.setText(getString(R.string.feels_like_high_daily,String.valueOf(highestApparentTemp), tempUnit));

                        TextView lowFeelsLikeView = thisCard.findViewById(R.id.lowFeelsLikeView);
                        lowFeelsLikeView.setText(getString(R.string.feels_like_low_daily,String.valueOf(lowestApparentTemp), tempUnit));

                        TextView humidityView = thisCard.findViewById(R.id.humidityView);
                        humidityView.setText(getString(R.string.humidity_daily, String.valueOf(humidityAverage)));

                        TextView windSpeedView = thisCard.findViewById(R.id.windSpeedView);
                        windSpeedView.setText(getString(R.string.wind_speed_daily, Double.toString(windSpeed), speedUnit));

                        TextView windChillView = thisCard.findViewById(R.id.windChillView);
                        windChillView.setText(getString(R.string.wind_chill_daily, String.valueOf((int)windChill), tempUnit));

                        TextView windGustView = thisCard.findViewById(R.id.windGustView);
                        windGustView.setText(getString(R.string.wind_gust_daily, Double.toString(windGust), speedUnit));

                        TextView dewPointView = thisCard.findViewById(R.id.dewPointView);
                        dewPointView.setText(getString(R.string.dew_point_daily, String.valueOf((int)dewPoint), tempUnit));

                    }
                    else{
                        // Else we need to attach this card to the bottom of the last card
                        ConstraintLayout thisCard = (ConstraintLayout)inflater.inflate(R.layout.daily_forecast_card, layout, false);
                        thisCard.setId(View.generateViewId());
                        layout.addView(thisCard);
                        constraints.clone(layout);

                        // Begin editing the views for this card
                        TextView dateView = thisCard.findViewById(R.id.dateView);
                        dateView.setText(formattedDate);

                        TextView lowView = thisCard.findViewById(R.id.lowTempView);
                        lowView.setText(String.valueOf(minTemp));

                        TextView highView = thisCard.findViewById(R.id.highTempView);
                        highView.setText(String.valueOf(maxTemp));

                        TextView rainProbabilityView = thisCard.findViewById(R.id.rainProbabilityView);
                        rainProbabilityView.setText(getString(R.string.rain_probability_daily, String.valueOf(precipitationProbability)));

                        TextView rainQuantityView = thisCard.findViewById(R.id.precipitationQuantityView);
                        rainQuantityView.setText(getString(R.string.rain_quantity_daily, Double.toString(precipitationQuantity)));

                        TextView cloudCoverView = thisCard.findViewById(R.id.cloudCoverView);
                        cloudCoverView.setText(getString(R.string.cloud_cover_daily, String.valueOf(skyCover)));

                        TextView highFeelsLikeView = thisCard.findViewById(R.id.highFeelsLikeView);
                        highFeelsLikeView.setText(getString(R.string.feels_like_high_daily,String.valueOf(highestApparentTemp), tempUnit));

                        TextView lowFeelsLikeView = thisCard.findViewById(R.id.lowFeelsLikeView);
                        lowFeelsLikeView.setText(getString(R.string.feels_like_low_daily,String.valueOf(lowestApparentTemp), tempUnit));

                        TextView humidityView = thisCard.findViewById(R.id.humidityView);
                        humidityView.setText(getString(R.string.humidity_daily, String.valueOf(humidityAverage)));

                        TextView windSpeedView = thisCard.findViewById(R.id.windSpeedView);
                        windSpeedView.setText(getString(R.string.wind_speed_daily, Double.toString(windSpeed), speedUnit));

                        TextView windChillView = thisCard.findViewById(R.id.windChillView);
                        windChillView.setText(getString(R.string.wind_chill_daily, String.valueOf((int)windChill), tempUnit));

                        TextView windGustView = thisCard.findViewById(R.id.windGustView);
                        windGustView.setText(getString(R.string.wind_gust_daily, Double.toString(windGust), speedUnit));

                        TextView dewPointView = thisCard.findViewById(R.id.dewPointView);
                        dewPointView.setText(getString(R.string.dew_point_daily, String.valueOf((int)dewPoint), tempUnit));

                        constraints.connect(thisCard.getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT, HelperFunctions.dpToPx(2, DailyForecastActivity.this));
                        constraints.connect(thisCard.getId(), ConstraintSet.TOP, lastCardView.getId(), ConstraintSet.BOTTOM, HelperFunctions.dpToPx(2, DailyForecastActivity.this));
                        constraints.applyTo(layout);
                        lastCardView = thisCard;
                    }
                }
            }
}
