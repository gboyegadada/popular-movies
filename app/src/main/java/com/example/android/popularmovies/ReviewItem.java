package com.example.android.popularmovies;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Gboyega.Dada on 5/8/2017.
 */

public class ReviewItem {
    public String id;
    public String author;
    public String content;
    public String url;

    public ReviewItem(JSONObject item) {
        try {
            this.id = item.getString("id");
            this.author = item.getString("author");
            this.content = item.getString("content");
            this.url = item.getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
