package com.example.android.popularmovies;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.io.Serializable;

/**
 * Created by Gboyega.Dada on 4/14/2017.
 */

@Parcel
public class MovieItem {
    String title;
    String overview;
    String poster_path;
    String release_date;
    String running_time;
    int vote_count;
    int vote_average;
    int popularity;
    boolean video;

    public MovieItem() {

    }

    public MovieItem(JSONObject json) {
        try {
            title = json.getString("original_title");
            overview = json.getString("overview");
            poster_path = json.getString("poster_path");
            release_date = json.getString("release_date");
            vote_count = json.getInt("vote_count");
            vote_average = json.getInt("vote_average");
            popularity = json.getInt("popularity");
            video = json.getBoolean("video");
            running_time = "";

        } catch (JSONException ex) {
            title = "No Title";
            overview = "No Overview";
        }
    }
}
