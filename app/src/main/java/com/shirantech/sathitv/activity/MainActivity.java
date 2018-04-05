package com.shirantech.sathitv.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;
import com.shirantech.sathitv.BuildConfig;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.SathiTvApplication;
import com.shirantech.sathitv.fragment.AboutFragment;
import com.shirantech.sathitv.fragment.FeedbackFragment;
import com.shirantech.sathitv.fragment.HomeScreenFragment;
import com.shirantech.sathitv.helper.CrashlyticsHelper;
import com.shirantech.sathitv.helper.PreferencesHelper;
import com.shirantech.sathitv.logger.Log;
import com.shirantech.sathitv.model.response.GeneralResponse;
import com.shirantech.sathitv.network.ApiConstants;
import com.shirantech.sathitv.utils.AppLog;
import com.shirantech.sathitv.utils.AppUtil;
import com.shirantech.sathitv.utils.ServerResponse;
import com.shirantech.sathitv.widget.CustomFontNavigationView;
import com.shirantech.sathitv.widget.CustomFontTextView;
import com.shirantech.sathitv.widget.CustomProgressView;

/**
 * An {@link Activity} showing home screen.
 */
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getName();
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String URL_REQUEST_LOGOUT = ApiConstants.APP_USER_URL + "logout";

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private CustomFontTextView textViewName;
    private CustomFontNavigationView mNavigationView;
    private CustomProgressView mProgressView;
    private String imageUrl;
    private SimpleDraweeView imageView;
    private boolean mUserLearnedDrawer;
    public int mCurrentSelectedPosition = 0;

    /**
     * Get the {@link Intent} to start this activity
     *
     * @param context context to initialize the Intent.
     * @return the Intent to start {@link MainActivity}
     */
    public static Intent getStartIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpToolbar();
        initView();

        mUserLearnedDrawer = PreferencesHelper.readDrawerSetting(this);
        if (getIntent().getExtras() != null) {
            String menuFragment = getIntent().getExtras().getString("menuFragment");
            AppLog.showLog("Get the intent message from main activity" + menuFragment);
            if (!TextUtils.isEmpty(menuFragment)) {
                if (menuFragment.equals(ServerResponse.RASHIFAL)) {
                    mCurrentSelectedPosition = 1;
                } else if (menuFragment.equals(ServerResponse.CONSULTANT)) {
                    mCurrentSelectedPosition = 3;
                    AppLog.showLog("Comes in Consult");
                }
            }
        }
        imageUrl = PreferencesHelper.getProfileImageUrl(MainActivity.this);
        AppLog.showLog(TAG, "main Profile image url :: " + imageUrl);
        setUpNavDrawer();


        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION, 0);
        }

    }

    private void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mProgressView = (CustomProgressView) findViewById(R.id.progress_view);
        mNavigationView = (CustomFontNavigationView) findViewById(R.id.navigation_drawer);
        View headerLayout = mNavigationView.inflateHeaderView(R.layout.drawer_header);
        textViewName = (CustomFontTextView) headerLayout.findViewById(R.id.textViewNavName);
        imageView = (SimpleDraweeView) headerLayout.findViewById(R.id.imageViewNavHeader);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (((SathiTvApplication) getApplication()).isLanguageChanged()) {
            ((SathiTvApplication) getApplication()).setLanguageChanged(false);
            recreate();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION, 0);
        selectCurrentDrawerItem();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (mCurrentSelectedPosition != 0) {
                mCurrentSelectedPosition = 0;
                selectCurrentDrawerItem();
            } else {
                super.onBackPressed();
            }
        }
    }

    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
    }

    public void showProgress(final boolean showProgress) {
        // hide submit menu when progress is visible
//        showSubmitMenu(!showProgress);
        mProgressView.setVisibility(showProgress ? View.VISIBLE : View.GONE);
        mProgressView.setProgressMessage(R.string.message_logging_out);
    }

    private void setUpNavDrawer() {
        if (null != mToolbar) {
            //noinspection ConstantConditions
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationIcon(R.drawable.ic_navigation);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }

        if (!TextUtils.isEmpty(PreferencesHelper.getProfileImageUrl(MainActivity.this))) {
            AppLog.showLog(TAG, "imageUrl  ::: "+imageUrl);
            showPhoto(PreferencesHelper.getProfileImageUrl(MainActivity.this));
        }else{
            AppLog.showLog(TAG, "empty main url");
        }
        if (!TextUtils.isEmpty(PreferencesHelper.getUsername(this))) {
            textViewName.setText(PreferencesHelper.getUsername(this));
        } else {
            textViewName.setText(PreferencesHelper.getEmail(this));
        }
        mNavigationView.setFont(((SathiTvApplication) getApplication()).getCurrentLanguage().getFontName());
        mNavigationView.setNavigationItemSelectedListener(this);


        selectCurrentDrawerItem();

        if (!mUserLearnedDrawer) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            mUserLearnedDrawer = true;
            PreferencesHelper.writeDrawerSetting(this, true);
        }
    }

    private void showPhoto(String profileImageUrl) {
        AppLog.showLog(TAG, "profileImageUrl "+profileImageUrl);
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(
                Uri.parse(profileImageUrl))
                .setProgressiveRenderingEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(imageView.getController())
                .build();
        imageView.setController(controller);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        // only menu item from group 1 needs to be checkable
        if (menuItem.getGroupId() == R.id.group_1_drawer) {
            menuItem.setChecked(true);
        }

        switch (menuItem.getItemId()) {
            case R.id.navigation_item_home:
                mCurrentSelectedPosition = 0;
                mToolbar.setTitle(R.string.app_name);
                showFragment(HomeScreenFragment.newInstance());
                break;
        /*    case R.id.navigation_item_janam_kundali:
                mCurrentSelectedPosition = 1;
                mToolbar.setTitle(R.string.title_janam_kundali_reply);
                showFragment(JanamKundaliReplyFragment.newInstance());
                break;*/
//            case R.id.navigation_item_photo_gallery:
//                mCurrentSelectedPosition = 2;
//                mToolbar.setTitle(R.string.drawer_item_photo_gallery);
//                showFragment(PhotoGalleryFragment.newInstance());
//                break;
            case R.id.navigation_item_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.navigation_item_feedback:

                mToolbar.setTitle(R.string.drawer_item_feedback);
                showFragment(FeedbackFragment.newInstance());
                break;
            case R.id.navigation_item_about:

                mToolbar.setTitle(R.string.drawer_item_about);
                showFragment(AboutFragment.newInstance());
                break;
            case R.id.navigation_item_logout:
                showLogoutAlertDialog();
                break;
          /*  case R.id.navigation_item_health_consultant:
                mCurrentSelectedPosition = 3;
                mToolbar.setTitle(R.string.drawer_item_health_consultant);
                showFragment(HealthConsultantReplyFragment.newInstance());
                break;*/
            default:
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    private void selectCurrentDrawerItem() {
        Menu menu = mNavigationView.getMenu();
        onNavigationItemSelected(menu.getItem(mCurrentSelectedPosition));
    }

    @Override
    public void onStop() {
        Ion.getDefault(this).cancelAll(this);
        super.onStop();
    }

    public static Intent getStartIntentWithExtras(Context context, String extraMessage) {
        return new Intent(context, MainActivity.class).putExtra("menuFragment", extraMessage);

    }

    private void showLogoutAlertDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearToken();
                    }
                })
//                .build()
                .show();
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    private void clearToken() {
        AppLog.showLog(TAG, "clear token");
        if (AppUtil.isInternetConnected(this)) {
            AppLog.showLog(TAG, "url logout" + URL_REQUEST_LOGOUT);
            showProgress(true);
            final Builders.Any.B builder = Ion.with(this).load(URL_REQUEST_LOGOUT);
            if (BuildConfig.DEBUG) {
                builder.setLogging(TAG, Log.VERBOSE);
            }
            final String loginToken = PreferencesHelper.getLoginToken(this);
            AppLog.showLog(TAG, TextUtils.isEmpty(loginToken) ? "login token is empty" : loginToken + " log in token");
            JsonObject json = new JsonObject();
            json.addProperty(ApiConstants.PARAM_TOKEN, loginToken);
            AppLog.showLog(TAG, "json ::" + json);
            builder.setHeader(ApiConstants.HEADER_KEY_ACCEPT, ApiConstants.HEADER_ACCEPT_VALUE)
                    .setHeader(ApiConstants.HEADER_KEY_CONTENT_TYPE, ApiConstants.HEADER_ACCEPT_VALUE)
                    .setJsonObjectBody(json)
                    .as(GeneralResponse.class)
                    .setCallback(logoutCallback);
        } else {
            Snackbar.make(mDrawerLayout, R.string.error_network_connection, Snackbar.LENGTH_LONG).show();
        }
    }


    /**
     * Callback for model list
     */
    private FutureCallback<GeneralResponse> logoutCallback = new FutureCallback<GeneralResponse>() {
        @Override
        public void onCompleted(Exception exception, GeneralResponse serverResponse) {
            if (null == exception) {
                AppLog.showLog(TAG, " serverResponse.getStatus()" + serverResponse.getStatus());

                if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_SUCCESS, serverResponse.getStatus())) {
                    showSuccessMessage(serverResponse.getMessage());
                } else if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_FAILURE, serverResponse.getStatus())) {
                    AppLog.showLog(TAG, serverResponse.getMessage());
                    showFailureMessage(serverResponse.getMessage());

                    // TODO : track failure in getting photo using Google Analytics(or Crashlytics)
                } else {
                    showFailureMessage(R.string.error_problem_occurred);
                    // TODO : track connection/server error using Google Analytics(or Crashlytics)
                }
            } else {
                Log.e(TAG, exception.getLocalizedMessage(), exception);
                showFailureMessage(R.string.error_problem_occurred);
                CrashlyticsHelper.sendCaughtException(exception);
            }
        }
    };

    private void showSuccessMessage(String message) {

        mProgressView.setProgressMessage(R.string.message_logged_out);
        Snackbar.make(mDrawerLayout, message, Snackbar.LENGTH_LONG)
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        PreferencesHelper.signOut(MainActivity.this);
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();


                    }
                })
                .setAction("Action", null)
                .show();
    }


    private void showFailureMessage(int messageId) {
        showFailureMessage(getString(messageId));
    }

    /**
     * Show the message and hide the ProgressBar
     *
     * @param message message to show
     */
    private void showFailureMessage(String message) {
        showProgress(false);
        if (ServerResponse.isTokenInvalid(message)) {
            AppUtil.showInvalidTokenSnackBar(mDrawerLayout, message, MainActivity.this);
        } else {
            Snackbar.make(mDrawerLayout, message, Snackbar.LENGTH_SHORT).show();
        }

    }


}
