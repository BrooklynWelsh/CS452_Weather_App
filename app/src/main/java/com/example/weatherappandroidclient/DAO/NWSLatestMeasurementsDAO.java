package com.example.weatherappandroidclient.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.weatherappandroidclient.classes.NWSLatestMeasurements;

import java.util.List;

@Dao
public interface  NWSLatestMeasurementsDAO {
    @Query("SELECT * from latest_measurements LIMIT 1")
    NWSLatestMeasurements getMeasurement();

    @Insert
    void insert(NWSLatestMeasurements task);

    @Delete
    void delete(NWSLatestMeasurements task);

    @Update
    void update(NWSLatestMeasurements task);
}
