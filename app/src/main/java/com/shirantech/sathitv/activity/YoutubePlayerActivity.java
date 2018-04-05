package com.shirantech.sathitv.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.network.ApiConstants;
import com.shirantech.sathitv.utils.AppLog;
import com.shirantech.sathitv.utils.AppText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubePlayerActivity extends AppCompatActivity implements YouTubePlayer.OnFullscreenListener, YouTubePlayer.OnInitializedListener {
    private static final String TAG = YoutubePlayerActivity.class.getName();
    private static final String ARG_VIDEO_URL = "video_url";
    private static final int RECOVERY_DIALOG_REQUEST = 1;

    private Toolbar toolbar;
    private FragmentManager fragmentManager;
    private YouTubePlayerSupportFragment youTubePlayerSupportFragment;
    private YouTubePlayer youTubePlayer;
    private String url, video_id = "";;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_youtube_player);
        url = getIntent().getStringExtra(ARG_VIDEO_URL);
        AppLog.showLog(TAG, "on create you tube");
//        setUpToolbar();
        extractYoutubeId(url);
        initializeYoutube();
    }

    public static Intent getStartIntent(Context context, String videoUrl) {
        Intent startIntent = new Intent(context, YoutubePlayerActivity.class);
        startIntent.putExtra(ARG_VIDEO_URL, videoUrl);
        return startIntent;
    }

    /*
    * initialize youtube
    * */
    private void initializeYoutube() {
        youTubePlayerSupportFragment = new YouTubePlayerSupportFragment();
        youTubePlayerSupportFragment.initialize(AppText.DEVELOPER_KEY, this);
        getCurrentFragmentManager().beginTransaction()
                .replace(R.id.youtubeContainer, youTubePlayerSupportFragment).commit();
    }

    private FragmentManager getCurrentFragmentManager() {
        if (fragmentManager == null) {
            fragmentManager = getSupportFragmentManager();
        }
        return fragmentManager;
    }

    private void setUpToolbar() {
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            //noinspection ConstantConditions
            getSupportActionBar().setTitle(getString(R.string.dashboard_entertainment));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }


    @Override
    public void onFullscreen(boolean b) {

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        this.youTubePlayer = youTubePlayer;
        AppLog.showLog(TAG, "youtube initialize success ");
        AppLog.showLog(TAG, "url"+url);
//        youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);
        youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
        youTubePlayer.setOnFullscreenListener(this);
        if (!TextUtils.isEmpty(url)) {
            if (!wasRestored) {
                youTubePlayer.cueVideo(video_id);
            }
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(AppText.ERROR_PLAYER, youTubeInitializationResult.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    /*
    * gets id of youtube video
    * */
    private String extractYoutubeId(String url) {
        if (url != null && url.trim().length() > 0 && url.startsWith("http")) {
            String expression = "^.*((youtu.be" + "\\/)"
                    + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*";
            CharSequence input = url;
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            if (matcher.matches()) {
                String groupIndex1 = matcher.group(7);
                if (groupIndex1 != null && groupIndex1.length() == 11)
                    video_id = groupIndex1;
            }
        }
        AppLog.showLog(TAG, "id :: "+video_id);
        return video_id;
    }
}
