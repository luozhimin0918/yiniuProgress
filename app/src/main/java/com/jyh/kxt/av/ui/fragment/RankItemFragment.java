package com.jyh.kxt.av.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.jyh.kxt.R;
import com.jyh.kxt.av.json.VideoListJson;
import com.jyh.kxt.av.presenter.RankItemPresenter;
import com.jyh.kxt.av.ui.VideoDetailActivity;
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

public class RankItemFragment extends BaseFragment implements PageLoadLayout.OnAfreshLoadListener, AdapterView.OnItemClickListener {

    public static final String RANK_MORE_PLAY = "rank_more_play";//视听排行 最多播放
    public static final String RANK_MORE_COMMENT = "rank_more_comment";//视听排行 最多评论
    public static final String RANK_MORE_COLLECT = "rank_more_collect";//视听排行 最多收藏

    @BindView(R.id.plv_content) public PullToRefreshListView plvContent;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;
    private RankItemPresenter rankItemPresenter;

    private String rankUrl;
    public String type;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_video_item);
        rankItemPresenter = new RankItemPresenter(this);
        plRootView.setOnAfreshLoadListener(this);
        plvContent.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        plvContent.setDividerNull();
        plvContent.setOnRefreshListener(rankItemPresenter);
        plvContent.setOnItemClickListener(this);

        type = getArguments().getString(IntentConstant.RANK_TYPE);
        switch (type) {
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

    @Override
    public void onDestroyView() {
        getQueue().cancelAll(rankUrl);
        super.onDestroyView();
    }

    @Override
    public void onChangeTheme() {
        super.onChangeTheme();
        if (plvContent != null)
            plvContent.setDividerNull();
        if (rankItemPresenter != null)
            rankItemPresenter.onChangeTheme();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position>0){
            int dataPosition = position - 1;
            VideoListJson video = rankItemPresenter.rankAdapter.getData().get(dataPosition);
            Intent intent = new Intent(getContext(), VideoDetailActivity.class);
            intent.putExtra(IntentConstant.O_ID, video.getId());
            startActivity(intent);
        }
    }
}
