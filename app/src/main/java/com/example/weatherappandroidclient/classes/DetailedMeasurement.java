package com.example.weatherappandroidclient.classes;

import java.time.OffsetDateTime;

public class DetailedMeasurement {
    OffsetDateTime timestamp;
    int highTemp;
    int lowTemp;
    int cloudCover;
    int humidity;
    int precipitationChance;    // Just take the highest % from the given day

    public DetailedMeasurement(){}

    public DetailedMeasurement(OffsetDateTime timestamp, int highTemp, int lowTemp, int cloudCover, int humidity, int precipitationChance){
        this.timestamp = timestamp;
        this.highTemp = highTemp;
        this.lowTemp = lowTemp;
        this.cloudCover = cloudCover;
        this.humidity = humidity;
        this.precipitationChance = precipitationChance;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public int getHighTemp() {
        return highTemp;
    }

    public int getLowTemp() {
        return lowTemp;
    }

    public int getCloudCover() {
        return cloudCover;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getPrecipitationChance() {
        return precipitationChance;
    }
}
