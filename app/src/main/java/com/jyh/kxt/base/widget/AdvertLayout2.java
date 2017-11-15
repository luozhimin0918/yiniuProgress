package com.jyh.kxt.base.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
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

public class AdvertLayout2 extends FrameLayout {

    @BindView(R.id.layout_adLeft) FrameLayout flAdvertContent1;
    @BindView(R.id.layout_adRight) FrameLayout flAdvertContent2;

    private View advertRootTitle;
    private AdTitleIconBean adTitleIconBean;
    private List<AdTitleItemBean> adTitleItemList;

    public AdvertLayout2(Context context) {
        super(context);
        init();
    }

    public AdvertLayout2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AdvertLayout2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
    }


    public void setAdvertData(List<AdTitleItemBean> adTitleItemList, AdTitleIconBean adTitleIconBean) {
        LayoutInflater mInflater = LayoutInflater.from(getContext());
        advertRootTitle = mInflater.inflate(R.layout.view_advert2_content, this, false);
        ButterKnife.bind(this, advertRootTitle);
        addView(advertRootTitle);

        this.adTitleIconBean = adTitleIconBean;
        this.adTitleItemList = adTitleItemList;

        if (adTitleItemList == null || adTitleItemList.size() == 0) {
            flAdvertContent1.setVisibility(GONE);
            flAdvertContent2.setVisibility(GONE);
            return;
        }
        flAdvertContent1.setVisibility(View.VISIBLE);
        flAdvertContent2.setVisibility(View.VISIBLE);

        onChangerTheme();
    }

    public void onChangerTheme() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        Boolean isNight = SPUtils.getBoolean(getContext(), SpConstant.SETTING_DAY_NIGHT);

        advertRootTitle.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.theme1));

        if (adTitleItemList == null || adTitleItemList.size() == 0) {
            flAdvertContent1.setVisibility(GONE);
            flAdvertContent2.setVisibility(GONE);
            return;
        }

        //处理文本
        for (final AdTitleItemBean adTitleItemBean : adTitleItemList) {
            View view = inflater.inflate(R.layout.layout_ad_item, null);
            String textColor;
            //对右上角图片进行处理
            String iconUrl = null;
            if (isNight) {
                textColor = adTitleItemBean.getNight_color();
                if (adTitleIconBean != null)
                    iconUrl = adTitleIconBean.getNight_icon();
            } else {
                textColor = adTitleItemBean.getDay_color();
                if (adTitleIconBean != null)
                    iconUrl = adTitleIconBean.getDay_icon();
            }

            TextView tvContent = (TextView) view.findViewById(R.id.tv_ad);
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_ad);
            Glide.with(getContext()).load(iconUrl).placeholder(R.mipmap.icon_ad2).error(R.mipmap.icon_ad2).into(imageView);
            //设置颜色
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

            try {
                int fontColor = Color.parseColor(textColor);
                tvContent.setTextColor(fontColor);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (adTitleItemBean.getPosition() == 1) {
                flAdvertContent1.removeAllViews();
                flAdvertContent1.addView(view);
            } else {
                flAdvertContent2.removeAllViews();
                flAdvertContent2.addView(view);
            }
        }
    }
}
