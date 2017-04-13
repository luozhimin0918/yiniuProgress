package com.jyh.kxt.index.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.utils.UmengShareTool;
import com.jyh.kxt.index.presenter.MainPresenter;
import com.jyh.kxt.index.ui.fragment.AvFragment;
import com.jyh.kxt.index.ui.fragment.DatumFragment;
import com.jyh.kxt.index.ui.fragment.HomeFragment;
import com.jyh.kxt.index.ui.fragment.MarketFragment;
import com.jyh.kxt.index.ui.fragment.ProbeFragment;
import com.jyh.kxt.user.ui.EditUserInfoActivity;

import butterknife.BindView;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity implements DrawerLayout.DrawerListener, View.OnClickListener {

    @BindView(R.id.ll_content) LinearLayout llContent;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;

    private MainPresenter mainPresenter;

    //侧边栏控件
    public LinearLayout llHeaderLayout;
    public ImageView ivBlurAvatar;
    public RoundImageView rivAvatar;
    public TextView tvNickName;
    public TextView tvCollect;//收藏
    public TextView tvShare;//分享
    public TextView tvComment;//评论
    public TextView tvReply;//回复
    public TextView tvActivity;//活动


    //Fragment相关
    public BaseFragment currentFragment;
    private HomeFragment homeFragment;
    private AvFragment avFragment;
    private MarketFragment marketFragment;
    private DatumFragment datumFragment;
    private ProbeFragment probeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main, StatusBarColor.NO_COLOR);

        mainPresenter = new MainPresenter(this);

        //侧边栏相关控件
        initDrawer();

        mainPresenter.initHeaderLayout();

        clickSwitchFragment(R.id.rb_home);
    }

    /**
     * 侧边栏相关控件
     */
    private void initDrawer() {
        drawer.addDrawerListener(this);

        llHeaderLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.nav_header_main, null);
        navigationView.addView(llHeaderLayout);

        ivBlurAvatar = (ImageView) llHeaderLayout.findViewById(R.id.iv_blur_avatar);
        rivAvatar = (RoundImageView) llHeaderLayout.findViewById(R.id.riv_avatar);
        tvNickName = (TextView) llHeaderLayout.findViewById(R.id.tv_nickname);
        tvCollect = (TextView) llHeaderLayout.findViewById(R.id.tv_collect);
        tvComment = (TextView) llHeaderLayout.findViewById(R.id.tv_comment);
        tvReply = (TextView) llHeaderLayout.findViewById(R.id.tv_reply);
        tvActivity = (TextView) llHeaderLayout.findViewById(R.id.tv_activity);
        tvShare = (TextView) llHeaderLayout.findViewById(R.id.tv_share);

        rivAvatar.setOnClickListener(this);
        tvCollect.setOnClickListener(this);
        tvComment.setOnClickListener(this);
        tvReply.setOnClickListener(this);
        tvActivity.setOnClickListener(this);
        tvShare.setOnClickListener(this);

    }

    public void clickHeaderItem(View view) {
        switch (view.getId()) {
        }
    }

    public void clickSwitchFragment(View view) {
        clickSwitchFragment(view.getId());
    }

    public void clickSwitchFragment(int viewId) {
        BaseFragment mClickFragment = null;
        switch (viewId) {
            case R.id.rb_home:
                homeFragment = homeFragment == null ? HomeFragment.newInstance() : homeFragment;
                mClickFragment = homeFragment;
                break;
            case R.id.rb_audio_visual:
                avFragment = avFragment == null ? AvFragment.newInstance() : avFragment;
                mClickFragment = avFragment;
                break;
            case R.id.rb_market:
                marketFragment = marketFragment == null ? MarketFragment.newInstance() : marketFragment;
                mClickFragment = marketFragment;
                break;
            case R.id.rb_datum:
                datumFragment = datumFragment == null ? DatumFragment.newInstance() : datumFragment;
                mClickFragment = datumFragment;
                break;
            case R.id.rb_probe:
                probeFragment = probeFragment == null ? ProbeFragment.newInstance() : probeFragment;
                mClickFragment = probeFragment;
                break;
        }
        if (mClickFragment != null) {
            mainPresenter.switchToFragment(mClickFragment);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (currentFragment != null) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.riv_avatar:
                //个人中心
                Pair[] pairs = {
                        new Pair<View, String>(rivAvatar, EditUserInfoActivity.VIEW_NAME_IMG),
                        new Pair<View, String>(tvNickName, EditUserInfoActivity.VIEW_NAME_TITLE)};

                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pairs);
                Intent intent = new Intent(this, EditUserInfoActivity.class);
                ActivityCompat.startActivity(this, intent, activityOptionsCompat.toBundle());

                break;
            case R.id.tv_collect:
                //收藏
                break;
            case R.id.tv_comment:
                //评论
                break;
            case R.id.tv_reply:
                //回复
                break;
            case R.id.tv_activity:
                //活动
                break;
            case R.id.tv_share:
                //推荐
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UmengShareTool.onActivityResult(this, requestCode, resultCode, data);
    }
}
