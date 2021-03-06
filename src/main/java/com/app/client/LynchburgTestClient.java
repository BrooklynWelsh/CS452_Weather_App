package com.app.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.app.classes.NWSForecast;
import com.app.classes.NWSLatestMeasurements;
import com.app.classes.Point;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LynchburgTestClient {
	static ObjectMapper mapper = new ObjectMapper();
	public static void main(String[] args) throws IOException {
		// https://api.weather.gov/points/37.4138,-79.1422 gives us some data about Lynchburg, includign grid ID and gridX / gridY
		// The API wants a URL in the form of https://api.weather.gov/gridpoints/{office}/{grid X},{grid Y}/forecast in order to get forecasts
		
		// In order to get the office and grid X,Y, we need to access https://api.weather.gov/points/{latitude},{longitude}

		
		// Allowed to hard code coordinates for this milestone, will get from user in final application
		double latitude = 37.4128;
		double longitude = -79.146;
		
		// Create a URL from the coordinates given
		URL pointURL = new URL("https://api.weather.gov/points/" + latitude + "," + longitude);
		
		// Now convert JSON response into point object
		URL[] forecastURLs = getPointJSON(pointURL);	// Get desired forecast URLs, then create Point object
		
		Point lynchburgPoint = new Point(forecastURLs[0], forecastURLs[1], forecastURLs[2]);
		
		// Lynchburg's grid ID is 'RNK' and is has a gridX,Y value of 101,78
		
		List<NWSForecast> forecasts = getForecastJSON(lynchburgPoint.getForecast()); // First get weekly forecast JSON
		
		System.out.println("\n\n*************************\nBEGIN WEEKLY FORECAST\n*************************");
		for(NWSForecast forecast : forecasts) {
			System.out.println(forecast.toString() + "\n");
		}
	
		// Now get the hourly forecasts
		List<NWSForecast> hourlyForecasts = getForecastJSON(lynchburgPoint.getForecastHourly());
		System.out.println("\n\n*************************\nBEGIN HOURLY FORECAST\n*************************");
		for(NWSForecast forecast : hourlyForecasts) {
			System.out.println(forecast.toString() + "\n");
		}
		
		NWSLatestMeasurements measurements = getMeasurements(lynchburgPoint.getStationsURL());
		System.out.println("\n\n*************************\nCURRENT MEASUREMENTS\n*************************");
		System.out.println(measurements.toString());

	}
	
	public static List<NWSForecast> getForecastJSON(URL url) throws IOException{
		List<NWSForecast> forecasts = new ArrayList<NWSForecast>();
		JsonNode json = mapper.readTree(url);
		JsonNode propertyNode = json.path("properties");
		JsonNode periodNode = propertyNode.path("periods");
		Iterator<JsonNode> days = periodNode.elements();
		
		while(days.hasNext()) // TODO:  MOVE THESE WHILE LOOPS INTO getForecastJSON()
		{
			// Should be able to use Jackson here to create POJO
			NWSForecast currentForecast = mapper.treeToValue(days.next(), NWSForecast.class);
			forecasts.add(currentForecast);
		}
		
		return forecasts;
	}
	
	public static NWSLatestMeasurements getMeasurements(URL url) throws IOException {
		 JsonNode stationsResponse = mapper.readTree(url);
		 URL latestMeasurementsURL = new URL(stationsResponse.path("observationStations").path(0).textValue() + "/observations/latest");
         JsonNode measurementsResponse = mapper.readTree(latestMeasurementsURL);
         JsonNode propertyNode = measurementsResponse.path("properties");
         NWSLatestMeasurements measurements = mapper.treeToValue(propertyNode, NWSLatestMeasurements.class);
         return measurements;
	}
	
	public static URL[] getPointJSON(URL url) throws MalformedURLException{
		// For now we just want the forecast URLs, can edit to get more info
		URL[] forecastURLs = new URL[3];
		JsonNode json;
		
		try {
			json = mapper.readTree(url);
			
			JsonNode propertyNode = json.path("properties");					// Properties field contains the forecast URLs (and more)
			JsonNode forecastNode = propertyNode.path("forecast");				// First get the forecast URL string
			URL forecastURL = new URL(forecastNode.textValue());				// Convert to URL object and add to array
			forecastURLs[0] = forecastURL;										
			
			JsonNode forecastHourlyNode = propertyNode.path("forecastHourly");	// Now do same for the hourly forecasts
			URL forecastHourlyURL = new URL(forecastHourlyNode.textValue());
			forecastURLs[1] = forecastHourlyURL;	
			
			JsonNode stationsNode = propertyNode.path("observationStations");	// Now do same for the hourly forecasts
			URL stationsURL = new URL(stationsNode.textValue());
			forecastURLs[2] = stationsURL;	
		} catch (IOException e) {
			System.out.println("Sorry, but the NWS server returned the following error: " + e.getMessage());
		}
		
		return forecastURLs;
	}
}
