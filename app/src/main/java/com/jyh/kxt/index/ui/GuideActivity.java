package com.jyh.kxt.index.ui;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class GuideActivity extends BaseActivity {
    @BindView(R.id.vp_main) public ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide, StatusBarColor.NO_COLOR);


        ArrayList<View> mViewList = new ArrayList<>();

        LayoutInflater lf = LayoutInflater.from(this);
        View view1 = lf.inflate(R.layout.item_guide_img, null);
        View view2 = lf.inflate(R.layout.item_guide_img, null);
        View view3 = lf.inflate(R.layout.item_guide_img_finish, null);

        mViewList.add(view1);
        mViewList.add(view2);
        mViewList.add(view3);

        handleFillPicture(mViewList);

        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(mViewList);
        mViewPager.setAdapter(mViewPagerAdapter);
    }

    /**
     * 开始填充图片
     *
     * @param mViewList
     */
    private void handleFillPicture(ArrayList<View> mViewList) {
        ArrayList<Integer> mPictureImg = new ArrayList<>();
        mPictureImg.add(R.mipmap.icon_star1);
        mPictureImg.add(R.mipmap.icon_star2);
        mPictureImg.add(R.mipmap.icon_star3);

        for (int i = 0; i < mViewList.size(); i++) {
            View mItemView = mViewList.get(i);
            ImageView itemImage = (ImageView) mItemView.findViewById(R.id.iv_guide_desc);

            Integer mItemImageRes = mPictureImg.get(i);
            itemImage.setImageResource(mItemImageRes);
        }
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
}
