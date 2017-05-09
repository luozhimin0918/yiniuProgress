package com.jyh.kxt.index.presenter;

import android.graphics.Bitmap;
import android.support.v4.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.util.emoje.EmoticonsUtils;
import com.jyh.kxt.index.ui.MainActivity;


public class MainPresenter extends BasePresenter {

    @BindObject MainActivity mMainActivity;

    public MainPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void initHeaderLayout() {
        String imgUrl = "http://img.kuaixun360.com//Uploads/Editor/2016-12-27/5861d5137548e.jpg";

        Glide.with(mContext)
                .load(imgUrl)
                .asBitmap()
                .override(50, 50)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        mMainActivity.loginPhoto.setImageBitmap(resource);
                    }
                });
    }

    /**
     * 切换Fragment
     *
     * @param toFragment
     */
    public void switchToFragment(BaseFragment toFragment) {
        BaseFragment currentFragment = mMainActivity.currentFragment;
        //当前用户调用显示

        if (toFragment != currentFragment) {
            MainActivity mainActivity = (MainActivity) mContext;
            FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();

            if (currentFragment == null) {
                if (!toFragment.isAdded()) {
                    transaction.add(R.id.center_content, toFragment);
                }
            } else {
                if (!toFragment.isAdded()) {
                    transaction.hide(currentFragment).add(R.id.center_content, toFragment);
                } else {
                    transaction.hide(currentFragment).show(toFragment);
                }
            }

            mMainActivity.currentFragment = toFragment;
            transaction.commitAllowingStateLoss();
        }
    }

    /**
     * 发送一个延迟请求, 放一些不重要的 但是必须要请求的网络信息
     */
    public void postDelayRequest() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);


                    EmoticonsUtils.initEmoticonsDB(mContext);
//                    for (String emoJeItem : result) {
//                        String[] emoJeItemSplit = emoJeItem
//                                .subSequence(1, emoJeItem.length() - 1)
//                                .toString()
//                                .split(",");
//                        String name = emoJeItemSplit[0];
//                        String url = emoJeItemSplit[1];
//                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }
}
