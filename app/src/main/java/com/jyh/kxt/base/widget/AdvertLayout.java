package com.jyh.kxt.base.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.json.AdTitleIconBean;
import com.jyh.kxt.base.json.AdTitleItemBean;
import com.jyh.kxt.base.utils.JumpUtils;
import com.library.util.SPUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mr'Dai on 2017/9/12.
 */

public class AdvertLayout extends FrameLayout {

    @BindView(R.id.tv_advert_title) TextView tvAdvertTitle;
    @BindView(R.id.iv_advert_icon) ImageView ivAdvertIcon;

    @BindView(R.id.fl_advert_content1) FrameLayout flAdvertContent1;
    @BindView(R.id.fl_advert_content2) FrameLayout flAdvertContent2;

    @BindView(R.id.ll_advert_content) LinearLayout llAdvertContent;

    private View advertRootTitle;
    private AdTitleIconBean adTitleIconBean;
    private List<AdTitleItemBean> adTitleItemList;

    public AdvertLayout(Context context) {
        super(context);
        init();
    }

    public AdvertLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AdvertLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
    }


    public void setAdvertData(String title, List<AdTitleItemBean> adTitleItemList, AdTitleIconBean adTitleIconBean) {

        LayoutInflater mInflater = LayoutInflater.from(getContext());
        advertRootTitle = mInflater.inflate(R.layout.view_advert_title, this, false);
        ButterKnife.bind(this, advertRootTitle);
        addView(advertRootTitle);


        this.adTitleIconBean = adTitleIconBean;
        this.adTitleItemList = adTitleItemList;

        tvAdvertTitle.setText(title);
        tvAdvertTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.font_color60));

        if (adTitleItemList == null || adTitleItemList.size() == 0) {
            ivAdvertIcon.setVisibility(View.GONE);
            llAdvertContent.setVisibility(View.GONE);
            return;
        }
        ivAdvertIcon.setVisibility(View.VISIBLE);
        llAdvertContent.setVisibility(View.VISIBLE);

        onChangerTheme();
    }

    public void onChangerTheme() {
        LayoutInflater mInflater = LayoutInflater.from(getContext());
        Boolean isNight = SPUtils.getBoolean(getContext(), SpConstant.SETTING_DAY_NIGHT);

        advertRootTitle.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.theme1));
        tvAdvertTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.font_color60));

        if (adTitleItemList == null || adTitleItemList.size() == 0) {
            ivAdvertIcon.setVisibility(View.GONE);
            llAdvertContent.setVisibility(View.GONE);
            return;
        }
        //对右上角图片进行处理
        String iconUrl = null;
        try {
            iconUrl = isNight ? adTitleIconBean.getNight_icon() : adTitleIconBean.getDay_icon();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Glide.with(getContext()).load(iconUrl).into(ivAdvertIcon);

        //处理文本
        for (final AdTitleItemBean adTitleItemBean : adTitleItemList) {

            String textColor;
            if (isNight) {
                textColor = adTitleItemBean.getNight_color();
            } else {
                textColor = adTitleItemBean.getDay_color();
            }

            //设置颜色
            TextView tvContent = (TextView) mInflater.inflate(R.layout.view_advert_item_content, flAdvertContent1, false);
            tvContent.setText(adTitleItemBean.getTitle());

            tvContent.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = getContext();
                    JumpUtils.jump((BaseActivity) context,
                            adTitleItemBean.getO_class(),
                            adTitleItemBean.getO_action(),
                            adTitleItemBean.getO_id(),
                            adTitleItemBean.getHref());
                }
            });

            if (textColor != null) {
                int fontColor = Color.parseColor(textColor);
                tvContent.setTextColor(fontColor);
            }

            if (adTitleItemBean.getPosition() == 1) {
                flAdvertContent1.removeAllViews();
                flAdvertContent1.addView(tvContent);
            } else {
                flAdvertContent2.removeAllViews();
                flAdvertContent2.addView(tvContent);
            }
        }
    }
}
