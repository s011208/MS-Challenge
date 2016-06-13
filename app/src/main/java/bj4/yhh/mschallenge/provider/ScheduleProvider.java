package bj4.yhh.mschallenge.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import bj4.yhh.mschallenge.Utilities;

/**
 * Created by yenhsunhuang on 2016/6/5.
 */
public class ScheduleProvider extends ContentProvider {
    private static final String TAG = "CalendarProvider";
    private static final boolean DEBUG = Utilities.DEBUG;

    private static final boolean CLEAR_DATABASE_IN_ADVANCE = Log.isLoggable("clear_db", Log.VERBOSE);
    private static final boolean DEBUG_DATABASE_DATA = Log.isLoggable("show_db_data", Log.VERBOSE);

    public static final String AUTHORITY = "bj4.yhh.mschallenge.ScheduleProvider";

    public static final int FALSE = 0;
    public static final int TRUE = 1;

    private static final int MATCHER_SCHEDULE_CONTENT = 0;
    private static final int MATCHER_WEATHER_CONTENT = 1;

    private static final UriMatcher sMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sMatcher.addURI(AUTHORITY, TableScheduleContent.TABLE_NAME, MATCHER_SCHEDULE_CONTENT);
        sMatcher.addURI(AUTHORITY, TableWeather.TABLE_NAME, MATCHER_WEATHER_CONTENT);
    }

    private SQLiteDatabase mDatabase;

    @Override
    public boolean onCreate() {
        mDatabase = new ProviderDatabase(getContext()).getWritableDatabase();
        if (CLEAR_DATABASE_IN_ADVANCE) {
            if (DEBUG)
                Log.w(TAG, "CLEAR DATABASE TABLE");
            mDatabase.delete(TableWeather.TABLE_NAME, null, null);
            mDatabase.delete(TableScheduleContent.TABLE_NAME, null, null);
        }
        if (DEBUG_DATABASE_DATA) {
            printDatabaseData(TableScheduleContent.URI);
            printDatabaseData(TableWeather.URI);
        }
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor rtn = null;
        switch (sMatcher.match(uri)) {
            case MATCHER_SCHEDULE_CONTENT:
                rtn = mDatabase.query(TableScheduleContent.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MATCHER_WEATHER_CONTENT:
                rtn = mDatabase.query(TableWeather.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
        }
        return rtn;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri rtn = null;
        switch (sMatcher.match(uri)) {
            case MATCHER_SCHEDULE_CONTENT:
                rtn = ContentUris.withAppendedId(uri, mDatabase.insert(TableScheduleContent.TABLE_NAME, null, values));
                break;
            case MATCHER_WEATHER_CONTENT:
                rtn = ContentUris.withAppendedId(uri, mDatabase.insert(TableWeather.TABLE_NAME, null, values));
                break;
        }
        return rtn;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rtn = 0;
        switch (sMatcher.match(uri)) {
            case MATCHER_SCHEDULE_CONTENT:
                rtn = mDatabase.delete(TableScheduleContent.TABLE_NAME, selection, selectionArgs);
                break;
            case MATCHER_WEATHER_CONTENT:
                rtn = mDatabase.delete(TableWeather.TABLE_NAME, selection, selectionArgs);
                break;
        }
        return rtn;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        int rtn = 0;
        switch (sMatcher.match(uri)) {
            case MATCHER_SCHEDULE_CONTENT:
                rtn = mDatabase.update(TableScheduleContent.TABLE_NAME, values, selection, selectionArgs);
                break;
            case MATCHER_WEATHER_CONTENT:
                rtn = mDatabase.update(TableWeather.TABLE_NAME, values, selection, selectionArgs);
                break;
        }
        return rtn;
    }

    private void printDatabaseData(Uri uri) {
        Log.i(TAG, "============ start to print " + uri.getPath() + " ============");
        Cursor cursor = query(uri, null, null, null, null, null);
        if (cursor != null) {
            try {
                String data = DatabaseUtils.dumpCursorToString(cursor);
                Log.d(TAG, "data: " + data);
            } finally {
                cursor.close();
            }
        }
        Log.i(TAG, "============ finish ============");
    }
}
