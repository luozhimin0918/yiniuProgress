package com.jyh.kxt.user.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.custom.DiscolorButton;
import com.jyh.kxt.base.utils.validator.EditTextValidator;
import com.jyh.kxt.base.utils.validator.ValidationModel;
import com.jyh.kxt.base.utils.validator.validation.PwdOldValidation;
import com.jyh.kxt.base.utils.validator.validation.PwdValidation;
import com.jyh.kxt.base.widget.FunctionEditText;
import com.jyh.kxt.user.presenter.ChangePwdPresenter;

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
    @BindView(R.id.btn_sure) DiscolorButton changeBtn;

    private EditTextValidator editTextValidator;
    private ChangePwdPresenter presenter;

    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_changepwd, StatusBarColor.THEME1);

        tvBarTitle.setText("修改密码");

        type = getIntent().getIntExtra(TYPE, TYPE_SET);
        presenter = new ChangePwdPresenter(this);
        presenter.setType(type);

        if (type == TYPE_SET) {
            tvBarTitle.setText("设置密码");
            edtPwdOld.setHintText("输入密码");
            editTextValidator = new EditTextValidator(this)
                    .setButton(changeBtn)
                    .add(new ValidationModel(edtPwdOld, new PwdValidation()))
                    .add(new ValidationModel(edtPwdRe, new PwdValidation()))
                    .execute();
            edtPwdNew.setVisibility(View.GONE);
        } else {
            tvBarTitle.setText("修改密码");
            edtPwdOld.setHintText("旧密码");
            editTextValidator = new EditTextValidator(getContext())
                    .setButton(changeBtn)
                    .add(new ValidationModel(edtPwdOld, new PwdOldValidation()))
                    .add(new ValidationModel(edtPwdNew, new PwdValidation()))
                    .add(new ValidationModel(edtPwdRe, new PwdValidation()))
                    .execute();
        }

    }

    @OnClick({R.id.iv_bar_break, R.id.btn_sure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.btn_sure:
                if (editTextValidator.validate()) {
                    presenter.pwd(edtPwdOld.getEdtText(), edtPwdNew.getEdtText(), edtPwdRe.getEdtText());
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getQueue().cancelAll(getClass().getName());
    }

    public void showError(String info) {
        edtPwdRe.setErrorInfo(info);
    }
}
