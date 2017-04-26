package com.jyh.kxt.user.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.user.ui.fragment.LoginFragment;
import com.jyh.kxt.user.ui.fragment.RegisterFragment;
import com.library.widget.tablayout.SlidingTabLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/25.
 */

public class LoginOrRegisterActivity extends BaseActivity {

    @BindView(R.id.stl_navigation_bar) SlidingTabLayout stlNavigationBar;
    @BindView(R.id.fl_content) ViewPager vpContent;

    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;
    private List<Fragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBindingView(R.layout.activity_user_login_register);

        String[] tabs = new String[]{"登录", "注册"};

        loginFragment = new LoginFragment();
        registerFragment = new RegisterFragment();
        fragmentList.add(loginFragment);
        fragmentList.add(registerFragment);

        vpContent.setAdapter(new BaseFragmentAdapter(getSupportFragmentManager(), fragmentList));

        stlNavigationBar.setViewPager(vpContent, tabs);
    }

    @OnClick(R.id.iv_close)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                onBackPressed();
                break;
        }
    }
}
