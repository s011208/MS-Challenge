package bj4.yhh.mschallenge.provider;

import android.net.Uri;

/**
 * Created by yenhsunhuang on 2016/6/5.
 */
public class TableScheduleContent {
    public static final int SCHEDULE_NOTIFY_NONE = 0;
    public static final int SCHEDULE_NOTIFY_RIGHT_IN_TIME = 1;
    public static final int SCHEDULE_NOTIFY_BEFORE_5_MINUTES = 2;
    public static final int SCHEDULE_NOTIFY_BEFORE_10_MINUTES = 3;
    public static final int SCHEDULE_NOTIFY_BEFORE_15_MINUTES = 4;
    public static final int SCHEDULE_NOTIFY_BEFORE_30_MINUTES = 5;
    public static final int SCHEDULE_NOTIFY_BEFORE_1_HOUR = 6;

    public static final String TABLE_NAME = "ScheduleContent";

    public static Uri URI = Uri.parse("content://" + ScheduleProvider.AUTHORITY + "/" + TABLE_NAME);

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_IS_WHOLE_DAY = "is_whole_day";
    public static final String COLUMN_LOCATION = "loc";
    public static final String COLUMN_NOTIFY = "notify";
    public static final String COLUMN_MEMBER = "member";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_START_TIME = "s_time";
    public static final String COLUMN_FINISH_TIME = "f_time";
}
