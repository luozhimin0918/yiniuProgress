package com.jyh.kxt.user.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.alibaba.fastjson.JSON;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.annotation.DelNumListener;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.base.utils.collect.CollectUtils;
import com.jyh.kxt.main.json.flash.FlashJson;
import com.jyh.kxt.main.json.flash.Flash_NEWS;
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
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Set;

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
    private DelNumListener delNumListener;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_flash);

        collectFlashPresenter = new CollectFlashPresenter(this);

        plRootView.setOnAfreshLoadListener(this);
        FastInfoPinnedListView refreshableView = lvContent.getRefreshableView();
        refreshableView.addFooterListener(this);
        lvContent.setOnRefreshListener(this);
        lvContent.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        if (adapter != null)
            lvContent.setAdapter(adapter);
        lvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    List source = adapter.getSource();
                    Object obj = source.get(position - 1);
                    if (obj != null && obj instanceof FlashJson) {
                        FlashJson flashJson = (FlashJson) obj;

                        if (adapter.isEdit()) {
                            adapter.delClick(((CollectFlashAdapter.BaseBaseViewHolder) view.getTag()).ivDel, flashJson);
                            if (delNumListener != null) {
                                int delSize = adapter.getDelIds().size();
                                int allSize = adapter.getData().size();
                                delNumListener.delItem(delSize);
                                delNumListener.delAll(delSize == allSize);
                            }
                        } else {
                            String type = flashJson.getCode();
                            String content = flashJson.getContent();

                            CollectActivity ac = (CollectActivity) getActivity();

                            switch (type) {
                                case VarConstant.SOCKET_FLASH_KUAIXUN:
                                case VarConstant.SOCKET_FLASH_CJRL:
                                    JumpUtils.jump(ac, VarConstant.OCLASS_FLASH, VarConstant.OACTION_DETAIL, flashJson.getUid(), null);
                                    break;
                                case VarConstant.SOCKET_FLASH_KXTNEWS:
                                    Flash_NEWS flash_news = JSON.parseObject(content, Flash_NEWS.class);
                                    Flash_NEWS.Jump url = flash_news.getUrl();
                                    JumpUtils.jump(ac, url.getC(), VarConstant.OACTION_DETAIL, url.getI(), url.getU());
                                    break;
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

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
            delNumListener = observerData;
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
            Set<String> delIds = adapter.getDelIds();
            int size = 0;
            for (Object flash : data) {
                if (flash instanceof FlashJson) {
                    FlashJson flashJson = (FlashJson) flash;
                    flashJson.setSel(true);
                    delIds.add(flashJson.getSocre());
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
            Set<String> delIds = adapter.getDelIds();
            for (Object flash : data) {
                if (flash instanceof FlashJson) {
                    FlashJson flashJson = (FlashJson) flash;
                    flashJson.setSel(false);
                    String socre = flashJson.getSocre();
                    if (delIds.contains(socre)) {
                        delIds.remove(socre);
                    }
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
        CollectUtils.unCollects(getContext(), VarConstant.COLLECT_TYPE_FLASH, "", ids, new ObserverData() {
            @Override
            public void callback(Object o) {
                //删除取消收藏的数据
                adapter.removeById(finalIds);
                observerData.delSuccessed();
                EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_COLLECT_FLASH, null));
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
     *
     * @param observerData
     */
    public void quitEdit(DelNumListener observerData) {
        try {
            adapter.setEdit(false);
            List souces = adapter.getSoucesData();
            List data = adapter.getData();
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
            for (Object flash : souces) {
                if (flash instanceof FlashJson)
                    ((FlashJson) flash).setSel(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                try {
                    lvContent.getRefreshableView().goneFoot2();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusClass eventBus) {
        if (eventBus.fromCode == EventBusClass.EVENT_COLLECT_FLASH) {
            FlashJson flash = (FlashJson) eventBus.intentObj;
            List<FlashJson> data = adapter.getData();
            for (FlashJson flashJson : data) {
                if (flash.getSocre().equals(flashJson.getSocre())) {
                    adapter.removeById(flashJson.getSocre());
                    adapter.notifyDataSetChanged();
                    List<FlashJson> data1 = adapter.getData();
                    if (data1 == null || data1.size() == 0) {
                        plRootView.setNullImgId(R.mipmap.icon_collect_null);
                        plRootView.setNullText(getString(R.string.error_collect_null));
                        plRootView.loadEmptyData();
                    }
                    return;
                }
            }
            collectFlashPresenter.refresh();
        }
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
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
