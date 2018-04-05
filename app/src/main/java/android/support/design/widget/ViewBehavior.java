package android.support.design.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

/**
 * {@link CoordinatorLayout.Behavior} to move the view, which is applied to, in accordance with
 * the {@link Snackbar} like that of {@link FloatingActionButton}
 */
public class ViewBehavior extends CoordinatorLayout.Behavior<View> {
    private Rect mTmpRect;

    public ViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        if (dependency instanceof Snackbar.SnackbarLayout) {
            updateLayoutTranslationForSnackbar(parent, child, dependency);
        }

        return false;
    }

    public void onDependentViewRemoved(CoordinatorLayout parent, View child, View dependency) {
        if (dependency instanceof Snackbar.SnackbarLayout && ViewCompat.getTranslationY(child) != 0.0F) {
            child.animate().translationY(0.0F).scaleX(1.0F).scaleY(1.0F).alpha(1.0F)
                    .setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR)
                    .setListener(null);
        }
    }

    private void updateLayoutTranslationForSnackbar(CoordinatorLayout parent, View child, View snackbar) {
        if (child.getVisibility() == View.VISIBLE) {
            float translationY = getLayoutTranslationYForSnackbar(parent, child);
            child.setTranslationY(translationY);
        }
    }

    private float getLayoutTranslationYForSnackbar(CoordinatorLayout parent, View child) {
        float minOffset = 0.0F;
        List dependencies = parent.getDependencies(child);
        int i = 0;

        for (int z = dependencies.size(); i < z; ++i) {
            View view = (View) dependencies.get(i);
            if (view instanceof Snackbar.SnackbarLayout && parent.doViewsOverlap(child, view)) {
                minOffset = Math.min(minOffset, ViewCompat.getTranslationY(view) - (float) view.getHeight());
            }
        }

        return minOffset;
    }
}
