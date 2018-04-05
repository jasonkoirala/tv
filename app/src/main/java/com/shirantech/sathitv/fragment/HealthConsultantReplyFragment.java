package com.shirantech.sathitv.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;
import com.shirantech.sathitv.BuildConfig;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.adapter.HealthConsultantReplyListAdapter;
import com.shirantech.sathitv.helper.CrashlyticsHelper;
import com.shirantech.sathitv.helper.PreferencesHelper;
import com.shirantech.sathitv.logger.Log;
import com.shirantech.sathitv.model.HealthConsultant;
import com.shirantech.sathitv.model.response.HealtConsultantReplyResponse;
import com.shirantech.sathitv.network.ApiConstants;
import com.shirantech.sathitv.utils.AppLog;
import com.shirantech.sathitv.utils.AppUtil;
import com.shirantech.sathitv.utils.ServerResponse;
import com.shirantech.sathitv.widget.CustomProgressView;
import com.shirantech.sathitv.widget.RecyclerViewItemDecoration;

import java.util.List;

/**
 * A simple {@link Fragment} subclass showing list of HealthConsultant replies.
 * Use the {@link HealthConsultantReplyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HealthConsultantReplyFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "HealthConsultant";
    private static final String URL_REQUEST_CONSULTANT = ApiConstants.CHANNEL_URL + "getConsultantqueries";
    private RecyclerView mReplyListView;
    private CustomProgressView mProgressView;
    private List<HealthConsultant> mHealthConsultantList;

    /**
     * Callback for Health Consultant reply
     */
    private FutureCallback<HealtConsultantReplyResponse> mReplyCallback = new FutureCallback<HealtConsultantReplyResponse>() {
        @Override
        public void onCompleted(Exception exception, HealtConsultantReplyResponse serverResponse) {
            if (null == exception) {

                if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_SUCCESS, serverResponse.getStatus())) {
                    mHealthConsultantList = serverResponse.getHealthConsultantList();
                    AppLog.showLog(TAG, " mHealthConsultantList "+mHealthConsultantList.size());
                    populateData();
                } else if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_FAILURE, serverResponse.getStatus())) {
                    Log.d(TAG, serverResponse.getMessage());
                    String message = serverResponse.getMessage();
                    AppLog.showLog(TAG, " error "+message);
                    showMessageToUser(message);

                    // TODO : track failure in getting photo using Google Analytics(or Crashlytics)
                } else {
                    showMessageToUser(R.string.error_problem_occurred);
                    // TODO : track connection/server error using Google Analytics(or Crashlytics)
                }
            } else {
                Log.e(TAG, exception.getLocalizedMessage(), exception);
                showMessageToUser(R.string.error_problem_occurred);
                CrashlyticsHelper.sendCaughtException(exception);
            }
        }
    };

    /**
     * Use this factory method to create a new instance of this fragment
     *
     * @return A new instance of fragment {@link HomeScreenFragment}.
     */
    public static HealthConsultantReplyFragment newInstance() {
        return new HealthConsultantReplyFragment();
    }

    public HealthConsultantReplyFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_janam_kundali_reply, container, false);
        mReplyListView = (RecyclerView) view.findViewById(R.id.reply_listView);
        mProgressView = (CustomProgressView) view.findViewById(R.id.progress_view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showProgress(true);
        getHealthConsultantReply();

    }

    /**
     * Get the reply(s) for the Health Consultant request(if ready)
     */
    private void getHealthConsultantReply() {
        if (AppUtil.isInternetConnected(getActivity())) {
            showProgress(true);

            final String loginToken = PreferencesHelper.getLoginToken(getActivity());

            final Builders.Any.B builder = Ion.with(this).load(URL_REQUEST_CONSULTANT);
            if (BuildConfig.DEBUG) {
                builder.setLogging(TAG, Log.VERBOSE);
            }
            AppLog.showLog(TextUtils.isEmpty(loginToken) ? "login token is empty" : loginToken + " log in token");
            builder.setHeader(ApiConstants.HEADER_KEY_ACCEPT, ApiConstants.HEADER_ACCEPT_VALUE)
                    .setBodyParameter(ApiConstants.PARAM_TOKEN, loginToken)
                    .as(HealtConsultantReplyResponse.class)
                    .setCallback(mReplyCallback);
        } else {
            showMessageToUser(R.string.error_network_connection);
        }
    }

    /**
     * Shows the progress while getting reply.
     *
     * @param showProgress <code>true</code> to show the progress <code>false</code> otherwise.
     */
    public void showProgress(final boolean showProgress) {
        mProgressView.setVisibility(showProgress ? View.VISIBLE : View.GONE);
        mProgressView.setProgressMessage(R.string.message_getting_reply);
    }

    /**
     * If photos are obtained from the server populate the list with provided photo list.
     */
    private void populateData() {
        if ((null != mHealthConsultantList && !mHealthConsultantList.isEmpty())) {
            showProgress(false);
            setupReplyList();
        } else {
            showMessageToUser(R.string.message_no_request_yet_health_consultant);
        }
    }

    /**
     * Show the message and hide the ProgressBar
     *
     * @param messageId Resource id of message to show.
     */
    private void showMessageToUser(int messageId) {
        showMessageToUser(getString(messageId));
    }

    /**
     * Show the message and hide the ProgressBar
     *
     * @param message message to show
     */
    private void showMessageToUser(String message) {
        mProgressView.setProgressMessage(message);
        mProgressView.hideProgressBar();
        if (ServerResponse.isTokenInvalid(message)) {
            AppUtil.showInvalidTokenSnackBar(mReplyListView, message, getActivity());
        } else {
            Snackbar.make(mReplyListView, message, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void setupReplyList() {
        final int itemSpacing = (int) getResources().getDimension(R.dimen.spacing_normal);
        mReplyListView.setHasFixedSize(true);
        mReplyListView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mReplyListView.addItemDecoration(new RecyclerViewItemDecoration(itemSpacing));
        mReplyListView.setAdapter(new HealthConsultantReplyListAdapter(mHealthConsultantList, this,getActivity()));
    }

    @Override
    public void onClick(View clickedView) {
        /*if (R.id.main_layout == clickedView.getId()) {
            final int position = (int) clickedView.getTag();
            final boolean videoAvailable = mHealthConsultantList.get(position).getStatus();
            if (videoAvailable) {
                startActivity(FullscreenPlayerActivity.getStartIntent(
                        getActivity(), mHealthConsultantList.get(position).getVideoUrl()));
            } else {
                showVideoUnavailableDialog();
            }
        }*/
    }

    private void showVideoUnavailableDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.video_not_ready_alert_title)
                .setMessage(R.string.video_not_ready_alert_message)
                .setPositiveButton(R.string.prompt_ok, null)
                .show();
    }
}
