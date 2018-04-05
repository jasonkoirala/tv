package com.shirantech.sathitv.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
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
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.adapter.ModelAllPhotosRecyclerViewAdapter;
import com.shirantech.sathitv.adapter.ShowAllPhotosPagerAdapter;
import com.shirantech.sathitv.helper.CrashlyticsHelper;
import com.shirantech.sathitv.logger.Log;
import com.shirantech.sathitv.model.ModelProfile;
import com.shirantech.sathitv.model.response.ModelProfileResponse;
import com.shirantech.sathitv.network.ApiConstants;
import com.shirantech.sathitv.utils.AppLog;
import com.shirantech.sathitv.utils.AppUtil;
import com.shirantech.sathitv.utils.ServerResponse;
import com.shirantech.sathitv.widget.CustomFontTextView;
import com.shirantech.sathitv.widget.CustomProgressView;

import java.util.List;

public class ShowAllPhotosActivity extends AppCompatActivity implements ModelAllPhotosRecyclerViewAdapter.OnPhotoClickListener {

    private static final String TAG = ShowAllPhotosActivity.class.getName();
    private static final String URL_ALL_PHOTOS = ApiConstants.APP_USER_URL + "getAllPhotosOfModel";
    public static final String EXTRA_PHOTOS = "all_photos";
    private ViewPager viewPager;
    private int userId;
    private CustomProgressView progressView;
    private CustomFontTextView textViewNoPhotos;
    private List<ModelProfile> modelPhotosList;
    private RecyclerView recyclerViewPhotos;
    private ModelAllPhotosRecyclerViewAdapter.OnPhotoClickListener listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_show_all_photos);
        setUpToolbar();
        initViews();
        userId = getIntent().getIntExtra(EXTRA_PHOTOS, 0);
        AppLog.showLog(TAG, "user id :: "+userId);
        getAllPhotos();
        listener = (ModelAllPhotosRecyclerViewAdapter.OnPhotoClickListener) this;
    }

    private void initViews() {
        viewPager = (ViewPager) findViewById(R.id.viewPagerPhotos);
        progressView = (CustomProgressView) findViewById(R.id.progressViewModel);
        textViewNoPhotos = (CustomFontTextView) findViewById(R.id.textViewNoPhotos);
        recyclerViewPhotos = (RecyclerView) findViewById(R.id.recyclerViewPhotos);
        LinearLayoutManager layoutManagerExclusive = new LinearLayoutManager(ShowAllPhotosActivity.this, LinearLayoutManager.HORIZONTAL, false);
//        recyclerViewPhotos.addItemDecoration(itemDecoration);
        recyclerViewPhotos.setLayoutManager(layoutManagerExclusive);
    }
    private void setAllPhotosAdapter(List<ModelProfile> photosList) {
        AppLog.showLog(TAG, "set adapter");
        ShowAllPhotosPagerAdapter pagerAdapter = new ShowAllPhotosPagerAdapter(getSupportFragmentManager(), photosList);
        viewPager.setAdapter(pagerAdapter);


    }

    private void setUpToolbar() {
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            //noinspection ConstantConditions
            getSupportActionBar().setTitle(getString(R.string.title_view_all_photos));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }


    /**
     * Callback for News list
     */
    private FutureCallback<ModelProfileResponse> callback = new FutureCallback<ModelProfileResponse>() {
        @Override
        public void onCompleted(Exception exception, ModelProfileResponse serverResponse) {
            AppLog.showLog(TAG,"response :: "+serverResponse.getModelPhotosList());
            if (null == exception) {
                if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_SUCCESS, serverResponse.getStatus())) {
//                    showProgress(false);
                    modelPhotosList = serverResponse.getModelPhotosList();
                    if (0 == modelPhotosList.size()) {
                        AppLog.showLog(TAG, "0 size");
                        textViewNoPhotos.setVisibility(View.VISIBLE);
//                        Toast.makeText(ShowAllPhotosActivity.this, "Photos not available", Toast.LENGTH_SHORT).show();
                    } else {
                        setAllPhotosAdapter(modelPhotosList);
                        setSliderAdapter(modelPhotosList);
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

    private void setSliderAdapter(List<ModelProfile> modelPhotosList) {
        ModelAllPhotosRecyclerViewAdapter adapter = new ModelAllPhotosRecyclerViewAdapter(modelPhotosList, ShowAllPhotosActivity.this, listener);
        recyclerViewPhotos.setAdapter(adapter);

    }


    private void getAllPhotos() {
        if (AppUtil.isInternetConnected(this)) {
            AppLog.showLog(TAG, "URL_ALL_PHOTOS" + URL_ALL_PHOTOS);
//            showProgress(true);
            final Builders.Any.B builder = Ion.with(this).load(URL_ALL_PHOTOS);
            if (BuildConfig.DEBUG) {
                builder.setLogging(TAG, Log.VERBOSE);
            }
            JsonObject json = new JsonObject();
            json.addProperty("userId", userId);
            AppLog.showLog(TAG, "json ::" + json);
//            AppLog.showLog(TextUtils.isEmpty(loginToken) ? "login token is empty" : loginToken+" log in token");
            builder.setHeader(ApiConstants.HEADER_KEY_ACCEPT, ApiConstants.HEADER_ACCEPT_VALUE)
                .setJsonObjectBody(json)
                    .as(ModelProfileResponse.class)
                    .setCallback(callback);
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
        progressView.setVisibility(showProgress ? View.VISIBLE : View.GONE);
//        mProgressView.setProgressMessage(R.string.message_getting_reply);
        progressView.setProgressMessage(R.string.message_getting_news);
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
            AppUtil.showInvalidTokenSnackBar(viewPager, message, ShowAllPhotosActivity.this);
        } else {
            Snackbar.make(viewPager, message, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(int position) {
        viewPager.setCurrentItem(position);
    }
}
