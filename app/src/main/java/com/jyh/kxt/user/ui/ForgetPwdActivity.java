package com.jyh.kxt.user.ui;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.custom.DiscolorButton;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.utils.validator.EditTextValidator;
import com.jyh.kxt.base.utils.validator.ValidationModel;
import com.jyh.kxt.base.utils.validator.validation.EmailOrPhoneValidation;
import com.jyh.kxt.base.utils.validator.validation.PwdDynamicValidation;
import com.jyh.kxt.base.utils.validator.validation.PwdValidation;
import com.jyh.kxt.base.widget.FunctionEditText;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.presenter.ForgetPwdPresenter;
import com.library.base.http.VarConstant;
import com.library.util.NetUtils;
import com.library.util.RegexValidateUtil;
import com.library.widget.window.ToastView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:忘记密码
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/27.
 */

public class ForgetPwdActivity extends BaseActivity {

    @BindView(R.id.iv_bar_break) public ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;

    @BindView(R.id.btn_send) DiscolorButton btnSend;
    @BindView(R.id.edt_email) public FunctionEditText edtEmail;
    @BindView(R.id.edt_pwd) FunctionEditText edtPwd;
    @BindView(R.id.tv_warning) TextView tvWarning;

    private ForgetPwdPresenter forgetPwdPresenter;

    public static final String EMAIL = "email";
    private EditTextValidator editTextValidator;

    private int step = 1;//步骤

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_forget, StatusBarColor.THEME1);

        forgetPwdPresenter = new ForgetPwdPresenter(this);

        tvBarTitle.setText("忘记密码");

        editTextValidator = new EditTextValidator(getContext())
                .setButton(btnSend)
                .add(new ValidationModel(edtEmail, new EmailOrPhoneValidation()))
                .add(new ValidationModel(edtPwd, new PwdDynamicValidation()))
                .execute();

        String emailStr = getIntent().getStringExtra(EMAIL);
        edtEmail.getEdt().setText(emailStr);

        edtPwd.setFunctionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //请求动态密码
                if (RegexValidateUtil.isEmpty(edtEmail.getEdtText())) {
                    ToastView.makeText3(getContext(), "手机号或邮箱不能为空");
                    return;
                }
                if (RegexValidateUtil.checkCellphone(edtEmail.getEdtText()) || RegexValidateUtil.checkEmail(edtEmail.getEdtText()))
                    forgetPwdPresenter.requestPwd();
                else
                    ToastView.makeText3(getContext(), "手机号或邮箱不合法");
            }
        });

    }

    @OnClick({R.id.iv_bar_break, R.id.btn_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                if (step == 2) {
                    forgetPwdPresenter.saveData(step, edtEmail.getEdtText().toString(), edtPwd.getEdtText().toString(), edtPwd
                            .getFunctionText(), edtPwd.getFunctionTextColor(), edtEmail.isShowPwd(), edtPwd.isShowPwd());
                    step = 1;
                    restoreView();
                    editTextValidator = new EditTextValidator(getContext())
                            .setButton(btnSend)
                            .add(new ValidationModel(edtEmail, new EmailOrPhoneValidation()))
                            .add(new ValidationModel(edtPwd, new PwdDynamicValidation()))
                            .execute();
                } else {
                    onBackPressed();
                }
                break;
            case R.id.btn_send:
                if (editTextValidator.validate())
                    if (step == 1) {
                        LoginUtils.verifyCode(forgetPwdPresenter, VarConstant.CODE_VERIFY, edtEmail.getEdtText(), edtPwd.getEdtText(),
                                forgetPwdPresenter.getClass().getName(), new ObserverData() {
                                    @Override
                                    public void callback(Object o) {
                                        forgetPwdPresenter.saveData(step, edtEmail.getEdtText().toString(), edtPwd.getEdtText().toString(),
                                                edtPwd
                                                        .getFunctionText(), edtPwd.getFunctionTextColor(), true, true);
                                        step = 2;
                                        restoreView();
                                        editTextValidator = new EditTextValidator(getContext())
                                                .setButton(btnSend)
                                                .add(new ValidationModel(edtEmail, new PwdValidation()))
                                                .add(new ValidationModel(edtPwd, new PwdValidation()))
                                                .execute();
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        ToastView.makeText3(getContext(), e == null || e.getMessage() == null ? "验证失败" : e.getMessage());
                                    }
                                });
                    } else {
                        if (NetUtils.isNetworkAvailable(this)) {
                            if (editTextValidator.validate()) {
                                if (edtEmail.getEdtText().equals(edtPwd.getEdtText())) {

                                    showWaitDialog(null);
                                    final String user = forgetPwdPresenter.getStepOneJson().getString("edt1Text");
                                    final String pwd = edtPwd.getEdtText();
                                    LoginUtils.changePwd(forgetPwdPresenter, user, "",
                                            pwd,
                                            forgetPwdPresenter.getClass().getName(), new ObserverData() {

                                                @Override
                                                public void callback(Object o) {
                                                    LoginUtils.requestLogin(forgetPwdPresenter, user, pwd, VarConstant
                                                            .LOGIN_TYPE_DEFAULT, forgetPwdPresenter.getClass().getName(), new
                                                            ObserverData<UserJson>() {

                                                        @Override
                                                        public void callback(UserJson userJson) {
                                                            dismissWaitDialog();
                                                            ToastView.makeText(getContext(), "登录成功");
                                                            LoginUtils.login(getContext(), userJson);
                                                            finish();
                                                        }

                                                        @Override
                                                        public void onError(Exception e) {
                                                            ToastView.makeText(getContext(), "密码设置成功,自动登录失败 " + e == null || e.getMessage
                                                                    () == null ? "" : e.getMessage());
                                                            finish();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onError(Exception e) {
                                                    dismissWaitDialog();
                                                    ToastView.makeText(getContext(), e == null || e.getMessage() == null ? "设置失败" : e
                                                            .getMessage());
                                                }
                                            });

                                } else {
                                    edtPwd.setErrorInfo("密码不一致");
                                }
                            }
                        } else
                            ToastView.makeText3(this, "暂无网络,请稍后再试");
                    }
                break;
        }
    }

    void restoreView() {
        JSONObject data;
        if (step == 1) {
            data = forgetPwdPresenter.getStepOneJson();
            edtPwd.setFunctionText(data.getString("function"));
            edtPwd.setFunctionTextColor(data.getInteger("functionColor"));
        } else {
            data = forgetPwdPresenter.getStepTwoJson();
        }
        tvWarning.setText(data.getString("warning"));
        edtEmail.setHintText(data.getString("edt1hint"));
        edtPwd.setHintText(data.getString("edt2hint"));
        edtEmail.setType(data.getInteger("edt1Type"));
        edtPwd.setType(data.getInteger("edt2Type"));
        edtEmail.reflash();
        edtPwd.reflash();
        edtEmail.setEdtText(data.getString("edt1Text"));
        edtPwd.setEdtText(data.getString("edt2Text"));

        edtEmail.setShowPwd(data.getBoolean("edt1open") == null ? false : data.getBoolean("edt1open"));
        edtPwd.setShowPwd(data.getBoolean("edt2open") == null ? false : data.getBoolean("edt2open"));
        edtEmail.requestFocus();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (forgetPwdPresenter != null)
            forgetPwdPresenter.onDestroy();
        getQueue().cancelAll(forgetPwdPresenter.getClass().getName());
    }

    public void setCountDown(int time) {
        if (step == 1) {
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
