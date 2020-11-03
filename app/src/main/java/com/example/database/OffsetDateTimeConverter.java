package com.example.database;

import androidx.room.TypeConverter;

import java.time.OffsetDateTime;

public class OffsetDateTimeConverter {
    @TypeConverter
    public static OffsetDateTime toDate(String dateString) {
        if (dateString == null) {
            return null;
        } else {
            return OffsetDateTime.parse(dateString);
        }
    }

    @TypeConverter
    public static String toDateString(OffsetDateTime date) {
        if (date == null) {
            return null;
        } else {
            return date.toString();
        }
    }
}

