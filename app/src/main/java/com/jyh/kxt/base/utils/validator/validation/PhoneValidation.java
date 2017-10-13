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

        String regex = "^(0\\d{2}-\\d{8}(-\\d{1,4})?)|(0\\d{3}-\\d{7,8}(-\\d{1,4})?)$";
        boolean result = Pattern.compile(regex).matcher(phone).find();
        if (!result) {
            errorInfo = "手机号码不合法";
            return false;
        }

        errorInfo=null;
        return true;
    }
}
