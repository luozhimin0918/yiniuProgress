package com.jyh.kxt.index.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.UmengLoginTool;
import com.jyh.kxt.base.utils.UmengShareTool;
import com.library.util.NetUtils;
import com.library.widget.window.ToastView;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:分享
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/4.
 */

public class ShareActivity extends BaseActivity {
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index_share, StatusBarColor.THEME1);

        tvBarTitle.setText("推荐好友");
    }

    @OnClick({R.id.iv_bar_break, R.id.rl_wx, R.id.rl_pyq, R.id.rl_sina, R.id.rl_qq})
    public void onClick(View view) {
        if (NetUtils.isNetworkAvailable(this)) {
            switch (view.getId()) {
                case R.id.iv_bar_break:
                    onBackPressed();
                    break;
                case R.id.rl_wx:
                    if (UMShareAPI.get(this).isInstall(this, SHARE_MEDIA.WEIXIN))
                        UmengShareTool.setShareContent(this, "快讯通财经", HttpConstant.OFFICIAL, "财经快讯速递专家", "", SHARE_MEDIA.WEIXIN);
                    else
                        ToastView.makeText3(getContext(), "未安装微信");
                    break;
                case R.id.rl_pyq:
                    if (UMShareAPI.get(this).isInstall(this, SHARE_MEDIA.WEIXIN))
                        UmengShareTool.setShareContent(this, "快讯通财经", HttpConstant.OFFICIAL, "财经快讯速递专家", "", SHARE_MEDIA.WEIXIN_CIRCLE);
                    else
                        ToastView.makeText3(getContext(), "未安装微信");
                    break;
                case R.id.rl_sina:
                    UmengShareTool.setShareContent(this, "快讯通财经", HttpConstant.OFFICIAL, "财经快讯速递专家", "", SHARE_MEDIA.SINA);
                    break;
                case R.id.rl_qq:
                    if (UMShareAPI.get(this).isInstall(this, SHARE_MEDIA.QQ))
                        UmengShareTool.setShareContent(this, "快讯通财经", HttpConstant.OFFICIAL, "财经快讯速递专家", "", SHARE_MEDIA.QQ);
                    else
                        ToastView.makeText3(getContext(), "未安装QQ");
                    break;
            }
        } else {
            ToastView.makeText3(this, "暂无网络,请稍后再试");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UmengShareTool.onActivityResult(this, requestCode, resultCode, data);
    }
}
