package com.jyh.kxt.user.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
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
import com.jyh.kxt.base.util.validation.EmailValidation;
import com.jyh.kxt.base.util.validation.PwdValidation;
import com.jyh.kxt.base.util.validation.UserNameValidation;
import com.jyh.kxt.base.utils.UmengLoginTool;
import com.jyh.kxt.base.widget.LineEditText;
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
    @BindView(R.id.db_login) Button button;
    private VolleyRequest request;

    private boolean isNameError, isEmailError, isPwdError;
    private EditTextValidator editTextValidator;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_register);

        editTextValidator = new EditTextValidator(getContext()).setButton(button)
                .add(new ValidationModel(edtName, tvErrorName, new UserNameValidation()))
                .add(new ValidationModel(edtEmail, tvErrorEmail, new EmailValidation()))
                .add(new ValidationModel(edtPwd, tvErrorPwd, new PwdValidation()))
                .execute();
    }

    @OnClick({R.id.db_login, R.id.iv_qq, R.id.iv_sina, R.id.iv_wx})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.db_login:
                //注册
                if (editTextValidator.validate())
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
        showWaitDialog(null);
        if (request == null) {
            request = new VolleyRequest(getContext(), getQueue());
            request.setTag(getClass().getName());
        }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getQueue().cancelAll(getClass().getName());
    }
}
