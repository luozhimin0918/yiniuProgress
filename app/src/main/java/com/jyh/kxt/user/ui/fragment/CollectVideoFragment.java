package com.jyh.kxt.user.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.jyh.kxt.R;
import com.jyh.kxt.av.json.VideoListJson;
import com.jyh.kxt.av.ui.VideoDetailActivity;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.user.adapter.CollectVideoAdapter;
import com.jyh.kxt.user.presenter.CollectVideoPresenter;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:Kxt
 * 类描述:收藏-视听
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/2.
 */

public class CollectVideoFragment extends BaseFragment implements PageLoadLayout.OnAfreshLoadListener, PullToRefreshBase
        .OnRefreshListener2 {

    @BindView(R.id.plv_content) public PullToRefreshListView plvContent;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;

    private CollectVideoPresenter collectVideoPresenter;

    private CollectVideoAdapter adapter;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_video_item);

        collectVideoPresenter = new CollectVideoPresenter(this);

        plRootView.setOnAfreshLoadListener(this);
        plvContent.setDividerNull();
        plvContent.setMode(PullToRefreshBase.Mode.BOTH);
        plvContent.setOnRefreshListener(this);

        plvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VideoListJson video = adapter.getData().get(position - 1);
                Intent videoIntent = new Intent(getContext(), VideoDetailActivity.class);
                videoIntent.putExtra(IntentConstant.ID, video.getId());
                startActivity(videoIntent);
            }
        });

        collectVideoPresenter.initData();
    }

    /**
     * 初始化数据
     *
     * @param videos
     */
    public void initData(List<VideoListJson> videos) {
        if (adapter == null) {
            adapter = new CollectVideoAdapter(videos, getContext());
            plvContent.setAdapter(adapter);
        } else {
            adapter.setData(videos);
        }
    }

    /**
     * 刷新
     *
     * @param videos
     */
    public void refresh(List<VideoListJson> videos) {
        adapter.setData(videos);
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
     * @param videoMore
     */
    public void loadMore(List<VideoListJson> videoMore) {
        adapter.addData(videoMore);
        plvContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                plvContent.onRefreshComplete();
            }
        }, 500);
    }

    @Override
    public void OnAfreshLoad() {
        collectVideoPresenter.initData();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        collectVideoPresenter.refresh();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        collectVideoPresenter.loadMore();
    }

}
