package com.jyh.kxt.av.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jyh.kxt.R;
import com.jyh.kxt.av.adapter.VideoAdapter;
import com.jyh.kxt.av.presenter.VideoItemPresenter;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.constant.IntentConstant;
import com.library.bean.EventBusClass;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Mr'Dai on 2017/4/10.
 * 视听-视听-视听Item栏目
 */
public class VideoItemFragment extends BaseFragment implements PageLoadLayout.OnAfreshLoadListener, PullToRefreshListView
        .OnRefreshListener2 {

    @BindView(R.id.plv_content) public PullToRefreshListView plvContent;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;

    private String name;
    private VideoItemPresenter videoItemPresenter;
    private String id;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_video_item);

        videoItemPresenter = new VideoItemPresenter(this);

        plRootView.setOnAfreshLoadListener(this);

        plvContent.setMode(PullToRefreshBase.Mode.BOTH);

        plvContent.setOnRefreshListener(this);

        Bundle arguments = getArguments();
        name = arguments.getString(IntentConstant.NAME);
        id = arguments.getString(IntentConstant.CODE);

        videoItemPresenter.init(id);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusClass eventBus) {
        if (eventBus.fromCode == EventBusClass.EVENT_COLLECT_VIDEO) {
            VideoAdapter videoAdapter = videoItemPresenter.videoAdapter;
            if (videoAdapter != null) {
                videoAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void userVisibleHint() {
        super.userVisibleHint();
    }

    @Override
    public void OnAfreshLoad() {
        videoItemPresenter.init(id);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        videoItemPresenter.onPullDownToRefresh(refreshView);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        videoItemPresenter.onPullUpToRefresh(refreshView);
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
        super.onDestroyView();
        try {
            getQueue().cancelAll(videoItemPresenter.getClass().getName());
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
