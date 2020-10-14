package com.app.classes;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NWSForecast {
	
	int number;
	String name;
	
	String startTime;
	String endTime;
	
	@JsonProperty("isDaytime")
	boolean isDaytime;
	int temperature;
	String temperatureUnit;
	int temperatureTrend;
	String windSpeed;
	String windDirection;
	String icon;
	String shortForecast;
	String detailedForecast;
	
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getWindSpeed() {
		return windSpeed;
	}
	public void setWindSpeed(String windSpeed) {
		this.windSpeed = windSpeed;
	}
	public String getWindDirection() {
		return windDirection;
	}
	public void setWindDirection(String windDirection) {
		this.windDirection = windDirection;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public boolean isDaytime() {
		return isDaytime;
	}
	public void setDaytime(boolean isDaytime) {
		this.isDaytime = isDaytime;
	}
	public int getTemperature() {
		return temperature;
	}
	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}
	public String getTemperatureUnit() {
		return temperatureUnit;
	}
	public void setTemperatureUnit(String temperatureUnit) {
		this.temperatureUnit = temperatureUnit;
	}
	public int getTemperatureTrend() {
		return temperatureTrend;
	}
	public void setTemperatureTrend(int temperatureTrend) {
		this.temperatureTrend = temperatureTrend;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getShortForecast() {
		return shortForecast;
	}
	public void setShortForecast(String shortForecast) {
		this.shortForecast = shortForecast;
	}
	public String getDetailedForecast() {
		return detailedForecast;
	}
	public void setDetailedForecast(String detailedForecast) {
		this.detailedForecast = detailedForecast;
	}
	
	@Override
	public String toString() {
		String forecast;
		if(name == "") {
			forecast=  "\nForecast for " + startTime + " to " 
					+ endTime + "\n temperature: " + temperature + "\n temperatureUnit: "
					+ temperatureUnit + "\n temperatureTrend: " + temperatureTrend + "\n windSpeed: " + windSpeed
					+ "\n windDirection: " + windDirection + "\n shortForecast: " + shortForecast;
		}
		else {
		forecast=  "\nForecast for " + name + "\n temperature: " + temperature + "\n temperatureUnit: "
				+ temperatureUnit + "\n temperatureTrend: " + temperatureTrend + "\n windSpeed: " + windSpeed
				+ "\n windDirection: " + windDirection + "\n shortForecast: " + shortForecast
				+ "\n detailedForecast: " + detailedForecast;
		}
		
		return forecast;
	}
	
	
}
