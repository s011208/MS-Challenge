package bj4.yhh.mschallenge;

import android.test.AndroidTestCase;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import bj4.yhh.mschallenge.calendar.CalendarPagerAdapter;

/**
 * Created by yenhsunhuang on 2016/6/3.
 */
public class MethodTest extends AndroidTestCase {

    public void testGetAllDateAtYearAndMonthWithSpecificDate() {
        List rtn = Utilities.getAllDateAtYearAndMonth(2016, Calendar.JUNE);
        assertEquals(42, rtn.size());
        rtn = Utilities.getAllDateAtYearAndMonth(2016, Calendar.JANUARY);
        assertEquals(49, rtn.size());
        rtn = Utilities.getAllDateAtYearAndMonth(2026, Calendar.FEBRUARY);
        assertEquals(35, rtn.size());
    }

    public void testGetAllDateAtYearAndMonthRandomly() {
        for (int i = 0; i < 10; ++i) {
            int year = (int) (Math.random() * 10000);
            int month = (int) ((Math.random() * 10000) % 12);
            List rtn = Utilities.getAllDateAtYearAndMonth(year, month);
            assertEquals(0, rtn.size() % Calendar.DAY_OF_WEEK);
        }
    }

    public void testCalculateDateByPosition() {
        Date rtn = CalendarPagerAdapter.calculateDateByPosition(2016, Calendar.JUNE, CalendarPagerAdapter.CENTRAL_PAGE);
        Calendar result = Calendar.getInstance();
        result.setTime(rtn);
        assertEquals(2016, result.get(Calendar.YEAR));
        assertEquals(Calendar.JUNE, result.get(Calendar.MONTH));

        rtn = CalendarPagerAdapter.calculateDateByPosition(2016, Calendar.JUNE, CalendarPagerAdapter.CENTRAL_PAGE - 1);
        result.setTime(rtn);
        assertEquals(2016, result.get(Calendar.YEAR));
        assertEquals(Calendar.MAY, result.get(Calendar.MONTH));

        rtn = CalendarPagerAdapter.calculateDateByPosition(2016, Calendar.JUNE, CalendarPagerAdapter.CENTRAL_PAGE - 12);
        result.setTime(rtn);
        assertEquals(2015, result.get(Calendar.YEAR));
        assertEquals(Calendar.JUNE, result.get(Calendar.MONTH));

        rtn = CalendarPagerAdapter.calculateDateByPosition(2016, Calendar.JUNE, CalendarPagerAdapter.CENTRAL_PAGE - 23);
        result.setTime(rtn);
        assertEquals(2014, result.get(Calendar.YEAR));
        assertEquals(Calendar.JULY, result.get(Calendar.MONTH));

        rtn = CalendarPagerAdapter.calculateDateByPosition(2016, Calendar.JUNE, CalendarPagerAdapter.CENTRAL_PAGE + 1);
        result.setTime(rtn);
        assertEquals(2016, result.get(Calendar.YEAR));
        assertEquals(Calendar.JULY, result.get(Calendar.MONTH));

        rtn = CalendarPagerAdapter.calculateDateByPosition(2016, Calendar.JUNE, CalendarPagerAdapter.CENTRAL_PAGE + 12);
        result.setTime(rtn);
        assertEquals(2017, result.get(Calendar.YEAR));
        assertEquals(Calendar.JUNE, result.get(Calendar.MONTH));

        rtn = CalendarPagerAdapter.calculateDateByPosition(2016, Calendar.JUNE, CalendarPagerAdapter.CENTRAL_PAGE + 23);
        result.setTime(rtn);
        assertEquals(2018, result.get(Calendar.YEAR));
        assertEquals(Calendar.MAY, result.get(Calendar.MONTH));
    }
}
