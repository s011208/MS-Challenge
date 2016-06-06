package bj4.yhh.mschallenge.provider;

import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by yenhsunhuang on 2016/6/6.
 */
public class Schedule {
    private long mId, mStartTime, mFinishTime;
    private String mTitle, mLocation, mMember, mDescription;
    private boolean mIsWholeDay;
    private int mNotify;

    public Schedule(long id, String title, boolean isWholeDay, long startTime, long finishTime
            , int notify, String location, String member, String description) {
        mId = id;
        mTitle = title;
        mIsWholeDay = isWholeDay;
        mStartTime = startTime;
        mFinishTime = finishTime;
        mNotify = notify;
        mLocation = location;
        mMember = member;
        mDescription = description;
    }

    public long getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public boolean getIsWholeDay() {
        return mIsWholeDay;
    }

    public long getStartTime() {
        return mStartTime;
    }

    public long getFinishTime() {
        return mFinishTime;
    }

    public int getNotify() {
        return mNotify;
    }

    public String getLocation() {
        return mLocation;
    }

    public String getMember() {
        return mMember;
    }

    public String getDescription() {
        return mDescription;
    }

    public static ArrayList<Schedule> fromCursor(Cursor cursor) {
        final ArrayList<Schedule> rtn = new ArrayList<>();
        if (cursor == null) return rtn;
        try {
            final int indexOfId = cursor.getColumnIndex(TableScheduleContent.COLUMN_ID);
            final int indexOfTitle = cursor.getColumnIndex(TableScheduleContent.COLUMN_TITLE);
            final int indexOfIsWholeDay = cursor.getColumnIndex(TableScheduleContent.COLUMN_IS_WHOLE_DAY);
            final int indexOfStartTime = cursor.getColumnIndex(TableScheduleContent.COLUMN_START_TIME);
            final int indexOfFinishTime = cursor.getColumnIndex(TableScheduleContent.COLUMN_FINISH_TIME);
            final int indexOfNotify = cursor.getColumnIndex(TableScheduleContent.COLUMN_NOTIFY);
            final int indexOfLocation = cursor.getColumnIndex(TableScheduleContent.COLUMN_LOCATION);
            final int indexOfMember = cursor.getColumnIndex(TableScheduleContent.COLUMN_MEMBER);
            final int indexOfDescription = cursor.getColumnIndex(TableScheduleContent.COLUMN_DESCRIPTION);
            while (cursor.moveToNext()) {
                rtn.add(new Schedule(
                        cursor.getLong(indexOfId),
                        cursor.getString(indexOfTitle),
                        cursor.getInt(indexOfIsWholeDay) == ScheduleProvider.TRUE,
                        cursor.getLong(indexOfStartTime),
                        cursor.getLong(indexOfFinishTime),
                        cursor.getInt(indexOfNotify),
                        cursor.getString(indexOfLocation),
                        cursor.getString(indexOfMember),
                        cursor.getString(indexOfDescription)));
            }
        } finally {
            cursor.close();
        }
        return rtn;
    }

    @Override
    public String toString() {
        return "title: " + mTitle + ", startTime: " + mStartTime + ", finishTime: " + mFinishTime;
    }
}
