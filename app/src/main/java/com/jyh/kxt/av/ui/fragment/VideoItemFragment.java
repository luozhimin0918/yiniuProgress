package com.jyh.kxt.av.ui.fragment;

import android.os.Bundle;

import com.jyh.kxt.R;
import com.jyh.kxt.av.presenter.VideoItemPresenter;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.constant.IntentConstant;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

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

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_video_item);

        videoItemPresenter = new VideoItemPresenter(this);

        plRootView.setOnAfreshLoadListener(this);

        plvContent.setMode(PullToRefreshBase.Mode.BOTH);

        plvContent.setOnRefreshListener(this);

        Bundle arguments = getArguments();
        name = arguments.getString(IntentConstant.NAME);

        videoItemPresenter.init(arguments);

    }

    @Override
    public void userVisibleHint() {
        super.userVisibleHint();
    }

    @Override
    public void OnAfreshLoad() {

    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        videoItemPresenter.onPullDownToRefresh(refreshView);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        videoItemPresenter.onPullUpToRefresh(refreshView);
    }
}
