package bj4.yhh.mschallenge.agenda;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import bj4.yhh.mschallenge.Utilities;
import bj4.yhh.mschallenge.provider.Schedule;

/**
 * Created by yenhsunhuang on 2016/6/6.
 */
public class RetrieveAgendaDataHelper extends AsyncTask<Void, Void, ArrayList<AgendaItem>> {
    private static final String TAG = "RetrieveAgendaHelper";
    private static final boolean DEBUG = false;

    public interface Callback {
        void onDataRetrieved(ArrayList<AgendaItem> data);
    }

    private final WeakReference<Context> mContext;
    private final long mStartDateTime, mFinishDateTime;
    private final WeakReference<Callback> mCallback;

    public RetrieveAgendaDataHelper(Context context, long startDate, long finishDate, Callback cb) {
        mContext = new WeakReference<>(context);
        mStartDateTime = startDate;
        mFinishDateTime = finishDate;
        mCallback = new WeakReference<>(cb);
    }

    @Override
    protected ArrayList<AgendaItem> doInBackground(Void... params) {
        final ArrayList<AgendaItem> rtn = new ArrayList<>();
        final Context context = mContext.get();
        if (context == null) return rtn;
        long scheduleStartRange = mStartDateTime;
        long scheduleFinishRange = mFinishDateTime + Utilities.DAY - Utilities.SECOND; // 23:59:59
        ArrayList<Schedule> schedules = Utilities.getSchedulesBetweenDate(scheduleStartRange, scheduleFinishRange, context);

        final int totalDay = (int) ((mFinishDateTime - mStartDateTime) / Utilities.DAY);

        for (int i = 0; i < totalDay; ++i) {
            final long dateTime = mStartDateTime + Utilities.DAY * i;
            final long finishDateTime = dateTime + Utilities.DAY - Utilities.SECOND;
            rtn.add(new Section(dateTime));
            boolean addEvent = false;
            for (Schedule schedule : schedules) {
                if (Utilities.isTimeOverlapping(schedule.getStartTime(), schedule.getFinishTime()
                        , dateTime, finishDateTime)) {
                    rtn.add(new Event(schedule, dateTime));
                    addEvent = true;
                }
            }
            if (!addEvent) {
                rtn.add(new NoEvent());
            }
        }

        if (DEBUG) {
            Log.d(TAG, "schedules size: " + schedules.size() + ", totalDay: " + totalDay
                    + ", rtn size: " + rtn.size());
        }
        return rtn;
    }

    @Override
    protected void onPostExecute(ArrayList<AgendaItem> agendaItems) {
        final Callback cb = mCallback.get();
        if (cb == null) return;
        cb.onDataRetrieved(agendaItems);
    }
}
