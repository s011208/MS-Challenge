package bj4.yhh.mschallenge.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

import bj4.yhh.mschallenge.R;
import bj4.yhh.mschallenge.Utilities;
import bj4.yhh.mschallenge.provider.Schedule;

/**
 * Created by yenhsunhuang on 2016/6/8.
 */
public class ViewScheduleDialog extends DialogFragment {
    public static final String ARGUMENT_SCHEDULE = "argument_schedule";
    public static final String ARGUMENT_SECTION_TIME = "argument_section_time";

    public interface Callback {
        void onScheduleDialogPositiveClick(Schedule schedule);

        void onScheduleDialogNegativeClick();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments == null) {
            throw new RuntimeException("argument is null");
        }
        final Schedule schedule;
        try {
            schedule = new Schedule(new JSONObject(arguments.getString(ARGUMENT_SCHEDULE)));
        } catch (JSONException e) {
            throw new RuntimeException("failed to init schedule", e);
        }
        View customView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_schedule_dialog, null);
        TextView title = (TextView) customView.findViewById(R.id.schedule_title);
        title.setText(schedule.getTitle());

        TextView notify = (TextView) customView.findViewById(R.id.schedule_notify);
        String[] notifySelections = getActivity().getResources().getStringArray(R.array.schedule_notify_time_list);
        notify.setText(notifySelections[schedule.getNotify()]);

        final TextView description = (TextView) customView.findViewById(R.id.schedule_description);
        final int maxLinesOfDescription = getActivity().getResources().getInteger(R.integer.view_schedule_dialog_title_max_line);
        final String readMoreString = getActivity().getResources().getString(R.string.view_schedule_dialog_read_more);
        if (TextUtils.isEmpty(schedule.getDescription())) {
            description.setVisibility(View.GONE);
        } else {
            description.setText(schedule.getDescription());
            description.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (description.getViewTreeObserver().isAlive()) {
                        description.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    if (description.getLineCount() > maxLinesOfDescription) {
                        final int lineEndIndex = description.getLayout().getLineEnd(maxLinesOfDescription - 1);
                        String ellipsizeDescription = description.getText().subSequence(0, lineEndIndex - readMoreString.length() + 1) + " " + readMoreString;
                        description.setText(ellipsizeDescription);
                    } else {
                        description.setClickable(false);
                    }
                }
            });
            description.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getActivity()).setTitle(R.string.schedule_description)
                            .setMessage(schedule.getDescription()).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create().show();
                }
            });
        }

        TextView location = (TextView) customView.findViewById(R.id.schedule_location);
        if (TextUtils.isEmpty(schedule.getLocation())) {
            location.setVisibility(View.GONE);
        } else {
            location.setText(schedule.getLocation());
        }

        TextView scheduleTime = (TextView) customView.findViewById(R.id.schedule_time);
        long sectionTime = arguments.getLong(ARGUMENT_SECTION_TIME, -1);
        String dateString;
        if (schedule.getStartTime() <= sectionTime) {
            dateString = new SimpleDateFormat("yyyy.MM.dd EEE", getActivity().getResources().getConfiguration().locale).format(sectionTime);
        } else {
            dateString = new SimpleDateFormat("yyyy.MM.dd EEE", getActivity().getResources().getConfiguration().locale).format(schedule.getStartTime());
        }
        String timeString;
        if (schedule.getIsWholeDay()) {
            timeString = getActivity().getString(R.string.schedule_activity_whole_day);
        } else {
            if (schedule.getStartTime() <= sectionTime && schedule.getFinishTime() >= sectionTime + Utilities.DAY) {
                timeString = getActivity().getString(R.string.schedule_activity_whole_day);
            } else if (schedule.getStartTime() <= sectionTime) {
                timeString = "00:00 ▻ " + new SimpleDateFormat("HH:mm", getActivity().getResources().getConfiguration().locale).format(schedule.getFinishTime());
            } else if (schedule.getFinishTime() >= sectionTime + Utilities.DAY) {
                timeString = new SimpleDateFormat("HH:mm", getActivity().getResources().getConfiguration().locale).format(schedule.getStartTime()) + " ▻ 24:00";
            } else {
                timeString = new SimpleDateFormat("HH:mm", getActivity().getResources().getConfiguration().locale).format(schedule.getStartTime()) + " ▻ " + new SimpleDateFormat("HH:mm", getActivity().getResources().getConfiguration().locale).format(schedule.getFinishTime());
            }
        }
        scheduleTime.setText(dateString + "\n" + timeString);

        return new AlertDialog.Builder(getActivity()).setView(customView)
                .setPositiveButton(R.string.view_schedule_dialog_edit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (getActivity() instanceof Callback) {
                            ((Callback) getActivity()).onScheduleDialogPositiveClick(schedule);
                        }
                        if (getTargetFragment() != null) {
                            Intent intent = getActivity().getIntent();
                            intent.putExtra(ARGUMENT_SCHEDULE, schedule.toJson().toString());
                            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                        }
                    }
                }).setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (getActivity() instanceof Callback) {
                            ((Callback) getActivity()).onScheduleDialogNegativeClick();
                        }
                        if (getTargetFragment() != null) {
                            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, getActivity().getIntent());
                        }
                    }
                }).create();
    }
}
