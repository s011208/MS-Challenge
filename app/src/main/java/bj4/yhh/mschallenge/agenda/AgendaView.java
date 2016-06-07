package bj4.yhh.mschallenge.agenda;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import java.util.Calendar;
import java.util.Date;

import bj4.yhh.mschallenge.Utilities;

/**
 * Created by yenhsunhuang on 2016/6/6.
 */
public class AgendaView extends ListView {
    private AgendaAdapter mAdapter;
    private Context mContext;

    public AgendaView(Context context) {
        this(context, null);
    }

    public AgendaView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AgendaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        Utilities.clearCalendarOffset(calendar);
        setDate(calendar);
    }

    private void setDate(long startDateTime, long finishDateTime) {
        if (mAdapter == null) {
            mAdapter = new AgendaAdapter(mContext, startDateTime, finishDateTime);
            setAdapter(mAdapter);
        } else {
            mAdapter.setDateTimeRange(startDateTime, finishDateTime);
        }
    }

    public void setDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Utilities.clearCalendarOffset(calendar);
        setDate(calendar);
    }

    public void setDate(Calendar calendar) {
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        final long startTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_MONTH, 14);
        final long finishTime = calendar.getTimeInMillis();
        setDate(startTime, finishTime);
    }
}
