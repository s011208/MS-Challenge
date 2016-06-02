package bj4.yhh.mschallenge.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;

import bj4.yhh.mschallenge.Utilities;

/**
 * Created by User on 2016/6/3.
 */
public class CalendarDateView extends GridView {
    private static final String TAG = "QQQQ";
    private static final boolean DEBUG = Utilities.DEBUG;

    private final ArrayList<WeakReference> mCallbacks = new ArrayList<WeakReference>();
    private CalendarDateViewAdapter mCalendarDateViewAdapter;

    public CalendarDateView(Context context) {
        this(context, null);
    }

    public CalendarDateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarDateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCalendarDateViewAdapter = new CalendarDateViewAdapter(context);
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

    private static class CalendarDateViewAdapter extends BaseAdapter {
        private final Context mContext;
        private final LayoutInflater mLayoutInflater;
        private Calendar mCalendar = Calendar.getInstance();

        public CalendarDateViewAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }
}
