package com.example.weatherappandroidclient;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.example.weatherappandroidclient.classes.NWSForecast;
import com.example.weatherappandroidclient.classes.OnEventListener;
import com.example.weatherappandroidclient.classes.VolleyServerRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class HourlyForecastActivity extends Activity {

    public static String pointURL = CurrentWeatherActivity.pointURL;    // Get pointURL from previous activity
    private TextView forecastText;
    public static String forecastURL;
    ObjectMapper mapper = new ObjectMapper();
    ArrayList<NWSForecast> forecasts = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly_forecast);
        forecastText = findViewById(R.id.hourlyForecast);
        forecastText.setMovementMethod(new ScrollingMovementMethod());
        getForecastURL(pointURL);
    }

    public void getForecastURL(String url){
        VolleyServerRequest stringRequest = new VolleyServerRequest(getApplicationContext(), new OnEventListener() {
            @Override
            public void onSuccess(Object object) throws IOException {
                JsonNode response = (JsonNode)object;
                JsonNode forecastNode = response.path("properties").path("forecastHourly");
                forecastURL = forecastNode.textValue();
                getNWSForecast(forecastURL);
            }

            @Override
            public void onErrorResponse(VolleyError error) {

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
                JsonNode hourlyForecastNode = response.path("properties").path("periods");
                Iterator<JsonNode> forecastIterator = hourlyForecastNode.elements();

                while(forecastIterator.hasNext()){
                    NWSForecast currentForecast = mapper.treeToValue(forecastIterator.next(), NWSForecast.class);
                    forecasts.add(currentForecast);
                }
                String finalForecast = "";
                for(NWSForecast forecast : forecasts){
                    finalForecast += forecast.toString() + "\n";
                }
                forecastText.setText("Here's the hourly forecast: \n\n" + finalForecast);
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onFailure(Exception e) {

            }
        }, url);
    }
}
