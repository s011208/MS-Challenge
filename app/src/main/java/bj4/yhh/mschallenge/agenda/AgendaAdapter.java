package bj4.yhh.mschallenge.agenda;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import bj4.yhh.mschallenge.R;
import bj4.yhh.mschallenge.Utilities;
import bj4.yhh.mschallenge.provider.Schedule;

/**
 * Created by yenhsunhuang on 2016/6/6.
 */
public class AgendaAdapter extends BaseAdapter {
    private static final boolean DEBUG = Utilities.DEBUG;
    private static final String TAG = "AgendaAdapter";

    public static final int ITEM_VIEW_TYPE_SECTION = 0;
    public static final int ITEM_VIEW_TYPE_EVENT = 1;
    public static final int ITEM_VIEW_TYPE_WEATHER = 2;
    public static final int ITEM_VIEW_TYPE_NO_EVENT = 3;

    private final Context mContext;
    private final LayoutInflater mInflater;
    private final ArrayList<AgendaItem> mItems = new ArrayList<>();
    private long mStartDateTime, mFinishDateTime;
    private final Calendar mCalendar = Calendar.getInstance();

    public AgendaAdapter(Context context, long startDateTime, long finishDateTime) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        Utilities.clearCalendarOffset(mCalendar);
        setDateTimeRange(startDateTime, finishDateTime);
    }

    public void setDateTimeRange(long startDateTime, long finishDateTime) {
        mStartDateTime = startDateTime;
        mFinishDateTime = finishDateTime;
        initData();
    }

    private void initData() {
        new RetrieveAgendaDataHelper(mContext, mStartDateTime, mFinishDateTime, new RetrieveAgendaDataHelper.Callback() {
            @Override
            public void onDataRetrieved(ArrayList<AgendaItem> data) {
                mItems.clear();
                mItems.addAll(data);
                notifyDataSetChanged();
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public AgendaItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_VIEW_TYPE_SECTION:
                convertView = handleViewTypeSection(position, convertView);
                break;
            case ITEM_VIEW_TYPE_EVENT:
                convertView = handleViewTypeEvent(position, convertView);
                break;
            case ITEM_VIEW_TYPE_NO_EVENT:
                convertView = handleViewTypeNoEvent(position, convertView);
                break;
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) instanceof Section) return ITEM_VIEW_TYPE_SECTION;
        else if (getItem(position) instanceof Event) return ITEM_VIEW_TYPE_EVENT;
        else if (getItem(position) instanceof Weather) return ITEM_VIEW_TYPE_WEATHER;
        else if (getItem(position) instanceof NoEvent) return ITEM_VIEW_TYPE_NO_EVENT;
        else throw new RuntimeException("unexpected item view type: " + getItem(position));
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    private View handleViewTypeEvent(int position, View convertView) {
        final Event item = (Event) getItem(position);
        EventViewHolder holder;
        if (convertView == null) {
            holder = new EventViewHolder();
            convertView = mInflater.inflate(R.layout.agenda_adapter_event, null);
            holder.mTitle = (TextView) convertView.findViewById(R.id.title);
            holder.mDuration = (TextView) convertView.findViewById(R.id.duration);
            holder.mLocation = (TextView) convertView.findViewById(R.id.location);
            holder.mStartTime = (TextView) convertView.findViewById(R.id.start_time);
            convertView.setTag(holder);
        } else {
            holder = (EventViewHolder) convertView.getTag();
        }
        final Schedule schedule = item.getSchedule();
        holder.mTitle.setText(schedule.getTitle());
        holder.mLocation.setVisibility(TextUtils.isEmpty(schedule.getLocation()) ? View.INVISIBLE : View.VISIBLE);
        holder.mLocation.setText(schedule.getLocation());
        if (schedule.getIsWholeDay()) {
            holder.mStartTime.setText(R.string.schedule_activity_whole_day);
            holder.mDuration.setText("1d");
        } else {
            if (schedule.getStartTime() < item.getSectionDateTime()) {
                holder.mStartTime.setText("0:00 AM");
            } else {
                holder.mStartTime.setText(new SimpleDateFormat("h:mm a").format(schedule.getStartTime()));
            }

            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTimeInMillis(schedule.getStartTime());
            Calendar finishCalendar = Calendar.getInstance();
            finishCalendar.setTimeInMillis(schedule.getFinishTime());
            int finishH = finishCalendar.get(Calendar.HOUR_OF_DAY);
            int finishM = finishCalendar.get(Calendar.MINUTE);
            int startH = startCalendar.get(Calendar.HOUR_OF_DAY);
            int startM = startCalendar.get(Calendar.MINUTE);
            int hours, minutes;
            if (schedule.getFinishTime() >= item.getSectionDateTime() + Utilities.DAY) {
                int diffMinutes = Utilities.getDiffMinutes(startH, startM, 24, 00);
                hours = diffMinutes / 60;
                minutes = diffMinutes % 60;
                holder.mDuration.setText((hours == 0 ? "" : hours + "h") + (minutes == 0 ? "" : minutes + "m"));
            } else {
                int diffMinutes = Utilities.getDiffMinutes(finishH, finishM, startH, startM);
                hours = diffMinutes / 60;
                minutes = diffMinutes % 60;
                holder.mDuration.setText((hours == 0 ? "" : hours + "h") + (minutes == 0 ? "" : minutes + "m"));
            }
        }
        return convertView;
    }

    private View handleViewTypeNoEvent(int position, View convertView) {
        final NoEvent item = (NoEvent) getItem(position);
        NoEventViewHolder holder;
        if (convertView == null) {
            holder = new NoEventViewHolder();
            convertView = mInflater.inflate(R.layout.agenda_adapter_no_event, null);
            holder.mNoEvent = (TextView) convertView.findViewById(R.id.no_event);
            convertView.setTag(holder);
        } else {
            holder = (NoEventViewHolder) convertView.getTag();
        }
        return convertView;
    }

    private View handleViewTypeSection(int position, View convertView) {
        final Section item = (Section) getItem(position);
        SectionViewHolder holder;
        if (convertView == null) {
            holder = new SectionViewHolder();
            convertView = mInflater.inflate(R.layout.agenda_adapter_section, null);
            holder.mSectionTitle = (TextView) convertView.findViewById(R.id.section_title);
            convertView.setTag(holder);
        } else {
            holder = (SectionViewHolder) convertView.getTag();
        }
        String display;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(item.getDateTime());
        if (mCalendar.get(Calendar.YEAR) != calendar.get(Calendar.YEAR)) {
            display = new SimpleDateFormat("EEEE, MMMM dd, yyyy").format(item.getDateTime());
        } else {
            display = new SimpleDateFormat("EEEE, MMMM dd").format(item.getDateTime());
        }
        if (calendar.get(Calendar.MONTH) == mCalendar.get(Calendar.MONTH)
                && calendar.get(Calendar.YEAR) == mCalendar.get(Calendar.YEAR)) {
            int currentDay = mCalendar.get(Calendar.DAY_OF_MONTH);
            int sectionDay = calendar.get(Calendar.DAY_OF_MONTH);
            if (currentDay - 1 == sectionDay) {
                display = mContext.getResources().getString(R.string.agenda_view_yesterday) + " ·" + display;
            } else if (currentDay == sectionDay) {
                display = mContext.getResources().getString(R.string.agenda_view_today) + " ·" + display;
            } else if (currentDay + 1 == sectionDay) {
                display = mContext.getResources().getString(R.string.agenda_view_tomorrow) + " ·" + display;
            }
        }
        holder.mSectionTitle.setText(display);
        return convertView;
    }

    private static class EventViewHolder {
        TextView mTitle, mLocation, mStartTime, mDuration;
    }

    private static class NoEventViewHolder {
        TextView mNoEvent;
    }

    private static class SectionViewHolder {
        TextView mSectionTitle;
    }
}
