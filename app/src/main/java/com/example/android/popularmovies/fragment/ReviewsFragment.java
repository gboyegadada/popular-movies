package com.example.android.popularmovies.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.MovieItem;
import com.example.android.popularmovies.MovieList;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.ReviewItem;
import com.example.android.popularmovies.ReviewList;
import com.example.android.popularmovies.ReviewsRecyclerViewAdapter;
import com.example.android.popularmovies.utils.Fetch;

import org.parceler.Parcels;

import java.util.MissingFormatArgumentException;

/**
 * Created by Gboyega.Dada on 5/6/2017.
 */

public class ReviewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<ReviewList> {
    final protected static String QUERY_URL_EXTRA = "query_url_extra";

    /*
     * This number will uniquely identify our Loader and is chosen arbitrarily. You can change this
     * to any number you like, as long as you use the same variable name.
     */
    private static final int REVIEWS_LOADER = 250;

    MovieItem mMovieData;
    ReviewsRecyclerViewAdapter mAdapter;
    String mResponseString;
    RecyclerView mRecyclerView;
    TextView mErrorMessageDisplay;
    TextView mNoItemsMessageDisplay;
    LinearLayoutManager mLayoutManager;
    public static LinearLayoutManager.SavedState mBundleRecyclerViewState;
    ProgressBar mLoadingIndicator;

    View mView;


    private static final String TAG = ReviewsFragment.class.getSimpleName();

    private String API_KEY;
    private static final String LIST_INSTANCE_STATE = "list_instance_state";
    private static final String CONTENT_JSON_STRING = "movies_json_string";
    private static final String BASE_URI = "https://api.themoviedb.org/3/movie";
    private String mReviewsUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.mView = inflater.inflate(R.layout.fv_reviews, container, false);

        if (null != savedInstanceState) {
            mBundleRecyclerViewState = savedInstanceState.getParcelable(LIST_INSTANCE_STATE);
            mResponseString = savedInstanceState.getString(CONTENT_JSON_STRING);
        }

        Bundle bundle = getArguments();
        if (null == bundle) {
            // WTF
            throw new MissingFormatArgumentException("Arguments bundle missing. No way to get Movie ID!");
        }

        this.mMovieData = Parcels.unwrap(bundle.getParcelable("movie_data"));



        // Fetch our API KEY from /res/values/secrets.xml
        // Note: I have added secrets.xml to gitignore. To run this project
        // replace this with your own KEY.
        // TODO (1): Add The Movie DB API KEY
        API_KEY = getString(R.string.themoviedb_api_key);

        mReviewsUri = BASE_URI + "/" + mMovieData.movie_id + "/reviews?api_key="+API_KEY+"&language=en-US&page=1";;


        mRecyclerView = (RecyclerView) mView.findViewById(R.id.rv_reviews);
        mErrorMessageDisplay = (TextView) mView.findViewById(R.id.tv_reviews_error_message_display);
        mNoItemsMessageDisplay = (TextView) mView.findViewById(R.id.tv_empty_reviews);

        mLayoutManager = new LinearLayoutManager(getActivity());

        mAdapter = new ReviewsRecyclerViewAdapter(new RecyclerViewListener());

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);


        mLoadingIndicator = (ProgressBar) mView.findViewById(R.id.pb_reviews);


        // initialize the AsyncTaskLoader
        getLoaderManager().initLoader(REVIEWS_LOADER, null, this);

        // If app hasn't been opened before load our default screen.
        if (null == savedInstanceState) loadData();

        return mView;
    }


    public boolean loadData() {
        // Nothing to put in here really
        Bundle queryBundle = new Bundle();

        LoaderManager loaderManager = getLoaderManager();
        Loader<MovieList> mainActivityLoader = loaderManager.getLoader(REVIEWS_LOADER);
        if (mainActivityLoader == null) {
            loaderManager.initLoader(REVIEWS_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(REVIEWS_LOADER, queryBundle, this);
        }

        return true;
    }


    public interface OnItemClickListener {
        void onClick(View view, ReviewItem item);
    }

    private class RecyclerViewListener implements OnItemClickListener {
        @Override
        public void onClick(View view, ReviewItem item) {
            // Intent intent = new Intent(ReviewsFragment.this, DetailsActivity.class);
            // intent.putExtra("data", Parcels.wrap(item));
            // startActivity(intent);
            // overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }
    }

    @Override
    public Loader<ReviewList> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<ReviewList>(getActivity()) {
            ReviewList mData;

            @Override
            protected void onStartLoading() {
                /* If no arguments were passed, we don't have a query to perform. Simply return. */
                if (args == null) return;

                if (null != mData) {
                    deliverResult(mData); // use cache
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad(); // force load
                }
            }


            @Override
            public ReviewList loadInBackground() {
                /* Extract the search query from the args using our constant */
                // String url = args.getString(QUERY_URL_EXTRA);

                // Request a string response from the provided URL.
                String response = Fetch.get(mReviewsUri);
                return (response != "UNDEFINED")
                        ? new ReviewList(response)
                        : null;
            }

            @Override
            public void deliverResult(ReviewList data) {
                mData = data;
                super.deliverResult(data);
            }

        };
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
        /* Hide no items message */
        mNoItemsMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadFinished(Loader<ReviewList> loader, ReviewList data) {

        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (data != null) {
            showMovieListView();
            mAdapter.setData(data);

            mNoItemsMessageDisplay.setVisibility(data.isEmpty()
                    ? View.VISIBLE
                    : View.INVISIBLE);

            Log.d(TAG, "Reviews count: "+data.size());
            if (!data.isEmpty()) Log.d(TAG, "Review Content: "+data.get(0).author);

        } else {
            showErrorMessage();
        }

    }

    @Override
    public void onLoaderReset(Loader<ReviewList> loader) {

    }
}
