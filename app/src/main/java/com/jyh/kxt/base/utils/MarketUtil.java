package com.jyh.kxt.base.utils;

import android.databinding.BindingAdapter;
import android.graphics.Color;
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
    public static void bindingColorLump(final TextView tvLabel, int bgGlint) {
        final int colorBg;

        switch (bgGlint) {
            case 0: //表示不执行任何操作
                colorBg = Color.TRANSPARENT;
                break;
            case 1: //上涨  调红_闪烁
                colorBg = ContextCompat.getColor(tvLabel.getContext(), R.color.rise_color);
                break;
            case 2: //下跌  调绿_闪烁
                colorBg = ContextCompat.getColor(tvLabel.getContext(), R.color.decline_color);
                break;
            default:
                colorBg = Color.TRANSPARENT;
                break;
        }

        RadianDrawable2 radianDrawable = new RadianDrawable2(tvLabel.getContext(), colorBg, 3);

        tvLabel.setTextColor(Color.WHITE);
        tvLabel.setBackground(radianDrawable);

        tvLabel.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvLabel.setTextColor(colorBg);
                tvLabel.setBackground(null);
            }
        }, 500);
    }
}
