package com.jyh.kxt.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Mr'Dai on 2017/8/28.
 */

public class SelectedImageView extends ImageView {

    public interface OnSelectedListener {
        void onSelectedUpdate(boolean selected);
    }

    private OnSelectedListener onSelectedListener;

    public SelectedImageView(Context context) {
        super(context);
    }

    public SelectedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        this.onSelectedListener = onSelectedListener;
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (onSelectedListener != null) {
            onSelectedListener.onSelectedUpdate(selected);
        }
    }
}
