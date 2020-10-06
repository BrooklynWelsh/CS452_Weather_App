package com.app.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.app.classes.HistoricalMeasurement;


public class HistoricalMeasurementResource {
	private HistoricalMeasurement forecast;
	public HistoricalMeasurementResource (HistoricalMeasurement forecast) {this.forecast = forecast;}

	
	public static HistoricalMeasurement getHistoricalMeasurements(Connection conn, int id) throws SQLException{
		Statement stations = conn.createStatement();
		ResultSet results = stations.executeQuery("SELECT * FROM measurements_historical WHERE stationid = " + id + ";");
		
		// Create a DailyForecast object, and set the appropriate fields using the ResultSet
		HistoricalMeasurement forecast = new HistoricalMeasurement();
		
		if(results.next() == false) forecast = null;
		else {
			forecast.setDate(results.getDate("measurement_date").toLocalDate());
			forecast.setFHigh(results.getFloat("fahrenheit_high"));
			forecast.setFLow(results.getFloat("fahrenheit_low"));
			forecast.setCHigh(results.getFloat("celsius_high"));
			forecast.setCLow(results.getFloat("celsius_low"));
			forecast.setWindSpeed(results.getFloat("wind_speed"));
			forecast.setWindGust(results.getFloat("wind_gust"));
			forecast.setPressure(results.getFloat("pressure"));
			forecast.setHumidity(results.getFloat("humidity"));
			forecast.setRain(results.getFloat("rain"));
			forecast.setCloud(results.getString("cloud"));
			forecast.setHeatIndex(results.getFloat("heat_index"));
			}
		
		return forecast;
	}
}
