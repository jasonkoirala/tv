package com.shirantech.sathitv.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;
import com.shirantech.sathitv.BuildConfig;
import com.shirantech.sathitv.OnReadMoreListener;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.adapter.ModelListRecyclerViewAdapter;
import com.shirantech.sathitv.fragment.ModelProfileFragment;
import com.shirantech.sathitv.helper.CrashlyticsHelper;
import com.shirantech.sathitv.helper.PreferencesHelper;
import com.shirantech.sathitv.logger.Log;
import com.shirantech.sathitv.model.ModelProfile;
import com.shirantech.sathitv.model.News;
import com.shirantech.sathitv.model.response.ModelProfileResponse;
import com.shirantech.sathitv.model.response.VODResponse;
import com.shirantech.sathitv.network.ApiConstants;
import com.shirantech.sathitv.utils.AppLog;
import com.shirantech.sathitv.utils.AppUtil;
import com.shirantech.sathitv.utils.ServerResponse;
import com.shirantech.sathitv.widget.CustomProgressView;

import java.util.List;

public class ModelListActivity extends AppCompatActivity implements OnReadMoreListener {
    private static final String TAG = ModelListActivity.class.getName();
    private static final String URL_REQUEST_MODEL_LIST = ApiConstants.APP_USER_URL + "getList";
    private RecyclerView recyclerView;
    private CustomProgressView progressView;
    private List<ModelProfile> modelList;
    private OnReadMoreListener listener;
    ModelProfileFragment modelProfileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_model_list);
        setUpToolbar();
        init();
        getModelListData();
        listener = (OnReadMoreListener) this;
    }

    private void getModelListData() {
        if (AppUtil.isInternetConnected(this)) {
            AppLog.showLog(TAG, "get model list url" + URL_REQUEST_MODEL_LIST);
            showProgress(true);
            final Builders.Any.B builder = Ion.with(this).load(URL_REQUEST_MODEL_LIST);
            if (BuildConfig.DEBUG) {
                builder.setLogging(TAG, Log.VERBOSE);
            }
            final String loginToken = PreferencesHelper.getLoginToken(this);
            AppLog.showLog(TAG, TextUtils.isEmpty(loginToken) ? "login token is empty" : loginToken+" log in token");
            JsonObject json = new JsonObject();
            json.addProperty(ApiConstants.PARAM_TOKEN, loginToken);
            AppLog.showLog(TAG, "json ::" + json);
            builder.setHeader(ApiConstants.HEADER_KEY_ACCEPT, ApiConstants.HEADER_ACCEPT_VALUE)
                    .setHeader(ApiConstants.HEADER_KEY_CONTENT_TYPE, ApiConstants.HEADER_ACCEPT_VALUE)
                    .setJsonObjectBody(json)
                    .as(ModelProfileResponse.class)
                    .setCallback(mReplyCallback);
        } else {
            showMessageToUser(R.string.error_network_connection);
        }

    }


    /**
     * Callback for model list
     */
    private FutureCallback<ModelProfileResponse> mReplyCallback = new FutureCallback<ModelProfileResponse>() {
        @Override
        public void onCompleted(Exception exception, ModelProfileResponse serverResponse) {
            if (null == exception) {
                AppLog.showLog(TAG, " serverResponse.getStatus()"+ serverResponse.getStatus());

                if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_SUCCESS, serverResponse.getStatus())) {
                    modelList = serverResponse.getModelList();
                    AppLog.showLog(TAG, "modelist size"+modelList.size());
                    showProgress(false);
                    populateData();
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

    /**
     * If models list are obtained from the server populate the list with provided model list.
     */
    private void populateData() {
        if ((null != modelList && !modelList.isEmpty())) {
            showProgress(false);
            setupReplyList();
        } else {
            showMessageToUser(R.string.message_model_no_data);
        }
    }


    private void setupReplyList() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ModelListRecyclerViewAdapter(modelList, listener, this));
    }

    /**
     * Get the {@link Intent} to start this activity
     *
     * @param context context to initialize the Intent.
     * @return the Intent to start {@link EntertainmentActivity}
     */
    public static Intent getStartIntent(Context context) {
        return new Intent(context, ModelListActivity.class);
    }


    private void setUpToolbar() {
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            //noinspection ConstantConditions
            getSupportActionBar().setTitle(getString(R.string.dashboard_model_chitchat));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != modelProfileFragment) {
                        if (modelProfileFragment.isVisible()) {
                            getSupportFragmentManager().beginTransaction().remove(modelProfileFragment).commit();
                        } else {
                            finish();
                        }
                    }else{
                        finish();
                    }
                }
            });
        }
    }

    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewModel);
        progressView = (CustomProgressView) findViewById(R.id.progressViewModel);

    }

    /**
     * Shows the progress while getting reply.
     *
     * @param showProgress <code>true</code> to show the progress <code>false</code> otherwise.
     */
    public void showProgress(final boolean showProgress) {
        progressView.setVisibility(showProgress ? View.VISIBLE : View.GONE);
//        mProgressView.setProgressMessage(R.string.message_getting_reply);
        progressView.setProgressMessage(R.string.message_getting_model_list);
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
            AppUtil.showInvalidTokenSnackBar(recyclerView, message, ModelListActivity.this);
        }else{
            Snackbar.make(recyclerView, message, Snackbar.LENGTH_SHORT).show();
        }
    }


    @Override
    public void callback(int position) {
        AppLog.showLog(TAG, "position :: " + position);
        modelProfileFragment = ModelProfileFragment.newInstance(modelList.get(position));

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.containerModel,
                modelProfileFragment).commit();
    }


    @Override
    public void onBackPressed() {
        if (null != modelProfileFragment) {
            if (modelProfileFragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().remove(modelProfileFragment).commit();
                AppLog.showLog(TAG, "is visible");
            } else {
                super.onBackPressed();
                AppLog.showLog(TAG, "is not visible");

            }

        }else{
            super.onBackPressed();
        }
    }
}
