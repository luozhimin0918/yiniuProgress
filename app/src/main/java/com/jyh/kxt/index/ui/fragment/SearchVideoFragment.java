package com.jyh.kxt.index.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.jyh.kxt.R;
import com.jyh.kxt.av.json.VideoListJson;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.index.adapter.VideoSearchAdapter;
import com.jyh.kxt.index.presenter.SearchVideoPresenter;
import com.library.base.http.VarConstant;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:Kxt
 * 类描述:搜索 视听
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/18.
 */

public class SearchVideoFragment extends BaseFragment implements PageLoadLayout.OnAfreshLoadListener, PullToRefreshBase
        .OnRefreshListener2, AdapterView.OnItemClickListener {

    @BindView(R.id.plv_content) public PullToRefreshListView plvContent;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;
    private SearchVideoPresenter presenter;
    private String key;
    private VideoSearchAdapter videoAdapter;
    private boolean isSearch;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_pulltorefresh_list);

        presenter = new SearchVideoPresenter(this);

        plvContent.setDividerNull();
        plvContent.setMode(PullToRefreshBase.Mode.BOTH);
        plvContent.setOnRefreshListener(this);
        plRootView.setOnAfreshLoadListener(this);
        plvContent.setOnItemClickListener(this);
        if (isSearch) {
            plRootView.loadWait();
            presenter.init(key);
        }
    }

    public void search(String searchKey) {
        this.key = searchKey;
        if (isVisible()) {
            plRootView.loadWait();
            presenter.init(key);
            isSearch = false;
        } else {
            isSearch = true;
        }
    }

    @Override
    public void OnAfreshLoad() {
        plRootView.loadWait();
        presenter.init(key);
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
        VideoListJson video = videoAdapter.getData().get(position - 1);
        JumpUtils.jump((BaseActivity) getActivity(), VarConstant.OCLASS_VIDEO, VarConstant.OACTION_DETAIL, video.getUid(), null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getQueue().cancelAll(presenter.getClass().getName());
    }

    public void init(List<VideoListJson> videos) {
        if (videos == null) {
            plRootView.setNullText(getString(R.string.error_search_null));
            plRootView.loadEmptyData();
        } else {
            if (videoAdapter == null) {
                videoAdapter = new VideoSearchAdapter(getContext(), videos);
                plvContent.setAdapter(videoAdapter);
            } else {
                videoAdapter.setData(videos);
            }
            videoAdapter.setSearchKey(key);
            plRootView.loadOver();
        }
    }

    public void refresh(List<VideoListJson> videos) {
        if (videos != null) {
            videoAdapter.setData(videos);
        }
        plvContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                plvContent.onRefreshComplete();
            }
        }, 200);
    }

    public void loadMore(List<VideoListJson> videos) {
        if (videos != null)
            videoAdapter.addData(videos);
        plvContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                plvContent.onRefreshComplete();
            }
        }, 200);
    }
}
