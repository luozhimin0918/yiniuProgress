package com.jyh.kxt.base.utils;

import android.databinding.BindingAdapter;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.custom.RadianDrawable2;

/**
 * Created by Mr'Dai on 2017/5/11.
 * 行情的Util
 */

public class MarketUtil {

    public static int isHighsOrLows(String range) {
        if (range.contains("+")) {
            return 1;
        } else if (range.contains("-")) {
            return -1;
        } else {
            return 0;
        }
    }

    @BindingAdapter("bindingRange")
    public static void bindingRange(TextView tvLabel, String range) {
        int fontColor;

        if (range.contains("+")) {
            fontColor = ContextCompat.getColor(tvLabel.getContext(), R.color.rise_color);

        } else if (range.contains("-")) {
            fontColor = ContextCompat.getColor(tvLabel.getContext(), R.color.decline_color);
        } else {
            fontColor = ContextCompat.getColor(tvLabel.getContext(), R.color.unaltered_color);
        }

        tvLabel.setTextColor(fontColor);
    }


    @BindingAdapter("bindingColorLump")
    public static void bindingColorLump(final TextView tvLabel, final String range) {

        //色块选中时文字 的颜色
        int colorLumpFont = ContextCompat.getColor(tvLabel.getContext(), R.color.shape_bg1);
        final int colorBg;

        if (range.contains("+")) {
            colorBg = ContextCompat.getColor(tvLabel.getContext(), R.color.rise_color);
        } else if (range.contains("-")) {
            colorBg = ContextCompat.getColor(tvLabel.getContext(), R.color.decline_color);
        } else {
            colorBg = ContextCompat.getColor(tvLabel.getContext(), android.R.color.transparent);
        }

        final RadianDrawable2 radianDrawable2 = new RadianDrawable2(tvLabel.getContext(), colorBg, 3);

        tvLabel.setTextColor(colorLumpFont);
        tvLabel.setBackground(radianDrawable2);

        tvLabel.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvLabel.setTextColor(colorBg);
                tvLabel.setBackground(null);
            }
        }, 500);
    }
}
