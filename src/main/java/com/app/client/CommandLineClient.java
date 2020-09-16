package com.app.client;

import java.io.IOException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import java.util.Scanner;

public class CommandLineClient {
	public static void main(String[] args) throws IOException{
		Client client = ClientBuilder.newClient();
		String url = "http://localhost:8080/app/api";	// Base URL
		Scanner scanner = new Scanner(System.in);
		
		
		System.out.println("Welcome to the WeatherApp Command Line API Client.\nWhat would you like to do?");
		System.out.println("\n\nOptions: \n1. View station information \n2. View daily forecasts \n3. View hourly forecasts \n4. View historical weather measurements \n5. View current weather measurements");
		System.out.println("\n Please enter the number corresponding to the action you wish to take.");
		int input = scanner.nextInt();
		Response response;
		if(input == 1) {
			System.out.println("\nMore options: \n\n 0. View info for all stations \n{id}. View info for a specific ID (enter the number corresponding to the ID)");
			input = scanner.nextInt();
			if(input == 0) {
				response = client.target(url + "/stations").request().get();
				System.out.println("The API returned the following response:");
				System.out.println(response.toString());
				System.out.println("\nThe response body contained the following:");
				System.out.println(response.readEntity(String.class));
			}
			else {
				response = client.target(url + "/stations/" + input).request().get();
				System.out.println("The API returned the following response:");
				System.out.println(response.toString());
				System.out.println("\nThe response body contained the following:");
				System.out.println(response.readEntity(String.class));
			}
		}
		else if(input == 2) {
			System.out.println("\nWhich station ID do you want to see daily forecasts for?");
			input = scanner.nextInt();
			response = client.target(url + "/stations/" + input + "/daily_forecasts").request().get();
			System.out.println("The API returned the following response:");
			System.out.println(response.toString());
			System.out.println("\nThe response body contained the following:");
			System.out.println(response.readEntity(String.class));
		}
		else if(input ==3) {
			System.out.println("\nWhich station ID do you want to see hourly forecasts for?");
			input = scanner.nextInt();
			response = client.target(url + "/stations/" + input + "/hourly_forecasts").request().get();
			System.out.println("The API returned the following response:");
			System.out.println(response.toString());
			System.out.println("\nThe response body contained the following:");
			System.out.println(response.readEntity(String.class));
		}
		else if(input == 4) {
			System.out.println("\nWhich station ID do you want to see historical measurements for?");
			input = scanner.nextInt();
			response = client.target(url + "/stations/" + input + "/historical_measurements").request().get();
			System.out.println("The API returned the following response:");
			System.out.println(response.toString());
			System.out.println("\nThe response body contained the following:");
			System.out.println(response.readEntity(String.class));
		}
		else if(input == 5) {
			System.out.println("\nWhich station ID do you want to see current measurements for?");
			input = scanner.nextInt();
			response = client.target(url + "/stations/" + input + "/current_measurements").request().get();
			System.out.println("The API returned the following response:");
			System.out.println(response.toString());
			System.out.println("\nThe response body contained the following:");
			System.out.println(response.readEntity(String.class));
		}
	}
}
