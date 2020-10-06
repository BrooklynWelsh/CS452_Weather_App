package com.app.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


import com.app.classes.DailyForecast;

public class DailyForecastResource {

	public DailyForecastResource (DailyForecast forecast) {}

	
	public static DailyForecast getForecast(Connection conn, int id) throws SQLException{
		Statement stations = conn.createStatement();
		ResultSet results = stations.executeQuery("SELECT * FROM forecast_daily WHERE stationid = " + id + ";");
	
		
		// Create a DailyForecast object, and set the appropriate fields using the ResultSet
		DailyForecast forecast = new DailyForecast();
		
		if(results.next() == false) {
			forecast = null;
		}
		else {
			forecast.setDate(results.getDate("day").toLocalDate());
			forecast.setFHigh(results.getFloat("fahrenheit_high"));
			forecast.setFLow(results.getFloat("fahrenheit_low"));
			forecast.setCHigh(results.getFloat("celsius_high"));
			forecast.setCLow(results.getFloat("celsius_low"));
			forecast.setWindSpeed(results.getFloat("wind_speed"));
			forecast.setWindGust(results.getFloat("wind_gust"));
			forecast.setPressure(results.getFloat("pressure"));
			forecast.setHumidity(results.getFloat("humidity"));
			forecast.setRainProb(results.getFloat("rain_probability"));
			forecast.setCloud(results.getString("cloud"));
			forecast.setHeatIndex(results.getFloat("heatindex"));
		}
		
		return forecast;
	}
}
