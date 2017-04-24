package com.jyh.kxt.base.custom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;

import com.jyh.kxt.R;


/**
 * Created by Mr'Dai on 2017/4/24.
 */

public class RadianDrawable extends GradientDrawable {

    private int btnStrokeWidth1;
    private Context mContext;

    public RadianDrawable(Context mContext) {
        this.mContext = mContext;

        btnStrokeWidth1 = mContext.getResources().getDimensionPixelOffset(R.dimen.btn_stroke_width1);
        int btnCornerRadius1 = mContext.getResources().getDimensionPixelOffset(R.dimen.btn_corner_radius1);


        setOrientation(Orientation.LEFT_RIGHT);
        setColor(Color.WHITE);

        setCornerRadius(btnCornerRadius1);
        setStroke(R.color.line_color);
    }

    public void setStroke(int colorRes) {
        int lineStrokeColor = ContextCompat.getColor(mContext, colorRes);
        super.setStroke(btnStrokeWidth1, lineStrokeColor);
    }
}
