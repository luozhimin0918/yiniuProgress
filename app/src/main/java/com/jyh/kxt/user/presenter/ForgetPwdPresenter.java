package com.jyh.kxt.user.presenter;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.user.ui.AboutActivity;
import com.jyh.kxt.user.ui.ForgetPwdActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;
import com.library.util.RegexValidateUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/27.
 */

public class ForgetPwdPresenter extends BasePresenter {

    @BindObject ForgetPwdActivity forgetPwdActivity;
    private VolleyRequest request;

    public ForgetPwdPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void sendInfo(String email) {
        //判断email是否合法
        if (request == null) {
            request = new VolleyRequest(mContext, mQueue);
            request.setTag(getClass().getName());
        }
        Map map = new HashMap();
        JSONObject jsonObject = request.getJsonParam();
        jsonObject.put(VarConstant.HTTP_EMAIL, email);
        try {
            map.put(VarConstant.HTTP_CONTENT2, EncryptionUtils.createJWT(VarConstant.KEY, jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        request.doPost(HttpConstant.USER_FORGET, map, new HttpListener<Object>() {
            @Override
            protected void onResponse(Object o) {

            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
            }
        });
        forgetPwdActivity.dismissWaitDialog();
    }
}
