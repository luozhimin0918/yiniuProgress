package com.jyh.kxt.index.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.explore.json.AuthorDetailsJson;
import com.jyh.kxt.explore.ui.AuthorActivity;
import com.jyh.kxt.index.adapter.AttentionAuthorAdapter;
import com.jyh.kxt.index.presenter.AttentionAuthorPresenter;
import com.library.bean.EventBusClass;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;

/**
 * 项目名:Kxt
 * 类描述:我的关注-作者
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/17.
 */

public class AttentionAuthorFragment extends BaseFragment implements PageLoadLayout.OnAfreshLoadListener, PullToRefreshBase
        .OnRefreshListener2, AdapterView.OnItemClickListener {

    @BindView(R.id.plv_content) public PullToRefreshListView plvContent;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;

    private AttentionAuthorPresenter presenter;
    private AttentionAuthorAdapter adapter;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_pulltorefresh_list);

        presenter = new AttentionAuthorPresenter(this);
        plRootView.setOnAfreshLoadListener(this);
        plvContent.setOnRefreshListener(this);
        plvContent.setOnItemClickListener(this);

        plRootView.loadWait();
        presenter.init();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getQueue().cancelAll(presenter.getClass().getName());
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnAfreshLoad() {
        plRootView.loadWait();
        presenter.init();
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getContext(), AuthorActivity.class);
        AuthorDetailsJson author = adapter.getData().get(position - 1);
        intent.putExtra(IntentConstant.O_ID, author.getId());
        startActivity(intent);
    }

    public void init(List<AuthorDetailsJson> authors) {
        if (authors == null || authors.size() == 0) {
            plRootView.loadEmptyData();
        } else {
            if (adapter == null) {
                adapter = new AttentionAuthorAdapter(getContext(), authors);
                plvContent.setAdapter(adapter);
                adapter.setRequest(presenter.request);
            } else {
                adapter.setData(authors);
            }
            plRootView.loadOver();
        }
    }

    /**
     * 刷新
     *
     * @param authors
     */
    public void refresh(List<AuthorDetailsJson> authors) {
        adapter.setData(authors);
        plvContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                plvContent.onRefreshComplete();
            }
        }, 200);
    }

    /**
     * 加载更多
     *
     * @param authors
     */
    public void loadMore(List<AuthorDetailsJson> authors) {
        adapter.addData(authors);
        plvContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                plvContent.onRefreshComplete();
            }
        }, 200);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusClass eventBus) {
        switch (eventBus.fromCode) {
            case EventBusClass.EVENT_ATTENTION_AUTHOR_DEL:
                //删除关注、若数据为空则显示空布局
                List<AuthorDetailsJson> data = adapter.getData();
                if (data == null || data.size() == 0)
                    plRootView.loadEmptyData();
                break;
            case EventBusClass.EVENT_ATTENTION_AUTHOR_ADD:
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onChangeTheme() {
        super.onChangeTheme();
        if(adapter!=null)
            adapter.notifyDataSetChanged();
    }
}
