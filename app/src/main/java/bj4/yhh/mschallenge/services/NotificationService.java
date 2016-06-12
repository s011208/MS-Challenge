package bj4.yhh.mschallenge.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import bj4.yhh.mschallenge.R;
import bj4.yhh.mschallenge.Utilities;
import bj4.yhh.mschallenge.provider.TableScheduleContent;

/**
 * Created by User on 2016/6/12.
 */
public class NotificationService extends Service {
    private static final String TAG = "NotificationService";
    private static final boolean DEBUG = Utilities.DEBUG;
    private final Timer mTimer = new Timer("NotificationService timer", false);
    private NotificationManager mNotificationManager;
    private final TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            if (DEBUG)
                Log.d(TAG, "mTimerTask run");
            final long currentTime = System.currentTimeMillis();
            Cursor data = getContentResolver().query(TableScheduleContent.URI, null,
                    TableScheduleContent.COLUMN_START_TIME + ">" + currentTime +
                            " and " + TableScheduleContent.COLUMN_NOTIFY + "!=" + TableScheduleContent.SCHEDULE_NOTIFY_NONE, null, null);
            if (DEBUG)
                Log.i(TAG, "data count: " + data.getCount());
            if (data != null) {
                try {
                    final int indexOfId = data.getColumnIndex(TableScheduleContent.COLUMN_ID);
                    final int indexOfTitle = data.getColumnIndex((TableScheduleContent.COLUMN_TITLE));
                    final int indexOfDescription = data.getColumnIndex(TableScheduleContent.COLUMN_DESCRIPTION);
                    final int indexOfStartTime = data.getColumnIndex(TableScheduleContent.COLUMN_START_TIME);
                    final int indexOfNotify = data.getColumnIndex(TableScheduleContent.COLUMN_NOTIFY);
                    while (data.moveToNext()) {
                        long id = data.getLong(indexOfId);
                        String title = data.getString(indexOfTitle);
                        String description = data.getString(indexOfDescription);
                        long startTime = data.getLong(indexOfStartTime);
                        int notify = data.getInt(indexOfNotify);
                        final long timeDiff = startTime - currentTime;
                        boolean sendNotify = false;
                        long notificationDiffTime = 0;
                        if (notify == TableScheduleContent.SCHEDULE_NOTIFY_BEFORE_5_MINUTES) {
                            if ((timeDiff >= 5 * Utilities.MINUTE && timeDiff <= 6 * Utilities.MINUTE)) {
                                sendNotify = true;
                                notificationDiffTime = 5 * Utilities.MINUTE;
                            }
                        } else if (notify == TableScheduleContent.SCHEDULE_NOTIFY_BEFORE_10_MINUTES) {
                            if ((timeDiff >= 10 * Utilities.MINUTE && timeDiff <= 11 * Utilities.MINUTE)) {
                                sendNotify = true;
                                notificationDiffTime = 10 * Utilities.MINUTE;
                            }
                        } else if (notify == TableScheduleContent.SCHEDULE_NOTIFY_BEFORE_15_MINUTES) {
                            if ((timeDiff >= 15 * Utilities.MINUTE && timeDiff <= 16 * Utilities.MINUTE)) {
                                sendNotify = true;
                                notificationDiffTime = 15 * Utilities.MINUTE;
                            }
                        } else if (notify == TableScheduleContent.SCHEDULE_NOTIFY_BEFORE_30_MINUTES) {
                            if ((timeDiff >= 30 * Utilities.MINUTE && timeDiff <= 31 * Utilities.MINUTE)) {
                                sendNotify = true;
                                notificationDiffTime = 30 * Utilities.MINUTE;
                            }
                        } else if (notify == TableScheduleContent.SCHEDULE_NOTIFY_BEFORE_1_HOUR) {
                            if ((timeDiff >= 60 * Utilities.MINUTE && timeDiff <= 61 * Utilities.MINUTE)) {
                                sendNotify = true;
                                notificationDiffTime = 60 * Utilities.MINUTE;
                            }
                        } else if (notify == TableScheduleContent.SCHEDULE_NOTIFY_RIGHT_IN_TIME) {
                            if ((timeDiff >= 0 * Utilities.MINUTE && timeDiff <= 1 * Utilities.MINUTE)) {
                                sendNotify = true;
                                notificationDiffTime = 0;
                            }
                        }
                        String notifyText = getResources().getStringArray(R.array.schedule_notify_start_time)[notify];
                        if (DEBUG)
                            Log.i(TAG, "sendNotify: " + sendNotify + ", title: " + title + ", des: " + description + ", notifyText: " + notifyText);

                        if (sendNotify) {
                            sendNotification((int) (id % Integer.MAX_VALUE), title, description, notifyText, startTime - notificationDiffTime);
                        }
                    }
                } finally {
                    data.close();
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        if (DEBUG) Log.d(TAG, "onCreate");
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        mTimer.schedule(mTimerTask, calendar.getTime(), Utilities.MINUTE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }

    private void sendNotification(int id, String title, String content, String subText, long when) {
        Notification.Builder builder = new Notification.Builder(NotificationService.this);
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.drawable.ic_calendar_clock_white_36dp);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setSubText(subText);
        builder.setWhen(when);
        builder.setDefaults(Notification.DEFAULT_SOUND);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setColor(getResources().getColor(R.color.colorPrimary));
        }

        Notification notification = builder.build();
        mNotificationManager.notify(id, notification);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
