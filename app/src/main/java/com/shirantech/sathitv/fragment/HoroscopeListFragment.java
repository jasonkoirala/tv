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
import com.shirantech.sathitv.adapter.HoroscopeRecyclerViewAdapter;
import com.shirantech.sathitv.helper.CrashlyticsHelper;
import com.shirantech.sathitv.helper.PreferencesHelper;
import com.shirantech.sathitv.logger.Log;
import com.shirantech.sathitv.model.response.HoroscopeResponse;
import com.shirantech.sathitv.network.ApiConstants;
import com.shirantech.sathitv.utils.AppLog;
import com.shirantech.sathitv.utils.AppUtil;
import com.shirantech.sathitv.utils.ServerResponse;
import com.shirantech.sathitv.widget.CustomProgressView;
//import com.shirantech.sathitv.widget.RecyclerViewItemDecoration;

import java.util.List;

/**
 * A simple {@link Fragment} subclass showing list of Horoscope replies.
 * Use the {@link HoroscopeListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HoroscopeListFragment extends Fragment {
    private static final String TAG = HoroscopeListFragment.class.getName();
    private static final String URL_REQUEST_HOROSCOPE = ApiConstants.CHANNEL_URL + "getHoroscope";
//    private static final String URL_REQUEST_HOROSCOPE = "http://f1soft-host.com/sathitv/api/channel/getHoroscope";


    private RecyclerView mHoroscopeListView;
    private CustomProgressView mProgressView;

    private List<HoroscopeResponse.Horoscope> mHoroscopeList;

    /**
     * Callback for Horoscope list
     */
    private FutureCallback<HoroscopeResponse> mReplyCallback = new FutureCallback<HoroscopeResponse>() {
        @Override
        public void onCompleted(Exception exception, HoroscopeResponse serverResponse) {



            if (null == exception) {
                if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_SUCCESS, serverResponse.getStatus())) {
                    mHoroscopeList = serverResponse.getHoroscopeList();

                    populateData();
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
    public static HoroscopeListFragment newInstance() {
        return new HoroscopeListFragment();
    }

    public HoroscopeListFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_horoscope_list, container, false);
        mHoroscopeListView = (RecyclerView) view.findViewById(R.id.horoscope_listview);
        mProgressView = (CustomProgressView) view.findViewById(R.id.progress_view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppLog.showLog(TAG,"Get url"+URL_REQUEST_HOROSCOPE);
        showProgress(true);
        getHoroscopeData();
    }

    /**
     * Get the reply(s) for the Horoscope data
     */
    private void getHoroscopeData() {
        AppLog.showLog(TAG,"get horoscope data"+URL_REQUEST_HOROSCOPE);
        if (AppUtil.isInternetConnected(getActivity())) {
            showProgress(true);

            final String loginToken = PreferencesHelper.getLoginToken(getActivity());

            final Builders.Any.B builder = Ion.with(this).load(URL_REQUEST_HOROSCOPE);

            if (BuildConfig.DEBUG) {
                builder.setLogging(TAG, Log.VERBOSE);
            }
            AppLog.showLog(TextUtils.isEmpty(loginToken) ? "login token is empty" : loginToken+" log in token");
            builder.setHeader(ApiConstants.HEADER_KEY_ACCEPT, ApiConstants.HEADER_ACCEPT_VALUE)
//                    .setBodyParameter(ApiConstants.PARAM_TOKEN, loginToken)
                    .as(HoroscopeResponse.class)
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
//        mProgressView.setProgressMessage(R.string.message_getting_reply);
        mProgressView.setProgressMessage(R.string.message_getting_horoscope);
    }

    /**
     * If photos are obtained from the server populate the list with provided photo list.
     */
    private void populateData() {
        if ((null != mHoroscopeList && !mHoroscopeList.isEmpty())) {
            showProgress(false);
            setupReplyList();
        } else {
            showMessageToUser(R.string.message_getting_horoscope);
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
            AppUtil.showInvalidTokenSnackBar(mHoroscopeListView, message, getActivity());
        } else {
            Snackbar.make(mHoroscopeListView, message, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void setupReplyList() {
        final int itemSpacing = (int) getResources().getDimension(R.dimen.spacing_normal);
        mHoroscopeListView.setHasFixedSize(true);
        mHoroscopeListView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mHoroscopeListView.addItemDecoration(new RecyclerViewItemDecoration(itemSpacing));
        mHoroscopeListView.setAdapter(new HoroscopeRecyclerViewAdapter(mHoroscopeList));
    }


}
