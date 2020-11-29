package com.example.weatherappandroidclient.classes;


import com.android.volley.VolleyError;

import java.io.IOException;

public interface OnEventListener<T> {
        public void onSuccess(T object) throws IOException;

    void onErrorResponse(VolleyError error);

    public void onFailure(Exception e);
    }

