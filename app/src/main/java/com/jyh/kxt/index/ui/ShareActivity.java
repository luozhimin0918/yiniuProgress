package com.jyh.kxt.index.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.json.UmengShareBean;
import com.jyh.kxt.base.utils.UmengShareUI;
import com.jyh.kxt.base.utils.UmengShareUtil;
import com.library.util.NetUtils;
import com.library.widget.window.ToastView;
import com.umeng.socialize.bean.SHARE_MEDIA;

import butterknife.BindView;
import butterknife.OnClick;

import static com.jyh.kxt.base.utils.UmengShareUtil.SHARE_INVITE;

/**
 * 项目名:Kxt
 * 类描述:分享
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/4.
 */

public class ShareActivity extends BaseActivity {
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;

    private UmengShareUtil umengShareUtil;
    private UmengShareBean umengShareBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index_share, StatusBarColor.THEME1);

        tvBarTitle.setText("推荐好友");
    }

    @OnClick({R.id.iv_bar_break, R.id.rl_wx, R.id.rl_pyq, R.id.rl_sina, R.id.rl_qq})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
        }

        umengShareBean = new UmengShareBean();
        umengShareBean.setTitle("一牛财经");
        umengShareBean.setDetail("财经快讯速递专家");
        umengShareBean.setWebUrl(HttpConstant.APP_DOWNLOAD);
        umengShareBean.setFromSource(SHARE_INVITE);

        if (umengShareUtil == null) {
            umengShareUtil = new UmengShareUtil(this, umengShareBean);
        }


        if (NetUtils.isNetworkAvailable(this)) {
            switch (view.getId()) {
                case R.id.rl_wx:
                    umengShareUtil.shareContent1(SHARE_MEDIA.WEIXIN, umengShareBean);
                    break;
                case R.id.rl_pyq:
                    umengShareUtil.shareContent1(SHARE_MEDIA.WEIXIN_CIRCLE, umengShareBean);
                    break;
                case R.id.rl_sina:
                    umengShareBean.setDetail("财经快讯速递专家 " + HttpConstant.OFFICIAL + " @一牛财经");
                    umengShareUtil.shareContent2(SHARE_MEDIA.SINA, umengShareBean);
                    break;
                case R.id.rl_qq:
                    umengShareUtil.shareContent1(SHARE_MEDIA.QQ, umengShareBean);
                    break;
            }
        } else {
            ToastView.makeText3(this, "暂无网络,请稍后再试");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UmengShareUI.onActivityResult(this, requestCode, resultCode, data);
    }
}
