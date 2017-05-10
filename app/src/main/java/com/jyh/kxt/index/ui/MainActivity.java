package com.jyh.kxt.index.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.dao.DBManager;
import com.jyh.kxt.base.util.emoje.DBUtils;
import com.jyh.kxt.base.utils.DoubleClickUtils;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.utils.UmengLoginTool;
import com.jyh.kxt.base.utils.UmengShareTool;
import com.jyh.kxt.index.presenter.MainPresenter;
import com.jyh.kxt.index.ui.fragment.AvFragment;
import com.jyh.kxt.index.ui.fragment.DatumFragment;
import com.jyh.kxt.index.ui.fragment.ExploreFragment;
import com.jyh.kxt.index.ui.fragment.HomeFragment;
import com.jyh.kxt.index.ui.fragment.MarketFragment;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.ui.AboutActivity;
import com.jyh.kxt.user.ui.CollectActivity;
import com.jyh.kxt.user.ui.EditUserInfoActivity;
import com.jyh.kxt.user.ui.LoginOrRegisterActivity;
import com.jyh.kxt.user.ui.SettingActivity;
import com.library.bean.EventBusClass;
import com.library.widget.window.ToastView;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity implements DrawerLayout.DrawerListener, View.OnClickListener {

    @BindView(R.id.ll_content) LinearLayout llContent;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.rb_home) public RadioButton rbHome;
    @BindView(R.id.rb_audio_visual) public RadioButton rbAudioVisual;
    @BindView(R.id.rb_market) public RadioButton rbMarket;
    @BindView(R.id.rb_datum) public RadioButton rbDatum;
    @BindView(R.id.rb_probe) public RadioButton rbProbe;

    private MainPresenter mainPresenter;

    //Fragment相关
    public BaseFragment currentFragment;
    public HomeFragment homeFragment;
    public AvFragment avFragment;
    public MarketFragment marketFragment;
    public DatumFragment datumFragment;
    public ExploreFragment exploreFragment;
    //侧边栏控件
    public LinearLayout llHeaderLayout;
    private RelativeLayout unLoginView, loginView;
    public RoundImageView loginPhoto;
    public TextView loginName;
    private ImageView ivQQ, ivSina, ivWx;
    private FrameLayout searchEdt;
    private LinearLayout collectBtn, focusBtn, historyBtn, plBtn, activityBtn, shareBtn, settingBtn, aboutBtn, themeBtn, loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main, StatusBarColor.NO_COLOR);

        mainPresenter = new MainPresenter(this);

        //侧边栏相关控件
        initDrawer();

        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mainPresenter.postDelayRequest();

        clickSwitchFragment(R.id.rb_home);

        DBUtils.toSDWriteFile(this, DBManager.dbName);
    }

    /**
     * 侧边栏相关控件
     */
    private void initDrawer() {
        drawer.addDrawerListener(this);

        llHeaderLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.nav_header_main, null);
        navigationView.addView(llHeaderLayout);

        unLoginView = (RelativeLayout) llHeaderLayout.findViewById(R.id.rl_unlogin);
        loginView = (RelativeLayout) llHeaderLayout.findViewById(R.id.rl_login);

        loginPhoto = (RoundImageView) llHeaderLayout.findViewById(R.id.riv_avatar);
        loginName = (TextView) llHeaderLayout.findViewById(R.id.tv_nickname);

        loginBtn = (LinearLayout) llHeaderLayout.findViewById(R.id.ll_login);
        ivQQ = (ImageView) llHeaderLayout.findViewById(R.id.iv_qq);
        ivSina = (ImageView) llHeaderLayout.findViewById(R.id.iv_sina);
        ivWx = (ImageView) llHeaderLayout.findViewById(R.id.iv_wx);

        searchEdt = (FrameLayout) llHeaderLayout.findViewById(R.id.fl_search);
        collectBtn = (LinearLayout) llHeaderLayout.findViewById(R.id.ll_collect);
        focusBtn = (LinearLayout) llHeaderLayout.findViewById(R.id.ll_focus);
        historyBtn = (LinearLayout) llHeaderLayout.findViewById(R.id.ll_history);
        plBtn = (LinearLayout) llHeaderLayout.findViewById(R.id.ll_pl);
        activityBtn = (LinearLayout) llHeaderLayout.findViewById(R.id.ll_activity);
        shareBtn = (LinearLayout) llHeaderLayout.findViewById(R.id.ll_share);
        settingBtn = (LinearLayout) llHeaderLayout.findViewById(R.id.ll_setting);
        aboutBtn = (LinearLayout) llHeaderLayout.findViewById(R.id.ll_about);
        themeBtn = (LinearLayout) llHeaderLayout.findViewById(R.id.ll_theme);

        loginPhoto.setOnClickListener(this);
        ivQQ.setOnClickListener(this);
        ivSina.setOnClickListener(this);
        ivWx.setOnClickListener(this);

        loginBtn.setOnClickListener(this);
        collectBtn.setOnClickListener(this);
        focusBtn.setOnClickListener(this);
        historyBtn.setOnClickListener(this);
        plBtn.setOnClickListener(this);
        activityBtn.setOnClickListener(this);
        shareBtn.setOnClickListener(this);
        settingBtn.setOnClickListener(this);
        aboutBtn.setOnClickListener(this);
        themeBtn.setOnClickListener(this);
        searchEdt.setOnClickListener(this);

        //用户登录信息
        changeUserStatus(LoginUtils.getUserInfo(this));

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
                exploreFragment = exploreFragment == null ? ExploreFragment.newInstance() : exploreFragment;
                mClickFragment = exploreFragment;
                break;
        }
        if (mClickFragment != null) {
            mainPresenter.switchToFragment(mClickFragment);
        }
        currentFragment = mClickFragment;
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
            if (DoubleClickUtils.isFastDoubleClick(500))
                super.onBackPressed();
            else
                ToastView.makeText3(this, "双击退出应用");
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.riv_avatar:
                //个人中心
                Pair[] pairs = {
                        new Pair<View, String>(loginPhoto, EditUserInfoActivity.VIEW_NAME_IMG),
                        new Pair<View, String>(loginName, EditUserInfoActivity.VIEW_NAME_TITLE)};

                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pairs);
                Intent intent = new Intent(this, EditUserInfoActivity.class);
                ActivityCompat.startActivity(this, intent, activityOptionsCompat.toBundle());
                break;
            case R.id.ll_collect:
                //收藏
                if (LoginUtils.isLogined(this)) {
                    startActivity(new Intent(this, CollectActivity.class));
                } else {
                    ToastView.makeText3(this, "请先登录");
                }
                break;
            case R.id.fl_search:
                //搜索
                startActivity(new Intent(this, SearchActivity.class));
                break;
            case R.id.ll_pl:
                //评论
                break;
            case R.id.ll_activity:
                //活动
                break;
            case R.id.ll_share:
                //推荐
                startActivity(new Intent(this, ShareActivity.class));
                break;
            case R.id.ll_about:
                //关于
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.ll_focus:
                //我的关注
                break;
            case R.id.ll_history:
                //浏览历史
                startActivity(new Intent(this, BrowerHistoryActivity.class));
                break;
            case R.id.ll_theme:
                //夜间模式
                break;
            case R.id.ll_setting:
                //设置
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.iv_qq:
                //QQ
                UmengLoginTool.umenglogin(this, SHARE_MEDIA.QQ);
                break;
            case R.id.iv_sina:
                //新浪
                UmengLoginTool.umenglogin(this, SHARE_MEDIA.SINA);
                break;
            case R.id.iv_wx:
                //微信
                UmengLoginTool.umenglogin(this, SHARE_MEDIA.WEIXIN);
                break;
            case R.id.ll_login:
                //登录
                startActivity(new Intent(this, LoginOrRegisterActivity.class));
                break;

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusClass eventBus) {
        switch (eventBus.fromCode) {
            case EventBusClass.EVENT_LOGIN:
                UserJson userJson = (UserJson) eventBus.intentObj;
                changeUserStatus(userJson);
                break;
        }
    }

    /**
     * 用户登录
     *
     * @param userJson
     */
    private void changeUserStatus(UserJson userJson) {

        int imgSize = (int) getResources().getDimension(R.dimen.drawer_head_login_avatarSize);

        if (userJson != null) {
            loginView.setVisibility(View.VISIBLE);
            unLoginView.setVisibility(View.GONE);

            Glide.with(getContext())
                    .load(userJson.getPicture())
                    .asBitmap()
                    .override(imgSize, imgSize)

                    .error(R.mipmap.icon_user_def_photo)
                    .placeholder(R.mipmap.icon_user_def_photo)

                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            loginPhoto.setImageBitmap(resource);
                        }
                    });

            loginName.setText(userJson.getNickname());

        } else {
            loginView.setVisibility(View.GONE);
            unLoginView.setVisibility(View.VISIBLE);

            loginPhoto.setImageResource(R.mipmap.icon_user_def_photo);

            loginName.setText("");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UmengShareTool.onActivityResult(this, requestCode, resultCode, data);
        if (currentFragment != null)
            currentFragment.onActivityResult(requestCode, resultCode, data);
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
}
