package com.example.weatherappandroidclient.classes;

import java.io.Serializable;

public class SearchResultCard implements Serializable {
    private String cityName;
    private String state;
    private String stateID;
    private String zip;
    private double lat;
    private double lng;

    public SearchResultCard(String cityName, String state, String stateID, String zip, double lat, double lng){
        this.cityName = cityName;
        this.state = state;
        this.stateID = stateID;
        this.zip = zip;
        this.lat = lat;
        this.lng = lng;
    }

    public String getCityName() {
        return cityName;
    }

    public String getState() {
        return state;
    }

    public String getStateID() {
        return stateID;
    }

    public String getZip() {
        return zip;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
