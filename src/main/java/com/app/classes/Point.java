package com.app.classes;

import java.net.URL;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// For the milestone, we only need some of the fields from the Point URL (there are A LOT)
// So this annotation lets us ignore the ones we don't specify
@JsonIgnoreProperties
public class Point {
	URL forecast;
	URL forecastHourly;
	
	public Point() {
	}
	
	public Point(URL forecastURL, URL forecastHourlyURL) {
		this.forecast = forecastURL;
		this.forecastHourly = forecastHourlyURL;
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
	public URL getForecastHourly() {
		return forecastHourly;
	}
	public void setForecastHourly(URL forecastHourly) {
		this.forecastHourly = forecastHourly;
	}
}
