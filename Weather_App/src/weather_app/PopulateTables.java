package weather_app;

import java.sql.*;
import java.util.Scanner;
import java.util.Properties;
import java.util.Random;

public class PopulateTables {

	public static Connection establishConnection() throws SQLException {
		// Create a properties object to set the JSON properties that we will use during the connection request
		// Alternative is to just put all of these into the URL string in getConnection.
		Properties props = new Properties();
		props.setProperty("gssEncMode", "disable");
		props.setProperty("user", "pi");
		props.setProperty("password", "Bdw040795");		// Should probably find a way to not have password in plaintext
		props.setProperty("sslmode", "disable");
		Connection WeatherDB = DriverManager.getConnection("jdbc:postgresql://10.0.0.100:5433/weather_app", props);
		
		return WeatherDB;
	}
	
	public static void lookupInfo(Connection conn, int id) throws SQLException {
		// First get and print the station info for given id
		Statement stationInfo = conn.createStatement();
		ResultSet results = stationInfo.executeQuery("SELECT * from stations WHERE stationid = " + id + ';');
		ResultSetMetaData columns = results.getMetaData();
		String columnName;
		int currentColumn = 1;
		int numColumns = columns.getColumnCount();
		while(results.next()) {
			for(int i = 1; i <= numColumns; i++) {
				System.out.println(columns.getColumnName(i) + " : " + results.getString(i));
			}
			
		}
		results.close();
		stationInfo.close();
		System.out.println("\n\n");
		
		// Now give the current measurements for given station
		System.out.println("Here's the current measurements : ");
		Statement currentMeasurements = conn.createStatement();
		results = currentMeasurements.executeQuery("SELECT * from measurements_current WHERE stationid = " + id + ';');
		columns = results.getMetaData();
		currentColumn = 1;
		numColumns = columns.getColumnCount();
		while(results.next()) {
			for(int i = 1; i <= numColumns; i++) {
				System.out.println(columns.getColumnName(i) + " : " + results.getString(i));
			}
		}
		
		// Next get the forecast
		System.out.println("\n\nHere's the daily forecast : ");
		Statement forecast = conn.createStatement();
		results = forecast.executeQuery("SELECT * from forecast_daily WHERE stationid = " + id + ';');
		columns = results.getMetaData();
		currentColumn = 1;
		numColumns = columns.getColumnCount();
		while(results.next()) {
			for(int i = 1; i <= numColumns; i++) {
				System.out.println(columns.getColumnName(i) + " : " + results.getString(i));
			}
		}
		
		// Finally, get the historical weather 
		System.out.println("Here's some historical measurements : ");
		Statement historicalMeasurements = conn.createStatement();
		results = historicalMeasurements.executeQuery("SELECT * from measurements_historical WHERE stationid = " + id + ';');
		columns = results.getMetaData();
		currentColumn = 1;
		numColumns = columns.getColumnCount();
		while(results.next()) {
			for(int i = 1; i <= numColumns; i++) {
				System.out.println(columns.getColumnName(i) + " : " + results.getString(i));
			}
		}
	}
	
	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		Connection weatherDB = establishConnection();
		
		// Ask user for station ID
		Scanner scan = new Scanner(System.in);
		System.out.println("Which ID do you want to look up?");
		int input;
		if(scan.hasNextInt()) {
			input = scan.nextInt();
			
			if(input == 0) System.exit(0);
			else {
				lookupInfo(weatherDB,input);
			}
		}
		
	}
	

}
