package com.example.weatherappandroidclient.classes;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Fts4
@Entity(tableName = "cities")
public class City {

    //@PrimaryKey int id;
    @NotNull @ColumnInfo (name = "city")                  private String cityName;
    @NotNull @ColumnInfo(name = "city_ascii")            private String cityNameASCII;
    @NotNull @ColumnInfo(name = "state_name")            private String stateName;
    @NotNull @ColumnInfo(name = "state_id")              private String stateId;
    @NotNull @ColumnInfo(name = "lat")                   private double latitude;
    @NotNull @ColumnInfo(name = "lng")                   private double longitude;
    @NotNull @PrimaryKey @ColumnInfo(name = "id")                    private int id;

    // These columns aren't needed, but I can't get Room to ignore them with the @Ignore annotation.
    @NotNull @ColumnInfo(name = "county_fips")   private int fips;
    @NotNull @ColumnInfo(name = "county_name")   private String countyName;
    @NotNull @ColumnInfo(name = "population")    private int population;
    @NotNull @ColumnInfo(name = "density")       private int density;
    @NotNull @ColumnInfo(name = "source")        private String source;
    @NotNull @ColumnInfo(name = "military")      private String isMilitary;
    @NotNull @ColumnInfo(name = "incorporated")  private String isIncorporated;
    @NotNull @ColumnInfo(name = "timezone")      private String timezone;
    @NotNull @ColumnInfo(name = "ranking")       private int ranking;
    @ColumnInfo(name = "zips")                   private String zip;


    public City(int id, String cityName, String stateName, String cityNameASCII, String stateId, double latitude, double longitude,
                int fips, String countyName, int population, int density, String source, String isMilitary, String isIncorporated, String timezone, int ranking, String zip){
        this.id = id;
        this.cityName = cityName;
        this.cityNameASCII = cityNameASCII;
        this.stateName = stateName;
        this.stateId = stateId;
        this.latitude = latitude;
        this.longitude = longitude;

        // Not needed fields
        this.fips = fips;
        this.countyName = countyName;
        this.population = population;
        this.density = density;
        this.source = source;
        this.isMilitary = isMilitary;
        this.isIncorporated = isIncorporated;
        this.timezone = timezone;
        this.ranking = ranking;
        this.zip = zip;
    }


    public String getCityNameASCII() {
        return cityNameASCII;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public String getCityName() {
        return cityName;
    }

    public String getStateName() {
        return stateName;
    }

    public String getStateId() {
        return stateId;
    }


    public int getId() {
        return id;
    }


    // Getters for fields we don't need

    public int getFips() {
        return fips;
    }

    public String getCountyName() {
        return countyName;
    }

    public int getPopulation() {
        return population;
    }

    public int getDensity() {
        return density;
    }

    public String getSource() {
        return source;
    }


    public String isMilitary() {
        return isMilitary;
    }

    public String isIncorporated() {
        return isIncorporated;
    }

    public String getTimezone() {
        return timezone;
    }

    public int getRanking() {
        return ranking;
    }

    public String getZip() {
        return zip;
    }

    @Override
    public String toString() {
        return "City{" +
                "cityName='" + cityName + '\'' +
                ", cityNameASCII='" + cityNameASCII + '\'' +
                ", stateName='" + stateName + '\'' +
                ", stateId='" + stateId + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", id=" + id +
                ", fips=" + fips +
                ", countyName='" + countyName + '\'' +
                ", population=" + population +
                ", density=" + density +
                ", source='" + source + '\'' +
                ", isMilitary='" + isMilitary + '\'' +
                ", isIncorporated='" + isIncorporated + '\'' +
                ", timezone='" + timezone + '\'' +
                ", ranking=" + ranking +
                ", zip='" + zip + '\'' +
                '}';
    }
}
