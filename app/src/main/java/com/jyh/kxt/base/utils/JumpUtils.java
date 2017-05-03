package com.jyh.kxt.base.utils;

import com.jyh.kxt.base.BaseActivity;
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
                    break;
                case VarConstant.OCLASS_VIDEO:
                    break;
            }
        } else {
            //网页跳转
        }
    }
}
