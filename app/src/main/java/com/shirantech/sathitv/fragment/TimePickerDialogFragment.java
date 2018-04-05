package com.shirantech.sathitv.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import com.shirantech.sathitv.R;
import com.shirantech.sathitv.widget.TimePicker;

import java.util.Calendar;

/**
 * Fragment for showing time picker dialog
 */
public class TimePickerDialogFragment extends DialogFragment {

    private OnTimeSetListener timeSetListener;
    private Calendar currentTime;

    /**
     * The callback interface used to indicate the user is done filling in
     * the time (they clicked on the 'Done' button).
     */
    public interface OnTimeSetListener {

        /**
         * @param view      The view associated with this listener.
         * @param hourOfDay The hour that was set.
         * @param minute    The minute that was set.
         */
        void onTimeSet(TimePicker view, int hourOfDay, int minute, int seconds);
    }

    /**
     * Initialize and show the time picker dialog
     *
     * @param fragmentManager FragmentManager to show DialogFragment
     * @param timeSetListener listener to get the changed time
     * @param calendar        calendar instance to initialize the TimePicker
     */
    public static void showTimePicker(FragmentManager fragmentManager,
                                      OnTimeSetListener timeSetListener, Calendar calendar) {
        TimePickerDialogFragment timePickerDialogFragment = new TimePickerDialogFragment();
        timePickerDialogFragment.setTimeSetListener(timeSetListener);
        timePickerDialogFragment.setCurrentTime(calendar);
        timePickerDialogFragment.show(fragmentManager, null);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final TimePicker timePicker = new TimePicker(getActivity());
        timePicker.setIs24HourView(false);
        timePicker.setCurrentHour(currentTime.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(currentTime.get(Calendar.MINUTE));
        timePicker.setCurrentSecond(currentTime.get(Calendar.SECOND));

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.time_picker_title)
                .setView(timePicker)
                .setPositiveButton(R.string.action_done, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        timeSetListener.onTimeSet(timePicker, timePicker.getCurrentHour(),
                                timePicker.getCurrentMinute(), timePicker.getCurrentSeconds());
                    }
                })
                .create();
    }

    private void setTimeSetListener(OnTimeSetListener timeSetListener) {
        this.timeSetListener = timeSetListener;
    }

    public void setCurrentTime(Calendar currentTime) {
        this.currentTime = currentTime;
    }
}
