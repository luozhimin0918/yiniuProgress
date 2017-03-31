package com.jyh.kxt.user.ui;

import android.os.Bundle;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseSwipeBackActivity;
import com.jyh.kxt.base.custom.DiscolorButton;
import com.jyh.kxt.user.presenter.SettingPresenter;

import butterknife.BindView;

/**
 * 设置
 */
public class SettingActivity extends BaseSwipeBackActivity {


    @BindView(R.id.btn_exit_login) DiscolorButton btnExitLogin;

    private SettingPresenter settingPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting, StatusBarColor.THEME1);

        settingPresenter = new SettingPresenter(this);

    }
}
