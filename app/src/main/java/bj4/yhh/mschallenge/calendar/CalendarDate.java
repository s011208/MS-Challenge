package bj4.yhh.mschallenge.calendar;

import java.util.Date;

/**
 * Created by yenhsunhuang on 2016/6/3.
 */
public class CalendarDate extends CalendarItem {
    private Date mDate;
    private boolean mHasSchedule;
    private boolean mClickable = true;

    public CalendarDate(Date date) {
        this(date, false);
    }

    public CalendarDate(Date date, boolean hasSchedule) {
        this(date, hasSchedule, false);
    }

    public CalendarDate(Date date, boolean hasSchedule, boolean clickable) {
        if (date == null) {
            throw new RuntimeException("Failed to set date");
        }
        mDate = date;
        mHasSchedule = hasSchedule;
        mClickable = clickable;
    }

    public void setDate(Date date) {
        mDate.setTime(date.getTime());
    }

    public Date getDate() {
        return mDate;
    }

    public void setSchedule(boolean schedule) {
        mHasSchedule = schedule;
    }

    public boolean hasSchedule() {
        return mHasSchedule;
    }

    public void setClickable(boolean clickable) {
        mClickable = clickable;
    }

    @Override
    public boolean isClickable() {
        return mClickable;
    }
}
