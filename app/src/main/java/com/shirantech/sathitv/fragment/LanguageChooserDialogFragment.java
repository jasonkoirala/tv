package com.shirantech.sathitv.fragment;


import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.shirantech.sathitv.R;
import com.shirantech.sathitv.model.Language;

/**
 * A simple {@link Fragment} subclass.
 */
public class LanguageChooserDialogFragment extends DialogFragment {

    /**
     * Use Nepali as default language if user cancels/dismisses the dialog.
     */
    private Language selectedLanguage = Language.NEPALI;

    /**
     * Interface definition for a callback to be pass the language selected by user.
     */
    public interface OnLanguageConfirmedListener {

        /**
         * Called to pass the selected language from the dialog.
         *
         * @param selectedLanguage the selected language
         */
        void OnLanguageConfirmed(Language selectedLanguage);
    }

    private OnLanguageConfirmedListener languageConfirmedListener;

    /**
     * Use this factory method to create a new instance of this fragment
     *
     * @return A new instance of fragment {@link LanguageChooserDialogFragment}.
     */
    public static LanguageChooserDialogFragment newInstance() {
        return new LanguageChooserDialogFragment();
    }

    public LanguageChooserDialogFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.language_select_title)
                .setMessage(R.string.language_select_message)
                .setPositiveButton(R.string.language_select_nepali,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                selectedLanguage = Language.NEPALI;
                            }
                        }
                )
                .setNegativeButton(R.string.language_select_english,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                selectedLanguage = Language.ENGLISH;
                            }
                        }
                )
                .create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        languageConfirmedListener.OnLanguageConfirmed(selectedLanguage);
    }

    /**
     * Set the listener to know about the completion of selection of language from dialog
     *
     * @param languageConfirmedListener listener for sending back selected language
     */
    public void setLanguageConfirmedListener(OnLanguageConfirmedListener languageConfirmedListener) {
        this.languageConfirmedListener = languageConfirmedListener;
    }
}
