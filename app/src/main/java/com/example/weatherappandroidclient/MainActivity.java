package com.example.weatherappandroidclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherappandroidclient.classes.CurrentMeasurement;
import com.example.weatherappandroidclient.classes.DailyForecast;
import com.example.weatherappandroidclient.classes.HistoricalMeasurement;
import com.example.weatherappandroidclient.classes.HourlyForecast;
import com.example.weatherappandroidclient.classes.Station;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText userInput;
    private TextView info;
    private int station = 0;

    // Genymotion emulator uses 10.0.3.2 to refer to our ACTUAL localhost (since "localhost" on the emulator refers to the emulated phones localhost")
    // This is why we switch from localhost for the Android app
    private String baseURL = "http://10.0.3.2:8080/app/api/stations";  // Base URL for all requests
    private String targetURL = baseURL;

    Response response;
    ObjectMapper mapper = new ObjectMapper();
    JsonFactory factory = new JsonFactory();
    JsonParser parser;
    TypeFactory typeFactory = mapper.getTypeFactory();
    JsonArrayRequest jsonArrayRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context  context = getApplicationContext();

        // Set variable for the user input text field
        userInput = findViewById(R.id.stationID);

        // Set variable for the submit / back buttons
        final Button okButton = findViewById(R.id.okbutton);
        Button backButton = findViewById(R.id.back);
        // Ok button has tag for which function it needs to serve (either get station id initially or
        // get more info about selected station after the first input
        okButton.setTag("first");

        // Since the back button will act the same regardless of input, we can define the listener here
        backButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                recreate();
            }
        });

        // Set variable for TextView to show information and prompts to the user
        info = findViewById(R.id.info);
        info.setText("First, enter a station ID to choose which station you'd like to view info for." +
                     "\n\n If you'd rather just view station info for ALL stations in the database, submit \"0\"");

        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                RequestQueue queue = Volley.newRequestQueue(context);   // Create the Volley request queue for processing requests
                userInput = (EditText) findViewById(R.id.stationID);
                targetURL = baseURL;

                // 0 tag indicates the first "menu" in the app, i.e  choosing which station we want.
                if (okButton.getTag() == "first") {
                    station = Integer.parseInt(String.valueOf(userInput.getText()));
                    if (station == 0) {
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, baseURL,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            List<Station> stations = mapper.readValue(response, typeFactory.constructCollectionType(List.class, Station.class));
                                            info.setText("Here's a list of all of the stations in the database: \n\n " + stations.toString());
                                        } catch (JsonProcessingException e) {
                                            info.setText("There was an error converting the JSON into an object.");
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("VOLLEY", error.getMessage());
                                info.setText("That didn't work!");
                            }
                        });
                        queue.add(stringRequest);   // Add the request to the queue for processing
                    } else {
                        targetURL = baseURL + "/" + station;
                        StringRequest getStation = new StringRequest(Request.Method.GET, targetURL,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            Station station = mapper.readValue(response, Station.class);
                                            info.setText("Here's the information for the station with the ID you gave: \n\n" + station.toString() + "\n\n Here are more options for the selected station \n 1. Daily Forecast \n " +
                                                    "2. Hourly Forecast \n 3. Historical Measurements \n 4. Current Measurements");

                                            okButton.setTag("second");     // Update button tag so that next time it is clicked it executes the else block below
                                        } catch (JsonProcessingException e) {
                                            Log.e("JACKSON" , e.getMessage());
                                            info.setText("There was an error converting the JSON into an object.");
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("VOLLEY", error.getMessage());
                                info.setText("That didn't work!");
                            }
                        });
                        queue.add(getStation);
                    }
                }

                // A value of 1 for okButton's tag indicates we've selected a station, and now we need to display and process
                // the options for getting data from this specific station
                else if(okButton.getTag() == "second"){
                    if(Integer.parseInt(String.valueOf(userInput.getText())) == 1) {  // Daily forecast request
                        targetURL += "/" + station + "/daily_forecasts";
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, targetURL,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            DailyForecast forecast = mapper.readValue(response, DailyForecast.class);
                                            info.setText("Here's the daily forecast:  \n\n" + forecast.toString());
                                        } catch (JsonProcessingException e) {
                                            Log.e("JACKSON" , e.getMessage());
                                            info.setText("Sorry, there was a problem getting the information. Maybe there are no daily forecasts for this station yet!");
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                try {
                                    String responseBody = new String(error.networkResponse.data, "utf-8");
                                    info.setText("The server returned the following error:\n\n " + responseBody);
                                } catch (UnsupportedEncodingException except) {

                                }
                            }
                        });
                        queue.add(stringRequest);
                    }
                    else if(Integer.parseInt(String.valueOf(userInput.getText())) == 2) { // Hourly forecast request
                        targetURL += "/" + station + "/hourly_forecasts";
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, targetURL,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            HourlyForecast forecast = mapper.readValue(response, HourlyForecast.class);
                                            info.setText("Here's the hourly forecast:  \n\n" + forecast.toString());
                                        } catch (JsonProcessingException e) {
                                            Log.e("JACKSON" , e.getMessage());
                                            info.setText("Sorry, there was a problem getting the information. Maybe there are no daily forecasts for this station yet!");
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                try {
                                    String responseBody = new String(error.networkResponse.data, "utf-8");
                                    info.setText("The server returned the following error:\n\n " + responseBody);
                                } catch (UnsupportedEncodingException except) {

                                }
                            }
                        });
                        queue.add(stringRequest);
                    }
                    else if(Integer.parseInt(String.valueOf(userInput.getText())) == 3) {
                        targetURL += "/" + station + "/historical_measurements";
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, targetURL,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            HistoricalMeasurement measurement = mapper.readValue(response, HistoricalMeasurement.class);
                                            info.setText("Here's the historical measurements:  \n\n" + measurement.toString());
                                        } catch (JsonProcessingException e) {
                                            Log.e("JACKSON" , e.getMessage());
                                            info.setText("Sorry, there was a problem getting the information. Maybe there are no daily forecasts for this station yet!");
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                try {
                                    String responseBody = new String(error.networkResponse.data, "utf-8");
                                    info.setText("The server returned the following error:\n\n " + responseBody);
                                } catch (UnsupportedEncodingException except) {

                                }
                            }
                        });
                        queue.add(stringRequest);
                    }
                    else if(Integer.parseInt(String.valueOf(userInput.getText())) == 4) {
                        targetURL += "/" + station + "/current_measurements";
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, targetURL,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            CurrentMeasurement measurement  = mapper.readValue(response, CurrentMeasurement.class);
                                            info.setText("Here's the current measurements:  \n\n" + measurement.toString());
                                        } catch (JsonProcessingException e) {
                                            Log.e("JACKSON" , e.getMessage());
                                            info.setText("Sorry, there was a problem getting the information. Maybe there are no daily forecasts for this station yet!");
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                try {
                                    String responseBody = new String(error.networkResponse.data, "utf-8");
                                    info.setText("The server returned the following error:\n\n " + responseBody);
                                } catch (UnsupportedEncodingException except) {

                                }
                            }
                        });
                        queue.add(stringRequest);
                    }
                }
            }
        });
    }

}