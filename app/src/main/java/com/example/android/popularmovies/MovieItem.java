package com.example.android.popularmovies;

import android.database.Cursor;

import com.example.android.popularmovies.data.FavoritesContract;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;


/**
 * Created by Gboyega.Dada on 4/14/2017.
 */

@Parcel
public class MovieItem {
    public int row_id;
    public int movie_id;
    public String title;
    public String original_title;
    public String overview;
    public String poster_path;
    public String release_date;
    public String release_time;
    public String running_time;
    public int vote_count;
    public int vote_average;
    public int popularity;
    public boolean video;

    public MovieItem() {

    }

    public MovieItem(JSONObject json) {
        try {
            movie_id = json.getInt("id");
            title = json.getString("title");
            original_title = json.getString("original_title");
            overview = json.getString("overview");
            poster_path = json.getString("poster_path");
            release_date = json.getString("release_date");
            vote_count = json.getInt("vote_count");
            vote_average = json.getInt("vote_average");
            popularity = json.getInt("popularity");
            video = json.getBoolean("video");
            running_time = "";
            release_time = "";

        } catch (JSONException e) {
            title = "No Title";
            overview = "No Overview";

            e.printStackTrace();
        }
    }

    // Overloading to the rescue!!
    // *** Important: Please cursor.move(to_position) before passing ***
    public MovieItem(Cursor cursor) {

        // Indices for the _id, description, and priority columns
        int rowIdIndex = cursor.getColumnIndex(FavoritesContract.FavoriteEntry._ID);
        int movieIdIndex = cursor.getColumnIndex(FavoritesContract.FavoriteEntry.COLUMN_MOVIE_ID);
        int titleIndex = cursor.getColumnIndex(FavoritesContract.FavoriteEntry.COLUMN_TITLE);
        int originalTitleIndex = cursor.getColumnIndex(FavoritesContract.FavoriteEntry.COLUMN_ORIGINAL_TITLE);
        int overviewIndex = cursor.getColumnIndex(FavoritesContract.FavoriteEntry.COLUMN_OVERVIEW);
        int releaseDateIndex = cursor.getColumnIndex(FavoritesContract.FavoriteEntry.COLUMN_RELEASE_DATE);
        int releaseTimeIndex = cursor.getColumnIndex(FavoritesContract.FavoriteEntry.COLUMN_RELEASE_TIME);
        int runningTimeIndex = cursor.getColumnIndex(FavoritesContract.FavoriteEntry.COLUMN_RUNNING_TIME);
        int voteCountIndex = cursor.getColumnIndex(FavoritesContract.FavoriteEntry.COLUMN_VOTE_COUNT);
        int voteAvgIndex = cursor.getColumnIndex(FavoritesContract.FavoriteEntry.COLUMN_VOTE_AVG);
        int posterPathIndex = cursor.getColumnIndex(FavoritesContract.FavoriteEntry.COLUMN_POSTER_PATH);

        row_id = cursor.getInt(rowIdIndex);
        movie_id = cursor.getInt(movieIdIndex);
        title = cursor.getString(titleIndex);
        original_title = cursor.getString(originalTitleIndex);
        overview = cursor.getString(overviewIndex);
        poster_path = cursor.getString(posterPathIndex);
        release_date = cursor.getString(releaseDateIndex);
        release_time = cursor.getString(releaseTimeIndex);
        vote_count = cursor.getInt(voteCountIndex);
        vote_average = cursor.getInt(voteAvgIndex);
        popularity = cursor.getInt(posterPathIndex);
        video = (cursor.getInt(posterPathIndex) == 1);
        running_time = cursor.getString(runningTimeIndex);
    }
}
