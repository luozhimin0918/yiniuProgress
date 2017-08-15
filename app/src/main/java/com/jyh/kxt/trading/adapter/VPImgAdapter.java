package com.jyh.kxt.trading.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/15.
 */

public class VPImgAdapter extends PagerAdapter {

    private List<String> urls;
    private Context mContext;
    private List<View> views = new ArrayList<>();
    private View.OnClickListener onClickLinstener;

    public VPImgAdapter(List<String> urls, Context mContext) {
        this.urls = urls;
        this.mContext = mContext;
        LayoutInflater from = LayoutInflater.from(mContext);
        for (String url : urls) {
            views.add(from.inflate(R.layout.pop_img, null, false));
        }
    }

    @Override
    public int getCount() {
        return views == null ? 0 : views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        container.removeView(views.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = views.get(position);
        final ImageView ivPop = (ImageView) view.findViewById(R.id.iv_pop);
        ivPop.setOnClickListener(onClickLinstener);
        Glide.with(mContext).load(urls.get(position))
                .asBitmap()
                .error(R.mipmap.icon_def_news)
                .placeholder(R.mipmap.icon_def_news)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        ivPop.setImageBitmap(resource);
                    }
                });
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams .MATCH_PARENT);
        container.addView(view, layoutParams);
        return view;
    }

    public void setOnClickLinstener(View.OnClickListener onClickLinstener) {
        this.onClickLinstener = onClickLinstener;
    }
}
