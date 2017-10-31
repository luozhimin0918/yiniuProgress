package com.jyh.kxt.user.presenter;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.ui.RegisterActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.bean.EventBusClass;
import com.library.widget.window.ToastView;

import org.greenrobot.eventbus.EventBus;

/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/10/13.
 */

public class RegisterPresenter extends BasePresenter {

    @BindObject RegisterActivity activity;

    private JSONObject stepOneJson = new JSONObject();
    private boolean canRequestPwd = true;//是否可以请求

    public RegisterPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void saveStepOne() {
        stepOneJson.put("phone", activity.edtPhone.getEdtText());
        stepOneJson.put("phone_hint", activity.edtPhone.getHintText());
        stepOneJson.put("pwd", activity.edtPwd.getEdtText());
        stepOneJson.put("pwd_hint", activity.edtPwd.getHintText());
        stepOneJson.put("function", activity.edtPwd.getFunctionText());
        stepOneJson.put("functionColor", activity.edtPwd.getFunctionTextColor());
        stepOneJson.put("btnTxt", activity.dbRegister.getText());
    }

    public JSONObject getStepOneJson() {
        return stepOneJson;
    }

    public void requestPwd() {
        if (canRequestPwd) {
            Message message = new Message();
            message.arg1 = 60;
            message.what = 1;
            handler.sendMessage(message);
            LoginUtils.requestCode(this, VarConstant.CODE_REGISTER, activity.edtPhone.getEdtText(), this.getClass()
                    .getName(), new ObserverData() {

                @Override
                public void callback(Object o) {

                }

                @Override
                public void onError(Exception e) {

                }
            });

        } else {

        }
    }

    Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                //用于显示倒计时
                if (msg.arg1 > 0) {
                    canRequestPwd = false;
                    Message message = new Message();
                    message.arg1 = msg.arg1 - 1;
                    message.what = 1;
                    handler.sendMessageDelayed(message, 1000);
                    activity.setCountDown(message.arg1);
                    stepOneJson.put("function", message.arg1 + "秒后重发");
                    stepOneJson.put("functionColor", ContextCompat.getColor(mContext, R.color.font_color9));
                } else {
                    canRequestPwd = true;
                    activity.setCountDown(msg.arg1);
                    stepOneJson.put("function", "获取动态码");
                    stepOneJson.put("functionColor", ContextCompat.getColor(mContext, R.color.font_color1));
                }
            }
            return false;
        }
    });

    public void register() {
        VolleyRequest request = new VolleyRequest(mContext, mQueue);
        request.setTag(getClass().getName());
        JSONObject jsonParam = request.getJsonParam();

        jsonParam.put(VarConstant.HTTP_PHONE, stepOneJson.getString("phone"));
        jsonParam.put(VarConstant.HTTP_USERNAME, activity.edtPhone.getEdtText());
        jsonParam.put(VarConstant.HTTP_PWD, activity.edtPwd.getEdtText());

        activity.showWaitDialog("注册中");
        request.doPost(HttpConstant.USER_REGISTER, jsonParam, new HttpListener<UserJson>() {
            @Override
            protected void onResponse(UserJson o) {
                LoginUtils.login(mContext, o);
                activity.dismissWaitDialog();
                ToastView.makeText(mContext, "注册成功");
                activity.finish();
                EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_LOGIN, o));
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                activity.dismissWaitDialog();
                ToastView.makeText3(mContext, error == null ? "注册失败，请重试" : error.getMessage());
            }
        });
    }
}
