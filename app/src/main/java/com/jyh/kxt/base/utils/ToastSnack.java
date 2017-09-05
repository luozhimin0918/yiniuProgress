package com.jyh.kxt.base.utils;

import android.content.Context;
import android.view.View;

import com.jyh.kxt.R;
import com.library.util.SystemUtil;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

/**
 * Created by Mr'Dai on 2017/9/5.
 */

public class ToastSnack {
    public static void show(Context mContext, View view,String tip) {
        int statusBarHeight = SystemUtil.getStatuBarHeight(mContext);
        int actionBarHeight = mContext.getResources().getDimensionPixelOffset(R.dimen.actionbar_height);

        TSnackbar.make(view,
                tip,
                TSnackbar.LENGTH_SHORT,
                TSnackbar.APPEAR_FROM_TOP_TO_DOWN)
                .setPromptThemBackground(Prompt.WARNING)
                .setMinHeight(statusBarHeight, actionBarHeight)
                .show();
    }
}
