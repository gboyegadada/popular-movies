package com.example.android.popularmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Gboyega.Dada on 5/8/2017.
 */

public class ReviewList extends ArrayList<ReviewItem> {

    public ReviewList(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            JSONArray jsonArray = json.getJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                this.add(i, new ReviewItem(jsonArray.getJSONObject(i)));
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }



}
