package com.example.android.popularmovies.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.MovieItem;
import com.example.android.popularmovies.MovieList;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.VideoItem;
import com.example.android.popularmovies.VideoList;
import com.example.android.popularmovies.VideosRecyclerViewAdapter;
import com.example.android.popularmovies.VideosRecyclerViewAdapter;
import com.example.android.popularmovies.utils.Fetch;

import org.parceler.Parcels;

import java.util.MissingFormatArgumentException;

/**
 * Created by Gboyega.Dada on 5/6/2017.
 */

public class VideosFragment extends Fragment implements LoaderManager.LoaderCallbacks<VideoList> {

    /*
     * This number will uniquely identify our Loader and is chosen arbitrarily. You can change this
     * to any number you like, as long as you use the same variable name.
     */
    private static final int VIDEOS_LOADER = 255;

    MovieItem mMovieData;
    VideosRecyclerViewAdapter mAdapter;
    String mResponseString;
    RecyclerView mRecyclerView;
    TextView mErrorMessageDisplay;
    TextView mNoItemsMessageDisplay;
    LinearLayoutManager mLayoutManager;
    public static LinearLayoutManager.SavedState mBundleRecyclerViewState;
    ProgressBar mLoadingIndicator;

    View mView;


    private static final String TAG = VideosFragment.class.getSimpleName();

    private String API_KEY;
    private static final String LIST_INSTANCE_STATE = "list_instance_state";
    private static final String CONTENT_JSON_STRING = "movies_json_string";
    private static final String BASE_URI = "https://api.themoviedb.org/3/movie";
    private String mTrailersUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.mView = inflater.inflate(R.layout.fv_videos, container, false);

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

        mTrailersUri = BASE_URI + "/" + mMovieData.movie_id + "/videos?api_key="+API_KEY+"&language=en-US&page=1";


        // Fetch our API KEY from /res/values/secrets.xml
        // Note: I have added secrets.xml to gitignore. To run this project
        // replace this with your own KEY.
        // TODO (1): Add The Movie DB API KEY
        API_KEY = getString(R.string.themoviedb_api_key);

        mRecyclerView = (RecyclerView) mView.findViewById(R.id.rv_videos);
        mErrorMessageDisplay = (TextView) mView.findViewById(R.id.tv_videos_error_message_display);
        mNoItemsMessageDisplay = (TextView) mView.findViewById(R.id.tv_empty_videos);

        mLayoutManager = new LinearLayoutManager(getActivity());

        mAdapter = new VideosRecyclerViewAdapter(new RecyclerViewListener());

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);


        mLoadingIndicator = (ProgressBar) mView.findViewById(R.id.pb_videos);


        // initialize the AsyncTaskLoader
        getLoaderManager().initLoader(VIDEOS_LOADER, null, this);

        // If app hasn't been opened before load our default screen.
        if (null == savedInstanceState) loadData();

        return mView;
    }


    public boolean loadData() {
        mLoadingIndicator.setVisibility(View.VISIBLE);

        // Nothing to put in here really
        Bundle queryBundle = new Bundle();

        LoaderManager loaderManager = getLoaderManager();
        Loader<MovieList> loader = loaderManager.getLoader(VIDEOS_LOADER);
        if (loader == null) {
            loaderManager.initLoader(VIDEOS_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(VIDEOS_LOADER, queryBundle, this);
        }

        return true;
    }


    public interface OnItemClickListener {
        void onClick(View view, VideoItem item);
    }

    private class RecyclerViewListener implements OnItemClickListener {
        @Override
        public void onClick(View view, VideoItem item) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:"+item.key));
            startActivity(i);
        }
    }

    @Override
    public Loader<VideoList> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<VideoList>(getActivity()) {
            VideoList mData;

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
            public VideoList loadInBackground() {
                /* Extract the search query from the args using our constant */
                // String url = args.getString(QUERY_URL_EXTRA);

                // Request a string response from the provided URL.
                String response = Fetch.get(mTrailersUri);
                return (response != "UNDEFINED")
                        ? new VideoList(response)
                        : null;
            }

            @Override
            public void deliverResult(VideoList data) {
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
        /* Then, make sure data is visible */
        if (mAdapter.getItemCount() > 0) {
            mNoItemsMessageDisplay.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.INVISIBLE);
            mNoItemsMessageDisplay.setVisibility(View.VISIBLE);
        }
    }

    /**
     * This method will make the error message visible and hide the weather
     * View.
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data and "no items" message */
        mRecyclerView.setVisibility(View.INVISIBLE);
        mNoItemsMessageDisplay.setVisibility(View.INVISIBLE);

        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadFinished(Loader<VideoList> loader, VideoList data) {

        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (data != null) {
            mAdapter.setData(data);
            showMovieListView();
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<VideoList> loader) {

    }
}
