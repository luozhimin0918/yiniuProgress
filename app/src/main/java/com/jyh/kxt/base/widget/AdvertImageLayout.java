package com.jyh.kxt.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.main.json.SlideJson;
import com.library.util.SystemUtil;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/9/18.
 */

public class AdvertImageLayout extends LinearLayout {
    public AdvertImageLayout(Context context) {
        super(context);
        init();
    }

    public AdvertImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AdvertImageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
    }

    public void addAdvertViews(List<SlideJson> slideAdvertList) {
//        slideAdvertList = new ArrayList<>();
//
//        SlideJson slideJson = new SlideJson();
//        slideJson.setPicture("http://qtimg.bdstatic.com/hiapk/news/201709/18/59bf7c192c37e~91_autow600~.jpg");
//        slideJson.setHref("http://news.91.com/funny/s59bf832fcc1a.html");
//        slideJson.setTitle("标题");
//
//        slideAdvertList.add(slideJson);

        if (slideAdvertList != null && slideAdvertList.size() > 0) {
            for (final SlideJson ad : slideAdvertList) {

                ImageView ivAd = new ImageView(getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                params.setMargins(0, 0, 0, SystemUtil.dp2px(getContext(), 5));
                addView(ivAd, params);

                Glide.with(getContext()).load(ad.getPicture()).error(R.mipmap.icon_def_video).into(ivAd);
                ivAd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JumpUtils.jump((BaseActivity) getContext(),
                                ad.getO_class(),
                                ad.getO_action(),
                                ad.getO_id(),
                                ad.getHref());
                    }
                });
            }
        }

    }
}
