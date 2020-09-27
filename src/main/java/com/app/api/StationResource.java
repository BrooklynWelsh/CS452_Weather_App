package com.app.api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.app.classes.CurrentMeasurement;
import com.app.classes.DailyForecast;
import com.app.classes.HistoricalMeasurement;
import com.app.classes.HourlyForecast;
import com.app.classes.Station;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
@Path("stations")
public class StationResource {
	
	public static Connection establishConnection() {
		// Create a properties object to set the JSON properties that we will use during the connection request
		// Alternative is to just put all of these into the URL string in getConnection.
		Properties props = new Properties();
		props.setProperty("gssEncMode", "disable");
		props.setProperty("user", "pi");
		props.setProperty("password", "Bdw040795"); // Should probably find a way to not have password in plaintext
		props.setProperty("sslmode", "disable");
		Connection WeatherDB;
		try {
		WeatherDB = DriverManager.getConnection("jdbc:postgresql://10.0.0.100:5433/weather_app", props);
		return WeatherDB;
		} catch (SQLException e) {
		e.printStackTrace();
		WeatherDB = null;
		return WeatherDB;
		}
	}

		// Connect to the postgres DB
		Connection weatherDB = establishConnection();

		
	/*											*
	 * 											*
	 * 				Begin GET functions			*	
	 * 											*
	 * 											*
	 */										
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStations() throws SQLException, JsonProcessingException{		
		Connection connection = establishConnection();
		List<Map<String,Object>> items = StationDataService.getStations(connection);
		if(items.isEmpty()) {
			return Response.status(Response.Status.NO_CONTENT).entity("Sorry, but we couldn't find any stations in the database.").build();
		}
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(items);
		return Response.ok(json, MediaType.APPLICATION_JSON).build();

	}
	
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStation(@PathParam("id") int id) throws SQLException, JsonProcessingException {
		Connection conn = establishConnection();
		Map<String,Object> items = StationDataService.getStation(conn, id);
		
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(items);
		return Response.ok(json, MediaType.APPLICATION_JSON).build(); 
	}
	
	@GET
	 @Path("{id}/daily_forecasts")
	    public Response getDailyForecastResource(@PathParam("id") int id) throws SQLException, JsonProcessingException {
		 	Connection conn = establishConnection();
	        DailyForecast forecast =  DailyForecastResource.getForecast(conn,id);
	        
	        if(forecast == null) {
	        	return Response.status(Response.Status.NOT_FOUND).entity("Sorry, but there are no daily forecasts in our database for the station with that ID.").type(MediaType.TEXT_PLAIN).build();
	        }
	        else {
		        ObjectMapper mapper = new ObjectMapper();
		        String json = mapper.writeValueAsString(forecast);
		        return Response.ok(json, MediaType.APPLICATION_JSON).build();
	        }
	    }

	@GET
	@Path("{id}/hourly_forecasts")
	public Response getHourlyForecastResource(@PathParam("id") int id) throws SQLException, JsonProcessingException{
		Connection conn = establishConnection();
        HourlyForecast forecast = HourlyForecastResource.getForecast(conn,id);
        
        if(forecast == null) {
        	return Response.status(Response.Status.NOT_FOUND).entity("Sorry, but there are no hourly forecasts in our database for the station with that ID.").type(MediaType.TEXT_PLAIN).build();
        }
        else {
	        ObjectMapper mapper = new ObjectMapper();
	        String json = mapper.writeValueAsString(forecast);
	        return Response.ok(json, MediaType.APPLICATION_JSON).build();
        }
	}
	
	@GET
	@Path("{id}/historical_measurements")
	public Response getHistoricalMeasurements(@PathParam("id") int id) throws SQLException, JsonProcessingException{
		Connection conn = establishConnection();
		HistoricalMeasurement measures = HistoricalMeasurementResource.getHistoricalMeasurements(conn, id);
		
		if(measures == null) return Response.status(Response.Status.NOT_FOUND).entity("Sorry, but there are no historical measurements in our database for the station with that ID.").type(MediaType.TEXT_PLAIN).build();
		ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(measures);
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Path("{id}/current_measurements")
	public Response getCurrentMeasurements(@PathParam("id") int id) throws SQLException, JsonProcessingException{
		Connection conn = establishConnection();
		CurrentMeasurement measures = CurrentMeasurementResource.getCurrentMeasurements(conn, id);
		
		ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(measures);
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
	}
	
	/*											*
	 * 											*
	 * 				End GET functions			*	
	 * 											*
	 * 			   Begin PUT functions			*
	 */		
		
	
	@PUT
	@Path("{id}")
	public Response putStation(@PathParam("id") int id, Station update) throws SQLException, JsonProcessingException{
		Connection conn = establishConnection();
		Map<String,Object> stationMap = StationDataService.getStation(conn,id);
		ObjectMapper mapper = new ObjectMapper();
		Station currentStation = mapper.convertValue(stationMap.get(0),Station.class);
		int response = StationDataService.updateStationEntry(conn, id, update);
		if(response == 0) return Response.status(Status.INTERNAL_SERVER_ERROR).entity("UPDATE statment failed.").build();
		else return Response.ok().build();
	}
	
	/*											*
	 * 											*
	 * 				End PUT functions			*	
	 * 											*
	 * 				Begin POST functions		*
	 */	
	
	@POST
	public Response postStation(Station newStation) throws SQLException, JsonProcessingException{
		Connection conn = establishConnection();
		int response = StationDataService.createStationEntry(conn, newStation);
		if(response == 0) return Response.status(Status.INTERNAL_SERVER_ERROR).entity("INSERT statment failed.").build();
		else return Response.ok().build();
	} 
}
