package com.shirantech.sathitv.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Frame for holding youtube thumbnail of ratio 16:9
 * <br>
 * Created by samir on 12/26/14.
 */
public class FixedAspectRatioFrameLayout43 extends FrameLayout {
    public FixedAspectRatioFrameLayout43(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int originalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int calculatedHeight = originalWidth * 9 / 16;

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(originalWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(calculatedHeight, MeasureSpec.EXACTLY));
    }
}
