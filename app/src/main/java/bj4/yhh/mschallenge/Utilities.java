package bj4.yhh.mschallenge;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by User on 2016/6/2.
 */
public class Utilities {
    public static final boolean DEBUG = true;

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
            final int dayOfWeekOfLastDay = calendar.get(Calendar.DAY_OF_WEEK);
            int nextYear = y;
            int nextMonth = m + 1;
            if (nextMonth > Calendar.DECEMBER) {
                nextMonth = Calendar.JANUARY;
                ++nextYear;
            }
            List<Date> nextDates = getAllDateAtYearAndMonth(nextYear, nextMonth, false);
            final int startIndex = 0;
            final int endIndex = (Calendar.DAY_OF_WEEK - dayOfWeekOfLastDay) + Calendar.DAY_OF_WEEK;
            rtn.addAll(nextDates.subList(startIndex, endIndex));
        }
        return rtn;
    }

    public static boolean isTimeOverlapping(long startX, long finishX, long startY, long finishY) {
        return Math.max(startX, startY) <= Math.min(finishX, finishY);
    }


}
