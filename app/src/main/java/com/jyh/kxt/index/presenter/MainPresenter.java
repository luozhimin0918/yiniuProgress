package com.jyh.kxt.index.presenter;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.util.emoje.EmoticonsUtils;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.index.ui.MainActivity;


public class MainPresenter extends BasePresenter {

    @BindObject MainActivity mMainActivity;
    private AlertDialog logoutDialog;

    public MainPresenter(IBaseView iBaseView) {
        super(iBaseView);
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

    /**
     * 退出弹窗
     */
    public void showQuitDialog() {
        if (logoutDialog == null)
            logoutDialog = new AlertDialog.Builder(mContext).setTitle("提醒").setMessage("确认退出当前账号?").setNegativeButton("确认", new
                    DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LoginUtils.logout(mContext);
                        }
                    }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create();
        if (logoutDialog.isShowing()) {
            logoutDialog.dismiss();
        }
        logoutDialog.show();
    }
}
