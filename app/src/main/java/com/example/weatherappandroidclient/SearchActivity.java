package com.example.weatherappandroidclient;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.provider.ContactsContract;

import androidx.appcompat.app.AppCompatActivity;

import com.example.database.DatabaseHelper;
import com.example.weatherappandroidclient.classes.SearchResultCard;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            generateResultCards(results);
        }
    }

    private Cursor getMatches(String query){
        SQLiteDatabase cityDB = DatabaseHelper.getHelper(this).getDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        Cursor cursor = builder.query(cityDB,new String[]{"city","state_id","state_name","lat","lng","zips"},"city_ascii MATCH ?",new String[]{query},null,null,null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    private void generateResultCards(Cursor results){
        // Iterate through Cursor results and products a list of search cards
        ArrayList<SearchResultCard> searchCards = new ArrayList<SearchResultCard>();
        try {
            while (results.moveToNext()) {
                SearchResultCard thisCard = new SearchResultCard(results.getString(results.getColumnIndex("city_name")),results.getString(results.getColumnIndex("city_ascii")));
                searchCards.add(thisCard);
            }
        }
        finally {results.close();}

        // Add each card to the recycler view


    }

}
