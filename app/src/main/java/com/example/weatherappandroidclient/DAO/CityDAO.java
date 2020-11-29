package com.example.weatherappandroidclient.DAO;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Transaction;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.example.weatherappandroidclient.classes.City;
import java.util.List;

@Dao
public interface CityDAO {
    @Transaction
    @Query(
            "SELECT cities.rowId, cities.id, cities.lng, cities.lat, cities.city, cities.state_name, cities.state_id, " +
                    "county_fips, city_ascii, county_name, source, density, population, military, incorporated, timezone, ranking, zips FROM cities "
                    + "JOIN 'citiesFts' ON (cities.rowId = citiesFts.rowId) WHERE 'citiesFts' MATCH :city")
    List<City> searchByCityName(String city);

    @Query("SELECT * FROM cities")
    List<City> getAll();
//
//    @Query("INSERT INTO citiesFts(citiesFts) VALUES('rebuild')")
//    void rebuild();

    @RawQuery
    int query(SupportSQLiteQuery query);

}
