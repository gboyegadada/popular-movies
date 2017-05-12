package com.example.android.popularmovies;

import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.ArrayList;

/**
 * Created by Gboyega.Dada on 4/15/2017.
 */

public class MovieList extends ArrayList<MovieItem> {

    public static final String MODE_JSON = "json";
    public static final String MODE_CURSOR = "cursor";

    public String mMode;
    private JSONArray mJson;
    private Cursor mCursor;


    public MovieList(Cursor cursor) {
        this.mCursor = cursor;
        this.mMode = this.MODE_CURSOR;
    }

    public MovieList(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            mJson = json.getJSONArray("results");
            this.mMode = this.MODE_JSON;
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean isEmpty() {
        switch(mMode) {
            case MODE_JSON:
                return mJson.length() == 0;
            case MODE_CURSOR:
                return mCursor.getCount() == 0;
            default:
                return super.isEmpty();
        }
    }

    @Override
    public int size() {
        switch(mMode) {
            case MODE_JSON:
                return mJson.length();
            case MODE_CURSOR:
                return mCursor.getCount();
            default:
                return super.size();
        }
    }

    @Override
    public MovieItem get(int position) {
        switch(mMode) {
            case MODE_JSON:
                return getFromJson(position);
            case MODE_CURSOR:
                return getFromCursor(position);
            default:
                return null;
        }
    }

    @Override
    public MovieItem set(int index, MovieItem item) {
        // do nothing (shhh!!)
        return item;
    }

    @Override
    public void clear() {
        this.mMode = null;
        this.mCursor = null;
        this.mJson = null;
    }


    public MovieItem getFromJson(int position) {
        try {
            return new MovieItem(mJson.getJSONObject(position));
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public MovieItem getFromCursor(int position) {
        int original_position = mCursor.getPosition();

        mCursor.moveToPosition(position);
        MovieItem item = new MovieItem(mCursor);

        // As you were!
        mCursor.moveToPosition(original_position);
        return item;
    }
}
