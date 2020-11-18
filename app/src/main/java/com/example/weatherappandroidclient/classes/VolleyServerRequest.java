package com.example.weatherappandroidclient.classes;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
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
    final int TIMEOUT_LENGTH_MS = 9000;
    final int MAX_RETRIES = 3;

    public  VolleyServerRequest(Context context, OnEventListener callback, String url){
        this.context = context;
        mCallBack = callback;
        setGetRequest(url);
    }


    public void setGetRequest(String url){

        RequestQueue queue = Volley.newRequestQueue(this.context);
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        JsonNode json = null;
                        try {
                            json = mapper.readTree(response);
                        } catch (JsonProcessingException e) {
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
                        Toast.makeText(context, "There was a problem getting info from NWS. Please refresh.", Toast.LENGTH_LONG);
                    }
                }
        );
        postRequest.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_LENGTH_MS, MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }
}

