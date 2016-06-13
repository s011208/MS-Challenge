package bj4.yhh.mschallenge.fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import bj4.yhh.mschallenge.R;
import bj4.yhh.mschallenge.Utilities;
import bj4.yhh.mschallenge.weather.TheDarkSkyForecastParser;
import bj4.yhh.mschallenge.weather.WeatherConfig;

/**
 * Created by User on 2016/6/11.
 */
public class CurrentWeatherFragment extends Fragment {
    private static final int REQUEST_FINE_LOCATION = 10001;

    private static final String SHARED_PREFERENCE = "weather_fragment";
    private static final String KEY_LOAD_TIME = "load_time";
    private static final String KEY_DATA = "data";

    private Geocoder mGeocoder;
    private TextView mLocation;
    private TextView mLocationHint;
    private TextView mCurrentWeather;
    private LinearLayout mWeatherContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.current_weather_fragment, null);
        mLocation = (TextView) root.findViewById(R.id.location);
        mLocationHint = (TextView) root.findViewById(R.id.location_hint);
        mCurrentWeather = (TextView) root.findViewById(R.id.current_weather);
        mWeatherContainer = (LinearLayout) root.findViewById(R.id.weather_container);
        mGeocoder = new Geocoder(getActivity());

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            initLocation();
        } else {
            mLocationHint.setVisibility(View.VISIBLE);
            mLocationHint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
                }
            });
            mWeatherContainer.setVisibility(View.GONE);
            mLocation.setVisibility(View.GONE);
        }
        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initLocation();
            }
        }
    }

    private void initLocation() {
        Location location = Utilities.getLastBestLocation(getActivity());
        try {
            List<Address> address = mGeocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (!address.isEmpty()) {
                String locality = address.get(0).getLocality();
                String adminArea = address.get(0).getAdminArea();
                String countryName = address.get(0).getCountryName();
                if (locality == null && adminArea == null && countryName == null) {
                    mLocation.setVisibility(View.INVISIBLE);
                    mLocationHint.setVisibility(View.INVISIBLE);
                }
                mLocation.setText((locality == null ? "" : locality + " ") +
                        (adminArea == null ? "" : adminArea + " ") + (countryName == null ? "" : countryName));
                mLocation.setVisibility(View.VISIBLE);
                mLocationHint.setVisibility(View.GONE);
            } else {
                mLocation.setVisibility(View.INVISIBLE);
                mLocationHint.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            mLocation.setVisibility(View.INVISIBLE);
            mLocationHint.setVisibility(View.INVISIBLE);
        }
        new AsyncTask<Void, Void, TheDarkSkyForecastParser>() {
            @Override
            protected TheDarkSkyForecastParser doInBackground(Void... params) {
                Context context = getActivity();
                if (context == null) return null;
                SharedPreferences spref = context.getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE);
                String data;
                // data will be expired after an hour
                if (spref.getLong(KEY_LOAD_TIME, -1) + Utilities.HOUR > SystemClock.elapsedRealtime()) {
                    // using old data
                    data = spref.getString(KEY_DATA, "");
                } else {
                    Location currentLocation = Utilities.getLastBestLocation(context);
                    final String queryAddress = WeatherConfig.getQueryAddress(
                            currentLocation.getLatitude(), currentLocation.getLongitude(), System.currentTimeMillis());
                    data = Utilities.getJSONFromUrl(queryAddress);
                    spref.edit().putLong(KEY_LOAD_TIME, SystemClock.elapsedRealtime()).putString(KEY_DATA, data).apply();
                }
                TheDarkSkyForecastParser parser = new TheDarkSkyForecastParser(data);
                if (!parser.isDataValid()) return null;
                return parser;
            }

            @Override
            protected void onPostExecute(TheDarkSkyForecastParser theDarkSkyForecastParser) {
                if (theDarkSkyForecastParser == null) return;
                StringBuilder currentWeatherString = new StringBuilder();
                currentWeatherString.append((int) (((float) theDarkSkyForecastParser.getCurrentlyData().getTemperature() - 32) * (5 / 9f)))
                        .append("°C").append("\n").append(theDarkSkyForecastParser.getCurrentlyData().getSummary());
                mCurrentWeather.setText(currentWeatherString.toString());
                mCurrentWeather.setCompoundDrawablesWithIntrinsicBounds(WeatherConfig.getIconResource(theDarkSkyForecastParser.getCurrentlyData().getIcon()), 0, 0, 0);
                mWeatherContainer.setVisibility(View.VISIBLE);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
