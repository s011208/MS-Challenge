package bj4.yhh.mschallenge;

import android.app.Application;
import android.content.Intent;

import java.lang.ref.WeakReference;

/**
 * Created by yenhsunhuang on 2016/6/9.
 */
public class MsChallengeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        startWeatherService();
    }

    private void startWeatherService() {
        Intent intent = new Intent(MsChallengeApplication.this, WeakReference.class);
        startService(intent);
    }
}
