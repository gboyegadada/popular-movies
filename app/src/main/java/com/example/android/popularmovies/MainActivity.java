package com.example.android.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerViewAdapter adapter;
    RequestQueue queue;
    JSONObject responseObject;
    String responseString;
    BottomNavigationView bnvMenuView;
    RecyclerView recyclerView;
    GridLayoutManager layoutManager;
    public static LinearLayoutManager.SavedState mBundleRecyclerViewState;
    ProgressBar loadingIndicator;



    private String API_KEY;
    private static final int GRID_SPAN_COUNT = 2;
    private static final String LIST_INSTANCE_STATE = "list_instance_state";
    private static final String MOVIES_JSON_STRING = "movies_json_string";
    private static final String BASE_URI = "https://api.themoviedb.org/3/movie";
    private static final String MOST_POPULAR_URI = BASE_URI+"/popular";
    private static final String TOP_RATED_URI = BASE_URI+"/top_rated";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (null != savedInstanceState) {
            mBundleRecyclerViewState = savedInstanceState.getParcelable(LIST_INSTANCE_STATE);
            responseString = savedInstanceState.getString(MOVIES_JSON_STRING);
        }


        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(this);

        // Fetch our API KEY from /res/values/secrets.xml
        // Note: I have added secrets.xml to gitignore. To run this project
        // replace this with your own KEY.
        // TODO (1): Add The Movie DB API KEY
        API_KEY = getString(R.string.themoviedb_api_key);

        recyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        layoutManager = new GridLayoutManager(this, GRID_SPAN_COUNT);

        adapter = new RecyclerViewAdapter(this, new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, MovieItem movie) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("movie_data", Parcels.wrap(movie));
                startActivity(intent);
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        bnvMenuView = (BottomNavigationView) findViewById(R.id.bnv_navigation);

        bnvMenuView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        switch(item.getItemId()) {
                            case R.id.bnv_action_show_most_popular:
                                return loadData("popular");
                            case R.id.bnv_action_show_top_rated:
                                return loadData("top_rated");
                            default:
                                return false;
                        }
                    }
                }
         );


        loadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        // loadData("popular");

        if (null == savedInstanceState) {
            loadData("popular"); // init
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(LIST_INSTANCE_STATE, recyclerView.getLayoutManager().onSaveInstanceState());
        outState.putString(MOVIES_JSON_STRING, responseString);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // restore movies data
        if (responseString != null) {
            adapter.setData(new MovieList().fromJSON(responseString));
        }

        // restore RecyclerView state
        if (mBundleRecyclerViewState != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(mBundleRecyclerViewState);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_show_most_popular:
                return loadData("popular");
            case R.id.action_show_top_rated:
                return loadData("top_rated");
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected String getUrl(String resource, int page) {
        switch (resource) {
            case "top_rated":
                return TOP_RATED_URI+"?api_key="+API_KEY+"&language=en-US&page="+page;
            case "popular":
                return MOST_POPULAR_URI+"?api_key="+API_KEY+"&language=en-US&page="+page;
            default:
                return "NOT_FOUND";
        }
    }

    public boolean loadData(String handle) {

        // Request a string response from the provided URL.
        String url = getUrl(handle, 1);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Update data and notify Adapter
                        adapter.setData(new MovieList().fromJSON(response));

                        // Save response for Instance Restore
                        responseString = response;

                        // Hide loader
                        loadingIndicator.setVisibility(View.INVISIBLE);

                        // Show grid view
                        // recyclerView.setVisibility(View.VISIBLE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // TODO (2): Handle request error
                error.printStackTrace();
            }
        });

        // Hide grid view
        // recyclerView.setVisibility(View.INVISIBLE);

        // Show loader
        loadingIndicator.setVisibility(View.VISIBLE);

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        return true;
    }

}
