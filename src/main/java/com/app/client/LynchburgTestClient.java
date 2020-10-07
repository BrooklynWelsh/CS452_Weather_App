package com.app.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.app.classes.NWSForecast;
import com.app.classes.Point;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
		
		Point lynchburgPoint = new Point(forecastURLs[0], forecastURLs[1]);
		
		// Lynchburg's grid ID is 'RNK' and is has a gridX,Y value of 101,78
		
		Iterator<JsonNode> days = getForecastJSON(lynchburgPoint.getForecast()); // First get weekly forecast JSON
		
		// Iterate through all children of the periodNode (which are the two forecasts for each day of the week)
		while(days.hasNext())
		{
			// Should be able to use Jackson here to create POJO
			NWSForecast currentForecast = mapper.treeToValue(days.next(), NWSForecast.class);
			System.out.println(currentForecast.toString());
		}
		
		System.out.println("\n\n\n");
		
		// Now get the hourly forecasts
		Iterator<JsonNode> hourlyDays = getForecastJSON(lynchburgPoint.getForecastHourly());
		
		while(hourlyDays.hasNext()) {
			NWSForecast currentForecast = mapper.treeToValue(hourlyDays.next(), NWSForecast.class);
			System.out.println(currentForecast.toString());
		}

	}
	
	public static Iterator<JsonNode> getForecastJSON(URL url) throws IOException{
		JsonNode json = mapper.readTree(url);
		JsonNode propertyNode = json.path("properties");
		JsonNode periodNode = propertyNode.path("periods");
		Iterator<JsonNode> days = periodNode.elements();
		
		return days;
	}
	
	public static URL[] getPointJSON(URL url) throws MalformedURLException{
		// For now we just want the forecast URLs, can edit to get more info
		URL[] forecastURLs = new URL[2];
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
		} catch (IOException e) {
			System.out.println("Sorry, but the NWS server returned the following error: " + e.getMessage());
		}
		
		return forecastURLs;
	}
}
