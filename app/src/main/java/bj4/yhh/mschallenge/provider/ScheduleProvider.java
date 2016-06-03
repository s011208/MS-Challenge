package bj4.yhh.mschallenge.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import bj4.yhh.mschallenge.Utilities;

/**
 * Created by yenhsunhuang on 2016/6/5.
 */
public class ScheduleProvider extends ContentProvider {
    private static final String TAG = "CalendarProvider";
    private static final boolean DEBUG = Utilities.DEBUG;

    public static final String AUTHORITY = "bj4.yhh.mschallenge.ScheduleProvider";

    public static final int FALSE = 0;
    public static final int TRUE = 1;

    private static final int MATCHER_SCHEDULE_CONTENT = 0;

    private static final UriMatcher sMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sMatcher.addURI(AUTHORITY, TableScheduleContent.TABLE_NAME, MATCHER_SCHEDULE_CONTENT);
    }

    private SQLiteDatabase mDatabase;

    @Override
    public boolean onCreate() {
        mDatabase = new ProviderDatabase(getContext()).getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor rtn = null;
        switch (sMatcher.match(uri)) {
            case MATCHER_SCHEDULE_CONTENT:
                rtn = mDatabase.query(TableScheduleContent.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
        }
        return rtn;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri rtn = null;
        switch (sMatcher.match(uri)) {
            case MATCHER_SCHEDULE_CONTENT:
                rtn = ContentUris.withAppendedId(uri, mDatabase.insert(TableScheduleContent.TABLE_NAME, null, values));
                break;
        }
        return rtn;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rtn = 0;
        switch (sMatcher.match(uri)) {
            case MATCHER_SCHEDULE_CONTENT:
                rtn = mDatabase.delete(TableScheduleContent.TABLE_NAME, selection, selectionArgs);
                break;
        }
        return rtn;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rtn = 0;
        switch (sMatcher.match(uri)) {
            case MATCHER_SCHEDULE_CONTENT:
                rtn = mDatabase.update(TableScheduleContent.TABLE_NAME, values, selection, selectionArgs);
                break;
        }
        return rtn;
    }
}
