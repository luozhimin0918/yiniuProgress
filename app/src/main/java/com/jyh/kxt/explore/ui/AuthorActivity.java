package com.jyh.kxt.explore.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.explore.json.AuthorDetailsJson;
import com.jyh.kxt.explore.presenter.AuthorPresenter;
import com.jyh.kxt.main.adapter.NewsAdapter;
import com.jyh.kxt.main.json.NewsJson;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:作者
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/3.
 */

public class AuthorActivity extends BaseActivity implements PageLoadLayout.OnAfreshLoadListener, PullToRefreshBase.OnRefreshListener2 {
    @BindView(R.id.v_like) View vLike;
    @BindView(R.id.iv_photo) RoundImageView ivPhoto;
    @BindView(R.id.tv_name) TextView tvName;
    @BindView(R.id.tv_fans) TextView tvFans;
    @BindView(R.id.tv_article) TextView tvArticle;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;
    @BindView(R.id.pl_content) PullToRefreshListView plContent;

    private AuthorPresenter authorPresenter;
    private boolean isLike;

    private String authorId = "";
    private NewsAdapter newsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_news_author, StatusBarColor.NO_COLOR);

        authorPresenter = new AuthorPresenter(this);

        plRootView.setOnAfreshLoadListener(this);
        plContent.setDividerNull();
        plContent.setMode(PullToRefreshBase.Mode.BOTH);
        plContent.setOnRefreshListener(this);

        authorId = getIntent().getStringExtra(IntentConstant.ID);

        plRootView.loadWait();
        authorPresenter.init(authorId);

    }


    /**
     * 初始化布局
     *
     * @param authorDetailsJson
     */
    public void setView(AuthorDetailsJson authorDetailsJson) {
        tvName.setText(authorDetailsJson.getName());
        tvFans.setText("粉丝 " + authorDetailsJson.getNum_fans());
        tvArticle.setText("文章 " + authorDetailsJson.getArticle_num());
        Glide.with(this)
                .load(authorDetailsJson.getPicture())
                .asBitmap()
                .override(120, 120)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        ivPhoto.setImageBitmap(resource);
                    }
                });
        List<NewsJson> list = authorDetailsJson.getList();
        if (list == null || list.size() == 0) {

        } else {
            if (newsAdapter == null) {
                newsAdapter = new NewsAdapter(this, list);
                plContent.setAdapter(newsAdapter);
            } else {
                newsAdapter.setData(list);
            }
        }
        plRootView.loadOver();
        plContent.onRefreshComplete();
    }

    @OnClick({R.id.iv_break, R.id.v_like})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_break:
                onBackPressed();
                break;
            case R.id.v_like:
                isLike = !isLike;
                vLike.setSelected(isLike);
                break;
        }
    }

    /**
     * 加载更多
     *
     * @param authorDetailsJson
     */
    public void loadMore(AuthorDetailsJson authorDetailsJson) {
        newsAdapter.addData(authorDetailsJson.getList());
    }

    @Override
    public void OnAfreshLoad() {
        plRootView.loadWait();
        authorPresenter.init(authorId);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        authorPresenter.init(authorId);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        authorPresenter.loadMore();
    }
}
