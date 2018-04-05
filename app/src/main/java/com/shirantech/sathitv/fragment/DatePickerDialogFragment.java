package com.shirantech.sathitv.fragment;

import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.widget.DatePicker;

import com.shirantech.sathitv.R;

import java.util.Calendar;

/**
 * Fragment for showing date picker dialog
 */
public class DatePickerDialogFragment extends DialogFragment {

    private OnDateSetListener dateSetListener;
    private Calendar currentDate;

    /**
     * Initialize and show the date picker dialog
     *
     * @param fragmentManager FragmentManager to show DialogFragment
     * @param dateSetListener listener to get the changed date
     * @param calendar        calendar instance to initialize the DatePicker
     */
    public static void showDatePicker(FragmentManager fragmentManager,
                                      OnDateSetListener dateSetListener, Calendar calendar) {
        DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
        datePickerDialogFragment.setDateSetListener(dateSetListener);
        datePickerDialogFragment.setCurrentDate(calendar);
        datePickerDialogFragment.show(fragmentManager, null);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final DatePicker datePicker = new DatePicker(getActivity());
        datePicker.setCalendarViewShown(false);
        datePicker.updateDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH));

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.date_picker_title)
                .setView(datePicker)
                .setPositiveButton(R.string.action_done, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dateSetListener.onDateSet(datePicker, datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                    }
                })
                .create();
    }

    private void setDateSetListener(OnDateSetListener dateSetListener) {
        this.dateSetListener = dateSetListener;
    }

    public void setCurrentDate(Calendar currentDate) {
        this.currentDate = currentDate;
    }
}
