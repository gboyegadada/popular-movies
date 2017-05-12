package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.popularmovies.data.FavoritesContract.FavoriteEntry;

/**
 * Created by Gboyega.Dada on 4/30/2017.
 */

public class FavoritesDbHelper extends SQLiteOpenHelper {

    // The name of the database
    private static final String DATABASE_NAME = "favoritesDb.db";

    // If you change the database schema, you must increment the database version
    private static final int VERSION = 1;

    public FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create tasks table (careful to follow SQL formatting rules)
        final String CREATE_TABLE = "CREATE TABLE "  + FavoriteEntry.TABLE_NAME + " (" +
                FavoriteEntry._ID + " INTEGER PRIMARY KEY, " +
                FavoriteEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                FavoriteEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_RELEASE_DATE + " DATE NOT NULL, " +
                FavoriteEntry.COLUMN_RELEASE_TIME + " VARCHAR(60) NOT NULL, " +
                FavoriteEntry.COLUMN_RUNNING_TIME + " VARCHAR(60) NOT NULL, " +
                FavoriteEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL, " +
                FavoriteEntry.COLUMN_VOTE_AVG + " SMALLINT NOT NULL, " +
                FavoriteEntry.COLUMN_POPULARITY + " INTEGER NOT NULL, " +
                FavoriteEntry.COLUMN_VIDEO + " BOOLEAN NOT NULL);";

        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
