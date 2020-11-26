package com.example.weatherappandroidclient.classes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherappandroidclient.CurrentWeatherActivity;
import com.example.weatherappandroidclient.R;

import java.util.List;

public class SearchAdapter  extends
        RecyclerView.Adapter<SearchAdapter.ViewHolder>{
    public static final String CITY_SEARCH = "com.example.weatherappandroidclient.CITY_SEARCH";
    private List<SearchResultCard> cards;

    public SearchAdapter(List<SearchResultCard> cards){
        this.cards = cards;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        ConstraintLayout searchCardsView = (ConstraintLayout)inflater.inflate(R.layout.search_result_card,parent,false);
        ViewHolder viewHolder = new ViewHolder(searchCardsView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        SearchResultCard thisCard = cards.get(position);

        TextView thisCityName = holder.cityName;
        TextView thisCityASCII = holder.cityNameASCII;
        thisCityName.setText(thisCard.getCityName());
        thisCityASCII.setText(thisCard.getCityNameASCII());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView cityName;
        public TextView cityNameASCII;
        public CardView resultCard;
        public String state;
        public String stateID;
        public double longitude;
        public double latitude;
        public int zip;

        public ViewHolder(View itemView){
            super(itemView);

            cityName = (TextView) itemView.findViewById(R.id.cityName);
            resultCard = (CardView) itemView.findViewById(R.id.search);

            // Set on click result for the search result
            resultCard.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), CurrentWeatherActivity.class);
                    intent.putExtra(CITY_SEARCH, cityName + ";" + stateID + ";" + zip + ";" + latitude + ";" + longitude);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}
