package com.jyh.kxt.main.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.main.presenter.NewsItemPresenter;
import com.library.bean.EventBusClass;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;

import static org.greenrobot.eventbus.ThreadMode.MAIN;

/**
 * 项目名:Kxt
 * 类描述:要闻item
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/12.
 */

public class NewsItemFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2, PageLoadLayout.OnAfreshLoadListener {

    private NewsItemPresenter newsItemPresenter;

    @BindView(R.id.plv_content) public PullToRefreshListView plvContent;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;
    private String name;
    private boolean isMain;//是否为首页
    private String code;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_news_item);

        newsItemPresenter = new NewsItemPresenter(this);

        plvContent.setMode(PullToRefreshBase.Mode.BOTH);

        plvContent.setOnRefreshListener(this);
        plvContent.setDividerNull();
        plRootView.setOnAfreshLoadListener(this);

        Bundle arguments = getArguments();
        name = arguments.getString(IntentConstant.NAME);
        int index = arguments.getInt(IntentConstant.INDEX);
        code = arguments.getString(IntentConstant.CODE);
        isMain = index == 0 ? true : false;

        newsItemPresenter.setMain(isMain);

        newsItemPresenter.init(arguments);

    }

    @Subscribe(threadMode = MAIN)
    public void onEvent(EventBusClass eventBus) {
        switch (eventBus.fromCode) {
            case EventBusClass.EVENT_CLEAR_BROWER:
                //清理浏览记录
                newsItemPresenter.clearBrowerHistory();
                break;
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        newsItemPresenter.onPullDownToRefresh(refreshView);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        newsItemPresenter.onPullUpToRefresh(refreshView);
    }

    @Override
    public void OnAfreshLoad() {
        plRootView.loadWait();
        newsItemPresenter.reLoad();
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
    public void onDestroyView() {
        try {
            getQueue().cancelAll(code);
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroyView();
    }
}
