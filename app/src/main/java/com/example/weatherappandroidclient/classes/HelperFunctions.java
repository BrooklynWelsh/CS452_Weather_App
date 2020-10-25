package com.example.weatherappandroidclient.classes;

import android.content.Context;

public class HelperFunctions {
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

}
