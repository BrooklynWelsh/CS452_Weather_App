package com.example.weatherappandroidclient.classes;

import java.net.URL;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// For the milestone, we only need some of the fields from the Point URL (there are A LOT)
// So this annotation lets us ignore the ones we don't specify
@JsonIgnoreProperties
public class NWSPoint {
	String forecast;
	String forecastHourly;
	String stationsURL;
	String gridPointURL;
	String pointURL;

	public NWSPoint() {
	}
	
	public NWSPoint(String forecastURL, String forecastHourlyURL, String stationsURL, String gridPointURL, String pointURL) {
		this.forecast = forecastURL;
		this.forecastHourly = forecastHourlyURL;
		this.stationsURL = stationsURL;
		this.gridPointURL = gridPointURL;
		this.pointURL = pointURL;
	}
	
	@Override
	public String toString() {
		return "Point [forecast=" + forecast + ", forecastHourly=" + forecastHourly + "]";
	}
	
	public String getForecast() {
		return forecast;
	}
	public String getStationsURL() {
		return stationsURL;
	}
	public String getForecastHourly() { return forecastHourly; }
	public String getGridPointURL() { return gridPointURL; }
	public String getPointURL() { return pointURL; }
}
