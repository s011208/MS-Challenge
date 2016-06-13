package bj4.yhh.mschallenge.weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yenhsunhuang on 2016/6/9.
 */
public class TheDarkSkyForecastParser {
    private static final String JSON_LATITUDE = "latitude";
    private static final String JSON_LONGITUDE = "longitude";
    private static final String JSON_TIMEZONE = "timezone";
    private static final String JSON_OFFSET = "offset";
    private static final String JSON_HOURLY = "hourly";
    private static final String JSON_CURRENTLY = "currently";
    private static final String JSON_DAILY = "daily";
    private static final String JSON_DATA = "data";
    private static final String JSON_SUMMARY = "summary";
    private static final String JSON_ICON = "icon";
    private static final String JSON_TIME = "time";
    private static final String JSON_TEMPERATURE = "temperature";
    private static final String JSON_TEMPERATURE_MIN = "temperatureMin";
    private static final String JSON_TEMPERATURE_MAX = "temperatureMax";

    private String mRawJson;
    private String mLatitude, mLongitude;
    private String mTimeZone;
    private String mOffset;
    private WeatherData mCurrentlyData;
    private HourlyData mHourlyData;
    private DailyData mDailyData;
    private boolean mIsDataValid = false;

    public TheDarkSkyForecastParser(String rawJson) {
        mRawJson = rawJson;
        if (mRawJson == null) return;
        parse();
    }

    public void parse() {
        try {
            JSONObject json = new JSONObject(mRawJson);
            mLatitude = json.getString(JSON_LATITUDE);
            mLongitude = json.getString(JSON_LONGITUDE);
            mTimeZone = json.getString(JSON_TIMEZONE);
            mOffset = json.getString(JSON_OFFSET);
            mDailyData = new DailyData(json.getJSONObject(JSON_DAILY));
            mCurrentlyData = new WeatherData(json.getJSONObject(JSON_CURRENTLY));
            mHourlyData = new HourlyData(json.getJSONObject(JSON_HOURLY));
            mIsDataValid = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isDataValid() {
        return mIsDataValid;
    }

    public String getLatitude() {
        return mLatitude;
    }

    public String getLongitude() {
        return mLongitude;
    }

    public String getTimeZone() {
        return mTimeZone;
    }

    public String getOffset() {
        return mOffset;
    }

    public HourlyData getHourlyData() {
        return mHourlyData;
    }

    public WeatherData getCurrentlyData() {
        return mCurrentlyData;
    }

    public DailyData getDailyData() {
        return mDailyData;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TheDarkSkyForecastParser, mLatitude: ").append(mLatitude)
                .append(", mLongitude: ").append(mLongitude)
                .append(", mTimeZone: ").append(mTimeZone).append(", mOffset: ").append(mOffset)
                .append("\nmCurrentlyData: ").append(mCurrentlyData.toString())
                .append("\nmDailyData: ").append(mDailyData.toString())
                .append("\nmHourlyData: ").append(mHourlyData.toString());
        return builder.toString();
    }

    public static class DailyData {
        private WeatherData mWeatherData;

        private DailyData(JSONObject json) throws JSONException {
            parse(json);
        }

        private void parse(JSONObject json) throws JSONException {
            mWeatherData = new WeatherData(json.getJSONArray(JSON_DATA).getJSONObject(0));
        }

        public WeatherData getWeatherData() {
            return mWeatherData;
        }

        @Override
        public String toString() {
            return "DailyData, " + mWeatherData.toString();
        }
    }

    public static class HourlyData {
        private String mSummary;
        private String mIcon;
        private List<WeatherData> mHourlyData = new ArrayList<>();

        private HourlyData(JSONObject json) throws JSONException {
            parse(json);
        }

        private void parse(JSONObject json) throws JSONException {
            mSummary = json.getString(JSON_SUMMARY);
            mIcon = json.getString(JSON_ICON);
            JSONArray hourlyArray = json.getJSONArray(JSON_DATA);
            for (int i = 0; i < hourlyArray.length(); ++i) {
                JSONObject weatherJsonData = hourlyArray.getJSONObject(i);
                mHourlyData.add(new WeatherData(weatherJsonData));
            }
        }

        public String getSummary() {
            return mSummary;
        }

        public String getIcon() {
            return mIcon;
        }

        public List<WeatherData> getData() {
            return mHourlyData;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("HourlyData, mSummary: " + mSummary + ", mIcon: " + mIcon);
            for (WeatherData data : mHourlyData) {
                builder.append("\n");
                builder.append(data.toString());
            }
            return builder.toString();
        }
    }

    public static class WeatherData {
        private long mTime;
        private int mTemperature = -1;
        private int mTemperatureMin = -1;
        private int mTemperatureMax = -1;
        private String mSummary;
        private String mIcon;

        private WeatherData(JSONObject json) throws JSONException {
            parse(json);
        }

        private void parse(JSONObject json) throws JSONException {
            mTime = json.getLong(JSON_TIME);
            mSummary = json.getString(JSON_SUMMARY);
            mIcon = json.getString(JSON_ICON);
            if (json.has(JSON_TEMPERATURE)) {
                mTemperature = (int) json.getDouble(JSON_TEMPERATURE);
            }
            if (json.has(JSON_TEMPERATURE_MIN)) {
                mTemperatureMax = (int) json.getDouble(JSON_TEMPERATURE_MIN);
            }
            if (json.has(JSON_TEMPERATURE_MAX)) {
                mTemperatureMax = (int) json.getDouble(JSON_TEMPERATURE_MAX);
            }
            if (mTemperature == -1) {
                mTemperature = (mTemperatureMax + mTemperatureMin) / 2;
            }
        }

        public long getTime() {
            return mTime;
        }

        public String getSummary() {
            return mSummary;
        }

        public String getIcon() {
            return mIcon;
        }

        public int getTemperature() {
            return mTemperature;
        }

        @Override
        public String toString() {
            return "WeatherData, mTime: " + mTime + ", mSummary: " + mSummary + ", mIcon: " + mIcon;
        }
    }
}
