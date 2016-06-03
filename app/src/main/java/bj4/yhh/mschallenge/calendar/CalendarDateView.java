package bj4.yhh.mschallenge.calendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import bj4.yhh.mschallenge.R;
import bj4.yhh.mschallenge.Utilities;
import bj4.yhh.mschallenge.views.FullyExpandedGridView;

/**
 * Created by User on 2016/6/3.
 */
public class CalendarDateView extends FullyExpandedGridView {
    private static final String TAG = "QQQQ";
    private static final boolean DEBUG = Utilities.DEBUG;

    private final ArrayList<WeakReference> mCallbacks = new ArrayList<WeakReference>();
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
        setSelector(new ColorDrawable(Color.TRANSPARENT));
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (DEBUG) {
                    Log.d(TAG, "onItemClick, position: " + position);
                }
                mCalendarDateViewAdapter.setPressedPosition(position);
                mCalendarDateViewAdapter.notifyDataSetInvalidated();
            }
        });
        setYearAndMonth(2016, Calendar.JUNE);
    }

    public void setYearAndMonth(int y, int m) {
        mCalendarDateViewAdapter = new CalendarDateViewAdapter(mContext, y, m);
        setAdapter(mCalendarDateViewAdapter);
    }

    public void setCallback(Callback cb) {
        mCallbacks.add(new WeakReference(cb));
    }

    public void removeCallback(Callback cb) {
        for (int i = 0; i < mCallbacks.size(); ++i) {
            Callback callback = (Callback) mCallbacks.get(i).get();
            if (callback == null || callback == cb) {
                mCallbacks.remove(i--);
            }
        }
    }

    public interface Callback {
        void onDaySelected();
    }
}
