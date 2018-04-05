package com.shirantech.sathitv.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceView;

import com.shirantech.sathitv.R;
import com.shirantech.sathitv.helper.ScreenHelper;

/**
 * Customized {@link SurfaceView} to maintain screen aspect ratio.
 */
public class VideoSurfaceView extends SurfaceView {
    private static final int VIDEO_WIDTH_DEFAULT = 720;
    private static final int VIDEO_HEIGHT_DEFAULT = 480;

    /**
     * Video dimens
     */
    private int mVideoWidth, mVideoHeight;
    /**
     * Screen dimens
     */
    private int mScreenWidth, mScreenHeight;
    /**
     * Required dimens to show video by fitting in the screen
     */
    private int mRequiredWidth, mRequiredHeight;

    /**
     * Screen width to height ratio
     */
    private float mScreenRatio;
    /**
     * Original video width to height ratio
     */
    private float mVideoRatio;

    /**
     * Whether the video is being played in landscape mode or portrait mode.
     */
    private boolean mFullScreen = false;

    public VideoSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init(attrs);
        }
    }

    public VideoSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            init(attrs);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VideoSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if (!isInEditMode()) {
            init(attrs);
        }
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.VideoSurfaceView);
        mVideoWidth = a.getInt(R.styleable.VideoSurfaceView_video_width, VIDEO_WIDTH_DEFAULT);
        mVideoHeight = a.getInt(R.styleable.VideoSurfaceView_video_height, VIDEO_HEIGHT_DEFAULT);
        a.recycle();
        mVideoRatio = (float) mVideoWidth / mVideoHeight;

        obtainScreenSize();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        obtainRequiredSize();

        // must set this at the end
        setMeasuredDimension(mRequiredWidth, mRequiredHeight);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void obtainRequiredSize() {
        if (mVideoWidth > 0 && mVideoHeight > 0) {
            if (mFullScreen) {
                // the screen is in landscape mode so width of screen will be the height in landscape
                // and height of screen will be the width
                if (mVideoRatio < mScreenRatio) {
                    // video is shorter compared to screen
                    mRequiredWidth = mScreenHeight;
                    mRequiredHeight = (int) (mRequiredWidth / mVideoRatio);
                } else if (mVideoRatio > mScreenRatio) {
                    // video is taller compared to screen
                    mRequiredHeight = mScreenWidth;
                    mRequiredWidth = (int) (mRequiredHeight * mVideoRatio);
                } else {
                    // video and screen have same aspect ratio so set video size to that of screen
                    mRequiredWidth = mScreenHeight;
                    mRequiredHeight = mScreenWidth;
                }
            } else {
                // the screen is in portrait mode
                mRequiredWidth = mScreenWidth;
                mRequiredHeight = (int) (mRequiredWidth / mVideoRatio);
            }
        } else {
            // fallback to screen dimens
            mRequiredWidth = mScreenWidth;
            mRequiredHeight = mScreenHeight;
        }
    }

    /**
     * Change the video size with the provided dimensions
     *
     * @param width  the width of the video
     * @param height the height of the video
     */

    public void changeVideoSize(int width, int height, boolean fullScreen) {
        mVideoWidth = width;
        mVideoHeight = height;
        mVideoRatio = (float) mVideoWidth / mVideoHeight;
        mFullScreen = fullScreen;

        // not sure whether it is useful or not but safe to do so
        getHolder().setFixedSize(width, height);

        requestLayout();
        invalidate();     // very important, so that onMeasure will be triggered
    }


    /**
     * Obtain the screen size disregarding the orientation, i.e taking longer side as height and
     * shorter side as width, to make calculations easier
     */
    private void obtainScreenSize() {
        if (Configuration.ORIENTATION_LANDSCAPE == getResources().getConfiguration().orientation) {
            mScreenWidth = ScreenHelper.getScreenHeight(getContext());
            mScreenHeight = ScreenHelper.getScreenWidth(getContext());
        } else {
            mScreenWidth = ScreenHelper.getScreenWidth(getContext());
            mScreenHeight = ScreenHelper.getScreenHeight(getContext());
        }
        mScreenRatio = (float) mScreenWidth / mScreenHeight;
    }
}
