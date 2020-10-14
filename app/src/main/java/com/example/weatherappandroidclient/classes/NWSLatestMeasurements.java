package com.example.weatherappandroidclient.classes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)     // There are some fields in the properties field that we can safely ignore
public class NWSLatestMeasurements {
    @JsonProperty("textDescription")
    String textDescription;
    double temperature;
    double windSpeed;
    int seaLevelPressure;
    int visibility;
    // TODO: include variables for precipitation last hour, 3 hours, 6 hours
    double relativeHumidity;

    @JsonProperty("textDescription")
    public void setTextDescription(String textDescription) {
        this.textDescription = textDescription;
    }

    @JsonProperty("textDescription")
    public String getTextDescription() {
        return textDescription;
    }

    @JsonProperty("temperature")
    private void unpackTemp(Map<String, Object> temp){
        if(temp.get("value") != null && temp.get("value").getClass() == Integer.class) temperature = (int)temp.get("value");
        else if(temp.get("value") != null && temp.get("value").getClass() == Double.class)  temperature = (double)temp.get("value");
    }

    @JsonProperty("windSpeed")
    private void unpackWindSpeed(Map<String, Object> windSpeed){
            if(windSpeed.get("value").getClass() == java.lang.Integer.class)    this.windSpeed = (int)windSpeed.get("value");
            else                                                                this.windSpeed = (double)windSpeed.get("value");
    }

    @JsonProperty("seaLevelPressure")
    private void unpackPressure(Map<String, Object> pressure){seaLevelPressure = (int)pressure.get("value");}

    @JsonProperty("visibility")
    private void unpackVisibility(Map<String, Object> visibility){this.visibility = (int)visibility.get("value");}

    @JsonProperty("relativeHumidity")
    private void unpackHumidity(Map<String, Object> humidity){
        if(humidity.get("value") != null && humidity.get("value").getClass() == Integer.class) relativeHumidity = (int) humidity.get("value");
        else if(humidity.get("value") != null && humidity.get("value").getClass() == Double.class) relativeHumidity = (double) humidity.get("value");

    }

    @Override
    public String toString() {
        return  "Here's the current weather for your area: \n\nCondition: " + textDescription + '\n' +
                "Temperature (Celsius): " + temperature + "\n" +
                "Wind Speed: " + windSpeed + "\n" +
                "Barometric Pressure: " + seaLevelPressure + "\n" +
                "Visibility (meters): " + visibility + "\n" +
                "Relative Humidity: " + relativeHumidity;
    }
}
