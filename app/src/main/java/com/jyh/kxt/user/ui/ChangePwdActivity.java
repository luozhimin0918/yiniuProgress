package com.jyh.kxt.user.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.widget.PwdEditText;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.RegexValidateUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:修改密码
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/27.
 */

public class ChangePwdActivity extends BaseActivity {
    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;
    @BindView(R.id.edt_pwd_old) PwdEditText edtPwdOld;
    @BindView(R.id.edt_pwd_new) PwdEditText edtPwdNew;
    @BindView(R.id.edt_pwd_re) PwdEditText edtPwdRe;
    @BindView(R.id.btn_sure) Button changeBtn;

    private EditText pwdOld, pwdNew, pwdRe;
    private boolean isOldError, isNewError, isReError;
    private VolleyRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_changepwd, StatusBarColor.THEME1);

        tvBarTitle.setText("修改密码");

        changeBtnStatus();

        pwdOld = getEditText(edtPwdOld);
        pwdNew = getEditText(edtPwdNew);
        pwdRe = getEditText(edtPwdRe);

        setEditTextListener(pwdOld, pwdNew, pwdRe);
    }

    @OnClick({R.id.iv_bar_break, R.id.btn_sure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.btn_sure:
                changePwd(pwdOld.getText().toString(), pwdNew.getText().toString(), pwdRe.getText().toString());
                break;
        }
    }

    private void changePwd(String oldPwd, String newPwd, String rePwd) {
        if (RegexValidateUtil.checkPwd(oldPwd)) {
            isOldError = true;
            setErrorInfo(0, RegexValidateUtil.errorInfo);
            return;
        }
        if (RegexValidateUtil.checkPwd(newPwd)) {
            isNewError = true;
            setErrorInfo(1, RegexValidateUtil.errorInfo);
            return;
        }
        if (RegexValidateUtil.checkPwd(rePwd)) {
            isReError = true;
            setErrorInfo(2, RegexValidateUtil.errorInfo);
            return;
        }
        if (request == null)
            request = new VolleyRequest(this, getQueue());

        showWaitDialog(null);
        request.doPost(HttpConstant.USER_CHANEPWD, getMap(), new HttpListener<Object>() {
            @Override
            protected void onResponse(Object o) {
                dismissWaitDialog();
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                dismissWaitDialog();
            }
        });
    }

    private Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        JSONObject jsonParam = request.getJsonParam();
        return map;
    }


    private void setEditTextListener(EditText pwdOld, EditText pwdNew, EditText pwdRe) {
        pwdOld.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                changeBtnStatus();
            }
        });
        pwdNew.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                changeBtnStatus();
            }
        });
        pwdRe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                changeBtnStatus();
            }
        });
    }

    /**
     * 改变按钮状态
     */
    private void changeBtnStatus() {
        if (!isReError && !isNewError && !isOldError) {
            changeBtn.setSelected(true);
            changeBtn.setClickable(true);
        } else {
            changeBtn.setSelected(false);
            changeBtn.setClickable(false);
        }
    }

    private void setErrorInfo(int type, String info) {
        switch (type) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
        }
    }

    private void clearError(int type) {
        switch (type) {
            case -1:
                break;
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
        }
    }

    private EditText getEditText(PwdEditText edt) {
        return edt.getEditText();
    }
}
