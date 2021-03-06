package com.example.database;

import androidx.room.Room;

import android.content.Context;

public class DatabaseClient {
    private Context context;
    private static DatabaseClient instance;

    //our app database object
    private LatestMeasurementsDatabase latestMeasurementsDatabase;

    // database for city info
    private CityDatabase citiesDatabase;

    private DatabaseClient(Context context) {
        this.context = context;

        //creating the app database with Room database builder
        latestMeasurementsDatabase = Room.databaseBuilder(context, LatestMeasurementsDatabase.class, "WeatherAppDB").build();

        citiesDatabase = Room.databaseBuilder(context, CityDatabase.class, "CityDB")
                .createFromAsset("CityDB.sqlite3")
                .fallbackToDestructiveMigration()
                .build();


//        Room.databaseBuilder(context, CityDatabase.class, "cityDB")
//                .addMigrations(new Migration(1, 2){
//                    @Override
//                    public void migrate(@NonNull SupportSQLiteDatabase database) {
//                        database.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS 'citiesFts' USING FTS4('city', 'state_name', 'state_id', content='cities')");
//                        database.execSQL("INSERT INTO citiesFts(citiesFts) VALUES ('rebuild')");
//                    }
//                })
//                .fallbackToDestructiveMigration()
//                .build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseClient(context);
        }
        return instance;
    }

    public LatestMeasurementsDatabase getLatestMeasurementsDatabase() {
        return latestMeasurementsDatabase;
    }

    public CityDatabase getCitiesDatabase(){
        return citiesDatabase;
    }
}
