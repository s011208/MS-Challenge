package bj4.yhh.mschallenge.calendar;

/**
 * Created by yenhsunhuang on 2016/6/3.
 */
public class Weekday extends CalendarItem {
    private String mDayText;

    public Weekday(String day) {
        mDayText = day;
    }

    public String getDayText() {
        return mDayText;
    }

    @Override
    public boolean isClickable() {
        return false;
    }
}
