package bj4.yhh.mschallenge;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Created by yenhsunhuang on 2016/6/10.
 */
public abstract class BaseActivityInstrumentationTest<T extends Activity> extends ActivityInstrumentationTestCase2 {
    private T mActivity;

    public BaseActivityInstrumentationTest(Class activityClass) {
        super(activityClass);
    }

    @Override
    public T getActivity() {
        if (mActivity == null) {
            Intent intent = new Intent(getInstrumentation().getTargetContext(), CalendarActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(CalendarActivity.class.getName(), null, false);
            getInstrumentation().getTargetContext().startActivity(intent);
            mActivity = (T) getInstrumentation().waitForMonitor(monitor);
            setActivity(mActivity);
        }
        return mActivity;
    }
}
