package com.jyh.kxt.index.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
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
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.impl.OnRequestPermissions;
import com.jyh.kxt.base.util.emoje.EmoticonsUtils;
import com.jyh.kxt.base.utils.DoubleClickUtils;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.utils.UmengLoginTool;
import com.jyh.kxt.base.utils.UmengShareTool;
import com.jyh.kxt.base.widget.night.ThemeUtil;
import com.jyh.kxt.datum.bean.CalendarFinanceBean;
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
import com.library.base.http.VarConstant;
import com.library.bean.EventBusClass;
import com.library.manager.ActivityManager;
import com.library.util.RegexValidateUtil;
import com.library.util.SPUtils;
import com.library.widget.window.ToastView;
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.magicwindow.MLinkAPIFactory;
import cn.magicwindow.mlink.annotation.MLinkDefaultRouter;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * 主界面
 */
@MLinkDefaultRouter
public class MainActivity extends BaseActivity implements DrawerLayout.DrawerListener, View.OnClickListener {

    @BindView(R.id.ll_content) LinearLayout llContent;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
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
    public ExploreFragment exploreFragment;
    //侧边栏控件
    public LinearLayout llHeaderLayout;
    TextView tvCollect, tvFocus, tvHistory, tvPl, tvActivity, tvShare, tvQuit, tvSetting, tvAbout;

    private RelativeLayout unLoginView, loginView;
    public RoundImageView loginPhoto;
    public TextView loginName;
    private ImageView ivQQ, ivSina, ivWx;
    private FrameLayout searchEdt;
    private LinearLayout collectBtn, focusBtn, historyBtn, plBtn, activityBtn, shareBtn, settingBtn, aboutBtn,
            themeBtn, loginBtn, quitBtn;
    private TextView tvTheme;
    private long oldClickNavigationTime;

    //魔窗web跳转参数
    public static String mwId = null;//跳转id
    public static String mwType = null;//跳转类型 true 列表页
    public static String mwPath = null;//跳转路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_anim3, R.anim.activity_out1);
        setContentView(R.layout.activity_main, StatusBarColor.NO_COLOR);

        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            savedInstanceState.clear();

            ThemeUtil.removeAllCache();
            ActivityManager.getInstance().finishAllActivity();

            return;
        }

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
//        DBUtils.toSDWriteFile(this, DBManager.dbName);
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
        tvTheme = (TextView) llHeaderLayout.findViewById(R.id.tv_theme);
        quitBtn = (LinearLayout) llHeaderLayout.findViewById(R.id.ll_quit);

        tvCollect = ButterKnife.findById(llHeaderLayout, R.id.tv_collect);
        tvFocus = ButterKnife.findById(llHeaderLayout, R.id.tv_focus);
        tvHistory = ButterKnife.findById(llHeaderLayout, R.id.tv_history);
        tvPl = ButterKnife.findById(llHeaderLayout, R.id.tv_pl);
        tvActivity = ButterKnife.findById(llHeaderLayout, R.id.tv_activity);
        tvShare = ButterKnife.findById(llHeaderLayout, R.id.tv_share);
        tvQuit = ButterKnife.findById(llHeaderLayout, R.id.tv_quit);
        tvSetting = ButterKnife.findById(llHeaderLayout, R.id.tv_setting);
        tvAbout = ButterKnife.findById(llHeaderLayout, R.id.tv_about);


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
                exploreFragment = exploreFragment == null ? ExploreFragment.newInstance() : exploreFragment;
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
                /*try {
                    int currentVersion = Build.VERSION.SDK_INT;
                    if (currentVersion > Build.VERSION_CODES.ECLAIR_MR1) {
                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(startMain);
                        System.exit(0);
                    } else {// android2.1
                        android.app.ActivityManager am = (android.app.ActivityManager) MainActivity.this
                                .getSystemService
                                        (ACTIVITY_SERVICE);
                        am.restartPackage(getPackageName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                */
                ShareTinkerInternals.killAllOtherProcess(getApplicationContext());
                android.os.Process.killProcess(android.os.Process.myPid());

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
                //个人中心
                Pair[] pairs = {
                        new Pair<View, String>(loginPhoto, EditUserInfoActivity.VIEW_NAME_IMG),
                        new Pair<View, String>(loginName, EditUserInfoActivity.VIEW_NAME_TITLE)};

                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation
                        (this, pairs);
                Intent intent = new Intent(this, EditUserInfoActivity.class);
                ActivityCompat.startActivity(this, intent, activityOptionsCompat.toBundle());
                break;
            case R.id.ll_collect:
                //收藏
                startActivity(new Intent(this, CollectActivity.class));
                break;
            case R.id.fl_search:
                //搜索
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivity(searchIntent);
                break;
            case R.id.ll_pl:
                //评论
                if (LoginUtils.isLogined(this)) {
                    startActivity(new Intent(this, MyCommentActivity.class));
                } else {
                    mainPresenter.showLoginDialog();
                }
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
            case R.id.ll_quit:
                mainPresenter.showQuitDialog();
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
            case EventBusClass.EVENT_LOGOUT:
                changeUserStatus(null);
                break;
            case EventBusClass.EVENT_CHANGEUSERINFO:
                changeUserStatus((UserJson) eventBus.intentObj);
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
            quitBtn.setVisibility(View.VISIBLE);

            String pictureStr = userJson.getPicture();
            Glide.with(getContext())
                    .load(pictureStr)
                    .asBitmap()
                    .override(imgSize, imgSize)

                    .error(R.mipmap.icon_user_def_photo)
                    .placeholder(R.mipmap.icon_user_def_photo)

                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap>
                                glideAnimation) {
                            loginPhoto.setImageBitmap(resource);
                        }
                    });

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
        } else {
            loginView.setVisibility(View.GONE);
            unLoginView.setVisibility(View.VISIBLE);
            quitBtn.setVisibility(View.GONE);

            loginPhoto.setImageResource(R.mipmap.icon_user_def_photo);

            loginName.setText("");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UmengShareTool.onActivityResult(this, requestCode, resultCode, data);
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
        super.recreate();
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
    protected void onDestroy() {
        super.onDestroy();
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
                        //todo 旧版中的点评 及现在的要闻
                        oclass = VarConstant.OCLASS_NEWS;
                        oaction=VarConstant.OACTION_DIANPING;
                        break;
                    case "/fastNews":
                        //快讯
                        oclass = VarConstant.OCLASS_FLASH;
                        if("true".equals(mwType)){
                            oaction=VarConstant.OACTION_LIST;
                        }else{
                            oaction=VarConstant.OACTION_DETAIL;
                        }
                        break;
                    case "/news":
                        //要闻
                        oclass = VarConstant.OCLASS_NEWS;
                        if("true".equals(mwType)){
                            oaction=VarConstant.OACTION_LIST;
                        }else{
                            oaction=VarConstant.OACTION_DETAIL;
                        }
                        break;
                    case "/price":
                        //行情
                        oclass = VarConstant.OCLASS_QUOTES;
                        if("true".equals(mwType)){
                            oaction=VarConstant.OACTION_LIST;
                        }else{
                            oaction=VarConstant.OACTION_DETAIL;
                        }
                        break;
                    case "/video":
                        oclass = VarConstant.OCLASS_VIDEO;
                        if("true".equals(mwType)){
                            oaction=VarConstant.OACTION_LIST;
                        }else{
                            oaction=VarConstant.OACTION_DETAIL;
                        }
                        break;
                    case "/datacenter":
                        oclass = VarConstant.OCLASS_DATA;
                        if("true".equals(mwType)){
                            oaction=VarConstant.OACTION_LIST;
                        }else{
                            oaction=VarConstant.OACTION_DETAIL;
                        }
                        break;
                    case "/rili":
                        oclass = VarConstant.OCLASS_DATA;
                        oaction = VarConstant.OACTION_RL;
                        break;
                }

                if (!RegexValidateUtil.isEmpty(oclass) && !RegexValidateUtil.isEmpty(oaction) && !RegexValidateUtil.isEmpty(oid)) {
                    JumpUtils.jump(this, oclass, oaction, oid, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mwId = null;
        mwPath = null;
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
}
