package com.jyh.kxt.base.util.emoje;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jyh.kxt.R;
import com.jyh.kxt.base.dao.DBManager;
import com.jyh.kxt.base.dao.DaoSession;
import com.jyh.kxt.base.dao.EmojeBean;
import com.library.util.SystemUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/5/5.
 */

public class EmoticonViewPager extends ViewPager {
    private int mHorizontalSpacing;
    private int mVerticalSpacing;
    private int gridViewPadding;

    private EmoticonLinearLayout emoticonLinearLayout;

    private List<ViewPager> viewPagerGroupList = new ArrayList<>();
    private List<View> viewPagerList = new ArrayList<>();

    public EmoticonViewPager(Context context) {
        this(context, null);
    }

    public EmoticonViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initViewPager(List<String> mGroupNameList) {

        emoticonLinearLayout = (EmoticonLinearLayout) getParent();

        gridViewPadding = SystemUtil.dp2px(getContext(), 10);

        DaoSession daoSessionRead = DBManager.getInstance(getContext()).getDaoSessionRead();

        for (String groupName : mGroupNameList) {
            int maxItemCount;
            if (roleJudge(groupName) == 1) {
                maxItemCount = 9;
            } else {
                maxItemCount = 20;
            }


            List<EmojeBean> emoJeBeen = daoSessionRead
                    .getEmojeBeanDao()
                    .queryRaw("WHERE GROUP_CHINESE_NAME = ?", groupName);

            int extendedCount = 0;
            if (emoJeBeen.size() % maxItemCount != 0) {
                extendedCount = 1;
            }
            int itemGroupViewPagerCount = (emoJeBeen.size() / maxItemCount) + extendedCount;

            //增加分割ViewPager
            final List<View> groupViewList = new ArrayList<>();

            for (int i = 0; i < itemGroupViewPagerCount; i++) {
                int startCount = i * maxItemCount;
                int endCount = (i * maxItemCount + maxItemCount) > emoJeBeen.size() ? emoJeBeen.size() : (i *
                        maxItemCount + maxItemCount);

                List<EmojeBean> emoJeSubBeen = emoJeBeen.subList(startCount, endCount);
                RelativeLayout relativeLayout = generateGridView(groupName, emoJeSubBeen);
                groupViewList.add(relativeLayout);

            }

            EmoticonsViewPagerAdapter groupItemAdapter = new EmoticonsViewPagerAdapter(groupViewList);
            ViewPager groupViewPager = new ViewPager(getContext());

            ViewGroup.LayoutParams groupViewPagerParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            groupViewPager.setLayoutParams(groupViewPagerParams);
            groupViewPager.setAdapter(groupItemAdapter);
            viewPagerGroupList.add(groupViewPager);

            viewPagerList.add(groupViewPager);
            groupViewPager.addOnPageChangeListener(new OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    emoticonLinearLayout.onGroupItemPageChangeListener(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

        EmoticonsViewPagerAdapter adapter = new EmoticonsViewPagerAdapter(viewPagerList);
        setAdapter(adapter);

        OnPageChangeListener listener = new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                emoticonLinearLayout.onGroupPageChangeListener(position, viewPagerGroupList);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };

        listener.onPageSelected(0);

        addOnPageChangeListener(listener);
    }


    private RelativeLayout generateGridView(String groupName, List<EmojeBean> emoJeSubBeen) {
        int numColumns;
        int itemSize;

        if (roleJudge(groupName) == 1) {
            numColumns = 5;
            itemSize = SystemUtil.dp2px(getContext(), 70);
            mHorizontalSpacing = SystemUtil.dp2px(getContext(), 0);
            mVerticalSpacing = SystemUtil.dp2px(getContext(), 0);
        } else {
            numColumns = 7;
            itemSize = SystemUtil.dp2px(getContext(), 40);
            mHorizontalSpacing = SystemUtil.dp2px(getContext(), 10);
            mVerticalSpacing = SystemUtil.dp2px(getContext(), 12);
        }


        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        //GridView 增加
        GridView itemPageGridView = new GridView(getContext());

        itemPageGridView.setMotionEventSplittingEnabled(false);
        itemPageGridView.setNumColumns(numColumns);
        itemPageGridView.setBackgroundColor(Color.TRANSPARENT);
        itemPageGridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        itemPageGridView.setCacheColorHint(0);
        itemPageGridView.setHorizontalSpacing(mHorizontalSpacing);
        itemPageGridView.setVerticalSpacing(mVerticalSpacing);
        itemPageGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        itemPageGridView.setGravity(Gravity.CENTER);
        itemPageGridView.setVerticalScrollBarEnabled(false);

        EmoticonGridAdapter mEmoticonGridAdapter = new EmoticonGridAdapter(getContext(), itemSize, emoJeSubBeen, this);
        itemPageGridView.setAdapter(mEmoticonGridAdapter);

        RelativeLayout.LayoutParams gridParentLayout = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        relativeLayout.addView(itemPageGridView, gridParentLayout);

        //右下角删除
        ImageView delImageView = new ImageView(getContext());
        delImageView.setImageResource(R.drawable.icon_del);
        int paddingPx = SystemUtil.dp2px(getContext(), 8);
        delImageView.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
        RelativeLayout.LayoutParams delParentLayout = new RelativeLayout.LayoutParams(
                SystemUtil.dp2px(getContext(), 45),
                SystemUtil.dp2px(getContext(), 40));

        delParentLayout.setMargins(0, 0, 0, SystemUtil.dp2px(getContext(), 5));
        delImageView.setBackgroundResource(R.drawable.iv_face);

        delParentLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        delParentLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);

        relativeLayout.addView(delImageView, delParentLayout);
        delImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                emoticonLinearLayout.deleteEmoJeClick();
            }
        });

        //添加布局到PageView 中
        relativeLayout.setPadding(gridViewPadding, gridViewPadding, gridViewPadding, gridViewPadding);

        return relativeLayout;
//        setAdapter(groupItemAdapter);
    }

    public void itemEmoJeClick(EmojeBean emojeBean) {
        emoticonLinearLayout.itemEmoJeClick(emojeBean);
    }


    private int roleJudge(String groupName) {
        if ("可可".equals(groupName) || "米亚".equals(groupName) || "茉晗".equals(groupName)) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * ViewPager 滑动的Adapter
     */
    private class EmoticonsViewPagerAdapter extends PagerAdapter {
        private List<View> viewPagers;

        public EmoticonsViewPagerAdapter(List<View> viewPagers) {
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

}
