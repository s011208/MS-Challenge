package bj4.yhh.mschallenge.calendar;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import bj4.yhh.mschallenge.R;
import bj4.yhh.mschallenge.Utilities;

/**
 * Created by yenhsunhuang on 2016/6/3.
 */
public class CalendarDateViewAdapter extends BaseAdapter {
    private static final String TAG = "CalendarDateViewAdapter";
    private static final boolean DEBUG = Utilities.DEBUG;

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

    public void setPressedPosition(int position) {
        mPressedPosition = position;
    }

    private void initData() {
        new RetrieveCalendarDataHelper(mYear, mMonth, new RetrieveCalendarDataHelper.Callback() {
            @Override
            public void onPositionOfDayRetrieved(int[] positions) {
                if (positions == null) return;
                mFirstPositionOfDayOfMonth = positions[0];
                mLastPositionOfDayOfMonth = positions[1];
            }

            @Override
            public void onDataRetrieved(ArrayList<Object> data) {
                if (data == null) return;
                mData.clear();
                mData.addAll(data);
            }

            @Override
            public void onFinishLoading() {
                notifyDataSetChanged();
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
                Log.d(TAG, "m: " + mMonth + ", y: " + mYear + ", mFirstPositionOfDayOfMonth: " + mFirstPositionOfDayOfMonth
                        + ", mLastPositionOfDayOfMonth: " + mLastPositionOfDayOfMonth);
                holder.mDayTextState.setVisibility(mPressedPosition == position ? View.VISIBLE : View.INVISIBLE);
                holder.mDot.setVisibility(calendarDate.hasSchedule() ? View.VISIBLE : View.INVISIBLE);
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

    public int getYear() {
        return mYear;
    }

    public int getMonth() {
        return mMonth;
    }
}