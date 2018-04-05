package com.shirantech.sathitv.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;
import com.shirantech.sathitv.BuildConfig;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.adapter.ChannelListAdapter;
import com.shirantech.sathitv.helper.CrashlyticsHelper;
import com.shirantech.sathitv.helper.PreferencesHelper;
import com.shirantech.sathitv.logger.Log;
import com.shirantech.sathitv.model.TvChannel;
import com.shirantech.sathitv.model.response.TvChannelListResponse;
import com.shirantech.sathitv.network.ApiConstants;
import com.shirantech.sathitv.utils.AppLog;
import com.shirantech.sathitv.utils.AppUtil;
import com.shirantech.sathitv.utils.ServerResponse;
import com.shirantech.sathitv.widget.CustomProgressView;
import com.shirantech.sathitv.widget.RecyclerViewItemDecoration;

import java.util.List;

/**
 * {@link android.app.Activity} for showing list of channels.
 */
public class ChannelListActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "ChannelList";
    private static final String URL_CHANNEL_LIST = ApiConstants.CHANNEL_URL + "getChannel";

    private RecyclerView mChannelGridView;
    private CustomProgressView mProgressView;

    private List<TvChannel> mTvChannelList;

    /**
     * Callback used in fetching channel list from server
     */
    private FutureCallback<TvChannelListResponse> mChannelListCallback = new FutureCallback<TvChannelListResponse>() {
        @Override
        public void onCompleted(Exception exception, TvChannelListResponse serverResponse) {
            if (null == exception) {
                if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_SUCCESS, serverResponse.getStatus())) {
                    mTvChannelList = serverResponse.getTvChannelList();
                    AppLog.showLog(TAG, "channellist size :: "+mTvChannelList.size());
                    populateData();
                } else if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_FAILURE, serverResponse.getStatus())) {
                    AppLog.showLog(TAG, serverResponse.getMessage() + " " + ServerResponse.isTokenInvalid(serverResponse.getMessage()) + "");
                    AppLog.showLog(TAG, ServerResponse.isTokenInvalid(serverResponse.getMessage()) + "");
                    showMessageToUser(serverResponse.getMessage());
                    // TODO : track failure in getting channels using Google Analytics(or Crashlytics)
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
     * Get the {@link Intent} to start this activity
     *
     * @param context context to initialize the Intent.
     * @return the Intent to start {@link ChannelListActivity}
     */
    public static Intent getStartIntent(Context context) {
        return new Intent(context, ChannelListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_list);

        setUpToolbar();
        initViews();

        showProgress(true);
        fetchChannels();
    }

    private void setUpToolbar() {
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            //noinspection ConstantConditions
            getSupportActionBar().setTitle(getString(R.string.dashboard_channels));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    private void initViews() {
        mChannelGridView = (RecyclerView) findViewById(R.id.channel_gridView);
        mProgressView = (CustomProgressView) findViewById(R.id.progress_view);
    }

    /**
     * Show/hide the progress while hiding/showing list
     *
     * @param show <code>true</code> to show the progress <code>false</code> otherwise.
     */
    private void showProgress(final boolean show) {
        final int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mChannelGridView.setVisibility(show ? View.GONE : View.VISIBLE);
        mChannelGridView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mChannelGridView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.setProgressMessage(R.string.message_getting_channels);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * Get the channels from the server
     */
    private void fetchChannels() {
        if (AppUtil.isInternetConnected(ChannelListActivity.this)) {
            final String loginToken = PreferencesHelper.getLoginToken(ChannelListActivity.this);
            final Builders.Any.B builder = Ion.with(ChannelListActivity.this)
                    .load(URL_CHANNEL_LIST);
            AppLog.showLog(TAG, "channel list url :: "+URL_CHANNEL_LIST);
            if (BuildConfig.DEBUG) {
                builder.setLogging(TAG, Log.VERBOSE);
            }
            builder.setHeader(ApiConstants.HEADER_KEY_ACCEPT, ApiConstants.HEADER_ACCEPT_VALUE)
                    .setBodyParameter(ApiConstants.PARAM_TOKEN, loginToken)
                    .as(TvChannelListResponse.class)
                    .setCallback(mChannelListCallback);
        } else {
            showMessageToUser(R.string.error_network_connection);
        }
    }

    /**
     * Show the channel in the list
     */
    private void populateData() {
        if ((null != mTvChannelList && !mTvChannelList.isEmpty())) {
            showProgress(false);
            setupPhotoGalleryView(getResources().getInteger(R.integer.photo_grid_tv_span_count));
        } else {
            showMessageToUser(R.string.message_no_channels);
        }
    }

    /**
     * Setup the {@link #mChannelGridView} to show the grid. And populate the grid with obtained channels.
     * @param
     * @param integer
     */
    private void setupPhotoGalleryView(int integer) {
        final int spanCount = integer;
        final int itemSpacing = (int) getResources().getDimension(R.dimen.spacing_normal);

        mChannelGridView.setHasFixedSize(true);
        mChannelGridView.setLayoutManager(new GridLayoutManager(ChannelListActivity.this, spanCount));
        mChannelGridView.addItemDecoration(new RecyclerViewItemDecoration(itemSpacing));
        mChannelGridView.setAdapter(new ChannelListAdapter(mTvChannelList, this));
    }

    /**
     * Show the message and hide the ProgressBar
     *
     * @param messageId Resource id of message to show.
     */
    private void showMessageToUser(int messageId) {
        mProgressView.setProgressMessage(messageId);
        mProgressView.hideProgressBar();
    }

    /**
     * Show the message and hide the ProgressBar
     *
     * @param message message to show.
     */
    private void showMessageToUser(String message) {

        mProgressView.setProgressMessage(message);
        mProgressView.hideProgressBar();
        if (ServerResponse.isTokenInvalid(message)) {
            AppUtil.showInvalidTokenSnackBar(mChannelGridView, message, ChannelListActivity.this);
        }else{
            Snackbar.make(mChannelGridView, message, Snackbar.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (Configuration.ORIENTATION_LANDSCAPE == getResources().getConfiguration().orientation) {
            setupPhotoGalleryView(4);
        } else {
            setupPhotoGalleryView(3);
        }
    }
    @Override
    public void onClick(View clickedView) {
        if (AppUtil.isInternetConnected(ChannelListActivity.this)) {
            if (R.id.photo_view == clickedView.getId()) {
                final int clickedPosition = (int) clickedView.getTag();
                if (mTvChannelList.get(clickedPosition).isFree()) {
                    //passed list and position to another activity
                    startActivity(TvChannelActivity.getStartIntent(ChannelListActivity.this,
                            mTvChannelList, clickedPosition));
                } else {
                    showSubscriptionMessage();
                }
            }
        }else{
            Snackbar.make(mChannelGridView, getResources().getString(R.string.error_network_connection), Snackbar.LENGTH_SHORT).show();

        }
    }

    /**
     * Show subscription required message
     */
    private void showSubscriptionMessage() {
        new AlertDialog.Builder(ChannelListActivity.this)
                .setTitle(R.string.subscription_alert_title)
                .setMessage(R.string.subscription_alert_message)
                .setPositiveButton(R.string.prompt_ok, null)
                .show();
    }
}
