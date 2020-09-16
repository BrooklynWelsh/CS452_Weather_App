package com.app.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.app.classes.CurrentMeasurement;

public class CurrentMeasurementResource {
	private CurrentMeasurement measurements;
	
	public CurrentMeasurementResource() {}
	public CurrentMeasurementResource (CurrentMeasurement measurements) {this.measurements = measurements;}
	
	public static CurrentMeasurement getCurrentMeasurements(Connection conn, int id) throws SQLException {
		Statement stations = conn.createStatement();
		ResultSet results = stations.executeQuery("SELECT * FROM measurements_current WHERE stationid = " + id + ";");
		
		results.next();
		// Create a DailyForecast object, and set the appropriate fields using the ResultSet
		CurrentMeasurement forecast = new CurrentMeasurement();
		forecast.setFTemp(results.getFloat("fahrenheit"));
		forecast.setCTemp(results.getFloat("celsius"));
		forecast.setWindSpeed(results.getFloat("wind_speed"));
		forecast.setWindGust(results.getFloat("wind_gust"));
		forecast.setPressure(results.getFloat("pressure"));
		forecast.setHumidity(results.getFloat("humidity"));
		forecast.setRain(results.getFloat("rain"));
		forecast.setCloud(results.getString("cloud"));
		forecast.setHeatIndex(results.getFloat("heat_index"));
		
		return forecast;
	}
}
