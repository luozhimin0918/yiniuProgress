package com.jyh.kxt.base.utils;

import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.text.TextUtils;
import android.widget.RadioButton;

import com.jyh.kxt.av.ui.VideoDetailActivity;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.json.JumpJson;
import com.jyh.kxt.datum.ui.DatumHistoryActivity;
import com.jyh.kxt.explore.ui.MoreActivity;
import com.jyh.kxt.index.ui.MainActivity;
import com.jyh.kxt.index.ui.ShareActivity;
import com.jyh.kxt.index.ui.WebActivity;
import com.jyh.kxt.index.ui.fragment.TradingFragment;
import com.jyh.kxt.main.ui.activity.DpActivity;
import com.jyh.kxt.main.ui.activity.FlashActivity;
import com.jyh.kxt.main.ui.activity.NewsContentActivity;
import com.jyh.kxt.main.ui.fragment.NewsFragment;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.jyh.kxt.market.ui.MarketDetailActivity;
import com.jyh.kxt.market.ui.fragment.MarketVPFragment;
import com.jyh.kxt.trading.ui.PublishActivity;
import com.jyh.kxt.trading.ui.ViewPointDetailActivity;
import com.jyh.kxt.trading.ui.fragment.ArticleFragment;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.VarConstant;
import com.library.manager.ActivityManager;
import com.library.util.DateUtils;
import com.library.util.RegexValidateUtil;

import cn.magicwindow.MLink;
import cn.magicwindow.MLinkAPIFactory;

/**
 * 项目名:Kxt
 * 类描述:跳转工具类
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/19.
 */

public class JumpUtils {


    public static void jump(BaseActivity activity, String o_class, String o_action, String o_id, String url) {
        JumpJson jumpJson = new JumpJson(o_action, o_class, o_id);
        jump(activity, jumpJson, url);
    }

    /**
     * 跳转
     *
     * @param context
     * @param jumpJson
     * @param url
     */
    public static void jump(final BaseActivity context, JumpJson jumpJson, String url) {
        jump(context, jumpJson, "", url);
    }

    /**
     * 跳转
     *
     * @param context
     * @param jumpJson
     * @param url
     */
    public static void jump(final BaseActivity context, JumpJson jumpJson, String webTitle, String url) {
        try {
            if (RegexValidateUtil.isEmpty(url)) {
                String o_class = jumpJson.getO_class();
                String o_action = jumpJson.getO_action();
                final String o_id = jumpJson.getO_id();

                if (RegexValidateUtil.isEmpty(o_class)) return;
                switch (o_class) {
                    case VarConstant.OCLASS_ACTIVITY:
                        jumpActivity(context, o_action, o_id);
                        return;
                    case VarConstant.OCLASS_BLOG:
                        jumpBlog(context, o_action, o_id);
                        return;
                    case VarConstant.OCLASS_TOPIC:
                        jumpTopic(context, o_action, o_id);
                        return;
                    case VarConstant.OCLASS_DATA:
                        jumpData(context, o_action, o_id);
                        return;
                    case VarConstant.OCLASS_VIDEO:
                        jumpVideo(context, o_action, o_id);
                        return;
                    case VarConstant.OCLASS_FLASH:
                        jumpFlash(context, o_action, o_id);
                        return;
                    case VarConstant.OCLASS_NEWS:
                        jumpNews(context, o_action, o_id);
                        return;
                    case VarConstant.OCLASS_QUOTES:
                        jumpQuotes(context, o_action, o_id);
                        break;
                    case VarConstant.OCLASS_VIEWPOINT:
                        jumpViewPoint(context, o_action, o_id);
                        break;
                    case VarConstant.OCLASS_MEMBER:
                        jumpMember(context, o_action, o_id);
                        break;
                    case VarConstant.OCLASS_TRADE:
                        jumpTrade(context, o_action, o_id);
                        break;
                }
            } else {
                //网页跳转
                Intent intent = new Intent(context, WebActivity.class);

                if (TextUtils.isEmpty(webTitle)) {
                    intent.putExtra(IntentConstant.NAME, webTitle);
                }

                intent.putExtra(IntentConstant.WEBURL, url);
                intent.putExtra(IntentConstant.JAVASCRIPTENABLED, false);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void jumpTrade(BaseActivity context, String o_action, String o_id) {
        MainActivity mainActivity;
        switch (o_action) {
            case VarConstant.OACTION_MAIN:
            case VarConstant.OACTION_LIST:
//                boolean logined = LoginUtils.isLogined(context);
//                if (!logined) return;
//                UserJson userInfo = LoginUtils.getUserInfo(context);
//                if (!RegexValidateUtil.isEmpty(userInfo.getWriter_id()) && !RegexValidateUtil.isEmpty(userInfo.getWriter_name())) {
//                    //发布观点
//                    Intent intent = new Intent(context, PublishActivity.class);
//                    context.startActivity(intent);
//                } else {
//                    //入驻专栏
//                    Intent intent = new Intent(context, WebActivity.class);
//                    intent.putExtra(IntentConstant.NAME, "专栏入驻");
//                    intent.putExtra(IntentConstant.WEBURL, HttpConstant.ZLRZ_URL + "?uid=" + userInfo.getUid());
//                    context.startActivity(intent);
//                }

                if (context instanceof MainActivity) {
                    mainActivity = (MainActivity) context;
                } else {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(intent);
                    ActivityManager.getInstance().moveToStackPeekActivity(MainActivity.class);
                    mainActivity = (MainActivity) ActivityManager.getInstance().getSingleActivity
                            (MainActivity.class);
                }
                if (mainActivity != null && mainActivity.drawer.isDrawerOpen(GravityCompat.START)) {
                    mainActivity.drawer.closeDrawer(GravityCompat.START);
                }
                RadioButton rbMarket = mainActivity.rbProbe;
                boolean rbMarketChecked = rbMarket.isChecked();
                if (rbMarketChecked) {
                } else {
                    rbMarket.performClick();
                }
                break;
        }
    }

    private static void jumpMember(BaseActivity context, String o_action, String o_id) {
        switch (o_action) {
            case VarConstant.OACTION_RECOMMEND:
                Intent intent = new Intent(context, ShareActivity.class);
                context.startActivity(intent);
                break;

        }
    }

    private static void jumpViewPoint(BaseActivity context, String o_action, String o_id) {

        switch (o_action) {
            case VarConstant.OACTION_VIEWPOINT_DETAIL:
                Intent viewPointIntent = new Intent(context, ViewPointDetailActivity.class);
                viewPointIntent.putExtra(IntentConstant.O_ID, o_id);
                context.startActivity(viewPointIntent);
                break;
        }
    }

    /**
     * 行情
     *
     * @param context
     * @param o_action
     * @param o_id
     */
    private static void jumpQuotes(BaseActivity context, String o_action, final String o_id) throws Exception {
        if (context instanceof MainActivity) {
            switch (o_action) {
                case VarConstant.OACTION_DETAIL:
                    //行情详情
                    Intent marketIntent = new Intent(context, MarketDetailActivity.class);
                    MarketItemBean marketBean = new MarketItemBean();
                    marketBean.setCode(o_id);
                    marketIntent.putExtra(IntentConstant.MARKET, marketBean);
                    context.startActivity(marketIntent);
                    break;
                case VarConstant.OACTION_INDEX:
                    //行情首页
                    final MainActivity mainActivity = (MainActivity) context;
                    RadioButton rbMarket = mainActivity.rbMarket;
                    boolean rbMarketChecked = rbMarket.isChecked();
                    if (rbMarketChecked) {
                        mainActivity.marketFragment.onTabSelect(0);
                    } else {
                        rbMarket.performClick();
                        rbMarket.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mainActivity.marketFragment.onTabSelect(0);
                            }
                        }, 200);
                    }
                    break;
                case VarConstant.OACTION_LIST:
                    //行情列表
                    final MainActivity mainActivityList = (MainActivity) context;
                    RadioButton rbMarketList = mainActivityList.rbMarket;
                    boolean rbMarketCheckedList = rbMarketList.isChecked();
                    final MarketVPFragment marketVPFragment = (MarketVPFragment) mainActivityList.marketFragment
                            .marketVPFragment;
                    if (rbMarketCheckedList) {
                        mainActivityList.marketFragment.onTabSelect(0);
                        String[] tabs = marketVPFragment.getTabs();
                        if (tabs == null || tabs.length == 0) {
                            marketVPFragment.setSelTab(o_id);
                        } else {
                            int length = tabs.length;
                            for (int i = 0; i < length; i++) {
                                if (tabs[i].equals(o_id)) {
                                    marketVPFragment.stlNavigationBar.setCurrentTab(i);
                                }
                            }
                        }
                    } else {
                        rbMarketList.performClick();
                        rbMarketList.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                String[] tabs = marketVPFragment.getTabs();
                                if (tabs == null || tabs.length == 0) {
                                    marketVPFragment.setSelTab(o_id);
                                } else {
                                    int length = tabs.length;
                                    for (int i = 0; i < length; i++) {
                                        if (tabs[i].equals(o_id)) {
                                            marketVPFragment.stlNavigationBar.setCurrentTab(i);
                                        }
                                    }
                                }
                            }
                        }, 200);
                    }
                    break;
                case VarConstant.OACTION_ZX:
                    //行情自选
                    final MainActivity mainActivityZx = (MainActivity) context;
                    RadioButton rbMarketZx = mainActivityZx.rbMarket;
                    boolean rbMarketCheckedZx = rbMarketZx.isChecked();
                    if (rbMarketCheckedZx) {
                        mainActivityZx.marketFragment.onTabSelect(1);
                    } else {
                        rbMarketZx.performClick();
                        rbMarketZx.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mainActivityZx.marketFragment.onTabSelect(1);
                            }
                        }, 200);
                    }
                    break;
                default:
                    break;
            }
        } else {
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            switch (o_action) {
                case VarConstant.OACTION_DETAIL:
                    //行情详情
                    Intent marketIntent = new Intent(context, MarketDetailActivity.class);
                    MarketItemBean marketBean = new MarketItemBean();
                    marketBean.setCode(o_id);
                    marketIntent.putExtra(IntentConstant.MARKET, marketBean);
                    context.startActivity(marketIntent);
                    break;
                case VarConstant.OACTION_INDEX:
                    context.startActivity(intent);
                    ActivityManager.getInstance().moveToStackPeekActivity(MainActivity.class);
                    final MainActivity mainActivity = (MainActivity) ActivityManager.getInstance().getSingleActivity
                            (MainActivity.class);
                    if (mainActivity != null && mainActivity.drawer.isDrawerOpen(GravityCompat.START)) {
                        mainActivity.drawer.closeDrawer(GravityCompat.START);
                    }
                    RadioButton rbMarket = mainActivity.rbMarket;
                    boolean rbMarketChecked = rbMarket.isChecked();
                    if (rbMarketChecked) {
                        mainActivity.marketFragment.onTabSelect(0);
                    } else {
                        rbMarket.performClick();
                        rbMarket.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mainActivity.marketFragment.onTabSelect(0);
                            }
                        }, 200);
                    }
                    break;
                case VarConstant.OACTION_LIST:
                    context.startActivity(intent);
                    ActivityManager.getInstance().moveToStackPeekActivity(MainActivity.class);
                    final MainActivity mainActivityList = (MainActivity) ActivityManager.getInstance()
                            .getSingleActivity(MainActivity
                                    .class);
                    if (mainActivityList != null && mainActivityList.drawer.isDrawerOpen(GravityCompat.START)) {
                        mainActivityList.drawer.closeDrawer(GravityCompat.START);
                    }
                    RadioButton rbMarketList = mainActivityList.rbMarket;
                    boolean rbMarketCheckedList = rbMarketList.isChecked();
                    final MarketVPFragment marketVPFragment = (MarketVPFragment) mainActivityList.marketFragment
                            .marketVPFragment;
                    if (rbMarketCheckedList) {
                        mainActivityList.marketFragment.onTabSelect(0);
                        String[] tabs = marketVPFragment.getTabs();
                        if (tabs == null || tabs.length == 0) {
                            marketVPFragment.setSelTab(o_id);
                        } else {
                            int length = tabs.length;
                            for (int i = 0; i < length; i++) {
                                if (tabs[i].equals(o_id)) {
                                    marketVPFragment.stlNavigationBar.setCurrentTab(i);
                                }
                            }
                        }
                    } else {
                        rbMarketList.performClick();
                        rbMarketList.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                String[] tabs = marketVPFragment.getTabs();
                                if (tabs == null || tabs.length == 0) {
                                    marketVPFragment.setSelTab(o_id);
                                } else {
                                    int length = tabs.length;
                                    for (int i = 0; i < length; i++) {
                                        if (tabs[i].equals(o_id)) {
                                            marketVPFragment.stlNavigationBar.setCurrentTab(i);
                                        }
                                    }
                                }
                            }
                        }, 200);
                    }
                    break;
                case VarConstant.OACTION_ZX:
                    context.startActivity(intent);
                    ActivityManager.getInstance().moveToStackPeekActivity(MainActivity.class);
                    final MainActivity mainActivityZx = (MainActivity) ActivityManager.getInstance()
                            .getSingleActivity(MainActivity.class);
                    if (mainActivityZx != null && mainActivityZx.drawer.isDrawerOpen(GravityCompat.START)) {
                        mainActivityZx.drawer.closeDrawer(GravityCompat.START);
                    }
                    RadioButton rbMarketZx = mainActivityZx.rbMarket;
                    boolean rbMarketCheckedZx = rbMarketZx.isChecked();
                    if (rbMarketCheckedZx) {
                        mainActivityZx.marketFragment.onTabSelect(1);
                    } else {
                        rbMarketZx.performClick();
                        rbMarketZx.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mainActivityZx.marketFragment.onTabSelect(1);
                            }
                        }, 200);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 要闻
     *
     * @param context
     * @param o_action
     * @param o_id
     */
    private static void jumpNews(BaseActivity context, String o_action, final String o_id) throws Exception {
        switch (o_action) {
            case VarConstant.OACTION_DIANPING:
                Intent dpIntent = new Intent(context, DpActivity.class);
                context.startActivity(dpIntent);
                break;
            case VarConstant.OACTION_DETAIL:
                Intent detailIntent = new Intent(context, NewsContentActivity.class);
                detailIntent.putExtra(IntentConstant.O_ID, o_id);
                detailIntent.putExtra(IntentConstant.TYPE, VarConstant.OCLASS_NEWS);
                context.startActivity(detailIntent);
                break;
            case VarConstant.OACTION_INDEX:
                MainActivity mainActivity;
                if (context instanceof MainActivity) {
                    mainActivity = (MainActivity) context;
                } else {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(intent);
                    ActivityManager.getInstance().moveToStackPeekActivity(MainActivity.class);
                    mainActivity = (MainActivity) ActivityManager.getInstance().getSingleActivity(MainActivity
                            .class);
                    if (mainActivity != null && mainActivity.drawer.isDrawerOpen(GravityCompat.START)) {
                        mainActivity.drawer.closeDrawer(GravityCompat.START);
                    }
                }
                final MainActivity mainActivityCopy = mainActivity;
                RadioButton rbHome = mainActivity.rbHome;
                rbHome.performClick();
                boolean rbHomeChecked = rbHome.isChecked();
                if (rbHomeChecked) {
                    mainActivity.homeFragment.onTabSelect(0);
                } else {
                    rbHome.performClick();
                    rbHome.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mainActivityCopy.homeFragment.onTabSelect(0);
                        }
                    }, 200);
                }
                break;
            case VarConstant.OACTION_LIST:
                MainActivity mainActivityList;
                if (context instanceof MainActivity) {
                    mainActivityList = (MainActivity) context;
                } else {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(intent);
                    ActivityManager.getInstance().moveToStackPeekActivity(MainActivity.class);
                    mainActivityList = (MainActivity) ActivityManager.getInstance().getSingleActivity(MainActivity
                            .class);
                    if (mainActivityList != null && mainActivityList.drawer.isDrawerOpen(GravityCompat.START)) {
                        mainActivityList.drawer.closeDrawer(GravityCompat.START);
                    }
                }
                final MainActivity mainActivityListCopy = mainActivityList;
                RadioButton rbHomeList = mainActivityList.rbHome;
                rbHomeList.performClick();
                boolean rbHomeCheckedList = rbHomeList.isChecked();

                final NewsFragment newsFragment = mainActivityList.homeFragment.newsFragment;

                if (rbHomeCheckedList) {
                    mainActivityList.homeFragment.onTabSelect(0);
                    if (newsFragment != null) {
                        String[] tabs = newsFragment.getTabs();
                        if (tabs == null || tabs.length == 0) {
                            newsFragment.setSelTab(o_id);
                        } else {
                            int length = tabs.length;
                            for (int i = 0; i < length; i++) {
                                if (tabs[i].equals(o_id)) {
                                    newsFragment.stlNavigationBar.setCurrentTab(i);
                                }
                            }
                        }
                    }
                } else {
                    rbHomeList.performClick();
                    rbHomeList.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mainActivityListCopy.homeFragment.onTabSelect(0);
                            if (newsFragment != null) {
                                String[] tabs = newsFragment.getTabs();
                                if (tabs == null || tabs.length == 0) {
                                    newsFragment.setSelTab(o_id);
                                } else {
                                    int length = tabs.length;
                                    for (int i = 0; i < length; i++) {
                                        if (tabs[i].equals(o_id)) {
                                            newsFragment.stlNavigationBar.setCurrentTab(i);
                                        }
                                    }
                                }
                            }
                        }
                    }, 200);
                }
                break;
        }
    }

    /**
     * 快讯
     *
     * @param context
     * @param o_action
     * @param o_id
     */
    private static void jumpFlash(BaseActivity context, String o_action, String o_id) throws Exception {

        switch (o_action) {
            case VarConstant.OACTION_DETAIL:
                Intent detailIntent = new Intent(context, FlashActivity.class);
                detailIntent.putExtra(IntentConstant.O_ID, o_id);
                context.startActivity(detailIntent);
                break;
            case VarConstant.OACTION_INDEX:
            case VarConstant.OACTION_LIST:
            default:
                MainActivity mainActivity;
                if (context instanceof MainActivity) {
                    mainActivity = (MainActivity) context;
                } else {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(intent);
                    ActivityManager.getInstance().moveToStackPeekActivity(MainActivity.class);
                    mainActivity = (MainActivity) ActivityManager.getInstance().getSingleActivity(MainActivity.class);
                    if (mainActivity != null && mainActivity.drawer.isDrawerOpen(GravityCompat.START)) {
                        mainActivity.drawer.closeDrawer(GravityCompat.START);
                    }
                }
                final MainActivity mainActivityCopy = mainActivity;
                RadioButton rbHomeFlash = mainActivity.rbHome;
                boolean checkedFlash = rbHomeFlash.isChecked();
                if (checkedFlash) {
                    mainActivity.homeFragment.onTabSelect(1);
                } else {
                    rbHomeFlash.performClick();
                    rbHomeFlash.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mainActivityCopy.homeFragment.onTabSelect(1);
                        }
                    }, 200);
                }
                break;
        }
    }

    /**
     * 视听
     *
     * @param context
     * @param o_action
     * @param o_id
     */
    private static void jumpVideo(BaseActivity context, String o_action, final String o_id) throws Exception {
        switch (o_action) {
            case VarConstant.OACTION_LIST:
                MainActivity mainActivity;
                if (context instanceof MainActivity) {
                    mainActivity = (MainActivity) context;
                } else {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(intent);
                    ActivityManager.getInstance().moveToStackPeekActivity(MainActivity.class);
                    mainActivity = (MainActivity) ActivityManager.getInstance().getSingleActivity(MainActivity.class);
                    if (mainActivity != null && mainActivity.drawer.isDrawerOpen(GravityCompat.START)) {
                        mainActivity.drawer.closeDrawer(GravityCompat.START);
                    }
                }
                RadioButton rbAudio = mainActivity.rbAudioVisual;
                boolean videoChecked = rbAudio.isChecked();
                if (videoChecked) {
                    mainActivity.avFragment.onTabSelect(0);
                    mainActivity.avFragment.videoFragment.setJumpId(o_id);
                } else {
                    rbAudio.performClick();
                    final MainActivity mainActivityCopy = mainActivity;
                    rbAudio.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mainActivityCopy.avFragment.onTabSelect(0);
                            mainActivityCopy.avFragment.videoFragment.setJumpId(o_id);
                        }
                    }, 200);
                }
                break;
            case VarConstant.OACTION_DETAIL:
                Intent detailIntent = new Intent(context, VideoDetailActivity.class);
                detailIntent.putExtra(IntentConstant.O_ID, o_id);
                context.startActivity(detailIntent);
                break;
        }
    }

    /**
     * 数据
     *
     * @param context
     * @param o_action
     * @param o_id
     * @throws Exception
     */
    private static void jumpData(BaseActivity context, String o_action, String o_id) throws Exception {
        switch (o_action) {
            case VarConstant.OACTION_LIST:

                MainActivity mainActivity;
                if (context instanceof MainActivity) {
                    mainActivity = (MainActivity) context;
                } else {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(intent);
                    ActivityManager.getInstance().moveToStackPeekActivity(MainActivity.class);
                    mainActivity = (MainActivity) ActivityManager.getInstance().getSingleActivity(MainActivity.class);
                    if (mainActivity != null && mainActivity.drawer.isDrawerOpen(GravityCompat.START)) {
                        mainActivity.drawer.closeDrawer(GravityCompat.START);
                    }
                }

                RadioButton rbDatum = mainActivity.rbDatum;
                boolean rbDatumChecked = rbDatum.isChecked();
                if (rbDatumChecked) {
                    mainActivity.datumFragment.onTabSelect(1);
                } else {
                    rbDatum.performClick();
                    final MainActivity mainActivityCopy = mainActivity;
                    rbDatum.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mainActivityCopy.datumFragment.onTabSelect(1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 200);
                }
                break;
            case VarConstant.OACTION_RL:

                MainActivity mainActivityRl;
                if (context instanceof MainActivity) {
                    mainActivityRl = (MainActivity) context;
                } else {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(intent);
                    ActivityManager.getInstance().moveToStackPeekActivity(MainActivity.class);
                    mainActivityRl = (MainActivity) ActivityManager.getInstance().getSingleActivity(MainActivity.class);
                    if (mainActivityRl != null && mainActivityRl.drawer.isDrawerOpen(GravityCompat.START)) {
                        mainActivityRl.drawer.closeDrawer(GravityCompat.START);
                    }
                }

                RadioButton rlDatum = mainActivityRl.rbDatum;
                boolean rlDatumChecked = rlDatum.isChecked();
                if (rlDatumChecked) {
                    mainActivityRl.datumFragment.onTabSelect(0);

                    long timeInMillis = System.currentTimeMillis();
                    String ymdStr = DateUtils.transformTime(timeInMillis, DateUtils.TYPE_YMD);
                    long ymdLong = Long.parseLong(DateUtils.transfromTime(ymdStr, DateUtils.TYPE_YMD));

                    mainActivityRl.datumFragment.gotoCorrespondItem(ymdLong);
                } else {
                    rlDatum.performClick();
                    final MainActivity mainActivityCopy = mainActivityRl;
                    rlDatum.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mainActivityCopy.datumFragment.onTabSelect(0);

                                long timeInMillis = System.currentTimeMillis();
                                String ymdStr = DateUtils.transformTime(timeInMillis, DateUtils.TYPE_YMD);
                                long ymdLong = Long.parseLong(DateUtils.transfromTime(ymdStr, DateUtils.TYPE_YMD));

                                mainActivityCopy.datumFragment.gotoCorrespondItem(ymdLong);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 200);
                }
                break;
            case VarConstant.OACTION_DETAIL:
                Intent detailIntent = new Intent(context, DatumHistoryActivity.class);
                detailIntent.putExtra(IntentConstant.O_ID, o_id);
                context.startActivity(detailIntent);
                break;
        }

    }

    /**
     * 专题
     *
     * @param context
     * @param o_action
     * @param o_id
     */
    private static void jumpTopic(BaseActivity context, String o_action, String o_id) {
        switch (o_action) {
            case VarConstant.OACTION_INDEX:
            case VarConstant.OACTION_LIST:
                //专题列表
                Intent activityIntent = new Intent(context, MoreActivity.class);
                activityIntent.putExtra(IntentConstant.TYPE, VarConstant.EXPLORE_TOPIC);
                context.startActivity(activityIntent);
                break;
            case VarConstant.OACTION_DETAIL:
                //专题详情
                break;
        }
    }

    /**
     * 专栏
     *
     * @param context
     * @param o_action
     * @param o_id
     */
    private static void jumpBlog(BaseActivity context, String o_action, final String o_id) {
        switch (o_action) {
            case VarConstant.OACTION_INDEX:
                //专栏列表
//                Intent authorIntent = new Intent(context, AuthorListActivity.class);
//                context.startActivity(authorIntent);

                MainActivity mainActivity;
                if (context instanceof MainActivity) {
                    mainActivity = (MainActivity) context;
                } else {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(intent);
                    ActivityManager.getInstance().moveToStackPeekActivity(MainActivity.class);
                    mainActivity = (MainActivity) ActivityManager.getInstance().getSingleActivity(MainActivity.class);
                    if (mainActivity != null && mainActivity.drawer.isDrawerOpen(GravityCompat.START)) {
                        mainActivity.drawer.closeDrawer(GravityCompat.START);
                    }
                }

                RadioButton rbTrading = mainActivity.rbProbe;
                boolean rbDatumChecked = rbTrading.isChecked();
                if (rbDatumChecked) {
                    mainActivity.exploreFragment.onTabSelect(1);
                    ArticleFragment articleFragment = mainActivity.exploreFragment.articleFragment;
                    if (articleFragment != null&&articleFragment.stlNavigationBar!=null)
                        articleFragment.stlNavigationBar.setCurrentTab(0);
                } else {
                    rbTrading.performClick();
                    final MainActivity mainActivityCopy = mainActivity;
                    rbTrading.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mainActivityCopy.exploreFragment.onTabSelect(1);
                                ArticleFragment articleFragment = mainActivityCopy.exploreFragment.articleFragment;
                                if (articleFragment != null&&articleFragment.stlNavigationBar!=null)
                                    articleFragment.stlNavigationBar.setCurrentTab(0);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 200);
                }


                break;
            case VarConstant.OACTION_LIST:
//                AuthorListActivity authorActivity = (AuthorListActivity) ActivityManager.getInstance().getSingleActivity
//                        (AuthorListActivity.class);
//                if (authorActivity == null) {
//                    Intent authorIntent2 = new Intent(context, AuthorListActivity.class);
//
//                    authorIntent2.putExtra(IntentConstant.INDEX, 1);
//                    authorIntent2.putExtra(IntentConstant.TAB, o_id);
//
//                    context.startActivity(authorIntent2);
//                } else {
//                    ActivityManager.getInstance().moveToStackPeekActivity(AuthorListActivity.class);
//                    authorActivity = (AuthorListActivity) ActivityManager.getInstance().getSingleActivity
//                            (AuthorListActivity.class);
//                    try {
//                        Thread.sleep(200);
//                        authorActivity.onTabSelect(1);
//                        ArticleFragment articleFragment = authorActivity.articleFragment;
//                        if (articleFragment != null) {
//                            String[] tabs = articleFragment.getTabs();
//                            if (tabs == null || tabs.length == 0) {
//                                articleFragment.setSelTab(o_id);
//                            } else {
//                                int length = tabs.length;
//                                for (int i = 0; i < length; i++) {
//                                    if (tabs[i].equals(o_id)) {
//                                        articleFragment.onTabSelect(i);
//                                    }
//                                }
//                            }
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }

                MainActivity mainActivity2;
                if (context instanceof MainActivity) {
                    mainActivity2 = (MainActivity) context;
                } else {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(intent);
                    ActivityManager.getInstance().moveToStackPeekActivity(MainActivity.class);
                    mainActivity2 = (MainActivity) ActivityManager.getInstance().getSingleActivity(MainActivity.class);
                    if (mainActivity2 != null && mainActivity2.drawer.isDrawerOpen(GravityCompat.START)) {
                        mainActivity2.drawer.closeDrawer(GravityCompat.START);
                    }
                }

                RadioButton rbTrading2 = mainActivity2.rbProbe;
                boolean rbDatumChecked2 = rbTrading2.isChecked();
                final TradingFragment tradingFragment = mainActivity2.exploreFragment;
                if (rbDatumChecked2) {
                    tradingFragment.setTab(o_id);
                    tradingFragment.onTabSelect(1);
                } else {
                    rbTrading2.performClick();
                    final MainActivity mainActivityCopy = mainActivity2;
                    rbTrading2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final TradingFragment tradingFragment = mainActivityCopy.exploreFragment;
                                tradingFragment.setTab(o_id);
                                tradingFragment.onTabSelect(1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 500);
                }

                break;
            case VarConstant.OACTION_DETAIL:
                Intent detailIntent = new Intent(context, NewsContentActivity.class);
                detailIntent.putExtra(IntentConstant.O_ID, o_id);
                detailIntent.putExtra(IntentConstant.TYPE, VarConstant.OCLASS_BLOG);
                context.startActivity(detailIntent);
                break;
        }
    }

    /**
     * 活动
     *
     * @param context
     * @param o_action
     * @param o_id
     */
    private static void jumpActivity(BaseActivity context, String o_action, String o_id) {
        switch (o_action) {
            case VarConstant.OACTION_INDEX:
            case VarConstant.OACTION_LIST:
                //活动列表
                Intent activityIntent = new Intent(context, MoreActivity.class);
                activityIntent.putExtra(IntentConstant.TYPE, VarConstant.EXPLORE_ACTIVITY);
                context.startActivity(activityIntent);
                break;
            case VarConstant.OACTION_DETAIL:
                //活动详情
                break;
        }
    }

    public static void MwJump(Intent intent, BaseActivity activity) {
        if (intent.getData() != null) {
            if (intent.getData().getHost().equals("appapi.kxt.com")) {

                String idStr = intent.getData().getQueryParameter("Id");
                if (!TextUtils.isEmpty(idStr)) {
                    MainActivity.mwId = idStr;
                } else {
                    MainActivity.mwId = null;
                }
                String typeStr = intent.getData().getQueryParameter("Type");
                MainActivity.mwType = typeStr;

                String pathStr = intent.getData().getPath();
                if (!TextUtils.isEmpty(pathStr)) {
                    MainActivity.mwPath = pathStr;
                } else {
                    MainActivity.mwPath = null;
                }

                //跳转后结束当前activity
                MLink.getInstance(activity).router(activity, intent.getData());
                activity.finish();
            }
        } else {
            //如果需要应用宝跳转，则调用。否则不需要
            MLink.getInstance(activity).checkYYB();
        }

        //跳转router调用
        if (intent.getData() != null) {
            MLinkAPIFactory.createAPI(activity).router(activity, intent.getData());
            //跳转后结束当前activity
            activity.finish();
        } else {
            //如果需要应用宝跳转，则调用。否则不需要
            MLinkAPIFactory.createAPI(activity).checkYYB();
        }

    }

//    public static void jumpDetails(Activity content, String o_class, String o_id, String href) {
//        Intent intent = null;
//        if (!RegexValidateUtil.isEmpty(href)) {
//            intent = new Intent(content, WebActivity.class);
//            intent.putExtra(IntentConstant.WEBURL, href);
//        } else {
//            switch (o_class) {
//                case VarConstant.OCLASS_VIDEO:
//                    //视听详情
//                    intent = new Intent(content, VideoDetailActivity.class);
//                    intent.putExtra(IntentConstant.O_ID, o_id);
//                    break;
//                case VarConstant.OCLASS_DATA:
//                    //数据详情
//                    intent = new Intent(content, DatumHistoryActivity.class);
//                    intent.putExtra(IntentConstant.O_ID, o_id);
//                    break;
//                case VarConstant.OCLASS_BLOG:
//                case VarConstant.OCLASS_ARTICLE:
//                case VarConstant.OCLASS_NEWS:
//                    //要闻详情
//                    intent = new Intent(content, NewsContentActivity.class);
//                    intent.putExtra(IntentConstant.O_ID, o_id);
//                    break;
//                case VarConstant.OCLASS_QUOTES:
//                    //行情详情
//                    intent = new Intent(content, MarketDetailActivity.class);
//                    intent.putExtra(IntentConstant.O_ID, o_id);
//                    break;
//                case VarConstant.OCLASS_FLASH:
//                    //快讯详情
//                    intent = new Intent(content, FlashActivity.class);
//                    intent.putExtra(IntentConstant.O_ID, o_id);
//                    break;
//            }
//        }
//        content.startActivity(intent);
//    }
}
