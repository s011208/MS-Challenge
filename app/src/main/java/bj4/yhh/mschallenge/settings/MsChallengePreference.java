package bj4.yhh.mschallenge.settings;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import bj4.yhh.mschallenge.R;

/**
 * Created by User on 2016/6/10.
 */
public class MsChallengePreference extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference_activity);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        AboutPreferenceFragment prefFragment = new AboutPreferenceFragment();
        transaction.add(R.id.fragment, prefFragment);
        transaction.commit();
    }
}
