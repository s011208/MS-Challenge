package bj4.yhh.mschallenge;

import android.test.AndroidTestCase;

import java.util.Calendar;
import java.util.List;

/**
 * Created by yenhsunhuang on 2016/6/3.
 */
public class MethodTest extends AndroidTestCase {

    public void testGetAllDateAtYearAndMonthWithSpecificDate() {
        List rtn = Utilities.getAllDateAtYearAndMonth(2016, Calendar.JUNE);
        assertEquals(rtn.size(), 42);
        rtn = Utilities.getAllDateAtYearAndMonth(2016, Calendar.JANUARY);
        assertEquals(rtn.size(), 49);
        rtn = Utilities.getAllDateAtYearAndMonth(2026, Calendar.FEBRUARY);
        assertEquals(rtn.size(), 35);
    }

    public void testGetAllDateAtYearAndMonthRandomly() {
        for (int i = 0; i < 10; ++i) {
            int year = (int) (Math.random() * 10000);
            int month = (int) ((Math.random() * 10000) % 12);
            List rtn = Utilities.getAllDateAtYearAndMonth(year, month);
            assertEquals(rtn.size() % Calendar.DAY_OF_WEEK, 0);
        }
    }
}
