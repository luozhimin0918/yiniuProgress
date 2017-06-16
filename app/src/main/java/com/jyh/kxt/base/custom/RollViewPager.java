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
import android.widget.ListAdapter;

import com.jyh.kxt.R;
import com.jyh.kxt.market.adapter.MarketGridAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Mr'Dai on 2017/5/18.
 */

public class RollViewPager extends ViewPager {

    private ArrayList<View> groupViewList;

    public interface GridViewItemData {
        void itemData(List dataSubList, GridView gridView);
    }

    private GridViewItemData gridViewItemData;
    private ViewPagerAdapter viewPagerAdapter;
    private int mGridMaxCount;
    private List dataList;
    private boolean showPaddingLine = true;

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

    public void build() {
        int extendedCount = 0;
        if (dataList.size() % mGridMaxCount != 0) {
            extendedCount = 1;
        }

        int itemGroupViewPagerCount = (dataList.size() / mGridMaxCount) + extendedCount;

        //增加分割ViewPager
        groupViewList = new ArrayList<>();

        for (int i = 0; i < itemGroupViewPagerCount; i++) {
            int startCount = i * mGridMaxCount;
            int endCount = (i * mGridMaxCount + mGridMaxCount) > dataList.size() ? dataList.size() : (i *
                    mGridMaxCount + mGridMaxCount);

            List dataSubList = dataList.subList(startCount, endCount);
            if (gridViewItemData != null) {

                GridView gridView = getGridView();
                gridViewItemData.itemData(dataSubList, gridView);
                groupViewList.add(gridView);
            }
        }

        viewPagerAdapter = new ViewPagerAdapter(groupViewList);
        setAdapter(viewPagerAdapter);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                RollViewPager.this.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_btn_bg_color));
            }
        },500);

    }

    public GridView getGridView() {
        GridView itemPageGridView = new GridView(getContext());

        itemPageGridView.setMotionEventSplittingEnabled(false);
        itemPageGridView.setNumColumns(3);
        itemPageGridView.setBackgroundColor(Color.TRANSPARENT);
        itemPageGridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        itemPageGridView.setCacheColorHint(0);
        itemPageGridView.setHorizontalSpacing(showPaddingLine ? 1 : 0);
        itemPageGridView.setVerticalSpacing(showPaddingLine ? 1 : 0);
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
        private int mChildCount = 0;

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

        @Override
        public int getItemPosition(Object object) {
            if (mChildCount > 0) {
                mChildCount--;
                return POSITION_NONE;
            }
            return super.getItemPosition(object);
        }

        @Override
        public void notifyDataSetChanged() {
            mChildCount = getCount();
            super.notifyDataSetChanged();
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void onChangeTheme() {
        try {
            if (groupViewList != null) {
                for (View mItemView : groupViewList) {
                    GridView mGridView = (GridView) mItemView;
                    ListAdapter adapter = mGridView.getAdapter();
                    if (adapter instanceof MarketGridAdapter) {
                        MarketGridAdapter gridAdapter = (MarketGridAdapter) adapter;
                        gridAdapter.notifyDataSetChanged();
                        gridAdapter.updateLayoutColor();
                    }
                }
                viewPagerAdapter.notifyDataSetChanged();
                this.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_btn_bg_color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isShowPaddingLine() {
        return showPaddingLine;
    }

    public void setShowPaddingLine(boolean showPaddingLine) {
        this.showPaddingLine = showPaddingLine;
    }
}
