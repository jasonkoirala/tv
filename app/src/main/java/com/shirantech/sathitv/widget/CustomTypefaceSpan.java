package com.shirantech.sathitv.widget;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

/**
 * Custom {@link TypefaceSpan} for font customization.
 */
public class CustomTypefaceSpan extends TypefaceSpan {

    private final Typeface newTypeface;

    public CustomTypefaceSpan(String family, Typeface typeface) {
        super(family);
        newTypeface = typeface;
    }

    @Override
    public void updateDrawState(@NonNull TextPaint textPaint) {
        applyCustomTypeFace(textPaint, newTypeface);
    }

    @Override
    public void updateMeasureState(@NonNull TextPaint paint) {
        applyCustomTypeFace(paint, newTypeface);
    }

    private static void applyCustomTypeFace(Paint paint, Typeface typeface) {
        paint.setTypeface(typeface);
    }
}
