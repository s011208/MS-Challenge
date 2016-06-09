package bj4.yhh.mschallenge.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yenhsunhuang on 2016/6/5.
 */
public class ProviderDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "provider_database.db";
    private static final int VERSION = 2;

    public ProviderDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTableScheduleContent(db);
        createTableWeather(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            createTableWeather(db);
            ++oldVersion;
        }
    }

    private static void createTableScheduleContent(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TableScheduleContent.TABLE_NAME + " ( "
                + TableScheduleContent.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TableScheduleContent.COLUMN_IS_WHOLE_DAY + " INTEGER DEFAULT " + ScheduleProvider.FALSE + ", "
                + TableScheduleContent.COLUMN_LOCATION + " TEXT,"
                + TableScheduleContent.COLUMN_NOTIFY + " INTEGER, "
                + TableScheduleContent.COLUMN_DESCRIPTION + " TEXT,"
                + TableScheduleContent.COLUMN_MEMBER + " TEXT,"
                + TableScheduleContent.COLUMN_START_TIME + " INTEGER NOT NULL, "
                + TableScheduleContent.COLUMN_FINISH_TIME + " INTEGER NOT NULL, "
                + TableScheduleContent.COLUMN_TITLE + " TEXT NOT NULL)");
    }

    private static void createTableWeather(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TableWeather.TABLE_NAME + " ( "
                + TableWeather.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TableWeather.COLUMN_DATE_TIME + " INTEGER NOT NULL,"
                + TableWeather.COLUMN_ICON + " TEXT NOT NULL,"
                + TableWeather.COLUMN_SUMMARY + " TEXT NOT NULL,"
                + TableWeather.COLUMN_TEMPERATURE + " INTEGER NOT NULL,"
                + TableWeather.COLUMN_WEATHER_TIME + " INTEGER NOT NULL)");
    }
}
