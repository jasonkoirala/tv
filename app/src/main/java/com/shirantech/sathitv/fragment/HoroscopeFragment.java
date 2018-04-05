package com.shirantech.sathitv.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.shirantech.sathitv.helper.CrashlyticsHelper;
import com.shirantech.sathitv.helper.PreferencesHelper;
import com.shirantech.sathitv.logger.Log;
import com.shirantech.sathitv.model.response.HealtConsultantReplyResponse;
import com.shirantech.sathitv.model.response.HoroscopeResponse;
import com.shirantech.sathitv.network.ApiConstants;
import com.shirantech.sathitv.utils.AppLog;
import com.shirantech.sathitv.utils.AppUtil;
import com.shirantech.sathitv.widget.CustomProgressView;
import com.shirantech.sathitv.widget.RecyclerViewItemDecoration;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class HoroscopeFragment extends Fragment {

    private static final String URL_REQUEST_HOROSCOPE = ApiConstants.CHANNEL_URL + "getConsultantqueries";
    private static final String TAG = "Horoscope";

//    private List<HoroscopeResponse.Horoscope> mHoroscopeList;
    private RecyclerView mHoroscopeListView;
    private CustomProgressView mCustomProgressView;

    /**
     * Callback for Horoscope request
     */
    private FutureCallback<HealtConsultantReplyResponse> mRequestCallback = new FutureCallback<HealtConsultantReplyResponse>() {
        @Override
        public void onCompleted(Exception exception, HealtConsultantReplyResponse serverResponse) {
            AppLog.showLog(TAG, "Status of horoscope " + serverResponse.getStatus()+exception);
            if (null == exception) {
                if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_SUCCESS, serverResponse.getStatus())) {
                 /*   mHoroscopeList = serverResponse.getHoroscopeList();
                    AppLog.showLog(TAG, mHoroscopeList.size() + " length of array");*/
                    populateData();
                } else if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_FAILURE, serverResponse.getStatus())) {
                    Log.d(TAG, serverResponse.getStatus());
                    showMessageToUser(serverResponse.getStatus());
                    // TODO : track horoscope request failure using Google Analytics(or Crashlytics)
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
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HoroscopeFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static HoroscopeFragment newInstance() {
        HoroscopeFragment fragment = new HoroscopeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_horoscope_list, container, false);
        mHoroscopeListView = (RecyclerView) view.findViewById(R.id.interviews_listView);
        mCustomProgressView = (CustomProgressView) view.findViewById(R.id.progress_view);

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getHoroscopes();
    }

    private void getHoroscopes() {
        if (AppUtil.isInternetConnected(getActivity())) {
            showProgress(true);

            final Builders.Any.B builder = Ion.with(this).load(URL_REQUEST_HOROSCOPE);
            if (BuildConfig.DEBUG) {
                builder.setLogging(TAG, Log.VERBOSE);
            }
            final String loginToken = PreferencesHelper.getLoginToken(getActivity());

            builder.setHeader(ApiConstants.HEADER_KEY_ACCEPT, ApiConstants.HEADER_ACCEPT_VALUE)
                    .setHeader("Content-Type",ApiConstants.HEADER_ACCEPT_VALUE)
                    .setBodyParameter(ApiConstants.PARAM_TOKEN, loginToken)
                    .as(HealtConsultantReplyResponse.class)
                    .setCallback(mRequestCallback);
        } else {
            showMessageToUser(R.string.error_network_connection);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(HoroscopeResponse.Horoscope item);
    }

    /**
     * If horoscopes are obtained from the server populate the list with provided horoscope list.
     */
    private void populateData() {
       /* if ((null != mHoroscopeList && !mHoroscopeList.isEmpty())) {
            showProgress(false);
            setupHoroscopeList();
        } else {
            showMessageToUser(R.string.message_no_interviews_yet);
        }*/
    }

    private void setupHoroscopeList() {
        final int itemSpacing = (int) getResources().getDimension(R.dimen.spacing_normal);

        mHoroscopeListView.setHasFixedSize(true);
        mHoroscopeListView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mHoroscopeListView.addItemDecoration(new RecyclerViewItemDecoration(itemSpacing));

//        mHoroscopeListView.setAdapter(new HoroscopeRecyclerViewAdapter(mHoroscopeList));
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

}
