package bj4.yhh.mschallenge;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import bj4.yhh.mschallenge.provider.Schedule;
import bj4.yhh.mschallenge.provider.TableScheduleContent;

/**
 * Created by User on 2016/6/2.
 */
public class Utilities {
    private static final String TAG = "Utilities";
    public static final boolean DEBUG = true;

    public static final int SIZE_OF_DATE = 42;

    public static final long SECOND = 1000;
    public static final long MINUTE = 60 * SECOND;
    public static final long HOUR = 60 * MINUTE;
    public static final long DAY = 24 * HOUR;

    private static final List<String> sMonthStringList = new ArrayList<>();

    private static final List<String> sShortWeekdayList = new ArrayList<>();

    static {
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
        sMonthStringList.addAll(Arrays.asList(dateFormatSymbols.getMonths()));
        sShortWeekdayList.addAll(Arrays.asList(dateFormatSymbols.getShortWeekdays()));
        // dateFormatSymbols.getShortWeekdays provides {"", "sun", "mon"...}
        sShortWeekdayList.remove(0);
    }

    private Utilities() {
    }

    public static List<String> getMonthString() {
        return new ArrayList(sMonthStringList);
    }

    public static List<String> getShortWeekdayString() {
        return new ArrayList<>(sShortWeekdayList);
    }

    /**
     * get all date data which will be display on calendar view
     *
     * @param y year
     * @param m month
     * @return all date data start with Sunday
     */
    public static List<Date> getAllDateAtYearAndMonth(int y, int m) {
        return getAllDateAtYearAndMonth(y, m, true);
    }

    private static List<Date> getAllDateAtYearAndMonth(final int y, final int m, final boolean fillUpSpace) {
        final List<Date> rtn = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, y);
        calendar.set(Calendar.MONTH, m);
        Utilities.clearCalendarOffset(calendar);
        final int dayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        if (fillUpSpace) {
            // filling up previous
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            final int dayOfWeekOfFirstDay = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeekOfFirstDay != Calendar.SUNDAY) {
                int previousYear = y;
                int previousMonth = m - 1;
                if (previousMonth < Calendar.JANUARY) {
                    previousMonth = Calendar.DECEMBER;
                    --previousYear;
                }
                List<Date> previousDates = getAllDateAtYearAndMonth(previousYear, previousMonth, false);
                final int startIndex = previousDates.size() - dayOfWeekOfFirstDay + 1;
                final int endIndex = previousDates.size();
                rtn.addAll(previousDates.subList(startIndex, endIndex));
            }
        }

        for (int i = 0; i < dayOfMonth; ++i) {
            calendar.set(Calendar.DAY_OF_MONTH, i + 1);
            rtn.add(new Date(calendar.getTimeInMillis()));
        }

        if (fillUpSpace) {
            // filling up following
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            int nextYear = y;
            int nextMonth = m + 1;
            if (nextMonth > Calendar.DECEMBER) {
                nextMonth = Calendar.JANUARY;
                ++nextYear;
            }
            // we keep all data size to SIZE_OF_DATE
            List<Date> nextDates = getAllDateAtYearAndMonth(nextYear, nextMonth, false);
            for (int i = 0; i < nextDates.size() && rtn.size() < SIZE_OF_DATE; ++i) {
                rtn.add(nextDates.get(i));
            }
        }
        return rtn;
    }

    public static boolean isTimeOverlapping(long startX, long finishX, long startY, long finishY) {
        return Math.max(startX, startY) <= Math.min(finishX, finishY);
    }

    public static ArrayList<Schedule> getSchedulesBetweenDate(long scheduleStartRange, long scheduleFinishRange, Context context) {
        Cursor scheduleCursor = context.getContentResolver().query(TableScheduleContent.URI, null,
                "(" + TableScheduleContent.COLUMN_START_TIME + " > " + scheduleStartRange +
                        " and " + TableScheduleContent.COLUMN_START_TIME + " < " + scheduleFinishRange +
                        ") or (" + TableScheduleContent.COLUMN_FINISH_TIME + " > " + scheduleStartRange +
                        " and " + TableScheduleContent.COLUMN_FINISH_TIME + " < " + scheduleFinishRange + ")",
                null, TableScheduleContent.COLUMN_IS_WHOLE_DAY + " desc, " + TableScheduleContent.COLUMN_START_TIME);
        return Schedule.fromCursor(scheduleCursor);
    }

    public static int getDiffMinutes(int h1, int m1, int h2, int m2) {
        int time1 = h1 * 60 + m1;
        int time2 = h2 * 60 + m2;
        return Math.abs(time1 - time2);
    }

    /**
     * keep year, month & day of calendar
     *
     * @param c
     */
    public static void clearCalendarOffset(Calendar c) {
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);
    }

    public static String debugDateTime(long time) {
        return new SimpleDateFormat("yyyy MM dd").format(time);
    }

    public static String getJSONFromUrl(String address) {
        try {
            URL url = new URL(address);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                return stringBuilder.toString();
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            if (DEBUG) {
                Log.w(TAG, e.getMessage(), e);
            }
            return null;
        }
    }

    public static Location getLastBestLocation(Context context) throws SecurityException {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) {
            GPSLocationTime = locationGPS.getTime();
        }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if (0 < GPSLocationTime - NetLocationTime) {
            return locationGPS;
        } else {
            return locationNet;
        }
    }
}
