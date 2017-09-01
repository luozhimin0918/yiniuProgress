package com.jyh.kxt.chat.presenter;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.chat.UserSettingActivity;
import com.jyh.kxt.chat.json.UserSettingJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.widget.window.ToastView;

/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/30.
 */

public class UserSettingPresenter extends BasePresenter {

    @BindObject UserSettingActivity activity;
    private VolleyRequest request;

    public UserSettingPresenter(IBaseView iBaseView) {
        super(iBaseView);
        request = new VolleyRequest(mContext, mQueue);
        request.setTag(getClass().getName());
    }

    public void ban(String receiverUid, final String isBan, final ObserverData<Boolean> observerData) {

        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_RECEIVER, receiverUid);
        jsonParam.put(VarConstant.HTTP_IS_BANNED, isBan);
        jsonParam.put(VarConstant.HTTP_SENDER, LoginUtils.getUserInfo(mContext).getUid());
        request.doGet(HttpConstant.MSG_USER_BAN, jsonParam, new HttpListener<Object>() {
            @Override
            protected void onResponse(Object o) {
                observerData.callback(true);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                observerData.onError(error);
            }
        });
    }

    public void init(String receiver) {
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_RECEIVER, receiver);
        jsonParam.put(VarConstant.HTTP_SENDER, LoginUtils.getUserInfo(mContext).getUid());
        request.doGet(HttpConstant.MSG_USER_SET, jsonParam, new HttpListener<UserSettingJson>() {
            @Override
            protected void onResponse(UserSettingJson userSettingJson) {
                activity.init(userSettingJson);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                activity.plRootView.loadError();
            }
        });
    }
}
