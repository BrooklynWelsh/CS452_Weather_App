package com.example.weatherappandroidclient.classes;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class VolleyServerRequest {
    Context context;
    private OnEventListener<JsonNode> mCallBack;
    ObjectMapper mapper = new ObjectMapper();

    public  VolleyServerRequest(Context context, OnEventListener callback, String url){
        this.context = context;
        mCallBack = callback;
        setGetRequest(url);
    }

//    public void setGetRequest(String url){
//
//        RequestQueue queue = Volley.newRequestQueue(this.context);
//
//        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>()
//                {
//                    @Override
//                    public void onResponse(String response) {
//                        // response
//                        JsonNode json;
//                        String stationsURL = null;
//                        try {
//                            json = mapper.readTree(response);
//                            JsonNode propertyNode = json.path("properties");					// Properties field contains the forecast URLs (and more)
//                            JsonNode forecastNode = propertyNode.path("observationStations");	// First get the forecast URL string
//                            stationsURL = forecastNode.textValue();				// Convert to URL object and add to array
//                        } catch (JsonProcessingException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        Log.d("Response", response);
//                        try {
//                            mCallBack.onSuccess(stationsURL);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // error
//                        Log.d("Error.Response", error.toString());
//                        mCallBack.onFailure(error);
//                    }
//                }
//        );
//        queue.add(postRequest);
//    }

    public void setGetRequest(String url){

        RequestQueue queue = Volley.newRequestQueue(this.context);
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        JsonNode json = null;
                        String stationsURL = null;
                        try {
                            json = mapper.readTree(response);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.d("Response", response);
                        try {
                            mCallBack.onSuccess(json);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        mCallBack.onFailure(error);
                    }
                }
        );
        queue.add(postRequest);
    }
}

