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


public class HourlyForecastDataService {
	
	public static List<Map<String,Object>> getHourlyForecast(Connection conn, int id) throws SQLException{
		List<Map<String,Object>> rows = new ArrayList<>();
		Statement stations = conn.createStatement();
		ResultSet results = stations.executeQuery("SELECT * FROM forecast_hourly WHERE stationid = " + id + ";");
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
}
