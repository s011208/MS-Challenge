package bj4.yhh.mschallenge.calendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.lang.ref.WeakReference;
import java.util.Date;

import bj4.yhh.mschallenge.R;
import bj4.yhh.mschallenge.Utilities;
import bj4.yhh.mschallenge.views.FullyExpandedGridView;

/**
 * Created by User on 2016/6/3.
 */
public class CalendarDateView extends FullyExpandedGridView {
    private static final String TAG = "CalendarDateView";
    private static final boolean DEBUG = Utilities.DEBUG;

    private WeakReference<Callback> mCallback;
    private CalendarDateViewAdapter mCalendarDateViewAdapter;
    private Context mContext;

    public CalendarDateView(Context context) {
        this(context, null);
    }

    public CalendarDateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarDateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setNumColumns(7);
        setDrawSelectorOnTop(true);
        setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        setVerticalSpacing(mContext.getResources().getDimensionPixelSize(R.dimen.calendar_view_vertical_spacing));
        setSelector(new ColorDrawable(Color.TRANSPARENT));
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (DEBUG) {
                    Log.d(TAG, "onItemClick, position: " + position);
                }
                if (!mCalendarDateViewAdapter.getItem(position).isClickable()) return;
                mCalendarDateViewAdapter.setPressedPosition(position);
                mCalendarDateViewAdapter.notifyDataSetInvalidated();
                if (mCallback.get() != null) {
                    mCallback.get().onDaySelected(
                            ((CalendarDate) mCalendarDateViewAdapter.getItem(position)).getDate());
                }
            }
        });
    }

    public void setArguments(int year, int month, Callback cb) {
        mCallback = new WeakReference<>(cb);
        if (mCalendarDateViewAdapter != null) return;
        mCalendarDateViewAdapter = new CalendarDateViewAdapter(mContext, year, month, cb.getSelectedDate());
        setAdapter(mCalendarDateViewAdapter);
    }

    public void updateArguments(int year, int month, Callback cb) {
        if (mCallback == null || mCallback.get() != cb) {
            mCallback = new WeakReference<>(cb);
        }
        mCalendarDateViewAdapter.update(year, month, mCallback.get().getSelectedDate());
    }

    public int getYear() {
        return mCalendarDateViewAdapter.getYear();
    }

    public int getMonth() {
        return mCalendarDateViewAdapter.getMonth();
    }

    public void updateSelectedDate(Date selectedDate) {
        int pressedPosition;
        for (int i = 0; i < mCalendarDateViewAdapter.getCount(); ++i) {
            CalendarItem item = mCalendarDateViewAdapter.getItem(i);
            if (item instanceof CalendarDate) {
                View dayTestState = ((CalendarDateViewAdapter.ViewHolder) getChildAt(i).getTag()).mDayTextState;
                View dot = ((CalendarDateViewAdapter.ViewHolder) getChildAt(i).getTag()).mDot;
                if (((CalendarDate) item).getDate().equals(selectedDate)) {
                    pressedPosition = i;
                    dayTestState.setVisibility(View.VISIBLE);
                    dot.setVisibility(View.INVISIBLE);
                    mCalendarDateViewAdapter.setPressedPosition(pressedPosition);
                } else {
                    if (dayTestState.getVisibility() == View.VISIBLE) {
                        dayTestState.setVisibility(View.INVISIBLE);
                        dot.setVisibility(((CalendarDate) item).hasSchedule() ? View.VISIBLE : View.INVISIBLE);
                    }
                }
            }
        }
    }

    public interface Callback {
        void onDaySelected(Date date);

        Date getSelectedDate();
    }
}
