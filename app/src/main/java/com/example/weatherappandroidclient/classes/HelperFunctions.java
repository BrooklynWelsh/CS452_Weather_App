package com.example.weatherappandroidclient.classes;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.example.database.DatabaseClient;
import com.example.weatherappandroidclient.R;
import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.picasso.Picasso;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Iterator;

public class HelperFunctions {
    public static final int HOURS_IN_DAY = 24;
    public static int convertToFahrenheit(double celsius){
        int fahrenheit = (int)(celsius / 5) * 9 + 32;
        return fahrenheit;
    }

    public static int dpToPx(int dp, Context context) {
        // Java functions only take pixel measurements, this is for converting to the more useful dp measurement
        float density = context.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

    public static void setCloudIcon(Activity activity, ImageView cloudImageView, int cloudCover, OffsetDateTime time, int rainProb) {
        // 0-10% = Clear, 10 - 50% = Scattered, 50 - 90% = Broken, 90 - 100% = Overcast
        // TODO: Can check the "weather" node at the gridpoints endpoint to check for conditions like fog, and can possibly choose icon for "showers" vs "heavy rain" and so on.

        // Check for rain probability first
        if(rainProb > 30 && rainProb < 60)  cloudImageView.setImageDrawable(activity.getResources().getDrawable(activity.getResources().getIdentifier("@drawable/wi_day_showers", null, activity.getPackageName())));
        else if( rainProb > 60)             cloudImageView.setImageDrawable(activity.getResources().getDrawable(activity.getResources().getIdentifier("@drawable/wi_day_rain", null, activity.getPackageName())));

        // Then check cloud cover conditions
        else if(cloudCover > 90) cloudImageView.setImageDrawable(activity.getResources().getDrawable(activity.getResources().getIdentifier("@drawable/wi_cloudy", null, activity.getPackageName())));

        else if(cloudCover < 90 && cloudCover >= 50) {
            if (time.getHour() < 18 && time.getHour() > 6) cloudImageView.setImageDrawable(activity.getResources().getDrawable(activity.getResources().getIdentifier("@drawable/wi_day_cloudy", null, activity.getPackageName())));
            else                                cloudImageView.setImageDrawable(activity.getResources().getDrawable(activity.getResources().getIdentifier("@drawable/wi_night_cloudy", null, activity.getPackageName())));
        }

        else if(cloudCover < 50 && cloudCover >= 10) {
            if (time.getHour() < 18 && time.getHour() > 6) cloudImageView.setImageDrawable(activity.getResources().getDrawable(activity.getResources().getIdentifier("@drawable/wi_day_cloudy_high", null, activity.getPackageName())));
            else                                            cloudImageView.setImageDrawable(activity.getResources().getDrawable(activity.getResources().getIdentifier("@drawable/wi_night_partly_cloudy", null, activity.getPackageName())));
        }

        else {
            if (time.getHour() < 18 && time.getHour() > 6)
                cloudImageView.setImageDrawable(activity.getResources().getDrawable(activity.getResources().getIdentifier("@drawable/wi_day_sunny", null, activity.getPackageName())));
            else
                cloudImageView.setImageDrawable(activity.getResources().getDrawable(activity.getResources().getIdentifier("@drawable/wi_night_clear", null, activity.getPackageName())));
        }
    }

    public static double getDailyAverage(Iterator<JsonNode> values, OffsetDateTime timestamp){
        boolean pastCurrentTime = false;
        int sumOfValues = 0;
        int indexOfDuration;
        int i = 0;
        while(!pastCurrentTime && values.hasNext() ){
            JsonNode node = values.next();
            indexOfDuration = node.path("validTime").textValue().indexOf("/");
            String thisTimestampString = node.path("validTime").textValue();
            // Check to make sure we are still in the same day
            OffsetDateTime thisTimestamp = OffsetDateTime.parse(thisTimestampString.substring(0, indexOfDuration));
            if(thisTimestamp.getDayOfWeek().compareTo(timestamp.getDayOfWeek()) > 0) pastCurrentTime = true;
            if(!pastCurrentTime && thisTimestamp.getDayOfWeek().compareTo(timestamp.getDayOfWeek()) == 0) {
                // Parse the duration
                indexOfDuration = thisTimestampString.indexOf("/");
                int duration = (int) Duration.parse(thisTimestampString.substring(indexOfDuration + 1)).toHours();
                double thisValue = node.path("value").asDouble();
                sumOfValues += (thisValue * duration);
            }
            i++;
        }
        return sumOfValues / HOURS_IN_DAY;
    }

    public static double getMin(Iterator<JsonNode> values, OffsetDateTime timestamp){
        double lowest = Double.NaN;
        boolean pastCurrentTime = false;
        int indexOfDuration;
        int i = 0;
        while(!pastCurrentTime && values.hasNext() ){
            JsonNode node = values.next();
            indexOfDuration = node.path("validTime").textValue().indexOf("/");
            String thisTimestampString = node.path("validTime").textValue();
            // Check to make sure we are still in the same day
            OffsetDateTime thisTimestamp = OffsetDateTime.parse(thisTimestampString.substring(0, indexOfDuration));
            if(thisTimestamp.getDayOfWeek().compareTo(timestamp.getDayOfWeek()) > 0) pastCurrentTime = true;
            if(!pastCurrentTime && thisTimestamp.getDayOfWeek().compareTo(timestamp.getDayOfWeek()) == 0) {
                // Parse the duration
                double thisValue = node.path("value").asDouble();
                if(Double.isNaN(lowest)|| thisValue < lowest) lowest = thisValue;
            }
            i++;
        }
        return lowest;
    }

    public static double getMax(Iterator<JsonNode> values, OffsetDateTime timestamp){
        double highest = Double.NaN;
        boolean pastCurrentTime = false;
        int indexOfDuration;
        int i = 0;
        while(!pastCurrentTime && values.hasNext() ){
            JsonNode node = values.next();
            indexOfDuration = node.path("validTime").textValue().indexOf("/");
            String thisTimestampString = node.path("validTime").textValue();
            // Check to make sure we are still in the same day
            OffsetDateTime thisTimestamp = OffsetDateTime.parse(thisTimestampString.substring(0, indexOfDuration));
            if(thisTimestamp.getDayOfWeek().compareTo(timestamp.getDayOfWeek()) > 0) pastCurrentTime = true;
            if(!pastCurrentTime && thisTimestamp.getDayOfWeek().compareTo(timestamp.getDayOfWeek()) == 0) {
                // Parse the duration
                double thisValue = node.path("value").asDouble();
                if(Double.isNaN(highest) || thisValue > highest) highest = thisValue;
            }
            i++;
        }
        return highest;
    }

    public static void setBackgroundImage(ImageView background, String cloudCover, double rainAmount, boolean isDaytime, int screenWidth, int screenHeight){
        if(rainAmount > 0) Picasso.get().load(R.drawable.rainy).resize(screenWidth, screenHeight).onlyScaleDown().into(background);
        else if(cloudCover.equals("FEW") || cloudCover.equals("SCT")) Picasso.get().load(R.drawable.scattered_clouds).resize(screenWidth, screenHeight).onlyScaleDown().into(background);
        else if(cloudCover.equals("BKN") || cloudCover.equals("OVC")) Picasso.get().load(R.drawable.overcast_sky).resize(screenWidth, screenHeight).onlyScaleDown().into(background);
        else if(isDaytime == false) Picasso.get().load(R.drawable.clear_night_sky).resize(screenWidth, screenHeight).onlyScaleDown().into(background);
        // Else we can leave it to the default clear sunny image
    }

    public static void saveNWSLatestMeasurement(Context context, NWSLatestMeasurements measurements){
        DatabaseClient.getInstance(context).getAppDatabase()
                .NWSLatestMeasurementsDAO()
                .insert(measurements);
    }



}
