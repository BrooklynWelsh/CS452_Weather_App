package com.example.weatherappandroidclient;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.database.DatabaseHelper;
import com.example.weatherappandroidclient.classes.SearchAdapter;
import com.example.weatherappandroidclient.classes.SearchResultCard;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            Cursor results = getMatches(query);
            if(results != null) generateResultCards(results);
            else Toast.makeText(this.getApplicationContext(), "There were no results for your search.",Toast.LENGTH_LONG);
        }
    }

    private Cursor getMatches(String query){
        SQLiteDatabase cityDB = DatabaseHelper.getHelper(this).getDatabase();
        Cursor cursor = cityDB.query("citiesFts",new String[]{"city","state_id","state_name","lat","lng","zips"},"city MATCH ? AND state_id != \"PR\"",new String[]{"*"+query+"*"},null,null,null);

        if (cursor == null) {
            return null;
        }
        return cursor;
    }

    private void generateResultCards(Cursor results){
        // Iterate through Cursor results and produce a list of search cards
        ArrayList<SearchResultCard> searchCards = new ArrayList<SearchResultCard>();
        try {
            while (results.moveToNext()) {
                SearchResultCard thisCard = new SearchResultCard(results.getString(results.getColumnIndex("city")),results.getString(results.getColumnIndex("state_name")),
                        results.getString(results.getColumnIndex("state_id")), results.getString(results.getColumnIndex("zips")),
                        results.getDouble(results.getColumnIndex("lat")), results.getDouble(results.getColumnIndex("lng")));
                searchCards.add(thisCard);
            }
        }
        finally {results.close();}

        addCardsToView(searchCards);
    }

    private void addCardsToView(ArrayList<SearchResultCard> cards){
        RecyclerView searchRecyclerView = findViewById(R.id.searchRecyclerView);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        SearchAdapter adapter = new SearchAdapter(cards);
        searchRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}
