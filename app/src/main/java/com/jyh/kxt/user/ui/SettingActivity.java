package com.jyh.kxt.user.ui;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseSwipeBackActivity;
import com.jyh.kxt.base.custom.DiscolorButton;
import com.jyh.kxt.base.util.validation.UserNameValidation;
import com.jyh.kxt.user.presenter.SettingPresenter;
import com.library.util.avalidations.EditTextValidator;
import com.library.util.avalidations.ValidationModel;

import butterknife.BindView;

/**
 * 设置
 */
public class SettingActivity extends BaseSwipeBackActivity {


    @BindView(R.id.tet_username) TextInputEditText tetUsername;
    @BindView(R.id.tet_password) TextInputEditText tetPassword;
    @BindView(R.id.btn_exit_login) DiscolorButton btnExitLogin;

    private SettingPresenter settingPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting, StatusBarColor.THEME1);

        settingPresenter = new SettingPresenter(this);


        EditTextValidator editTextValidator = new EditTextValidator(this)
                .setButton(btnExitLogin)
                .add(new ValidationModel(tetUsername, new UserNameValidation()))
                .add(new ValidationModel(tetPassword, new UserNameValidation()))
                .execute();

    }
}
