package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Gboyega.Dada on 4/30/2017.
 */

public class FavoritesContentProvider extends ContentProvider {

    public static final int FAVORITES = 100;
    public static final int FAVORITES_WITH_ID = 101;
    public static final int FAVORITES_WITH_MOVIE_ID = 102;
    public static UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // directory
        uriMatcher.addURI(FavoritesContract.AUTHORITY, FavoritesContract.PATH_FAVORITES, FAVORITES);

        // single item
        uriMatcher.addURI(FavoritesContract.AUTHORITY, FavoritesContract.PATH_FAVORITES+"/#", FAVORITES_WITH_ID);

        // single item with movie_id
        uriMatcher.addURI(FavoritesContract.AUTHORITY, FavoritesContract.PATH_FAVORITES+"/*", FAVORITES_WITH_MOVIE_ID);

        return uriMatcher;
    }

    FavoritesDbHelper mFavoritesDbHelper;

    @Override
    public boolean onCreate() {
        mFavoritesDbHelper = new FavoritesDbHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selections, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = mFavoritesDbHelper.getReadableDatabase();

        Cursor cursor;
        String id;

        switch (sUriMatcher.match(uri)) {
            case FAVORITES:
                cursor = db.query(
                        FavoritesContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selections,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FAVORITES_WITH_ID:
                id = uri.getLastPathSegment();
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};
                cursor = db.query(
                        FavoritesContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FAVORITES_WITH_MOVIE_ID:
                id = uri.getLastPathSegment();
                String mMovieSelection = FavoritesContract.FavoriteEntry.COLUMN_MOVIE_ID+"=?";
                String[] mMovieSelectionArgs = new String[]{id};
                cursor = db.query(
                        FavoritesContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        mMovieSelection,
                        mMovieSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mFavoritesDbHelper.getWritableDatabase();

        Uri returnUri;
        long id;
        switch(sUriMatcher.match(uri)) {
            case FAVORITES:
                id = db.insert(FavoritesContract.FavoriteEntry.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(FavoritesContract.FavoriteEntry.CONTENT_URI, id);

                } else {
                    throw new SQLException("Failed to insert row into: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {

        final SQLiteDatabase db = mFavoritesDbHelper.getWritableDatabase();

        String id;
        int deleted;
        String where;
        String[] whereArgs;

        switch (sUriMatcher.match(uri)) {
            case FAVORITES:
                where = "_id=*";
                deleted = db.delete(
                        FavoritesContract.FavoriteEntry.TABLE_NAME,
                        where,
                        null);
                break;
            case FAVORITES_WITH_ID:
                id = uri.getLastPathSegment();
                where = "_id=?";
                whereArgs = new String[]{id};
                deleted = db.delete(
                        FavoritesContract.FavoriteEntry.TABLE_NAME,
                        where,
                        whereArgs);
                break;
            case FAVORITES_WITH_MOVIE_ID:
                id = uri.getLastPathSegment();
                where = FavoritesContract.FavoriteEntry.COLUMN_MOVIE_ID+"=?";
                whereArgs = new String[]{id};
                deleted = db.delete(
                        FavoritesContract.FavoriteEntry.TABLE_NAME,
                        where,
                        whereArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        if (deleted != 0) {
            // A task was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
