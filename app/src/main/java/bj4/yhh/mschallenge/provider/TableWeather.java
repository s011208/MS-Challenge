package bj4.yhh.mschallenge.provider;

import android.net.Uri;

/**
 * Created by yenhsunhuang on 2016/6/9.
 */
public class TableWeather {
    public static final int WEATHER_TIME_MORNING = 0;
    public static final int WEATHER_TIME_AFTERNOON = 1;
    public static final int WEATHER_TIME_NIGHT = 2;

    public static final String TABLE_NAME = "weather_data";

    public static Uri URI = Uri.parse("content://" + ScheduleProvider.AUTHORITY + "/" + TABLE_NAME);

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE_TIME = "d_time";
    public static final String COLUMN_WEATHER_TIME = "w_time";
    public static final String COLUMN_SUMMARY = "summary";
    public static final String COLUMN_ICON = "icon";
    public static final String COLUMN_TEMPERATURE = "temperature";
}
