package bj4.yhh.mschallenge;

import android.app.Application;
import android.content.Intent;

import bj4.yhh.mschallenge.services.NotificationService;

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
        Intent startServiceIntent = new Intent(MsChallengeApplication.this, NotificationService.class);
        startService(startServiceIntent);
    }
}
