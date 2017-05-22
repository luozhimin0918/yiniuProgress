package com.jyh.kxt.base.custom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.GridView;

import com.jyh.kxt.R;
import com.library.util.LogUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Mr'Dai on 2017/5/18.
 */

public class RollViewPager extends ViewPager {

    public interface GridViewItemData {
        void itemData(List dataSubList, GridView gridView);
    }

    private GridViewItemData gridViewItemData;
    private int mGridMaxCount;
    private List dataList;

    public RollViewPager(Context context) {
        this(context, null);
    }

    public RollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public RollViewPager setDataList(List dataList) {
        this.dataList = dataList;
        return this;
    }

    public RollViewPager setGridMaxCount(int mGridMaxCount) {
        this.mGridMaxCount = mGridMaxCount;
        return this;
    }

    public void setGridViewItemData(GridViewItemData gridViewItemData) {
        this.gridViewItemData = gridViewItemData;
    }

    public  void build() {
        int extendedCount = 0;
        if (dataList.size() % mGridMaxCount != 0) {
            extendedCount = 1;
        }

        int itemGroupViewPagerCount = (dataList.size() / mGridMaxCount) + extendedCount;

        //增加分割ViewPager
        List<View> groupViewList = new ArrayList<>();

        for (int i = 0; i < itemGroupViewPagerCount; i++) {
            int startCount = i * mGridMaxCount;
            int endCount = (i * mGridMaxCount + mGridMaxCount) > dataList.size() ?  dataList.size() : (i * mGridMaxCount + mGridMaxCount);

            List dataSubList = dataList.subList(startCount, endCount);
            if (gridViewItemData != null) {

                GridView gridView = getGridView();
                gridViewItemData.itemData(dataSubList, gridView);
                groupViewList.add(gridView);
            }
        }

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(groupViewList);
        setAdapter(viewPagerAdapter);
    }

    public GridView getGridView() {

        int bgColor = ContextCompat.getColor(getContext(), R.color.gray_btn_bg_color);

        GridView itemPageGridView = new GridView(getContext());
        itemPageGridView.setMotionEventSplittingEnabled(false);
        itemPageGridView.setNumColumns(3);
        itemPageGridView.setBackgroundColor(bgColor);
        itemPageGridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        itemPageGridView.setCacheColorHint(0);
        itemPageGridView.setHorizontalSpacing(1);
        itemPageGridView.setVerticalSpacing(1);
        itemPageGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        itemPageGridView.setGravity(Gravity.CENTER);
        itemPageGridView.setVerticalScrollBarEnabled(false);

        return itemPageGridView;
    }

    /**
     * ViewPager 滑动的Adapter
     */
    private class ViewPagerAdapter extends PagerAdapter {
        private List<View> viewPagers;

        public ViewPagerAdapter(List<View> viewPagers) {
            this.viewPagers = viewPagers;
        }

        @Override
        public int getCount() {
            return viewPagers.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(viewPagers.get(arg1));
            return viewPagers.get(arg1);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        LogUtil.e(LogUtil.TAG, "宽度"+getWidth()+"gaodu :"+getHeight());
    }
}