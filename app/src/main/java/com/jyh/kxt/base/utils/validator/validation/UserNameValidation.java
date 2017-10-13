package com.jyh.kxt.base.utils.validator.validation;

import android.content.Context;

import com.jyh.kxt.base.utils.validator.ValidationExecutor;
import com.library.util.RegexValidateUtil;

/**
 * Created by Mr'Dai on 2017/4/5.
 */

public class UserNameValidation extends ValidationExecutor {

    @Override
    public boolean doValidate(Context context, String text) {

        if (RegexValidateUtil.isEmpty(text)) {
            errorInfo = "账号不能为空";
            return false;
        }
//
//        String regex = "^[a-zA-Z](?=.*?[a-zA-Z])(?=.*?[0-9])[a-zA-Z0-9_]{7,11}$";
//        boolean result = Pattern.compile(regex).matcher(text).find();
//        if (!result) {
//            errorInfo = "用户名错误";
//            return false;
//        }
        errorInfo = null;
        return true;
    }
}
