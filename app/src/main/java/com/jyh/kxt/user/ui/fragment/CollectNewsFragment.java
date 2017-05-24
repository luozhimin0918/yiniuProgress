package com.jyh.kxt.user.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.annotation.DelNumListener;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.base.utils.collect.CollectUtils;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.user.adapter.EditNewsAdapter;
import com.jyh.kxt.user.presenter.CollectNewsPresenter;
import com.jyh.kxt.user.ui.CollectActivity;
import com.library.base.http.VarConstant;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;
import com.library.widget.window.ToastView;

import java.util.List;

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
                NewsJson newsJson = adapter.dataList.get(position - 1);

                JumpUtils.jumpDetails(getActivity(), newsJson.getO_class(), newsJson.getO_id(), newsJson.getHref());
//                //保存浏览记录
//                BrowerHistoryUtils.save(getContext(), newsJson);
//
//                //单条刷新,改变浏览状态
//                adapter.getView(position, view, parent);
            }
        });

        if (adapter != null)
            plvContent.setAdapter(adapter);

        collectNewsPresenter.initData();
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
        collectNewsPresenter.initData();
    }

    /**
     * 编辑
     *
     * @param isNewsEdit
     * @param observerData
     */
    public void edit(boolean isNewsEdit, DelNumListener observerData) {
        try {
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
            for (NewsJson newsJson : data) {
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
            for (NewsJson newsJson : data) {
                newsJson.setSel(false);
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
        CollectUtils.unCollects(getContext(), VarConstant.COLLECT_TYPE_ARTICLE, ids, new ObserverData() {
            @Override
            public void callback(Object o) {
                //删除取消收藏的数据
                adapter.removeById(finalIds);
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
        List<NewsJson> data = adapter.getData();
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
        for (NewsJson newsJson : data) {
            newsJson.setSel(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getQueue().cancelAll(collectNewsPresenter.getClass().getName());
    }
}
