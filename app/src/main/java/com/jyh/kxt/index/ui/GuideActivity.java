package com.jyh.kxt.index.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.custom.RollDotView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class GuideActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    @BindView(R.id.vp_main) public ViewPager mViewPager;
    @BindView(R.id.rdv_dot) public RollDotView mRollDotView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide, StatusBarColor.NO_COLOR);

        ArrayList<View> mViewList = new ArrayList<>();

        LayoutInflater lf = LayoutInflater.from(this);
        View view1 = lf.inflate(R.layout.item_guide_img, null);
        View view2 = lf.inflate(R.layout.item_guide_img, null);
        View view3 = lf.inflate(R.layout.item_guide_img, null);
        View viewEnd = lf.inflate(R.layout.item_guide_img_finish, null);
        viewEnd.findViewById(R.id.btn_guide_finish).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(GuideActivity.this, MainActivity.class);
                        startActivity(intent);
                        GuideActivity.this.finish();
                    }
                });

        mViewList.add(view1);
        mViewList.add(view2);
        mViewList.add(view3);
        mViewList.add(viewEnd);

        handleFillPicture(mViewList);


        mRollDotView.setCircleCount(mViewList.size());
        mRollDotView.setCircleSize(3);

        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(mViewList);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOverScrollMode(ViewPager.OVER_SCROLL_NEVER);//去掉光晕

        mViewPager.addOnPageChangeListener(this);
    }

    /**
     * 开始填充图片
     *
     * @param mViewList
     */
    private void handleFillPicture(ArrayList<View> mViewList) {
        ArrayList<Integer> mPictureImg = new ArrayList<>();
        mPictureImg.add(R.mipmap.icon_lauch);
        mPictureImg.add(R.mipmap.icon_lauch);
        mPictureImg.add(R.mipmap.icon_lauch);
        mPictureImg.add(R.mipmap.icon_lauch);

        for (int i = 0; i < mViewList.size(); i++) {
            View mItemView = mViewList.get(i);
            ImageView itemImage = (ImageView) mItemView.findViewById(R.id.iv_guide_desc);

            Integer mItemImageRes = mPictureImg.get(i);
            itemImage.setImageResource(mItemImageRes);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mRollDotView.setSelectedPosition(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class ViewPagerAdapter extends PagerAdapter {
        private List<View> mViewList;

        ViewPagerAdapter(List<View> mViewList) {
            this.mViewList = mViewList;
        }

        @Override
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
