package com.jyh.kxt.index.presenter;

import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.utils.BrowerHistoryUtils;
import com.jyh.kxt.index.adapter.BrowerHistoryAdapter;
import com.jyh.kxt.index.ui.BrowerHistoryActivity;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.main.widget.FastInfoPinnedListView;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;

import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/4.
 */

public class BrowerHistoryPresenter extends BasePresenter implements FastInfoPinnedListView.FooterListener, PageLoadLayout
        .OnAfreshLoadListener, PullToRefreshBase.OnRefreshListener2 {

    @BindObject BrowerHistoryActivity browerHistoryActivity;

    public BrowerHistoryAdapter adapter;

    public BrowerHistoryPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    /**
     * 清除记录
     */
    public void clear() {
        BrowerHistoryUtils.clear(mContext);
        if (adapter != null) {
            adapter.setData(null);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 初始化数据
     */
    public void initData() {

        browerHistoryActivity.plRootView.loadWait();

        try {
            List<NewsJson> history = BrowerHistoryUtils.getHistory(mContext);
            if (history == null || history.size() == 0) {
                browerHistoryActivity.loadEmptyData();
            } else {
                if (adapter == null) {
                    adapter = new BrowerHistoryAdapter(mContext, history);
                    browerHistoryActivity.lvContent.setAdapter(adapter);
                } else {
                    adapter.setData(history);
                }
                browerHistoryActivity.plRootView.loadOver();
            }
        } catch (Exception e) {
            e.printStackTrace();
            browerHistoryActivity.loadEmptyData();
        }
    }

    @Override
    public void startLoadMore() {

    }

    @Override
    public void OnAfreshLoad() {

    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

    }
}
