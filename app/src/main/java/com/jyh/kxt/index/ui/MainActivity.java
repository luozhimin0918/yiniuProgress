package com.jyh.kxt.index.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatDelegate;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.bean.SignInfoJson;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.dao.DBManager;
import com.jyh.kxt.base.http.GlobalHttpRequest;
import com.jyh.kxt.base.impl.OnRequestPermissions;
import com.jyh.kxt.base.util.emoje.DBUtils;
import com.jyh.kxt.base.util.emoje.EmoticonsUtils;
import com.jyh.kxt.base.utils.DoubleClickUtils;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.utils.UmengLoginTool;
import com.jyh.kxt.base.utils.UmengShareUI;
import com.jyh.kxt.base.widget.night.ThemeUtil;
import com.jyh.kxt.chat.LetterActivity;
import com.jyh.kxt.chat.json.ChatRoomJson;
import com.jyh.kxt.chat.util.ChatSocketUtil;
import com.jyh.kxt.chat.util.OnChatMessage;
import com.jyh.kxt.datum.bean.CalendarFinanceBean;
import com.jyh.kxt.explore.ui.MoreActivity;
import com.jyh.kxt.index.presenter.MainPresenter;
import com.jyh.kxt.index.ui.fragment.AvFragment;
import com.jyh.kxt.index.ui.fragment.DatumFragment;
import com.jyh.kxt.index.ui.fragment.HomeFragment;
import com.jyh.kxt.index.ui.fragment.MarketFragment;
import com.jyh.kxt.index.ui.fragment.TradingFragment;
import com.jyh.kxt.score.ui.MyCoin2Activity;
import com.jyh.kxt.search.ui.SearchIndexActivity;
import com.jyh.kxt.trading.ui.AuthorActivity;
import com.jyh.kxt.trading.ui.PublishActivity;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.ui.AboutActivity;
import com.jyh.kxt.user.ui.CollectActivity;
import com.jyh.kxt.user.ui.EditUserInfoActivity;
import com.jyh.kxt.user.ui.LoginOrRegisterActivity;
import com.jyh.kxt.user.ui.SettingActivity;
import com.library.base.http.HttpDeliveryListener;
import com.library.base.http.VarConstant;
import com.library.bean.EventBusClass;
import com.library.manager.ActivityManager;
import com.library.util.NetUtils;
import com.library.util.RegexValidateUtil;
import com.library.util.SPUtils;
import com.library.widget.window.ToastView;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.magicwindow.MLinkAPIFactory;
import cn.magicwindow.mlink.annotation.MLinkDefaultRouter;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * 主界面
 */
@MLinkDefaultRouter
public class MainActivity extends BaseActivity implements DrawerLayout.DrawerListener, View.OnClickListener, OnChatMessage {

    @BindView(R.id.ll_content) LinearLayout llContent;
    @BindView(R.id.drawer_layout) public DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.rb_home) public RadioButton rbHome;
    @BindView(R.id.rb_audio_visual) public RadioButton rbAudioVisual;
    @BindView(R.id.rb_market) public RadioButton rbMarket;
    @BindView(R.id.rb_datum) public RadioButton rbDatum;
    @BindView(R.id.rb_probe) public RadioButton rbProbe;

    public MainPresenter mainPresenter;

    //Fragment相关
    public BaseFragment currentFragment;
    public HomeFragment homeFragment;
    public AvFragment avFragment;
    public MarketFragment marketFragment;
    public DatumFragment datumFragment;
    public TradingFragment exploreFragment;
    //侧边栏控件
    public RelativeLayout llHeaderLayout;
    TextView tvCollect, tvFocus, tvHistory, tvPl, tvActivity, tvShare, tvQuit, tvSetting, tvAbout, tvMine, tvPoint, tvLetter, tvSign,
            tvRedDot;
    RelativeLayout rlSign;
    ImageView ivSign, ivSignEnter;

    private RelativeLayout unLoginView, loginView;
    public RoundImageView loginPhoto;
    public TextView loginName;
    private ImageView ivQQ, ivSina, ivWx;
    private FrameLayout searchEdt;
    private LinearLayout collectBtn, focusBtn, historyBtn, plBtn, activityBtn, shareBtn, settingBtn, aboutBtn,
            themeBtn, loginBtn, quitBtn, mineBtn, pointBtn, letterBtn;
    private TextView tvTheme;
    private long oldClickNavigationTime;

    //未签到
    private LinearLayout rlUnSign;

    //任务未完成
    private LinearLayout rlUnTask;

    private TextView tvTaskHint;

    //魔窗web跳转参数
    public static String mwId = null;//跳转id
    public static String mwType = null;//跳转类型 true 列表页
    public static String mwPath = null;//跳转路径

    public int mActivityFrom = 0;//如果为0 则为默认进入  1 则表示内存回收重启,不显示广告弹窗  2 表示welcom界面已经加载完成数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            savedInstanceState.clear();

            try {
                List<Fragment> fragments = getSupportFragmentManager().getFragments();
                for (int i = 0; i < fragments.size(); i++) {
                    getSupportFragmentManager().beginTransaction().remove(fragments.get(i)).commitAllowingStateLoss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            ThemeUtil.removeAllCache();
            ActivityManager.getInstance().finishAllActivity();
            this.finish();

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(SpConstant.MAIN_ACTIVITY_FROM, 1);
            startActivity(intent);
            return;
        }


        overridePendingTransition(R.anim.activity_anim3, R.anim.activity_out1);
        setContentView(R.layout.activity_main, StatusBarColor.NO_COLOR);

        mActivityFrom = getIntent().getIntExtra(SpConstant.MAIN_ACTIVITY_FROM, 0);
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

        //初始化操作
        EmoticonsUtils.loadEmoticonToDB(this);
        DBUtils.toSDWriteFile(this, DBManager.dbName);


        ChatSocketUtil.getInstance().sendSocketParams(this, null, this);
    }

    /**
     * 侧边栏相关控件
     */
    private void initDrawer() {
        drawer.addDrawerListener(this);

        llHeaderLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.nav_header_main, null);

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
        tvTheme = (TextView) llHeaderLayout.findViewById(R.id.tv_theme);
        quitBtn = (LinearLayout) llHeaderLayout.findViewById(R.id.ll_quit);
        mineBtn = (LinearLayout) llHeaderLayout.findViewById(R.id.ll_mine);
        letterBtn = (LinearLayout) llHeaderLayout.findViewById(R.id.ll_letter);
        pointBtn = (LinearLayout) llHeaderLayout.findViewById(R.id.ll_postPoint);

        tvPoint = (TextView) llHeaderLayout.findViewById(R.id.tv_postPoint);
        tvCollect = ButterKnife.findById(llHeaderLayout, R.id.tv_collect);
        tvFocus = ButterKnife.findById(llHeaderLayout, R.id.tv_focus);
        tvHistory = ButterKnife.findById(llHeaderLayout, R.id.tv_history);
        tvPl = ButterKnife.findById(llHeaderLayout, R.id.tv_pl);
        tvActivity = ButterKnife.findById(llHeaderLayout, R.id.tv_activity);
        tvShare = ButterKnife.findById(llHeaderLayout, R.id.tv_share);
        tvQuit = ButterKnife.findById(llHeaderLayout, R.id.tv_quit);
        tvSetting = ButterKnife.findById(llHeaderLayout, R.id.tv_setting);
        tvAbout = ButterKnife.findById(llHeaderLayout, R.id.tv_about);
        tvMine = ButterKnife.findById(llHeaderLayout, R.id.tv_mine);
        tvLetter = ButterKnife.findById(llHeaderLayout, R.id.tv_letter);

        rlSign = ButterKnife.findById(llHeaderLayout, R.id.rl_sign);
        ivSign = ButterKnife.findById(llHeaderLayout, R.id.iv_sign);
        ivSignEnter = ButterKnife.findById(llHeaderLayout, R.id.iv_sign_enter);
        tvSign = ButterKnife.findById(llHeaderLayout, R.id.tv_sign);

        tvRedDot = ButterKnife.findById(llHeaderLayout, R.id.head_red_dot);


        //签到未签到状态

        rlUnSign = ButterKnife.findById(llHeaderLayout, R.id.rl_un_sign);
        rlUnTask = ButterKnife.findById(llHeaderLayout, R.id.rl_un_task);
        tvTaskHint = ButterKnife.findById(llHeaderLayout, R.id.tv_task_hint);


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
        quitBtn.setOnClickListener(this);
        mineBtn.setOnClickListener(this);
        pointBtn.setOnClickListener(this);
        letterBtn.setOnClickListener(this);
        rlSign.setOnClickListener(this);

        //用户登录信息
        changeUserStatus(LoginUtils.getUserInfo(this));

        int theme = ThemeUtil.getAlertTheme(this);
        switch (theme) {
            case android.support.v7.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert:
                tvTheme.setText("白天");
                break;
            case android.support.v7.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert:
                tvTheme.setText("夜间");
                break;
        }
    }

    public void clickSwitchFragment(View view) {
        clickSwitchFragment(view.getId());
    }


    public void clickSwitchFragment(int viewId) {
        BaseFragment mClickFragment = null;
        long currentTime = System.currentTimeMillis();
        switch (viewId) {
            case R.id.rb_home:
                homeFragment = homeFragment == null ? HomeFragment.newInstance() : homeFragment;
                mClickFragment = homeFragment;
                if (currentFragment == homeFragment && currentTime - oldClickNavigationTime < 2000) {
                    homeFragment.doubleClickFragment();
                }
                break;
            case R.id.rb_audio_visual:
                avFragment = avFragment == null ? AvFragment.newInstance() : avFragment;
                mClickFragment = avFragment;
                if (currentFragment == avFragment && currentTime - oldClickNavigationTime < 2000) {
                    avFragment.doubleClickFragment();
                }
                break;
            case R.id.rb_market:
                marketFragment = marketFragment == null ? MarketFragment.newInstance() : marketFragment;
                mClickFragment = marketFragment;
                if (currentFragment == marketFragment && currentTime - oldClickNavigationTime < 2000) {
                    marketFragment.doubleClickFragment();
                }
                break;
            case R.id.rb_datum:
                datumFragment = datumFragment == null ? DatumFragment.newInstance() : datumFragment;
                mClickFragment = datumFragment;
                if (currentFragment == datumFragment && currentTime - oldClickNavigationTime < 2000) {
                    datumFragment.doubleClickFragment();
                }
                break;
            case R.id.rb_probe:
                exploreFragment = exploreFragment == null ? new TradingFragment() : exploreFragment;
                mClickFragment = exploreFragment;
                if (currentFragment == exploreFragment && currentTime - oldClickNavigationTime < 2000) {
                    exploreFragment.doubleClickFragment();
                }
                break;
        }
        if (mClickFragment != null) {
            mainPresenter.switchToFragment(mClickFragment);
        }
        currentFragment = mClickFragment;
        oldClickNavigationTime = System.currentTimeMillis();
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
//        GlobalHttpRequest.getInstance().getSignInfo(this, new HttpDeliveryListener<SignInfoJson>() {
//            @Override
//            public void onResponse(SignInfoJson signInfoJson) {
//                updateLeftSignState(signInfoJson);
//            }
//
//            @Override
//            public void onErrorResponse() {
//
//            }
//        });
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
            if (DoubleClickUtils.isFastDoubleClick(2000)) {
                try {
                    int currentVersion = Build.VERSION.SDK_INT;
                    if (currentVersion > Build.VERSION_CODES.ECLAIR_MR1) {
                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(startMain);
                        System.exit(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ThemeUtil.removeAllCache();
                ActivityManager.getInstance().finishAllActivity();

                super.onBackPressed();
            } else {
                ToastView.makeText3(this, "双击退出应用");
            }
        }

    }

    boolean isYeJianTheme = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.riv_avatar:
                startActivity(new Intent(this, EditUserInfoActivity.class));
                break;
            case R.id.ll_collect:
                //收藏
                startActivity(new Intent(this, CollectActivity.class));
                break;
            case R.id.fl_search:
                //搜索
//                Intent searchIntent = new Intent(this, SearchActivity.class);
//                startActivity(searchIntent);
                Intent searchIntent = new Intent(this, SearchIndexActivity.class);
                startActivity(searchIntent);
                break;
            case R.id.ll_pl:
                //评论
                if (LoginUtils.isLogined(this)) {
                    startActivity(new Intent(this, CommentListActivity.class));
                } else {
                    mainPresenter.showLoginDialog();
                }
                break;
            case R.id.ll_activity:
                //活动
                Intent activityIntent = new Intent(this, MoreActivity.class);
                activityIntent.putExtra(IntentConstant.TYPE, VarConstant.EXPLORE_ACTIVITY);
                startActivity(activityIntent);
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
                if (LoginUtils.isLogined(this)) {
                    startActivity(new Intent(this, AttentionActivity.class));
                } else {
                    mainPresenter.showLoginDialog();
                }
                break;
            case R.id.ll_history:
                //浏览历史
                startActivity(new Intent(this, BrowerHistoryActivity.class));
                break;
            case R.id.ll_theme:
                //夜间模式
                int theme = ThemeUtil.getAlertTheme(this);
                switch (theme) {
                    case android.support.v7.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert:
                        SPUtils.save(this, SpConstant.SETTING_DAY_NIGHT, false);
                        setDayNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        tvTheme.setText("夜间");
                        break;
                    case android.support.v7.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert:
                        SPUtils.save(this, SpConstant.SETTING_DAY_NIGHT, true);
                        setDayNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        tvTheme.setText("白天");
                        break;
                }
                ThemeUtil.changeCacheActionTheme(this);
                break;
            case R.id.ll_setting:
                //设置
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.iv_qq:
                //QQ
                if (NetUtils.isNetworkAvailable(this)) {
                    if (UMShareAPI.get(this).isInstall(this, SHARE_MEDIA.QQ)) {
                        UmengLoginTool.umenglogin(this, SHARE_MEDIA.QQ);
                    } else {
                        ToastView.makeText3(this, "未安装QQ");
                    }
                } else {
                    ToastView.makeText3(getContext(), "暂无网络,请稍后再试");
                }

                break;
            case R.id.iv_sina:
                //微博
                if (NetUtils.isNetworkAvailable(this)) {
                    UmengLoginTool.umenglogin(this, SHARE_MEDIA.SINA);
                } else {
                    ToastView.makeText3(getContext(), "暂无网络,请稍后再试");
                }
                break;
            case R.id.iv_wx:
                //微信
                if (NetUtils.isNetworkAvailable(this)) {
                    if (UMShareAPI.get(this).isInstall(this, SHARE_MEDIA.WEIXIN)) {
                        UmengLoginTool.umenglogin(this, SHARE_MEDIA.WEIXIN);
                    } else {
                        ToastView.makeText3(this, "未安装微信");
                    }
                } else {
                    ToastView.makeText3(getContext(), "暂无网络,请稍后再试");
                }
                break;
            case R.id.ll_login:
                //登录
                startActivity(new Intent(this, LoginOrRegisterActivity.class));
                break;
            case R.id.ll_quit:
                mainPresenter.showQuitDialog();
                break;
            case R.id.ll_postPoint:
                //发布观点
                UserJson pointUserJson = LoginUtils.getUserInfo(this);
                if (pointUserJson == null) {
                    Intent intent = new Intent(this, LoginOrRegisterActivity.class);
                    startActivity(intent);
                    return;
                }
                if (pointUserJson.getWriter_id() == null) {
                    Intent intent = new Intent(this, WebActivity.class);
                    intent.putExtra(IntentConstant.NAME, "专栏入驻");
                    intent.putExtra(IntentConstant.WEBURL, HttpConstant.ZLRZ_URL + "?uid=" + pointUserJson.getUid());
                    startActivity(intent);
                    return;
                }

                Intent publishIntent = new Intent(this, PublishActivity.class);
                startActivity(publishIntent);
                break;
            case R.id.ll_mine:
                //我的专栏
                UserJson userInfo = LoginUtils.getUserInfo(this);
                if (userInfo != null && userInfo.getWriter_id() != null) {
                    Intent intent = new Intent(this, AuthorActivity.class);
                    intent.putExtra(IntentConstant.O_ID, userInfo.getWriter_id());
                    startActivity(intent);
                }
                break;
            case R.id.ll_letter:
                //我的私信
                startActivity(new Intent(this, LetterActivity.class));
                break;
            case R.id.rl_sign:
                //签到
                startActivity(new Intent(this, MyCoin2Activity.class));
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusClass eventBus) {
        switch (eventBus.fromCode) {
            case EventBusClass.EVENT_LOGIN:
            case EventBusClass.EVENT_LOGIN_UPDATE:
                UserJson userJson = (UserJson) eventBus.intentObj;
                changeUserStatus(userJson);
                break;
            case EventBusClass.EVENT_LOGOUT:
                changeUserStatus(null);
                break;
            case EventBusClass.EVENT_CHANGEUSERINFO:
                changeUserStatus((UserJson) eventBus.intentObj);
                break;
            case EventBusClass.EVENT_UNREAD_MSG:

                break;
            case EventBusClass.EVENT_COIN_SIGN:
                SignInfoJson signInfoJson = (SignInfoJson) eventBus.intentObj;
                GlobalHttpRequest.getInstance().setSignInfoJson(signInfoJson);
                updateLeftSignState(signInfoJson);
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

            if (userJson.getIs_unread_msg() == 1) {
                tvRedDot.setVisibility(View.VISIBLE);
            } else {
                tvRedDot.setVisibility(View.GONE);
            }


            loginView.setVisibility(View.VISIBLE);
            unLoginView.setVisibility(View.GONE);
            quitBtn.setVisibility(View.VISIBLE);
            letterBtn.setVisibility(View.VISIBLE);

            if (!RegexValidateUtil.isEmpty(userJson.getWriter_id()) && !RegexValidateUtil.isEmpty(userJson.getWriter_name())) {
                mineBtn.setVisibility(View.VISIBLE);
                pointBtn.setVisibility(View.VISIBLE);
            } else {
                mineBtn.setVisibility(View.GONE);
                pointBtn.setVisibility(View.GONE);
            }

            String pictureStr = userJson.getPicture();
            Glide.with(getContext())
                    .load(pictureStr)
                    .asBitmap()
                    .override(imgSize, imgSize)

                    .error(R.mipmap.icon_user_def_photo)
                    .placeholder(R.mipmap.icon_user_def_photo)

                    .into(loginPhoto);

            Glide.with(getContext())
                    .load(pictureStr)
                    .crossFade(1000)
                    .bitmapTransform(new BlurTransformation(getContext(), 15, 4)) // “23”：设置模糊度(在0.0到25.0之间)，默认”25";
                    // "4":图片缩放比例,默认“1”。
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable>
                                glideAnimation) {
                            loginView.setBackground(resource);
                        }
                    });

            loginName.setText(userJson.getNickname());


            GlobalHttpRequest.getInstance().getSignInfo(this, new HttpDeliveryListener<SignInfoJson>() {
                @Override
                public void onResponse(SignInfoJson signInfoJson) {
                    updateLeftSignState(signInfoJson);
                }

                @Override
                public void onErrorResponse() {

                }
            });
        } else {
            tvRedDot.setVisibility(View.GONE);
            loginView.setVisibility(View.GONE);
            unLoginView.setVisibility(View.VISIBLE);
            quitBtn.setVisibility(View.GONE);
            letterBtn.setVisibility(View.GONE);
            mineBtn.setVisibility(View.GONE);
            pointBtn.setVisibility(View.GONE);

            loginPhoto.setImageResource(R.mipmap.icon_user_def_photo);

            loginName.setText("");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        UmengShareUI.onActivityResult(this, requestCode, resultCode, data);
        if (currentFragment != null) {
            currentFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private long baseTime;
    private OnRequestPermissions onRequestPermissions;
    private CalendarFinanceBean calendarFinanceBean;

    /**
     * 权限申请管理
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            if (datumFragment != null) {
                datumFragment.obtainAlarmPermissionsSuccess(baseTime, calendarFinanceBean, onRequestPermissions);
            }
        } else {
            if (datumFragment != null) {
                datumFragment.obtainAlarmPermissionsFailure();
            }
        }
    }

    @Override
    public void recreate() {
        super.recreate()
        ;
    }

    /**
     * 添加日历访问权限
     *
     * @param calendarFinanceBean
     * @param baseTime
     * @param onRequestPermissions
     */
    public void checkAlarmPermissions(long baseTime,
                                      CalendarFinanceBean calendarFinanceBean,
                                      OnRequestPermissions onRequestPermissions) {

        this.baseTime = baseTime;
        this.calendarFinanceBean = calendarFinanceBean;
        this.onRequestPermissions = onRequestPermissions;

        int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR);
        int readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR);

        if (writePermission != PackageManager.PERMISSION_GRANTED
                || readPermission != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_CALENDAR,
                            Manifest.permission.READ_CALENDAR}, 1000);
        } else {
            if (datumFragment != null) {
                datumFragment.obtainAlarmPermissionsSuccess(baseTime, calendarFinanceBean, onRequestPermissions);
            }
        }
    }

    @Override
    public void onChatMessage(ChatRoomJson chatRoomJson) {

        UserJson userInfo = LoginUtils.getUserInfo(this);
        if (userInfo != null) {
            userInfo.setIs_unread_msg(1);
            LoginUtils.changeUserInfo(this, userInfo);//重新保存
            EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_LOGIN_UPDATE, userInfo));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ChatSocketUtil.getInstance().unOnChatMessage(this);

        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showUserCenter() {
        drawer.openDrawer(Gravity.LEFT);
    }


    @Override
    protected void onChangeTheme() {
        try {
            if (LoginUtils.isLogined(this)) {
                UserJson userInfo = LoginUtils.getUserInfo(this);
                super.onChangeTheme();
                Glide.with(getContext())
                        .load(userInfo.getPicture())
                        .crossFade(1000)
                        .bitmapTransform(new BlurTransformation(getContext(), 23, 4)) // “23”：设置模糊度(在0.0到25.0之间)
                        // ，默认”25";"4":图片缩放比例,默认“1”。
                        .into(new SimpleTarget<GlideDrawable>() {
                            @Override
                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable>
                                    glideAnimation) {
                                loginView.setBackground(resource);
                            }
                        });
            }

            leftTextFontColor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void leftTextFontColor() {
        int fontColor = ContextCompat.getColor(this, R.color.font_color5);
        tvCollect.setTextColor(fontColor);
        tvFocus.setTextColor(fontColor);
        tvHistory.setTextColor(fontColor);
        tvPl.setTextColor(fontColor);
        tvActivity.setTextColor(fontColor);
        tvShare.setTextColor(fontColor);
        tvQuit.setTextColor(fontColor);

        tvSetting.setTextColor(fontColor);
        tvAbout.setTextColor(fontColor);
        tvTheme.setTextColor(fontColor);

        ivSign.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.icon_score_sign_small));
        ivSignEnter.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.icon_score_enter));
        tvSign.setTextColor(ContextCompat.getColor(this, R.color.white));
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (!RegexValidateUtil.isEmpty(mwPath)) {
                String oclass = null;
                String oaction = null;
                String oid = mwId;

                switch (mwPath) {
                    case "/comment":
                        oclass = VarConstant.OCLASS_NEWS;
                        oaction = VarConstant.OACTION_DIANPING;
                        break;
                    case "/fastNews":
                        //快讯
                        oclass = VarConstant.OCLASS_FLASH;
                        if ("true".equals(mwType)) {
                            oaction = VarConstant.OACTION_LIST;
                        } else {
                            oaction = VarConstant.OACTION_DETAIL;
                        }
                        break;
                    case "/news":
                        //要闻
                        oclass = VarConstant.OCLASS_NEWS;
                        if ("true".equals(mwType)) {
                            oaction = VarConstant.OACTION_LIST;
                        } else {
                            oaction = VarConstant.OACTION_DETAIL;
                        }
                        break;
                    case "/price":
                        //行情
                        oclass = VarConstant.OCLASS_QUOTES;
                        if ("true".equals(mwType)) {
                            oaction = VarConstant.OACTION_LIST;
                        } else {
                            oaction = VarConstant.OACTION_DETAIL;
                        }
                        break;
                    case "/video":
                        oclass = VarConstant.OCLASS_VIDEO;
                        if ("true".equals(mwType)) {
                            oaction = VarConstant.OACTION_LIST;
                        } else {
                            oaction = VarConstant.OACTION_DETAIL;
                        }
                        break;
                    case "/datacenter":
                        oclass = VarConstant.OCLASS_DATA;
                        if ("true".equals(mwType)) {
                            oaction = VarConstant.OACTION_LIST;
                        } else {
                            oaction = VarConstant.OACTION_DETAIL;
                        }
                        break;
                    case "/rili":
                        oclass = VarConstant.OCLASS_DATA;
                        oaction = VarConstant.OACTION_RL;
                        break;
                }

                if (!RegexValidateUtil.isEmpty(oclass) && !RegexValidateUtil.isEmpty(oaction) && !RegexValidateUtil
                        .isEmpty(oid)) {
                    JumpUtils.jump(this, oclass, oaction, oid, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mwId = null;
        mwPath = null;
    }

    //读写权限检查
    public void initPermission() {
        int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED
                || readPermission != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 2000);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri mLink = intent.getData();
        if (mLink != null) {
            MLinkAPIFactory.createAPI(this).router(mLink);
        } else {
            MLinkAPIFactory.createAPI(this).checkYYB();
        }
    }


    /**
     * 刷新左边的签到状态
     *
     * @param signInfoJson
     */
    private void updateLeftSignState(SignInfoJson signInfoJson) {

        if (signInfoJson.getSign_state() == 0) {
            rlUnSign.setVisibility(View.VISIBLE);
        } else {
            rlUnSign.setVisibility(View.GONE);

            if (signInfoJson.getTask_state() == 0) {
                rlUnTask.setVisibility(View.VISIBLE);
            } else {
                rlUnTask.setVisibility(View.GONE);
            }
        }


        if (signInfoJson.getSign_state() == 1 && signInfoJson.getTask_state() == 1) {
            //任务已经完成
            tvTaskHint.setText("任务完成");
            rlUnTask.setVisibility(View.VISIBLE);
        }
    }
}
