package com.app.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.app.classes.HourlyForecast;

public class HourlyForecastResource {

	private HourlyForecast forecast;
	
	public HourlyForecastResource() {}
	public HourlyForecastResource (HourlyForecast forecast) {this.forecast = forecast;}
	
	public static HourlyForecast getForecast(Connection conn, int id) throws SQLException {
		
		List<Map<String,Object>> rows = new ArrayList<>();
		Statement stations = conn.createStatement();
		ResultSet results = stations.executeQuery("SELECT * FROM forecast_hourly WHERE stationid = " + id + ";");
		
		// Create a DailyForecast object, and set the appropriate fields using the ResultSet
		HourlyForecast forecast = new HourlyForecast();
		
		if(results.next() == false) {
			forecast = null;
		}
		else {
			forecast.setDate(results.getTime("hour").toLocalTime());
			forecast.setFTemp(results.getFloat("fahrenheit"));
			forecast.setCTemp(results.getFloat("celsius"));
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
