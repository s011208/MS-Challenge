package bj4.yhh.mschallenge.agenda;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import java.util.Calendar;

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
        // TODO change date
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        final long startTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_MONTH, 14);
        final long finishTime = calendar.getTimeInMillis();
        update(startTime, finishTime);
    }

    public void update(long startDateTime, long finishDateTime) {
        mAdapter = new AgendaAdapter(mContext, startDateTime, finishDateTime);
        setAdapter(mAdapter);
    }
}
