package com.jyh.kxt.base.utils.validator.validation;

import android.content.Context;
import android.text.TextUtils;

import com.jyh.kxt.base.utils.validator.ValidationExecutor;

import java.util.regex.Pattern;


/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/8.
 */

public class PwdOldValidation extends ValidationExecutor {

    @Override
    public boolean doValidate(Context context, String pwd) {
        if (TextUtils.isEmpty(pwd)) {
            errorInfo = "密码不能为空";
            return false;
        }
        if (pwd.length() < 6 || pwd.length() > 16) {
            errorInfo = "请输入6 - 16位密码";
            return false;
        }
        return true;
    }
}
