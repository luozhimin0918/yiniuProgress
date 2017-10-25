package com.jyh.kxt.base.utils.validator.validation;

import android.content.Context;

import com.jyh.kxt.base.utils.validator.ValidationExecutor;
import com.library.util.RegexValidateUtil;

import java.util.regex.Pattern;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/8.
 */

public class PhoneValidation extends ValidationExecutor {
    @Override
    public boolean doValidate(Context context, String phone) {

        if (RegexValidateUtil.isEmpty(phone)) {
            errorInfo = "手机号码不能为空";
            return false;
        }

        String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[0-9])|(18[0,5-9]))\\d{8}$";
        boolean result = Pattern.compile(regex).matcher(phone).find();
        if (!result) {
            errorInfo = "手机号码不合法";
            return false;
        }

        errorInfo=null;
        return true;
    }
}
