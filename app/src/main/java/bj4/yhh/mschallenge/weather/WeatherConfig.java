package bj4.yhh.mschallenge.weather;

import java.util.HashMap;
import java.util.Map;

import bj4.yhh.mschallenge.R;

/**
 * Created by yenhsunhuang on 2016/6/9.
 */
public class WeatherConfig {
    /**
     * https://api.forecast.io/forecast/(API_KEY)/(LATITUDE,LONGITUDE,[TIME(optional)])
     * https://api.forecast.io/forecast/bac319d82bed09c2f0879c3646d10fbb/25.0270150,121.4793150,1465408785
     */
    public static final String API_KEY = "bac319d82bed09c2f0879c3646d10fbb";

    private static final Map<String, Integer> sWeatherIconMap = new HashMap<>();

    static {
        sWeatherIconMap.put("clear-day", R.drawable.ic_weather_clear_day);
        sWeatherIconMap.put("clear-night", R.drawable.ic_weather_clear_night);
        sWeatherIconMap.put("rain", R.drawable.ic_weather_rain);
        sWeatherIconMap.put("snow", R.drawable.ic_weather_snow);
        sWeatherIconMap.put("sleet", R.drawable.ic_weather_sleet);
        sWeatherIconMap.put("wind", R.drawable.ic_weather_wind);
        sWeatherIconMap.put("fog", R.drawable.ic_weather_fog);
        sWeatherIconMap.put("cloudy", R.drawable.ic_weather_cloudy);
        sWeatherIconMap.put("partly-cloudy-day", R.drawable.ic_weather_partly_cloud_day);
        sWeatherIconMap.put("partly-cloudy-night", R.drawable.ic_weather_partly_cloud_night);
        sWeatherIconMap.put("hail", R.drawable.ic_weather_heil);
        sWeatherIconMap.put("thunderstorm", R.drawable.ic_weather_thunderstorm);
        sWeatherIconMap.put("tornado", R.drawable.ic_weather_tornado);
    }

    public static int getIconResource(String iconString) {
        return sWeatherIconMap.get(iconString);
    }
}
