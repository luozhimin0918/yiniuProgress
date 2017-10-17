package com.jyh.kxt.base.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.json.AdItemJson;
import com.jyh.kxt.base.json.AdTitleIconBean;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.main.json.AdJson;
import com.library.util.SPUtils;
import com.library.util.SystemUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mr'Dai on 2017/9/12.
 */

public class AdvertLayout3 extends FrameLayout {

    @BindView(R.id.tv_ad) VerticalTextView tvAd;
    @BindView(R.id.iv_ad) ImageView ivAd;
    @BindView(R.id.iv_ad2) ImageView ivAd2;
    @BindView(R.id.ll_ad) LinearLayout llAd;
    private View advertRootTitle;
    private AdJson ad;

    public AdvertLayout3(Context context) {
        super(context);
        init();
    }

    public AdvertLayout3(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AdvertLayout3(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
    }


    public void setAdvertData(AdJson ad) {
        if (ad == null) {
            setVisibility(GONE);
            return;
        }
        this.ad = ad;
        LayoutInflater mInflater = LayoutInflater.from(getContext());
        advertRootTitle = mInflater.inflate(R.layout.view_advert3_content, this, false);
        ButterKnife.bind(this, advertRootTitle);
        addView(advertRootTitle);

        onChangerTheme();
    }

    public void onResume() {
        if (tvAd != null)
            tvAd.startAutoScroll();
    }

    public void onPause() {
        if (tvAd != null)
            tvAd.stopAutoScroll();
    }

    public void onChangerTheme() {
        Boolean isNight = SPUtils.getBoolean(getContext(), SpConstant.SETTING_DAY_NIGHT);
        advertRootTitle.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.theme1));

        if (ad.getText_ad() != null && ad.getText_ad().size() > 0) {
            final List<AdItemJson> text_ad = ad.getText_ad();
            ArrayList<String> text_ads = new ArrayList<>();
            for (AdItemJson adItemJson : text_ad) {
                String title = adItemJson.getTitle();
                if (title.length() > 25) {
                    title = title.substring(0, 25);
                }
                text_ads.add(title);
            }
            tvAd.setTextList(text_ads);
            tvAd.setText(16, 5, Color.RED);//设置属性,具体跟踪源码
            tvAd.setTextStillTime(3000);//设置停留时长间隔
            tvAd.setAnimTime(300);//设置进入和退出的时间间隔
            // 对单条文字的点击监听
            tvAd.setOnItemClickListener(new VerticalTextView.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    AdItemJson adItemJson = text_ad.get(position);
                    JumpUtils.jump((BaseActivity) getContext(), adItemJson.getO_class(), adItemJson.getO_action(), adItemJson.getO_id(),
                            adItemJson
                                    .getHref());
                }
            });
            tvAd.setOnScrollListener(new VerticalTextView.OnScrollListener() {
                @Override
                public void onScroll(int position) {
                    AdItemJson adItemJson = text_ad.get(position);
                    Boolean isNight = SPUtils.getBoolean(getContext(), SpConstant.SETTING_DAY_NIGHT);
                    String colorString = isNight ? adItemJson.getNight_color() : adItemJson.getDay_color();
                    if (colorString != null) {
                        String font_size = adItemJson.getFont_size();
                        float fontSize = 16;
                        if (font_size != null) {
                            try {
                                fontSize = SystemUtil.px2dp(getContext(), Integer.parseInt(font_size.replace("px", "")));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                        int textColor = Color.parseColor(colorString);
                        tvAd.setText(fontSize, 5, textColor);
                        AdTitleIconBean icon = adItemJson.getIcon();
                        Glide.with(getContext()).load(isNight ? icon.getNight_icon() : icon.getDay_icon()).placeholder(R.mipmap.icon_ad2)
                                .into(ivAd);
                    }
                }
            });
            AdTitleIconBean icon = ad.getText_ad().get(0).getIcon();
            Glide.with(getContext()).load(isNight ? icon.getNight_icon() : icon.getDay_icon()).placeholder(R.mipmap.icon_ad2)
                    .into(ivAd);
            if (text_ads.size() > 1)
                tvAd.startAutoScroll();
            llAd.setVisibility(VISIBLE);
        } else {
            llAd.setVisibility(GONE);
        }

        if (ad.getPic_ad() != null) {
            final AdItemJson pic_ad = ad.getPic_ad();
//            int imageHeight = pic_ad.getImageHeight();
//            ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, imageHeight);
//            ivAd2.setLayoutParams(layoutParams);
            Glide.with(getContext()).load(pic_ad.getPicture()).into(ivAd2);
            ivAd2.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    JumpUtils.jump((BaseActivity) getContext(), pic_ad.getO_class(), pic_ad.getO_action(), pic_ad.getO_id(), pic_ad
                            .getHref());
                }
            });
            ivAd2.setVisibility(VISIBLE);
        } else {
            ivAd2.setVisibility(GONE);
        }


    }
}
