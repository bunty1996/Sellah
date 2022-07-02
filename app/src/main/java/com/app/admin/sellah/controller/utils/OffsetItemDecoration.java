package com.app.admin.sellah.controller.utils;

import android.content.Context;
import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;

public class OffsetItemDecoration extends RecyclerView.ItemDecoration {

    private Context ctx;
    private int offset;

    public OffsetItemDecoration(Context ctx,int offset) {

        this.ctx = ctx;
        this.offset=offset;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        super.getItemOffsets(outRect, view, parent, state);
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if (parent.getChildAdapterPosition(view) == 0) {
            ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).leftMargin = 0;
            setupOutRect(outRect, offset, true);
        } else if (parent.getChildAdapterPosition(view) == state.getItemCount() - 1) {
            ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).rightMargin = 0;
            setupOutRect(outRect, offset, false);
        }
    }

    private void setupOutRect(Rect rect, int offset, boolean start) {

        if (start) {
            rect.left = offset;
        } else {
            rect.right = offset;
        }
    }

}
