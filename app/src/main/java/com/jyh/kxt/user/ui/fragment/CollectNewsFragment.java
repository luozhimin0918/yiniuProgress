package com.jyh.kxt.user.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.annotation.DelNumListener;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.base.utils.collect.CollectUtils;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.user.adapter.EditNewsAdapter;
import com.jyh.kxt.user.presenter.CollectNewsPresenter;
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
 * 类描述:收藏-文章
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/2.
 */

public class CollectNewsFragment extends BaseFragment implements PageLoadLayout.OnAfreshLoadListener, PullToRefreshBase.OnRefreshListener2 {

    @BindView(R.id.plv_content) public PullToRefreshListView plvContent;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;

    private CollectNewsPresenter collectNewsPresenter;

    public EditNewsAdapter adapter;
    private String type;
    private DelNumListener delNumListener;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_news_item);

        collectNewsPresenter = new CollectNewsPresenter(this);

        plRootView.setOnAfreshLoadListener(this);
        plvContent.setOnRefreshListener(this);
        plvContent.setDividerNull();
        plvContent.setMode(PullToRefreshBase.Mode.BOTH);

        plvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    NewsJson newsJson = adapter.dataList.get(position - 1);
                    if (adapter.isEdit()) {
                        adapter.delClick((EditNewsAdapter.ViewHolder) view.getTag(), newsJson);
                        if (delNumListener != null) {
                            int delSize = adapter.getDelIds().size();
                            int allSize = adapter.dataList.size();
                            delNumListener.delItem(delSize);
                            delNumListener.delAll(delSize == allSize);
                        }
                    } else {
                        JumpUtils.jump((BaseActivity) getActivity(), newsJson.getO_class(), newsJson.getO_action(), newsJson.getO_id(),
                                newsJson
                                        .getHref());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (adapter != null)
            plvContent.setAdapter(adapter);

        type = getArguments().getString(IntentConstant.TYPE);
        collectNewsPresenter.initData(type);
    }

    /**
     * 初始化数据
     *
     * @param adapterSourceList
     */
    public void initData(List<NewsJson> adapterSourceList) {
        if (adapter == null) {
            adapter = new EditNewsAdapter(adapterSourceList, getContext());
            plvContent.setAdapter(adapter);
        } else {
            adapter.setData(adapterSourceList);
        }
    }

    /**
     * 刷新
     *
     * @param adapterSourceList
     */
    public void refresh(List<NewsJson> adapterSourceList) {
        adapter.setData(adapterSourceList);
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
     * @param newsMore
     */
    public void loadMore(List<NewsJson> newsMore) {
        adapter.addData(newsMore);
        plvContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                plvContent.onRefreshComplete();
            }
        }, 500);
    }

    @Override
    public void onPullDownToRefresh(final PullToRefreshBase refreshView) {
        collectNewsPresenter.refresh();
    }

    @Override
    public void onPullUpToRefresh(final PullToRefreshBase refreshView) {
        collectNewsPresenter.loadMore();
    }

    @Override
    public void OnAfreshLoad() {
        collectNewsPresenter.initData(type);
    }

    /**
     * 编辑
     *
     * @param isNewsEdit
     * @param observerData
     */
    public void edit(boolean isNewsEdit, DelNumListener observerData) {
        try {
            this.delNumListener = observerData;
            adapter.setEdit(isNewsEdit);
            adapter.setSelListener(observerData);
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
            List<NewsJson> data = adapter.getData();
            Set<String> delIds = adapter.getDelIds();
            for (NewsJson newsJson : data) {
                delIds.add(newsJson.getO_id());
                newsJson.setSel(true);
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
            List<NewsJson> data = adapter.getData();
            Set<String> delIds = adapter.getDelIds();
            for (NewsJson newsJson : data) {
                newsJson.setSel(false);
                String o_id = newsJson.getO_id();
                if (delIds.contains(o_id))
                    delIds.remove(o_id);

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
        List<NewsJson> data = adapter.getData();
        String ids = "";
        for (NewsJson newsJson : data) {
            if (newsJson.isSel()) {
                String id = newsJson.getO_id();
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
        CollectUtils.unCollects(getContext(), VarConstant.COLLECT_TYPE_ARTICLE, type, ids, new ObserverData() {
            @Override
            public void callback(Object o) {
                //删除取消收藏的数据
                adapter.removeById(finalIds);
                observerData.delSuccessed();
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
            List<NewsJson> data = adapter.getData();
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
            for (NewsJson newsJson : data) {
                newsJson.setSel(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        getQueue().cancelAll(CollectNewsPresenter.class.getName());
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onChangeTheme() {
        super.onChangeTheme();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        if (plvContent != null)
            plvContent.setDividerNull();
    }

    @Override
    public void onDetach() {
        super.onDetach();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusClass eventBus) {
        if (eventBus.fromCode == EventBusClass.EVENT_COLLECT_NEWS) {
            if (eventBus.fromCode == EventBusClass.EVENT_COLLECT_FLASH) {
                NewsJson flash = (NewsJson) eventBus.intentObj;
                List<NewsJson> data = adapter.getData();
                for (NewsJson flashJson : data) {
                    if (flash.getO_id().equals(flashJson.getO_id())) {
                        adapter.removeById(flashJson.getO_id());
                        adapter.notifyDataSetChanged();
                        List<NewsJson> data1 = adapter.getData();
                        if (data1 == null || data1.size() == 0) {
                            plRootView.setNullImgId(R.mipmap.icon_collect_null);
                            plRootView.setNullText(getString(R.string.error_collect_null));
                            plRootView.loadEmptyData();
                        }
                        return;
                    }
                }
                collectNewsPresenter.refresh();
            }
        }

    }
}
