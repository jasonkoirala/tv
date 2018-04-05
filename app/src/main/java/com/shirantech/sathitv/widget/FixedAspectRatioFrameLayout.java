package com.shirantech.sathitv.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.shirantech.sathitv.R;

/**
 * A {@link FrameLayout} with aspect ratio 16:9
 */
public class FixedAspectRatioFrameLayout extends FrameLayout {
    private static final int DEFAULT_RATIO_WIDTH = 13;
    private static final int DEFAULT_RATIO_HEIGHT = 7;

    private int ratioWidth, ratioHeight;

    public FixedAspectRatioFrameLayout(Context context) {
        super(context);
    }

    public FixedAspectRatioFrameLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FixedAspectRatioFrameLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainAspectRatio(context, attrs, defStyleAttr, 0);
    }

    public FixedAspectRatioFrameLayout(
            Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        obtainAspectRatio(context, attrs, defStyleAttr, defStyleRes);
    }

    private void obtainAspectRatio(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.FixedAspectRatioFrameLayout, defStyleAttr, defStyleRes);

        ratioWidth = a.getInt(R.styleable.FixedAspectRatioFrameLayout_ratio_width, DEFAULT_RATIO_WIDTH);
        ratioHeight = a.getInt(R.styleable.FixedAspectRatioFrameLayout_ratio_height, DEFAULT_RATIO_HEIGHT);

        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int originalWidth = MeasureSpec.getSize(widthMeasureSpec);
        final int originalHeight = MeasureSpec.getSize(heightMeasureSpec);

        int calculatedWidth, calculatedHeight;
        if (originalWidth > originalHeight) {
            calculatedWidth = originalWidth;
            calculatedHeight = originalWidth * ratioHeight / ratioWidth;
        } else {
            calculatedWidth = originalHeight * ratioWidth / ratioHeight;
            calculatedHeight = originalHeight;
        }

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(calculatedWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(calculatedHeight, MeasureSpec.EXACTLY));
    }

    /**
     * Set the aspect ratio for the FrameLayout.
     *
     * @param ratioWidth  width part of aspect ratio(eg. 16 in 16:9)
     * @param ratioHeight height part of aspect ratio(eg. 9 in 16:9)
     */
    public void setAspectRatio(final int ratioWidth, final int ratioHeight) {
        this.ratioWidth = ratioWidth;
        this.ratioHeight = ratioHeight;
        this.requestLayout();
    }

    /**
     * Get the width part of current aspect ratio.
     *
     * @return ratio width
     */
    public int getRatioWidth() {
        return ratioWidth;
    }

    /**
     * Get the height part of current aspect ratio.
     *
     * @return ratio height
     */
    public int getRatioHeight() {
        return ratioHeight;
    }
}
