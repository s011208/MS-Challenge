package bj4.yhh.mschallenge.calendar;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import bj4.yhh.mschallenge.Utilities;
import bj4.yhh.mschallenge.provider.Schedule;

/**
 * Created by yenhsunhuang on 2016/6/3.
 */
public class RetrieveCalendarDataHelper extends AsyncTask<Void, Void, ArrayList<CalendarItem>> {
    interface Callback {
        void onPositionOfDayRetrieved(int[] positions);

        void onDataRetrieved(ArrayList<CalendarItem> data);

        void onFinishLoading();
    }

    private final int mYear, mMonth;
    private int mFirstPositionOfDayOfMonth, mLastPositionOfDayOfMonth;
    private final Calendar mCalendar;
    private final WeakReference<Callback> mCallback;
    private final WeakReference<Context> mContext;

    RetrieveCalendarDataHelper(Context context, int y, int m, Callback cb) {
        mContext = new WeakReference<>(context);
        mCalendar = Calendar.getInstance();
        mYear = y;
        mMonth = m;
        mCallback = new WeakReference<>(cb);
    }

    @Override
    protected ArrayList<CalendarItem> doInBackground(Void... params) {
        return retrieveData();
    }

    @Override
    protected void onPostExecute(ArrayList<CalendarItem> objects) {
        Callback cb = mCallback.get();
        if (cb == null) return;
        cb.onDataRetrieved(objects);
        cb.onPositionOfDayRetrieved(new int[]{mFirstPositionOfDayOfMonth, mLastPositionOfDayOfMonth});
        cb.onFinishLoading();
    }

    private ArrayList<CalendarItem> retrieveData() {
        final ArrayList<CalendarItem> rtn = new ArrayList<>();
        final Context context = mContext.get();
        if (context == null) return rtn;
        List<String> weekDayString = Utilities.getShortWeekdayString();
        for (int i = 0; i < weekDayString.size(); ++i) {
            rtn.add(new Weekday(weekDayString.get(i).toUpperCase()));
        }

        List<Date> displayDates = Utilities.getAllDateAtYearAndMonth(mYear, mMonth);

        Calendar calendar = Calendar.getInstance();
        int firstDayOfMonth = mCalendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        int lastDayOfMonth = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        long scheduleStartRange = displayDates.get(0).getTime();
        long scheduleFinishRange = displayDates.get(displayDates.size() - 1).getTime() + Utilities.DAY - Utilities.SECOND; // 23:59:59
        ArrayList<Schedule> schedules = Utilities.getSchedulesBetweenDate(scheduleStartRange, scheduleFinishRange, context);
        for (Date date : displayDates) {
            calendar.setTime(date);
            final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            final int month = calendar.get(Calendar.MONTH);
            boolean clickable = true;
            if (month == mMonth) {
                if (dayOfMonth == firstDayOfMonth) {
                    mFirstPositionOfDayOfMonth = rtn.size();
                } else if (dayOfMonth == lastDayOfMonth) {
                    mLastPositionOfDayOfMonth = rtn.size();
                }
            } else {
                clickable = false;
            }
            boolean hasSchedule = false;
            for (Schedule schedule : schedules) {
                if (Utilities.isTimeOverlapping(schedule.getStartTime(), schedule.getFinishTime()
                        , date.getTime(), date.getTime() + Utilities.DAY - Utilities.SECOND)) {
                    hasSchedule = true;
                    break;
                }
            }
            rtn.add(new CalendarDate(date, hasSchedule, clickable));
        }
        return rtn;
    }
}
