package bj4.yhh.mschallenge.agenda;

import bj4.yhh.mschallenge.provider.Schedule;

/**
 * Created by yenhsunhuang on 2016/6/6.
 */
public class Event extends AgendaItem {
    private long mSectionDateTime;
    private Schedule mSchedule;

    public Event(Schedule schedule, long sectionDateTime) {
        mSchedule = schedule;
        mSectionDateTime = sectionDateTime;
    }

    public Schedule getSchedule() {
        return mSchedule;
    }

    public long getSectionDateTime() {
        return mSectionDateTime;
    }

    @Override
    public boolean isClickable() {
        return true;
    }
}
