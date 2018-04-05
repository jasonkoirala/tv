package com.shirantech.sathitv.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;
import com.shirantech.sathitv.BuildConfig;
import com.shirantech.sathitv.OnReadMoreListener;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.adapter.NewsListRecyclerViewAdapter;
import com.shirantech.sathitv.helper.CrashlyticsHelper;
import com.shirantech.sathitv.logger.Log;
import com.shirantech.sathitv.model.News;
import com.shirantech.sathitv.model.response.NewsResponse;
import com.shirantech.sathitv.model.response.VODResponse;
import com.shirantech.sathitv.network.ApiConstants;
import com.shirantech.sathitv.utils.AppLog;
import com.shirantech.sathitv.utils.AppUtil;
import com.shirantech.sathitv.utils.ServerResponse;
import com.shirantech.sathitv.widget.CustomProgressView;

import java.util.List;

public class NewsActivity extends AppCompatActivity {

    private static final String TAG = NewsActivity.class.getName();
    private static final String URL_NEWS = ApiConstants.APP_USER_URL + "getNews?offset=";

    private RecyclerView recyclerView;
    private CustomProgressView progressView;
    private List<News> newsList;
    int offset = 0, newoffset;
    private boolean isLoadMore = true;
    private NewsListRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_news);
        setUpToolbar();
        initViews();
        getNewsData(offset);
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
                    getNewsData(newoffset);
                }
            }
        });
    }

    private void getNewsData(int offset) {
        if (AppUtil.isInternetConnected(this)) {
            AppLog.showLog(TAG, "get news data" + URL_NEWS);
            showProgress(true);
            final Builders.Any.B builder = Ion.with(this).load(URL_NEWS + offset);
            if (BuildConfig.DEBUG) {
                builder.setLogging(TAG, Log.VERBOSE);
            }
//            AppLog.showLog(TextUtils.isEmpty(loginToken) ? "login token is empty" : loginToken+" log in token");
            builder.setHeader(ApiConstants.HEADER_KEY_ACCEPT, ApiConstants.HEADER_ACCEPT_VALUE)
//                    .setBodyParameter(ApiConstants.PARAM_TOKEN, loginToken)
                    .as(NewsResponse.class)
                    .setCallback(mReplyCallback);
        } else {
            showMessageToUser(R.string.error_network_connection);
        }
    }


    /**
     * Callback for News list
     */
    private FutureCallback<NewsResponse> mReplyCallback = new FutureCallback<NewsResponse>() {
        @Override
        public void onCompleted(Exception exception, NewsResponse serverResponse) {
            if (null == exception) {
                if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_SUCCESS, serverResponse.getStatus())) {
                    newsList = serverResponse.getNewsList();
                    newoffset = serverResponse.getOffset();
                    showProgress(false);
                    if (0 == newsList.size()) {
                        isLoadMore = false;
                        AppLog.showLog(TAG, "0 size");
                    } else {
                        populateData(newsList);
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

    private void populateData(List<News> newsList) {
        showProgress(false);
        if (null == adapter) {
            setupReplyList(newsList);
        } else {
            adapter.addMoreVideos(newsList);

        }
    }

    private void setupReplyList(List<News> newsList) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsListRecyclerViewAdapter(newsList, this);
        recyclerView.setAdapter(adapter);
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
        progressView.setProgressMessage(message);
        progressView.hideProgressBar();

        if (ServerResponse.isTokenInvalid(message)) {
            AppUtil.showInvalidTokenSnackBar(recyclerView, message, NewsActivity.this);
        } else {
            Snackbar.make(recyclerView, message, Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * Shows the progress while getting reply.
     *
     * @param showProgress <code>true</code> to show the progress <code>false</code> otherwise.
     */
    public void showProgress(final boolean showProgress) {
        progressView.setVisibility(showProgress ? View.VISIBLE : View.GONE);
//        mProgressView.setProgressMessage(R.string.message_getting_reply);
        progressView.setProgressMessage(R.string.message_getting_news);
    }

    /**
     * Get the {@link Intent} to start this activity
     *
     * @param context context to initialize the Intent.
     * @return the Intent to start {@link HealthConsultantActivity}
     */
    public static Intent getStartIntent(Context context) {
        return new Intent(context, NewsActivity.class);
    }


    private void setUpToolbar() {
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            //noinspection ConstantConditions
            getSupportActionBar().setTitle(getString(R.string.dashboard_news));
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
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewNews);
        progressView = (CustomProgressView) findViewById(R.id.progressViewNews);
    }


}
