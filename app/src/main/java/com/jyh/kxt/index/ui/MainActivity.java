package com.jyh.kxt.index.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.index.presenter.MainPresenter;
import com.jyh.kxt.user.ui.SettingActivity;

import butterknife.BindView;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity implements DrawerLayout.DrawerListener {

    @BindView(R.id.ll_content) LinearLayout llContent;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.center_content) FrameLayout flCenterContent;


    private MainPresenter mainPresenter;

    //侧边栏控件
    public LinearLayout llHeaderLayout;
    public ImageView ivBlurAvatar;
    public RoundImageView rivAvatar;
    public TextView tvNickName;

    //Fragment相关
    public BaseFragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main, StatusBarColor.NO_COLOR);

        mainPresenter = new MainPresenter(this);
        //侧边栏相关控件
        drawer.addDrawerListener(this);

        llHeaderLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.nav_header_main, null);
        navigationView.addView(llHeaderLayout);

        ivBlurAvatar = (ImageView) llHeaderLayout.findViewById(R.id.iv_blur_avatar);
        rivAvatar = (RoundImageView) llHeaderLayout.findViewById(R.id.riv_avatar);
        tvNickName = (TextView) llHeaderLayout.findViewById(R.id.tv_nickname);
        mainPresenter.initHeaderLayout();

    }

    public void clickSwitchFragment(View view) {
        switch (view.getId()) {
            case R.id.rb_home:
                startActivity(new Intent(this, SettingActivity.class));
                break;
        }
    }

    /**
     * 抽屉滑动监听
     */
    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        float mainViewOffset = drawerView.getWidth() * slideOffset;
        ViewCompat.setTranslationX(llContent, mainViewOffset);
    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
