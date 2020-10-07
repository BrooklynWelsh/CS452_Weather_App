package com.app.classes;

import java.time.LocalDate;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class DailyForecast {
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	LocalDate date;
	
	int id;
	float fahrenheit_high;
	float fahrenheit_low;
	float celsius_high;
	float celsius_low;
	float wind_speed;
	float wind_gust;
	float pressure;
	float humidity;
	float rain_probability;
	String cloud;
	float heatindex;
	
	public LocalDate getDate() {return date;}
	public void setDate(LocalDate date) {this.date = date;}
	
	public int getId() {return id;}
	
	public float getFHigh() {return fahrenheit_high;}
	public void setFHigh(float high) {this.fahrenheit_high = high;}
	
	public float getFLow() {return fahrenheit_low;}
	public void setFLow(float low) {this.fahrenheit_low = low;}
	
	public float getCHigh() {return celsius_high;}
	public void setCHigh(float high) {this.celsius_high = high;}
	
	public float getCLow() {return celsius_low;}
	public void setCLow(float low) {this.celsius_low = low;}
	
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
		return "DailyForecast [date=" + date + ", id=" + id + ", fahrenheit_high=" + fahrenheit_high
				+ ", fahrenheit_low=" + fahrenheit_low + ", celsius_high=" + celsius_high + ", celsius_low="
				+ celsius_low + ", wind_speed=" + wind_speed + ", wind_gust=" + wind_gust + ", pressure=" + pressure
				+ ", humidity=" + humidity + ", rain_probability=" + rain_probability + ", cloud=" + cloud
				+ ", heatindex=" + heatindex + "]";
	}
}
