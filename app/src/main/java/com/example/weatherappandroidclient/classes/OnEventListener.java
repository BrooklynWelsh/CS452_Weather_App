package com.example.weatherappandroidclient.classes;


import java.io.IOException;

public interface OnEventListener<T> {
        public void onSuccess(T object) throws IOException;

        public void onFailure(Exception e);
    }

