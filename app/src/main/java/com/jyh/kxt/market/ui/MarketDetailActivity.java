package com.jyh.kxt.market.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
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
import com.jyh.kxt.market.bean.MarketDetailBean;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.jyh.kxt.market.kline.bean.MarketTrendBean;
import com.jyh.kxt.market.kline.mychart.MyLineChart;
import com.jyh.kxt.market.presenter.KLinePresenter;
import com.jyh.kxt.market.presenter.MarketDetailChartPresenter;
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

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_code) TextView tvBarCode;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;

    @BindView(R.id.ll_market_detail_optional) LinearLayout llMarketDetailOptional;
    @BindView(R.id.iv_market_detail_optional_img) ImageView ivOptionalImage;
    @BindView(R.id.tv_market_detail_optional) TextView tvOptional;
    @BindView(R.id.ll_market_detail_share) LinearLayout llMarketDetailShare;
    @BindView(R.id.pll_content) PageLoadLayout pageLoadLayout;


    @BindView(R.id.market_head_layout) RelativeLayout marketHeadLayout;
    @BindView(R.id.market_chart_frame) FrameLayout frameLayout;
    @BindView(R.id.market_chart_fenshi) TextView tvFenShiView;
    @BindView(R.id.view_select_sign) View selectSignView;

    @BindView(R.id.market_chart_low) TextView marketChartLow;
    @BindView(R.id.market_chart_zde) TextView marketChartZde;
    @BindView(R.id.market_chart_zdf) TextView marketChartZdf;
    @BindView(R.id.market_chart_zuoshou) TextView marketChartZuoshou;
    @BindView(R.id.market_chart_jinkai) TextView marketChartJinkai;
    @BindView(R.id.market_chart_zuigao) TextView marketChartZuigao;
    @BindView(R.id.market_chart_zuidi) TextView marketChartZuidi;
    @BindView(R.id.market_chart_update_time) TextView marketChartLastTime;

    @BindView(R.id.market_chart_load) PageLoadLayout marketChartLoad;

    /**
     * 分时图的Chart
     */
    public TextView minuteChartDesc;
    public MyLineChart minuteChartView;
    private MinutePresenter minutePresenter;

    /**
     * K线图的Chart
     */
    public CombinedChart combinedchart;
    public TextView tvMa5, tvMa10, tvMa30;
    private KLinePresenter kLinePresenter;

    private MarketDetailChartPresenter marketDetailChartPresenter;
    //点击上面的分时  日时等item
    private int clickNavigationPosition = 0;
    private TextView clickOldNavigationView;
    private int selectSignPadding = 0;
    private int selectSignTranslateLeft = 0;

    //存储请求过来的数据
    private HashMap<Integer, List<MarketTrendBean>> marketTrendMap = new HashMap<>();
    private HashMap<Integer, View> chartMap = new HashMap<>();


    private MarketItemBean marketItemBean;
    private List<MarketItemBean> marketItemList;

    private boolean defaultUpdateAddStatus = false;
    /**
     * 是否允许添加, true 表示可以添加  图标为+  网络请求为删除  K线图修改的
     */
    private boolean updateAddStatus = true;
    private ShareJson shareJson;

    private MarketDetailBean mMarketDetailBean;
    private MarketDetailBean.ShareBean mDetailShare;

    @OnClick({R.id.ll_market_detail_optional, R.id.ll_market_detail_share, R.id.iv_bar_break})
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
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_detail, StatusBarColor.NO_COLOR);
        requestInitDetail();
    }

    private void initDetailActivity() {

        marketDetailChartPresenter = new MarketDetailChartPresenter(this);

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
                pageLoadLayout.loadOver();
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

        frameLayout.removeAllViews();

        if (chartView != null && localMarketList != null) {
            frameLayout.addView(chartView);
            marketChartLoad.loadOver();
            return;
        }
        marketChartLoad.loadWait();

        marketDetailChartPresenter.requestChartData(marketItemBean.getCode(),
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
                                    frameLayout,
                                    false);
                            frameLayout.addView(minuteLayout);
                            chartMap.put(fromSource, minuteLayout);

                            minuteChartDesc = (TextView) minuteLayout.findViewById(R.id.tv_current_price);
                            minuteChartView = (MyLineChart) minuteLayout.findViewById(R.id.minute_chart);

                            minutePresenter = new MinutePresenter(MarketDetailActivity.this);
                            minutePresenter.initChart(MarketDetailActivity.this);
                            minutePresenter.setData(marketTrendList);

                        } else {
                            LayoutInflater mInflater = LayoutInflater.from(getContext());
                            View kLineLayout = mInflater.inflate(
                                    R.layout.view_market_chart_kline,
                                    frameLayout,
                                    false);
                            frameLayout.addView(kLineLayout);
                            chartMap.put(fromSource, kLineLayout);

                            combinedchart = (CombinedChart) kLineLayout.findViewById(R.id.combinedchart);
                            tvMa5 = (TextView) kLineLayout.findViewById(R.id.kline_tv_ma5);
                            tvMa10 = (TextView) kLineLayout.findViewById(R.id.kline_tv_ma10);
                            tvMa30 = (TextView) kLineLayout.findViewById(R.id.kline_tv_ma30);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UmengShareTool.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        try {
            requestAddOrDeleteOptions();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onBackPressed();
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

    private int oldHeadBgState = 0;

    @Override
    public void onTextMessage(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        JSONObject jsonObject = null;
        try {
            JSONArray jsonArray = JSONArray.parseArray(text);
            jsonObject = jsonArray.getJSONObject(0);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                jsonObject = JSONObject.parseObject(text);
            } catch (Exception e1) {
                e1.printStackTrace();
                return;
            }
        }

        String price = jsonObject.getString("p");   //最新价  3.5951
        Double change = jsonObject.getDouble("d");  //涨跌额  0.0238
        String range = jsonObject.getString("df");  //涨跌幅 "0.6664239912636867%",

        String zuoshou = jsonObject.getString("lc");  //昨收
        String jinkai = jsonObject.getString("o");  //今开
        String zuigao = jsonObject.getString("h");  //最高
        String zuidi = jsonObject.getString("l");  //最低

        String lastTime = jsonObject.getString("t");//最后更新时间
        long lastTimeLong = Long.parseLong(lastTime) * 1000;
        marketChartLastTime.setText("最后更新时间： " + DateFormat.format("HH:mm:ss", lastTimeLong));

        DecimalFormat df = new DecimalFormat("0.00");
        String formatChange = df.format(change);

        range = range.replace("%", "");
        double rangeDouble = Double.parseDouble(range);


        //判断涨跌幅状态,
        if (rangeDouble == 0 && oldHeadBgState != 0) {
            marketHeadLayout.setBackgroundResource(R.mipmap.market_head_bg0);
            oldHeadBgState = 0;
        } else if (rangeDouble > 0 && oldHeadBgState != 1) {
            marketHeadLayout.setBackgroundResource(R.mipmap.market_head_bg1);
            oldHeadBgState = 1;
        } else if (rangeDouble < 0 && oldHeadBgState != 2) {
            marketHeadLayout.setBackgroundResource(R.mipmap.market_head_bg2);
            oldHeadBgState = 2;
        }

        String formatRange = df.format(rangeDouble) + "%";

        marketChartLow.setText(price);
        marketChartZde.setText(formatChange.indexOf("-") == -1 ? "+" + formatChange : formatChange);
        marketChartZdf.setText(formatRange.indexOf("-") == -1 ? "+" + formatRange : formatRange);

        marketChartZuoshou.setText(zuoshou);
        marketChartJinkai.setText(jinkai);
        marketChartZuigao.setText(zuigao);
        marketChartZuidi.setText(zuidi);


    }
}
