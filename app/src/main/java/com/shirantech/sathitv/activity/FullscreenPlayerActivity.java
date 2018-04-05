package com.shirantech.sathitv.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.MediaController;

import com.shirantech.sathitv.R;

import com.shirantech.sathitv.utils.AppLog;
import com.shirantech.sathitv.widget.CustomProgressView;
import com.shirantech.sathitv.widget.VideoSurfaceView;

import java.io.IOException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenPlayerActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnVideoSizeChangedListener, MediaController.MediaPlayerControl {
    private static final String TAG = "Full Screen Player";
    private static final String ARG_VIDEO_URL = "video_url";

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private FrameLayout mMainLayout;
    private VideoSurfaceView mVideoSurfaceView;
    private CustomProgressView mProgressView;
    private MediaPlayer mMediaPlayer;
    private SurfaceHolder mSurfaceHolder;
    private MediaController mMediaController;
    private String mVideoUrl;
    private boolean mVisible;
    private int mVideoWidth;
    private int mVideoHeight;
    private boolean mVideoSizeKnown = false;
    private boolean mVideoReadyToBePlayed = false;


    /**
     * Get the {@link Intent} to start this activity
     *
     * @param context  context to initialize the Intent.
     * @param videoUrl url of video to play
     * @return the Intent to start {@link FullscreenPlayerActivity}
     */
    public static Intent getStartIntent(Context context, String videoUrl) {
        Intent startIntent = new Intent(context, FullscreenPlayerActivity.class);
        startIntent.putExtra(ARG_VIDEO_URL, videoUrl);
        return startIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppLog.showLog(TAG, "full screen play");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_fullscreen_player);

        mVideoUrl = getIntent().getStringExtra(ARG_VIDEO_URL);
        mVisible = true;

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initViews();
    }



    private void initViews() {
        mMainLayout = (FrameLayout) findViewById(R.id.main_layout);
        mVideoSurfaceView = (VideoSurfaceView) findViewById(R.id.video_surfaceView);
        // Set up the user interaction to manually show or hide the system UI.
        mVideoSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        mSurfaceHolder = mVideoSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);

        mProgressView = (CustomProgressView) findViewById(R.id.progress_view);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        if (null != mMediaController) {
            mMediaController.show();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mVideoSurfaceView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mVideoSurfaceView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            if (null != mMediaController) {
                mMediaController.hide();
            }
        }
    };

    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    // Implement SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        AppLog.showLog(TAG, "play video "+ holder);
        playVideo();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    // End SurfaceHolder.Callback

    private void playVideo() {
        doCleanUp();

        showBufferingMessage();
        try {
            mMediaPlayer = new MediaPlayer();
            AppLog.showLog(TAG, "url :: "+Uri.parse(mVideoUrl));
            //TODO null url
            mMediaPlayer.setDataSource(FullscreenPlayerActivity.this, Uri.parse(mVideoUrl));
            mMediaPlayer.setDisplay(mSurfaceHolder);
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        if (width == 0 || height == 0) {
            return;
        }
        mVideoSizeKnown = true;
        mVideoWidth = width;
        mVideoHeight = height;
        if (mVideoReadyToBePlayed) {
            startVideoPlayBack();
        }
    }

    // Implement MediaPlayer.OnPreparedListener
    @Override
    public void onPrepared(MediaPlayer mp) {
        hideBufferingMessage();

        attachMediaController();
        mVideoReadyToBePlayed = true;
        if (mVideoSizeKnown) {
            mMediaPlayer.start();
        }
    }
    // End MediaPlayer.OnPreparedListener

    private void attachMediaController() {
        mMediaController = new MediaController(FullscreenPlayerActivity.this);
        mMediaController.setMediaPlayer(this);
        mMediaController.setAnchorView(mMainLayout);
    }

    private void showBufferingMessage() {
        mProgressView.setVisibility(View.VISIBLE);
        mProgressView.setProgressMessage(R.string.message_stream_buffering);
    }

    private void hideBufferingMessage() {
        mProgressView.setVisibility(View.GONE);
    }

    /**
     * Update the video size with the updated values to fit screen maintaining the aspect ratio
     */
    private void updateVideoSize() {
        mVideoSurfaceView.changeVideoSize(mVideoWidth, mVideoHeight, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pause();
    }

    @Override
    protected void onDestroy() {
        releaseMediaPlayer();
        doCleanUp();
        super.onDestroy();
    }

    private void releaseMediaPlayer() {
        if (mMediaPlayer.isPlaying())
            mMediaPlayer.stop();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /**
     * Reset video play conditions
     */
    private void doCleanUp() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        mVideoReadyToBePlayed = false;
        mVideoSizeKnown = false;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.d(TAG, "onBufferingUpdate percent:" + percent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "onCompletion called");
    }

    private void startVideoPlayBack() {
        updateVideoSize();
        mMediaPlayer.start();
    }

    // Implement MediaPlayerControl
    @Override
    public void start() {
        if (null != mMediaPlayer) {
            mMediaPlayer.start();
        }
    }

    @Override
    public void pause() {
        if (null != mMediaPlayer) {
            mMediaPlayer.pause();
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mMediaPlayer) {
            mMediaPlayer.pause();        }
    }

    @Override
    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int position) {
        mMediaPlayer.seekTo(position);
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
    // End MediaPlayerControl


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//            Toast.makeText(this, "Potrait ", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
