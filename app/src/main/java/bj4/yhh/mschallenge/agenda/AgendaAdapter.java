package bj4.yhh.mschallenge.agenda;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import bj4.yhh.mschallenge.R;
import bj4.yhh.mschallenge.Utilities;
import bj4.yhh.mschallenge.provider.Schedule;
import bj4.yhh.mschallenge.provider.TableWeather;
import bj4.yhh.mschallenge.views.PinnedSectionListView;
import bj4.yhh.mschallenge.weather.RetrieveWeatherDataHelper;
import bj4.yhh.mschallenge.weather.WeatherConfig;

/**
 * Created by yenhsunhuang on 2016/6/6.
 */
public class AgendaAdapter extends BaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter {
    public static final int ITEM_VIEW_TYPE_SECTION = 0;
    public static final int ITEM_VIEW_TYPE_EVENT = 1;
    public static final int ITEM_VIEW_TYPE_WEATHER = 2;
    public static final int ITEM_VIEW_TYPE_NO_EVENT = 3;
    private static final boolean DEBUG = Utilities.DEBUG;
    private static final String TAG = "AgendaAdapter";
    private final Context mContext;
    private final LayoutInflater mInflater;
    private final ArrayList<AgendaItem> mItems = new ArrayList<>();
    private final Calendar mClearOffsetCalendar = Calendar.getInstance(),
            mOriginCalendar = Calendar.getInstance(), mReusableCalendar = Calendar.getInstance();
    private long mStartDateTime, mFinishDateTime;
    private Callback mCallback;
    private int mWindowBackgroundColor;

    private RetrieveAgendaDataHelper.Callback mRetrieveAgendaDataHelperCallback;

    public AgendaAdapter(Context context, long startDateTime, long finishDateTime, long selectedDateTime, Callback cb) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mClearOffsetCalendar.setTimeInMillis(System.currentTimeMillis());
        Utilities.clearCalendarOffset(mClearOffsetCalendar);
        mCallback = cb;
        setDateTimeRange(startDateTime, finishDateTime, selectedDateTime);
        setWindowBackgroundColor();
    }

    /**
     * @param startDateTime start time of agenda view
     * @param finishDateTime finish time of agenda view
     * @param selectedDateTime focus date item
     * @return whether reload data
     */
    public boolean setDateTimeRange(long startDateTime, long finishDateTime, long selectedDateTime) {
        final boolean isTimeOverlapping = Utilities.isTimeOverlapping(mStartDateTime + Utilities.SECOND, mFinishDateTime - Utilities.SECOND, selectedDateTime, selectedDateTime + Utilities.DAY);
        if (DEBUG) {
            Log.v(TAG, "isTimeOverlapping: " + isTimeOverlapping + ", mStartDateTime: " + Utilities.debugDateTime(mStartDateTime)
                    + ", mFinishDateTime: " + Utilities.debugDateTime(mFinishDateTime) + ", selectedDateTime: " + Utilities.debugDateTime(selectedDateTime));
        }
        if (isTimeOverlapping) {
            return false;
        } else {
            mStartDateTime = startDateTime;
            mFinishDateTime = finishDateTime;
            initData(false);
            return true;
        }
    }

    /**
     * reload data with the same range of time
     */
    public void reloadData() {
        initData(true);
    }

    private void initData(final boolean reload) {
        if (mRetrieveAgendaDataHelperCallback == null) {
            mRetrieveAgendaDataHelperCallback = new RetrieveAgendaDataHelper.Callback() {
                @Override
                public void onDataRetrieved(ArrayList<AgendaItem> data) {
                    mItems.clear();
                    mItems.addAll(data);
                    if (mCallback != null) {
                        mCallback.onDataLoaded(reload);
                    }
                    notifyDataSetChanged();
                }
            };
        }
        new RetrieveAgendaDataHelper(mContext, mStartDateTime, mFinishDateTime, mRetrieveAgendaDataHelperCallback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
                convertView = handleViewTypeSection(position, convertView, parent);
                break;
            case ITEM_VIEW_TYPE_EVENT:
                convertView = handleViewTypeEvent(position, convertView);
                break;
            case ITEM_VIEW_TYPE_NO_EVENT:
                convertView = handleViewTypeNoEvent(position, convertView);
                break;
            case ITEM_VIEW_TYPE_WEATHER:
                convertView = handlerViewTypeWeather(position, convertView);
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

    private View handlerViewTypeWeather(int position, View convertView) {
        Weather weather = (Weather) getItem(position);
        WeatherViewHolder holder;
        if (convertView == null) {
            holder = new WeatherViewHolder();
            convertView = mInflater.inflate(R.layout.agenda_adapter_weather, null);
            holder.mWeather = (TextView) convertView.findViewById(R.id.weather);
            holder.mWeatherTime = (TextView) convertView.findViewById(R.id.weather_time);
            holder.mWeatherContainer = (RelativeLayout) convertView.findViewById(R.id.weather_container);
            convertView.setTag(holder);
        } else {
            holder = (WeatherViewHolder) convertView.getTag();
        }
        final int currentHour = mOriginCalendar.get(Calendar.HOUR_OF_DAY);
        mReusableCalendar.setTimeInMillis(weather.getDateTime());
        final int focusBackgroundColor = Color.rgb(255, 235, 238);
        final boolean isToday = mReusableCalendar.get(Calendar.YEAR) == mOriginCalendar.get(Calendar.YEAR) &&
                mReusableCalendar.get(Calendar.MONTH) == mOriginCalendar.get(Calendar.MONTH) &&
                mReusableCalendar.get(Calendar.DAY_OF_MONTH) == mOriginCalendar.get(Calendar.DAY_OF_MONTH);
        switch (weather.getWeatherTime()) {
            case TableWeather.WEATHER_TIME_MORNING:
                holder.mWeatherTime.setText(R.string.agenda_view_morning);
                if (isToday && currentHour <= 12 && currentHour >= 0) {
                    holder.mWeatherContainer.setBackgroundColor(focusBackgroundColor);
                } else {
                    holder.mWeatherContainer.setBackgroundColor(mWindowBackgroundColor);
                }
                break;
            case TableWeather.WEATHER_TIME_AFTERNOON:
                holder.mWeatherTime.setText(R.string.agenda_view_afternoon);
                if (isToday && currentHour <= 18 && currentHour > 12) {
                    holder.mWeatherContainer.setBackgroundColor(focusBackgroundColor);
                } else {
                    holder.mWeatherContainer.setBackgroundColor(mWindowBackgroundColor);
                }
                break;
            case TableWeather.WEATHER_TIME_NIGHT:
                if (isToday && currentHour <= 24 && currentHour > 18) {
                    holder.mWeatherContainer.setBackgroundColor(focusBackgroundColor);
                } else {
                    holder.mWeatherContainer.setBackgroundColor(mWindowBackgroundColor);
                }
                holder.mWeatherTime.setText(R.string.agenda_view_night);
                break;
        }
        holder.mWeather.setTag(position);
        if (TextUtils.isEmpty(weather.getIconString())) {
            // load from helper
            holder.mWeather.setCompoundDrawables(null, null, null, null);
            holder.mWeather.setText("");
            holder.mWeather.setContentDescription("");
            new RetrieveWeatherDataHelper(mContext, weather, holder.mWeather, position).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        } else {
            holder.mWeather.setCompoundDrawablesWithIntrinsicBounds(WeatherConfig.getIconResource(weather.getIconString()), 0, 0, 0);
            holder.mWeather.setText(weather.getTemperature() + "°");
            holder.mWeather.setContentDescription(weather.getSummary());
        }
        return convertView;
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
            Calendar sectionCalendar = Calendar.getInstance();
            sectionCalendar.setTimeInMillis(item.getSectionDateTime());
            int finishH = finishCalendar.get(Calendar.HOUR_OF_DAY);
            int finishM = finishCalendar.get(Calendar.MINUTE);
            int startH = startCalendar.get(Calendar.HOUR_OF_DAY);
            int startM = startCalendar.get(Calendar.MINUTE);
            if (startCalendar.get(Calendar.YEAR) != sectionCalendar.get(Calendar.YEAR)
                    || startCalendar.get(Calendar.MONTH) != sectionCalendar.get(Calendar.MONTH)
                    || startCalendar.get(Calendar.DAY_OF_MONTH) != sectionCalendar.get(Calendar.DAY_OF_MONTH)) {
                startH = startM = 0;
            }

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
        NoEventViewHolder holder;
        if (convertView == null) {
            holder = new NoEventViewHolder();
            convertView = mInflater.inflate(R.layout.agenda_adapter_no_event, null);
            holder.mNoEvent = (TextView) convertView.findViewById(R.id.no_event);
            convertView.setTag(holder);
        }
        return convertView;
    }

    private View handleViewTypeSection(int position, View convertView, ViewGroup parent) {
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
        if (mClearOffsetCalendar.get(Calendar.YEAR) != calendar.get(Calendar.YEAR)) {
            display = new SimpleDateFormat("EEEE, MMMM dd, yyyy").format(item.getDateTime());
        } else {
            display = new SimpleDateFormat("EEEE, MMMM dd").format(item.getDateTime());
        }
        if (calendar.get(Calendar.MONTH) == mClearOffsetCalendar.get(Calendar.MONTH)
                && calendar.get(Calendar.YEAR) == mClearOffsetCalendar.get(Calendar.YEAR)) {
            int currentDay = mClearOffsetCalendar.get(Calendar.DAY_OF_MONTH);
            int sectionDay = calendar.get(Calendar.DAY_OF_MONTH);
            if (currentDay - 1 == sectionDay) {
                display = mContext.getResources().getString(R.string.agenda_view_yesterday) + " ·" + display;
            } else if (currentDay == sectionDay) {
                display = mContext.getResources().getString(R.string.agenda_view_today) + " ·" + display;
            } else if (currentDay + 1 == sectionDay) {
                display = mContext.getResources().getString(R.string.agenda_view_tomorrow) + " ·" + display;
            }
            if (currentDay == sectionDay) {
                holder.mSectionTitle.setTextColor(Color.rgb(0x3f, 0x51, 0xb5));
                holder.mSectionTitle.setBackgroundColor(Color.rgb(0xe3, 0xf2, 0xfd));
            } else {
                holder.mSectionTitle.setTextColor(Color.rgb(0x99, 0x99, 0x99));
                holder.mSectionTitle.setBackgroundColor(mWindowBackgroundColor);
            }
        }
        holder.mSectionTitle.setText(display);
        return convertView;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == ITEM_VIEW_TYPE_SECTION;
    }

    private void setWindowBackgroundColor() {
        try {
            int[] backgroundColorAttr = new int[]{android.R.attr.windowBackground};
            int indexOfAttrBackgroundColor = 0;
            TypedArray a = mContext.obtainStyledAttributes(backgroundColorAttr);
            mWindowBackgroundColor = a.getColor(indexOfAttrBackgroundColor, -1);
            a.recycle();
        } catch (Exception e) {
            mWindowBackgroundColor = Color.WHITE;
        }
    }

    public interface Callback {
        void onDataLoaded(boolean reload);
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

    private static class WeatherViewHolder {
        TextView mWeatherTime, mWeather;
        RelativeLayout mWeatherContainer;
    }
}
