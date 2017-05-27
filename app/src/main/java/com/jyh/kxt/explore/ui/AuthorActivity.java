package com.jyh.kxt.explore.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.explore.adapter.NewsAdapter;
import com.jyh.kxt.explore.json.AuthorDetailsJson;
import com.jyh.kxt.explore.json.AuthorNewsJson;
import com.jyh.kxt.explore.presenter.AuthorPresenter;
import com.library.base.http.VarConstant;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:作者
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/3.
 */

public class AuthorActivity extends BaseActivity implements PageLoadLayout.OnAfreshLoadListener, PullToRefreshBase.OnRefreshListener2,
        AdapterView.OnItemClickListener {
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;
    @BindView(R.id.pl_content) public PullToRefreshListView plContent;
    @BindView(R.id.pl_list_rootView) public PageLoadLayout plListRootView;
    @BindView(R.id.ll_error) LinearLayout llError;
    private View vLike;
    private RoundImageView ivPhoto;
    private TextView tvName;
    private TextView tvFans;
    private TextView tvArticle;
    private TextView tvInfo;

    private AuthorPresenter authorPresenter;
    private boolean isLike;

    private String authorId = "";
    public NewsAdapter newsAdapter;
    private View headView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_news_author, StatusBarColor.NO_COLOR);
        ButterKnife.bind(this);

        authorPresenter = new AuthorPresenter(this);

        plRootView.setOnAfreshLoadListener(this);
        plContent.setDividerNull();
        plContent.setMode(PullToRefreshBase.Mode.BOTH);
        plContent.setOnRefreshListener(this);
        plContent.setOnItemClickListener(this);
        plListRootView.setOnAfreshLoadListener(new PageLoadLayout.OnAfreshLoadListener() {
            @Override
            public void OnAfreshLoad() {
                plListRootView.loadWait();
                authorPresenter.reLoadListData();
            }
        });

        llError.setVisibility(View.GONE);

        authorId = getIntent().getStringExtra(IntentConstant.O_ID);
        initHeadView();

        loadWait();
        authorPresenter.init(authorId);

    }

    /**
     * 初始化布局
     *
     * @param authorDetailsJson
     */
    public void setView(AuthorDetailsJson authorDetailsJson) {
        setHeadView(authorDetailsJson);
        try {
            List<AuthorNewsJson> list = authorDetailsJson.getList();
            if (list == null || list.size() == 0) {
                plListRootView.loadEmptyData();
            } else {
                List<AuthorNewsJson> data;
                if (list.size() > VarConstant.LIST_MAX_SIZE) {
                    authorPresenter.setMore(true);
                    authorPresenter.setLastId(list.get(VarConstant.LIST_MAX_SIZE - 1).getO_id());
                    data = new ArrayList<>(list.subList(0, VarConstant.LIST_MAX_SIZE));
                } else {
                    authorPresenter.setMore(false);
                    authorPresenter.setLastId("");
                    data = new ArrayList<>(list);
                }
                if (newsAdapter == null) {
                    newsAdapter = new NewsAdapter(this, data);
                    plContent.setAdapter(newsAdapter);
                } else {
                    newsAdapter.setData(data);
                }
                if (plContent.getRefreshableView().getHeaderViewsCount() <= 1)
                    plContent.getRefreshableView().addHeaderView(headView);
            }
        } catch (Exception e) {
            e.printStackTrace();
            plListRootView.loadError();
        }
        loadOver();
    }

    @OnClick({R.id.iv_break2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_break2:
                onBackPressed();
                break;
        }
    }

    public void refresh(AuthorDetailsJson authorDetailsJson) {
        setHeadView(authorDetailsJson);
        try {
            List<AuthorNewsJson> list = authorDetailsJson.getList();
            if (list == null || list.size() == 0) {
            } else {
                List<AuthorNewsJson> data;
                if (list.size() > VarConstant.LIST_MAX_SIZE) {
                    authorPresenter.setMore(true);
                    authorPresenter.setLastId(newsAdapter.getLastId());
                    data = new ArrayList<>(list.subList(0, VarConstant.LIST_MAX_SIZE));
                } else {
                    authorPresenter.setMore(false);
                    authorPresenter.setLastId("");
                    data = new ArrayList<>(list);
                }
                newsAdapter.setData(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        plRootView.postDelayed(new Runnable() {
            @Override
            public void run() {
                plContent.onRefreshComplete();
            }
        }, 500);
    }

    /**
     * 加载更多
     *
     * @param newsJsons
     */
    public void loadMore(List<AuthorNewsJson> newsJsons) {
        newsAdapter.addData(newsJsons);
    }

    /**
     * 加载错误,再次加载
     *
     * @param list
     */
    public void reLoadListData(List<AuthorNewsJson> list) {
        List<AuthorNewsJson> data;
        if (list.size() > VarConstant.LIST_MAX_SIZE) {
            authorPresenter.setMore(true);
            authorPresenter.setLastId(newsAdapter.getLastId());
            data = new ArrayList<>(list.subList(0, VarConstant.LIST_MAX_SIZE));
        } else {
            authorPresenter.setMore(false);
            authorPresenter.setLastId("");
            data = new ArrayList<>(list);
        }
        if (newsAdapter == null) {
            newsAdapter = new NewsAdapter(this, data);
            plContent.setAdapter(newsAdapter);
        } else {
            newsAdapter.setData(data);
        }
    }

    @Override
    public void OnAfreshLoad() {
        loadWait();
        authorPresenter.init(authorId);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        authorPresenter.refresh();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        authorPresenter.loadMore();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mQueue.cancelAll(authorPresenter.getClass().getName());
    }


    public void loadWait() {
        llError.setVisibility(View.VISIBLE);
        plRootView.loadWait();
    }


    public void loadError() {
        llError.setVisibility(View.VISIBLE);
        plRootView.loadError();
    }

    public void loadOver() {
        llError.setVisibility(View.GONE);
        plRootView.loadOver();
    }

    /**
     * 关注
     *
     * @param isFollow
     */
    public void attention(boolean isFollow) {
        vLike.setSelected(!isFollow);
    }

    public void initHeadView() {
        headView = LayoutInflater.from(this).inflate(R.layout.layout_author_head, null, false);
        tvName = (TextView) headView.findViewById(R.id.tv_name);
        tvInfo = (TextView) headView.findViewById(R.id.tv_info);
        ivPhoto = (RoundImageView) headView.findViewById(R.id.iv_photo);
        vLike = headView.findViewById(R.id.v_like);
        tvFans = (TextView) headView.findViewById(R.id.tv_fans);
        tvArticle = (TextView) headView.findViewById(R.id.tv_article);
        View ivBreak = headView.findViewById(R.id.iv_break);

        ivBreak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        vLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authorPresenter.attention(vLike.isSelected());
            }
        });
    }

    private void setHeadView(AuthorDetailsJson authorDetailsJson) {
        tvName.setText(authorDetailsJson.getName());
        tvFans.setText("粉丝 " + authorDetailsJson.getNum_fans());
        tvArticle.setText("文章 " + authorDetailsJson.getArticle_num());
        tvInfo.setText(authorDetailsJson.getIntroduce());

        //关注
        String is_follow = authorDetailsJson.getIs_follow();
        if (is_follow == null)
            vLike.setSelected(false);
        else if (is_follow.equals("1"))
            vLike.setSelected(true);
        else
            vLike.setSelected(false);

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
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        List<AuthorNewsJson> data = newsAdapter.getData();
        AuthorNewsJson newsJson = data.get(position - 2);
        JumpUtils.jump(this, newsJson.getO_class(), newsJson.getO_action(), newsJson.getO_id(), newsJson.getHref());
    }
}
