package bj4.yhh.mschallenge.calendar;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by yenhsunhuang on 2016/6/3.
 */
public class CalendarPagerAdapter extends PagerAdapter {
    private static final int NUMBER_OF_PAGE = 1000;
    public static final int CENTRAL_PAGE = 500;
    private final int mBaseYear, mBaseMonth;
    private final Context mContext;
    private final Calendar mCalendar = Calendar.getInstance();

    public CalendarPagerAdapter(Context context) {
        mContext = context;
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        mBaseYear = mCalendar.get(Calendar.YEAR);
        mBaseMonth = mCalendar.get(Calendar.MONTH);
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
        CalendarDateView calendarView = new CalendarDateView(mContext);
        Date pageDate = calculateDateByPosition(mBaseYear, mBaseMonth, position);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(pageDate);
        calendarView.setYearAndMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
        container.addView(calendarView);
        calendarView.setTag(CalendarPagerAdapter.class.getName() + position);
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
        container.removeView((View) object);
    }
}
