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
import com.shirantech.sathitv.activity.FullscreenPlayerActivity;
import com.shirantech.sathitv.adapter.InterviewListAdapter;
import com.shirantech.sathitv.helper.CrashlyticsHelper;
import com.shirantech.sathitv.helper.PreferencesHelper;
import com.shirantech.sathitv.logger.Log;
import com.shirantech.sathitv.model.response.InterviewsReplyResponse;
import com.shirantech.sathitv.network.ApiConstants;
import com.shirantech.sathitv.utils.AppUtil;
import com.shirantech.sathitv.utils.ServerResponse;
import com.shirantech.sathitv.widget.CustomProgressView;

import java.util.List;

public class InterviewFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = InterviewFragment.class.getName();
    private static final String URL_REQUEST_INTERVIEWS = ApiConstants.CHANNEL_URL + "getInterview";

    private RecyclerView mInterviewListView;
    private InterviewsReplyResponse interview;
    private CustomProgressView mCustomProgressView;

    private List<InterviewsReplyResponse.InterviewItem> mInterviewsList;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public InterviewFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static InterviewFragment newInstance() {
        InterviewFragment fragment = new InterviewFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    /**
     * Callback for Janam Kundali reply
     */
    private FutureCallback<InterviewsReplyResponse> mReplyCallback = new FutureCallback<InterviewsReplyResponse>() {
        @Override
        public void onCompleted(Exception exception, InterviewsReplyResponse serverResponse) {
            if (null == exception) {
                if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_SUCCESS, serverResponse.getStatus())) {
                    mInterviewsList = serverResponse.getInterviewItemList();
                    populateData();
                } else if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_FAILURE, serverResponse.getStatus())) {
                    Log.d(TAG, serverResponse.getMessage());
                    showMessageToUser(serverResponse.getMessage());

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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_interview, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mInterviewListView = (RecyclerView) view.findViewById(R.id.interviews_listView);
        mCustomProgressView = (CustomProgressView) view.findViewById(R.id.progress_view);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showProgress(true);
        getInterviews();
    }

    private void getInterviews(){
        if (AppUtil.isInternetConnected(getActivity())) {
            showProgress(true);

            final String loginToken = PreferencesHelper.getLoginToken(getActivity());

            final Builders.Any.B builder = Ion.with(this).load(URL_REQUEST_INTERVIEWS);
            if (BuildConfig.DEBUG) {
                builder.setLogging(TAG, Log.VERBOSE);
            }
//            android.util.Log.i("Get the login token", TextUtils.isEmpty(loginToken) ? "login token is empty" : loginToken);
            builder.setHeader(ApiConstants.HEADER_KEY_ACCEPT, ApiConstants.HEADER_ACCEPT_VALUE)
                    .setBodyParameter(ApiConstants.PARAM_TOKEN, loginToken)
                    .as(InterviewsReplyResponse.class)
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
        mCustomProgressView.setVisibility(showProgress ? View.VISIBLE : View.GONE);
        mCustomProgressView.setProgressMessage(R.string.message_getting_interviews);
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
        mCustomProgressView.setProgressMessage(message);
        mCustomProgressView.hideProgressBar();
        if (ServerResponse.isTokenInvalid(message)) {
            AppUtil.showInvalidTokenSnackBar(mInterviewListView, message, getActivity());
        } else {
            Snackbar.make(mInterviewListView, message, Snackbar.LENGTH_SHORT).show();
        }
    }
    /**
     * If interviews are obtained from the server populate the list with provided interviews list.
     */
    private void populateData() {
        if ((null != mInterviewsList && !mInterviewsList.isEmpty())) {
            showProgress(false);
            setupInterviewList();
        } else {
            showMessageToUser(R.string.message_no_interviews_yet);
        }
    }
    private void setupInterviewList() {
        final int itemSpacing = (int) getResources().getDimension(R.dimen.spacing_normal);
       final  LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mInterviewListView.setHasFixedSize(true);
        mInterviewListView.setLayoutManager(layoutManager);

//        mInterviewListView.addItemDecoration(new RecyclerViewItemDecoration(itemSpacing));
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mInterviewListView.setAdapter(new InterviewListAdapter(mInterviewsList, this));
    }


    @Override
    public void onClick(View clickedView) {
        if (R.id.main_layout == clickedView.getId()) {
            final int position = (int) clickedView.getTag();
            final boolean videoAvailable = TextUtils.isEmpty(mInterviewsList.get(position).getVideoUrl())?false:true;
            if (videoAvailable) {
                startActivity(FullscreenPlayerActivity.getStartIntent(
                        getActivity(), mInterviewsList.get(position).getVideoUrl()));
            } else {
                showVideoUnavailableDialog();
            }
        }
    }
    private void showVideoUnavailableDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.video_not_ready_alert_title)
                .setMessage(R.string.video_not_ready_alert_message)
                .setPositiveButton(R.string.prompt_ok, null)
                .show();
    }
}
