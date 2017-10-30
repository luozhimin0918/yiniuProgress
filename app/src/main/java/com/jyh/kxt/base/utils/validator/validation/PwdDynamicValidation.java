package com.jyh.kxt.base.utils.validator.validation;

import android.content.Context;
import android.text.TextUtils;

import com.jyh.kxt.base.utils.validator.ValidationExecutor;


/**
 * 项目名:Kxt
 * 类描述:动态密码
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/8.
 */

public class PwdDynamicValidation extends ValidationExecutor {

    @Override
    public boolean doValidate(Context context, String pwd) {
        if (TextUtils.isEmpty(pwd)) {
            errorInfo = "动态码不能为空";
            return false;
        }

        errorInfo=null;
        return true;
    }
}
