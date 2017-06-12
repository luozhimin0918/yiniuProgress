package com.jyh.kxt.explore.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.explore.adapter.NewsAdapter;
import com.jyh.kxt.explore.json.AuthorNewsJson;
import com.jyh.kxt.explore.presenter.ArticleItemPresenter;
import com.library.base.http.VarConstant;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import java.util.List;

import butterknife.BindView;

/**
 * 项目名:Kxt
 * 类描述:名家专栏-文章
 * 创建人:苟蒙蒙
 * 创建日期:2017/6/12.
 */

public class ArticleItemFragment extends BaseFragment implements AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener2,
        PageLoadLayout.OnAfreshLoadListener {
    @BindView(R.id.plv_content) public PullToRefreshListView plvContent;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;

    private ArticleItemPresenter presenter;

    private String name;
    private String id;
    private String type;
    private NewsAdapter newsAdapter;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_news_item);
    }

    @Override
    public void userVisibleHint() {
        Bundle arguments = getArguments();
        name = arguments.getString(IntentConstant.NAME);
        id = arguments.getString(IntentConstant.CODE);
        type = arguments.getString(IntentConstant.TYPE);
        presenter = new ArticleItemPresenter(this);

        plRootView.setOnAfreshLoadListener(this);
        plvContent.setDividerNull();
        plvContent.setMode(PullToRefreshBase.Mode.BOTH);
        plvContent.setOnItemClickListener(this);
        plvContent.setOnRefreshListener(this);

        if (VarConstant.EXPLORE_ARTICLE_LIST_TYPE_FOLLOW.equals(type)) {
            boolean isLogined = LoginUtils.isLogined(getContext());
            if (isLogined) {
                plRootView.loadWait();
                presenter.init(id, type);
            } else {
                plRootView.setNullText("请先登录");
                plRootView.loadEmptyData();
            }
        } else {
            plRootView.loadWait();
            presenter.init(id, type);
        }
    }

    public void init(List<AuthorNewsJson> list) {
        if (newsAdapter == null) {
            newsAdapter = new NewsAdapter(getContext(), list);
            plvContent.setAdapter(newsAdapter);
        } else {
            newsAdapter.setData(list);
        }
    }

    public void refresh(List<AuthorNewsJson> list) {
        if (list != null && list.size() != 0) {
            newsAdapter.setData(list);
        }
    }

    public void loadMore(List<AuthorNewsJson> list) {
        if (list != null && list.size() != 0) {
            newsAdapter.addData(list);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        presenter.refresh();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        presenter.loadMore();
    }

    @Override
    public void OnAfreshLoad() {
        plRootView.loadWait();
        if (VarConstant.EXPLORE_ARTICLE_LIST_TYPE_FOLLOW.equals(type)) {
            boolean isLogined = LoginUtils.isLogined(getContext());
            if (isLogined) {
                presenter.init(id, type);
            } else {
                plRootView.setNullText("请先登录");
                plRootView.loadEmptyData();
            }
        } else {
            presenter.init(id, type);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getQueue().cancelAll(id + type);
    }

    @Override
    public void onChangeTheme() {
        super.onChangeTheme();
        if (newsAdapter != null)
            newsAdapter.notifyDataSetChanged();
    }

}
