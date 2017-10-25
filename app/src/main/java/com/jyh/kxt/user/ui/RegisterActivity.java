package com.jyh.kxt.user.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.custom.DiscolorButton;
import com.jyh.kxt.base.utils.validator.EditTextValidator;
import com.jyh.kxt.base.utils.validator.ValidationModel;
import com.jyh.kxt.base.utils.validator.validation.EmailValidation;
import com.jyh.kxt.base.utils.validator.validation.PhoneValidation;
import com.jyh.kxt.base.utils.validator.validation.PwdDynamicValidation;
import com.jyh.kxt.base.utils.validator.validation.PwdValidation;
import com.jyh.kxt.base.utils.validator.validation.UserNameValidation;
import com.jyh.kxt.base.widget.FunctionEditText;
import com.jyh.kxt.user.presenter.RegisterPresenter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:KxtProfessional
 * 类描述:注册界面
 * 创建人:苟蒙蒙
 * 创建日期:2017/10/13.
 */

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.edt_phone) public FunctionEditText edtPhone;
    @BindView(R.id.edt_pwd) public FunctionEditText edtPwd;
    @BindView(R.id.db_register) public DiscolorButton dbRegister;

    private RegisterPresenter presenter;
    private int step = 0;
    private EditTextValidator editTextValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register, StatusBarColor.THEME1);

        presenter = new RegisterPresenter(this);

        tvBarTitle.setText("账号注册");

        editTextValidator = new EditTextValidator(this)
                .add(new ValidationModel(edtPhone, new PhoneValidation()))
                .add(new ValidationModel(edtPwd, new PwdDynamicValidation()))
                .setButton(dbRegister)
                .execute();

        edtPwd.setFunctionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (step == 0) {
                    //请求动态密码
                    presenter.requestPwd();
                }
            }
        });
    }

    @OnClick({R.id.iv_bar_break, R.id.db_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.db_register:
                if (editTextValidator.validate()) {
                    if (step == 0) {
                        //进行下一步设置密码
                        step = 1;
                        initStepTwo();
                    } else {
                        // TODO: 2017/10/13  完成注册
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (step == 0) {
            super.onBackPressed();
        } else {
            showErrorView();
        }
    }

    private AlertDialog errorDialog;

    /**
     * 提示设置密码
     */
    private void showErrorView() {
        if (errorDialog == null) {
            errorDialog = new AlertDialog.Builder(this)
                    .setTitle("温馨提示")
                    .setMessage("您还未设置昵称或密码，退出将导致注册失败")
                    .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            edtPwd.setType(2);
                            edtPwd.reflash();
                            restoreStepOne();
                        }
                    }).setPositiveButton("设置密码", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
        }
        if (errorDialog.isShowing()) {
            errorDialog.dismiss();
        }
        errorDialog.show();
    }

    /**
     * 复原第一步数据
     */
    private void restoreStepOne() {
        step = 0;
        JSONObject stepOneJson = presenter.getStepOneJson();
        edtPhone.setEdtText(stepOneJson.getString("phone"));
        edtPhone.setHintText(stepOneJson.getString("phone_hint"));
        edtPwd.setEdtText(stepOneJson.getString("pwd"));
        edtPwd.setHintText(stepOneJson.getString("pwd_hint"));
        edtPwd.setShowTxtLine(true);
        edtPwd.setFunctionText(stepOneJson.getString("function"));
        edtPwd.setFunctionTextColor(stepOneJson.getInteger("functionColor") == null ? 0 : stepOneJson.getInteger("functionColor"));
        dbRegister.setText(stepOneJson.getString("btnTxt"));
    }

    /**
     * 初始化第二部界面
     */
    private void initStepTwo() {
        presenter.saveStepOne();//保存当前状态
        edtPwd.setType(3);
        edtPwd.reflash();
        edtPhone.setEdtText("");
        edtPhone.setHintText("设置昵称");
        edtPwd.setEdtText("");
        edtPwd.setHintText("设置密码(6-16位字母、数字和符号)");
        dbRegister.setText("完成注册");

        editTextValidator = new EditTextValidator(this)
                .add(new ValidationModel(edtPhone, new UserNameValidation()))
                .add(new ValidationModel(edtPwd, new PwdValidation()))
                .setButton(dbRegister)
                .execute();

    }

    /**
     * 显示倒计时
     *
     * @param time
     */
    public void setCountDown(int time) {
        if (step == 0) {
            if (time > 0) {
                edtPwd.setFunctionText(time + "秒后重发");
                edtPwd.setFunctionTextColor(ContextCompat.getColor(this, R.color.font_color9));
            } else {
                edtPwd.setFunctionText("获取动态码");
                edtPwd.setFunctionTextColor(ContextCompat.getColor(this, R.color.font_color1));
            }
        }
    }
}
