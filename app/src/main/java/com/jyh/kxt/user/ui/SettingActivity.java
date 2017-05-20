package com.jyh.kxt.user.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseSwipeBackActivity;
import com.jyh.kxt.user.presenter.SettingPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 设置
 */
public class SettingActivity extends BaseSwipeBackActivity {

    public static final String TYPE_PUSH = "push";
    public static final String TYPE_SOUND = "sound";

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;
    @BindView(R.id.iv_push) ImageView ivPush;
    @BindView(R.id.rl_push) RelativeLayout rlPush;
    @BindView(R.id.iv_sound) ImageView ivSound;
    @BindView(R.id.rl_sound) RelativeLayout rlSound;
    @BindView(R.id.rl_clear) RelativeLayout rlClear;
    @BindView(R.id.rl_version) RelativeLayout rlVersion;
    private SettingPresenter settingPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting, StatusBarColor.THEME1);
        ButterKnife.bind(this);

        settingPresenter = new SettingPresenter(this);

        tvBarTitle.setText("设置");
    }

    @OnClick({R.id.iv_bar_break, R.id.rl_push, R.id.rl_sound, R.id.rl_clear, R.id.rl_version})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.rl_push:
                settingPresenter.push();
                break;
            case R.id.rl_sound:
                settingPresenter.sound();
                break;
            case R.id.rl_clear:
                settingPresenter.clear();
                break;
            case R.id.rl_version:
                settingPresenter.version();
                break;
        }
    }

    /**
     * 改变按钮状态
     *
     * @param isSel
     * @param type
     */
    public void changeBtnStatus(boolean isSel, String type) {
        switch (type) {
            case TYPE_PUSH:
                ivPush.setSelected(isSel);
                break;
            case TYPE_SOUND:
                ivSound.setSelected(isSel);
                break;
        }
    }
}
