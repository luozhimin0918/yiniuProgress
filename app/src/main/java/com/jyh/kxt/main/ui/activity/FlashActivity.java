package com.jyh.kxt.main.ui.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.custom.RadianDrawable;
import com.jyh.kxt.base.json.ShareJson;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.base.utils.MarketConnectUtil;
import com.jyh.kxt.base.utils.PingYinUtil;
import com.jyh.kxt.base.utils.UmengShareTool;
import com.jyh.kxt.base.utils.collect.CollectUtils;
import com.jyh.kxt.base.widget.StarView;
import com.jyh.kxt.base.widget.night.ThemeUtil;
import com.jyh.kxt.index.json.MainInitJson;
import com.jyh.kxt.main.adapter.NewsAdapter;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.main.json.SlideJson;
import com.jyh.kxt.main.json.flash.FlashContentJson;
import com.jyh.kxt.main.json.flash.FlashJson;
import com.jyh.kxt.main.json.flash.Flash_KX;
import com.jyh.kxt.main.json.flash.Flash_NEWS;
import com.jyh.kxt.main.json.flash.Flash_RL;
import com.jyh.kxt.main.presenter.FlashActivityPresenter;
import com.library.base.http.VarConstant;
import com.library.bean.EventBusClass;
import com.library.util.DateUtils;
import com.library.util.SPUtils;
import com.library.util.SystemUtil;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:快讯详情
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/5.
 */

public class FlashActivity extends BaseActivity implements PageLoadLayout.OnAfreshLoadListener, AdapterView.OnItemClickListener {

    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;
    @BindView(R.id.plv_content) public PullToRefreshListView plvContent;
    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;
    @BindView(R.id.iv_break) ImageView ivBreak;
    @BindView(R.id.iv_collect) ImageView ivCollect;
    @BindView(R.id.iv_share) ImageView ivShare;
    @BindView(R.id.iv_more) ImageView ivMore;

    private FlashActivityPresenter flashActivityPresenter;
    private String id;
    private boolean isCollect;
    private boolean isLoadOver;
    private FlashJson flashJson;
    private String title = "";
    private String shareUrl = "";
    private String discription = "";
    private String image = "";
    private View flashHeadView;

    private TextView tvTime;
    private TextView tvFlashTitle;
    private TextView tvFlashContent;
    private LinearLayout llFlashAd;
    private TextView tvRlTitle;
    private StarView rlStar;
    private ImageView ivRlFlag;
    private LinearLayout rlContent;
    private TextView tvDescribe;
    private LinearLayout llRLAD;
    private LinearLayout layoutTj;
    private View layoutRL;
    private View layoutFlash;
    private NewsAdapter newsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash, StatusBarColor.THEME1);
        flashActivityPresenter = new FlashActivityPresenter(this);

        plvContent.setDividerNull();
        plvContent.setMode(PullToRefreshBase.Mode.DISABLED);
        plvContent.setOnItemClickListener(this);
        plRootView.setOnAfreshLoadListener(this);

        ivBarBreak.setVisibility(View.INVISIBLE);
        tvBarTitle.setText("快讯");

        plRootView.loadWait();
        id = getIntent().getStringExtra(IntentConstant.O_ID);

        flashActivityPresenter.init(id);

    }

    @OnClick({R.id.iv_break, R.id.iv_collect, R.id.iv_share, R.id.iv_more})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_break:
                onBackPressed();
                break;
            case R.id.iv_collect:
                if (isLoadOver)
                    if (isCollect) {
                        CollectUtils.unCollect(this, VarConstant.COLLECT_TYPE_FLASH, flashJson, new ObserverData() {
                            @Override
                            public void callback(Object o) {
                                ivCollect.setSelected(false);
                                isCollect = false;
                                EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_COLLECT_FLASH, flashJson));
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        }, null);
                    } else {
                        CollectUtils.collect(this, VarConstant.COLLECT_TYPE_FLASH, flashJson, new ObserverData() {
                            @Override
                            public void callback(Object o) {
                                ivCollect.setSelected(true);
                                isCollect = true;
                                EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_COLLECT_FLASH, flashJson));
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        }, null);
                    }

                break;
            case R.id.iv_share:
                UmengShareTool.initUmengLayout(this, new ShareJson(title, shareUrl, discription, image, null,
                        UmengShareTool.TYPE_DEFAULT, null, null, null, false, false), flashJson, ivShare, null);
                break;
            case R.id.iv_more:
                int theme = ThemeUtil.getAlertTheme(getContext());
                switch (theme) {
                    case android.support.v7.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert:
                        setDayNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        SPUtils.save(getContext(), SpConstant.SETTING_DAY_NIGHT, false);
                        break;
                    case android.support.v7.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert:
                        setDayNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        SPUtils.save(getContext(), SpConstant.SETTING_DAY_NIGHT, true);
                        break;
                }
                break;
        }
    }

    /**
     * 初始化布局
     *
     * @param flash
     */
    public void init(FlashContentJson flash) {
        List<NewsJson> article = flash.getArticle();
        try {
            if (plvContent.getRefreshableView().getHeaderViewsCount() <= 1) {
                initHeadView();
                plvContent.getRefreshableView().addHeaderView(flashHeadView);
            }
            initHeadData(flash);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (newsAdapter == null) {
            newsAdapter = new NewsAdapter(this, article);
            plvContent.setAdapter(newsAdapter);
        } else {
            newsAdapter.setData(article);
        }

        plRootView.loadOver();

    }

    /**
     * 初始化头部布局
     */
    public void initHeadView() {
        flashHeadView = LayoutInflater.from(this).inflate(R.layout.layout_head_flashcontent, null, false);

        tvTime = (TextView) flashHeadView.findViewById(R.id.tv_time);
        tvFlashTitle = (TextView) flashHeadView.findViewById(R.id.tv_flash_title);
        tvFlashContent = (TextView) flashHeadView.findViewById(R.id.tv_flash_content);
        llFlashAd = (LinearLayout) flashHeadView.findViewById(R.id.iv_adFlash);
        tvRlTitle = (TextView) flashHeadView.findViewById(R.id.tv_rl_title);
        rlStar = (StarView) flashHeadView.findViewById(R.id.sv_star);
        ivRlFlag = (ImageView) flashHeadView.findViewById(R.id.iv_flag);
        rlContent = (LinearLayout) flashHeadView.findViewById(R.id.ll_content);
        tvDescribe = (TextView) flashHeadView.findViewById(R.id.tv_describe);
        llRLAD = (LinearLayout) flashHeadView.findViewById(R.id.iv_adRL);
        layoutTj = (LinearLayout) flashHeadView.findViewById(R.id.ll_tj);
        layoutRL = flashHeadView.findViewById(R.id.layout_rl);
        layoutFlash = flashHeadView.findViewById(R.id.layout_flash);

        DisplayMetrics screenDisplay = SystemUtil.getScreenDisplay(this);
        int headHeight = screenDisplay.heightPixels - SystemUtil.dp2px(this, 266)-SystemUtil.getStatuBarHeight(this);//两个item高度84*2 + 上下导航栏高度(48+50)
        flashHeadView.setMinimumHeight(headHeight);
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .WRAP_CONTENT);
        flashHeadView.setLayoutParams(layoutParams);
    }

    public void initHeadData(FlashContentJson flash) {
        try {
            flashJson = flash.getKuaixun();

            isCollect = CollectUtils.isCollect(this, VarConstant.COLLECT_TYPE_FLASH, flashJson);

            String configStr = SPUtils.getString(this, SpConstant.INIT_LOAD_APP_CONFIG);
            MainInitJson config = JSON.parseObject(configStr, MainInitJson.class);
            String url_kx_share = config.getUrl_kx_share();

            shareUrl = url_kx_share.replace("{id}", flashJson.getSocre());

            ivCollect.setSelected(isCollect);

            List<SlideJson> ads = flash.getAd();
            String type = flashJson.getCode();
            switch (type) {
                case VarConstant.SOCKET_FLASH_KUAIXUN:
                    initFlash(flashJson.getContent(), ads);
                    break;
                case VarConstant.SOCKET_FLASH_CJRL:
                    initRl(flashJson.getContent(), ads);
                    break;
            }
            plRootView.loadOver();
            isLoadOver = true;
        } catch (Exception e) {
            e.printStackTrace();
            plRootView.loadError();
        }
    }

    private void initFlash(String content, List<SlideJson> ads) throws Exception {
        layoutFlash.setVisibility(View.VISIBLE);
        layoutRL.setVisibility(View.GONE);
        Flash_KX flash_kx = JSON.parseObject(content, Flash_KX.class);

        title = flash_kx.getTitle();
        String time = flash_kx.getTime();
        try {
            String timeInMill = DateUtils.transfromTime(flash_kx.getTime(), DateUtils.TYPE_YMDHMS);
            time = DateUtils.transformTime(Long.parseLong(timeInMill), DateUtils.TYPE_YMDE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvTime.setText(time);

        String contentStr = flash_kx.getTitle();
        String[] split = contentStr.split("<br />");
        int length = split.length;
        if (length > 1) {
            tvFlashTitle.setText(split[0]);
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < length; i++) {
                if (i >= 1)
                    buffer.append(split[i]);
            }
            tvFlashContent.setText(buffer.toString().replace("<br/>", "\n").replace("<br />", "\n"));
        } else {
            tvFlashContent.setVisibility(View.GONE);
            tvFlashTitle.setText(contentStr.replace("<br/>", "\n").replace("<br />", "\n"));
        }

        String importance = flash_kx.getImportance();

        if (ads != null && ads.size() > 0) {
            for (final SlideJson ad : ads) {
                ImageView ivAd = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                        .LayoutParams.WRAP_CONTENT);
                ivAd.setLayoutParams(params);
                llFlashAd.addView(ivAd);
                Glide.with(this).load(HttpConstant.IMG_URL + ad.getPicture()).error(R.mipmap.icon_def_video).placeholder(R.mipmap
                        .icon_def_video).into(ivAd);
                ivAd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JumpUtils.jump(FlashActivity.this, ad.getO_class(), ad.getO_action(), ad.getO_id(), ad.getHref());
                    }
                });
            }
        }

        if (VarConstant.IMPORTANCE_HIGH.equals(importance)) {
            tvFlashTitle.setTextColor(ContextCompat.getColor(this, R.color.font_color11));
            tvFlashContent.setTextColor(ContextCompat.getColor(this, R.color.font_color11));
        } else {
            tvFlashTitle.setTextColor(ContextCompat.getColor(this, R.color.font_color1));
            tvFlashContent.setTextColor(ContextCompat.getColor(this, R.color.font_color1));
        }
    }

    private void initRl(String content, List<SlideJson> ads) throws Exception {
        layoutFlash.setVisibility(View.GONE);
        layoutRL.setVisibility(View.VISIBLE);

        Flash_RL rl = JSON.parseObject(content, Flash_RL.class);
        discription = getResources().getString(R.string.date_describe,
                rl.getBefore(),
                rl.getForecast(),
                rl.getReality());
        tvRlTitle.setText(rl.getTitle());

        Glide.with(this).load(String.format(HttpConstant.FLAG_URL, PingYinUtil.getFirstSpell(rl.getState()))).into(ivRlFlag);

        if (ads != null && ads.size() > 0) {
            for (SlideJson ad : ads) {
                ImageView ivAd = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                        .LayoutParams.WRAP_CONTENT);
                ivAd.setLayoutParams(params);
                llRLAD.addView(ivAd);
                Glide.with(this).load(HttpConstant.IMG_URL + ad.getPicture()).error(R.mipmap.icon_def_video).placeholder(R.mipmap
                        .icon_def_video).into(ivAd);
            }
        }

        String time2 = "00:00";
        try {
            time2 = getTime(rl.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvTime.setText(time2);

        tvDescribe.setText(getResources().getString(R.string.date_describe, rl.getBefore(), rl.getForecast(), rl
                .getReality()));

        /**
         * 前值 后值 等
         */
        String describe = getResources().getString(R.string.date_describe,
                rl.getBefore(),
                rl.getForecast(),
                rl.getReality());

        String reality = rl.getReality();
        setDescribeForegroundColor(tvDescribe, describe, reality);

        /**
         *  重要程度
         */
        rlStar.setImportance(rl.getImportance());

        /**
         * 公布状态, 已公布,未公布 利多 ,金银 , 石油   影响较小等
         */
        setAlarmState(reality,
                Integer.parseInt(rl.getEffecttype()),//0 利多美元  1 利多金银石油 2 影响较小
                rlContent);
    }

    @Override
    public void OnAfreshLoad() {
        plRootView.loadWait();
        flashActivityPresenter.init(id);
    }

    private String getTime(String time) throws Exception {
        String[] splitTime = time.split(" ");
        String[] splitTime2 = splitTime[1].split(":");
        return splitTime2[0] + ":" + splitTime2[1];
    }

    /**
     * 设置前值、预期值等
     *
     * @param tvDescribe
     * @param describe
     * @param reality
     */
    private void setDescribeForegroundColor(TextView tvDescribe, String describe, String reality) {
        try {
            float realityFloat = Float.parseFloat(reality.replace("%", ""));
            int index = describe.lastIndexOf(":") + 1;
            SpannableString spannableString = new SpannableString(describe);
            if (realityFloat > 0) {
                spannableString.setSpan(
                        new ForegroundColorSpan(ContextCompat.getColor(this, R.color.rise_color)),
                        index,
                        describe.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


            } else if (realityFloat < 0) {
                spannableString.setSpan(
                        new ForegroundColorSpan(ContextCompat.getColor(this, R.color.decline_color)),
                        index,
                        describe.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                tvDescribe.setTextColor(ContextCompat.getColor(this, R.color.unaltered_color));
            }
        } catch (NumberFormatException e) {
        } finally {
            tvDescribe.setText(describe);
        }
    }

    private void setAlarmState(String reality, int effectType, LinearLayout llExponent) {

        llExponent.removeAllViews();


        String effect = "||";
        switch (effectType) {
            case 0:
                effect = "美元|金银 石油|";
                break;
            case 1:
                effect = "金银 石油|美元|";
                break;
            case 2:
                effect = "||";
                break;
        }


        RadianDrawable radianDrawable = new RadianDrawable(this);

        try {
            Float.parseFloat(reality.replace("%", ""));

            radianDrawable.setStroke(R.color.line_color);

            if (effectType != 2) {
                String[] effectSplit = effect.split("\\|");
                for (int i = 0; i < effectSplit.length; i++) {
                    drawingShapeColor(i, effectType, effectSplit[i], llExponent);
                }
            } else {
                drawingShapeColor(2, effectType, "影响较小", llExponent);
            }

        } catch (NumberFormatException e) {

            RadianDrawable effectDrawable = new RadianDrawable(this);

            TextView textView = generateTextView();
            textView.setText("未公布");

            effectDrawable.setStroke(R.color.line_color);
            textView.setTextColor(ContextCompat.getColor(this, R.color.line_color));
            textView.setBackground(effectDrawable);

            llExponent.addView(textView);
        }
    }

    private void drawingShapeColor(int type, int effectType, String effect, LinearLayout llExponent) {
        int shapeColor = 0;
        Drawable leftDrawable = null;

        switch (type) {
            case 0:
                shapeColor = R.color.calendar_line0;
                leftDrawable = ContextCompat.getDrawable(this, R.mipmap.icon_top_red);
                break;
            case 1:
                shapeColor = R.color.calendar_line1;
                leftDrawable = ContextCompat.getDrawable(this, R.mipmap.icon_decline_green);
                break;
            case 2:
                shapeColor = R.color.calendar_line2;
                leftDrawable = null;
                break;
        }

        if (effectType != 2) {
            if (effect != null && !"".equals(effect.trim())) {
                String[] effect0Split = effect.split(" ");

                for (int i = 0; i < effect0Split.length; i++) {
                    RadianDrawable effectDrawable = new RadianDrawable(this);

                    TextView textView = generateTextView();
                    textView.setText(effect0Split[i]);

                    effectDrawable.setStroke(shapeColor);
                    textView.setTextColor(ContextCompat.getColor(this, shapeColor));

                    textView.setBackground(effectDrawable);

                    textView.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
                    llExponent.addView(textView);
                }
            }
        } else {
            RadianDrawable effectDrawable = new RadianDrawable(this);

            TextView textView = generateTextView();
            textView.setText(effect);

            effectDrawable.setStroke(shapeColor);
            textView.setTextColor(ContextCompat.getColor(this, shapeColor));
            textView.setBackground(effectDrawable);

            llExponent.addView(textView);
        }
    }

    private TextView generateTextView() {
        int padding = SystemUtil.dp2px(this, 4);
        int MarginsRight = SystemUtil.dp2px(this, 10);
        int minWidth = SystemUtil.dp2px(this, 50);

        TextView itemView = new TextView(this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        itemView.setLayoutParams(layoutParams);

        layoutParams.gravity = Gravity.CENTER;
        layoutParams.setMargins(0, 0, MarginsRight, 0);
        itemView.setPadding(padding, padding, padding, padding);
        itemView.setMinWidth(minWidth);

        itemView.setTextSize(11);
        itemView.setGravity(Gravity.CENTER);
        return itemView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getQueue().cancelAll(flashActivityPresenter.getClass().getName());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position > 1) {
            NewsJson newsJson = newsAdapter.getData().get(position - 2);
            JumpUtils.jump(this,newsJson.getO_class(),newsJson.getO_action(),newsJson.getO_id(),newsJson.getHref());
        }
    }

}
