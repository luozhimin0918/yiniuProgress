package com.jyh.kxt.user.ui;

import android.os.Bundle;
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
import com.jyh.kxt.base.utils.validator.EditTextValidator;
import com.jyh.kxt.base.utils.validator.ValidationModel;
import com.jyh.kxt.base.utils.validator.validation.PwdValidation;
import com.jyh.kxt.base.utils.validator.validation.UserNameValidation;
import com.jyh.kxt.base.widget.FunctionEditText;
import com.jyh.kxt.base.widget.PwdEditText;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:修改密码
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/27.
 */

public class ChangePwdActivity extends BaseActivity {

    public static final String TYPE = "type";
    public static final int TYPE_SET = 1;//设置密码
    public static final int TYPE_CHANGE = 2;//修改密码

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;
    @BindView(R.id.edt_pwd_old) FunctionEditText edtPwdOld;
    @BindView(R.id.edt_pwd_new) FunctionEditText edtPwdNew;
    @BindView(R.id.edt_pwd_new_two) FunctionEditText edtPwdRe;
    @BindView(R.id.btn_sure) Button changeBtn;

    private EditText pwdOld, pwdNew, pwdRe;
    private VolleyRequest request;
    private EditTextValidator editTextValidator;

    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_changepwd, StatusBarColor.THEME1);

        tvBarTitle.setText("修改密码");

        pwdOld = getEditText(edtPwdOld);
        pwdNew = getEditText(edtPwdNew);
        pwdRe = getEditText(edtPwdRe);

        type=getIntent().getIntExtra(TYPE,TYPE_SET);

        editTextValidator = new EditTextValidator(getContext())
                .setButton(changeBtn)
                .add(new ValidationModel(edtPwdOld, new PwdValidation()))
                .add(new ValidationModel(edtPwdNew, new PwdValidation()))
                .add(new ValidationModel(edtPwdRe, new PwdValidation()))
                .execute();

    }

    @OnClick({R.id.iv_bar_break, R.id.btn_sure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.btn_sure:
                if (editTextValidator.validate())
                    changePwd(pwdOld.getText().toString(), pwdNew.getText().toString(), pwdRe.getText().toString());
                break;
        }
    }

    private void changePwd(String oldPwd, String newPwd, String rePwd) {

        if (request == null) {
            request = new VolleyRequest(this, getQueue());
            request.setTag(getClass().getName());
        }
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

    private EditText getEditText(FunctionEditText edt) {
        return edt.getEdt();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getQueue().cancelAll(getClass().getName());
    }
}
