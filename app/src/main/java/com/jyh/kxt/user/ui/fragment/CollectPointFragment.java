package com.jyh.kxt.user.ui.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ListView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.annotation.DelNumListener;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.utils.MarketConnectUtil;
import com.jyh.kxt.trading.json.ViewPointTradeBean;
import com.jyh.kxt.trading.presenter.ArticleContentPresenter;
import com.jyh.kxt.trading.util.TradeHandlerUtil;
import com.jyh.kxt.user.adapter.CollectPointAdapter;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.presenter.CollectPointPresenter;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;
import com.library.widget.window.ToastView;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import butterknife.BindView;

/**
 * 项目名:Kxt
 * 类描述:收藏-文章
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/2.
 */

public class CollectPointFragment extends BaseFragment implements PageLoadLayout.OnAfreshLoadListener {

    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;
    @BindView(R.id.ptrlv_content) public PullToRefreshListView mPullPinnedListView;

    public CollectPointAdapter adapter;

    public ArticleContentPresenter articleContentPresenter;
    private CollectPointPresenter collectPointPresenter;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_point_collect);
        initView();

        TradeHandlerUtil.getInstance().initTradeHandler(getContext());

        articleContentPresenter = new ArticleContentPresenter(getContext());

        collectPointPresenter = new CollectPointPresenter(this);
        plRootView.loadWait();

        collectPointPresenter.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        collectPointPresenter.initData();
    }

    private void initView() {
        ListView mRefreshableView = mPullPinnedListView.getRefreshableView();
        mRefreshableView.setDivider(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.line_background4)));
        mRefreshableView.setDividerHeight(0);
        mRefreshableView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mPullPinnedListView.setMode(PullToRefreshBase.Mode.BOTH);

        mPullPinnedListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                collectPointPresenter.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                collectPointPresenter.initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                collectPointPresenter.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                collectPointPresenter.initData();
            }
        });
    }


    public void setCollectPointAdapter(CollectPointAdapter adapter) {
        this.adapter = adapter;
    }

    public void del(final DelNumListener observerData) {

        final Set<String> sparseArray = new HashSet<>();
        //获取选中的id
        final List<ViewPointTradeBean> data = adapter.getData();

        String ids = "";
        Iterator<ViewPointTradeBean> iteratorAdapter = data.iterator();
        while (iteratorAdapter.hasNext()) {
            ViewPointTradeBean next = iteratorAdapter.next();

            if (next.isSel()) {
                sparseArray.add(next.o_id);
                ids += next.o_id + ",";

                iteratorAdapter.remove();
            }
        }
        //选中非空判断
        if ("".equals(ids)) {
            ToastView.makeText3(getContext(), "请选中至少一项");
            return;
        }

        if (LoginUtils.isLogined(getContext())) {

            TradeHandlerUtil.getInstance().dels(getContext(), new ObserverData<Boolean>() {
                @Override
                public void callback(Boolean aBoolean) {
                    //本地的最新的收藏列表
                    delLocal(sparseArray, observerData, data);
                }

                @Override
                public void onError(Exception e) {
                    observerData.delError();
                    quitEdit(observerData);//退出编辑状态
                }
            }, ids);
        } else {
            delLocal(sparseArray, observerData, data);
        }
    }

    /**
     * 本地删除
     *
     * @param sparseArray
     * @param observerData
     * @param data
     */
    private void delLocal(Set<String> sparseArray, DelNumListener observerData, List<ViewPointTradeBean> data) {
        List<ViewPointTradeBean> localityCollectList = TradeHandlerUtil.getInstance().getLocalityCollectList(getContext(), 0,
                Integer
                        .MAX_VALUE);
        Iterator<ViewPointTradeBean> iterator = localityCollectList.iterator();

        while (iterator.hasNext()) {
            ViewPointTradeBean next = iterator.next();

            if (sparseArray.contains(next.o_id)) {
                TradeHandlerUtil.getInstance().updateCollect(getContext(), next.o_id, false);
                iterator.remove();
            }
        }
        TradeHandlerUtil.getInstance().clearCollectBean(getContext());
        TradeHandlerUtil.getInstance().saveCollectList(getContext(), localityCollectList);

        observerData.delSuccessed();
        quitEdit(observerData);//退出编辑状态

        if (data.size() == 0) {
            plRootView.setNullImgId(R.mipmap.icon_collect_null);
            plRootView.setNullText(getString(R.string.error_collect_null));
            plRootView.loadEmptyData();
        }
    }

    public void quitEdit(DelNumListener observerData) {
        adapter.setEdit(false);
        selAll(false, observerData);
        if (observerData != null) {
            observerData.delItem(0);
            observerData.quitEdit();
        }
    }

    public void edit(boolean isNewsEdit, DelNumListener observerData) {
        adapter.setDelNumListener(observerData);
        try {
            adapter.setEdit(isNewsEdit);
            if (isNewsEdit) {
                mPullPinnedListView.setMode(PullToRefreshBase.Mode.DISABLED);
            } else {
                mPullPinnedListView.setMode(PullToRefreshBase.Mode.BOTH);
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selAll(boolean selected, DelNumListener observerData) {
        List<ViewPointTradeBean> data = adapter.getData();
        for (ViewPointTradeBean viewPointTradeBean : data) {
            viewPointTradeBean.setSel(selected);
        }
        adapter.setDelNumListener(observerData);

        if (selected) {
            adapter.selectItemCount = data.size();
            observerData.delItem(data.size());
        } else {
            adapter.selectItemCount = 0;
            observerData.delItem(0);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void OnAfreshLoad() {

    }

}
