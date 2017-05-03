package com.jyh.kxt.av.ui.fragment;

import android.os.Bundle;

import com.jyh.kxt.R;
import com.jyh.kxt.av.presenter.RankItemPresenter;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import butterknife.BindView;

/**
 * 项目名:Kxt
 * 类描述:视听排行
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/2.
 */

public class RankItemFragment extends BaseFragment implements PageLoadLayout.OnAfreshLoadListener {

    public static final String RANK_MORE_PLAY = "rank_more_play";//视听排行 最多播放
    public static final String RANK_MORE_COMMENT = "rank_more_comment";//视听排行 最多评论
    public static final String RANK_MORE_COLLECT = "rank_more_collect";//视听排行 最多收藏

    @BindView(R.id.plv_content) public PullToRefreshListView plvContent;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;
    private RankItemPresenter rankItemPresenter;

    private String rankUrl;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_video_item);
        rankItemPresenter = new RankItemPresenter(this);
        plRootView.setOnAfreshLoadListener(this);
        plvContent.setMode(PullToRefreshBase.Mode.BOTH);
        plvContent.setOnRefreshListener(rankItemPresenter);

        switch (getArguments().getString(IntentConstant.RANK_TYPE)) {
            case RANK_MORE_PLAY:
                rankUrl = HttpConstant.VIDEO_MOST_PLAY;
                break;
            case RANK_MORE_COMMENT:
                rankUrl = HttpConstant.VIDEO_MOST_COMMENT;
                break;
            case RANK_MORE_COLLECT:
                rankUrl = HttpConstant.VIDEO_MOST_COLLECT;
                break;
        }

        plRootView.loadWait();
        rankItemPresenter.init(rankUrl);

    }

    @Override
    public void OnAfreshLoad() {
        plRootView.loadWait();
        rankItemPresenter.init(rankUrl);
    }
}
