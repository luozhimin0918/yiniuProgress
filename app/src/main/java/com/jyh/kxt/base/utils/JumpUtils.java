package com.jyh.kxt.base.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.tool.util.L;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.RadioButton;

import com.jyh.kxt.av.ui.VideoDetailActivity;
import com.jyh.kxt.av.ui.fragment.VideoFragment;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.datum.ui.DatumHistoryActivity;
import com.jyh.kxt.explore.ui.AuthorListActivity;
import com.jyh.kxt.explore.ui.MoreActivity;
import com.jyh.kxt.index.ui.BrowerHistoryActivity;
import com.jyh.kxt.index.ui.MainActivity;
import com.jyh.kxt.index.ui.SearchActivity;
import com.jyh.kxt.index.ui.WebActivity;
import com.jyh.kxt.index.ui.fragment.AvFragment;
import com.jyh.kxt.main.ui.activity.DpActivity;
import com.jyh.kxt.main.ui.activity.FlashActivity;
import com.jyh.kxt.main.ui.activity.NewsContentActivity;
import com.jyh.kxt.market.bean.MarketNavBean;
import com.jyh.kxt.market.ui.MarketDetailActivity;
import com.jyh.kxt.market.ui.fragment.MarketVPFragment;
import com.library.base.http.VarConstant;
import com.jyh.kxt.base.json.JumpJson;
import com.library.manager.ActivityManager;
import com.library.util.DateUtils;
import com.library.util.RegexValidateUtil;
import com.library.util.SystemUtil;

import java.util.List;

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
                }
            } else {
                //网页跳转
                Intent intent = new Intent(context, WebActivity.class);
                intent.putExtra(IntentConstant.WEBURL, url);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 行情
     *
     * @param context
     * @param o_action
     * @param o_id
     */
    private static void jumpQuotes(BaseActivity context, String o_action, String o_id) {
        if (context instanceof MainActivity) {
            switch (o_action) {
                case VarConstant.OACTION_DETAIL:
                    break;
                case VarConstant.OACTION_INDEX:
                case VarConstant.OACTION_LIST:
                    break;
                case VarConstant.OACTION_ZX:
                    final MainActivity mainActivity = (MainActivity) context;
                    RadioButton rbMarket = mainActivity.rbMarket;
                    boolean rbMarketChecked = rbMarket.isChecked();
                    if (rbMarketChecked) {
                        mainActivity.marketFragment.onTabSelect(1);
                    } else {
                        rbMarket.performClick();
                        rbMarket.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mainActivity.marketFragment.onTabSelect(1);
                            }
                        }, 200);
                    }
                    break;
                default:
                    break;
            }
        } else {
            switch (o_action) {
                case VarConstant.OACTION_DETAIL:
                    break;
                case VarConstant.OACTION_INDEX:
                case VarConstant.OACTION_LIST:
                    break;
                case VarConstant.OACTION_ZX:
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
    private static void jumpNews(BaseActivity context, String o_action, String o_id) {
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
        }
    }

    /**
     * 快讯
     *
     * @param context
     * @param o_action
     * @param o_id
     */
    private static void jumpFlash(BaseActivity context, String o_action, String o_id) {

        if (context instanceof MainActivity) {

            switch (o_action) {
                case VarConstant.OACTION_DETAIL:
                    Intent detailIntent = new Intent(context, FlashActivity.class);
                    detailIntent.putExtra(IntentConstant.O_ID, o_id);
                    context.startActivity(detailIntent);
                    break;
                case VarConstant.OACTION_INDEX:
                case VarConstant.OACTION_LIST:
                default:
                    final MainActivity mainActivity = (MainActivity) context;
                    RadioButton rbHomeFlash = mainActivity.rbHome;
                    boolean checkedFlash = rbHomeFlash.isChecked();
                    if (checkedFlash) {
                        ViewPager vpContent = mainActivity.homeFragment.vpContent;
                        vpContent.setCurrentItem(1);
                    } else {
                        rbHomeFlash.performClick();
                        rbHomeFlash.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ViewPager vpContent = mainActivity.homeFragment.vpContent;
                                vpContent.setCurrentItem(1);
                            }
                        }, 200);
                    }
                    break;
            }
        } else {
            switch (o_action) {
                case VarConstant.OACTION_DETAIL:
                    Intent detailIntent = new Intent(context, FlashActivity.class);
                    detailIntent.putExtra(IntentConstant.O_ID, o_id);
                    context.startActivity(detailIntent);
                    break;
            }
        }
    }

    /**
     * 视听
     *
     * @param context
     * @param o_action
     * @param o_id
     */
    private static void jumpVideo(BaseActivity context, String o_action, final String o_id) {
        if (context instanceof MainActivity) {
            final MainActivity mainActivity = (MainActivity) context;
            switch (o_action) {
                case VarConstant.OACTION_LIST:
                    RadioButton rbAudio = mainActivity.rbAudioVisual;
                    boolean videoChecked = rbAudio.isChecked();
                    if (videoChecked) {
                        ViewPager vpAudioVisual = mainActivity.avFragment.vpAudioVisual;
                        vpAudioVisual.setCurrentItem(0);
                        VideoFragment videoFragment = (VideoFragment) ((FragmentPagerAdapter) vpAudioVisual
                                .getAdapter()).getItem(0);
                        videoFragment.setJumpId(o_id);
                    } else {
                        rbAudio.performClick();
                        rbAudio.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ViewPager vpAudioVisual = mainActivity.avFragment.vpAudioVisual;
                                vpAudioVisual.setCurrentItem(0);
                                VideoFragment videoFragment = (VideoFragment) ((BaseFragmentAdapter) vpAudioVisual
                                        .getAdapter()).getItem(0);
                                videoFragment.setJumpId(o_id);
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
        } else {
            switch (o_action) {
                case VarConstant.OACTION_DETAIL:
                    Intent detailIntent = new Intent(context, VideoDetailActivity.class);
                    detailIntent.putExtra(IntentConstant.O_ID, o_id);
                    context.startActivity(detailIntent);
                    break;
            }
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
        if (context instanceof MainActivity) {
            final MainActivity mainActivity = (MainActivity) context;
            switch (o_action) {
                case VarConstant.OACTION_LIST:
                    RadioButton rbDatum = mainActivity.rbDatum;
                    boolean rbDatumChecked = rbDatum.isChecked();
                    if (rbDatumChecked) {
                        mainActivity.datumFragment.onTabSelect(1);
                    } else {
                        rbDatum.performClick();
                        rbDatum.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mainActivity.datumFragment.onTabSelect(1);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 200);
                    }
                    break;
                case VarConstant.OACTION_RL:
                    RadioButton rlDatum = mainActivity.rbDatum;
                    boolean rlDatumChecked = rlDatum.isChecked();
                    if (rlDatumChecked) {
                        mainActivity.datumFragment.onTabSelect(0);

                        long timeInMillis = System.currentTimeMillis();
                        String ymdStr = DateUtils.transformTime(timeInMillis, DateUtils.TYPE_YMD);
                        long ymdLong = Long.parseLong(DateUtils.transfromTime(ymdStr, DateUtils.TYPE_YMD));

                        mainActivity.datumFragment.gotoCorrespondItem(ymdLong);
                    } else {
                        rlDatum.performClick();
                        rlDatum.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mainActivity.datumFragment.onTabSelect(0);

                                    long timeInMillis = System.currentTimeMillis();
                                    String ymdStr = DateUtils.transformTime(timeInMillis, DateUtils.TYPE_YMD);
                                    long ymdLong = Long.parseLong(DateUtils.transfromTime(ymdStr, DateUtils.TYPE_YMD));

                                    mainActivity.datumFragment.gotoCorrespondItem(ymdLong);
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
        } else {
            switch (o_action) {
                case VarConstant.OACTION_DETAIL:
                    Intent detailIntent = new Intent(context, DatumHistoryActivity.class);
                    detailIntent.putExtra(IntentConstant.O_ID, o_id);
                    context.startActivity(detailIntent);
                    break;
            }
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
    private static void jumpBlog(BaseActivity context, String o_action, String o_id) {
        switch (o_action) {
            case VarConstant.OACTION_INDEX:
            case VarConstant.OACTION_LIST:
                //专栏列表
                Intent authorIntent = new Intent(context, AuthorListActivity.class);
                context.startActivity(authorIntent);
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
