package com.example.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.PrimaryKey;

import com.example.weatherappandroidclient.classes.City;

@Fts4(contentEntity = City.class)
@Entity(tableName = "citiesFts")
public class CityFTS4 {
    @ColumnInfo(name = "rowid")
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "city")
    private final String cityName;
    @ColumnInfo(name = "state_name")
    private final String stateName;
    @ColumnInfo(name = "state_id")
    private final String stateId;

    public CityFTS4( int id, String cityName, String stateName, String stateId){
        this.id = id;
        this.cityName = cityName;
        this.stateName = stateName;
        this.stateId = stateId;
    }


    public int getId() {
        return id;
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
}
