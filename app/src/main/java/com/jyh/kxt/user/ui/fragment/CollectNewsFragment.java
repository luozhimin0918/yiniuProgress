package com.jyh.kxt.user.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.utils.BrowerHistoryUtils;
import com.jyh.kxt.index.ui.WebActivity;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.main.ui.activity.NewsContentActivity;
import com.jyh.kxt.user.adapter.CollectNewsAdapter;
import com.jyh.kxt.user.presenter.CollectNewsPresenter;
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

    private CollectNewsAdapter adapter;

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
                NewsJson newsJson = adapter.dataList.get(position - 1);

                Intent intent = null;
                if (TextUtils.isEmpty(newsJson.getHref())) {
                    intent = new Intent(getContext(), NewsContentActivity.class);
                    intent.putExtra(IntentConstant.O_ID, newsJson.getO_id());
                } else {
                    intent = new Intent(getContext(), WebActivity.class);
                    intent.putExtra(IntentConstant.WEBURL, newsJson.getHref());
                }

                startActivity(intent);
//                //保存浏览记录
//                BrowerHistoryUtils.save(getContext(), newsJson);
//
//                //单条刷新,改变浏览状态
//                adapter.getView(position, view, parent);
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
        if (adapter == null) {
            adapter = new CollectNewsAdapter(adapterSourceList, getContext());
            plvContent.setAdapter(adapter);
        } else {
            adapter.setData(adapterSourceList);
        }
    }

    /**
     * 刷新
     *
     * @param adapterSourceList
     */
    public void refresh(List<NewsJson> adapterSourceList) {
        adapter.setData(adapterSourceList);
        plvContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                plvContent.onRefreshComplete();
            }
        }, 500);
    }

    /**
     * 加载更多
     *
     * @param newsMore
     */
    public void loadMore(List<NewsJson> newsMore) {
        adapter.addData(newsMore);
        plvContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                plvContent.onRefreshComplete();
            }
        }, 500);
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
