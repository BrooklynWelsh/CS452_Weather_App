package com.example.weatherappandroidclient.classes;

import com.example.database.OffsetDateTimeConverter;

import java.time.OffsetDateTime;

public class HourlyForecastCard {
    private OffsetDateTime timestamp;
    private int temperature;
    private int feelsLike;
    private String conditions;
    private int precipitationChance;
    private int windSpeed;
    private String windDirection;
    private int dewPoint;
    private String visibility;
    private int humidity;
    private int windChill;
    private double pressure;
    boolean isDaytime;
    String drawableString;

    public HourlyForecastCard(OffsetDateTime timestamp, int temperature, int feelsLike, int precipitationChance, int windSpeed, int windDirection,
                                int dewPoint, int visibility, int humidity, int windChill, double pressure, int skyCover){
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.precipitationChance = precipitationChance;
        this.windSpeed = windSpeed;
        this.dewPoint = dewPoint;
        if(visibility > 5000)                               this.visibility = "Good";
        else if(visibility <  5000 && visibility > 1000)    this.visibility = "Mist";
        else if(visibility < 1000 && visibility > 100)      this.visibility = "Fog";
        else if(visibility < 100)                           this.visibility = "Very Low";
        this.humidity = humidity;

        // Set boolean for day or night time
        if(timestamp.getHour() < 18 && timestamp.getHour() > 6)  isDaytime = true;
        else                                                     isDaytime = false;

        // Check for no wind chill
        if(windChill == 0)  windChill = temperature;
        else                this.windChill = temperature - windChill;
        this.pressure = pressure;

        // Calculate wind direction
        if(windDirection > 349 || windDirection < 11)   this.windDirection = "N";
        else if(windDirection > 11 && windDirection < 34)   this.windDirection = "NNE";
        else if(windDirection > 34 && windDirection < 56)   this.windDirection = "NE";
        else if(windDirection > 56 && windDirection < 79)   this.windDirection = "ENE";
        else if(windDirection > 79 && windDirection < 101)  this.windDirection = "E";
        else if(windDirection > 101 && windDirection < 124) this.windDirection = "ESE";
        else if(windDirection > 124 && windDirection < 146) this.windDirection = "SE";
        else if(windDirection > 146 && windDirection < 169) this.windDirection = "SSE";
        else if(windDirection > 169 && windDirection < 191) this.windDirection = "S";
        else if(windDirection > 191 && windDirection < 214) this.windDirection = "SSW";
        else if(windDirection > 214 && windDirection < 236) this.windDirection = "SW";
        else if(windDirection > 236 && windDirection < 259) this.windDirection = "WSW";
        else if(windDirection > 259 && windDirection < 281) this.windDirection = "W";
        else if(windDirection > 281 && windDirection < 304) this.windDirection = "WNW";
        else if(windDirection > 304 && windDirection < 326) this.windDirection = "NW";
        else if(windDirection > 326 && windDirection < 349) this.windDirection = "NNW";

        // Determine string for conditions (cloudy, clear, ect)
        // Also set a string for what icon to use
        if(precipitationChance > 50) {
            this.conditions = "Rain";
            if(this.isDaytime == true)  drawableString = "@drawable/wi_day_rain";
            else                        drawableString = "@drawable/wi_night_alt_rain";
        }
        else if(skyCover > 90) {
            this.conditions = "Cloudy";
            drawableString = "@drawable/wi_cloudy";
        }
        else if(skyCover < 90 && skyCover > 50) {
            this.conditions = "Mostly Cloudy";
            if(this.isDaytime == true)  drawableString = "@drawable/wi_day_cloudy";
            else                        drawableString = "@drawable/wi_night_alt_cloudy";
        }
        else if(skyCover < 50 && skyCover > 10) {
            this.conditions = "Partly Cloudy";
            if(this.isDaytime == true)  drawableString = "@drawable/wi_day_sunny_overcast";
            else                        drawableString = "@drawable/wi_night_alt_partly_cloudy";
        }
        else
        {
            this.conditions = "Clear";
            if(isDaytime)   drawableString ="@drawable/wi_day_sunny";
            else            drawableString ="@drawable/wi_night_clear";
        }

        // TODO: Check for snow, ice, etc.
    }

    // GETTERS
    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public int getTemperature() {
        return temperature;
    }

    public int getFeelsLike() {
        return feelsLike;
    }

    public String getConditions() {
        return conditions;
    }

    public int getPrecipitationChance() {
        return precipitationChance;
    }

    public int getWindSpeed() {
        return windSpeed;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public int getDewPoint() {
        return dewPoint;
    }

    public String getVisibility() {
        return visibility;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getWindChill() {
        return windChill;
    }

    public double getPressure() {
        return pressure;
    }

    // SETTERS

}
