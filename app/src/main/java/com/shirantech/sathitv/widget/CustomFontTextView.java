package com.shirantech.sathitv.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

import com.shirantech.sathitv.SathiTvApplication;
import com.shirantech.sathitv.helper.FontHelper;

/**
 * A {@link TextView} with customizable font. Currently font is obtained from
 * {@link SathiTvApplication#getCurrentLanguage()}.
 */
public class CustomFontTextView extends AppCompatTextView {
    public CustomFontTextView(Context context) {
        super(context);
        if (!isInEditMode()) {
            setFont(context);
        }
    }

    public CustomFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            setFont(context);
        }
    }

    public CustomFontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            setFont(context);
        }
    }

    private void setFont(Context context) {
        SathiTvApplication application = (SathiTvApplication) ((AppCompatActivity) context).getApplication();
        String fontFilename = application.getCurrentLanguage().getFontName();
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                FontHelper.getFontPath(fontFilename, FontHelper.FONT_TYPE_REGULAR));
        setTypeface(tf);
    }
}
