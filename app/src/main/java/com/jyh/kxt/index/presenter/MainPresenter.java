package com.jyh.kxt.index.presenter;

import android.graphics.Bitmap;
import android.support.v4.app.FragmentTransaction;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindActivity;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.index.json.CjrlJson;
import com.jyh.kxt.index.ui.MainActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.widget.PageLoadLayout;

import java.util.HashMap;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;


public class MainPresenter extends BasePresenter {

    @BindActivity MainActivity mMainActivity;

    public MainPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void initHeaderLayout() {
        String imgUrl = "http://img2.imgtn.bdimg.com/it/u=3699270011,3265098417&fm=214&gp=0.jpg";

        Glide.with(mContext)
                .load(imgUrl)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        mMainActivity.rivAvatar.setImageBitmap(resource);
                    }
                });

        Glide.with(mContext)
                .load(imgUrl)
                .bitmapTransform(new BlurTransformation(mContext, 10, 10))//高斯模糊
                .into(mMainActivity.ivBlurAvatar);
    }

    /**
     * 切换Fragment
     * @param toFragment
     */
    public void switchToFragment(BaseFragment toFragment) {
        BaseFragment currentFragment = mMainActivity.currentFragment;
        //当前用户调用显示

        if (toFragment != currentFragment) {
            MainActivity mainActivity = (MainActivity) mContext;
            FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();

            if (toFragment == null) {
                if (!currentFragment.isAdded()) {
                    transaction.add(R.id.center_content, currentFragment);
                }
            } else {
                if (!toFragment.isAdded()) {
                    transaction.hide(currentFragment).add(R.id.center_content, toFragment);
                } else {
                    transaction.hide(currentFragment).show(toFragment);
                }
                mMainActivity.currentFragment = toFragment;
            }
            transaction.commitAllowingStateLoss();
        }
    }

    public void requestTipInfo(final PageLoadLayout pllLoad) {
        pllLoad.loadWait(PageLoadLayout.BgColor.TRANSPARENT8, null);

        HashMap<String, String> map = new HashMap<>();
        map.put("date", "2016-12-23");

        VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);
        volleyRequest.doGet(HttpConstant.CJRL, map, new HttpListener<List<CjrlJson>>() {
            @Override
            protected void onResponse(List<CjrlJson> result) {
                pllLoad.loadOver();
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                pllLoad.loadError();
            }
        });
    }

}
