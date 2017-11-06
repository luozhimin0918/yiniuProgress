package com.jyh.kxt.base.utils.validator.validation;

import android.content.Context;

import com.jyh.kxt.base.utils.validator.ValidationExecutor;
import com.library.util.RegexValidateUtil;

import java.util.regex.Pattern;

/**
 * 项目名:Kxt
 * 类描述:邮箱验证
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/8.
 */

public class EmailOrPhoneValidation extends ValidationExecutor {

    @Override
    public boolean doValidate(Context context, String email) {

        if (RegexValidateUtil.isEmpty(email)) {
            errorInfo = "邮箱或手机号不能为空";
            return false;
        }

        String emailRegex = "[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        boolean result = Pattern.compile(emailRegex).matcher(email).find();
        String phoneRegex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[0-9])|(18[0,5-9]))\\d{8}$";
        boolean result2 = Pattern.compile(phoneRegex).matcher(email).find();
        if (!result && !result2) {
            errorInfo = "邮箱或手机号不合法";
            return false;
        }

        errorInfo = null;
        return result|result2;
    }

}
