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
import com.jyh.kxt.base.dao.DBManager;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.trading.json.ViewPointTradeBean;
import com.jyh.kxt.trading.presenter.ArticleContentPresenter;
import com.jyh.kxt.trading.util.TradeHandlerUtil;
import com.jyh.kxt.user.adapter.CollectPointAdapter;
import com.jyh.kxt.user.presenter.CollectPointPresenter;
import com.library.bean.EventBusClass;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;
import com.library.widget.window.ToastView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.greendao.database.Database;

import java.util.Iterator;
import java.util.List;

import butterknife.BindView;

import static com.library.bean.EventBusClass.EVENT_VIEW_COLLECT_CANCEL1;

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


        articleContentPresenter = new ArticleContentPresenter(getContext());

        collectPointPresenter = new CollectPointPresenter(this);
        plRootView.loadWait();

        collectPointPresenter.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        collectPointPresenter.initData();
        EventBus.getDefault().register(this);
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
                collectPointPresenter.loadMore();
            }
        });
    }

    private String cancelId;

    @Subscribe
    public void onUpdateCollectState(EventBusClass eventBusClass) {
        if (eventBusClass.fromCode == EventBusClass.EVENT_VIEW_POINT_HANDLER || eventBusClass.fromCode == EVENT_VIEW_COLLECT_CANCEL1) {
            TradeHandlerUtil.EventHandlerBean intentObj = (TradeHandlerUtil.EventHandlerBean) eventBusClass.intentObj;
            List<ViewPointTradeBean> data = adapter.getData();

            for (ViewPointTradeBean viewPointTradeBean : data) {
                if (viewPointTradeBean.o_id.equals(intentObj.tradeId)) {
                    if (intentObj.collectState == 0) {
                        cancelId = intentObj.tradeId;
                    } else {
                        cancelId = null;
                    }
                }
            }

            if (eventBusClass.fromCode == EVENT_VIEW_COLLECT_CANCEL1) {
                onResume();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (cancelId != null) {
            List<ViewPointTradeBean> data = adapter.getData();
            Iterator<ViewPointTradeBean> iterator = data.iterator();
            while (iterator.hasNext()) {
                ViewPointTradeBean next = iterator.next();
                if (cancelId.contains(next.o_id)) {
                    iterator.remove();
                }
            }
            cancelId = null;
            adapter.notifyDataSetChanged();

            if (data.size() == 0) {
                plRootView.setNullImgId(R.mipmap.icon_collect_null);
                plRootView.setNullText(getString(R.string.error_collect_null));
                plRootView.loadEmptyData();
            }
        }
    }

    public void setCollectPointAdapter(CollectPointAdapter adapter) {
        this.adapter = adapter;
    }

    public void del(final DelNumListener observerData) {
        //获取选中的id
        final List<ViewPointTradeBean> data = adapter.getData();

        final StringBuffer objectIds = new StringBuffer();
        Iterator<ViewPointTradeBean> iteratorAdapter = data.iterator();
        while (iteratorAdapter.hasNext()) {
            ViewPointTradeBean next = iteratorAdapter.next();

            if (next.isSel) {
                objectIds.append(next.o_id + ",");
                iteratorAdapter.remove();
            }
        }
        //选中非空判断
        if ("".equals(objectIds.toString())) {
            ToastView.makeText3(getContext(), "请选中至少一项");
            return;
        }
        objectIds.deleteCharAt(objectIds.length() - 1);

        if (LoginUtils.isLogined(getContext())) {

            TradeHandlerUtil.getInstance().dels(getContext(), new ObserverData<Boolean>() {
                @Override
                public void callback(Boolean aBoolean) {
                    //本地的最新的收藏列表
                    delLocal(objectIds.toString(), observerData, data);
                }

                @Override
                public void onError(Exception e) {
                    observerData.delError();
                    quitEdit(observerData);//退出编辑状态
                }
            }, objectIds.toString());
        } else {
            delLocal(objectIds.toString(), observerData, data);
        }
    }

    /**
     * 本地删除
     *
     * @param idArrays
     * @param observerData
     * @param data
     */
    private void delLocal(String idArrays, DelNumListener observerData, List<ViewPointTradeBean> data) {

        DBManager mDBManager = DBManager.getInstance(getContext());
        Database database = mDBManager.getDaoSessionWrit().getDatabase();
        database.execSQL("DELETE FROM POINT_BEAN WHERE oId in (" + idArrays + ")");
        database.execSQL("UPDATE MARK_BEAN SET COLLECT_STATE = 0 where  O_ID in (" + idArrays + ")");

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
        if (adapter == null || adapter.getData() == null || adapter.getData().size() == 0) {
            return;
        }
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
            viewPointTradeBean.isSel = selected;
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
