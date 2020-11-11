package com.example.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.weatherappandroidclient.DAO.NWSLatestMeasurementsDAO;
import com.example.weatherappandroidclient.classes.NWSLatestMeasurements;

@Database(entities = {NWSLatestMeasurements.class}, version = 1)
@TypeConverters({OffsetDateTimeConverter.class})
public abstract class LatestMeasurementsDatabase extends RoomDatabase {
        public abstract NWSLatestMeasurementsDAO NWSLatestMeasurementsDAO();
}

