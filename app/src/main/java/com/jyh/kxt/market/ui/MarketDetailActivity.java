package com.jyh.kxt.market.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.impl.OnSocketTextMessage;
import com.jyh.kxt.base.json.ShareJson;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.utils.MarketConnectUtil;
import com.jyh.kxt.base.utils.MarketUtil;
import com.jyh.kxt.base.utils.UmengShareTool;
import com.jyh.kxt.base.widget.night.ThemeUtil;
import com.jyh.kxt.market.bean.MarketDetailBean;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.jyh.kxt.market.bean.MarketSocketBean;
import com.jyh.kxt.market.kline.bean.MarketTrendBean;
import com.jyh.kxt.market.presenter.BaseChartPresenter;
import com.jyh.kxt.market.presenter.KLinePresenter;
import com.jyh.kxt.market.presenter.MarketDetailPresenter;
import com.jyh.kxt.market.presenter.MinutePresenter;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.bean.EventBusClass;
import com.library.util.LogUtil;
import com.library.util.SystemUtil;
import com.library.widget.PageLoadLayout;
import com.library.widget.window.ToastView;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class MarketDetailActivity extends BaseActivity implements OnSocketTextMessage {

    @BindView(R.id.ll_market_detail_optional) LinearLayout llMarketDetailOptional;
    @BindView(R.id.iv_market_detail_optional_img) ImageView ivOptionalImage;
    @BindView(R.id.tv_market_detail_optional) TextView tvOptional;
    @BindView(R.id.ll_market_detail_share) LinearLayout llMarketDetailShare;
    @BindView(R.id.pll_content) PageLoadLayout pageLoadLayout;


    @BindView(R.id.market_chart_frame) public FrameLayout chartContainerLayout;

    /**
     * Head Layout 相关控件
     */
    @BindView(R.id.market_head_layout) public ViewGroup marketHeadLayout;
    @BindView(R.id.iv_bar_break) public ImageView ivBarBreak;
    @BindView(R.id.iv_bar_function) public ImageView ivUpdateView;
    @BindView(R.id.tv_bar_code) public TextView tvBarCode;
    @BindView(R.id.tv_bar_title) public TextView tvBarTitle;

    @BindView(R.id.market_head_parent) public FrameLayout marketHeadParentLayout;
    @BindView(R.id.market_chart_low) public TextView marketChartLow;
    @BindView(R.id.market_chart_zde) public TextView marketChartZde;
    @BindView(R.id.market_chart_zdf) public TextView marketChartZdf;
    @BindView(R.id.market_chart_zuoshou) public TextView marketChartZuoshou;
    @BindView(R.id.market_chart_jinkai) public TextView marketChartJinkai;
    @BindView(R.id.market_chart_zuigao) public TextView marketChartZuigao;
    @BindView(R.id.market_chart_zuidi) public TextView marketChartZuidi;
    @BindView(R.id.market_chart_update_time) public TextView marketChartLastTime;

    @BindView(R.id.market_chart_fenshi) public RelativeLayout rlFenShiView;


    @BindView(R.id.ll_nav) public LinearLayout marketFunctionNav;

    @BindView(R.id.market_chart_load) PageLoadLayout marketChartLoad;

    /**
     * 分时图的Chart
     */

    private MarketDetailPresenter marketDetailPresenter;
    //点击上面的分时  日时等item
    public int clickNavigationPosition = 0;
    private RelativeLayout clickOldNavigationView;

    //存储请求过来的数据
    private HashMap<Integer, BaseChartPresenter> basePresenterMap = new HashMap<>();


    private MarketItemBean marketItemBean;
    private List<MarketItemBean> marketItemList;

    private MarketDetailBean.ShareBean mDetailShare;
    public MarketDetailBean mMarketDetailBean;
    public MarketSocketBean marketSocketBean;

    private boolean defaultUpdateAddStatus = false;
    private int currentThemeSource = 0; //0表示白天 1表示夜间
    /**
     * 是否允许添加, true 表示可以添加  图标为+  网络请求为删除  K线图修改的
     */
    private boolean updateAddStatus = true;
    private ShareJson shareJson;

    public boolean portrait = true;

    @OnClick({R.id.ll_market_detail_optional,
                     R.id.ll_market_detail_share,
                     R.id.iv_bar_break,
                     R.id.iv_bar_function,
                     R.id.ll_market_detail_full})
    public void onOptionClick(View view) {
        switch (view.getId()) {
            case R.id.ll_market_detail_optional:
                if (!pageLoadLayout.isSuccessLoadOver()) {
                    return;
                }
                addOrDeleteMarket();
                break;
            case R.id.ll_market_detail_share:
                if (!pageLoadLayout.isSuccessLoadOver()) {
                    return;
                }
                shareJson = new ShareJson(mDetailShare.getTitle(),
                        mDetailShare.getUrl(),
                        mDetailShare.getDescript(),
                        null,
                        null,
                        UmengShareTool.TYPE_DEFAULT,
                        null,
                        null,
                        null,
                        false, false);
                shareJson.setShareFromSource(2);
                shareJson.setWeiBoDiscript(mMarketDetailBean.getShare_sina_title());
                UmengShareTool.initUmengLayout(MarketDetailActivity.this,
                        shareJson,
                        marketItemBean,
                        view, null);
                break;
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.iv_bar_function:
                updateChartDate();
                break;
            case R.id.ll_market_detail_full:
                fullScreenDisplay();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_detail, StatusBarColor.NO_COLOR);

        int alertTheme = ThemeUtil.getAlertTheme(getContext());
        switch (alertTheme) {
            case android.support.v7.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert:
                currentThemeSource = 1;
                break;
            case android.support.v7.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert:
                currentThemeSource = 0;
                break;
        }
        marketSocketBean = new MarketSocketBean();
        requestInitDetail();
    }

    private void initDetailActivity() {

        marketDetailPresenter = new MarketDetailPresenter(this);

        //设置导航栏的标记View 宽度

        JSONArray codes = new JSONArray();
        codes.add(mMarketDetailBean.getData().getCode());
        MarketConnectUtil.getInstance().sendSocketParams(
                this,
                codes,
                this);
    }

    private void requestInitDetail() {
        pageLoadLayout.loadWait();
        marketItemBean = getIntent().getParcelableExtra(IntentConstant.MARKET);
        VolleyRequest volleyRequest = new VolleyRequest(this, mQueue);
        JSONObject jsonParam = volleyRequest.getJsonParam();
        jsonParam.put("code", marketItemBean.getCode());
        volleyRequest.doGet(HttpConstant.MARKET_DETAIL, jsonParam, new HttpListener<MarketDetailBean>() {
            @Override
            protected void onResponse(MarketDetailBean mMarketDetailBean) {
                MarketDetailActivity.this.mMarketDetailBean = mMarketDetailBean;

                tvBarTitle.setText(mMarketDetailBean.getData().getName());
                tvBarCode.setText(mMarketDetailBean.getData().getCode());

                marketItemList = MarketUtil.getMarketEditOption(getContext());
                if (marketItemList == null) {
                    marketItemList = new ArrayList<>();
                }


                marketItemBean = null;
                marketItemBean = mMarketDetailBean.getData();
                mDetailShare = mMarketDetailBean.getShare();

                verifyOptionAppend();
                initDetailActivity();
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                pageLoadLayout.loadError();
            }
        });
    }

    @OnClick({R.id.market_chart_fenshi, R.id.market_chart_fen5,
                     R.id.market_chart_fen15,
                     R.id.market_chart_fen30, R.id.market_chart_fen60,
                     R.id.market_chart_rik, R.id.market_chart_zhouk,
                     R.id.market_chart_yue1})
    public void onNavigationItemClick(View view) {
        RelativeLayout itemView = (RelativeLayout) view;

        if (itemView == clickOldNavigationView) {
            return;
        } else {
            mQueue.cancelAll(marketItemBean.getCode());
        }

        switch (view.getId()) {
            case R.id.market_chart_fenshi:
                clickNavigationPosition = 0;
                break;
            case R.id.market_chart_fen5:
                clickNavigationPosition = 1;
                break;
            case R.id.market_chart_fen15:
                clickNavigationPosition = 2;
                break;
            case R.id.market_chart_fen30:
                clickNavigationPosition = 3;
                break;
            case R.id.market_chart_fen60:
                clickNavigationPosition = 4;
                break;
            case R.id.market_chart_rik:
                clickNavigationPosition = 5;
                break;
            case R.id.market_chart_zhouk:
                clickNavigationPosition = 6;
                break;
            case R.id.market_chart_yue1:
                clickNavigationPosition = 7;
                break;
        }

        requestChartData(clickNavigationPosition);

        TextView itemTextView = (TextView) itemView.findViewWithTag("text");
        itemTextView.setTextColor(ContextCompat.getColor(this, R.color.blue1));
        addLineView(itemView);

        if (clickOldNavigationView != null) {
            try {
                TextView clickOldNavigationTextView = (TextView) clickOldNavigationView.findViewWithTag("text");
                clickOldNavigationTextView.setTextColor(ContextCompat.getColor(this, R.color.font_color2));

                View lineView = clickOldNavigationView.findViewWithTag("lineView");
                clickOldNavigationView.removeView(lineView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        clickOldNavigationView = itemView;
    }

    private void addLineView(View view) {
        float lineWidth = getResources().getDimension(R.dimen.market_nav_item_width);
        int height = SystemUtil.dp2px(this, 2);
        View viewLine = new View(this);
        viewLine.setTag("lineView");
        RelativeLayout.LayoutParams viewLineParams = new RelativeLayout.LayoutParams(
                (int) lineWidth,
                height);
        viewLineParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        int margins = SystemUtil.dp2px(this, 5);
        viewLineParams.setMargins(margins, 0, margins, 0);
        viewLine.setLayoutParams(viewLineParams);
        viewLine.setBackgroundColor(ContextCompat.getColor(this, R.color.blue1));

        RelativeLayout navLayout = (RelativeLayout) view;
        navLayout.addView(viewLine);
    }

    private void requestChartData(final int fromSource) {
        BaseChartPresenter baseChartPresenter = basePresenterMap.get(fromSource);

        chartContainerLayout.removeAllViews();
        if (baseChartPresenter != null) {
            baseChartPresenter.removeHighlight();

            chartContainerLayout.addView(baseChartPresenter.getChartLayout());
            marketChartLoad.loadOver();
            return;
        }
        marketChartLoad.loadWait();

        marketDetailPresenter.requestChartData(marketItemBean.getCode(),
                fromSource,
                new HttpListener<String>() {
                    @Override
                    protected void onResponse(String list) {
                        chartContainerLayout.removeAllViews();

                        marketChartLoad.loadOver();

                        List<MarketTrendBean> marketTrendList = JSONArray.parseArray(list, MarketTrendBean.class);

                        if (fromSource == 0) {
                            MinutePresenter minutePresenter = new MinutePresenter(MarketDetailActivity.this);
                            minutePresenter.initChart();
                            minutePresenter.setData(marketTrendList, 0);
                            basePresenterMap.put(fromSource, minutePresenter);
                        } else {
                            KLinePresenter kLinePresenter = new KLinePresenter(MarketDetailActivity.this);
                            kLinePresenter.initChart();
                            kLinePresenter.setData(marketTrendList, fromSource);

                            basePresenterMap.put(fromSource, kLinePresenter);
                        }
                    }

                    @Override
                    protected void onErrorResponse(VolleyError error) {
                        marketChartLoad.loadError();
                    }
                });
    }

    public void fullScreenDisplay() {
        int requestedOrientation = getRequestedOrientation();
        if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {// 转小屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {// 转全屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void addOrDeleteMarket() {
        if (TextUtils.isEmpty(marketItemBean.getName()) || TextUtils.isEmpty(marketItemBean.getCode())) {
            Toast.makeText(this, "数据不全操作失败", Toast.LENGTH_SHORT).show();
            return;
        }

        if (updateAddStatus) {
            if (marketItemList.size() > 30) {
                TSnackbar.make(ivBarBreak, "自选行情数量太多,超出30条了喔,添加失败", TSnackbar.LENGTH_LONG, TSnackbar
                        .APPEAR_FROM_TOP_TO_DOWN)
                        .setPromptThemBackground(Prompt.WARNING).show();
                return;
            }
            marketItemList.add(marketItemBean);
            ivOptionalImage.setSelected(true);
            TSnackbar.make(ivBarBreak, "添加成功", TSnackbar.LENGTH_LONG, TSnackbar.APPEAR_FROM_TOP_TO_DOWN)
                    .setPromptThemBackground(Prompt.WARNING).show();
        } else {
            marketItemList.remove(marketItemBean);
            ivOptionalImage.setSelected(false);
            TSnackbar.make(ivBarBreak, "取消自选", TSnackbar.LENGTH_LONG, TSnackbar.APPEAR_FROM_TOP_TO_DOWN)
                    .setPromptThemBackground(Prompt.WARNING).show();
        }
        updateAddStatus = !updateAddStatus;

        MarketUtil.saveMarketEditOption(getContext(), marketItemList, 2);
        updateOptionIcon();
    }

    private void verifyOptionAppend() {
        int existPosition = -1;
        for (int i = 0; i < marketItemList.size(); i++) {
            if (marketItemList.get(i).getCode().equals(marketItemBean.getCode())) {
                updateAddStatus = false;
                existPosition = i;
            }
        }
        if (existPosition != -1) {
            marketItemList.remove(existPosition);
            marketItemList.add(existPosition, marketItemBean);
        }
        defaultUpdateAddStatus = updateAddStatus;
        updateOptionIcon();
    }

    private void updateOptionIcon() {
        if (updateAddStatus) {
            ivOptionalImage.setSelected(false);
        } else {
            ivOptionalImage.setSelected(true);
        }
    }


    private void requestAddOrDeleteOptions() {
        if (defaultUpdateAddStatus == updateAddStatus) {
            return;
        }

        MarketUtil.saveMarketEditOption(getContext(), marketItemList, 2);
        EventBusClass eventBusClass = new EventBusClass(
                EventBusClass.MARKET_OPTION_UPDATE,
                marketItemList);
        EventBus.getDefault().post(eventBusClass);

        UserJson userInfo = LoginUtils.getUserInfo(this);
        if (userInfo == null) {
            return;
        }


        String url;
        if (updateAddStatus) {
            url = HttpConstant.QUOTES_DELFAVOR;
        } else {
            url = HttpConstant.QUOTES_ADDFAVOR;
        }

        VolleyRequest volleyRequest = new VolleyRequest(getContext(), getQueue());

        volleyRequest.setTag("");
        JSONObject jsonParam = volleyRequest.getJsonParam();
        jsonParam.put("uid", userInfo.getUid());
        jsonParam.put("accessToken", userInfo.getToken());
        jsonParam.put("code", marketItemBean.getCode());

        volleyRequest.doPost(url, jsonParam, new HttpListener<String>() {
            @Override
            protected void onResponse(String s) {
                LogUtil.e(LogUtil.TAG, "onResponse() called with: s = [" + s + "]");
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
            }
        });
    }


    /**
     * {
     * "b": "0",
     * "c": "SHICOM",
     * "d": "43.409999999999854",
     * "df": "1.361852445593347%",
     * "h": "3232.94",
     * "l": "3179.73",
     * "lc": "3187.57",
     * "o": "3181.4",
     * "p": "3230.98",
     * "s": "0",
     * "t": "1500447600",
     * "tv": "272420700",
     * "v": "0"
     * }
     *
     * @param text
     */

    private long minuteTime1 = 0L;
    private long minuteTime5 = 0L;
    private long minuteTime30 = 0L;

    private int textMessageCount = 0;

    @Override
    public void onTextMessage(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }

        marketSocketBean.setJsonData(text);

        JSONObject jsonObject = null;
        try {
            JSONArray jsonArray = JSONArray.parseArray(text);
            jsonObject = jsonArray.getJSONObject(0);
        } catch (Exception e) {
            try {
                jsonObject = JSONObject.parseObject(text);
            } catch (Exception e1) {
                return;
            }
        }

        String code = jsonObject.getString("c");   //CODE  防止点击块的时候,code 乱来
        if (!marketItemBean.getCode().equals(code)) {
            //存在code不同的现象,则重新发起socket 参数
            JSONArray codes = new JSONArray();
            codes.add(marketItemBean.getCode());
            MarketConnectUtil.getInstance().sendSocketParams(
                    this,
                    codes,
                    this);
            return;
        }


        if (textMessageCount == 0) {//保证数据Socket发送完成并且连接完成之后再发起请求
            textMessageCount = 100;
            onNavigationItemClick(rlFenShiView);
        }


        marketSocketBean.price = jsonObject.getString("p");   //最新价  3.5951
        Double change = jsonObject.getDouble("d");  //涨跌额  0.0238
        marketSocketBean.range = jsonObject.getString("df");  //涨跌幅 "0.6664239912636867%",

        marketSocketBean.zuoshou = jsonObject.getString("lc");  //昨收
        marketSocketBean.jinkai = jsonObject.getString("o");  //今开
        marketSocketBean.zuigao = jsonObject.getString("h");  //最高
        marketSocketBean.zuidi = jsonObject.getString("l");  //最低

        String lastTime = jsonObject.getString("t");//最后更新时间
        long lastTimeLong = Long.parseLong(lastTime) * 1000;

        marketSocketBean.lastTime = "最后更新时间：" + DateFormat.format("HH:mm:ss", lastTimeLong);
        if (marketChartLastTime != null) {
            marketChartLastTime.setText(marketSocketBean.lastTime);
        }

        try {
            conditionRefreshChart(lastTimeLong);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DecimalFormat dfChange = new DecimalFormat("0.00");
        String formatChange = dfChange.format(change);

        marketSocketBean.range = marketSocketBean.range.replace("%", "");
        final double rangeDouble = Double.parseDouble(marketSocketBean.range);


        if (currentThemeSource == 0) {
            //判断涨跌幅状态,

            int colors[] = new int[2];
            GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);

            if (rangeDouble == 0) {
                colors[0] = 0xff7583A0;
                colors[1] = 0xff95A1BB;

            } else if (rangeDouble > 0) {
                colors[0] = 0xffFF564A;
                colors[1] = 0xffFF564A;

            } else if (rangeDouble < 0) {
                colors[0] = 0xff0AB76C;
                colors[1] = 0xff0AB76C;
            }

            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                marketHeadLayout.setBackgroundDrawable(gradientDrawable);
            } else {
                marketHeadLayout.setBackground(gradientDrawable);
            }

        } else {
            //判断涨跌幅状态,
            int textColor = 0;
            if (rangeDouble == 0) {
                textColor = ContextCompat.getColor(this, R.color.unaltered_color1);
            } else if (rangeDouble > 0) {
                textColor = ContextCompat.getColor(this, R.color.rise_color);
            } else if (rangeDouble < 0) {
                textColor = ContextCompat.getColor(this, R.color.decline_color);
            }
            marketChartLow.setTextColor(textColor);
            marketChartZde.setTextColor(textColor);
            marketChartZdf.setTextColor(textColor);

            marketHeadLayout.setBackgroundColor(Color.parseColor("#242424"));
        }

        DecimalFormat df = new DecimalFormat("0.0000");
        String formatRange = df.format(rangeDouble) + "%";

        marketChartLow.setText(marketSocketBean.price);
        marketSocketBean.change = !formatChange.contains("-") ? "+" + formatChange : formatChange;
        marketChartZde.setText(marketSocketBean.change);

        marketSocketBean.range = !formatRange.contains("-") ? "+" + formatRange : formatRange;
        marketChartZdf.setText(marketSocketBean.range);

        marketChartZuoshou.setText(marketSocketBean.zuoshou);
        marketChartJinkai.setText(marketSocketBean.jinkai);
        marketChartZuigao.setText(marketSocketBean.zuigao);
        marketChartZuidi.setText(marketSocketBean.zuidi);

        if (!pageLoadLayout.isSuccessLoadOver()) {//防止进入的时候感觉闪烁一下  体验不好
            pageLoadLayout.loadOver();
        }
    }

    /**
     * 条件满足的时候开始刷新表
     *
     * @param lastTimeLong
     */
    private void conditionRefreshChart(long lastTimeLong) {
        if (minuteTime1 == 0L) {
            minuteTime1 = lastTimeLong;
        } else {
            long intervalTime = lastTimeLong - minuteTime1;
            Log.e(TAG, "剩下刷新时间:minuteTime1 :>> " + (60 - intervalTime / 1000) + "秒");

//            minutePresenter.updateLimitLine(Float.parseFloat(marketSocketBean.price));

            if (intervalTime >= 60 * 1000) { //分时刷新
                minuteTime1 = lastTimeLong;//分时改变到当前时间
                Log.e(TAG, "开始刷新:minuteTime1 :>> " + minuteTime1);

                MarketTrendBean marketTrendBean = new MarketTrendBean();

                marketTrendBean.setClose(Double.parseDouble(marketSocketBean.price));
                marketTrendBean.setHigh(Double.parseDouble(marketSocketBean.zuigao));
                marketTrendBean.setLow(Double.parseDouble(marketSocketBean.zuidi));
                marketTrendBean.setOpen(Double.parseDouble(marketSocketBean.jinkai));

                CharSequence formatQuoteTime = DateFormat.format("yyyy-MM-dd HH:mm:ss", lastTimeLong);
                marketTrendBean.setQuotetime(formatQuoteTime.toString());

                marketTrendBean.setVolume(0);//总量
                marketTrendBean.setStart(0);//开盘时间

                BaseChartPresenter baseChartPresenter = basePresenterMap.get(0);
                if (baseChartPresenter != null) {
                    ((MinutePresenter) baseChartPresenter).notifyDataChanged(marketTrendBean);
                }
            }
        }


        if (minuteTime5 == 0) {
            minuteTime5 = lastTimeLong;
        } else {
            long intervalTime = lastTimeLong - minuteTime5;

            Log.e(TAG, "剩下刷新时间:minuteTime5 :>> " + (5 * 60 - intervalTime / 1000) + "秒");

            if (intervalTime >= 5 * 60 * 1000) { //5分时刷新
                minuteTime5 = lastTimeLong;//分时改变到当前时间

                marketDetailPresenter.requestChartData(marketItemBean.getCode(), 1, new HttpListener<String>() {
                    @Override
                    protected void onResponse(String list) {
                        BaseChartPresenter baseChartPresenter = basePresenterMap.get(1);
                        if (baseChartPresenter != null) {
                            List<MarketTrendBean> marketTrendList = JSONArray.parseArray(list, MarketTrendBean.class);
                            baseChartPresenter.setData(marketTrendList, 1);
                        }
                    }
                });
            }
        }

        if (minuteTime30 == 0) {
            minuteTime30 = lastTimeLong;
        } else {
            long intervalTime = lastTimeLong - minuteTime30;
            Log.e(TAG, "剩下刷新时间:minuteTime30 :>> " + (30 * 60 - intervalTime / 1000) + "秒");

            if (intervalTime >= 30 * 60 * 1000) { //30分时刷新
                minuteTime30 = lastTimeLong;//分时改变到当前时间

                Log.e(TAG, "开始刷新:minuteTime30 :>> " + minuteTime30);
                marketDetailPresenter.requestChartData(marketItemBean.getCode(), 2, new HttpListener<String>() {
                    @Override
                    protected void onResponse(String list) {
                        BaseChartPresenter baseChartPresenter = basePresenterMap.get(2);
                        if (baseChartPresenter != null) {
                            List<MarketTrendBean> marketTrendList = JSONArray.parseArray(list, MarketTrendBean.class);
                            baseChartPresenter.setData(marketTrendList, 2);
                        }
                    }
                });
            }
        }

    }

    private boolean isUpdateDataIng = false;

    public void updateChartDate() {

        if (isUpdateDataIng) {
            return;
        }

        if (basePresenterMap.get(clickNavigationPosition) == null) {
            ToastView.makeText(this, "请稍后刷新");
            return;
        }

        RotateAnimation rotateAnimation = new RotateAnimation(
                0f,
                360f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        rotateAnimation.setDuration(1000);
        rotateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        rotateAnimation.setRepeatCount(-1);

        ivUpdateView.startAnimation(rotateAnimation);

        isUpdateDataIng = true;


        //检查Socket 是否断开！
        JSONArray codes = new JSONArray();
        codes.add(mMarketDetailBean.getData().getCode());
        MarketConnectUtil.getInstance().sendSocketParams(
                this,
                codes,
                this);

        //重置Chart数据
        marketChartLoad.loadWait();
        marketDetailPresenter.requestChartData(marketItemBean.getCode(),
                clickNavigationPosition,
                new HttpListener<String>() {
                    @Override
                    protected void onResponse(String list) {
                        marketChartLoad.loadOver();
                        List<MarketTrendBean> marketTrendList = JSONArray.parseArray(list, MarketTrendBean.class);

                        BaseChartPresenter baseChartPresenter = basePresenterMap.get(clickNavigationPosition);
                        baseChartPresenter.setData(marketTrendList, clickNavigationPosition);
                        isUpdateDataIng = false;
                        ivUpdateView.clearAnimation();

                        baseChartPresenter.removeHighlight();
                    }

                    @Override
                    protected void onErrorResponse(VolleyError error) {
                        marketChartLoad.loadError();

                        isUpdateDataIng = false;
                        ivUpdateView.clearAnimation();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            JSONArray codes = new JSONArray();
            codes.add(mMarketDetailBean.getData().getCode());
            MarketConnectUtil.getInstance().sendSocketParams(
                    this,
                    codes,
                    this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        marketDetailPresenter.onConfigurationChanged(newConfig);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BaseChartPresenter baseChartPresenter = basePresenterMap.get(clickNavigationPosition);
                baseChartPresenter.getChartView().invalidate();
            }
        }, 500);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UmengShareTool.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (!portrait) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return;
        }

        try {
            requestAddOrDeleteOptions();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (shareJson != null) {
                Bitmap shareBitmap = shareJson.getBitmap();
                if (shareBitmap != null && !shareBitmap.isRecycled()) {
                    shareBitmap.recycle();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
