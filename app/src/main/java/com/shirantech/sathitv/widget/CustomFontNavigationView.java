package com.shirantech.sathitv.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.NavigationView;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;

import com.shirantech.sathitv.helper.FontHelper;

/**
 * A {@link NavigationView} with customizable font. Font can be set using {@link #setFont(String)}
 */
public class CustomFontNavigationView extends NavigationView {

    public CustomFontNavigationView(Context context) {
        super(context);
    }

    public CustomFontNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomFontNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Set the font to the items in the {@link NavigationView}
     *
     * @param fontName font name to set
     */
    public void setFont(String fontName) {
        final Menu menu = getMenu();
        for (int i = 0; i < menu.size(); i++) {
            applyFontToMenuItem(menu.getItem(i), fontName);
        }
    }

    private void applyFontToMenuItem(MenuItem menuItem, String fontFilename) {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                FontHelper.getFontPath(fontFilename, FontHelper.FONT_TYPE_REGULAR));
        SpannableString menuTitle = new SpannableString(menuItem.getTitle());
        menuTitle.setSpan(new CustomTypefaceSpan("", tf), 0, menuTitle.length(),
                SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
    }

}
