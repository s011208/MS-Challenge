package bj4.yhh.mschallenge;

import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Created by yenhsunhuang on 2016/6/10.
 */
public class TestCalendarActivityRotation extends ActivityInstrumentationTestCase2<CalendarActivity> {
    private CalendarActivity mActivity;

    public TestCalendarActivityRotation() {
        super(CalendarActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = getActivity();
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
        SystemClock.sleep(2000);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getInstrumentation().waitForIdleSync();
        SystemClock.sleep(2000);
    }

    private void setOrientationToPortrait() {
        getInstrumentation().waitForIdleSync();
        SystemClock.sleep(2000);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getInstrumentation().waitForIdleSync();
        SystemClock.sleep(2000);
    }

    @Override
    public CalendarActivity getActivity() {
        if (mActivity == null) {
            Intent intent = new Intent(getInstrumentation().getTargetContext(), CalendarActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(CalendarActivity.class.getName(), null, false);
            getInstrumentation().getTargetContext().startActivity(intent);
            mActivity = (CalendarActivity) getInstrumentation().waitForMonitor(monitor);
            setActivity(mActivity);
        }
        return mActivity;
    }
}
