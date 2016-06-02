package bj4.yhh.mschallenge;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by User on 2016/6/2.
 */
public class Utilities {
    private static final List<String> sMonthStringList = Arrays.asList(new DateFormatSymbols().getMonths());

    private Utilities() {
    }

    public static List<String> getMonthString() {
        return new ArrayList(sMonthStringList);
    }
}
