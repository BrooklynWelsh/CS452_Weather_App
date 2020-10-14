package com.app.classes;

import java.net.URL;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// For the milestone, we only need some of the fields from the Point URL (there are A LOT)
// So this annotation lets us ignore the ones we don't specify
@JsonIgnoreProperties
public class Point {
	URL forecast;
	URL forecastHourly;
	URL stationsURL;
	
	public Point() {
	}
	
	public Point(URL forecastURL, URL forecastHourlyURL, URL stationsURL) {
		this.forecast = forecastURL;
		this.forecastHourly = forecastHourlyURL;
		this.stationsURL = stationsURL;
	}
	
	@Override
	public String toString() {
		return "Point [forecast=" + forecast + ", forecastHourly=" + forecastHourly + "]";
	}
	
	public URL getForecast() {
		return forecast;
	}
	public void setForecast(URL forecast) {
		this.forecast = forecast;
	}
	public URL getStationsURL() {
		return stationsURL;
	}

	public void setStationsURL(URL stationsURL) {
		this.stationsURL = stationsURL;
	}

	public URL getForecastHourly() {
		return forecastHourly;
	}
	public void setForecastHourly(URL forecastHourly) {
		this.forecastHourly = forecastHourly;
	}
}
