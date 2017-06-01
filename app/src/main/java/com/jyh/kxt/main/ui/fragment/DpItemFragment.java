package com.jyh.kxt.main.ui.fragment;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.utils.BrowerHistoryUtils;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.main.adapter.NewsAdapter;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.main.presenter.DpFragmentPresenter;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import java.util.List;

import butterknife.BindView;

/**
 * 项目名:Kxt
 * 类描述:点评
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/18.
 */

public class DpItemFragment extends BaseFragment implements PageLoadLayout.OnAfreshLoadListener, PullToRefreshBase.OnRefreshListener2,
        AdapterView.OnItemClickListener {

    @BindView(R.id.plv_content) public PullToRefreshListView plvContent;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;

    private String code;
    private DpFragmentPresenter presenter;
    private NewsAdapter newsAdapter;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_news_dp);
        presenter = new DpFragmentPresenter(this);
        newsAdapter = null;
    }

    @Override
    public void userVisibleHint() {
        super.userVisibleHint();

        plvContent.setDividerNull();
        plRootView.setOnAfreshLoadListener(this);
        plvContent.setOnRefreshListener(this);
        plvContent.setOnItemClickListener(this);

        Bundle arguments = getArguments();
        code = arguments.getString(IntentConstant.CODE);

        plRootView.loadWait();
        presenter.init(code);
    }

    @Override
    public void OnAfreshLoad() {
        plRootView.loadWait();
        presenter.init(code);
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
        try {
            getQueue().cancelAll(code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroyView();
    }

    public void init(List<NewsJson> news) {
        if (news == null) {
            plRootView.loadEmptyData();
            return;
        }
        if (newsAdapter == null) {
            newsAdapter = new NewsAdapter(getContext(), news);
            plvContent.setAdapter(newsAdapter);
        } else {
            newsAdapter.setData(news);
        }
        plRootView.loadOver();
    }

    public void refresh(List<NewsJson> news) {
        if (news != null) {
            newsAdapter.setData(news);
        }
        plvContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                plvContent.onRefreshComplete();
            }
        }, 200);
    }

    public void loadMore(List<NewsJson> news) {
        if (news != null)
            newsAdapter.addData(news);
        plvContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                plvContent.onRefreshComplete();
            }
        }, 200);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position >= 1) {
            int location = position - 1;
            NewsJson newsJson = newsAdapter.getData().get(location);
            JumpUtils.jump((BaseActivity) getActivity(), newsJson.getO_class(), newsJson.getO_action(), newsJson.getO_id(), newsJson
                    .getHref());
            //保存浏览记录
            BrowerHistoryUtils.save(getContext(), newsJson);
            //单条刷新,改变浏览状态
            newsAdapter.getView(location, view, parent);
        }
    }
}
