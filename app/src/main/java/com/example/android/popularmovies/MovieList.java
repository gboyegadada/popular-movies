package com.example.android.popularmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by Gboyega.Dada on 4/15/2017.
 */

@Parcel
public class MovieList extends ArrayList<MovieItem> {


    protected MovieList fromJSON(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            JSONArray results = json.getJSONArray("results");
            for (int i=0;i<results.length();i++) {
                this.add(new MovieItem(results.getJSONObject(i)));
            }
        } catch (JSONException ex) {
            // TODO (3): Handle JSON exception
            ex.printStackTrace();
        }

        return this;
    }
}
