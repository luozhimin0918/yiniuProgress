package com.jyh.kxt.base.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.adapter.ShareWindowAdapter;
import com.jyh.kxt.base.json.ShareItemJson;
import com.jyh.kxt.base.json.UmengShareBean;
import com.jyh.kxt.base.util.PopupUtil;
import com.library.widget.window.ToastView;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.greendao.annotation.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/9/8.
 */

public class UmengShareUI {

    private PopupUtil mSharePopup;
    private ViewGroup mShareLayout;

    public UmengShareUI(BaseActivity activity) {
        this.mActivity = activity;
    }

    private BaseActivity mActivity;
    private OnPopupFunListener mOnPopupFunListener;

    private ShareWindowAdapter platformAdapter;
    private ShareWindowAdapter functionAdapter;

    private View customView;
    private View spaceLineView;

    private RecyclerView rvPlatform;
    private RecyclerView rvFunction;

    public void setOnPopupFunListener(OnPopupFunListener mOnPopupFunListener) {
        this.mOnPopupFunListener = mOnPopupFunListener;
    }

    /**
     * Umeng 分享 UI：1 上方显示QQ  下方显示数据,可以自由加入视图
     */
    public PopupUtil showSharePopup(@NotNull UmengShareBean umengShareBean) {
        return showSharePopup2(umengShareBean, null, null);
    }


    public PopupUtil showSharePopup(@NotNull UmengShareBean umengShareBean, List<ShareItemJson> shareItemList) {
        return showSharePopup2(umengShareBean, shareItemList, null);
    }

    public PopupUtil showSharePopup(@NotNull UmengShareBean umengShareBean, View customView) {
        return showSharePopup2(umengShareBean, null, customView);
    }

    /**
     * @param umengShareBean
     * @param shareItemList  如果不为空  则显示RecycleView
     * @param customView     如果不为空,则添加一个视图进入
     *                       二者选其一
     */
    private PopupUtil showSharePopup2(@NotNull final UmengShareBean umengShareBean, List<ShareItemJson> shareItemList, View customView) {
        this.customView = customView;

        if (mSharePopup != null && mSharePopup.isShowing()) {
            mSharePopup.dismiss();
        }

        mSharePopup = new PopupUtil(mActivity);
        mShareLayout = (ViewGroup) mSharePopup.createPopupView(R.layout.dialog_umeng_share);

        PopupUtil.Config config = new PopupUtil.Config();
        config.outsideTouchable = true;
        config.alpha = 0.5f;
        config.bgColor = 0X00000000;

        config.animationStyle = R.style.PopupWindow_Style2;
        config.width = WindowManager.LayoutParams.MATCH_PARENT;
        config.height = WindowManager.LayoutParams.WRAP_CONTENT;

        mSharePopup.setConfig(config);

        LinearLayout mShareContentLayout = (LinearLayout) mShareLayout.findViewById(R.id.ll_umeng_share_content);
        //添加自定义视图
        if (customView != null) {
            mShareContentLayout.addView(customView);
        }

        spaceLineView = mShareLayout.findViewById(R.id.v_line);

        LinearLayoutManager shareManager = new LinearLayoutManager(mActivity);
        shareManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        //初始化分享平台
        rvPlatform = (RecyclerView) mShareLayout.findViewById(R.id.rv_share);
        rvPlatform.setLayoutManager(shareManager);
        List<ShareItemJson> platformList = new ArrayList<>();
        platformList.add(new ShareItemJson(R.mipmap.icon_share_qyq, "朋友圈"));
        platformList.add(new ShareItemJson(R.mipmap.icon_share_wx, "微信好友"));
        platformList.add(new ShareItemJson(R.mipmap.icon_share_sina, "微博"));
        platformList.add(new ShareItemJson(R.mipmap.icon_share_qq, "QQ"));
        platformList.add(new ShareItemJson(R.mipmap.icon_share_zone, "QQ空间"));
        platformAdapter = new ShareWindowAdapter(platformList, mActivity);
        rvPlatform.setAdapter(platformAdapter);

        PlatformClickShare onPopupFunListener = new PlatformClickShare(umengShareBean);
        platformAdapter.setOnPopupFunListener(onPopupFunListener);

        //初始化功能按钮
        rvFunction = (RecyclerView) mShareLayout.findViewById(R.id.rv_function);
        if (shareItemList != null) {
            rvFunction.setVisibility(View.VISIBLE);

            LinearLayoutManager funManager = new LinearLayoutManager(mActivity);
            funManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            rvFunction.setLayoutManager(funManager);
            functionAdapter = new ShareWindowAdapter(shareItemList, mActivity);
            rvFunction.setAdapter(functionAdapter);
            functionAdapter.setOnPopupFunListener(new OnPopupFunListener() {
                @Override
                public void onClickItem(View itemView, ShareItemJson mShareItemJson, RecyclerView.Adapter recyclerAdapter) {
                    switch (mShareItemJson.tagId) {
                        case UmengShareUtil.FUN_COPY_URL:
                            copyUrlToClipboard(umengShareBean);
                            return;
                        case UmengShareUtil.FUN_CLOSE_POP:
                            mSharePopup.dismiss();
                            return;
                    }

                    if (mOnPopupFunListener != null) {
                        mOnPopupFunListener.onClickItem(itemView, mShareItemJson, recyclerAdapter);
                    }
                }
            });
        } else {
            rvFunction.setVisibility(View.GONE);
        }

        mSharePopup.showAtLocation(mShareLayout, Gravity.BOTTOM, 0, 0);
        return mSharePopup;
    }

    public View getCustomLayout(int layoutRes) {
        return LayoutInflater.from(mActivity).inflate(layoutRes, mShareLayout, false);
    }

    /**
     * 复制连接到剪切板
     */
    private void copyUrlToClipboard(UmengShareBean umengShareBean) {
        try {
            ClipboardManager clipboard = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("simple text", umengShareBean.getWebUrl());
            clipboard.setPrimaryClip(clip);
            ToastView.makeText3(mActivity, "链接复制成功");
        } catch (Exception e) {
            e.printStackTrace();
            ToastView.makeText3(mActivity, "复制链接失败，请重试");
        }
        try {
            mSharePopup.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 平台点击分享的类
     */
    private class PlatformClickShare implements OnPopupFunListener {
        UmengShareBean mUmengShareBean;
        UmengShareUtil mUmengShareUtil;

        PlatformClickShare(UmengShareBean mUmengShareBean) {
            this.mUmengShareBean = mUmengShareBean;
            this.mUmengShareUtil = new UmengShareUtil(mActivity, mSharePopup, mUmengShareBean);
        }

        @Override
        public void onClickItem(View view, ShareItemJson mShareItemJson, RecyclerView.Adapter recyclerAdapter) {

            if (mUmengShareBean == null || mUmengShareBean.getWebUrl() == null) {
                ToastView.makeText3(view.getContext(), "数据加载中,请稍后再试");
                return;
            }

            if (mSharePopup.isShowing()) {
                mSharePopup.dismiss();
            }

            int icon = mShareItemJson.icon;
            switch (icon) {
                case R.mipmap.icon_share_qyq:
                    mUmengShareUtil.shareContent1(SHARE_MEDIA.WEIXIN_CIRCLE, mUmengShareBean);
                    break;
                case R.mipmap.icon_share_wx:
                    mUmengShareUtil.shareContent1(SHARE_MEDIA.WEIXIN, mUmengShareBean);
                    break;
                case R.mipmap.icon_share_sina:
                    if (!TextUtils.isEmpty(mUmengShareBean.getSinaTitle())) {
                        mUmengShareBean.setDetail(mUmengShareBean.getSinaTitle());
                        mUmengShareUtil.shareContent2(SHARE_MEDIA.SINA, mUmengShareBean);
                    } else {
                        mUmengShareUtil.shareContent1(SHARE_MEDIA.SINA, mUmengShareBean);
                    }
                    break;
                case R.mipmap.icon_share_qq:
                    mUmengShareUtil.shareContent1(SHARE_MEDIA.QQ, mUmengShareBean);
                    break;
                case R.mipmap.icon_share_zone:
                    mUmengShareUtil.shareContent1(SHARE_MEDIA.QZONE, mUmengShareBean);
                    break;
            }
        }
    }

    public void onChangeTheme() {
        try {
            int themeBgColor = ContextCompat.getColor(mActivity, R.color.theme1);
            mShareLayout.setBackgroundColor(themeBgColor);

            if (spaceLineView != null) {
                spaceLineView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.line_color2));
            }

            if (customView != null) {
                customView.setBackgroundColor(themeBgColor);
            }
            if (rvPlatform != null) {
                rvPlatform.setBackgroundColor(themeBgColor);
                if (platformAdapter != null) {
                    platformAdapter.notifyDataSetChanged();
                }
            }

            if (rvFunction != null) {
                rvFunction.setBackgroundColor(themeBgColor);
                if (functionAdapter != null) {
                    functionAdapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        UMShareAPI.get(context).onActivityResult(requestCode, resultCode, data);
    }
}
