package com.jyh.kxt.user.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.jyh.kxt.R;
import com.jyh.kxt.av.json.VideoListJson;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.annotation.DelNumListener;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.utils.collect.CollectUtils;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.main.json.flash.FlashJson;
import com.jyh.kxt.main.widget.FastInfoPinnedListView;
import com.jyh.kxt.main.widget.FastInfoPullPinnedListView;
import com.jyh.kxt.user.adapter.CollectFlashAdapter;
import com.jyh.kxt.user.presenter.CollectFlashPresenter;
import com.jyh.kxt.user.ui.CollectActivity;
import com.library.base.http.VarConstant;
import com.library.bean.EventBusClass;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.window.ToastView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;

/**
 * 项目名:Kxt
 * 类描述:收藏-快讯
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/2.
 */

public class CollectFlashFragment extends BaseFragment implements FastInfoPinnedListView.FooterListener, PullToRefreshBase
        .OnRefreshListener2, PageLoadLayout.OnAfreshLoadListener {

    @BindView(R.id.lv_content) public FastInfoPullPinnedListView lvContent;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;
    public CollectFlashAdapter adapter;

    private CollectFlashPresenter collectFlashPresenter;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_flash);

        collectFlashPresenter = new CollectFlashPresenter(this);

        plRootView.setOnAfreshLoadListener(this);
        FastInfoPinnedListView refreshableView = lvContent.getRefreshableView();
        refreshableView.addFooterListener(this);
        lvContent.setOnRefreshListener(this);
        lvContent.setMode(PullToRefreshBase.Mode.DISABLED);

        if (adapter != null)
            lvContent.setAdapter(adapter);

        collectFlashPresenter.init();
    }

    /**
     * 编辑
     *
     * @param isFlashEdit
     * @param observerData
     */
    public void edit(boolean isFlashEdit, DelNumListener observerData) {
        try {
            adapter.setEdit(isFlashEdit);
            adapter.setObserverData(observerData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 全选
     *
     * @param selected
     * @param observerData
     */
    public void selAll(boolean selected, DelNumListener observerData) {
        if (selected) {
            //全选
            List data = adapter.getSoucesData();
            int size = 0;
            for (Object flash : data) {
                if (flash instanceof FlashJson) {
                    FlashJson flashJson = (FlashJson) flash;
                    flashJson.setSel(true);
                    size++;
                }
            }
            try {
                //设置选中数量
                observerData.delItem(size);
            } catch (Exception e) {
                e.printStackTrace();
                observerData.delItem(0);
            }
        } else {
            //取消全选
            List data = adapter.getSoucesData();
            for (Object flash : data) {
                if (flash instanceof FlashJson) {
                    FlashJson flashJson = (FlashJson) flash;
                    flashJson.setSel(true);
                }
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
        List data = adapter.getSoucesData();
        String ids = "";
        for (Object flash : data) {
            if (flash instanceof FlashJson) {
                FlashJson flashJson = (FlashJson) flash;
                if (flashJson.isSel()) {
                    String id = flashJson.getSocre();
                    if (ids.equals("")) {
                        ids = id;
                    } else {
                        ids += "," + id;
                    }
                }
            }
        }
        //选中非空判断
        if ("".equals(ids)) {
            ToastView.makeText3(getContext(), "请选中至少一项");
            return;
        }

        final String finalIds = ids;
        CollectUtils.unCollects(getContext(), VarConstant.COLLECT_TYPE_FLASH, ids, new ObserverData() {
            @Override
            public void callback(Object o) {
                //删除取消收藏的数据
                adapter.removeById(finalIds);
                EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_COLLECT_FLASH, null));
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
     *
     * @param observerData
     */
    public void quitEdit(DelNumListener observerData) {
        adapter.setEdit(false);
        List souces = adapter.getSoucesData();
        List data = adapter.getData();
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
        for (Object flash : souces) {
            if (flash instanceof FlashJson)
                ((FlashJson) flash).setSel(false);
        }
    }

    @Override
    public void startLoadMore() {
        collectFlashPresenter.loadMore();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        collectFlashPresenter.refresh();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

    }

    @Override
    public void OnAfreshLoad() {
        collectFlashPresenter.init();
    }

    public void init(List<FlashJson> flashJsons) {
        if (adapter == null) {
            adapter = new CollectFlashAdapter(flashJsons, getContext());
            lvContent.setAdapter(adapter);
        } else
            adapter.setData(flashJsons);
    }

    public void loadMore(List<FlashJson> newsMore) {
        adapter.addData(newsMore);
        lvContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                lvContent.onRefreshComplete();
            }
        }, 500);
    }

    public void refresh(List<FlashJson> adapterSourceList) {
        adapter.setData(adapterSourceList);
        lvContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                lvContent.onRefreshComplete();
            }
        }, 500);
    }

    @Override
    public void onChangeTheme() {
        super.onChangeTheme();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
