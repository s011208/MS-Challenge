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

    private static class CalendarDateViewAdapter extends BaseAdapter {
        private static final int VIEW_TYPE_WEEKDAY = 1;
        private static final int VIEW_TYPE_DATE = 2;

        private final Context mContext;
        private final LayoutInflater mLayoutInflater;
        private ArrayList<Object> mData = new ArrayList<>();
        private final int mYear, mMonth;
        private Calendar mCalendar = Calendar.getInstance();
        private int mFirstPositionOfDayOfMonth, mLastPositionOfDayOfMonth;
        private int mPressedPosition = -1;

        public CalendarDateViewAdapter(Context context, int y, int m) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mYear = y;
            mMonth = m;
            mCalendar.set(Calendar.YEAR, mYear);
            mCalendar.set(Calendar.MONTH, mMonth);
            initData();
        }

        private void setPressedPosition(int position) {
            mPressedPosition = position;
        }

        private void initData() {
            mData.clear();
            List<String> weekDayString = Utilities.getShortWeekdayString();
            for (int i = 0; i < weekDayString.size(); ++i) {
                if (DEBUG) {
                    Log.d(TAG, "weekDay: " + weekDayString.get(i));
                }
                mData.add(new Weekday(weekDayString.get(i).toUpperCase()));
            }

            List<Date> displayDates = Utilities.getAllDateAtYearAndMonth(mYear, mMonth);

            Calendar calendar = Calendar.getInstance();
            int firstDayOfMonth = 1;
            int lastDayOfMonth = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            for (Date date : displayDates) {
                calendar.setTime(date);
                final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                final int month = calendar.get(Calendar.MONTH);
                if (DEBUG) {
                    Log.d(TAG, "dayOfMonth: " + dayOfMonth + ", lastDayOfMonth: " + lastDayOfMonth);
                }
                if (month == mMonth) {
                    if (dayOfMonth == firstDayOfMonth) {
                        mFirstPositionOfDayOfMonth = mData.size();
                    } else if (dayOfMonth == lastDayOfMonth) {
                        mLastPositionOfDayOfMonth = mData.size();
                    }
                }
                mData.add(new CalendarDate(date, false));
            }
            if (DEBUG)
                Log.e(TAG, "mFirstPositionOfDayOfMonth: " + mFirstPositionOfDayOfMonth
                        + ", mLastPositionOfDayOfMonth: " + mLastPositionOfDayOfMonth);
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mLayoutInflater.inflate(R.layout.calendar_day_item, null);
                holder.mDayText = (TextView) convertView.findViewById(R.id.date);
                holder.mDot = (ImageView) convertView.findViewById(R.id.date_dot);
                holder.mDayTextState = (ImageView) convertView.findViewById(R.id.date_state);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            switch (getItemViewType(position)) {
                case VIEW_TYPE_WEEKDAY:
                    final Weekday weekday = (Weekday) getItem(position);
                    holder.mDayText.setText(weekday.getDayText());
                    if (position == Calendar.SUNDAY - 1 || position == Calendar.SATURDAY - 1) {
                        holder.mDayText.setTextColor(Color.GRAY);
                    } else {
                        holder.mDayText.setTextColor(Color.BLACK);
                    }
                    break;
                case VIEW_TYPE_DATE:
                    final CalendarDate calendarDate = (CalendarDate) getItem(position);
                    mCalendar.setTime(calendarDate.getDate());
                    holder.mDayText.setText(String.valueOf(mCalendar.get(Calendar.DAY_OF_MONTH)));
                    if (position < mFirstPositionOfDayOfMonth || position > mLastPositionOfDayOfMonth) {
                        holder.mDayText.setTextColor(Color.GRAY);
                    } else {
                        holder.mDayText.setTextColor(Color.BLACK);
                    }
                    holder.mDayTextState.setVisibility(mPressedPosition == position ? VISIBLE : INVISIBLE);
                    holder.mDot.setVisibility(calendarDate.hasSchedule() ? VISIBLE : INVISIBLE);
                    break;
                default:
            }
            return convertView;
        }

        private static class ViewHolder {
            private TextView mDayText;
            private ImageView mDot;
            private ImageView mDayTextState;
        }

        @Override
        public int getItemViewType(int position) {
            final Object item = getItem(position);
            if (item instanceof Weekday) return VIEW_TYPE_WEEKDAY;
            else if (item instanceof CalendarDate) return VIEW_TYPE_DATE;
            else return super.getItemViewType(position);
        }
    }
}
