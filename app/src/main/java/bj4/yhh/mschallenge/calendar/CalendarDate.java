package bj4.yhh.mschallenge.calendar;

import java.util.Date;

/**
 * Created by yenhsunhuang on 2016/6/3.
 */
public class CalendarDate {
    private Date mDate;
    private boolean mHasSchedule;

    public CalendarDate(Date date) {
        this(date, false);
    }

    public CalendarDate(Date date, boolean hasSchedule) {
        if (date == null) {
            throw new RuntimeException("Failed to set date");
        }
        mDate = date;
        mHasSchedule = hasSchedule;
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
}
