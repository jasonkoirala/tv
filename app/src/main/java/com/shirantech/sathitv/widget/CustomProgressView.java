package com.shirantech.sathitv.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.shirantech.sathitv.R;

/**
 * Layout for customized {@link android.widget.ProgressBar}. Message can also be shown along with progressBar.
 */
public class CustomProgressView extends FrameLayout {
    private static final int DEFAULT_MESSAGE_COLOR = R.color.primary_text_inverse;
    private CustomFontTextView mTextView;

    private ColorStateList colorStateList;

    public CustomProgressView(Context context) {
        super(context);
        if (!isInEditMode()) {
            initViews(context);
        }
    }

    public CustomProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomProgressView);
        colorStateList = a.getColorStateList(R.styleable.CustomProgressView_message_color);
        a.recycle();

        if (!isInEditMode()) {
            initViews(context);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomProgressView);
        colorStateList = a.getColorStateList(R.styleable.CustomProgressView_message_color);
        a.recycle();

        if (!isInEditMode()) {
            initViews(context);
        }
    }

    private void initViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_progress_view, this);
        mTextView = (CustomFontTextView) findViewById(R.id.tv_progress_message_v1);
        if (null == colorStateList) {
            mTextView.setTextColor(ContextCompat.getColor(getContext(), DEFAULT_MESSAGE_COLOR));
        } else {
            mTextView.setTextColor(colorStateList);
        }
    }

    /**
     * Shows the provided message as progress message in the progress view.
     *
     * @param progressMessage message to show
     */
    public void setProgressMessage(String progressMessage) {
        mTextView.setText(progressMessage);
    }

    /**
     * Shows the provided message as progress message in the progress view.
     *
     * @param progressMessageId Id of string message to show
     */
    public void setProgressMessage(int progressMessageId) {
        mTextView.setText(getContext().getString(progressMessageId));
    }

    /**
     * Hides the ProgressBar
     */
    public void hideProgressBar() {
        findViewById(android.R.id.progress).setVisibility(INVISIBLE);
    }

    /**
     * show the ProgressBar
     */
    public void showProgressBar() {
        findViewById(android.R.id.progress).setVisibility(VISIBLE);
    }
}
