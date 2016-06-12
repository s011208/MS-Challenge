package bj4.yhh.mschallenge.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import bj4.yhh.mschallenge.services.NotificationService;

/**
 * Try to keep StartServiceReceiver alive
 * Created by User on 2016/6/12.
 */
public class StartServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, NotificationService.class);
        context.startService(startServiceIntent);
    }
}
