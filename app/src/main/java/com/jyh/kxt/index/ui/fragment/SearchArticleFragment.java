package com.jyh.kxt.index.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.utils.BrowerHistoryUtils;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.index.presenter.SearchArticlePresenter;
import com.jyh.kxt.main.adapter.NewsAdapter;
import com.jyh.kxt.main.json.NewsJson;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import java.util.List;

import butterknife.BindView;

/**
 * 项目名:Kxt
 * 类描述:搜索 文章
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/18.
 */

public class SearchArticleFragment extends BaseFragment implements PageLoadLayout.OnAfreshLoadListener, PullToRefreshBase
        .OnRefreshListener2, AdapterView.OnItemClickListener {

    @BindView(R.id.plv_content) public PullToRefreshListView plvContent;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;

    private SearchArticlePresenter presenter;
    private String key;
    private NewsAdapter newsAdapter;
    private boolean isSearch;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_pulltorefresh_list);

        presenter = new SearchArticlePresenter(this);

        plvContent.setDividerNull();
        plvContent.setMode(PullToRefreshBase.Mode.BOTH);
        plvContent.setOnRefreshListener(this);
        plRootView.setOnAfreshLoadListener(this);
        plvContent.setOnItemClickListener(this);

        if (isSearch) {
            plRootView.loadWait();
            presenter.init(key);
        }

    }

    public void search(String key) {
        this.key = key;
        if (isVisible()) {
            plRootView.loadWait();
            presenter.init(key);
            isSearch = false;
        } else {
            isSearch = true;
        }
    }

    @Override
    public void OnAfreshLoad() {
        plRootView.loadWait();
        presenter.init(key);
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
    public void onDestroyView() {
        super.onDestroyView();
        getQueue().cancelAll(presenter.getClass().getName());
    }

    public void init(List<NewsJson> newsJsons) {
        if (newsJsons == null || newsJsons.size() == 0) {
            plRootView.setNullText(getString(R.string.error_search_null));
            plRootView.loadEmptyData();
            return;
        } else {
            if (newsAdapter == null) {
                newsAdapter = new NewsAdapter(getContext(), newsJsons);
                plvContent.setAdapter(newsAdapter);
            } else {
                newsAdapter.setData(newsJsons);
            }
            newsAdapter.setSearchKey(key);
            plRootView.loadOver();
        }
    }

    public void refresh(List<NewsJson> list) {
        if (list != null) {
            newsAdapter.setData(list);
        }
        plvContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                plvContent.onRefreshComplete();
            }
        }, 200);
    }

    public void loadMore(List<NewsJson> list) {
        if (list != null) {
            newsAdapter.addData(list);
        }
        plvContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                plvContent.onRefreshComplete();
            }
        }, 200);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int index = position - 1;
        NewsJson newsJson = newsAdapter.getData().get(index);
        JumpUtils.jump((BaseActivity) getActivity(), newsJson.getO_class(), newsJson.getO_action(), newsJson.getO_id(), newsJson.getHref());
        BrowerHistoryUtils.save(getContext(), newsJson);
        newsAdapter.getView(index, view, parent);
    }
}
