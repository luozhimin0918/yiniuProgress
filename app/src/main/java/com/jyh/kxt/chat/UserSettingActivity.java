package com.jyh.kxt.chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.chat.json.UserSettingJson;
import com.jyh.kxt.chat.presenter.UserSettingPresenter;
import com.jyh.kxt.trading.ui.AuthorActivity;
import com.library.bean.EventBusClass;
import com.library.widget.PageLoadLayout;
import com.library.widget.window.ToastView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:KxtProfessional
 * 类描述: 消息设置
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/30.
 */

public class UserSettingActivity extends BaseActivity {

    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;
    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;
    @BindView(R.id.riv_avatar) RoundImageView rivAvatar;
    @BindView(R.id.tv_name) TextView tvName;
    @BindView(R.id.v_ban) View vBan;

    private UserSettingPresenter presenter;
    private String receiverUid;
    private String isBan;
    private boolean isWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter_usersetting);

        tvBarTitle.setText("消息设置");

        receiverUid = getIntent().getStringExtra(IntentConstant.U_ID);
        presenter = new UserSettingPresenter(this);

        plRootView.loadWait();
        presenter.init(receiverUid);
    }

    @OnClick({R.id.iv_bar_break, R.id.ll_user, R.id.ll_ban})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.ll_user:
                if (isWriter) {
                    Intent intent = new Intent(this, AuthorActivity.class);
                    intent.putExtra(IntentConstant.O_ID, receiverUid);
                    startActivity(intent);
                }
                break;
            case R.id.ll_ban:
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

    public void init(UserSettingJson userSettingJson) {
        if (userSettingJson == null) {
            plRootView.loadError();
            return;
        }
        tvName.setText(userSettingJson.getNickname());
        Glide.with(this).load(userSettingJson.getAvatar()).asBitmap().placeholder(R.mipmap.icon_user_def_photo).error(R.mipmap
                .icon_user_def_photo).into(new ImageViewTarget<Bitmap>(rivAvatar) {
            @Override
            protected void setResource(Bitmap resource) {
                rivAvatar.setImageBitmap(resource);
            }
        });
        isBan = userSettingJson.getIs_banned();
        String is_writer = userSettingJson.getIs_writer();
        if (is_writer != null && is_writer.equals("1"))
            isWriter = true;
        else
            isWriter = false;
        if (isBan != null && "1".equals(isBan)) {

            vBan.setSelected(true);
        } else {
            vBan.setSelected(false);
        }
        plRootView.loadOver();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_MSG_BAN, isBan.equals("1") ? true : false));
    }
}
