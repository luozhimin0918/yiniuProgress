package com.jyh.kxt.user.ui;

import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.custom.DiscolorButton;
import com.jyh.kxt.base.util.validation.EmailValidation;
import com.jyh.kxt.base.util.validation.PwdValidation;
import com.jyh.kxt.base.widget.FunctionEditText;
import com.jyh.kxt.user.presenter.ForgetPwdPresenter;
import com.library.util.NetUtils;
import com.library.util.avalidations.EditTextValidator;
import com.library.util.avalidations.ValidationModel;
import com.library.widget.window.ToastView;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    @BindView(R.id.edt_email) FunctionEditText edtEmail;
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
                .add(new ValidationModel(edtEmail.getEdt(), null, new EmailValidation()))
                .add(new ValidationModel(edtPwd.getEdt(), null, new PwdValidation()))
                .execute();

        String emailStr = getIntent().getStringExtra(EMAIL);
        edtEmail.getEdt().setText(emailStr);

    }

    @OnClick({R.id.iv_bar_break, R.id.btn_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                if (step == 2) {
                    forgetPwdPresenter.saveData(step,edtEmail.getEdtText().toString(), edtPwd.getEdtText().toString(), edtPwd
                            .getFunctionText(), edtPwd.getFunctionTextColor(), edtEmail.isShowPwd(), edtPwd.isShowPwd());
                    step=1;
                    restoreView();
                } else {
                    onBackPressed();
                }
                break;
            case R.id.btn_send:
                if (step == 1) {
                    forgetPwdPresenter.saveData(step, edtEmail.getEdtText().toString(), edtPwd.getEdtText().toString(), edtPwd
                            .getFunctionText(), edtPwd.getFunctionTextColor(), true, true);
                    step=2;
                    restoreView();
                } else {
                    if (NetUtils.isNetworkAvailable(this)) {
                        if (editTextValidator.validate()) {
                            showWaitDialog(null);
                            forgetPwdPresenter.sendInfo(edtEmail.getEdt().getText().toString());
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

        edtEmail.setShowPwd(data.getBoolean("edt1open")==null?false:data.getBoolean("edt1open"));
        edtPwd.setShowPwd(data.getBoolean("edt2open")==null?false:data.getBoolean("edt2open"));

        edtEmail.requestFocus();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getQueue().cancelAll(forgetPwdPresenter.getClass().getName());
    }
}
