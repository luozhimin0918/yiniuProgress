package com.jyh.kxt.main.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.widget.LoadX5WebView;
import com.jyh.kxt.base.widget.LoadX5WebView2;
import com.jyh.kxt.main.json.NewsContentJson;
import com.jyh.kxt.main.presenter.NewsContentPresenter;
import com.library.widget.PageLoadLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:要闻详情
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/18.
 */

public class NewsContentActivity extends BaseActivity {

    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;
    @BindView(R.id.web_content) LoadX5WebView2 wv_content;
    @BindView(R.id.iv_break) ImageView ivBreak;
    @BindView(R.id.iv_comment) ImageView ivComment;
    @BindView(R.id.iv_collect) ImageView ivCollect;
    @BindView(R.id.iv_ding) ImageView ivDing;
    @BindView(R.id.iv_share) ImageView ivShare;
    @BindView(R.id.tv_title) TextView tvTitle;

    @BindView(R.id.ll_dp) RelativeLayout llDp;//点评头部
    @BindView(R.id.iv_photo) RoundImageView ivDpPhoto;
    @BindView(R.id.tv_name) TextView tvDpName;
    @BindView(R.id.tv_type) TextView tvDpType;
    @BindView(R.id.tv_time) TextView tvDpTime;
    @BindView(R.id.iv_like) ImageView ivDpLike;

    @BindView(R.id.ll_news) RelativeLayout llNews;//要闻头部
    @BindView(R.id.tv_news_type) TextView tvNewsType;
    @BindView(R.id.tv_news_time) TextView tvNewsTime;

    private NewsContentPresenter newsContentPresenter;

    public String id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_news_content, StatusBarColor.THEME1);
        ButterKnife.bind(this);

        newsContentPresenter = new NewsContentPresenter(this);

        Intent intent = getIntent();
        id = intent.getStringExtra(IntentConstant.O_ID);

        newsContentPresenter.init();
    }

    public void setView(NewsContentJson news) {
        String content = news.getContent();
        String title = news.getTitle();
        String authorName = news.getAuthor_name();
        String authorImg = news.getAuthor_image();
        String type = news.getType();
        String typeName = news.getTypeName();
        String time = news.getCreate_time();
        boolean isGood = "0".equals(news.getIs_good()) ? false : true;
        boolean isCollect = "0".equals(news.getIs_favor()) ? false : true;
        boolean isAttention = "0".equals(news.getIs_follow()) ? false : true;
        boolean isNews = "news".equals(type) ? true : false;

        tvTitle.setText(title);
        wv_content.build().loadData(content);

        if (isNews) {
            //要闻
            llNews.setVisibility(View.VISIBLE);
            llDp.setVisibility(View.GONE);
            tvNewsTime.setText(time);
            tvNewsType.setText(typeName);
        } else {
            //点评
            llDp.setVisibility(View.VISIBLE);
            llNews.setVisibility(View.GONE);
            tvDpName.setText(authorName);
            tvDpType.setText(typeName);
            tvDpTime.setText(time);
            Glide.with(this)
                    .load(authorImg)
                    .asBitmap()
                    .override(80, 80)
                    .error(R.mipmap.ico_def_load).placeholder(R.mipmap.ico_def_load)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            ivDpPhoto.setImageBitmap(resource);
                        }
                    });
            ivDpLike.setSelected(isAttention);
        }

        ivDing.setSelected(isGood);
        ivCollect.setSelected(isCollect);

        plRootView.loadOver();

    }

    @OnClick({R.id.iv_break, R.id.iv_comment, R.id.iv_collect, R.id.iv_ding, R.id.iv_share, R.id.iv_photo, R.id.iv_like})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_break:
                onBackPressed();
                break;
            case R.id.iv_comment:
                //回复
                break;
            case R.id.iv_collect:
                //收藏
                break;
            case R.id.iv_ding:
                //点赞
                break;
            case R.id.iv_share:
                //分享
                break;
            case R.id.iv_photo:
                //作者专栏
                break;
            case R.id.iv_like:
                //关注作者
                break;
        }
    }
}
