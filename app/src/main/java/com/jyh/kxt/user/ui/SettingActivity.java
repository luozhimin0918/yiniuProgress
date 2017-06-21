package com.jyh.kxt.user.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.utils.GlideCacheUtil;
import com.jyh.kxt.user.presenter.SettingPresenter;
import com.library.util.SPUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 设置
 */
public class SettingActivity extends BaseActivity {

    @BindView(R.id.iv_bar_break) public ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;
    @BindView(R.id.iv_push) CheckBox ivPush;
    @BindView(R.id.rl_push) RelativeLayout rlPush;
    @BindView(R.id.iv_sound) CheckBox ivSound;
    @BindView(R.id.rl_sound) RelativeLayout rlSound;
    @BindView(R.id.rl_clear) RelativeLayout rlClear;
    @BindView(R.id.rl_version) RelativeLayout rlVersion;
    @BindView(R.id.tv_clear) TextView tvClear;
    @BindView(R.id.v_version_point) View vPoint;
    private SettingPresenter settingPresenter;
    private String cacheSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting, StatusBarColor.THEME1);


        Boolean isOpenPush = SPUtils.getBoolean(this, SpConstant.SETTING_PUSH);
        Boolean isOpenSound = SPUtils.getBoolean(this, SpConstant.SETTING_SOUND);

        ivPush.setChecked(isOpenPush);
        ivSound.setChecked(isOpenSound);

        settingPresenter = new SettingPresenter(this);
        tvBarTitle.setText("设置");

        cacheSize = GlideCacheUtil.getInstance().getCacheSize(this);
        tvClear.setText(cacheSize);
        settingPresenter.version(true);
    }

    @OnClick({R.id.iv_bar_break, R.id.rl_push, R.id.iv_push, R.id.rl_sound, R.id.iv_sound, R.id.rl_clear, R.id
            .rl_version})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.rl_push:
            case R.id.iv_push:
                changeBtnStatus(view, 0);
                break;
            case R.id.rl_sound:
            case R.id.iv_sound:
                changeBtnStatus(view, 1);
                break;
            case R.id.rl_clear:
                settingPresenter.clear(cacheSize);
                tvClear.setText("0.00KB");
                break;
            case R.id.rl_version:
                settingPresenter.version(false);
                break;
        }
    }

    /**
     * 改变按钮状态
     *
     * @param view
     * @param type
     */
    public void changeBtnStatus(View view, int type) {
        switch (type) {
            case 0:
                boolean checkedPush = view instanceof CheckBox ? ivPush.isChecked() : !ivPush.isChecked();
                ivPush.setChecked(checkedPush);
                break;
            case 1:
                boolean checkedSound = view instanceof CheckBox ? ivSound.isChecked() : !ivSound.isChecked();
                ivSound.setChecked(checkedSound);
                break;
        }
    }

    public void changeVersionPointStatus(boolean isShow) {
        vPoint.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getQueue().cancelAll(SettingPresenter.class.getName());
    }
}
