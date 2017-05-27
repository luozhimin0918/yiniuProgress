package com.jyh.kxt.market.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.json.ShareJson;
import com.jyh.kxt.base.utils.UmengShareTool;
import com.jyh.kxt.base.widget.LoadX5WebView;
import com.jyh.kxt.base.widget.night.ThemeUtil;
import com.jyh.kxt.index.json.MainInitJson;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.library.base.http.VarConstant;
import com.library.util.BitmapUtils;
import com.library.util.SPUtils;
import com.library.widget.window.ToastView;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MarketDetailActivity extends BaseActivity {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;

    @BindView(R.id.ll_market_detail_optional) LinearLayout llMarketDetailOptional;
    @BindView(R.id.iv_market_detail_optional_img) ImageView ivOptionalImage;
    @BindView(R.id.tv_market_detail_optional) TextView tvOptional;
    @BindView(R.id.ll_market_detail_share) LinearLayout llMarketDetailShare;
    @BindView(R.id.activity_market_detail) RelativeLayout activityMarketDetail;
    @BindView(R.id.lwv_content) LoadX5WebView lwvContent;

    private MarketItemBean marketItemBean;
    private List<MarketItemBean> marketItemList;

    /**
     * 是否允许添加, true 表示可以添加  为+
     */
    private boolean isAllowAddOptional = true;
    private String quotesChartUrl;

    @OnClick({R.id.ll_market_detail_optional, R.id.ll_market_detail_share})
    public void onOptionClick(View view) {
        switch (view.getId()) {
            case R.id.ll_market_detail_optional:
                addOrDeleteMarket();
                break;
            case R.id.ll_market_detail_share:

                if (oldBitmap != null && !oldBitmap.isRecycled()) {
                    oldBitmap.recycle();
                    oldBitmap = null;
                }
                getScreenBitmap(new ObserverData<Bitmap>() {
                    @Override
                    public void callback(Bitmap bitmap) {
                        if (bitmap != null) {
                            UmengShareTool.initUmengLayout(MarketDetailActivity.this, new ShareJson(marketItemBean.getName(),
                                    quotesChartUrl, "", null, bitmap,
                                    UmengShareTool.TYPE_MARKET, null, null, null, false, false), null, ivBarBreak, null);
                        } else {
                            ToastView.makeText3(MarketDetailActivity.this, "截图失败无法分享,请重试");
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        ToastView.makeText3(MarketDetailActivity.this, "截图失败无法分享,请重试");
                    }

                }, this);


                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_detail, StatusBarColor.THEME1);

        try {
            String marketOption = SPUtils.getString(this, SpConstant.MARKET_MY_OPTION);
            marketItemList = JSONArray.parseArray(marketOption, MarketItemBean.class);
            if (marketItemList == null) {
                marketItemList = new ArrayList<>();
            }
            String appConfig = SPUtils.getString(this, SpConstant.INIT_LOAD_APP_CONFIG);
            MainInitJson mainInitJson = JSONObject.parseObject(appConfig, MainInitJson.class);
            marketItemBean = getIntent().getParcelableExtra("market");

            quotesChartUrl = mainInitJson.getQuotes_chart_url();
            quotesChartUrl = quotesChartUrl.replaceAll("\\{code\\}", marketItemBean.getCode());
            quotesChartUrl = quotesChartUrl.replaceAll("\\{system\\}", VarConstant.HTTP_SYSTEM_VALUE);
            quotesChartUrl = quotesChartUrl.replaceAll("\\{version\\}", VarConstant.HTTP_VERSION_VALUE);


            ThemeUtil.addActivityToThemeCache(this);

            Boolean isNight = SPUtils.getBoolean(this, SpConstant.SETTING_DAY_NIGHT);
            if (isNight) {
                quotesChartUrl += "&yejian=1";
            } else {
                quotesChartUrl += "&yejian=0";
            }

            lwvContent.build();
            lwvContent.loadUrl(quotesChartUrl);
            verifyOptionAppend();

            tvBarTitle.setText(marketItemBean.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addOrDeleteMarket() {
        if (isAllowAddOptional) {
            marketItemList.add(marketItemBean);
            ivOptionalImage.setSelected(true);
            tvOptional.setText("取消自选");
            TSnackbar.make(ivBarBreak, "添加成功", TSnackbar.LENGTH_LONG, TSnackbar.APPEAR_FROM_TOP_TO_DOWN)
                    .setPromptThemBackground(Prompt.WARNING).show();
        } else {
            marketItemList.remove(marketItemBean);
            ivOptionalImage.setSelected(false);
            tvOptional.setText("添加自选");
            TSnackbar.make(ivBarBreak, "已删除", TSnackbar.LENGTH_LONG, TSnackbar.APPEAR_FROM_TOP_TO_DOWN)
                    .setPromptThemBackground(Prompt.WARNING).show();
        }
        isAllowAddOptional = !isAllowAddOptional;

        String listStr = JSON.toJSONString(marketItemList);
        SPUtils.save(this, SpConstant.MARKET_MY_OPTION, listStr);
        updateOptionIcon();
    }

    private void verifyOptionAppend() {
        int existPosition = -1;
        for (int i = 0; i < marketItemList.size(); i++) {
            if (marketItemList.get(i).getCode().equals(marketItemBean.getCode())) {
                isAllowAddOptional = false;
                existPosition = i;
            }
        }
        if (existPosition != -1) {
            marketItemList.remove(existPosition);
            marketItemList.add(existPosition, marketItemBean);
        }
        updateOptionIcon();
    }

    private void updateOptionIcon() {
        if (isAllowAddOptional) {
            ivOptionalImage.setSelected(false);
            tvOptional.setText("添加自选");
        } else {
            ivOptionalImage.setSelected(true);
            tvOptional.setText("删除自选");
        }
    }

    private Bitmap oldBitmap;

    public void getScreenBitmap(final ObserverData<Bitmap> observerData, Activity activity) {
        // 获取windows中最顶层的view
        final View view = activity.getWindow().getDecorView();
        view.buildDrawingCache();

        // 获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        final int statusBarHeights = rect.top;
        Display display = activity.getWindowManager().getDefaultDisplay();

        // 获取屏幕宽和高
        final int widths = display.getWidth();
        final int heights = display.getHeight();
        final int height = heights - statusBarHeights;

        // 允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);

        // 去掉状态栏
        final Bitmap bitmap = view.getDrawingCache();
        byte[] drawCacheBitmapBytes = BitmapUtils.Bitmap2Bytes(bitmap, Bitmap.CompressFormat.JPEG);

        Glide.with(activity).load(drawCacheBitmapBytes).asBitmap().override(widths / 2, height / 2).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                // 销毁缓存信息
                oldBitmap = resource;
                view.destroyDrawingCache();
                observerData.callback(resource);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UmengShareTool.onActivityResult(this, requestCode, resultCode, data);
    }
}
