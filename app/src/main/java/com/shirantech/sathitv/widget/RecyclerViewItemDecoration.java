package com.shirantech.sathitv.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Decorator for managing space among items in the RecyclerView(can be used in list, grid, staggered grid).
 */
public class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration {
    private final int halfSpace;

    public RecyclerViewItemDecoration(int spacing) {
        this.halfSpace = spacing / 2;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getPaddingLeft() != halfSpace) {
            parent.setPadding(halfSpace, 0, halfSpace, 0);
            parent.setClipToPadding(false);
        }

        outRect.top = halfSpace;
        outRect.bottom = halfSpace;
        outRect.left = halfSpace;
        outRect.right = halfSpace;
    }
}
