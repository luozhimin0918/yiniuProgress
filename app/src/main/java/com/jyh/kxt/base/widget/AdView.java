package com.jyh.kxt.base.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.json.AdItemJson;
import com.jyh.kxt.base.widget.night.heple.SkinnableTextView;
import com.jyh.kxt.datum.bean.AdJson;
import com.jyh.kxt.index.ui.WebActivity;
import com.library.util.RegexValidateUtil;
import com.library.util.SPUtils;
import com.library.util.SystemUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:kxt_android
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/11/14.
 */

public class AdView extends LinearLayout {

    @BindView(R.id.iv_ad) ImageView ivAd;
    @BindView(R.id.ll_ad) LinearLayout llAd;

    private Context mContext;

    private List<SkinnableTextView> adTvs = new ArrayList<>();
    private List<SkinnableTextView> adTvs2 = new ArrayList<>();
    private List<AdItemJson> textAds;

    public AdView(Context context) {
        this(context, null);
    }

    public AdView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {

    }

    public void setAd(final AdItemJson mPicAd, List<AdItemJson> mTextAd) {
        removeAllViews();
        textAds = mTextAd;
        LinearLayout adRoot = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.news_header_ad, this, false);
        ButterKnife.bind(this, adRoot);
        ivAd = (ImageView) adRoot.findViewById(R.id.iv_ad);
        llAd = (LinearLayout) adRoot.findViewById(R.id.ll_ad);
        try {
            if (mPicAd != null) {
                ivAd.getLayoutParams().height = SystemUtil.dp2px(mContext, mPicAd.getImageHeight());

                String picture = mPicAd.getPicture();
                if (RegexValidateUtil.isEmpty(picture)) {
                    ivAd.setVisibility(View.GONE);
                } else {
                    ivAd.setVisibility(View.VISIBLE);
                }
                Glide.with(mContext).load(picture).error(R.mipmap.icon_def_news)
                        .placeholder(R.mipmap.icon_def_news).into(ivAd);

                adRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, WebActivity.class);
                        intent.putExtra(IntentConstant.NAME, mPicAd.getTitle());
                        intent.putExtra(IntentConstant.WEBURL, mPicAd.getHref());
                        intent.putExtra(IntentConstant.AUTOOBTAINTITLE, true);
                        mContext.startActivity(intent);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (mTextAd != null && mTextAd.size() != 0) {
                LayoutInflater mInflater = LayoutInflater.from(mContext);
                adTvs.clear();
                adTvs2.clear();
                for (final AdItemJson adItemJson : mTextAd) {
                    View adLayoutView = mInflater.inflate(R.layout.item_news_ad, adRoot, false);

                    SkinnableTextView mAdTextView = (SkinnableTextView) adLayoutView.findViewById(R.id
                            .tv_news_ad_title);
                    mAdTextView.setText(" ● " + adItemJson.getTitle());


                    SkinnableTextView mAdTraitView = (SkinnableTextView) adLayoutView.findViewById(R.id
                            .tv_news_ad_trait);
                    adLayoutView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, WebActivity.class);
                            intent.putExtra(IntentConstant.NAME, adItemJson.getTitle());
                            intent.putExtra(IntentConstant.WEBURL, adItemJson.getHref());
                            mContext.startActivity(intent);
                        }
                    });
                    adTvs.add(mAdTextView);
                    adTvs2.add(mAdTraitView);
                    llAd.addView(adLayoutView);
                }
                onChangeTheme();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        addView(adRoot);
    }

    public void onChangeTheme() {
        try {
            boolean isNight = SPUtils.getBoolean(mContext, SpConstant.SETTING_DAY_NIGHT);
            int size = adTvs.size();
            for (int i = 0; i < size; i++) {
                AdItemJson adItemJson = textAds.get(i);
                adTvs.get(i).setTextColor(Color.parseColor(isNight ? adItemJson.getNight_color() : adItemJson.getDay_color()));
                adTvs2.get(i).setTextColor(ContextCompat.getColor(mContext, R.color.font_color5));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
