package bj4.yhh.mschallenge.weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import bj4.yhh.mschallenge.Utilities;
import bj4.yhh.mschallenge.agenda.Weather;
import bj4.yhh.mschallenge.provider.TableWeather;

/**
 * Created by yenhsunhuang on 2016/6/9.
 */
public class RetrieveWeatherDataHelper extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "RetrieveWeatherData";
    private static final boolean DEBUG = Utilities.DEBUG;

    private static final int INDEX_OF_MORNING_TIME_OF_HOURLY_DATA = 6;
    private static final int INDEX_OF_AFTERNOON_TIME_OF_HOURLY_DATA = 12;
    private static final int INDEX_OF_NIGHT_TIME_OF_HOURLY_DATA = 18;

    private WeakReference<Context> mContext;
    private TextView mWeatherTextView;
    private Weather mWeather;
    private int mRequestIndex;

    public RetrieveWeatherDataHelper(Context context, Weather weather, TextView textView, int requestIndex) {
        mContext = new WeakReference<>(context);
        mWeather = weather;
        mWeatherTextView = textView;
        mRequestIndex = requestIndex;
    }

    @Override
    protected Void doInBackground(Void... params) {
        final Context context = mContext.get();
        if (context == null) return null;
        boolean findDataInProvider = false;
        Cursor cursorWeather = context.getContentResolver().query(TableWeather.URI, null, TableWeather.COLUMN_DATE_TIME + "=" + mWeather.getDateTime() + " and " + TableWeather.COLUMN_WEATHER_TIME + "=" + mWeather.getWeatherTime(), null, null, null);
        if (cursorWeather != null) {
            try {
                if (cursorWeather.moveToNext()) {
                    mWeather.setIconString(cursorWeather.getString(cursorWeather.getColumnIndex(TableWeather.COLUMN_ICON)));
                    mWeather.setSummary(cursorWeather.getString(cursorWeather.getColumnIndex(TableWeather.COLUMN_SUMMARY)));
                    mWeather.setTemperature(cursorWeather.getInt(cursorWeather.getColumnIndex(TableWeather.COLUMN_TEMPERATURE)));
                    findDataInProvider = true;
                }
            } finally {
                cursorWeather.close();
            }
        }
        if (!findDataInProvider) {
            // query from api
            try {
                Location currentLocation = Utilities.getLastBestLocation(context);
                final String address = WeatherConfig.getQueryAddress(
                        currentLocation.getLatitude(), currentLocation.getLongitude(), mWeather.getDateTime());
                if (DEBUG) {
                    Log.d(TAG, "query address: " + address);
                }
                String data = Utilities.getJSONFromUrl(address);
                TheDarkSkyForecastParser parser = new TheDarkSkyForecastParser(data);
                if (parser.isDataValid()) {
                    insertWeatherDataToProvider(context, parser, mWeather.getDateTime());
                    int index = INDEX_OF_MORNING_TIME_OF_HOURLY_DATA;
                    switch (mWeather.getWeatherTime()) {
                        case TableWeather.WEATHER_TIME_MORNING:
                            index = INDEX_OF_MORNING_TIME_OF_HOURLY_DATA;
                            break;
                        case TableWeather.WEATHER_TIME_AFTERNOON:
                            index = INDEX_OF_AFTERNOON_TIME_OF_HOURLY_DATA;
                            break;
                        case TableWeather.WEATHER_TIME_NIGHT:
                            index = INDEX_OF_NIGHT_TIME_OF_HOURLY_DATA;
                            break;
                    }
                    mWeather.setIconString(parser.getHourlyData().getData().get(index).getIcon());
                    mWeather.setSummary(parser.getHourlyData().getData().get(index).getIcon());
                    mWeather.setTemperature(parser.getHourlyData().getData().get(index).getTemperature());
                }
                if (DEBUG) {
                    Log.d(TAG, "parser result: " + parser.isDataValid());
                }
            } catch (SecurityException e) {
                // request permission in Calendar activity onCreate
                if (DEBUG) {
                    Log.w(TAG, "unexpected exception", e);
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        final int textViewTag = (int) mWeatherTextView.getTag();
        if (mRequestIndex == textViewTag && !TextUtils.isEmpty(mWeather.getIconString())) {
            mWeatherTextView.setCompoundDrawablesWithIntrinsicBounds(WeatherConfig.getIconResource(mWeather.getIconString()), 0, 0, 0);
            mWeatherTextView.setText(mWeather.getTemperature() + "°");
            mWeatherTextView.setContentDescription(mWeather.getSummary());
        }
    }

    private void insertWeatherDataToProvider(Context context, TheDarkSkyForecastParser parser, long dateTime) {
        ContentValues cv = new ContentValues();
        TheDarkSkyForecastParser.WeatherData morning = parser.getHourlyData().getData().get(INDEX_OF_MORNING_TIME_OF_HOURLY_DATA);
        cv.put(TableWeather.COLUMN_DATE_TIME, dateTime);
        cv.put(TableWeather.COLUMN_WEATHER_TIME, TableWeather.WEATHER_TIME_MORNING);
        cv.put(TableWeather.COLUMN_ICON, morning.getIcon());
        cv.put(TableWeather.COLUMN_SUMMARY, morning.getSummary());
        cv.put(TableWeather.COLUMN_TEMPERATURE, morning.getTemperature());
        context.getContentResolver().insert(TableWeather.URI, cv);

        cv.clear();
        TheDarkSkyForecastParser.WeatherData afternoon = parser.getHourlyData().getData().get(INDEX_OF_AFTERNOON_TIME_OF_HOURLY_DATA);
        cv.put(TableWeather.COLUMN_DATE_TIME, dateTime);
        cv.put(TableWeather.COLUMN_WEATHER_TIME, TableWeather.WEATHER_TIME_AFTERNOON);
        cv.put(TableWeather.COLUMN_ICON, afternoon.getIcon());
        cv.put(TableWeather.COLUMN_SUMMARY, afternoon.getSummary());
        cv.put(TableWeather.COLUMN_TEMPERATURE, afternoon.getTemperature());
        context.getContentResolver().insert(TableWeather.URI, cv);

        cv.clear();
        TheDarkSkyForecastParser.WeatherData night = parser.getHourlyData().getData().get(INDEX_OF_NIGHT_TIME_OF_HOURLY_DATA);
        cv.put(TableWeather.COLUMN_DATE_TIME, dateTime);
        cv.put(TableWeather.COLUMN_WEATHER_TIME, TableWeather.WEATHER_TIME_NIGHT);
        cv.put(TableWeather.COLUMN_ICON, night.getIcon());
        cv.put(TableWeather.COLUMN_SUMMARY, night.getSummary());
        cv.put(TableWeather.COLUMN_TEMPERATURE, night.getTemperature());
        context.getContentResolver().insert(TableWeather.URI, cv);
    }
}
