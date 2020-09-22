package com.app.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.app.classes.Station;



public class StationDataService {
	public static List<Map<String,Object>> getStations(Connection conn) throws SQLException{
		List<Map<String,Object>> rows = new ArrayList<>();
		Statement stations = conn.createStatement();
		ResultSet results = stations.executeQuery("SELECT * FROM stations");
		ResultSetMetaData columns = results.getMetaData();
		int columnCount = columns.getColumnCount();
		
		while(results.next()) {
			Map<String, Object> row = new HashMap<>();
			
			for (int i = 1; i <= columnCount; i++) {
		           String colName = columns.getColumnName(i);
		           Object colVal = results.getObject(i);
		           row.put(colName, colVal);
			}
			rows.add(row);
		}
		
		return rows;
	}
	
	public static List<Map<String,Object>> getStation(Connection conn, int id) throws SQLException{
		List<Map<String,Object>> rows = new ArrayList<>();
		Statement stations = conn.createStatement();
		ResultSet results = stations.executeQuery("SELECT * FROM stations WHERE stationid = " + id + ";");
		ResultSetMetaData columns = results.getMetaData();
		int columnCount = columns.getColumnCount();
		
		while(results.next()) {
			Map<String, Object> row = new HashMap<>();
			
			for (int i = 1; i <= columnCount; i++) {
		           String colName = columns.getColumnName(i);
		           Object colVal = results.getObject(i);
		           row.put(colName, colVal);
			}
			rows.add(row);
		}
		
		return rows;
	}
	
	public static int updateStationEntry(Connection conn, int id, Station station) throws SQLException{
		Statement insert = conn.createStatement();
		return insert.executeUpdate("UPDATE stations SET (name, latitude, longitude, altitude) = (" + station.getName() + ","
									+ station.getLatitude() + "," + station.getLongitude() + "," + station.getAltitude() + ")"); 
	}
	
	public static int createStationEntry(Connection conn, Station station) throws SQLException{
		Statement insert = conn.createStatement();
		return insert.executeUpdate("INSERT into stations VALUES(" + station.getName() + "," + station.getLatitude() + "," + station.getLongitude() + ","
								+ station.getAltitude()); 
	}
}
