package bj4.yhh.mschallenge.settings;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import bj4.yhh.mschallenge.R;

/**
 * Created by User on 2016/6/10.
 */
public class AboutPreferenceFragment extends PreferenceFragment {

    private static final String LINKED_IN_PAGE = "linkedin_page";
    private static final String GITHUB = "github";
    private static final String FACEBOOK = "facebook";
    private static final String EMAIL = "email";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_about);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        final String key = preference.getKey();
        if (LINKED_IN_PAGE.equals(key)) {
            try {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.linkedin.com/in/s011208"));
                startActivity(i);
                return true;
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getActivity(), R.string.preference_about_me_failed_to_start_browser, Toast.LENGTH_LONG).show();
            }
        } else if (GITHUB.equals(key)) {
            try {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://github.com/s011208/"));
                startActivity(i);
                return true;
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getActivity(), R.string.preference_about_me_failed_to_start_browser, Toast.LENGTH_LONG).show();
            }
        } else if (FACEBOOK.equals(key)) {
            try {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.facebook.com/s011208"));
                startActivity(i);
                return true;
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getActivity(), R.string.preference_about_me_failed_to_start_browser, Toast.LENGTH_LONG).show();
            }
        } else if (EMAIL.equals(key)) {
            try {
                String emailAddress = getActivity().getResources().getString(R.string.preference_about_me_email_summary);
                String emailSubject = getActivity().getResources().getString(R.string.preference_about_me_email_subject);
                String emailMessage = getActivity().getResources().getString(R.string.preference_about_me_email_text);
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {emailAddress});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(Intent.EXTRA_TEXT, emailMessage);
                startActivity(emailIntent);
                return true;
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getActivity(), R.string.preference_about_me_failed_to_start_browser, Toast.LENGTH_LONG).show();
            }
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
