package com.app.classes;

import java.time.LocalTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class HourlyForecast {
	@JsonDeserialize(using = LocalTimeDeserializer.class)
	@JsonSerialize(using = LocalTimeSerializer.class)
	LocalTime date;
	
	int id;
	float fahrenheit;
	float celsius;
	float wind_speed;
	float wind_gust;
	float pressure;
	float humidity;
	float rain_probability;
	String cloud;
	float heatindex;
	
	public LocalTime getDate() {return date;}
	public void setDate(LocalTime date) {this.date = date;}
	
	public int getId() {return id;}
	
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
	
	public float getRainProb() {return rain_probability;}
	public void setRainProb(float prob) {this.rain_probability = prob;}
	
	public String getCloud() {return cloud;}
	public void setCloud(String cloud) {this.cloud = cloud;}
	
	public float getHeatIndex() {return heatindex;}
	public void setHeatIndex(float heatindex) {this.heatindex = heatindex;}
	
	@Override
	public String toString() {
		return "HourlyForecast [date=" + date + ", stationid=" + id + ", fahrenheit=" + fahrenheit + ", celsius="
				+ celsius + ", wind_speed=" + wind_speed + ", wind_gust=" + wind_gust + ", pressure=" + pressure
				+ ", humidity=" + humidity + ", rain_probability=" + rain_probability + ", cloud=" + cloud
				+ ", heatindex=" + heatindex + "]";
	}
}
