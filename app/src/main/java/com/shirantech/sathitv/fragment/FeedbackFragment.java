package com.shirantech.sathitv.fragment;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;
import com.shirantech.sathitv.BuildConfig;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.activity.MainActivity;
import com.shirantech.sathitv.helper.CrashlyticsHelper;
import com.shirantech.sathitv.helper.PreferencesHelper;
import com.shirantech.sathitv.logger.Log;
import com.shirantech.sathitv.model.postparams.FeedbackParams;
import com.shirantech.sathitv.model.response.GeneralResponse;
import com.shirantech.sathitv.network.ApiConstants;
import com.shirantech.sathitv.utils.AppLog;
import com.shirantech.sathitv.utils.AppUtil;
import com.shirantech.sathitv.utils.ServerResponse;
import com.shirantech.sathitv.widget.CustomProgressView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedbackFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedbackFragment extends Fragment implements View.OnClickListener {
    private static final String URL_POST_FEEDBACK = ApiConstants.APP_USER_URL + "feedback";
    private static final String TAG = "Feedback";
    private LinearLayout mMainLayout;
    private EditText mNameEditText, mSubjectEditText, mMessageEditText;
    private Button buttonPost;
    private CustomProgressView mProgressView;


    /**
     * Callback for Janam kundali request
     */
    private FutureCallback<GeneralResponse> mRequestCallback = new FutureCallback<GeneralResponse>() {
        @Override
        public void onCompleted(Exception exception, GeneralResponse serverResponse) {
            AppLog.showLog("server response " + serverResponse.getStatus() + " this is the response");
            if (null == exception) {

                if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_SUCCESS, serverResponse.getStatus())) {
                    showSuccessMessage(serverResponse.getMessage());
                    AppLog.showLog(serverResponse + "");
                } else if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_FAILURE, serverResponse.getStatus())) {
                    Log.d(TAG, serverResponse.getMessage());
                    showFailureMessage(serverResponse.getMessage());

                    // TODO : track janam kundali request failure using Google Analytics(or Crashlytics)
                } else {
                    showFailureMessage(R.string.error_problem_occurred);
                    // TODO : track connection/server error using Google Analytics(or Crashlytics)
                }
            } else {
                Log.e(TAG, exception.getLocalizedMessage(), exception);
                showFailureMessage(R.string.error_problem_occurred);
                CrashlyticsHelper.sendCaughtException(exception);
            }
        }

        private void showSuccessMessage(String message) {
            mProgressView.setProgressMessage(R.string.message_submitted);
            Snackbar.make(mMainLayout, message, Snackbar.LENGTH_LONG)
                    .setCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            super.onDismissed(snackbar, event);
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, HomeScreenFragment.newInstance())
                                    .commit();
                            ((MainActivity) getActivity()).mCurrentSelectedPosition = 0;
                        }
                    })
                    .show();
        }

        private void showFailureMessage(final String message) {
            showProgress(false);
            if (ServerResponse.isTokenInvalid(message)) {
                AppUtil.showInvalidTokenSnackBar(mMainLayout, message, getActivity());
            } else {
                Snackbar.make(mMainLayout, message, Snackbar.LENGTH_SHORT).show();
            }

        }

        private void showFailureMessage(int messageId) {
            showFailureMessage(getString(messageId));
        }
    };

    private TextWatcher mInputWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // no-op
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(mNameEditText.getText().toString().trim()) &&
                    !TextUtils.isEmpty(mSubjectEditText.getText().toString().trim()) &&
                    !TextUtils.isEmpty(mMessageEditText.getText().toString().trim())
                    ) {
//                showSubmitMenu(true);
            } else {
//                showSubmitMenu(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            // no-op
        }
    };

    public FeedbackFragment() {
        // Required empty public constructor
    }


    public static FeedbackFragment newInstance() {
        FeedbackFragment fragment = new FeedbackFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_feedback, container, false);
        AppLog.showLog(TAG);
        mProgressView = (CustomProgressView) view.findViewById(R.id.progress_view);
        mNameEditText = (EditText) view.findViewById(R.id.name_editText);
        mSubjectEditText = (EditText) view.findViewById(R.id.subject_editText);
        mMessageEditText = (EditText) view.findViewById(R.id.message_editText);
        mNameEditText.setText(PreferencesHelper.getUsername(getActivity()));
        mMainLayout = (LinearLayout) view.findViewById(R.id.main_layout);
        mSubjectEditText.requestFocus();
        buttonPost = (Button) view.findViewById(R.id.buttonFeedbackSubmit);
        buttonPost.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        getActivity().getMenuInflater().inflate(R.menu.menu_rashifal_request, menu);
        menu.findItem(R.id.action_submit_request);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_submit_request) {
            validateFields();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
*/

    private void validateFields() {
        AppUtil.hideSoftKeyboard(getActivity());

        // reset previously set errors
        resetError();

        // Store values at the time of the submit attempt.
        final String name = mNameEditText.getText().toString().trim();
        final String subject = mSubjectEditText.getText().toString().trim();
        final String message = mMessageEditText.getText().toString().trim();

        boolean valid = true;
        View focusView = null;

        // check for location
        if (TextUtils.isEmpty(subject)) {
            showError(mSubjectEditText, R.string.error_subject_required);
            focusView = mSubjectEditText;
            valid = false;
        }

        // check for name
        if (TextUtils.isEmpty(name)) {
            showError(mNameEditText, R.string.error_name_required);
            focusView = mNameEditText;
            valid = false;
        }

        // check for symptoms
        if (TextUtils.isEmpty(message)) {
            showError(mMessageEditText, R.string.error_message_required);
            focusView = mMessageEditText;
            valid = false;
        }

        if (valid) {
            sendFeedbackPost();
        } else {
            // There was an error; don't attempt submit and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
    }



    /**
     * reset the error
     */
    private void resetError() {
        mNameEditText.setError(null);
        mMessageEditText.setError(null);
        mSubjectEditText.setError(null);
    }


    /**
     * Show error in the provided TextInputLayout
     *
     * @param editText   TextInputLayout to show error in
     * @param errorMessageId id of error message to show
     */
    private void showError(EditText editText, int errorMessageId) {
        editText.setError(getString(errorMessageId));
    }

    /**
     * Send feedback to the server
     */
    private void sendFeedbackPost() {
        if (AppUtil.isInternetConnected(getActivity())) {
            showProgress(true);

            final String loginToken = PreferencesHelper.getLoginToken(getActivity());
            final String message = mMessageEditText.getText().toString().trim();
            final String subject = mSubjectEditText.getText().toString().trim();

            final Builders.Any.B builder = Ion.with(this).load(URL_POST_FEEDBACK);
            if (BuildConfig.DEBUG) {
                builder.setLogging(TAG, Log.VERBOSE);
            }


            builder.setHeader(ApiConstants.HEADER_KEY_ACCEPT, ApiConstants.HEADER_ACCEPT_VALUE)
                    .setJsonPojoBody(new FeedbackParams(subject, message, loginToken))
                    .as(GeneralResponse.class)
                    .setCallback(mRequestCallback);
        } else {
            Snackbar.make(mMainLayout, R.string.error_network_connection, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Shows the progress while request submission.
     *
     * @param showProgress <code>true</code> to show the progress <code>false</code> otherwise.
     */
    public void showProgress(final boolean showProgress) {
        // hide submit menu when progress is visible
//        showSubmitMenu(!showProgress);
        mProgressView.setVisibility(showProgress ? View.VISIBLE : View.GONE);
        mProgressView.setProgressMessage(R.string.message_submitting);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonFeedbackSubmit:
                validateFields();
                break;
            default:
        }
    }
}
