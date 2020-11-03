package com.example.database;

import androidx.room.Room;
import android.content.Context;

public class DatabaseClient {
    private Context context;
    private static DatabaseClient instance;

    //our app database object
    private AppDatabase appDatabase;

    private DatabaseClient(Context context) {
        this.context = context;

        //creating the app database with Room database builder
        appDatabase = Room.databaseBuilder(context, AppDatabase.class, "WeatherAppDB").build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseClient(context);
        }
        return instance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
