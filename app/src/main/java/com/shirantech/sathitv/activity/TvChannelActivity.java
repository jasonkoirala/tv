package com.shirantech.sathitv.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.adapter.ChannelListAdapter;
import com.shirantech.sathitv.model.TvChannel;
import com.shirantech.sathitv.utils.AppLog;
import com.shirantech.sathitv.utils.AppUtil;
import com.shirantech.sathitv.widget.CustomFontTextView;
import com.shirantech.sathitv.widget.CustomProgressView;
import com.shirantech.sathitv.widget.RecyclerViewItemDecoration;
import com.shirantech.sathitv.widget.VideoControllerView;
import com.shirantech.sathitv.widget.VideoSurfaceView;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * {@link android.app.Activity} for playing a tv channel.
 */
public class TvChannelActivity extends BaseActivity implements SurfaceHolder.Callback,
        View.OnTouchListener, MediaPlayer.OnPreparedListener, VideoControllerView.MediaPlayerControl,
        MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnVideoSizeChangedListener, MediaPlayer.OnErrorListener,
        View.OnClickListener {
    private static final String TAG = "TV Channel";
    private static final String ARG_CURRENT_CHANNEL = "current_channel";
    private static final String ARG_CURRENT_CHANNEL_POSITION = "current_channel_position";

    private FrameLayout mVideoSurfaceContainer;
    private VideoSurfaceView mVideoSurfaceView;
    private CustomProgressView mProgressView;
    private CustomFontTextView textViewOtherChannels;
    private RecyclerView recycleViewChannel;

    private SurfaceHolder mSurfaceHolder;
    private MediaPlayer mMediaPlayer;
    private VideoControllerView mVideoController;
    private List<TvChannel> channelList;
    private TvChannel mCurrentChannel;

    private boolean mFullScreen = false;

    private int defaultVideoWidth;
    private int defaultVideoHeight;
    private int mVideoWidth;
    private int mVideoHeight;
    private boolean mVideoSizeKnown = false;
    private boolean mVideoReadyToBePlayed = false;
    private int position;

    /**
     * Get the {@link Intent} to start this activity
     * list converted into string with gson
     */
    public static Intent getStartIntent(Context context, List<TvChannel> tvChannelList, int position) {
        Intent startIntent = new Intent(context, TvChannelActivity.class);
        Gson gson = new Gson();
        startIntent.putExtra(ARG_CURRENT_CHANNEL, gson.toJson(tvChannelList));
        startIntent.putExtra(ARG_CURRENT_CHANNEL_POSITION, position);
        return startIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_channel);
        overridePendingTransition(0, 0);
        channelList = getArgData(getIntent());
        mCurrentChannel = channelList.get(position);
        AppLog.showLog(TAG, "channellsit ::" + channelList.size());
        setupToolbar();
        initViews();
        showOtherChannels();
        AppLog.showLog(TAG, "orientation ::" + getResources().getConfiguration().orientation);
        defaultVideoWidth = getResources().getInteger(R.integer.default_video_width);
        defaultVideoHeight = getResources().getInteger(R.integer.default_video_height);
    }

    /**
     * Get the channelList and position passed from the intent
     * String converted into list and returned using gson
     */
    private List<TvChannel> getArgData(Intent intent) {
        position = intent.getIntExtra(ARG_CURRENT_CHANNEL_POSITION, 0);
        Type listType = new TypeToken<List<TvChannel>>() {
        }.getType();
        final String tvChannelJson = intent.getStringExtra(ARG_CURRENT_CHANNEL);
        return new Gson().fromJson(tvChannelJson, listType);
    }

    /**
     * Initialize toolbar
     */
    private void setupToolbar() {
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            //noinspection ConstantConditions
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(mCurrentChannel.getName());
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    /**
     * Initialize views
     */
    private void initViews() {
        mVideoSurfaceContainer = (FrameLayout) findViewById(R.id.video_surface_container);
        mVideoSurfaceView = (VideoSurfaceView) findViewById(R.id.video_surfaceView);
        mVideoSurfaceView.setOnTouchListener(this);
        mSurfaceHolder = mVideoSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mProgressView = (CustomProgressView) findViewById(R.id.progress_view);
        textViewOtherChannels = (CustomFontTextView) findViewById(R.id.textViewOtherChannels);
        recycleViewChannel = (RecyclerView) findViewById(R.id.recycleViewChannel);
    }

    /**
     * Show the all channel list
     */
    private void showOtherChannels() {
        final int spanCount = getResources().getInteger(R.integer.photo_grid_tv_span_count);
        final int itemSpacing = (int) getResources().getDimension(R.dimen.spacing_normal);
        recycleViewChannel.setHasFixedSize(true);
        recycleViewChannel.setLayoutManager(new GridLayoutManager(TvChannelActivity.this, spanCount));
        recycleViewChannel.addItemDecoration(new RecyclerViewItemDecoration(itemSpacing));
        ChannelListAdapter adapter = new ChannelListAdapter(channelList, this);
        recycleViewChannel.setAdapter(adapter);
    }


    // Implement SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            playVideo(mCurrentChannel.getStreamingUrl());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    // End SurfaceHolder.Callback


    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        // TODO : show buffering message when loading
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

    // Implement MediaPlayer.OnPreparedListenerException e
    @Override
    public void onPrepared(MediaPlayer mp) {
        AppLog.showLog(TAG, "");
        hideBufferingMessage();
        assignMediaController();
        mVideoReadyToBePlayed = true;
        if (mVideoSizeKnown) {
            if (null != mMediaPlayer) {
                mMediaPlayer.start();
            }
        }
    }
    // End MediaPlayer.OnPreparedListener

    /**
     * Initialize the video controller and set to the media player.
     */
    private void assignMediaController() {
        mVideoController = new VideoControllerView(TvChannelActivity.this);
        mVideoController.setMediaPlayer(this);
        mVideoController.setAnchorView(mVideoSurfaceContainer);
    }


    /**
     * Handle error when media player interrupted due to no  internet connection
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        AppLog.showLog(TAG, " what :: " + what + "extra :: " + extra);
        if (-38 == extra) {
            AppLog.showLog(TAG, " extra :: " + extra);

            mProgressView.setVisibility(View.VISIBLE);
            mProgressView.setProgressMessage(R.string.error_network_connection);
            releaseMediaPlayer();
        } else {
            showChannelError();
        }
        return false;
    }

    /**
     * Show error if channel cannot be played
     */
    private void showChannelError() {
        mVideoSurfaceView.setVisibility(View.GONE);
        mProgressView.hideProgressBar();
        mProgressView.setProgressMessage(R.string.message_channel_could_not_be_played);
    }

    private void showProgressView() {
        mVideoSurfaceView.setVisibility(View.VISIBLE);
        mProgressView.showProgressBar();
        mProgressView.setProgressMessage(R.string.message_stream_buffering);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
       /* if (null != mVideoController) {
            mVideoController.show();
        }*/
        return false;
    }

    /*@Override
  public void onBackPressed() {
  if (isFullScreen()) {
  toggleFullScreen();
  } else {
  super.onBackPressed();
  }
  }*/


    @Override
    protected void onResume() {
        super.onResume();
        if (Configuration.ORIENTATION_LANDSCAPE == getResources().getConfiguration().orientation) {
            mFullScreen = true;
            switchFullScreen();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pause();
        releaseMediaPlayer();
        doCleanUp();
    }

    @Override
    protected void onDestroy() {
        // TODO : ui freezes when releasing media player in 4.4 and above
        // workaround needed to tackle the problem.
        // not releasing mediaplayer for now
        releaseMediaPlayer();
        doCleanUp();
        super.onDestroy();
    }

    /**
     * Update the video size with the updated values to fit screen maintaining the aspect ratio
     */
    private void updateVideoSize() {
        mVideoSurfaceView.changeVideoSize(mVideoWidth, mVideoHeight, mFullScreen);
    }

    private void startVideoPlayBack() {
        updateVideoSize();
        mMediaPlayer.start();
    }

    private void showBufferingMessage() {
        AppLog.showLog(TAG, "streaming buffering");
        mProgressView.setVisibility(View.VISIBLE);
        mProgressView.setProgressMessage(R.string.message_stream_buffering);
    }

    private void hideBufferingMessage() {
        mProgressView.setVisibility(View.GONE);
    }

    /**
     * Reset video play conditions
     */
    private void doCleanUp() {
        mVideoWidth = defaultVideoWidth;
        mVideoHeight = defaultVideoHeight;
        mVideoReadyToBePlayed = false;
        mVideoSizeKnown = false;
    }

    /**
     * Release the media player
     */
    private void releaseMediaPlayer() {
        AppLog.showLog(TAG, "release media player");
        if (null != mMediaPlayer) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }

            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    // Implement VideoMediaController.MediaPlayerControl
    @Override
    public void start() {
        if (null != mMediaPlayer) {
            mMediaPlayer.start();
        }
    }

    @Override
    public void pause() {
        if (null != mMediaPlayer && mMediaPlayer.isPlaying()) {
            releaseMediaPlayer();
            doCleanUp();
        }
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
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public boolean isFullScreen() {
        return mFullScreen;
    }

    @Override
    public void toggleFullScreen() {
        /*mFullScreen = !mFullScreen;
        switchFullScreen();*/
    }
    // End VideoMediaController.MediaPlayerControl


    /**
     * Switch to portrait or fullscreen based on {@link #isFullScreen()}
     */
    private void switchFullScreen() {
        if (isFullScreen()) {
            getSupportActionBar().hide();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            updateVideoSize();
        } else {
            getSupportActionBar().show();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            updateVideoSize();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (Configuration.ORIENTATION_LANDSCAPE == getResources().getConfiguration().orientation) {
            mFullScreen = true;
            switchFullScreen();
        } else {
            mFullScreen = false;
            switchFullScreen();
        }
    }


    @Override
    public void onClick(View clickedView) {
        AppLog.showLog(TAG, "other channels clicked");
        if (AppUtil.isInternetConnected(TvChannelActivity.this)) {
            if (R.id.photo_view == clickedView.getId()) {
                final int clickedPosition = (int) clickedView.getTag();
                if (channelList.get(clickedPosition).isFree()) {
                    showProgressView();
                    position = clickedPosition;
                    mCurrentChannel = channelList.get(clickedPosition);
                    if (null != mMediaPlayer) {
                        mMediaPlayer.reset();
                    }
                    mVideoSurfaceView.setVisibility(View.VISIBLE);
                    AppLog.showLog(TAG, "position ::" + channelList.get(clickedPosition));
                    AppLog.showLog(TAG, "streaming url ::" + channelList.get(clickedPosition).getStreamingUrl());
                    getSupportActionBar().setTitle(channelList.get(clickedPosition).getName());
                    playVideo(channelList.get(clickedPosition).getStreamingUrl());
                } else {
                    showSubscriptionMessage();
                }
            }
        } else {
            Snackbar.make(recycleViewChannel, getResources().getString(R.string.error_network_connection),
                    Snackbar.LENGTH_SHORT).show();
        }
    }


    private void playVideo(String streamingUrl) {
        doCleanUp();
        mVideoSurfaceView.setVisibility(View.VISIBLE);
        try {
            showBufferingMessage();
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mMediaPlayer.setDataSource(TvChannelActivity.this, Uri.parse(streamingUrl));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setDisplay(mSurfaceHolder);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.prepareAsync();

        } catch (IllegalArgumentException | SecurityException | IllegalStateException e) {
            e.printStackTrace();
            AppLog.showLog(TAG, "url :: " + streamingUrl);
            showChannelError();
        }
    }


    /**
     * Show subscription required message
     */
    private void showSubscriptionMessage() {
//        mProgressView.hideProgressBar();
        new AlertDialog.Builder(TvChannelActivity.this)
                .setTitle(R.string.subscription_alert_title)
                .setMessage(R.string.subscription_alert_message)
                .setPositiveButton(R.string.prompt_ok, null)
                .show();
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

}