package bj4.yhh.mschallenge.calendar;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by yenhsunhuang on 2016/6/3.
 */
public class CalendarPagerAdapter extends PagerAdapter {
    public static final int CENTRAL_PAGE = 500;
    private static final int NUMBER_OF_PAGE = 1000;
    private final int mBaseYear, mBaseMonth;
    private final Context mContext;
    private final Map<Integer, CalendarDateView> mViewsMap = new HashMap<>();
    private final Stack<CalendarDateView> mCalendarDateViewCache = new Stack<>();

    public CalendarPagerAdapter(Context context) {
        mContext = context;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        mBaseYear = calendar.get(Calendar.YEAR);
        mBaseMonth = calendar.get(Calendar.MONTH);
    }

    public static Date calculateDateByPosition(final int baseYear, final int baseMonth, final int position) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, baseYear);
        calendar.set(Calendar.MONTH, baseMonth);
        final int diff = position - CalendarPagerAdapter.CENTRAL_PAGE;
        calendar.add(Calendar.MONTH, diff);
        return calendar.getTime();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        CalendarDateView calendarView;
        Date pageDate = calculateDateByPosition(mBaseYear, mBaseMonth, position);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(pageDate);
        if (mCalendarDateViewCache.isEmpty()) {
            calendarView = new CalendarDateView(mContext);
            calendarView.setArguments(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), (CalendarDateView.Callback) mContext);
        } else {
            calendarView = mCalendarDateViewCache.pop();
            calendarView.updateArguments(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), (CalendarDateView.Callback) mContext);
        }
        container.addView(calendarView);
        calendarView.setTag(CalendarPagerAdapter.class.getName() + position);
        mViewsMap.put(position, calendarView);
        return calendarView;
    }

    @Override
    public int getCount() {
        return NUMBER_OF_PAGE;
    }


    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mViewsMap.remove(position);
        container.removeView((View) object);
        mCalendarDateViewCache.push((CalendarDateView) object);
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void setSelectedDate(Date selectedDate, int position) {
        mViewsMap.get(position).updateSelectedDate(selectedDate);
    }
}
