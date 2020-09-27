package com.app.client;

import java.io.IOException;
import java.net.URL;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import com.app.classes.CurrentMeasurement;
import com.app.classes.DailyForecast;
import com.app.classes.HistoricalMeasurement;
import com.app.classes.HourlyForecast;
import com.app.classes.Station;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CommandLineClient {
	public static void main(String[] args) throws IOException{
		Client client = ClientBuilder.newClient();
		String baseURL = "http://localhost:8080/app/api";	// Base URL
		Scanner scanner = new Scanner(System.in);
		
		
		System.out.println("Welcome to the WeatherApp Command Line API Client.\nWhat would you like to do?");

		int input = 1;
		Response response;
		URL targetURL;
		ObjectMapper mapper = new ObjectMapper();
		JsonFactory factory = new JsonFactory();
		JsonParser parser;
		TypeFactory typeFactory = mapper.getTypeFactory();
		
		while(input != 0) {
			System.out.println("\n\nOptions: \n1. View station information \n2. View daily forecasts \n3. View hourly forecasts \n4. View historical weather measurements \n5. View current weather measurements");
			System.out.println("\n Please enter the number corresponding to the action you wish to take.");
			
			input = scanner.nextInt();
			if(input == 1) {
				System.out.println("\nMore options: \n\n 0. View info for all stations \n{id}. View info for a specific ID (enter the number corresponding to the ID)");
				input = scanner.nextInt();
				if(input == 0) {
					targetURL = new URL(baseURL + "/stations"); 
					List<Station> stations = mapper.readValue(targetURL,typeFactory.constructCollectionType(List.class, Station.class));
					System.out.println(stations.toString());
				}
				
				else {
					targetURL = new URL(baseURL + "/stations/" + input);
					Station station = mapper.readValue(targetURL, Station.class);
					System.out.println(station.toString());
				}
			}
			else if(input == 2) {
				System.out.println("\nWhich station ID do you want to see daily forecasts for?");
				input = scanner.nextInt();
				targetURL = new URL(baseURL + "/stations/" + input + "/daily_forecasts");
				DailyForecast forecast = mapper.readValue(targetURL, DailyForecast.class);
				System.out.println(forecast.toString());
			}
			else if(input ==3) {
				System.out.println("\nWhich station ID do you want to see hourly forecasts for?");
				input = scanner.nextInt();
				targetURL = new URL(baseURL + "/stations/" + input + "/hourly_forecasts");
				HourlyForecast forecast = mapper.readValue(targetURL, HourlyForecast.class);
				System.out.println(forecast.toString());
			}
			else if(input == 4) {
				System.out.println("\nWhich station ID do you want to see historical measurements for?");
				input = scanner.nextInt();
				targetURL = new URL(baseURL + "/stations/" + input + "/historical_measurements");
				HistoricalMeasurement measurement = mapper.readValue(targetURL, HistoricalMeasurement.class);
				System.out.println(measurement.toString());
			}
			else if(input == 5) {
				System.out.println("\nWhich station ID do you want to see current measurements for?");
				input = scanner.nextInt();
				targetURL = new URL(baseURL + "/stations/" + input + "/current_measurements");
				CurrentMeasurement measurement = mapper.readValue(targetURL, CurrentMeasurement.class);
				System.out.println(measurement.toString());
			}
		}
	}
}
