package com.jyh.kxt.base.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.jyh.kxt.R;

/**
 * Created by Mr'Dai on 2017/5/18.
 */

public class RollDotViewPager extends FrameLayout implements ViewPager.OnPageChangeListener {
    private RollViewPager rollViewPager;
    private RollDotView rollDotView;

    public RollDotViewPager(Context context) {
        this(context, null);
    }

    public RollDotViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RollDotViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View rollLayoutDotView = LayoutInflater.from(getContext()).inflate(R.layout.view_roll_dot, this, false);
        rollViewPager = (RollViewPager) rollLayoutDotView.findViewById(R.id.rdvp_content);
        rollDotView = (RollDotView) rollLayoutDotView.findViewById(R.id.rdv_dot);

        addView(rollLayoutDotView);

        rollViewPager.addOnPageChangeListener(this);
    }

    public RollViewPager getRollViewPager() {
        return rollViewPager;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        rollDotView.setSelectedPosition(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void build() {
        rollViewPager.build();
        int count = rollViewPager.getAdapter().getCount();
        rollDotView.setCircleCount(count);
    }

    public void onChangeTheme() {
        rollDotView.onChangeTheme();
        rollViewPager.onChangeTheme();
    }
}
