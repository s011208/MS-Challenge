package bj4.yhh.mschallenge;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import bj4.yhh.mschallenge.agenda.AgendaAdapter;
import bj4.yhh.mschallenge.agenda.AgendaItem;
import bj4.yhh.mschallenge.agenda.AgendaView;
import bj4.yhh.mschallenge.agenda.Event;
import bj4.yhh.mschallenge.agenda.NoEvent;
import bj4.yhh.mschallenge.agenda.Section;
import bj4.yhh.mschallenge.calendar.CalendarDateView;
import bj4.yhh.mschallenge.calendar.CalendarPager;
import bj4.yhh.mschallenge.dialogs.ViewScheduleDialog;
import bj4.yhh.mschallenge.provider.Schedule;
import bj4.yhh.mschallenge.settings.MsChallengePreference;

public class CalendarActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        CalendarDateView.Callback, ViewScheduleDialog.Callback {

    private static final String TAG = "CalendarActivity";
    private static final boolean DEBUG = Utilities.DEBUG;
    private static final boolean IS_SUPPORT_MATERIAL_DESIGN = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    private static final int CALENDAR_VIEW_VISIBILITY_CHANGE_DURATION = 500;
    private static final int REQUEST_ADD_NEW_SCHEDULE = 1000;
    private static final int SNAKE_BAR_DELAY_TIME = 1000;

    private static final String EXTRA_MENU_BUTTON_TEXT = "e_menu_button_text";
    private static final String EXTRA_IS_SHOW_CALENDAR = "e_is_show_calendar";

    private static final int REQUEST_PERMISSION_LOCATION = 1000;

    private final Calendar mCalendar = Calendar.getInstance();
    private final List<String> mMonthString = Utilities.getMonthString();
    private TextView mMenuMonthText;
    private boolean mIsShowCalendar = false;
    private CalendarPager mCalendarPager;
    private Date mSelectedDateTime;
    private AgendaView mAgendaView;
    private ValueAnimator mMenuButtonAnimator;
    private FloatingActionButton mFab;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate activity id: " + CalendarActivity.this);
        setContentView(R.layout.activity_calendar);
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        Utilities.clearCalendarOffset(mCalendar);
        mSelectedDateTime = mCalendar.getTime();
        mIsShowCalendar = savedInstanceState == null ? false : savedInstanceState.getBoolean(EXTRA_IS_SHOW_CALENDAR, false);
        initComponents(savedInstanceState);
        requestPermissions();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_MENU_BUTTON_TEXT, mMenuMonthText.getText().toString());
        outState.putBoolean(EXTRA_IS_SHOW_CALENDAR, mIsShowCalendar);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            // we do not care about location result currently
        }
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(CalendarActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(CalendarActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(CalendarActivity.this)
                        .setTitle(R.string.permission_dialog_location_title).setMessage(R.string.permission_dialog_location_title).create().show();
            } else {
                ActivityCompat.requestPermissions(CalendarActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSION_LOCATION);
            }
        }
    }

    private void initComponents(Bundle savedInstanceState) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(mSelectedDateTime);
                startAddScheduleActivityForResult(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initCustomActionBar(savedInstanceState);
        mCalendarPager = (CalendarPager) findViewById(R.id.calendar_pager);
        mCalendarPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                int[] value = mCalendarPager.getCurrentMonthAndYear();
                if (value != null) {
                    String text = mMonthString.get(value[1]);
                    if (value[0] != mCalendar.get(Calendar.YEAR)) {
                        text += " " + value[0];
                    }
                    mMenuMonthText.setText(text);
                    mCalendarPager.setSelectedDate(mSelectedDateTime, position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mAgendaView = (AgendaView) findViewById(R.id.agenda_view);
        mAgendaView.setCallback(new AgendaView.Callback() {
            @Override
            public void onSectionItemChanged(long newItemDateTime) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(newItemDateTime);
                mSelectedDateTime = calendar.getTime();
                mCalendarPager.setSelectedDate(calendar.getTime());
            }
        });
        mAgendaView.setDate(mSelectedDateTime);
        mAgendaView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AgendaItem item = (AgendaItem) mAgendaView.getAdapter().getItem(position);
                if (item instanceof NoEvent) {
                    Section section = mAgendaView.findSectionOfItem(position);
                    if (DEBUG) {
                        Log.d(TAG, "section: " + Utilities.debugDateTime(section.getDateTime()) + ", position: " + position);
                    }
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(section.getDateTime());
                    startAddScheduleActivityForResult(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                } else if (item instanceof Event) {
                    Section section = mAgendaView.findSectionOfItem(position);
                    Event event = (Event) item;
                    ViewScheduleDialog dialog = new ViewScheduleDialog();
                    Bundle arguments = new Bundle();
                    arguments.putString(ViewScheduleDialog.ARGUMENT_SCHEDULE, event.getSchedule().toString());
                    arguments.putLong(ViewScheduleDialog.ARGUMENT_SECTION_TIME, section.getDateTime());
                    dialog.setArguments(arguments);
                    dialog.show(getFragmentManager(), ViewScheduleDialog.class.getName());
                }
            }
        });
        switchCalendarVisibility(false);
    }

    private void initCustomActionBar(Bundle savedInstanceState) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setContentInsetsAbsolute(0, 0);
            toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_settings_white_24dp));
        }
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT);
        mMenuMonthText = (TextView) ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.menu_month_layout, null);
        mMenuMonthText.setText(savedInstanceState == null ? mMonthString.get(mCalendar.get(Calendar.MONTH)) : savedInstanceState.getString(EXTRA_MENU_BUTTON_TEXT, mMonthString.get(mCalendar.get(Calendar.MONTH))));
        getSupportActionBar().setCustomView(mMenuMonthText, params);
        mMenuMonthText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsShowCalendar = !mIsShowCalendar;
                updateMenuChevronIcon(true);
                switchCalendarVisibility(true);
            }
        });
        updateMenuChevronIcon(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD_NEW_SCHEDULE) {
            if (resultCode == Activity.RESULT_OK) {
                mCalendarPager.requestUpdate();
                if (data != null) {
                    final int resultReason = data.getIntExtra(AddScheduleActivity.EXTRA_RESULT_REASON, -1);
                    if (resultReason != R.string.calendar_activity_remove_schedule_success) {
                        long scheduleStartTime = data.getLongExtra(AddScheduleActivity.EXTRA_START_TIME, -1);
                        long scheduleFinishTime = data.getLongExtra(AddScheduleActivity.EXTRA_FINISH_TIME, -1);
                        boolean isNewScheduleInAgendaView = Utilities.isTimeOverlapping(scheduleStartTime, scheduleFinishTime, mAgendaView.getStartTime(), mAgendaView.getFinishTime());
                        if (DEBUG) {
                            Log.d(TAG, "isNewScheduleInAgendaView: " + isNewScheduleInAgendaView
                                    + ", scheduleStartTime:" + scheduleStartTime + ", scheduleFinishTime:" + scheduleFinishTime
                                    + "\nmAgendaView.getStartTime(): " + mAgendaView.getStartTime() + ", mAgendaView.getFinishTime(): " + mAgendaView.getFinishTime());
                        }
                        if (isNewScheduleInAgendaView) {
                            ((AgendaAdapter) mAgendaView.getAdapter()).reloadData();
                        }
                    } else {
                        long scheduleStartTime = data.getLongExtra(AddScheduleActivity.EXTRA_START_TIME, -1);
                        long scheduleFinishTime = data.getLongExtra(AddScheduleActivity.EXTRA_FINISH_TIME, -1);
                        boolean isRemovedScheduleInAgendaView = Utilities.isTimeOverlapping(scheduleStartTime, scheduleFinishTime, mAgendaView.getStartTime(), mAgendaView.getFinishTime());
                        if (isRemovedScheduleInAgendaView) {
                            ((AgendaAdapter) mAgendaView.getAdapter()).reloadData();
                        }
                    }
                    if (resultReason != -1) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(mFab, resultReason, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            }
                        }, SNAKE_BAR_DELAY_TIME);
                    }
                } else {
                    ((AgendaAdapter) mAgendaView.getAdapter()).reloadData();
                }
            }
        }
    }

    private void updateMenuChevronIcon(final boolean runAnimation) {
        if (mIsShowCalendar) {
            // will show icon ^
            if (IS_SUPPORT_MATERIAL_DESIGN) {
                if (runAnimation) {
                    mMenuMonthText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_down_white_a_vec, 0);
                    ((Animatable) mMenuMonthText.getCompoundDrawables()[2]).start();
                } else {
                    mMenuMonthText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_up_white_a_vec, 0);
                }
            } else {
                mMenuMonthText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_up_white_24dp, 0);
            }
        } else {
            // will show icon v
            if (IS_SUPPORT_MATERIAL_DESIGN) {
                if (runAnimation) {
                    mMenuMonthText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_up_white_a_vec, 0);
                    ((Animatable) mMenuMonthText.getCompoundDrawables()[2]).start();
                } else {
                    mMenuMonthText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_down_white_a_vec, 0);
                }
            } else {
                mMenuMonthText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_down_white_24dp, 0);
            }
        }
    }

    private void switchCalendarVisibility(boolean animate) {
        if (animate) {
            if (mMenuButtonAnimator != null && mMenuButtonAnimator.isRunning()) {
                mMenuButtonAnimator.cancel();
            }
            if (mIsShowCalendar) {
                mMenuButtonAnimator = new ValueAnimator().ofFloat(1f, 0f);
                mMenuButtonAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Float value = (Float) animation.getAnimatedValue();
                        mCalendarPager.setTranslationY(-mCalendarPager.getHeight() * value);
                    }
                });
                mMenuButtonAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (mCalendarPager.getVisibility() != View.VISIBLE) {
                            mCalendarPager.setVisibility(View.VISIBLE);
                        }
                        RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) mAgendaView.getLayoutParams();
                        param.addRule(RelativeLayout.BELOW, mCalendarPager.getId());
                        ((RelativeLayout) mAgendaView.getParent()).updateViewLayout(mAgendaView, param);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                mMenuButtonAnimator.setDuration(CALENDAR_VIEW_VISIBILITY_CHANGE_DURATION);
                mMenuButtonAnimator.start();
            } else {
                mMenuButtonAnimator = new ValueAnimator().ofFloat(0f, 1f);
                mMenuButtonAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Float value = (Float) animation.getAnimatedValue();
                        mCalendarPager.setTranslationY(-mCalendarPager.getHeight() * value);
                    }
                });
                mMenuButtonAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) mAgendaView.getLayoutParams();
                        param.addRule(RelativeLayout.BELOW, 0);
                        ((RelativeLayout) mAgendaView.getParent()).updateViewLayout(mAgendaView, param);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                mMenuButtonAnimator.setDuration(CALENDAR_VIEW_VISIBILITY_CHANGE_DURATION);
                mMenuButtonAnimator.start();
            }
        } else {
            if (mIsShowCalendar) {
                mCalendarPager.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) mAgendaView.getLayoutParams();
                param.addRule(RelativeLayout.BELOW, mCalendarPager.getId());
                ((RelativeLayout) mAgendaView.getParent()).updateViewLayout(mAgendaView, param);
            } else {
                mCalendarPager.setVisibility(View.INVISIBLE);
                RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) mAgendaView.getLayoutParams();
                param.addRule(RelativeLayout.BELOW, 0);
                ((RelativeLayout) mAgendaView.getParent()).updateViewLayout(mAgendaView, param);
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.calendar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(CalendarActivity.this, MsChallengePreference.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDaySelected(Date date) {
        mSelectedDateTime = date;
        mCalendarPager.setSelectedDate(date);
        mAgendaView.setDate(mSelectedDateTime);
    }

    @Override
    public Date getSelectedDate() {
        return mSelectedDateTime;
    }

    private void startAddScheduleActivityForResult(int y, int m, int d) {
        Intent startIntent = new Intent(CalendarActivity.this, AddScheduleActivity.class);
        startIntent.putExtra(AddScheduleActivity.EXTRA_YEAR, y);
        startIntent.putExtra(AddScheduleActivity.EXTRA_MONTH, m);
        startIntent.putExtra(AddScheduleActivity.EXTRA_DAY, d);
        startActivityForResult(startIntent, REQUEST_ADD_NEW_SCHEDULE);
    }

    private void startAddScheduleActivityForResult(Schedule schedule) {
        Intent startIntent = new Intent(CalendarActivity.this, AddScheduleActivity.class);
        startIntent.putExtra(AddScheduleActivity.EXTRA_SCHEDULE, schedule.toJson().toString());
        startActivityForResult(startIntent, REQUEST_ADD_NEW_SCHEDULE);
    }

    @Override
    public void onScheduleDialogPositiveClick(Schedule schedule) {
        if (DEBUG) {
            Log.d(TAG, "onScheduleDialogPositiveClick schedule: " + schedule);
        }
        startAddScheduleActivityForResult(schedule);
    }

    @Override
    public void onScheduleDialogNegativeClick() {
        // do noting when negative button click
    }
}
