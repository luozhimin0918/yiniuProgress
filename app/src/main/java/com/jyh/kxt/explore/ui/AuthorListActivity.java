package com.jyh.kxt.explore.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.annotation.OnItemClickListener;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.explore.adapter.AuthorHeadContentAdapter;
import com.jyh.kxt.explore.adapter.AuthorHeadViewAdapter;
import com.jyh.kxt.explore.json.AuthorJson;
import com.jyh.kxt.explore.presenter.AuthorListPresenter;
import com.jyh.kxt.main.adapter.NewsAdapter;
import com.jyh.kxt.main.json.NewsJson;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;
import com.library.widget.recycler.DividerGridItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:名家专栏
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/15.
 */

public class AuthorListActivity extends BaseActivity implements PageLoadLayout.OnAfreshLoadListener, PullToRefreshBase.OnRefreshListener2 {
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.plv_content) public PullToRefreshListView plvContent;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;

    private AuthorListPresenter authorListPresenter;
    public NewsAdapter newsAdapter;
    private View headView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_authorlist, StatusBarColor.THEME1);

        authorListPresenter = new AuthorListPresenter(this);

        tvBarTitle.setText("名家专栏");
        plRootView.setOnAfreshLoadListener(this);
        plvContent.setDividerNull();
        plvContent.setMode(PullToRefreshBase.Mode.BOTH);
        plvContent.setOnRefreshListener(this);

        plRootView.loadWait();
        authorListPresenter.init();
    }

    @OnClick(R.id.iv_bar_break)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
        }
    }

    @Override
    public void OnAfreshLoad() {
        plRootView.loadWait();
        authorListPresenter.init();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        authorListPresenter.refresh();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        authorListPresenter.loadMore();
    }

    /**
     * 添加头部布局
     *
     * @param authors
     */
    public void addHeadView(List<AuthorJson> authors) {
        if (headView == null)
            headView = LayoutInflater.from(this).inflate(R.layout.layout_explore_author_head, null);
        ViewPager vpContent = (ViewPager) headView.findViewById(R.id.vp_content);
        final LinearLayout llDian = (LinearLayout) headView.findViewById(R.id.ll_dian);
        llDian.removeAllViews();
        int size = authors.size();
        int pageCount = size / 6;
        int pageNum = size % 6;
        if (pageNum != 0)
            pageCount++;
        List<View> views = new ArrayList<>();
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .MATCH_PARENT);

        for (int i = 0; i < pageCount; i++) {
            initPageView(authors, llDian, size, pageCount, views, params, i);
        }
        vpContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int childCount = llDian.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = llDian.getChildAt(i);
                    if (i == position) {
                        childAt.setSelected(true);
                    } else {
                        childAt.setSelected(false);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        vpContent.setAdapter(new AuthorHeadViewAdapter(this, views));
    }

    /**
     * 初始化数据
     *
     * @param newsList
     */
    public void init(List<NewsJson> newsList) {
        if (newsAdapter == null) {
            newsAdapter = new NewsAdapter(this, newsList);
            plvContent.setAdapter(newsAdapter);
        } else {
            newsAdapter.setData(newsList);
        }
        if (headView != null)
            plvContent.getRefreshableView().removeHeaderView(headView);
        plvContent.getRefreshableView().addHeaderView(headView);
        plRootView.loadOver();
    }

    /**
     * 刷新列表
     *
     * @param newsList
     */
    public void refreshList(List<NewsJson> newsList) {
        if (headView != null)
            plvContent.getRefreshableView().removeHeaderView(headView);
        plvContent.getRefreshableView().addHeaderView(headView);
        newsAdapter.setData(newsList);
    }


    public void loadMore(List<NewsJson> newsJsons) {
        newsAdapter.addData(newsJsons);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mQueue.cancelAll(authorListPresenter.getClass().getName());
    }

    /**
     * 初始化头部每页布局
     *
     * @param authors
     * @param llDian
     * @param size
     * @param pageCount
     * @param views
     * @param params
     * @param i 页码
     */
    private void initPageView(List<AuthorJson> authors, LinearLayout llDian, int size, int pageCount, List<View> views, ViewGroup
            .LayoutParams params, int i) {
        RecyclerView recyclerView = new RecyclerView(this);
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setLayoutParams(params);

        List<AuthorJson> authorJsons = null;
        if (i == pageCount - 1) {
            //最后一页
            authorJsons = authors.subList(i * 6, size);
        } else {
            authorJsons = authors.subList(i * 6, (i + 1) * 6);
        }

        ImageView dian = new ImageView(this);
        dian.setImageResource(R.drawable.sel_dian);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(8, 8);
        layoutParams.rightMargin = 10;
        dian.setLayoutParams(layoutParams);

        llDian.addView(dian);

        recyclerView.addItemDecoration(new DividerGridItemDecoration(this));

        final AuthorHeadContentAdapter adapter = new AuthorHeadContentAdapter(this, authorJsons);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                String authorId = adapter.getData().get(position).getId();
                Intent authorIntent = new Intent(getContext(), AuthorActivity.class);
                authorIntent.putExtra(IntentConstant.ID, authorId);
                startActivity(authorIntent);
            }
        });
        recyclerView.setAdapter(adapter);
        views.add(recyclerView);
    }
}
