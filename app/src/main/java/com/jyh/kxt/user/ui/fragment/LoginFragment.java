package com.jyh.kxt.user.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.utils.UmengLoginTool;
import com.jyh.kxt.base.widget.LineEditText;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.ui.ChangePwdActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;
import com.library.util.RegexValidateUtil;
import com.library.widget.window.ToastView;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:登录
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/25.
 */

public class LoginFragment extends BaseFragment {

    @BindView(R.id.edt_email) LineEditText edtEmail;
    @BindView(R.id.tv_error_email) TextView tvErrorEmail;
    @BindView(R.id.edt_pwd) LineEditText edtPwd;
    @BindView(R.id.tv_error_pwd) TextView tvErrorPwd;
    private VolleyRequest request;

    private boolean isEmailError = false;
    private boolean isPwdError = false;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_login);
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
                    clearError(1);
                }
            }
        });
    }

    @OnClick({R.id.tv_forgetPwd, R.id.db_login, R.id.iv_qq, R.id.iv_sina, R.id.iv_wx})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_forgetPwd:
                //忘记密码
                Intent intent = new Intent(getContext(), ChangePwdActivity.class);
                startActivity(intent);
                break;
            case R.id.db_login:
                //登录
                login();
                break;
            case R.id.iv_qq:
                //qq
                UmengLoginTool.umenglogin((BaseActivity) getActivity(), SHARE_MEDIA.QQ);
                break;
            case R.id.iv_sina:
                //新浪
                UmengLoginTool.umenglogin((BaseActivity) getActivity(), SHARE_MEDIA.SINA);
                break;
            case R.id.iv_wx:
                //微信
                UmengLoginTool.umenglogin((BaseActivity) getActivity(), SHARE_MEDIA.WEIXIN);
                break;
        }
    }

    private void login() {
        String name = edtEmail.getText().toString();
        if (!RegexValidateUtil.checkEmail(name) && !RegexValidateUtil.checkName(name)) {
            showError(0, RegexValidateUtil.errorInfo);
            isEmailError = true;
            return;
        }
        if (TextUtils.isEmpty(edtPwd.getText().toString())) {
            showError(1, RegexValidateUtil.errorInfo);
            isPwdError = true;
            return;
        }

        showWaitDialog(null);
        clearError(-1);
        if (request == null)
            request = new VolleyRequest(getContext(), getQueue());

        request.doPost(HttpConstant.USER_LOGIN2, getMap(), new HttpListener<UserJson>() {
            @Override
            protected void onResponse(UserJson user) {
                dismissWaitDialog();
                LoginUtils.login(getContext(),user);
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
        jsonParam.put(VarConstant.HTTP_USERNAME, edtEmail.getText().toString());
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
     * @param i    0 email 1 密码
     * @param info
     */
    private void showError(int i, String info) {
        tvErrorEmail.setText("");
        tvErrorPwd.setText("");
        switch (i) {
            case 0:
                tvErrorEmail.setText(info);
                break;
            case 1:
                tvErrorPwd.setText(info);
                break;
        }
    }

    /**
     * 清除错误
     *
     * @param type
     */
    private void clearError(int type) {
        switch (type) {
            case -1:
                tvErrorEmail.setText("");
                tvErrorPwd.setText("");
                break;
            case 0:
                tvErrorEmail.setText("");
                break;
            case 1:
                tvErrorPwd.setText("");
                break;
        }
    }
}
