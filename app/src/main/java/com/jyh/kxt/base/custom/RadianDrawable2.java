package com.jyh.kxt.base.custom;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;

import com.library.util.SystemUtil;

/**
 * Created by Mr'Dai on 2017/5/11.
 */

public class RadianDrawable2 extends GradientDrawable {

    public RadianDrawable2(Context mContext, int mColor, int radiusSize) {

        int dottedSize = SystemUtil.dp2px(mContext, radiusSize);
        setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        setColor(mColor);
        setCornerRadius(dottedSize);
    }
}
