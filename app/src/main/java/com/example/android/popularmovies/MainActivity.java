package com.example.android.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.data.FavoritesContract;
import com.example.android.popularmovies.utils.Fetch;

import org.parceler.Parcels;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<MovieList> {

    private static final String TAG = MainActivity.class.getSimpleName();
    final protected static String QUERY_URL_EXTRA = "query_url_extra";
    final protected static String QUERY_MODE_EXTRA = "query_mode_extra";

    /*
     * This number will uniquely identify our Loader and is chosen arbitrarily. You can change this
     * to any number you like, as long as you use the same variable name.
     */
    private static final int MAIN_ACTIVITY_LOADER = 25;

    RecyclerViewAdapter mMoviesAdapter;
    String mActiveFilter;
    String mResponseString;
    BottomNavigationView bnvMenuView;
    RecyclerView mRecyclerView;
    TextView mErrorMessageDisplay;
    GridLayoutManager mLayoutManager;
    public static LinearLayoutManager.SavedState mBundleRecyclerViewState;
    ProgressBar mLoadingIndicator;



    private String API_KEY;
    private static final int GRID_SPAN_COUNT = 2;
    private static final String LIST_INSTANCE_STATE = "list_instance_state";
    private static final String MOVIES_JSON_STRING = "movies_json_string";
    private static final String ACTIVE_FILTER_HANDLE = "active_filter_handle";
    private static final String BASE_URI = "https://api.themoviedb.org/3/movie";
    private static final String MOST_POPULAR_URI = BASE_URI+"/popular";
    private static final String TOP_RATED_URI = BASE_URI+"/top_rated";

    private static final String FILTER_POPULAR = "popular";
    private static final String FILTER_TOP_RATED = "top_rated";
    private static final String FILTER_FAVORITES = "favorites";
    private static final String DEFAULT_FILTER_HANDLE = FILTER_POPULAR;

    private static final String QUERY_MODE_CURSOR = "cursor";
    private static final String QUERY_MODE_API = "api";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (null != savedInstanceState) {
            mBundleRecyclerViewState = savedInstanceState.getParcelable(LIST_INSTANCE_STATE);
            mResponseString = savedInstanceState.getString(MOVIES_JSON_STRING);
            mActiveFilter = savedInstanceState.getString(ACTIVE_FILTER_HANDLE);
        }


        // Fetch our API KEY from /res/values/secrets.xml
        // Note: I have added secrets.xml to gitignore. To run this project
        // replace this with your own KEY.
        // TODO (1): Add The Movie DB API KEY
        API_KEY = getString(R.string.themoviedb_api_key);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        mLayoutManager = new GridLayoutManager(this, GRID_SPAN_COUNT);

        mMoviesAdapter = new RecyclerViewAdapter(this, new RecyclerViewListener());

        mRecyclerView.setAdapter(mMoviesAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);

        bnvMenuView = (BottomNavigationView) findViewById(R.id.bnv_navigation);

        bnvMenuView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        switch(item.getItemId()) {
                            case R.id.bnv_action_show_most_popular:
                                return loadData(FILTER_POPULAR);
                            case R.id.bnv_action_show_top_rated:
                                return loadData(FILTER_TOP_RATED);
                            case R.id.bnv_action_show_favorites:
                                return loadData(FILTER_FAVORITES);
                            default:
                                return false;
                        }
                    }
                }
         );


        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);


        // initialize the AsyncTaskLoader
        getSupportLoaderManager().initLoader(MAIN_ACTIVITY_LOADER, null, this);

        // If app hasn't been opened before load our default screen.
        if (null == savedInstanceState) loadData(DEFAULT_FILTER_HANDLE);
    }


    public interface OnItemClickListener {
        void onClick(View view, MovieItem item);
    }

    private class RecyclerViewListener implements OnItemClickListener {
        @Override
        public void onClick(View view, MovieItem movie) {
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            intent.putExtra("movie_data", Parcels.wrap(movie));
            startActivity(intent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(LIST_INSTANCE_STATE, mRecyclerView.getLayoutManager().onSaveInstanceState());
        outState.putString(MOVIES_JSON_STRING, mResponseString);
        outState.putString(ACTIVE_FILTER_HANDLE, mActiveFilter);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // restore movies data
        if (mResponseString != null) {
            mMoviesAdapter.setData(new MovieList(mResponseString));
        }

        // restore RecyclerView state
        if (mBundleRecyclerViewState != null) {
            mRecyclerView.getLayoutManager().onRestoreInstanceState(mBundleRecyclerViewState);
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
                return loadData(FILTER_POPULAR);
            case R.id.action_show_top_rated:
                return loadData(FILTER_TOP_RATED);
            case R.id.action_show_favorites:
                return loadData(FILTER_FAVORITES);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected String getUrl(String resource, int page) {
        switch (resource) {
            case FILTER_TOP_RATED:
                return TOP_RATED_URI+"?api_key="+API_KEY+"&language=en-US&page="+page;
            case FILTER_POPULAR:
                return MOST_POPULAR_URI+"?api_key="+API_KEY+"&language=en-US&page="+page;
            default:
                return "NOT_FOUND";
        }
    }

    public boolean loadData(String handle) {

        String queryMode;
        switch(handle) {
            case FILTER_FAVORITES:
                queryMode = QUERY_MODE_CURSOR;
                break;
            case FILTER_POPULAR:
            case FILTER_TOP_RATED:
            default:
                // mRecyclerView.setAdapter(mMoviesAdapter);
                queryMode = QUERY_MODE_API;
        }

        String url = getUrl(handle, 1);
        Bundle queryBundle = new Bundle();
        queryBundle.putString(QUERY_URL_EXTRA, url);
        queryBundle.putString(QUERY_MODE_EXTRA, queryMode);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<MovieList> mainActivityLoader = loaderManager.getLoader(MAIN_ACTIVITY_LOADER);
        if (mainActivityLoader == null) {
            loaderManager.initLoader(MAIN_ACTIVITY_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(MAIN_ACTIVITY_LOADER, queryBundle, this);
        }

        return true;
    }

    /**
     * This method will make the View for the weather data visible and
     * hide the error message.
     */
    private void showMovieListView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the weather
     * View.
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<MovieList> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<MovieList>(this) {
            MovieList mData;
            String mQueryMode;

            @Override
            protected void onStartLoading() {
                /* If no arguments were passed, 0154080089 we don't have a query to perform. Simply return. */
                if (args == null) return;

                mQueryMode = args.getString(QUERY_MODE_EXTRA);


                if (null != mData && mQueryMode != QUERY_MODE_CURSOR) {
                    // Only use cache if we doing remote query. For SQLite cursor mode
                    // we need to reload in case the user 'unfavorites' a movie and that needs to
                    // show in the grid
                    deliverResult(mData); // use cache
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad(); // force load
                }
            }


            @Override
            public MovieList loadInBackground() {

                switch(args.getString(QUERY_MODE_EXTRA)) {
                    case QUERY_MODE_API:
                        return fetchFromAPI();
                    case QUERY_MODE_CURSOR:
                        return fetchFromCursor();
                    default:
                        return null;
                }

            }

            @Override
            public void deliverResult(MovieList data) {
                mData = data;
                super.deliverResult(data);
            }


            private MovieList fetchFromCursor() {
                // Will implement to load data

                // Query and load all task data in the background; sort by priority
                // [Hint] use a try/catch block to catch any errors in loading data

                try {
                    Cursor cursor = getContentResolver().query(FavoritesContract.FavoriteEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            FavoritesContract.FavoriteEntry.COLUMN_MOVIE_ID);
                    return new MovieList(cursor);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            private MovieList fetchFromAPI() {
                /* Extract the search query from the args using our constant */
                String url = args.getString(QUERY_URL_EXTRA);

                // Request a string response from the provided URL.
                String response = Fetch.get(url);
                return (response != "UNDEFINED")
                        ? new MovieList(response)
                        : null;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<MovieList> loader, MovieList data) {

        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (data != null) {
            showMovieListView();
            mMoviesAdapter.setData(data);
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<MovieList> loader) {

    }
}
