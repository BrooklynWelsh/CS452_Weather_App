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
        holder.searchResultObject=cards.get(position);

        TextView thisCityName = holder.cityName;
        thisCityName.setText(thisCityName.getContext().getString(R.string.search_city_state_info,thisCard.getCityName(),thisCard.getStateID()));
    }

    public SearchResultCard getItem(int position){
        return cards.get(position);
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public SearchResultCard searchResultObject;
        public TextView cityName;
        public CardView resultCard;

        public ViewHolder(View itemView){
            super(itemView);

            cityName = (TextView) itemView.findViewById(R.id.cityName);
            resultCard = (CardView) itemView.findViewById(R.id.searchCardView);

            // Set on click result for the search result
            resultCard.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // Start CurrentWeatherActivity and pass in the clicked search result object
                    Intent intent = new Intent(v.getContext(), CurrentWeatherActivity.class);
                    intent.putExtra("SearchResult",searchResultObject);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}
