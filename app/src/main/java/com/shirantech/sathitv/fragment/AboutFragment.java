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
import android.widget.TextView;

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
import com.shirantech.sathitv.model.response.AboutResponse;
import com.shirantech.sathitv.model.response.GeneralResponse;
import com.shirantech.sathitv.network.ApiConstants;
import com.shirantech.sathitv.utils.AppLog;
import com.shirantech.sathitv.utils.AppUtil;
import com.shirantech.sathitv.utils.ServerResponse;
import com.shirantech.sathitv.widget.CustomProgressView;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AboutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutFragment extends Fragment {
    private static final String URL_REQUEST_ABOUT = ApiConstants.APP_USER_URL + "getAboutContent";
    private static final String TAG = AboutFragment.class.getName();
    private CustomProgressView mProgressView;
    private TextView textViewAbout;


    /**
     * Callback for about request
     */
    private FutureCallback<AboutResponse> aboutCallback = new FutureCallback<AboutResponse>() {
        @Override
        public void onCompleted(Exception exception, AboutResponse serverResponse) {
            AppLog.showLog("server response " + serverResponse.getStatus() + " this is the response");
            if (null == exception) {
                if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_SUCCESS, serverResponse.getStatus())) {
                    showProgress(false);
                    String about = serverResponse.getAbout();
                    if (!TextUtils.isEmpty(about)) {
                        textViewAbout.setVisibility(View.VISIBLE);
                        textViewAbout.setText(about);
                    } else {
                        showFailureMessage(serverResponse.getMessage());
                    }
                    AppLog.showLog(TAG, "about :: " + serverResponse.getAbout());
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

        private void showFailureMessage(final String message) {
            showProgress(false);
            if (ServerResponse.isTokenInvalid(message)) {
                AppUtil.showInvalidTokenSnackBar(textViewAbout, message, getActivity());
            } else {
                Snackbar.make(textViewAbout, message, Snackbar.LENGTH_SHORT).show();
            }

        }

        private void showFailureMessage(int messageId) {
            showFailureMessage(getString(messageId));
        }
    };


    public AboutFragment() {
        // Required empty public constructor
    }


    public static AboutFragment newInstance() {
        AboutFragment fragment = new AboutFragment();
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
        final View view = inflater.inflate(R.layout.fragment_about, container, false);
        AppLog.showLog(TAG);
        mProgressView = (CustomProgressView) view.findViewById(R.id.progress_view);
        textViewAbout = (TextView) view.findViewById(R.id.textViewAbout);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getAboutDataFromServer();

    }


    /**
     * get about data from server
     */
    private void getAboutDataFromServer() {
        if (AppUtil.isInternetConnected(getActivity())) {
            showProgress(true);

            final Builders.Any.B builder = Ion.with(this).load(URL_REQUEST_ABOUT);
            if (BuildConfig.DEBUG) {
                builder.setLogging(TAG, Log.VERBOSE);
            }
            builder.setHeader(ApiConstants.HEADER_KEY_ACCEPT, ApiConstants.HEADER_ACCEPT_VALUE)
                    .as(AboutResponse.class)
                    .setCallback(aboutCallback);
        } else {
            Snackbar.make(textViewAbout, R.string.error_network_connection, Snackbar.LENGTH_LONG).show();
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
        mProgressView.setProgressMessage(R.string.message_loading);
    }


}
