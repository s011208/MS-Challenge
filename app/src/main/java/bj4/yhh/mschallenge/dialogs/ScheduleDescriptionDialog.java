package bj4.yhh.mschallenge.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import bj4.yhh.mschallenge.R;

/**
 * Created by yenhsunhuang on 2016/6/5.
 */
public class ScheduleDescriptionDialog extends DialogFragment {

    private String mDescription;

    public interface Callback {
        void onDescriptionUpdate(String description);
    }

    public static final String EXTRA_DESCRIPTION = "extra_description";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mDescription = getArguments().getString(EXTRA_DESCRIPTION);
        }
        LayoutInflater inflater = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        View customTitle = inflater.inflate(R.layout.schedule_description_dialog_title, null);
        final ImageView ok = (ImageView) customTitle.findViewById(R.id.check);
        View customDescription = inflater.inflate(R.layout.schedule_description_dialog_content, null);
        final EditText customContent = (EditText) customDescription.findViewById(R.id.description);
        customContent.setText(mDescription);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                if (activity != null && activity instanceof Callback) {
                    ((Callback) activity).onDescriptionUpdate(customContent.getText().toString());
                    dismiss();
                }
            }
        });


        return new AlertDialog.Builder(getActivity())
                .setCustomTitle(customTitle)
                .setView(customDescription).create();
    }
}
