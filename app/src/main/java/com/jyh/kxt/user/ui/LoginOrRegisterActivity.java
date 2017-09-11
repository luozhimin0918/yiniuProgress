package com.jyh.kxt.user.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.base.utils.UmengShareUI;
import com.jyh.kxt.user.ui.fragment.LoginFragment;
import com.jyh.kxt.user.ui.fragment.RegisterFragment;
import com.library.bean.EventBusClass;
import com.library.util.SystemUtil;
import com.library.widget.tablayout.SlidingTabLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:登录注册界面
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/25.
 */

public class LoginOrRegisterActivity extends BaseActivity {

    @BindView(R.id.sv_rootView) public ScrollView rootView;
    @BindView(R.id.iv_close) ImageView ivClose;
    @BindView(R.id.stl_navigation_bar) SlidingTabLayout stlNavigationBar;
    @BindView(R.id.fl_bg) FrameLayout flBg;
    @BindView(R.id.vp_content) ViewPager vpContent;
    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;
    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_anim1, R.anim.activity_out1);

        setContentView(R.layout.activity_user_login_register, StatusBarColor.NO_COLOR);

        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] tabs = new String[]{"注册", "登录"};

        loginFragment = new LoginFragment();
        registerFragment = new RegisterFragment();
        fragmentList.add(registerFragment);
        fragmentList.add(loginFragment);

        vpContent.setAdapter(new BaseFragmentAdapter(getSupportFragmentManager(), fragmentList));

        stlNavigationBar.setViewPager(vpContent, tabs);

        stlNavigationBar.setCurrentTab(1);

        DisplayMetrics screenDisplay = SystemUtil.getScreenDisplay(this);
        stlNavigationBar.setTabWidth(SystemUtil.px2dp(this, screenDisplay.widthPixels / 2));
    }

    @OnClick(R.id.iv_close)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                onBackPressed();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusClass eventBus) {
        if (eventBus.fromCode == EventBusClass.EVENT_LOGIN) {
            setResult(Activity.RESULT_OK);
            onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UmengShareUI.onActivityResult(this, requestCode, resultCode, data);
    }
}
