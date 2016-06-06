package bj4.yhh.mschallenge.calendar;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import bj4.yhh.mschallenge.Utilities;
import bj4.yhh.mschallenge.provider.Schedule;
import bj4.yhh.mschallenge.provider.TableScheduleContent;

/**
 * Created by yenhsunhuang on 2016/6/3.
 */
public class RetrieveCalendarDataHelper extends AsyncTask<Void, Void, ArrayList<Object>> {
    interface Callback {
        void onPositionOfDayRetrieved(int[] positions);

        void onDataRetrieved(ArrayList<Object> data);

        void onFinishLoading();
    }

    private final int mYear, mMonth;
    private int mFirstPositionOfDayOfMonth, mLastPositionOfDayOfMonth;
    private final Calendar mCalendar;
    private final WeakReference<Callback> mCallback;
    private final WeakReference<Context> mContext;

    RetrieveCalendarDataHelper(Context context, int y, int m, Callback cb) {
        mContext = new WeakReference<Context>(context);
        mCalendar = Calendar.getInstance();
        mYear = y;
        mMonth = m;
        mCallback = new WeakReference<>(cb);
    }

    @Override
    protected ArrayList<Object> doInBackground(Void... params) {
        return retrieveData();
    }

    @Override
    protected void onPostExecute(ArrayList<Object> objects) {
        Callback cb = mCallback.get();
        if (cb == null) return;
        cb.onDataRetrieved(objects);
        cb.onPositionOfDayRetrieved(new int[]{mFirstPositionOfDayOfMonth, mLastPositionOfDayOfMonth});
        cb.onFinishLoading();
    }

    private ArrayList<Object> retrieveData() {
        final ArrayList<Object> rtn = new ArrayList<>();
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
        Cursor scheduleCursor = context.getContentResolver().query(TableScheduleContent.URI, null,
                "(" + TableScheduleContent.COLUMN_START_TIME + " > " + scheduleStartRange +
                        " and " + TableScheduleContent.COLUMN_START_TIME + " < " + scheduleFinishRange +
                        ") or (" + TableScheduleContent.COLUMN_FINISH_TIME + " > " + scheduleStartRange +
                        " and " + TableScheduleContent.COLUMN_FINISH_TIME + " < " + scheduleFinishRange + ")",
                null, null);
        ArrayList<Schedule> schedules = Schedule.fromCursor(scheduleCursor);
        for (Date date : displayDates) {
            calendar.setTime(date);
            final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            final int month = calendar.get(Calendar.MONTH);
            if (month == mMonth) {
                if (dayOfMonth == firstDayOfMonth) {
                    mFirstPositionOfDayOfMonth = rtn.size();
                } else if (dayOfMonth == lastDayOfMonth) {
                    mLastPositionOfDayOfMonth = rtn.size();
                }
            }
            boolean hasSchedule = false;
            for (Schedule schedule : schedules) {
                if (Utilities.isTimeOverlapping(schedule.getStartTime(), schedule.getFinishTime()
                        , date.getTime(), date.getTime() + Utilities.DAY - Utilities.SECOND)) {
                    hasSchedule = true;
                    break;
                }
            }
            rtn.add(new CalendarDate(date, hasSchedule));
        }
        return rtn;
    }
}
