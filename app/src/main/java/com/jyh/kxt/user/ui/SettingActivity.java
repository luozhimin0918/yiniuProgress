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

    private SettingPresenter settingPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting, StatusBarColor.THEME1);

        settingPresenter = new SettingPresenter(this);

    }
}
