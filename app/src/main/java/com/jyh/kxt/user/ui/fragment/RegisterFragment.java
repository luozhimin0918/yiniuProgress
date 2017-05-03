package com.jyh.kxt.user.ui.fragment;

import android.databinding.repacked.google.common.collect.MapConstraint;
import android.databinding.repacked.google.common.collect.ObjectArrays;
import android.databinding.repacked.org.antlr.v4.parse.ToolANTLRLexer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.UmengLoginTool;
import com.jyh.kxt.base.widget.LineEditText;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.bean.EventBusClass;
import com.library.util.EncryptionUtils;
import com.library.util.RegexValidateUtil;
import com.library.widget.window.ToastView;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:注册
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/25.
 */

public class RegisterFragment extends BaseFragment {
    @BindView(R.id.edt_name) LineEditText edtName;
    @BindView(R.id.tv_error_name) TextView tvErrorName;
    @BindView(R.id.edt_email) LineEditText edtEmail;
    @BindView(R.id.tv_error_email) TextView tvErrorEmail;
    @BindView(R.id.edt_pwd) LineEditText edtPwd;
    @BindView(R.id.tv_error_pwd) TextView tvErrorPwd;
    private VolleyRequest request;

    private boolean isNameError, isEmailError, isPwdError;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_register);
        clearErrorInfo();
    }

    @OnClick({R.id.db_login, R.id.iv_qq, R.id.iv_sina, R.id.iv_wx})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.db_login:
                //注册
                register();
                break;
            case R.id.iv_qq:
                UmengLoginTool.umenglogin((BaseActivity) getActivity(), SHARE_MEDIA.QQ);
                break;
            case R.id.iv_sina:
                UmengLoginTool.umenglogin((BaseActivity) getActivity(), SHARE_MEDIA.SINA);
                break;
            case R.id.iv_wx:
                UmengLoginTool.umenglogin((BaseActivity) getActivity(), SHARE_MEDIA.WEIXIN);
                break;
        }
    }

    private void register() {
        if (!RegexValidateUtil.checkName(edtName.getText().toString())) {
            showError(0, RegexValidateUtil.errorInfo);
            isNameError = true;
            return;
        }
        if (!RegexValidateUtil.checkEmail(edtEmail.getText().toString())) {
            showError(1, RegexValidateUtil.errorInfo);
            isEmailError = true;
            return;
        }
        if (!RegexValidateUtil.checkPwd(edtPwd.getText().toString())) {
            showError(2, RegexValidateUtil.errorInfo);
            isPwdError = true;
            return;
        }

        showWaitDialog(null);
        clearError(-1);
        if (request == null)
            request = new VolleyRequest(getContext(), getQueue());
        request.doPost(HttpConstant.USER_REGISTER, getMap(), new HttpListener<Object>() {
            @Override
            protected void onResponse(Object o) {
                ToastView.makeText3(getContext(), request.getMag());
                dismissWaitDialog();
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                dismissWaitDialog();
                ToastView.makeText3(getContext(), error == null ? "" : error.getMessage());
            }
        });
    }

    private Map getMap() {
        Map map = new HashMap();
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_EMAIL, edtEmail.getText().toString());
        jsonParam.put(VarConstant.HTTP_USERNAME, edtName.getText().toString());
        jsonParam.put(VarConstant.HTTP_PWD, edtPwd.getText().toString());
        try {
            map.put(VarConstant.HTTP_CONTENT2, EncryptionUtils.createJWT(VarConstant.KEY, jsonParam.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 错误提示
     *
     * @param type
     * @param info
     */
    private void showError(int type, String info) {

        tvErrorEmail.setText("");
        tvErrorName.setText("");
        tvErrorPwd.setText("");

        switch (type) {
            case 0:
                tvErrorName.setText(info);
                break;
            case 1:
                tvErrorEmail.setText(info);
                break;
            case 2:
                tvErrorPwd.setText(info);
                break;
        }

    }

    /**
     * 清除错误提示
     *
     * @param type -1清除所有 0清除昵称错误 1清除email错误 2清除密码错误
     */
    private void clearError(int type) {
        switch (type) {
            case -1:
                tvErrorPwd.setText("");
                tvErrorEmail.setText("");
                tvErrorName.setText("");
                break;
            case 0:
                tvErrorName.setText("");
                break;
            case 1:
                tvErrorEmail.setText("");
                break;
            case 2:
                tvErrorPwd.setText("");
                break;
        }
    }

    private void clearErrorInfo() {
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isEmailError) {
                    isEmailError = false;
                    clearError(1);
                }
            }
        });
        edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isNameError) {
                    isNameError = false;
                    clearError(0);
                }
            }
        });
        edtPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isPwdError) {
                    isPwdError = false;
                    clearError(2);
                }
            }
        });
    }
}
