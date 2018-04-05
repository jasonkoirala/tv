package com.shirantech.sathitv.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import com.shirantech.sathitv.adapter.VODRecyclerViewAdapter;
import com.shirantech.sathitv.helper.CrashlyticsHelper;
import com.shirantech.sathitv.logger.Log;
import com.shirantech.sathitv.model.VOD;
import com.shirantech.sathitv.model.response.VODResponse;
import com.shirantech.sathitv.network.ApiConstants;
import com.shirantech.sathitv.utils.AppLog;
import com.shirantech.sathitv.utils.AppUtil;
import com.shirantech.sathitv.utils.ServerResponse;
import com.shirantech.sathitv.widget.CustomProgressView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VODFragment extends Fragment {
    private static final String TAG = VODFragment.class.getName();
    private static final String URL_REQUEST_VOD = ApiConstants.CHANNEL_URL + "getVod?offset=";
    private static final String URL_REQUEST_MOD = ApiConstants.CHANNEL_URL + "getMod?offset=";
    private static final int TASK_ID_VOD = 0;
    private static final int TASK_ID_MOD = 1;
    private static final String POSITION = "position";
    private RecyclerView recyclerView;
    private CustomProgressView progressView;
    private List<VOD> vodList;
    private boolean isLoadMore = true;
    int offset = 0, newoffset, position;

    private VODRecyclerViewAdapter adapter;

    public VODFragment() {
        // Required empty public constructor
    }

    /**
     * Callback for VOD list
     */
    private FutureCallback<VODResponse> mReplyCallback = new FutureCallback<VODResponse>() {
        @Override
        public void onCompleted(Exception exception, VODResponse serverResponse) {
            if (null == exception) {
                if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_SUCCESS, serverResponse.getStatus())) {
                    if (position == 0) {
                        vodList = serverResponse.getVodsList();
                    } else {
                        vodList = serverResponse.getModsList();
                    }
                    newoffset = serverResponse.getOffset();
                    AppLog.showLog(TAG, "vodlist " + vodList.size());
                    AppLog.showLog(TAG, "new offset " + serverResponse.getOffset());
                    showProgress(false);
                    if (0 == vodList.size()) {
                        isLoadMore = false;
                        AppLog.showLog(TAG, "0 size");
                    } else {
                        populateData(vodList);
                    }
                } else if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_FAILURE, serverResponse.getStatus())) {
                    Log.d(TAG, serverResponse.getMessage());
                    String message = serverResponse.getMessage();

                    showMessageToUser(message);

                    // TODO : track failure in getting photo using Google Analytics(or Crashlytics)
                } else {
                    showMessageToUser(R.string.error_problem_occurred);
                    // TODO : track connection/server error using Google Analytics(or Crashlytics)
                }
            } else {
                AppLog.showLog(TAG, "exception" + exception);
                showMessageToUser(R.string.error_problem_occurred);
                CrashlyticsHelper.sendCaughtException(exception);
            }
        }
    };

    /**
     * If photos are obtained from the server populate the list with provided photo list.
     *
     * @param vodList
     */
    private void populateData(List<VOD> vodList) {

        showProgress(false);
        if (null == adapter) {
            setupReplyList(vodList);
        } else {
            adapter.addMoreVideos(vodList);

        }
    }

    private void setupReplyList(List<VOD> vodList) {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new VODRecyclerViewAdapter(vodList, getActivity());
        recyclerView.setAdapter(adapter);
    }

    // TODO: Rename and change types and number of parameters
    public static VODFragment newInstance(int position) {
        VODFragment fragment = new VODFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vod, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewVod);
        progressView = (CustomProgressView) view.findViewById(R.id.progressViewVod);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showProgress(true);
        getVodData(offset, position);
        onScrollList();

    }

    private void onScrollList() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                AppLog.showLog(TAG, "scrolled changed" + newoffset);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isLoadMore) {
                    getVodData(newoffset, position);
                }
            }
        });
    }

    private void getVodData(int offset, int position) {
        AppLog.showLog(TAG, "get vod data" + URL_REQUEST_VOD);
        if (AppUtil.isInternetConnected(getActivity())) {
            showProgress(true);
            String url;
            if (position == TASK_ID_VOD) {
                url = URL_REQUEST_VOD + offset;
            } else {
                url = URL_REQUEST_MOD + offset;
            }
            final Builders.Any.B builder = Ion.with(this).load(url);
            if (BuildConfig.DEBUG) {
                builder.setLogging(TAG, Log.VERBOSE);
            }
            builder.setHeader(ApiConstants.HEADER_KEY_ACCEPT, ApiConstants.HEADER_ACCEPT_VALUE)
                    .as(VODResponse.class)
                    .setCallback(mReplyCallback);
        } else {
            showMessageToUser(R.string.error_network_connection);
        }
    }

    /**
     * Show the message and hide the ProgressBar
     *
     * @param message message to show
     */
    private void showMessageToUser(String message) {
        progressView.setProgressMessage(message);
        progressView.hideProgressBar();
        if (ServerResponse.isTokenInvalid(message)) {
            AppUtil.showInvalidTokenSnackBar(recyclerView, message, getActivity());
        } else {
            Snackbar.make(recyclerView, message, Snackbar.LENGTH_SHORT).show();
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
     * Shows the progress while getting reply.
     *
     * @param showProgress <code>true</code> to show the progress <code>false</code> otherwise.
     */
    public void showProgress(final boolean showProgress) {
        progressView.setVisibility(showProgress ? View.VISIBLE : View.GONE);
//        mProgressView.setProgressMessage(R.string.message_getting_reply);
        progressView.setProgressMessage(R.string.message_getting_vod);
    }


}
