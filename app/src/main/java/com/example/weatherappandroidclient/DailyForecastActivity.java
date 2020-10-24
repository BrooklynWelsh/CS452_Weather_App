package com.example.weatherappandroidclient;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.solver.widgets.Helper;

import com.example.weatherappandroidclient.classes.HelperFunctions;
import com.example.weatherappandroidclient.classes.NWSForecast;
import com.example.weatherappandroidclient.classes.OnEventListener;
import com.example.weatherappandroidclient.classes.VolleyServerRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;

public class DailyForecastActivity extends Activity {

    public static String pointURL = CurrentWeatherActivity.pointObject.getGridPointURL();    // Get pointURL from previous activity
    private TextView forecastText;
    public static String forecastURL;
    ObjectMapper mapper = new ObjectMapper();
    ArrayList<NWSForecast> forecasts = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);
        getNWSForecast(pointURL);
    }

    public void getNWSForecast(String url){
        VolleyServerRequest stringRequest = new VolleyServerRequest(getApplicationContext(), new OnEventListener() {
            @Override
            public void onSuccess(Object object) throws IOException {
                JsonNode response = (JsonNode)object;
                JsonNode weeklyForecastNode = response.path("properties").path("periods");
                Iterator<JsonNode> forecastIterator = weeklyForecastNode.elements();

                while(forecastIterator.hasNext()){
                    NWSForecast currentForecast = mapper.treeToValue(forecastIterator.next(), NWSForecast.class);
                    forecasts.add(currentForecast);
                }
                String finalForecast = "";
                for(NWSForecast forecast : forecasts){
                    finalForecast += forecast.toString() + "\n";
                }
                forecastText.setText("Here's the forecast for this week: \n\n" + finalForecast);
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
        // Now begin working on the daily forecast view
        int tempSize = propertiesNode.path("maxTemperature").path("values").size();
        Iterator<JsonNode> minTempIterator = propertiesNode.path("minTemperature").path("values").elements();

            }

    @Override
    public void onFailure(Exception e) {

    }
}, url);
        }


}
