package com.jyh.kxt.explore.ui;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.explore.adapter.MoreAdapter;
import com.jyh.kxt.explore.json.ActivityJson;
import com.jyh.kxt.explore.json.TopicJson;
import com.jyh.kxt.explore.presenter.MorePresenter;
import com.library.base.http.VarConstant;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:更多
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/8.
 */

public class MoreActivity extends BaseActivity implements PageLoadLayout.OnAfreshLoadListener, PullToRefreshBase.OnRefreshListener2,
        AdapterView.OnItemClickListener {

    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.pl_content) public PullToRefreshListView plContent;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;

    private MorePresenter morePresenter;
    private String type;

    public MoreAdapter moreAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_more, StatusBarColor.THEME1);

        morePresenter = new MorePresenter(this);

        plContent.setDividerNull();
        plContent.setMode(PullToRefreshBase.Mode.BOTH);
        plContent.setOnRefreshListener(this);
        plContent.setOnItemClickListener(this);
        plRootView.setOnAfreshLoadListener(this);
        type = getIntent().getStringExtra(IntentConstant.TYPE);

        String name = "";
        switch (type) {
            case VarConstant.EXPLORE_ACTIVITY:
                name = "活动";
                break;
            case VarConstant.EXPLORE_TOPIC:
                name = "专题";
                break;
        }
        tvBarTitle.setText(name);

        plRootView.loadWait();
        morePresenter.init(type);
    }

    public void init(List data) {
        if (moreAdapter == null) {
            moreAdapter = new MoreAdapter(data, this, type);
            plContent.setAdapter(moreAdapter);
        } else {
            moreAdapter.setData(data);
        }
        plRootView.loadOver();
    }


    public void refresh(List list) {
        if (list != null)
            moreAdapter.setData(list);
        plContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                plContent.onRefreshComplete();
            }
        }, 500);
    }

    public void loadMore(List data) {
        moreAdapter.addData(data);
        plContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                plContent.onRefreshComplete();
            }
        }, 500);
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
        //重新加载
        if (type != null) {
            plRootView.loadWait();
            morePresenter.init(type);
        } else {
            plRootView.loadError();
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        morePresenter.refresh();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        morePresenter.loadMore();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getQueue().cancelAll(morePresenter.getClass().getName());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int dataPosition = position - 1;
        switch (type) {
            case VarConstant.EXPLORE_ACTIVITY:
                ActivityJson activity = JSON.parseObject(moreAdapter.getData().get(dataPosition).toString(), ActivityJson.class);
                JumpUtils.jump(this, VarConstant.OCLASS_ACTIVITY, VarConstant.OACTION_DETAIL, null, activity.getUrl());
                break;
            case VarConstant.EXPLORE_TOPIC:
                TopicJson topic = JSON.parseObject(moreAdapter.getData().get(dataPosition).toString(), TopicJson.class);
                JumpUtils.jump(this, topic.getO_class(), topic.getO_action(), topic.getO_id(), topic.getHref());
                break;
        }
    }
}
