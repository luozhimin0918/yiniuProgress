package com.jyh.kxt.user.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.main.adapter.NewsAdapter;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.presenter.CollectNewsPresenter;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import java.util.List;

import butterknife.BindView;

/**
 * 项目名:Kxt
 * 类描述:收藏-文章
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/2.
 */

public class CollectNewsFragment extends BaseFragment implements PageLoadLayout.OnAfreshLoadListener, PullToRefreshBase.OnRefreshListener2 {

    @BindView(R.id.plv_content) public PullToRefreshListView plvContent;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;

    private CollectNewsPresenter collectNewsPresenter;

    private NewsAdapter newsAdapter;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_news_item);

        collectNewsPresenter = new CollectNewsPresenter(this);

        plRootView.setOnAfreshLoadListener(this);
        plvContent.setOnRefreshListener(this);
        plvContent.setDividerNull();
        plvContent.setMode(PullToRefreshBase.Mode.BOTH);

        plvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        collectNewsPresenter.initData();
    }

    /**
     * 初始化数据
     *
     * @param adapterSourceList
     */
    public void initData(List<NewsJson> adapterSourceList) {

    }

    /**
     * 刷新
     *
     * @param adapterSourceList
     */
    public void refresh(List<NewsJson> adapterSourceList) {

    }

    /**
     * 加载更多
     *
     * @param newsMore
     */
    public void loadMore(List<NewsJson> newsMore) {

    }

    @Override
    public void onPullDownToRefresh(final PullToRefreshBase refreshView) {
        collectNewsPresenter.refresh();
    }

    @Override
    public void onPullUpToRefresh(final PullToRefreshBase refreshView) {
        collectNewsPresenter.loadMore();
    }

    @Override
    public void OnAfreshLoad() {
        collectNewsPresenter.initData();
    }

}
