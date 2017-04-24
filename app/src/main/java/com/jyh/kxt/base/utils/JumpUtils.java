package com.jyh.kxt.base.utils;

import android.content.Intent;
import android.text.TextUtils;

import com.library.base.http.VarConstant;
import com.jyh.kxt.index.ui.MainActivity;
import com.jyh.kxt.index.ui.WebActivity;
import com.jyh.kxt.main.json.SlideJson;

/**
 * 项目名:Kxt
 * 类描述:跳转工具类
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/19.
 */

public class JumpUtils {

    /**
     * 跳转
     *
     * @param slideJson
     */
    public static void jump(final MainActivity context, SlideJson slideJson) {
        if (slideJson == null || context == null) return;
        if (TextUtils.isEmpty(slideJson.getHref())) {

            String o_class = slideJson.getO_class();
            String o_action = slideJson.getO_action();
            String o_id = slideJson.getO_id();

            switch (o_class) {
                case VarConstant.OCLASS_DATA:
                    //数据
                    context.rbDatum.performClick();
                    context.rbDatum.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            context.datumFragment.onTabSelect(1);
                        }
                    }, 500);
                    break;
                case VarConstant.OCLASS_FLASH:
                    //快讯
                    context.rbHome.performClick();
                    break;
                case VarConstant.OCLASS_NEWS:
                    //要闻
                    context.rbHome.performClick();
                    break;
                case VarConstant.OCLASS_QUOTES:
                    //行情
                    context.rbMarket.performClick();
                    break;
                case VarConstant.OCLASS_RL:
                    //日历
                    context.rbDatum.performClick();
                    break;
                case VarConstant.OCLASS_VIDEO:
                    //视听
                    context.rbAudioVisual.performClick();
                    break;
            }
        } else {
            Intent intent = new Intent(context, WebActivity.class);
            context.startActivity(intent);
        }
    }
}
