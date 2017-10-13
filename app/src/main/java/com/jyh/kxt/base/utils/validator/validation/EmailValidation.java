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

public class EmailValidation extends ValidationExecutor {

    @Override
    public boolean doValidate(Context context, String email) {

        if (RegexValidateUtil.isEmpty(email)) {
            errorInfo = "邮箱不能为空";
            return false;
        }

//        String regex = "^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        String regex = "[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

        boolean result = Pattern.compile(regex).matcher(email).find();
        if (!result) {
            errorInfo = "邮箱不合法";
            return false;
        }

        errorInfo=null;
        return result;
    }

}
