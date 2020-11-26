package com.example.weatherappandroidclient.classes;

public class SearchResultCard {
    private String cityName;
    private String state;
    private String stateID;

    public SearchResultCard(String cityName, String cityNameASCII){
        this.cityName = cityName;
        this.cityNameASCII = cityNameASCII;
    }

    public String getCityName() {
        return cityName;
    }

}
