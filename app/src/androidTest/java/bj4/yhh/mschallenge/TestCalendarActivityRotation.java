package bj4.yhh.mschallenge;

import android.content.pm.ActivityInfo;
import android.os.SystemClock;

/**
 * Created by yenhsunhuang on 2016/6/10.
 */
public class TestCalendarActivityRotation extends BaseActivityInstrumentationTest<CalendarActivity> {

    public TestCalendarActivityRotation() {
        super(CalendarActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testScreenRotation() {
        setOrientationToLandScape();
        setOrientationToPortrait();
    }

    private void setOrientationToLandScape() {
        getInstrumentation().waitForIdleSync();
        SystemClock.sleep(3000);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getInstrumentation().waitForIdleSync();
        SystemClock.sleep(3000);
    }

    private void setOrientationToPortrait() {
        getInstrumentation().waitForIdleSync();
        SystemClock.sleep(3000);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getInstrumentation().waitForIdleSync();
        SystemClock.sleep(3000);
    }
}
