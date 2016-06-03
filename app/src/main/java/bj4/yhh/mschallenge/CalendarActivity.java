package bj4.yhh.mschallenge;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import bj4.yhh.mschallenge.calendar.CalendarPager;

public class CalendarActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "QQQQ";
    private static final boolean DEBUG = Utilities.DEBUG;
    private static final boolean IS_SUPPORT_MATERIAL_DESIGN = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

    private TextView mMenuMonthText;
    private boolean mIsShowCalendar = false;

    private CalendarPager mCalendarPager;
    private final Calendar mCalendar = Calendar.getInstance();
    private final List<String> mMonthString = Utilities.getMonthString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        initComponents();
    }

    private void initComponents() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initCustomActionBar();
        mCalendarPager = (CalendarPager) findViewById(R.id.calendar_pager);
        mCalendarPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int[] value = mCalendarPager.getCurrentMonthAndYear();
                String text = mMonthString.get(value[1]);
                if (value[0] != mCalendar.get(Calendar.YEAR)) {
                    text += " " + value[0];
                }
                mMenuMonthText.setText(text);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initCustomActionBar() {
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
        mMenuMonthText.setText(mMonthString.get(mCalendar.get(Calendar.MONTH)));
        getSupportActionBar().setCustomView(mMenuMonthText, params);
        mMenuMonthText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG) {
                    Log.d(TAG, "mMenuMonthText onClick");
                }
                mIsShowCalendar = !mIsShowCalendar;
                updateMenuChevronIcon(true);
                switchCalendarVisibility();
            }
        });
        updateMenuChevronIcon(false);
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

    private void switchCalendarVisibility() {

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
}
