package com.jyh.kxt.base.util.validation;

import android.content.Context;

import com.library.util.avalidations.ValidationExecutor;
import com.library.widget.window.ToastView;

import java.util.regex.Pattern;

/**
 * Created by Mr'Dai on 2017/4/5.
 */

public class UserNameValidation extends ValidationExecutor {
    @Override
    public boolean doValidate(Context context, String text) {
        String regex = "^[a-zA-Z](?=.*?[a-zA-Z])(?=.*?[0-9])[a-zA-Z0-9_]{7,11}$";
        boolean result = Pattern.compile(regex).matcher(text).find();
        if (!result) {
            ToastView.makeText(context, "用户名错误");
            return false;
        }
        return true;
    }
}
