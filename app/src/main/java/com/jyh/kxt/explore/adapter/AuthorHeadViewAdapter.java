package com.jyh.kxt.explore.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:名家专栏头部布局Adapter
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/15.
 */

public class AuthorHeadViewAdapter extends PagerAdapter {

    private Context context;
    private List<View> views;

    public AuthorHeadViewAdapter(Context context, List<View> views) {
        this.context = context;
        this.views = views;
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
        View child = views.get(position);
        container.addView(child, 0);
        return child;
    }
}
