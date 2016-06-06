package bj4.yhh.mschallenge.calendar;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
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
            return new int[]{((CalendarDateView) view).getYear(), ((CalendarDateView) view).getMonth()};
        }
        return null;
    }

    public void requestUpdate() {
        if (DEBUG) {
            Log.d(TAG, "requestUpdate");
        }
        mPagerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        View view = getChildAt(0);
        if (view != null) {
            view.measure(widthMeasureSpec, heightMeasureSpec);
        }

        setMeasuredDimension(getMeasuredWidth(), measureHeight(heightMeasureSpec, view));
    }

    private int measureHeight(int measureSpec, View view) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            if (view != null) {
                result = view.getMeasuredHeight();
            }
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }
}
