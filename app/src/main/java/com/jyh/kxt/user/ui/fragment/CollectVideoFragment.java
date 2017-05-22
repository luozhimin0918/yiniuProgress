package com.jyh.kxt.user.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.jyh.kxt.R;
import com.jyh.kxt.av.json.VideoListJson;
import com.jyh.kxt.av.ui.VideoDetailActivity;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.annotation.DelNumListener;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.utils.collect.CollectUtils;
import com.jyh.kxt.user.adapter.CollectVideoAdapter;
import com.jyh.kxt.user.presenter.CollectVideoPresenter;
import com.jyh.kxt.user.ui.CollectActivity;
import com.library.base.http.VarConstant;
import com.library.bean.EventBusClass;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;
import com.library.widget.window.ToastView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;

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

    public CollectVideoAdapter adapter;

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
                videoIntent.putExtra(IntentConstant.O_ID, video.getId());
                startActivity(videoIntent);
            }
        });

        if (adapter != null)
            plvContent.setAdapter(adapter);

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

    /**
     * 编辑
     *
     * @param isVideoEdit
     * @param observerData
     */
    public void edit(boolean isVideoEdit, DelNumListener observerData) {
        try {
            adapter.setEdit(isVideoEdit);
            adapter.setSelListener(observerData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 全选
     *
     * @param selected
     */
    public void selAll(boolean selected, DelNumListener observerData) {
        if (selected) {
            //全选
            List<VideoListJson> data = adapter.getData();
            for (VideoListJson videoListJson : data) {
                videoListJson.setSel(true);
            }
            try {
                //设置选中数量
                observerData.delItem(data.size());
            } catch (Exception e) {
                e.printStackTrace();
                observerData.delItem(0);
            }
        } else {
            //取消全选
            List<VideoListJson> data = adapter.getData();
            for (VideoListJson videoListJson : data) {
                videoListJson.setSel(false);
            }
            //还原选中数量
            observerData.delItem(0);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 删除
     *
     * @param observerData
     */
    public void del(final DelNumListener observerData) {
        //获取选中的id
        List<VideoListJson> data = adapter.getData();
        String ids = "";
        for (VideoListJson videoListJson : data) {
            if (videoListJson.isSel()) {
                String id = videoListJson.getId();
                if (ids.equals("")) {
                    ids = id;
                } else {
                    ids += "," + id;
                }
            }
        }
        //选中非空判断
        if ("".equals(ids)) {
            ToastView.makeText3(getContext(), "请选中至少一项");
            return;
        }

        final String finalIds = ids;
        CollectUtils.unCollects(getContext(), VarConstant.COLLECT_TYPE_VIDEO, ids, new ObserverData() {
            @Override
            public void callback(Object o) {
                //删除取消收藏的数据
                adapter.removeById(finalIds);
                EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_COLLECT_VIDEO, null));
                //退出编辑状态
                quitEdit(observerData);
            }

            @Override
            public void onError(Exception e) {
                //退出编辑状态
                quitEdit(observerData);
            }
        });

    }

    /**
     * 退出编辑
     */
    public void quitEdit(DelNumListener observerData) {
        adapter.setEdit(false);
        List<VideoListJson> data = adapter.getData();
        //还原删除按钮数字
        if (observerData != null)
            observerData.delItem(0);
        //空数据处理
        if (data == null || data.size() == 0) {
            plRootView.setNullImgId(R.mipmap.icon_collect_null);
            plRootView.setNullText("");
            plRootView.loadEmptyData();
            return;
        }
        //还原选中状态
        for (VideoListJson videoListJson : data) {
            videoListJson.setSel(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getQueue().cancelAll(collectVideoPresenter.getClass().getName());
    }
}
