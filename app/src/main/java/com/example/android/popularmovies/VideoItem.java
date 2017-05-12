package com.example.android.popularmovies;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Gboyega.Dada on 5/10/2017.
 */

public class VideoItem {
    public String id;
    public String iso_639_1;
    public String iso_3166_1;
    public String key;
    public String name;
    public String site;
    public int size;
    public String type;

    public VideoItem(JSONObject item) {
        try {
            this.id = item.getString("id");
            this.iso_639_1 = item.getString("iso_639_1");
            this.iso_3166_1 = item.getString("iso_3166_1");
            this.key = item.getString("key");
            this.name = item.getString("name");
            this.site = item.getString("site");
            this.size = item.getInt("size");
            this.type = item.getString("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
