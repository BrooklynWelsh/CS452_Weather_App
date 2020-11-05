package com.example.weatherappandroidclient.classes;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.example.weatherappandroidclient.CurrentWeatherActivity;
import com.example.weatherappandroidclient.R;

import org.checkerframework.checker.units.qual.Current;

import java.lang.reflect.Field;
import java.time.format.DateTimeFormatter;
import java.util.List;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class HourlyAdapter extends
        RecyclerView.Adapter<HourlyAdapter.ViewHolder> {

    private List<HourlyForecastCard> cards;

    public HourlyAdapter(List<HourlyForecastCard> cards){
        this.cards = cards;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        ConstraintLayout hourlyCardsView = (ConstraintLayout)inflater.inflate(R.layout.hourly_forecast_card, parent, false);

        ViewHolder viewHolder = new ViewHolder(hourlyCardsView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HourlyForecastCard thisCard = cards.get(position);

        TextView thisTimestamp = holder.timestamp;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("h a");
        thisTimestamp.setText(fmt.format(thisCard.getTimestamp()));

        TextView thisTemp = holder.temperatureText;
        thisTemp.setText(String.valueOf(thisCard.getTemperature()) + "°F");

        TextView thisConditions = holder.conditionsText;
        //thisTemp.setText(String.valueOf(thisCard.getTemperature()));
        // Determine the conditions

        TextView thisPrecipitationChance = holder.precipitationChanceText;
        thisPrecipitationChance.setText(String.valueOf(thisCard.getPrecipitationChance()) + "%");

        TextView thisWindDirection = holder.windDirectionText;
        thisWindDirection.setText(thisCard.getWindDirection());

        TextView thisWindSpeed = holder.windSpeedText;
        thisWindSpeed.setText(String.valueOf(thisCard.getWindSpeed()) + "mph"); // TODO: Use resource string instead

        TextView thisFeelsLike = holder.feelsLikeText;
        thisFeelsLike.setText(String.valueOf(thisCard.getFeelsLike())  + "°F");

        TextView thisDewPoint = holder.dewPointText;
        thisDewPoint.setText(String.valueOf(thisCard.getDewPoint()) + "°F");

        TextView thisHumidity = holder.humidityText;
        thisHumidity.setText(String.valueOf(thisCard.getHumidity()) + "%");

        TextView thisPressure = holder.pressureText;
        thisPressure.setText(String.valueOf(thisCard.getPressure()) + "\"Hg");

        TextView thisVisibility = holder.visibilityText;
        thisVisibility.setText(String.valueOf(thisCard.getVisibility()));

        // Determine the icon for weather conditions
        ImageView thisIcon = holder.iconView;
        int drawableResourceId = holder.iconView.getContext().getResources().getIdentifier(thisCard.drawableString, "drawable", holder.iconView.getContext().getPackageName());
        thisIcon.setImageDrawable(holder.iconView.getContext().getResources().getDrawable(drawableResourceId));
        ImageViewCompat.setImageTintList(thisIcon, ColorStateList.valueOf(Color.WHITE));
    }
    @Override
    public int getItemCount() {
        return cards.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView timestamp;
        public TextView temperatureText;
        public TextView conditionsText;
        public TextView precipitationChanceText;
        public TextView windDirectionText;
        public TextView windSpeedText;
        public TextView feelsLikeText;
        public TextView dewPointText;
        public TextView humidityText;
        public TextView pressureText;
        public TextView visibilityText;

        public ImageView iconView;
        public ImageButton arrowButton;
        public ConstraintLayout hiddenView;
        public CardView cardView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            timestamp = (TextView) itemView.findViewById(R.id.timestampView);
            temperatureText = (TextView) itemView.findViewById(R.id.temperatureView);
            conditionsText = (TextView) itemView.findViewById(R.id.weatherTextView);
            precipitationChanceText = (TextView) itemView.findViewById(R.id.rainChanceTextView);
            windDirectionText = (TextView) itemView.findViewById(R.id.windDirectionTextView);
            windSpeedText = (TextView) itemView.findViewById(R.id.windSpeedTextView);
            feelsLikeText = (TextView) itemView.findViewById(R.id.feelsLikeValue);
            dewPointText = (TextView) itemView.findViewById(R.id.dewPointValue);
            humidityText = (TextView) itemView.findViewById(R.id.humidityValue);
            pressureText = (TextView) itemView.findViewById(R.id.pressureValue);
            visibilityText = (TextView) itemView.findViewById(R.id.visibilityValue);

            iconView = (ImageView) itemView.findViewById(R.id.iconView);

            arrowButton = (ImageButton) itemView.findViewById(R.id.expand_arrow);
            hiddenView = (ConstraintLayout) itemView.findViewById(R.id.hidden_view);
            cardView = (CardView) itemView.findViewById((R.id.cardView));

            // Set the on click listener for the expand arrow
            arrowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // If the CardView is already expanded, set its visibility
                    //  to gone and change the expand less icon to expand more.
                    if (hiddenView.getVisibility() == View.VISIBLE) {

                        // The transition of the hiddenView is carried out
                        //  by the TransitionManager class.
                        // Here we use an object of the AutoTransition
                        // Class to create a default transition.
                        TransitionManager.beginDelayedTransition(cardView,
                                new AutoTransition());
                        hiddenView.setVisibility(View.GONE);
                        arrowButton.setImageResource(R.drawable.ic_baseline_expand_more_24);
                    }

                    // If the CardView is not expanded, set its visibility
                    // to visible and change the expand more icon to expand less.
                    else {

                        TransitionManager.beginDelayedTransition(cardView,
                                new AutoTransition());
                        hiddenView.setVisibility(View.VISIBLE);
                        arrowButton.setImageResource(R.drawable.ic_baseline_expand_less_24);
                    }
                }
            });
        }
    }
}
