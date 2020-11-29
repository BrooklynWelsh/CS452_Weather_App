package com.example.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.weatherappandroidclient.DAO.CityDAO;
import com.example.weatherappandroidclient.classes.City;

@Database(entities = {City.class, CityFTS4.class}, version = 2)
public abstract class CityDatabase extends RoomDatabase {
    public abstract CityDAO CityDAO();

}

