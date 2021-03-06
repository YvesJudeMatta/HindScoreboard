package com.yvesmatta.hindscoreboard.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.yvesmatta.hindscoreboard.DBOpenHelper;

public class PlayerProvider extends ContentProvider {

    private static final String AUTHORITY = "com.yvesmatta.hindscoreboard.playerprovider";
    private static final String BASE_PATH = "hind_players";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    // Constants to identify the requested operation
    private static final int PLAYER = 1;
    private static final int PLAYER_ID = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, PLAYER);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", PLAYER_ID);
    }

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        DBOpenHelper helper = new DBOpenHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] strings, String s, String[] strings1, String s1) {
        if (uriMatcher.match(uri) == PLAYER_ID) {
            s = DBOpenHelper.PLAYER_ID + "=" + uri.getLastPathSegment();
        }

        return database.query(
                DBOpenHelper.TABLE_PLAYER,
                DBOpenHelper.ALL_COLUMNS,
                s,
                null, null, null,
                DBOpenHelper.CREATED_AT
        );
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        long id = database.insert(DBOpenHelper.TABLE_PLAYER, null, contentValues);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, String s, String[] strings) {
        return database.delete(DBOpenHelper.TABLE_PLAYER, s, strings);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String s, String[] strings) {
        return database.update(DBOpenHelper.TABLE_PLAYER, contentValues, s, strings);
    }
}
