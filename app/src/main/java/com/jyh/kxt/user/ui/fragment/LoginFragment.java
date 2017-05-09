package com.jyh.kxt.user.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.util.validation.PwdValidation;
import com.jyh.kxt.base.util.validation.UserNameValidation;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.utils.UmengLoginTool;
import com.jyh.kxt.base.widget.LineEditText;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.ui.ChangePwdActivity;
import com.jyh.kxt.user.ui.LoginOrRegisterActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;
import com.library.util.RegexValidateUtil;
import com.library.util.avalidations.EditTextValidator;
import com.library.util.avalidations.ValidationModel;
import com.library.widget.window.ToastView;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTouch;

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

    @BindView(R.id.db_login) Button btnLogin;

    private VolleyRequest request;

    private EditTextValidator editTextValidator;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_login);

        editTextValidator = new EditTextValidator(getContext())
                .setButton(btnLogin)
                .add(new ValidationModel(edtEmail, tvErrorEmail,  new UserNameValidation()))
                .add(new ValidationModel(edtPwd, tvErrorPwd, new PwdValidation()))
                .execute();
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
                if (editTextValidator.validate())
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
        showWaitDialog(null);
        if (request == null)
            request = new VolleyRequest(getContext(), getQueue());

        request.doPost(HttpConstant.USER_LOGIN2, getMap(), new HttpListener<UserJson>() {
            @Override
            protected void onResponse(UserJson user) {
                dismissWaitDialog();
                LoginUtils.login(getContext(), user);
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

}
