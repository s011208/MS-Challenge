package bj4.yhh.mschallenge.agenda;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.AbsListView;

import java.util.Calendar;
import java.util.Date;

import bj4.yhh.mschallenge.Utilities;
import bj4.yhh.mschallenge.views.PinnedSectionListView;

/**
 * Created by yenhsunhuang on 2016/6/6.
 */
public class AgendaView extends PinnedSectionListView {
    private static final String TAG = "AgendaView";
    private static final boolean DEBUG = Utilities.DEBUG;
    private static final int DEFAULT_LIST_RANGE = 7;
    private AgendaAdapter mAdapter;
    private Context mContext;
    private long mDefaultSelectedTime;
    private AgendaAdapter.Callback mOnDataLoadedListener = new AgendaAdapter.Callback() {
        @Override
        public void onDataLoaded(boolean reload) {
            if (reload) return;
            updateSelection();
        }
    };
    private Callback mCallback;
    private int mScrollState = OnScrollListener.SCROLL_STATE_IDLE;
    private long mStartTime, mFinishTime;

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
        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                mScrollState = scrollState;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int sectionPosition = findCurrentSectionPosition(firstVisibleItem);
                if (sectionPosition > -1) {
                    AgendaItem item = mAdapter.getItem(sectionPosition);
                    if (DEBUG) {
                        Log.d(TAG, "sectionPosition: " + sectionPosition
                                + ", date: " + Utilities.debugDateTime(((Section) item).getDateTime())
                                + ", mScrollState: " + mScrollState);
                    }
                    if (mCallback != null && mScrollState != OnScrollListener.SCROLL_STATE_IDLE) {
                        mCallback.onSectionItemChanged(((Section) item).getDateTime());
                    }
                }
            }
        });
    }

    public void setCallback(Callback cb) {
        mCallback = cb;
    }

    private void setDate(long startDateTime, long finishDateTime, long selectedDateTime) {
        mDefaultSelectedTime = selectedDateTime;
        if (mAdapter == null) {
            mAdapter = new AgendaAdapter(mContext, startDateTime, finishDateTime, selectedDateTime, mOnDataLoadedListener);
            setAdapter(mAdapter);
        } else {
            if (!mAdapter.setDateTimeRange(startDateTime, finishDateTime, selectedDateTime)) {
                updateSelection();
            }
        }
        mStartTime = startDateTime;
        mFinishTime = finishDateTime;
    }

    private int findIndexOfSelectedDateTime() {
        for (int i = 0; i < mAdapter.getCount(); ++i) {
            final AgendaItem item = mAdapter.getItem(i);
            if (item instanceof Section) {
                if (((Section) item).getDateTime() == mDefaultSelectedTime) {
                    if (DEBUG) {
                        Log.v(TAG, "index: " + i + ", time: " + Utilities.debugDateTime(((Section) item).getDateTime()));
                    }
                    return i;
                }
            }
        }
        return -1;
    }

    public Section findSectionOfItem(int position) {
        for (int i = position; i >= 0; --i) {
            final AgendaItem item = mAdapter.getItem(i);
            if (item instanceof Section) {
                return (Section) item;
            }
        }
        return null;
    }

    public void setDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Utilities.clearCalendarOffset(calendar);
        setDate(calendar);
    }

    public void setDate(Calendar calendar) {
        long selectedTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_MONTH, -DEFAULT_LIST_RANGE);
        final long startTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_MONTH, DEFAULT_LIST_RANGE * 2 + 1);
        final long finishTime = calendar.getTimeInMillis();
        setDate(startTime, finishTime, selectedTime);
    }

    private void updateSelection() {
        int index = findIndexOfSelectedDateTime();
        if (DEBUG) {
            Log.d(TAG, "index: " + index);
        }
        if (index != -1) {
            setSelection(index);
        }
    }

    public long getStartTime() {
        return mStartTime;
    }

    public long getFinishTime() {
        return mFinishTime;
    }

    public interface Callback {
        void onSectionItemChanged(long newItemDateTime);
    }
}
