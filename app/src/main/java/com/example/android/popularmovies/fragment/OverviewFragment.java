package com.example.android.popularmovies.fragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.MovieItem;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.FavoritesContract;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.MissingFormatArgumentException;

/**
 * Created by Gboyega.Dada on 5/10/2017.
 */

public class OverviewFragment extends Fragment implements LoaderManager.LoaderCallbacks<String> {

    View mView;
    ContentResolver mContentResolver;

    MovieItem mMovieData;
    Uri mMovieUri;
    Button mFavoriteButton;

    private static final String TAG = OverviewFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fv_overview, container, false);
        mContentResolver = getActivity().getContentResolver();

        Bundle bundle = getArguments();
        if (null == bundle) {
            // WTF
            throw new MissingFormatArgumentException("Arguments bundle missing. No way to get Movie ID!");
        }

        this.mMovieData = Parcels.unwrap(bundle.getParcelable("movie_data"));
        this.mMovieUri = FavoritesContract.FavoriteEntry.CONTENT_URI.buildUpon()
                .appendPath("m")
                .appendPath(""+mMovieData.movie_id)
                .build();

        ImageView poster = (ImageView) mView.findViewById(R.id.iv_poster);
        TextView main_title = (TextView) mView.findViewById(R.id.tv_details_header);
        TextView release_year = (TextView) mView.findViewById(R.id.tv_release_year);
        TextView running_time = (TextView) mView.findViewById(R.id.tv_running_time);
        TextView rating = (TextView) mView.findViewById(R.id.tv_rating);
        TextView overview = (TextView) mView.findViewById(R.id.tv_overview);
        mFavoriteButton = (Button) mView.findViewById(R.id.btn_favorite);


        Picasso.with(getActivity())
                .load("http://image.tmdb.org/t/p/w500"+mMovieData.poster_path)
                .placeholder(R.drawable.ic_movie_poster_placeholder)
                .error(R.drawable.ic_broken_image)
                .into(poster);

        main_title.setText(mMovieData.title);

        String [] dateParts = mMovieData.release_date.split("-");

        release_year.setText(dateParts[0]);
        running_time.setText(getString(R.string.details_date_label)+" "+mMovieData.release_date); // mMovieData.running_time;
        rating.setText(getString(R.string.details_rating_label)+" "+mMovieData.vote_average + "/10");
        overview.setText(mMovieData.overview);



        mFavoriteButton.setOnClickListener(new FavButtonClickHandler());

        refreshFavoriteStatus(null);

        return mView;
    }

    private class FavButtonClickHandler implements View.OnClickListener {
        public void onClick(View v) {
            Boolean favorited = checkFavoriteStatus();

            if (favorited) {
                int id = mContentResolver.delete(mMovieUri, null, null);
                if (id != 0) favorited = false;
            } else {
                ContentValues contentValues = new ContentValues();

                contentValues.put(FavoritesContract.FavoriteEntry.COLUMN_MOVIE_ID, mMovieData.movie_id);
                contentValues.put(FavoritesContract.FavoriteEntry.COLUMN_ORIGINAL_TITLE, mMovieData.original_title);
                contentValues.put(FavoritesContract.FavoriteEntry.COLUMN_TITLE, mMovieData.title);
                contentValues.put(FavoritesContract.FavoriteEntry.COLUMN_OVERVIEW, mMovieData.overview);
                contentValues.put(FavoritesContract.FavoriteEntry.COLUMN_POPULARITY, mMovieData.popularity);
                contentValues.put(FavoritesContract.FavoriteEntry.COLUMN_POSTER_PATH, mMovieData.poster_path);
                contentValues.put(FavoritesContract.FavoriteEntry.COLUMN_RELEASE_DATE, mMovieData.release_date);
                contentValues.put(FavoritesContract.FavoriteEntry.COLUMN_RELEASE_TIME, mMovieData.release_time);
                contentValues.put(FavoritesContract.FavoriteEntry.COLUMN_VIDEO, mMovieData.video);
                contentValues.put(FavoritesContract.FavoriteEntry.COLUMN_VOTE_AVG, mMovieData.vote_average);
                contentValues.put(FavoritesContract.FavoriteEntry.COLUMN_VOTE_COUNT, mMovieData.vote_count);
                contentValues.put(FavoritesContract.FavoriteEntry.COLUMN_RUNNING_TIME, mMovieData.running_time);

                Uri uri = mContentResolver.insert(FavoritesContract.FavoriteEntry.CONTENT_URI, contentValues);
                favorited = true;
            }

            refreshFavoriteStatus(favorited);
        }
    }

    public boolean checkFavoriteStatus() {
        Cursor cursor = mContentResolver.query(mMovieUri,
                null,
                null,
                null,
                FavoritesContract.FavoriteEntry.COLUMN_MOVIE_ID);


        return (cursor.getCount() > 0);
    }

    public void refreshFavoriteStatus(Boolean status) {
        status = (null == status) ? checkFavoriteStatus() : status;
        int favBtnLabelID = status ? R.string.unmark_favorite : R.string.mark_favorite;
        int favBtnIconID = status ? R.drawable.ic_fav_active_accent : R.drawable.ic_fav_inactive;

        mFavoriteButton.setText(getString(favBtnLabelID));
        mFavoriteButton.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(getActivity(), favBtnIconID),
                null, null, null);
    }



    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(getActivity()) {
            String mData;

            @Override
            protected void onStartLoading() {
                /* If no arguments were passed, we don't have a query to perform. Simply return. */
                if (args == null) return;


                if (null != mData) {
                    deliverResult(mData); // use cache
                } else {
                    forceLoad(); // force load
                }
            }


            @Override
            public String loadInBackground() {

                // Will implement to load data

                // Query and load all task data in the background; sort by priority
                // [Hint] use a try/catch block to catch any errors in loading data
                try {
                    Cursor cursor = mContentResolver.query(mMovieUri,
                            null,
                            null,
                            null,
                            FavoritesContract.FavoriteEntry.COLUMN_MOVIE_ID);
                    return new String((cursor.getCount() > 0) ? "true" : "false"); // cursor;

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }

            }

            @Override
            public void deliverResult(String data) {
                mData = data;
                super.deliverResult(data);
            }

        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        switch (data) {
            case "true":
                mFavoriteButton.setText(getString(R.string.mark_favorite));
                break;
            default:
                mFavoriteButton.setText(getString(R.string.unmark_favorite));
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

}
