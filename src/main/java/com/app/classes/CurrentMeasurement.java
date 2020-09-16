package com.app.classes;

public class CurrentMeasurement {
	int stationid;
	float fahrenheit;
	float celsius;
	float wind_speed;
	float wind_gust;
	float pressure;
	float humidity;
	float rain;
	String cloud;
	float heatindex;

	public int getId() {return stationid;}
	
	public float getFTemp() {return fahrenheit;}
	public void setFTemp(float fahrenheit) {this.fahrenheit = fahrenheit;}
	
	public float getCTemp() {return celsius;}
	public void setCTemp(float celsius) {this.celsius = celsius;}
	
	public float getWindSpeed() {return wind_speed;}
	public void setWindSpeed(float speed) {this.wind_speed = speed;}
	
	public float getWindGust() {return wind_gust;}
	public void setWindGust(float gust) {this.wind_gust = gust;}
	
	public float getPressure() {return pressure;}
	public void setPressure(float pressure) {this.pressure = pressure;}
	
	public float getHumidity() {return humidity;}
	public void setHumidity(float humidity) {this.humidity = humidity;}
	
	public float getRain() {return rain;}
	public void setRain(float rain) {this.rain = rain;}
	
	public String getCloud() {return cloud;}
	public void setCloud(String cloud) {this.cloud = cloud;}
	
	public float getHeatIndex() {return heatindex;}
	public void setHeatIndex(float heatindex) {this.heatindex = heatindex;}
}
