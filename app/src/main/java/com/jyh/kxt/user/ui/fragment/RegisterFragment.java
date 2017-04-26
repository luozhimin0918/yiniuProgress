package com.jyh.kxt.user.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.widget.LineEditText;

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

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setBindingView(R.layout.fragment_register);
    }

    @OnClick({R.id.db_login, R.id.iv_qq, R.id.iv_sina, R.id.iv_wx})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.db_login:
                break;
            case R.id.iv_qq:
                break;
            case R.id.iv_sina:
                break;
            case R.id.iv_wx:
                break;
        }
    }
}
