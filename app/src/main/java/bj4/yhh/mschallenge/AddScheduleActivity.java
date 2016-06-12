package bj4.yhh.mschallenge;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bj4.yhh.mschallenge.dialogs.ScheduleDescriptionDialog;
import bj4.yhh.mschallenge.provider.Schedule;
import bj4.yhh.mschallenge.provider.TableScheduleContent;

/**
 * Created by yenhsunhuang on 2016/6/3.
 */
public class AddScheduleActivity extends AppCompatActivity implements View.OnClickListener,
        ScheduleDescriptionDialog.Callback {

    public static final String EXTRA_YEAR = "e_year";
    public static final String EXTRA_MONTH = "e_month";
    public static final String EXTRA_DAY = "e_day";
    public static final String EXTRA_HOUR = "e_hour";
    public static final String EXTRA_MINUTE = "e_minute";
    public static final String EXTRA_START_TIME = "e_start_time";
    public static final String EXTRA_FINISH_TIME = "e_finish_time";
    public static final String EXTRA_SCHEDULE = "e_schedule";
    public static final String EXTRA_ID = "e_id";
    public static final String EXTRA_RESULT_REASON = "e_result_reason";
    private static final String EXTRA_TITLE = "e_title";
    private static final String EXTRA_IS_WHOLE_DAY = "e_is_whole_day";
    private static final String EXTRA_LOCATION = "e_location";
    private static final String EXTRA_NOTIFY = "e_notify";
    private static final String EXTRA_MEMBER = "e_member";
    private static final String EXTRA_DESCRIPTION = "e_description";
    private static final String TAG = "AddScheduleActivity";
    private static final boolean DEBUG = Utilities.DEBUG;
    private static final long HOUR = Utilities.HOUR;
    private static final long DAY = Utilities.DAY;

    private AutoCompleteTextView mTitle;
    private EditText mLocation;
    private TextView mStartDate, mFinishDate,
            mStartTime, mFinishTime, mMember, mDescription, mNotifyResult, mDelete;
    private Switch mWholeDaySwitcher;
    private LinearLayout mStartDateContainer, mFinishDateContainer;
    private RelativeLayout mWholeDayContainer, mNotifyContainer;

    private ValueAnimator mSwitcherAnimation;

    private ImageView mOk, mCancel;

    private Date mStartDateData, mFinishDateData;

    private String[] mNotifyStringArray;
    private int mNotifyDataIndex = TableScheduleContent.SCHEDULE_NOTIFY_NONE;
    private long mUpdateId = -1;

    private String mDescriptionData, mLocationData, mTitleData;
    private boolean mIsWholeDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_add_schedule);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year, month, day, hour, minute;
        if (savedInstanceState != null) {
            mUpdateId = savedInstanceState.getLong(EXTRA_ID);
            mTitleData = savedInstanceState.getString(EXTRA_TITLE);
            mIsWholeDay = savedInstanceState.getBoolean(EXTRA_IS_WHOLE_DAY);
            mStartDateData = new Date(savedInstanceState.getLong(EXTRA_START_TIME));
            mFinishDateData = new Date(savedInstanceState.getLong(EXTRA_FINISH_TIME));
            mLocationData = savedInstanceState.getString(EXTRA_LOCATION);
            mNotifyDataIndex = savedInstanceState.getInt(EXTRA_NOTIFY);
            mDescriptionData = savedInstanceState.getString(EXTRA_DESCRIPTION);
        } else if (getIntent() != null && getIntent().hasExtra(EXTRA_SCHEDULE)) {
            Schedule schedule;
            try {
                schedule = new Schedule(new JSONObject(getIntent().getStringExtra(EXTRA_SCHEDULE)));
                Calendar dateCalendar = Calendar.getInstance();
                dateCalendar.setTimeInMillis(schedule.getStartTime());
                mStartDateData = dateCalendar.getTime();
                dateCalendar.setTimeInMillis(schedule.getFinishTime());
                mFinishDateData = dateCalendar.getTime();
                mNotifyDataIndex = schedule.getNotify();
                mDescriptionData = schedule.getDescription();
                mLocationData = schedule.getLocation();
                mIsWholeDay = schedule.getIsWholeDay();
                mTitleData = schedule.getTitle();
                mUpdateId = schedule.getId();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            if (getIntent() != null) {
                year = getIntent().getIntExtra(EXTRA_YEAR, calendar.get(Calendar.YEAR));
                month = getIntent().getIntExtra(EXTRA_MONTH, calendar.get(Calendar.MONTH));
                day = getIntent().getIntExtra(EXTRA_DAY, calendar.get(Calendar.DAY_OF_MONTH));
                hour = getIntent().getIntExtra(EXTRA_HOUR, calendar.get(Calendar.HOUR_OF_DAY));
                minute = getIntent().getIntExtra(EXTRA_MINUTE, calendar.get(Calendar.MINUTE));

            } else {
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                minute = calendar.get(Calendar.MINUTE);
            }
            if (minute / 5 != 0) {
                // set minutes as the multiple of 5
                minute = minute + 5 - minute % 5;
            }
            mStartDateData = generateDateAndTime(year, month, day, hour, minute);
            mFinishDateData = generateDateAndTime(year, month, day, hour, minute);
            // one hour later
            mFinishDateData.setTime(mFinishDateData.getTime() + HOUR);
        }
        mNotifyStringArray = getResources().getStringArray(R.array.schedule_notify_time_list);
        initComponents();

        // hide ime when activity showing
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(EXTRA_ID, mUpdateId);
        outState.putString(EXTRA_TITLE, mTitle.getText().toString());
        outState.putBoolean(EXTRA_IS_WHOLE_DAY, mWholeDaySwitcher.isChecked());
        outState.putLong(EXTRA_START_TIME, mStartDateData.getTime());
        outState.putLong(EXTRA_FINISH_TIME, mFinishDateData.getTime());
        outState.putString(EXTRA_LOCATION, mLocation.getText().toString());
        outState.putInt(EXTRA_NOTIFY, mNotifyDataIndex);
        outState.putString(EXTRA_MEMBER, "");
        outState.putString(EXTRA_DESCRIPTION, mDescriptionData);
    }

    private Date generateDateAndTime(int y, int m, int d, int h, int min) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(y, m, d, h, min);
        return calendar.getTime();
    }

    private void initComponents() {
        initCustomActionBar();
        mTitle = (AutoCompleteTextView) findViewById(R.id.title);
        mTitle.setText(mTitleData);
        mTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mOk.setEnabled(!TextUtils.isEmpty(s));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        new AsyncTask<Void, Void, TitleArrayAdapter>() {
            @Override
            protected TitleArrayAdapter doInBackground(Void... params) {
                Set<String> dataSet = new HashSet<>();
                Cursor c = getContentResolver().query(TableScheduleContent.URI, new String[]{TableScheduleContent.COLUMN_TITLE},
                        null, null, null);
                if (c != null) {
                    try {
                        while (c.moveToNext()) {
                            dataSet.add(c.getString(0));
                        }
                    } finally {
                        c.close();
                    }
                }
                return new TitleArrayAdapter(AddScheduleActivity.this, android.R.layout.simple_list_item_1, new ArrayList(dataSet));
            }

            @Override
            protected void onPostExecute(TitleArrayAdapter adapter) {
                mTitle.setAdapter(adapter);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        mLocation = (EditText) findViewById(R.id.location);
        if (mLocationData != null) {
            mLocation.setText(mLocationData);
        }

        mWholeDayContainer = (RelativeLayout) findViewById(R.id.whole_day_container);
        mWholeDayContainer.setOnClickListener(this);
        mWholeDaySwitcher = (Switch) findViewById(R.id.whole_day_switcher);
        mWholeDaySwitcher.setChecked(mIsWholeDay);
        mWholeDaySwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (DEBUG) {
                    Log.d(TAG, "isChecked: " + isChecked);
                }
                if (mSwitcherAnimation.isRunning()) mSwitcherAnimation.cancel();
                if (isChecked) {
                    mSwitcherAnimation.start();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(mStartDateData);
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    mStartDateData.setTime(calendar.getTimeInMillis());
                    mStartDate.setText(getDateStringFormat(mStartDateData));
                    mStartTime.setText(getTimeStringFormat(mStartDateData));

                    calendar.setTime(mFinishDateData);
                    calendar.set(Calendar.HOUR_OF_DAY, 1);
                    calendar.set(Calendar.MINUTE, 0);
                    mFinishDateData.setTime(calendar.getTimeInMillis());
                    mFinishDate.setText(getDateStringFormat(mFinishDateData));
                    mFinishTime.setText(getTimeStringFormat(mFinishDateData));
                } else {
                    mSwitcherAnimation.reverse();
                }
            }
        });
        mWholeDaySwitcher.setClickable(false);
        mWholeDaySwitcher.setBackground(null);

        mStartDateContainer = (LinearLayout) findViewById(R.id.start_date_container);
        mStartDateContainer.setOnClickListener(this);
        mFinishDateContainer = (LinearLayout) findViewById(R.id.finish_date_container);
        mFinishDateContainer.setOnClickListener(this);

        mStartDate = (TextView) findViewById(R.id.start_date);
        mStartDate.setText(getDateStringFormat(mStartDateData));
        mFinishDate = (TextView) findViewById(R.id.finish_date);
        mFinishDate.setText(getDateStringFormat(mFinishDateData));

        mStartTime = (TextView) findViewById(R.id.start_time);
        mStartTime.setText(getTimeStringFormat(mStartDateData));
        mStartTime.setOnClickListener(this);
        mFinishTime = (TextView) findViewById(R.id.finish_time);
        mFinishTime.setOnClickListener(this);
        mFinishTime.setText(getTimeStringFormat(mFinishDateData));

        mNotifyResult = (TextView) findViewById(R.id.notify_result);
        mNotifyContainer = (RelativeLayout) findViewById(R.id.notify);
        mNotifyContainer.setOnClickListener(this);
        mMember = (TextView) findViewById(R.id.member);
        mMember.setOnClickListener(this);
        mDescription = (TextView) findViewById(R.id.description);
        if (mDescriptionData != null) {
            mDescription.setText(mDescriptionData);
        }
        mDescription.setOnClickListener(this);

        mSwitcherAnimation = new ValueAnimator();
        mSwitcherAnimation.setFloatValues(1f, 0f);
        mSwitcherAnimation.setDuration(250);
        mSwitcherAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mStartTime.setAlpha((Float) animation.getAnimatedValue());
                mFinishTime.setAlpha((Float) animation.getAnimatedValue());
            }
        });
        mSwitcherAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (mStartTime.getAlpha() == 0) {
                    mStartTime.setVisibility(View.VISIBLE);
                    mFinishTime.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mStartTime.getAlpha() == 0) {
                    mStartTime.setVisibility(View.INVISIBLE);
                    mFinishTime.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        mDelete = (TextView) findViewById(R.id.delete_schedule);
        if (mUpdateId != -1) {
            findViewById(R.id.delete_schedule_sep).setVisibility(View.VISIBLE);
            mDelete.setVisibility(View.VISIBLE);
            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(AddScheduleActivity.this)
                            .setMessage(R.string.schedule_remove_confirm_dialog_message)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getContentResolver().delete(TableScheduleContent.URI, TableScheduleContent.COLUMN_ID + "=" + mUpdateId, null);
                                    Intent intent = new Intent();
                                    intent.putExtra(EXTRA_START_TIME, mStartDateData.getTime());
                                    intent.putExtra(EXTRA_FINISH_TIME, mFinishDateData.getTime());
                                    intent.putExtra(EXTRA_RESULT_REASON, R.string.calendar_activity_remove_schedule_success);
                                    setResult(Activity.RESULT_OK, intent);
                                    finish();
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create().show();
                }
            });
        }
    }

    private void initCustomActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        View v = getLayoutInflater().inflate(R.layout.schedule_toolbar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT);
        getSupportActionBar().setCustomView(v, params);
        mOk = (ImageView) v.findViewById(R.id.ok);
        mOk.setOnClickListener(this);
        mOk.setEnabled(!TextUtils.isEmpty(mTitleData));
        mCancel = (ImageView) v.findViewById(R.id.cancel);
        mCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.ok) {
            handleOkClick();
        } else if (id == R.id.cancel) {
            handleOnCancel();
        } else if (id == R.id.start_date_container) {
            handleStartDateContainerClick();
        } else if (id == R.id.finish_date_container) {
            handleFinishDateContainerClick();
        } else if (id == R.id.whole_day_container) {
            mWholeDaySwitcher.setChecked(!mWholeDaySwitcher.isChecked());
        } else if (id == R.id.start_time) {
            handleStartTimeClick();
        } else if (id == R.id.finish_time) {
            handleFinishTimeClick();
        } else if (id == R.id.notify) {
            handleNotifyClick();
        } else if (id == R.id.description) {
            handleDescriptionClick();
        }
    }

    @Override
    public void onBackPressed() {
        if (mOk.isEnabled()) {
            handleOnCancel();
        } else {
            super.onBackPressed();
        }
    }

    private void handleOnCancel() {
        new AlertDialog.Builder(AddScheduleActivity.this)
                .setTitle(R.string.schedule_cancel_confirm_dialog_title)
                .setMessage(R.string.schedule_cancel_confirm_dialog_message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(Activity.RESULT_CANCELED);
                        finish();
                    }
                }).setNegativeButton(R.string.schedule_cancel_confirm_dialog_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create().show();
    }

    private void handleOkClick() {
        ContentValues cv = new ContentValues();
        cv.put(TableScheduleContent.COLUMN_TITLE, mTitle.getText().toString());
        cv.put(TableScheduleContent.COLUMN_IS_WHOLE_DAY, mWholeDaySwitcher.isChecked());
        cv.put(TableScheduleContent.COLUMN_START_TIME, mStartDateData.getTime());
        cv.put(TableScheduleContent.COLUMN_FINISH_TIME, mFinishDateData.getTime());
        cv.put(TableScheduleContent.COLUMN_LOCATION, mLocation.getText().toString());
        cv.put(TableScheduleContent.COLUMN_NOTIFY, mNotifyDataIndex);
        cv.put(TableScheduleContent.COLUMN_MEMBER, "");
        cv.put(TableScheduleContent.COLUMN_DESCRIPTION, mDescriptionData == null ? "" : mDescriptionData);
        int okReason;
        if (mUpdateId == -1) {
            okReason = R.string.calendar_activity_add_schedule_success;
            getContentResolver().insert(TableScheduleContent.URI, cv);
        } else {
            okReason = R.string.calendar_activity_update_schedule_success;
            getContentResolver().update(TableScheduleContent.URI, cv, TableScheduleContent.COLUMN_ID + "=" + mUpdateId, null);
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_START_TIME, mStartDateData.getTime());
        intent.putExtra(EXTRA_FINISH_TIME, mFinishDateData.getTime());
        intent.putExtra(EXTRA_ID, mUpdateId);
        intent.putExtra(EXTRA_RESULT_REASON, okReason);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void handleDescriptionClick() {
        DialogFragment dialog = new ScheduleDescriptionDialog();
        Bundle bundle = new Bundle();
        bundle.putString(ScheduleDescriptionDialog.EXTRA_DESCRIPTION, mDescriptionData);
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), ScheduleDescriptionDialog.class.getName());
    }

    private void handleNotifyClick() {
        new AlertDialog.Builder(AddScheduleActivity.this)
                .setTitle(null)
                .setSingleChoiceItems(mNotifyStringArray, mNotifyDataIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mNotifyDataIndex = which;
                        if (mNotifyDataIndex == 0) {
                            mNotifyResult.setText(null);
                        } else {
                            mNotifyResult.setText(mNotifyStringArray[mNotifyDataIndex]);
                        }
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    private void handleStartTimeClick() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(mStartDateData);
        TimePickerDialog dialog = new TimePickerDialog(AddScheduleActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mStartDateData = generateDateAndTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
                mStartTime.setText(getTimeStringFormat(mStartDateData));
                if (mStartDateData.getTime() > mFinishDateData.getTime()) {
                    mFinishDateData.setTime(mStartDateData.getTime() + HOUR);
                    mFinishTime.setText(getTimeStringFormat(mFinishDateData));
                    mFinishDate.setText(getDateStringFormat(mFinishDateData));
                }
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        dialog.show();
    }

    private void handleFinishTimeClick() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(mFinishDateData);
        TimePickerDialog dialog = new TimePickerDialog(AddScheduleActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mFinishDateData = generateDateAndTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
                mFinishTime.setText(getTimeStringFormat(mFinishDateData));
                if (mFinishDateData.getTime() < mStartDateData.getTime()) {
                    mStartDateData.setTime(mFinishDateData.getTime() - HOUR);
                    mStartTime.setText(getTimeStringFormat(mStartDateData));
                    mStartDate.setText(getDateStringFormat(mStartDateData));
                }
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        dialog.show();
    }

    private void handleStartDateContainerClick() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(mStartDateData);
        DatePickerDialog dialog = new DatePickerDialog(AddScheduleActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mStartDateData = generateDateAndTime(year, monthOfYear, dayOfMonth, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                mStartDate.setText(getDateStringFormat(mStartDateData));
                if (mStartDateData.getTime() > mFinishDateData.getTime()) {
                    final long hourAndMinuteData = mFinishDateData.getTime() % (DAY);
                    final long newFinishDateData = (mStartDateData.getTime() / DAY) * DAY + hourAndMinuteData;
                    mFinishDateData.setTime(newFinishDateData);
                    mFinishDate.setText(getDateStringFormat(mFinishDateData));
                    mFinishTime.setText(getTimeStringFormat(mFinishDateData));
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void handleFinishDateContainerClick() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(mFinishDateData);
        DatePickerDialog dialog = new DatePickerDialog(AddScheduleActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mFinishDateData = generateDateAndTime(year, monthOfYear, dayOfMonth, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                mFinishDate.setText(getDateStringFormat(mFinishDateData));
                if (mFinishDateData.getTime() < mStartDateData.getTime()) {
                    final long hourAndMinuteData = mStartDateData.getTime() % (DAY);
                    final long newStartDateData = (mFinishDateData.getTime() / DAY) * DAY + hourAndMinuteData;
                    mStartDateData.setTime(newStartDateData);
                    mStartDate.setText(getDateStringFormat(mStartDateData));
                    mStartTime.setText(getTimeStringFormat(mStartDateData));
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private String getDateStringFormat(Date date) {
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTimeInMillis(System.currentTimeMillis());
        Calendar setCalendar = Calendar.getInstance();
        setCalendar.setTime(date);
        String format;
        if (currentCalendar.get(Calendar.YEAR) != setCalendar.get(Calendar.YEAR)) {
            format = "EEE, MMM dd, yyyy";
        } else {
            format = "EEE, MMM dd";
        }
        return new SimpleDateFormat(format).format(date);
    }

    private String getTimeStringFormat(Date date) {
        return new SimpleDateFormat("HH:mm").format(date);
    }

    @Override
    public void onDescriptionUpdate(String description) {
        if (TextUtils.isEmpty(description)) {
            mDescriptionData = null;
            mDescription.setText(R.string.schedule_description);
        } else {
            mDescriptionData = description;
            mDescription.setText(description);
        }
    }

    private static class TitleArrayAdapter extends ArrayAdapter<String> {
        private final List<String> mAllData = new ArrayList<>();

        public TitleArrayAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            mAllData.addAll(objects);
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    List<String> data = new ArrayList<>();
                    for (String str : mAllData) {
                        if (str.contains(constraint)) {
                            data.add(str);
                        }
                    }
                    results.count = data.size();
                    results.values = data;
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        clear();
                        addAll((List<String>) results.values);
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
        }
    }

}
