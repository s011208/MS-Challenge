package bj4.yhh.mschallenge.calendar;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import bj4.yhh.mschallenge.Utilities;

/**
 * Created by yenhsunhuang on 2016/6/3.
 */
public class CalendarPager extends ViewPager {
    private static final String TAG = "CalendarPager";
    private static final boolean DEBUG = Utilities.DEBUG;

    private final PagerAdapter mPagerAdapter;
    public CalendarPager(Context context) {
        this(context, null);
    }

    public CalendarPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOffscreenPageLimit(3);
        mPagerAdapter = new CalendarPagerAdapter(context);
        setAdapter(mPagerAdapter);
        setCurrentItem(CalendarPagerAdapter.CENTRAL_PAGE);
    }

    public int[] getCurrentMonthAndYear() {
        View view = findViewWithTag(CalendarPagerAdapter.class.getName() + getCurrentItem());
        if (view != null && view instanceof CalendarDateView) {
            return new int[] {((CalendarDateView)view).getYear(), ((CalendarDateView)view).getMonth()};
        }
        return null;
    }
}
