package com.jyh.kxt.base.util.validation;

import android.content.Context;
import android.text.TextUtils;

import com.library.util.avalidations.ValidationExecutor;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/8.
 */

public class PwdValidation extends ValidationExecutor {

    @Override
    public boolean doValidate(Context context, String pwd) {
        if (TextUtils.isEmpty(pwd)) {
            errorInfo = "密码不能为空";
            return false;
        }
        if (pwd.length() < 6 || pwd.length() > 18) {
            errorInfo = "请输入6 - 18位密码";
            return false;
        }

        errorInfo=null;
        return true;
    }
}
