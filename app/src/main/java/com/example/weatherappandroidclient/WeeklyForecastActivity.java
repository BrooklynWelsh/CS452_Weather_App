package com.example.weatherappandroidclient;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherappandroidclient.classes.NWSForecast;
import com.example.weatherappandroidclient.classes.OnEventListener;
import com.example.weatherappandroidclient.classes.VolleyServerRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class WeeklyForecastActivity extends AppCompatActivity {

    public static String pointURL = CurrentWeatherActivity.pointURL;    // Get pointURL from previous activity
    private TextView forecastText;
    public static String forecastURL;
    ObjectMapper mapper = new ObjectMapper();
    ArrayList<NWSForecast> forecasts = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_forecast);
        forecastText = findViewById(R.id.weeklyForecast);
        forecastText.setMovementMethod(new ScrollingMovementMethod());
        getForecastURL(pointURL);
    }

    public void getForecastURL(String url){
        VolleyServerRequest stringRequest = new VolleyServerRequest(getApplicationContext(), new OnEventListener() {
            @Override
            public void onSuccess(Object object) throws IOException {
                JsonNode response = (JsonNode)object;
                JsonNode forecastNode = response.path("properties").path("forecast");
                forecastURL = forecastNode.textValue();
                getNWSForecast(forecastURL);
            }

            @Override
            public void onFailure(Exception e) {

            }
        }, url);
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
}
