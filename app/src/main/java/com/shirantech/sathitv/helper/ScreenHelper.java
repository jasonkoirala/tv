package com.shirantech.sathitv.helper;

import android.content.Context;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

import com.shirantech.sathitv.R;

/**
 * Helper for screen related task
 */
public class ScreenHelper {

    /**
     * Get the drawable to set background in Splash, Login & register screens.
     * <p>
     * There are some issues in using xml gradient in pre-lollipop devices and lollipop+ devices.
     * So use this method instead to set background with radial gradient.
     * </p>
     *
     * @param context context to use.
     * @return the drawable to set
     */
    public static Drawable getSplashBg(final Context context) {
        final int width = getScreenWidth(context);
        final int height = getScreenHeight(context);

        final ShapeDrawable mDrawable = new ShapeDrawable(new RectShape());
        mDrawable.getPaint().setShader(new RadialGradient(width / 2, height / 2, 6 * width / 10,
                context.getResources().getColor(R.color.transparent_black), context.getResources().getColor(R.color.black)/*Color.parseColor("#393838")*/, Shader.TileMode.CLAMP));
        return mDrawable;
    }

    /**
     * Get the screen width
     * @param context context to use
     * @return the screen width
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * Get the screen height
     * @param context context to use
     * @return the screen height
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
}
