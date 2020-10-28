package com.example.weatherappandroidclient.classes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
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
    double heatIndex;
    double windChill;
    double precipitationLastHour;

    @JsonIgnore
    String cloudLayers;

    List<String> cloudTypes = Arrays.asList("FEW", "SCT", "BKN", "OVC");
    JsonNode rootNode;

    // GETTERS

    @JsonProperty("textDescription")
    public String getTextDescription() {
        return textDescription;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public int getSeaLevelPressure() {
        return seaLevelPressure;
    }

    public int getVisibility() {
        return visibility;
    }

    public double getRelativeHumidity() {
        return relativeHumidity;
    }

    public double getHeatIndex() { return heatIndex; }

    public double getWindChill() { return windChill; }

    public double getPrecipitationLastHour() {return precipitationLastHour;}

    public String getCloudLayers(){return cloudLayers;}

    // SETTER for JsonNode
    public void setNode(JsonNode node) {this.rootNode = node;}

    @JsonProperty("textDescription")
    public void setTextDescription(String textDescription) {
        this.textDescription = textDescription;
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
    private void unpackPressure(Map<String, Object> pressure){if(pressure.get("value") != null) this.seaLevelPressure = (int)pressure.get("value");}

    @JsonProperty("visibility")
    private void unpackVisibility(Map<String, Object> visibility){this.visibility = (int)visibility.get("value");}

    @JsonProperty("relativeHumidity")
    private void unpackHumidity(Map<String, Object> humidity){
        if(humidity.get("value") != null && humidity.get("value").getClass() == Integer.class) this.relativeHumidity = (int) humidity.get("value");
        else if(humidity.get("value") != null && humidity.get("value").getClass() == Double.class) this.relativeHumidity = (double) humidity.get("value");
    }

    @JsonProperty("windChill")
    private void unpackWindChill(Map<String, Object> windChill){
        if(windChill.get("value") != null) this.windChill = (double) windChill.get("value");
        else this.windChill = 0;
    }

    @JsonProperty("heatIndex")
    private void unpackHeatIndex(Map<String, Object> heatIndex){
        if(heatIndex.get("value") != null) this.heatIndex = (double) heatIndex.get("value");
        else this.heatIndex = this.temperature - this.windChill;
    }

    @JsonProperty("precipitationLastHour")
    private void unpackPrecipitationLastHour(Map<String, Object> precipitationLastHour){
        if(precipitationLastHour.get("value") != null) this.precipitationLastHour = (double) precipitationLastHour.get("value");
        else this.precipitationLastHour = 0;
    }

    public void setCloudLayers(){
        // Set cloudLayers originally to none
        this.cloudLayers = "NONE";
        // Iterate through each layer object, set cloudLayers equal to the highest one
        Iterator<JsonNode> layersNode = this.rootNode.path("cloudLayers").elements();
        while(layersNode.hasNext()){
            JsonNode thisNode = layersNode.next();
            if(this.cloudLayers == "NONE") this.cloudLayers = thisNode.path("amount").textValue();
            else if(cloudTypes.contains(thisNode.path("amount").textValue()) && cloudTypes.indexOf(thisNode.path("amount").textValue()) > cloudTypes.indexOf(this.cloudLayers)){
                this.cloudLayers = thisNode.path("amount").textValue();
            }
        }
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
