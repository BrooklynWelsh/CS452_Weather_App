package com.app.classes;

import java.io.Serializable;

public class Station implements Serializable{
	public Station() {
	}
	
	private String name;
	private float latitude;
	private float longitude;
	private int altitude;
	
	public String getName() { return name; }
	 public void setName(String name) { this.name = name; }
	 
	 public float getLatitude() { return latitude; }
	 public void setLastName(float latitude) { this.latitude= latitude; }
	 
	 public float getLongitude() {return longitude;}
	 public void setLongitude(float longitude) { this.longitude= longitude;}
	 
	 public int getAltitude() {return altitude;}
	 public void setAltitude(int altitude) {this.altitude = altitude;}
}
