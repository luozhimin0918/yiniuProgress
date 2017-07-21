package com.jyh.kxt.market.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData;
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
import com.jyh.kxt.market.kline.mychart.MyLineChart;
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
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class MarketDetailActivity extends BaseActivity implements ViewPortHandler.OnLongPressIndicatorHandler,
        OnSocketTextMessage {

    @BindView(R.id.ll_market_detail_optional) LinearLayout llMarketDetailOptional;
    @BindView(R.id.iv_market_detail_optional_img) ImageView ivOptionalImage;
    @BindView(R.id.tv_market_detail_optional) TextView tvOptional;
    @BindView(R.id.ll_market_detail_share) LinearLayout llMarketDetailShare;
    @BindView(R.id.pll_content) PageLoadLayout pageLoadLayout;


    @BindView(R.id.market_chart_frame) FrameLayout chartContainerLayout;
    @BindView(R.id.market_chart_fenshi) TextView tvFenShiView;
    @BindView(R.id.view_select_sign) public View selectSignView;

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


    @BindView(R.id.ll_nav) public LinearLayout marketFunctionNav;

    @BindView(R.id.market_chart_load) PageLoadLayout marketChartLoad;

    /**
     * 分时图的Chart
     */
    public TextView minuteChartTime, minuteChartDesc;
    public MyLineChart minuteChartView;
    private MinutePresenter minutePresenter;

    /**
     * K线图的Chart
     */
    public CombinedChart combinedchart;
    public TextView tvMa5, tvMa10, tvMa30;

    public TextView tvKLineTime, tvKLineKaiPan, tvKLineZuiGao, tvKLineZuiDi, tvKLineShouPan;

    private KLinePresenter kLinePresenter;

    private MarketDetailPresenter marketDetailPresenter;
    //点击上面的分时  日时等item
    public int clickNavigationPosition = 0;
    private TextView clickOldNavigationView;
    public int selectSignPadding = 0;
    public int selectSignTranslateLeft = 0;

    //存储请求过来的数据
    private HashMap<Integer, List<MarketTrendBean>> marketTrendMap = new HashMap<>();
    private HashMap<Integer, View> chartMap = new HashMap<>();


    private MarketItemBean marketItemBean;
    private List<MarketItemBean> marketItemList;

    private boolean defaultUpdateAddStatus = false;
    private int currentThemeSource = 0; //0表示白天 1表示夜间
    /**
     * 是否允许添加, true 表示可以添加  图标为+  网络请求为删除  K线图修改的
     */
    private boolean updateAddStatus = true;
    private ShareJson shareJson;

    public MarketDetailBean mMarketDetailBean;
    private MarketDetailBean.ShareBean mDetailShare;

    public MarketSocketBean marketSocketBean;
    public boolean portrait = true;

    @OnClick({R.id.ll_market_detail_optional,
                     R.id.ll_market_detail_share,
                     R.id.iv_bar_break,
                     R.id.iv_bar_function,
                     R.id.market_chart_switch_full})
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
            case R.id.market_chart_switch_full:
                int requestedOrientation = getRequestedOrientation();
                if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {// 转小屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {// 转全屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
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
        selectSignPadding = SystemUtil.dp2px(this, 25);
        DisplayMetrics screenDisplay = SystemUtil.getScreenDisplay(this);
        ViewGroup.LayoutParams selectSingViewParams = selectSignView.getLayoutParams();
        selectSingViewParams.width = screenDisplay.widthPixels / 5 - selectSignPadding;


        onNavigationItemClick(tvFenShiView);


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
                     R.id.market_chart_fen30, R.id.market_chart_rik, R.id.market_chart_zhouk})
    public void onNavigationItemClick(View view) {
        TextView itemView = (TextView) view;

        if (itemView == clickOldNavigationView) {
            return;
        }

        switch (view.getId()) {
            case R.id.market_chart_fenshi:
                clickNavigationPosition = 0;
                break;
            case R.id.market_chart_fen5:
                clickNavigationPosition = 1;
                break;
            case R.id.market_chart_fen30:
                clickNavigationPosition = 2;
                break;
            case R.id.market_chart_rik:
                clickNavigationPosition = 3;
                break;
            case R.id.market_chart_zhouk:
                clickNavigationPosition = 4;
                break;
        }

        requestChartData(clickNavigationPosition);

        itemView.setTextColor(ContextCompat.getColor(this, R.color.blue1));
        if (clickOldNavigationView != null) {
            clickOldNavigationView.setTextColor(ContextCompat.getColor(this, R.color.font_color2));
        }

        final int toXDelta = view.getLeft() + selectSignPadding / 2;
        TranslateAnimation translateAnimation = new TranslateAnimation(
                selectSignTranslateLeft,
                toXDelta,
                0,
                0);
        translateAnimation.setDuration(400);
        translateAnimation.setInterpolator(new OvershootInterpolator());
        translateAnimation.setFillAfter(true);
        selectSignView.startAnimation(translateAnimation);

        selectSignTranslateLeft = toXDelta;
        clickOldNavigationView = itemView;
    }

    private void requestChartData(final int fromSource) {
        View chartView = chartMap.get(fromSource);
        List<MarketTrendBean> localMarketList = marketTrendMap.get(fromSource);

        chartContainerLayout.removeAllViews();
        if (chartView != null && localMarketList != null) {
            chartContainerLayout.addView(chartView);
            marketChartLoad.loadOver();
            return;
        }
        marketChartLoad.loadWait();

        marketDetailPresenter.requestChartData(marketItemBean.getCode(),
                fromSource,
                new HttpListener<String>() {
                    @Override
                    protected void onResponse(String list) {
                        marketChartLoad.loadOver();

                        List<MarketTrendBean> marketTrendList = JSONArray.parseArray(list, MarketTrendBean.class);
                        marketTrendMap.put(fromSource, marketTrendList);

                        if (fromSource == 0) {
                            LayoutInflater mInflater = LayoutInflater.from(getContext());
                            View minuteLayout = mInflater.inflate(
                                    R.layout.view_market_chart_minute,
                                    chartContainerLayout,
                                    false);
                            chartContainerLayout.addView(minuteLayout);
                            chartMap.put(fromSource, minuteLayout);

                            minuteChartTime = (TextView) minuteLayout.findViewById(R.id.tv_minute_time);
                            minuteChartDesc = (TextView) minuteLayout.findViewById(R.id.tv_current_price);
                            minuteChartView = (MyLineChart) minuteLayout.findViewById(R.id.minute_chart);

                            minutePresenter = new MinutePresenter(MarketDetailActivity.this);
                            minutePresenter.initChart(MarketDetailActivity.this);
                            minutePresenter.setData(marketTrendList);

                        } else {
                            LayoutInflater mInflater = LayoutInflater.from(getContext());
                            View kLineLayout = mInflater.inflate(
                                    R.layout.view_market_chart_kline,
                                    chartContainerLayout,
                                    false);
                            chartContainerLayout.addView(kLineLayout);
                            chartMap.put(fromSource, kLineLayout);

                            combinedchart = (CombinedChart) kLineLayout.findViewById(R.id.combinedchart);
                            tvMa5 = (TextView) kLineLayout.findViewById(R.id.kline_tv_ma5);
                            tvMa10 = (TextView) kLineLayout.findViewById(R.id.kline_tv_ma10);
                            tvMa30 = (TextView) kLineLayout.findViewById(R.id.kline_tv_ma30);

                            tvKLineTime = (TextView) kLineLayout.findViewById(R.id.kline_tv_time);
                            tvKLineKaiPan = (TextView) kLineLayout.findViewById(R.id.kline_tv_kaipan);
                            tvKLineZuiGao = (TextView) kLineLayout.findViewById(R.id.kline_tv_zuigao);
                            tvKLineZuiDi = (TextView) kLineLayout.findViewById(R.id.kline_tv_zuidi);
                            tvKLineShouPan = (TextView) kLineLayout.findViewById(R.id.kline_tv_shoupan);

                            kLinePresenter = new KLinePresenter(MarketDetailActivity.this);
                            kLinePresenter.initChart(MarketDetailActivity.this);
                            kLinePresenter.setData(marketTrendList);

                        }
                    }

                    @Override
                    protected void onErrorResponse(VolleyError error) {
                        marketChartLoad.loadError();
                    }
                });
    }


    @Override
    public void longPressIndicator(int xIndex, BarLineScatterCandleBubbleData candleData) {

        if (clickNavigationPosition == 0 && minuteChartView != null) {
         /*   Entry entryForXIndex;
            if (candleData instanceof LineData) {
                entryForXIndex = ((LineData) candleData).getDataSets().get(0).getEntryForXIndex(xIndex);
            } else {
                entryForXIndex = ((CandleData) candleData).getDataSets().get(0).getEntryForXIndex(xIndex);
            }
            String text = "现价：" + entryForXIndex.getVal();
            SpannableStringBuilder builder = new SpannableStringBuilder(text);

            ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
            builder.setSpan(redSpan, 3, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            minuteChartDesc.setText(builder);*/
            minutePresenter.longPressIndicator(xIndex);
        } else if (clickNavigationPosition != 0) {
            kLinePresenter.longPressIndicator(xIndex);
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
            TSnackbar.make(ivBarBreak, "已删除", TSnackbar.LENGTH_LONG, TSnackbar.APPEAR_FROM_TOP_TO_DOWN)
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
            tvOptional.setText("添加自选");
        } else {
            ivOptionalImage.setSelected(true);
            tvOptional.setText("删除自选");
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

    private long oldLastTime;

    @Override
    public synchronized void onTextMessage(String text) {
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

        CharSequence currentMM = DateFormat.format("mm", lastTimeLong);
        if (oldLastTime != 0) {
            CharSequence oldMM = DateFormat.format("mm", oldLastTime);
            if (!currentMM.equals(oldMM)) {
                MarketTrendBean marketTrendBean = new MarketTrendBean();

                marketTrendBean.setClose(Double.parseDouble(marketSocketBean.price));
                marketTrendBean.setHigh(Double.parseDouble(marketSocketBean.zuigao));
                marketTrendBean.setLow(Double.parseDouble(marketSocketBean.zuidi));
                marketTrendBean.setOpen(Double.parseDouble(marketSocketBean.jinkai));

                CharSequence formatQuoteTime = DateFormat.format("yyyy-MM-dd HH:mm:ss", lastTimeLong);
                marketTrendBean.setQuotetime(formatQuoteTime.toString());

                marketTrendBean.setVolume(0);//总量
                marketTrendBean.setStart(0);//开盘时间

                if (minutePresenter != null) {
                    minutePresenter.notifyDataChanged(marketTrendBean);
                }
            }
        }

        oldLastTime = lastTimeLong;

        DecimalFormat df = new DecimalFormat("0.00");
        String formatChange = df.format(change);

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
                colors[0] = 0xffFF5159;
                colors[1] = 0xffFF866C;

            } else if (rangeDouble < 0) {
                colors[0] = 0xff00B4C7;
                colors[1] = 0xff00D8A9;
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
        }

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

    private boolean isUpdateDataIng = false;

    public void updateChartDate() {

        if (isUpdateDataIng) {
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
                        if (clickNavigationPosition == 0) {
                            minutePresenter.setData(marketTrendList);
                        } else {
                            kLinePresenter.setData(marketTrendList);
                        }

                        isUpdateDataIng = false;
                        ivUpdateView.clearAnimation();
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        marketDetailPresenter.onConfigurationChanged(newConfig);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (combinedchart != null) {
                    combinedchart.invalidate();
                }
                if (minuteChartView != null) {
                    minuteChartView.invalidate();
                }
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
