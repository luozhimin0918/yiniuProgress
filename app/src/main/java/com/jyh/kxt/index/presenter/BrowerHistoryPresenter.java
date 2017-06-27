package com.jyh.kxt.index.presenter;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.util.PopupUtil;
import com.jyh.kxt.base.utils.BrowerHistoryUtils;
import com.jyh.kxt.index.adapter.BrowerHistoryAdapter;
import com.jyh.kxt.index.ui.BrowerHistoryActivity;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.main.widget.FastInfoPinnedListView;
import com.library.base.http.VarConstant;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.window.ToastView;

import java.util.ArrayList;
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
    private PopupUtil popupWindow;

    List<List<NewsJson>> lists = new ArrayList<>();
    private int count = 0;//一共有几页数据
    private int current = 0;//当前页码
    private List<List<NewsJson>> copyList;

    public BrowerHistoryPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    /**
     * 清除记录
     *
     * @param view
     */
    public void clear(View view) {
        if (adapter != null && adapter.dataList != null && adapter.dataList.size() > 0) {
            //有数据可点击删除,无数据时不做处理
            if (popupWindow == null) {
                popupWindow = new PopupUtil((Activity) mContext);
                View rootView = popupWindow.createPopupView(R.layout.pop_brower_clear);

                TextView tvCancel = (TextView) rootView.findViewById(R.id.tv_cancel);
                TextView tvSure = (TextView) rootView.findViewById(R.id.tv_sure);
                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });
                tvSure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BrowerHistoryUtils.clear(mContext);
                        if (adapter != null) {
                            adapter.setData(null);
                            adapter.notifyDataSetChanged();
                            browerHistoryActivity.loadEmptyData();
                        }
                        popupWindow.dismiss();
                    }
                });

                PopupUtil.Config config = new PopupUtil.Config();
                config.outsideTouchable = true;
                config.alpha = 0.5f;
                config.bgColor = 0X00000000;

                config.animationStyle = R.style.PopupWindow_Style2;
                config.width = WindowManager.LayoutParams.MATCH_PARENT;
                config.height = WindowManager.LayoutParams.MATCH_PARENT;

                popupWindow.setConfig(config);

            }
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        }
    }

    /**
     * 初始化数据
     */
    public void initData() {

        browerHistoryActivity.plRootView.loadWait();
        current = 0;
        try {
            List<NewsJson> history = BrowerHistoryUtils.getHistory(mContext);
            if (history == null || history.size() == 0) {
                browerHistoryActivity.loadEmptyData();
            } else {
                int size = history.size();
                lists.clear();
                if (size > VarConstant.LIST_MAX_SIZE) {
                    count = size / VarConstant.LIST_MAX_SIZE;
                    for (int i = 0; i < count; i++) {
                        int fromIndex = i * VarConstant.LIST_MAX_SIZE;
                        int toIndex = fromIndex + VarConstant.LIST_MAX_SIZE - 1;
                        int i1 = size - 1;
                        if (toIndex > i1) {
                            toIndex = i1;
                        }
                        lists.add(history.subList(fromIndex, toIndex));
                    }
                } else {
                    lists.add(history);
                }

                copyList = new ArrayList<>();
                for (List<NewsJson> list : lists) {
                    List<NewsJson> newsJsons = new ArrayList<>();
                    for (NewsJson newsJson : list) {
                        newsJsons.add(newsJson);
                    }
                    copyList.add(newsJsons);
                }

                if (adapter == null) {
                    adapter = new BrowerHistoryAdapter(mContext, lists.get(0));
                    browerHistoryActivity.lvContent.setAdapter(adapter);
                } else {
                    adapter.setData(lists.get(0));
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
        current++;
        if (current >= count) {
            ToastView.makeText3(mContext, mContext.getString(R.string.no_data));
            browerHistoryActivity.lvContent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        browerHistoryActivity.lvContent.getRefreshableView().goneFoot2();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 500);
            return;
        }

        lists.set(current, copyList.get(current));

        adapter.setData(lists.get(current));
        browerHistoryActivity.lvContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    browerHistoryActivity.lvContent.getRefreshableView().goneFoot2();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 500);
    }

    @Override
    public void OnAfreshLoad() {
        initData();
    }

    @Override
    public void onPullDownToRefresh(final PullToRefreshBase refreshView) {
        current = 0;
        lists.set(0, copyList.get(0));
        adapter.setData(lists.get(0));
        refreshView.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    refreshView.onRefreshComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 500);
    }

    @Override
    public void onPullUpToRefresh(final PullToRefreshBase refreshView) {

    }
}
