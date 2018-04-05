package com.shirantech.sathitv.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;
import com.shirantech.sathitv.BuildConfig;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.activity.ChannelListActivity;
import com.shirantech.sathitv.activity.EntertainmentActivity;
import com.shirantech.sathitv.activity.HealthConsultantActivity;
import com.shirantech.sathitv.activity.AstrologyActivity;
import com.shirantech.sathitv.activity.ModelListActivity;
import com.shirantech.sathitv.activity.NewsActivity;
import com.shirantech.sathitv.adapter.BannerPagerAdapter;
import com.shirantech.sathitv.helper.CrashlyticsHelper;
import com.shirantech.sathitv.logger.Log;
import com.shirantech.sathitv.model.Banner;
import com.shirantech.sathitv.model.response.BannerResponse;
import com.shirantech.sathitv.network.ApiConstants;
import com.shirantech.sathitv.utils.AppLog;
import com.shirantech.sathitv.utils.AppUtil;
import com.shirantech.sathitv.utils.ServerResponse;
import com.shirantech.sathitv.widget.CustomProgressView;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A {@link Fragment} for the home screen.
 * Use the {@link HomeScreenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeScreenFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = HomeScreenFragment.class.getName();
    private final String URL_REQUEST_BANNER = ApiConstants.APP_USER_URL + "getBanner";
    private GridLayout gridLayout;
    private ViewPager viewPagerBanner;
    private List<Banner> bannerImagesList;
    private CustomProgressView mProgressView;
    private CirclePageIndicator circlePageIndicator;
    int totalSliderCount;
    int currentSliderPage = 0;

    /**
     * Use this factory method to create a new instance of this fragment
     *
     * @return A new instance of fragment {@link HomeScreenFragment}.
     */
    public static HomeScreenFragment newInstance() {
        HomeScreenFragment fragment = new HomeScreenFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //// TODO: 11/3/15 handle invalid token problem
    public HomeScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home_screen, container, false);
        final FrameLayout mDashboardChannels = (FrameLayout) view.findViewById(R.id.dashboard_channels);
        mDashboardChannels.setOnClickListener(this);
        final FrameLayout mDashboardModelChitChat = (FrameLayout) view.findViewById(R.id.dashboard_model_chitchat);
        mDashboardModelChitChat.setOnClickListener(this);
        final FrameLayout mDashboardEntertainment = (FrameLayout) view.findViewById(R.id.dashboard_entertainment);
        mDashboardEntertainment.setOnClickListener(this);
        final FrameLayout mDashboardNews = (FrameLayout) view.findViewById(R.id.dashboard_news);
        mDashboardNews.setOnClickListener(this);
        final FrameLayout mDashboardKundali = (FrameLayout) view.findViewById(R.id.dashboard_kundali);
        mDashboardKundali.setOnClickListener(this);
        final FrameLayout mDashboardHealthConsultant = (FrameLayout) view.findViewById(R.id.dashboard_health_consultant);
        mDashboardHealthConsultant.setOnClickListener(this);
        viewPagerBanner = (ViewPager) view.findViewById(R.id.viewPagerBanner);
        gridLayout = (GridLayout) view.findViewById(R.id.gridLayout);
        mProgressView = (CustomProgressView) view.findViewById(R.id.progress_view);
        circlePageIndicator = (CirclePageIndicator) view.findViewById(R.id.circlePagerIndicator);
        getBannersFromServer();
        return view;
    }

    /*
    * get banners images from server
    * */
    private void getBannersFromServer() {
        if (AppUtil.isInternetConnected(getActivity())) {
            showProgress(true);
            final Builders.Any.B builder = Ion.with(this).load(URL_REQUEST_BANNER);
            if (BuildConfig.DEBUG) {
                builder.setLogging(TAG, Log.VERBOSE);
            }
            builder.setHeader(ApiConstants.HEADER_KEY_ACCEPT, ApiConstants.HEADER_ACCEPT_VALUE)
                    .as(BannerResponse.class)
                    .setCallback(bannerCallback);
        } else {
            showMessageToUser(R.string.error_network_connection);
        }
    }


    /**
     * Callback for Banner images list
     */
    private FutureCallback<BannerResponse> bannerCallback = new FutureCallback<BannerResponse>() {
        @Override
        public void onCompleted(Exception exception, BannerResponse serverResponse) {
            if (null == exception) {
                if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_SUCCESS, serverResponse.getStatus())) {
                    bannerImagesList = serverResponse.getBannerImagesList();
                    showProgress(false);
                    if (0 == bannerImagesList.size()) {
                        showMessageToUser("No images");
                        AppLog.showLog(TAG, "0 size");
                    } else {
                        loadBannerSlider(bannerImagesList);
                    }
                } else if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_FAILURE, serverResponse.getStatus())) {
                    AppLog.showLog(TAG, serverResponse.getMessage());
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


    /*
    * loads banner images in viewpager
    * */
    private void loadBannerSlider(List<Banner> bannerImagesList) {
        totalSliderCount = bannerImagesList.size();
        AppLog.showLog(TAG, "bannerImagesList::::" + bannerImagesList.size());
        viewPagerBanner.setAdapter(new BannerPagerAdapter(getChildFragmentManager(), bannerImagesList));
        circlePageIndicator.setFillColor(getResources().getColor(R.color.primary_dark));
        circlePageIndicator.setViewPager(viewPagerBanner);
        startSliderFling();
    }

     Handler handler ;
     Runnable Update;
    Timer swipeTimer;

    /*
    * sets time for sliding banner images
    * */
    private void startSliderFling() {
        handler = new Handler();
    Update = new Runnable() {
            public void run() {
                if (currentSliderPage == totalSliderCount) {
                    currentSliderPage = 0;
                }
                viewPagerBanner.setCurrentItem(currentSliderPage, true);
                currentSliderPage++;
            }
        };

        swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(Update);
            }
        }, 1000, 5000);
    }


    public void stopTask() {

        if (swipeTimer != null) {
            Log.d("TIMER", "timer canceled");
            swipeTimer.cancel();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        AppLog.showLog(TAG, "slider onDestroy");
        stopTask();
    }

    @Override
    public void onPause() {
        AppLog.showLog(TAG, "slider pause");
        super.onPause();
        stopTask();
    }

    @Override
    public void onResume() {
        AppLog.showLog(TAG, "slider onResume");
        super.onResume();
        if(null != handler){
            swipeTimer = new Timer();
            swipeTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    handler.post(Update);
                }
            }, 1000, 5000);
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


    /**
     * Show the message and hide the ProgressBar
     *
     * @param messageId Resource id of message to show.
     */
    private void showMessageToUser(int messageId) {
//        showMessageToUser(getString(messageId));
    }

    /**
     * Show the message and hide the ProgressBar
     *
     * @param message message to show
     */
    private void showMessageToUser(String message) {
        showProgress(false);
        if (ServerResponse.isTokenInvalid(message)) {
            AppUtil.showInvalidTokenSnackBar(gridLayout, message, getActivity());
        } else {
            Snackbar.make(gridLayout, message, Snackbar.LENGTH_SHORT).show();
        }
        AppUtil.showInvalidTokenSnackBar(gridLayout, message, getActivity());
    }

    @Override
    public void onClick(View clickedView) {
        switch (clickedView.getId()) {
            case R.id.dashboard_channels: {
                getActivity().startActivity(ChannelListActivity.getStartIntent(getActivity()));
                break;
            }

            case R.id.dashboard_model_chitchat: {
                getActivity().startActivity(ModelListActivity.getStartIntent(getActivity()));
                break;
            }

            case R.id.dashboard_entertainment: {
                getActivity().startActivity(EntertainmentActivity.getStartIntent(getActivity()));
                break;
            }
            case R.id.dashboard_kundali: {
                getActivity().startActivity(AstrologyActivity.getStartIntent(getActivity()));
                break;
            }
            case R.id.dashboard_health_consultant: {
                getActivity().startActivity(HealthConsultantActivity.getStartIntent(getActivity()));
                break;
            }

            case R.id.dashboard_news: {
                getActivity().startActivity(NewsActivity.getStartIntent(getActivity()));
                break;
            }
            default: {
                // no-op
            }
        }
    }

    /**
     * Show service not available message to user
     */
    private void showServiceNotAvailableMessage() {
        new AlertDialog.Builder(getActivity())
                .setMessage("This service will be available soon")
                .setPositiveButton(R.string.prompt_ok, null)
                .show();
    }
}
