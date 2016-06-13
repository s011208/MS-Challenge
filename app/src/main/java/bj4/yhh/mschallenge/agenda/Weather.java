package bj4.yhh.mschallenge.agenda;

/**
 * Created by yenhsunhuang on 2016/6/6.
 */
public class Weather extends AgendaItem {
    private long mDateTime;
    private int mWeatherTime;
    private int mTemperature;

    private String mSummary, mIconString;

    public Weather(int weatherTime, long dateTime) {
        mWeatherTime = weatherTime;
        mDateTime = dateTime;
    }

    public void setTemperature(int temperature) {
        mTemperature = (int) (((float) temperature - 32) * (5 / 9f));
    }

    public int getTemperature() {
        return mTemperature;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setIconString(String iconString) {
        mIconString = iconString;
    }

    public String getIconString() {
        return mIconString;
    }

    public long getDateTime() {
        return mDateTime;
    }

    public int getWeatherTime() {
        return mWeatherTime;
    }

}
