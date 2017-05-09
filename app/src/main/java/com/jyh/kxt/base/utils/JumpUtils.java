package com.jyh.kxt.base.utils;

import android.content.Intent;

import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.index.ui.MainActivity;
import com.jyh.kxt.index.ui.SearchActivity;
import com.jyh.kxt.index.ui.WebActivity;
import com.library.base.http.VarConstant;
import com.jyh.kxt.base.json.JumpJson;

/**
 * 项目名:Kxt
 * 类描述:跳转工具类
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/19.
 */

public class JumpUtils {


    public static void jump(BaseActivity activity, String o_class, String o_action, String o_id, String url) {
        JumpJson jumpJson = new JumpJson(o_action, o_class, o_id);
        jump(activity, jumpJson, url);
    }

    /**
     * 跳转
     *
     * @param context
     * @param jumpJson
     * @param url
     */
    public static void jump(final BaseActivity context, JumpJson jumpJson, String url) {
        if (context == null) return;
        if (url == null) {
            if (jumpJson == null) return;
            String o_class = jumpJson.o_class;
            if (o_class == null) return;
            if (context instanceof MainActivity) {
                final MainActivity mainActivity = (MainActivity) context;
                switch (o_class) {
                    case VarConstant.OCLASS_DATA:
                        //数据
                        mainActivity.rbDatum.performClick();
                        mainActivity.rbDatum.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mainActivity.datumFragment.onTabSelect(1);
                            }
                        }, 500);
                        break;
                    case VarConstant.OCLASS_ARTICLE:
                        break;
                    case VarConstant.OCLASS_DATING:
                        break;
                    case VarConstant.OCLASS_DIANPING:

                        break;
                    case VarConstant.OCLASS_FLASH:
                        //快讯
                        mainActivity.rbHome.performClick();
                        mainActivity.homeFragment.onTabSelect(1);
                        break;
                    case VarConstant.OCLASS_INDEX:
                        //首页
                        mainActivity.rbHome.performClick();
                        break;
                    case VarConstant.OCLASS_NEWS:
                        //要闻
                        mainActivity.rbHome.performClick();
                        break;
                    case VarConstant.OCLASS_QUOTES:
                        //行情
                        mainActivity.rbMarket.performClick();
                        break;
                    case VarConstant.OCLASS_RL:
                        //日历
                        mainActivity.rbDatum.performClick();
                        break;
                    case VarConstant.OCLASS_SCHOOL:
                        break;
                    case VarConstant.OCLASS_SEARCH:
                        //搜索
                        Intent searchIntent = new Intent(mainActivity, SearchActivity.class);
                        //搜索关键字
                        searchIntent.putExtra(IntentConstant.SEARCH_KEY, jumpJson.o_id);
                        mainActivity.startActivity(searchIntent);
                        break;
                    case VarConstant.OCLASS_VIDEO:
                        //视听
                        mainActivity.rbAudioVisual.performClick();
                        break;
                }
            } else {
                switch (o_class) {
                    case VarConstant.OCLASS_DATA:
                        break;
                    case VarConstant.OCLASS_ARTICLE:
                        break;
                    case VarConstant.OCLASS_DATING:
                        break;
                    case VarConstant.OCLASS_DIANPING:
                        break;
                    case VarConstant.OCLASS_FLASH:
                        break;
                    case VarConstant.OCLASS_INDEX:
                        break;
                    case VarConstant.OCLASS_NEWS:
                        break;
                    case VarConstant.OCLASS_QUOTES:
                        break;
                    case VarConstant.OCLASS_RL:
                        break;
                    case VarConstant.OCLASS_SCHOOL:
                        break;
                    case VarConstant.OCLASS_SEARCH:
                        //搜索
                        Intent searchIntent = new Intent(context, SearchActivity.class);
                        //搜索关键字
                        searchIntent.putExtra(IntentConstant.SEARCH_KEY, jumpJson.o_id);
                        context.startActivity(searchIntent);
                        break;
                    case VarConstant.OCLASS_VIDEO:
                        break;
                }
            }
        } else {
            //网页跳转
            Intent intent = new Intent(context, WebActivity.class);
            intent.putExtra(IntentConstant.WEBURL, url);
            context.startActivity(intent);
        }
    }
}
