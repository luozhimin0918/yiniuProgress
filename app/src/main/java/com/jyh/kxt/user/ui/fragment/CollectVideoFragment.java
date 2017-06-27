package com.jyh.kxt.user.ui.fragment;

import android.content.Intent;
import android.net.sip.SipRegistrationListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.jyh.kxt.R;
import com.jyh.kxt.av.json.VideoListJson;
import com.jyh.kxt.av.ui.VideoDetailActivity;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.annotation.DelNumListener;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.utils.collect.CollectUtils;
import com.jyh.kxt.main.json.NewsJson;
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
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Set;

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
    private boolean isRefresh;
    private DelNumListener delNumListener;

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
                try {
                    VideoListJson video = adapter.getData().get(position - 1);
                    if (adapter.isEdit()) {
                        adapter.delClick((CollectVideoAdapter.ViewHolder) view.getTag(), video);
                        if (delNumListener != null) {
                            int delSize = adapter.getDelIds().size();
                            int allSize = adapter.getData().size();
                            delNumListener.delItem(delSize);
                            delNumListener.delAll(delSize == allSize);
                        }
                    } else {
                        Intent videoIntent = new Intent(getContext(), VideoDetailActivity.class);
                        videoIntent.putExtra(IntentConstant.O_ID, video.getId());
                        startActivity(videoIntent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
            delNumListener = observerData;
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
            Set<String> delIds = adapter.getDelIds();
            for (VideoListJson videoListJson : data) {
                videoListJson.setSel(true);
                delIds.add(videoListJson.getId());
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
            Set<String> delIds = adapter.getDelIds();
            for (VideoListJson videoListJson : data) {
                videoListJson.setSel(false);
                String id = videoListJson.getId();
                if (delIds.contains(id))
                    delIds.remove(id);
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
        CollectUtils.unCollects(getContext(), VarConstant.COLLECT_TYPE_VIDEO, "", ids, new ObserverData() {
            @Override
            public void callback(Object o) {
                //删除取消收藏的数据
                adapter.removeById(finalIds);
                observerData.delSuccessed();
                EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_COLLECT_VIDEO, null));
                //退出编辑状态
                quitEdit(observerData);
            }

            @Override
            public void onError(Exception e) {
                //退出编辑状态
                observerData.delError();
                quitEdit(observerData);
            }
        });

    }

    /**
     * 退出编辑
     */
    public void quitEdit(DelNumListener observerData) {
        try {
            adapter.setEdit(false);
            List<VideoListJson> data = adapter.getData();
            //还原删除按钮数字
            if (observerData != null) {
                observerData.delItem(0);
                observerData.quitEdit();
            }
            //空数据处理
            if (data == null || data.size() == 0) {
                plRootView.setNullImgId(R.mipmap.icon_collect_null);
                plRootView.setNullText(getString(R.string.error_collect_null));
                plRootView.loadEmptyData();
                return;
            }
            //还原选中状态
            for (VideoListJson videoListJson : data) {
                videoListJson.setSel(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        getQueue().cancelAll(collectVideoPresenter.getClass().getName());
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
    public void onChangeTheme() {
        super.onChangeTheme();
        if (adapter != null)
            adapter.notifyDataSetChanged();
        if (plvContent != null)
            plvContent.setDividerNull();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusClass eventBus) {
        if (eventBus.fromCode == EventBusClass.EVENT_COLLECT_VIDEO) {
            if (eventBus.fromCode == EventBusClass.EVENT_COLLECT_FLASH) {
                VideoListJson flash = (VideoListJson) eventBus.intentObj;
                List<VideoListJson> data = adapter.getData();
                for (VideoListJson flashJson : data) {
                    if (flash.getId().equals(flashJson.getId())) {
                        adapter.removeById(flashJson.getId());
                        adapter.notifyDataSetChanged();
                        List<VideoListJson> data1 = adapter.getData();
                        if (data1 == null || data1.size() == 0) {
                            plRootView.setNullImgId(R.mipmap.icon_collect_null);
                            plRootView.setNullText(getString(R.string.error_collect_null));
                            plRootView.loadEmptyData();
                        }
                        return;
                    }
                }
                collectVideoPresenter.refresh();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isRefresh) {
            isRefresh = false;
            collectVideoPresenter.refresh();
        }
    }
}
