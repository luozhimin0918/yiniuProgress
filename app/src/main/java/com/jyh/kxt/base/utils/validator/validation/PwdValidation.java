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

public class PwdValidation extends ValidationExecutor {

    @Override
    public boolean doValidate(Context context, String pwd) {
        if (TextUtils.isEmpty(pwd)) {
            errorInfo = "密码不能为空";
            return false;
        }
        if (pwd.length() < 6 || pwd.length() > 18) {
            errorInfo = "请输入6 - 16位密码";
            return false;
        }
        String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)(?![`~!@#$%^&*()+=|{}_':;',\\\\\\\\[\\\\\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？])" +
                "([0-9A-Za-z]|[`~!@#$%^&*()+=|{}_':;',\\\\\\\\[\\\\\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]){6,20}$";

        boolean result = Pattern.compile(regex).matcher(pwd).find();
        if (!result) {
            errorInfo = "密码至少使用字母数字和符号两种以上组合";
            return false;
        }

        errorInfo=null;
        return result;
    }
}
