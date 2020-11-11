package com.example.weatherappandroidclient.classes;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.weatherappandroidclient.CurrentWeatherActivity;
import com.example.weatherappandroidclient.DailyForecastActivity;
import com.example.weatherappandroidclient.HourlyForecastActivity;
import com.example.weatherappandroidclient.R;

import org.checkerframework.checker.units.qual.Current;

import java.util.Objects;

public class BottomToolbarFragment extends Fragment {

    private ImageButton dailyButton;
    private TextView dailyText;
    private ImageButton todayButton;
    private TextView todayText;

    final int buttonPressedColor = Color.parseColor("#9CD6F9");
    boolean todayButtonClicked = true;
    boolean dailyButtonClicked = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View toolbarView = inflater.inflate(R.layout.bottom_toolbar_custom_layout, container, false);

        // Initialize toolbar buttons
        dailyButton = toolbarView.findViewById(R.id.dailyIcon);
        dailyText = toolbarView.findViewById(R.id.dailyText);
        todayButton = toolbarView.findViewById(R.id.todayIcon);
        todayText = toolbarView.findViewById(R.id.todayText);

        if(Objects.equals(getActivity().getClass().getSimpleName(), "CurrentWeatherActivity")){
            todayButtonClicked = true;
            todayButton.getDrawable().setTint(buttonPressedColor);
            todayText.setTextColor(buttonPressedColor);

            dailyButtonClicked = false;
            dailyButton.getDrawable().setTint(Color.WHITE);
            dailyText.setTextColor(Color.WHITE);
        }
        else{
            dailyButtonClicked = true;
            dailyButton.getDrawable().setTint(buttonPressedColor);
            dailyText.setTextColor(buttonPressedColor);

            todayButtonClicked = false;
            todayButton.getDrawable().setTint(Color.WHITE);
            todayText.setTextColor(Color.WHITE);
        }

        // Listener for home/hourly button
        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CLICK", "THE TODAY BUTTON WAS CLICKED");
                if(dailyButtonClicked == true) {                                       // Only do anything if user is on another activity (i.e. is on the daily forecast)
                    todayButtonClicked = true;
                    todayButton.getDrawable().setTint(buttonPressedColor);
                    todayText.setTextColor(buttonPressedColor);

                    // Reset the dailyButton's attributes
                    dailyButton.getDrawable().setTint(Color.WHITE);
                    dailyText.setTextColor(Color.WHITE);
                    dailyButtonClicked = false;

                    startCurrentWeatherActivity();
                }
            }
        });

        // Listener for daily forecast button
        dailyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CLICK", "THE DAILY BUTTON WAS CLICKED");
                if(todayButtonClicked == true) {
                    dailyButtonClicked = true;
                    dailyButton.getDrawable().setTint(buttonPressedColor);
                    dailyText.setTextColor(buttonPressedColor);

                    // Reset the todayButton's attributes
                    todayButton.getDrawable().setTint(Color.WHITE);
                    todayText.setTextColor(Color.WHITE);
                    todayButtonClicked = false;

                    startDailyForecastActivity();
                }
            }
        });
        return toolbarView;
    }

    public void startDailyForecastActivity() {
        Intent intent = new Intent(getContext(), DailyForecastActivity.class);
        startActivity(intent);
    }

    private void startCurrentWeatherActivity() {
        Intent intent = new Intent(getContext(), CurrentWeatherActivity.class);
        intent.putExtra("isCurrentWeatherActivity", true);
        startActivity(intent);
    }
}
