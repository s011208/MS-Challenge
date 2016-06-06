package bj4.yhh.mschallenge.agenda;

/**
 * Created by yenhsunhuang on 2016/6/6.
 */
public class Section extends AgendaItem {
    private long mDateTime;

    public Section(long dateTime) {
        mDateTime = dateTime;
    }

    public long getDateTime() {
        return mDateTime;
    }

    @Override
    public boolean isClickable() {
        return false;
    }
}
