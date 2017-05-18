package com.jyh.kxt.base.utils;

import android.content.Intent;
import android.databinding.tool.util.L;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.widget.RadioButton;

import com.jyh.kxt.av.ui.VideoDetailActivity;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.datum.ui.DatumHistoryActivity;
import com.jyh.kxt.index.ui.MainActivity;
import com.jyh.kxt.index.ui.SearchActivity;
import com.jyh.kxt.index.ui.WebActivity;
import com.jyh.kxt.index.ui.fragment.AvFragment;
import com.jyh.kxt.main.ui.activity.FlashActivity;
import com.jyh.kxt.main.ui.activity.NewsContentActivity;
import com.jyh.kxt.market.bean.MarketNavBean;
import com.jyh.kxt.market.ui.MarketDetailActivity;
import com.jyh.kxt.market.ui.fragment.MarketVPFragment;
import com.library.base.http.VarConstant;
import com.jyh.kxt.base.json.JumpJson;
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
//                if (jumpJson == null) return;
//                String o_class = jumpJson.o_class;
//                if (o_class == null) return;
//                if (context instanceof MainActivity) {
//                    final MainActivity mainActivity = (MainActivity) context;
//                    switch (o_class) {
//                        case VarConstant.OCLASS_DATA:
//                            //数据
//                            final String data_action = jumpJson.getO_action();
//                            final String data_id = jumpJson.getO_id();
//                            switch (data_action) {
//                                case VarConstant.OACTION_ETF:
//                                    Intent etfIntent = new Intent(mainActivity, DatumHistoryActivity.class);
//                                    etfIntent.putExtra(IntentConstant.O_ID, data_id);
//                                    mainActivity.startActivity(etfIntent);
//                                    break;
//                                case VarConstant.OACTION_DETAIL:
//                                    break;
//                                case VarConstant.OACTION_CFTC:
//                                    break;
//                            }
//                            /*
//                            mainActivity.rbDatum.performClick();
//                            mainActivity.rbDatum.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mainActivity.datumFragment.onTabSelect(1);
//
//                                }
//                            }, 500);*/
//
//                            break;
//                        case VarConstant.OCLASS_ARTICLE:
//                            //文章
//                            String article_action = jumpJson.getO_action();
//                            switch (article_action) {
//                                case VarConstant.OACTION_DETAIL:
//                                    //详情
//                                    Intent newsIntent = new Intent(mainActivity, NewsContentActivity.class);
//                                    newsIntent.putExtra(IntentConstant.O_ID, jumpJson.getO_id());
//                                    mainActivity.startActivity(newsIntent);
//                                    break;
//                            }
//                            break;
//                        case VarConstant.OCLASS_DATING:
//                            break;
//                        case VarConstant.OCLASS_DIANPING:
//
//                            break;
//                        case VarConstant.OCLASS_FLASH:
//                            //快讯
//                            mainActivity.rbHome.performClick();
//                            mainActivity.homeFragment.onTabSelect(1);
//                            break;
//                        case VarConstant.OCLASS_INDEX:
//                            //首页
//                            mainActivity.rbHome.performClick();
//                            break;
//                        case VarConstant.OCLASS_NEWS:
//                            //要闻
//                            mainActivity.rbHome.performClick();
//                            break;
//                        case VarConstant.OCLASS_QUOTES:
//                            //行情
//                            mainActivity.rbMarket.performClick();
//                            break;
//                        case VarConstant.OCLASS_RL:
//                            //日历
//                            mainActivity.rbDatum.performClick();
//                            break;
//                        case VarConstant.OCLASS_SCHOOL:
//                            break;
//                        case VarConstant.OCLASS_SEARCH:
//                            //搜索
//                            Intent searchIntent = new Intent(mainActivity, SearchActivity.class);
//                            //搜索关键字
//                            searchIntent.putExtra(IntentConstant.SEARCH_KEY, jumpJson.o_id);
//                            mainActivity.startActivity(searchIntent);
//                            break;
//                        case VarConstant.OCLASS_VIDEO:
//                            //视听
//                            mainActivity.rbAudioVisual.performClick();
//                            break;
//                    }
//                } else {
//                    switch (o_class) {
//                        case VarConstant.OCLASS_DATA:
//                            break;
//                        case VarConstant.OCLASS_ARTICLE:
//                            break;
//                        case VarConstant.OCLASS_DATING:
//                            break;
//                        case VarConstant.OCLASS_DIANPING:
//                            break;
//                        case VarConstant.OCLASS_FLASH:
//                            break;
//                        case VarConstant.OCLASS_INDEX:
//                            break;
//                        case VarConstant.OCLASS_NEWS:
//                            break;
//                        case VarConstant.OCLASS_QUOTES:
//                            break;
//                        case VarConstant.OCLASS_RL:
//                            break;
//                        case VarConstant.OCLASS_SCHOOL:
//                            break;
//                        case VarConstant.OCLASS_SEARCH:
//                            //搜索
//                            Intent searchIntent = new Intent(context, SearchActivity.class);
//                            //搜索关键字
//                            searchIntent.putExtra(IntentConstant.SEARCH_KEY, jumpJson.o_id);
//                            context.startActivity(searchIntent);
//                            break;
//                        case VarConstant.OCLASS_VIDEO:
//                            break;
//                    }
//                }
                String o_class = jumpJson.getO_class();
                String o_action = jumpJson.getO_action();
                String o_id = jumpJson.getO_id();

                if (context instanceof MainActivity) {
                    final MainActivity mainActivity = (MainActivity) context;
                    switch (o_action) {
                        case VarConstant.OACTION_DETAIL:
                            //详情跳转
                            jumpDetails(o_class, o_id, mainActivity);
                            break;
                        case VarConstant.OACTION_ARTICLE:
                            break;
                        case VarConstant.OACTION_INDEX:
                            //首页
                            switch (o_class) {
                                case VarConstant.OCLASS_INDEX:
                                    //app首页
                                    RadioButton rbHome = mainActivity.rbHome;
                                    boolean checked = rbHome.isChecked();
                                    if (checked) {
                                        ViewPager vpContent = mainActivity.homeFragment.vpContent;
                                        if (vpContent.getCurrentItem() != 0) {
                                            vpContent.setCurrentItem(0);
                                        }
                                    } else {
                                        rbHome.performClick();
                                        rbHome.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                ViewPager vpContent = mainActivity.homeFragment.vpContent;
                                                if (vpContent.getCurrentItem() != 0) {
                                                    vpContent.setCurrentItem(0);
                                                }
                                            }
                                        }, 200);
                                    }
                                    break;
                                case VarConstant.OCLASS_VIDEO:
                                    //视听首页
                                    RadioButton rbAudio = mainActivity.rbAudioVisual;
                                    boolean videoChecked = rbAudio.isChecked();
                                    if (videoChecked) {
                                        ViewPager vpAudioVisual = mainActivity.avFragment.vpAudioVisual;
                                        if (vpAudioVisual.getCurrentItem() != 0) {
                                            vpAudioVisual.setCurrentItem(0);
                                        }
                                    } else {
                                        rbAudio.performClick();
                                        rbAudio.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                ViewPager vpAudioVisual = mainActivity.avFragment.vpAudioVisual;
                                                if (vpAudioVisual.getCurrentItem() != 0) {
                                                    vpAudioVisual.setCurrentItem(0);
                                                }
                                            }
                                        }, 200);
                                    }
                                    break;
                                case VarConstant.OCLASS_FLASH:
                                    //快讯首页
                                    RadioButton rbHomeFlash = mainActivity.rbHome;
                                    boolean checkedFlash = rbHomeFlash.isChecked();
                                    if (checkedFlash) {
                                        ViewPager vpContent = mainActivity.homeFragment.vpContent;
                                        if (vpContent.getCurrentItem() != 1) {
                                            vpContent.setCurrentItem(1);
                                        }
                                    } else {
                                        rbHomeFlash.performClick();
                                        rbHomeFlash.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                ViewPager vpContent = mainActivity.homeFragment.vpContent;
                                                if (vpContent.getCurrentItem() != 1) {
                                                    vpContent.setCurrentItem(1);
                                                }
                                            }
                                        }, 200);
                                    }
                                    break;
                                case VarConstant.OCLASS_QUOTES:
                                    //行情首页
                                    RadioButton marketBtn = mainActivity.rbMarket;
                                    boolean marketBtnChecked = marketBtn.isChecked();
                                    if (marketBtnChecked) {
                                        int tabSelectPosition = mainActivity.marketFragment.getTabSelectPosition();
                                        if (tabSelectPosition != 0) {
                                            mainActivity.marketFragment.onTabSelect(0);
                                        }
                                    } else {
                                        marketBtn.performClick();
                                        marketBtn.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                int tabSelectPosition = mainActivity.marketFragment.getTabSelectPosition();
                                                if (tabSelectPosition != 0) {
                                                    mainActivity.marketFragment.onTabSelect(0);
                                                }
                                            }
                                        }, 200);
                                    }
                                    break;
                                case VarConstant.OCLASS_RL:
                                    //日历首页
                                    RadioButton rbDatum = mainActivity.rbDatum;
                                    boolean rbDatumChecked = rbDatum.isChecked();
                                    if (rbDatumChecked) {
                                        mainActivity.datumFragment.onTabSelect(0);

                                        long timeInMillis = System.currentTimeMillis();
                                        String ymdStr = DateUtils.transformTime(timeInMillis, DateUtils.TYPE_YMD);
                                        long ymdLong = Long.parseLong(DateUtils.transfromTime(ymdStr, DateUtils.TYPE_YMD));

                                        mainActivity.datumFragment.gotoCorrespondItem(ymdLong);
                                    } else {
                                        rbDatum.performClick();
                                        rbDatum.postDelayed(new Runnable() {
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
                                case VarConstant.OCLASS_DATING:
                                    //大厅首页
                                    break;
                                case VarConstant.OCLASS_DIANPING:
                                    //点评首页
                                    break;
                            }
                            break;
                        case VarConstant.OACTION_LIST:
                            switch (o_class) {
                                case VarConstant.OCLASS_DIANPING:
                                    break;
                                case VarConstant.OCLASS_SCHOOL:
                                    break;
                                case VarConstant.OCLASS_NEWS:
                                    break;
                                case VarConstant.OCLASS_VIDEO:

                                    break;
                                case VarConstant.OCLASS_QUOTES:

                                    break;
                            }
                            break;
                        case VarConstant.OACTION_ETF:
                            break;
                        case VarConstant.OACTION_CFTC:
                            break;
                    }
                } else {

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
     * 跳转详情页
     *
     * @param o_class
     * @param o_id
     * @param content
     */
    private static void jumpDetails(String o_class, String o_id, BaseActivity content) {
        Intent intent = null;
        switch (o_class) {
            case VarConstant.OCLASS_VIDEO:
                //视听详情
                intent = new Intent(content, VideoDetailActivity.class);
                intent.putExtra(IntentConstant.O_ID, o_id);
                break;
            case VarConstant.OCLASS_DATA:
                //数据详情
                intent = new Intent(content, DatumHistoryActivity.class);
                intent.putExtra(IntentConstant.O_ID, o_id);
                break;
            case VarConstant.OCLASS_ARTICLE:
            case VarConstant.OCLASS_NEWS:
                //要闻详情
                intent = new Intent(content, NewsContentActivity.class);
                intent.putExtra(IntentConstant.O_ID, o_id);
                break;
            case VarConstant.OCLASS_QUOTES:
                //行情详情
                intent = new Intent(content, MarketDetailActivity.class);
                intent.putExtra(IntentConstant.O_ID, o_id);
                break;
            case VarConstant.OCLASS_FLASH:
                //快讯详情
                intent = new Intent(content, FlashActivity.class);
                intent.putExtra(IntentConstant.O_ID, o_id);
                break;
        }
        content.startActivity(intent);
    }
}
