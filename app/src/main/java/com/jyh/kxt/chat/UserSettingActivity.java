package com.jyh.kxt.chat;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.chat.presenter.UserSettingPresenter;
import com.library.widget.window.ToastView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:KxtProfessional
 * 类描述: 消息设置
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/30.
 */

public class UserSettingActivity extends BaseActivity {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;
    @BindView(R.id.riv_avatar) RoundImageView rivAvatar;
    @BindView(R.id.tv_name) TextView tvName;
    @BindView(R.id.v_ban) View vBan;

    private UserSettingPresenter presenter;
    private String receiverUid;
    private String isBan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter_usersetting);

        tvBarTitle.setText("消息设置");

        presenter = new UserSettingPresenter(this);
    }

    @OnClick({R.id.iv_bar_break, R.id.ll_user, R.id.v_ban})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.ll_user:

                break;
            case R.id.v_ban:
                presenter.ban(receiverUid, isBan, new ObserverData<Boolean>() {
                    @Override
                    public void callback(Boolean aBoolean) {
                        if ("0".equals(isBan)) {
                            isBan = "1";
                            vBan.setSelected(true);
                        } else {
                            isBan = "0";
                            vBan.setSelected(false);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        ToastView.makeText3(getContext(), "0".equals(isBan) ? "添加屏蔽失败" : "解除屏蔽失败");
                    }
                });
                break;
        }
    }
}
