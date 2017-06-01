package com.jyh.kxt.market.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.utils.MarketUtil;
import com.jyh.kxt.base.widget.helper.OnStartDragListener;
import com.jyh.kxt.base.widget.helper.SimpleItemTouchHelperCallback;
import com.jyh.kxt.market.adapter.MarketEditAdapter;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.bean.EventBusClass;
import com.library.util.SystemUtil;
import com.library.widget.PageLoadLayout;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MarketEditActivity extends BaseActivity implements OnStartDragListener {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;

    @BindView(R.id.pll_content) PageLoadLayout pllContent;
    @BindView(R.id.rv_content) RecyclerView rvContent;

    @BindView(R.id.tv_delete_count) public TextView tvSelectedCount;
    @BindView(R.id.cb_complete_checked) public CheckBox cbCompleteChecked;

    private ItemTouchHelper mItemTouchHelper;
    private MarketEditAdapter marketEditAdapter;

    private List<MarketItemBean> defaultInitMarketList = new ArrayList<>();
    private List<MarketItemBean> adapterMarketItemList = new ArrayList<>();


    @OnClick({R.id.cb_complete_checked, R.id.tv_delete_count, R.id.iv_bar_break, R.id.iv_bar_function})
    public void onBottomNavClick(View view) {
        switch (view.getId()) {
            case R.id.cb_complete_checked:
                CheckBox completeChecked = (CheckBox) view;
                try {
                    marketEditAdapter.completeChecked(completeChecked.isChecked());
                } catch (Exception e) {
                    e.printStackTrace();
                    completeChecked.setChecked(false);
                }
                break;
            case R.id.tv_delete_count:
                try {
                    List<MarketItemBean> checkedList = marketEditAdapter.getCheckedList();
                    for (int i = 0; i < checkedList.size(); i++) {
                        MarketItemBean checkedPosition = checkedList.get(i);
                        int indexOf = marketEditAdapter.listContent.indexOf(checkedPosition);
                        marketEditAdapter.onItemDismiss(indexOf);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.iv_bar_function:
                onSaveAndExit();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_edit, StatusBarColor.THEME1);

        tvBarTitle.setText("编辑");
        ivBarFunction.setVisibility(View.GONE);

        UserJson userInfo = LoginUtils.getUserInfo(this);
        adapterMarketItemList.addAll(MarketUtil.getMarketEditOption(getContext()));
        defaultInitMarketList.addAll(adapterMarketItemList);

        if (userInfo == null) {
            initEditInfo();
        } else {
            requestSynchronization(userInfo);
        }
    }

    private void initEditInfo() {
        try {
            marketEditAdapter = new MarketEditAdapter(this, this, adapterMarketItemList, pllContent);

            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(marketEditAdapter);
            mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(rvContent);

            rvContent.setHasFixedSize(true);
            rvContent.setAdapter(marketEditAdapter);
            rvContent.setLayoutManager(new LinearLayoutManager(this));
        } catch (Exception e) {
            pllContent.loadEmptyData();
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onBackPressed() {
        if (defaultInitMarketList.size() != adapterMarketItemList.size()) {
            openTipWindow();
            return;
        }
        for (int i = 0; i < defaultInitMarketList.size(); i++) {
            if (defaultInitMarketList.get(i) != adapterMarketItemList.get(i)) {
                openTipWindow();
                return;
            }
        }
        super.onBackPressed();
    }

    private void openTipWindow() {
        onSaveAndExit();
      /*  new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("自选数据发生改变,是否保存并且退出?")
                .setPositiveButton("保存并退出",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                .setNegativeButton("直接退出",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MarketEditActivity.this.finish();
                            }
                        }).show();*/
    }

    private void onSaveAndExit() {
        UserJson userInfo = LoginUtils.getUserInfo(this);
        if (userInfo != null) {
            requestRefresh(userInfo);
        } else {
            postEventBean();
        }
    }


    private void postEventBean() {
        MarketUtil.saveMarketEditOption(getContext(), adapterMarketItemList, 2);

        EventBusClass eventBusClass = new EventBusClass(
                EventBusClass.MARKET_OPTION_UPDATE,
                adapterMarketItemList);
        EventBus.getDefault().post(eventBusClass);
        finish();
    }

    private void requestSynchronization(UserJson userInfo) {
        pllContent.loadWait();

        VolleyRequest volleyRequest = new VolleyRequest(getContext(), getQueue());
        JSONObject jsonParam = volleyRequest.getJsonParam();
        jsonParam.put("uid", userInfo.getUid());
        jsonParam.put("accessToken", userInfo.getToken());

        volleyRequest.doPost(HttpConstant.QUOTES_FAVOR, jsonParam, new HttpListener<List<MarketItemBean>>() {
            @Override
            protected void onResponse(List<MarketItemBean> marketItemBeen) {
                pllContent.loadOver();

                adapterMarketItemList.clear();
                adapterMarketItemList.addAll(MarketUtil.getMergeLocalMarket(getContext(), marketItemBeen));
                defaultInitMarketList.addAll(adapterMarketItemList);
                initEditInfo();
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);

                if (error != null) {
                    if (adapterMarketItemList.size() == 0) {
                        pllContent.loadEmptyData();
                    } else {
                        pllContent.loadOver();
                        initEditInfo();
                    }
                } else {
                    pllContent.loadEmptyData();
                }
            }
        });
    }

    /**
     * 请求刷新同步
     */
    private void requestRefresh(final UserJson userInfo) {

        final TSnackbar snackBar = TSnackbar.make(tvBarTitle, "同步中...", TSnackbar.LENGTH_INDEFINITE, TSnackbar
                .APPEAR_FROM_TOP_TO_DOWN);
        snackBar.setPromptThemBackground(Prompt.SUCCESS);
        snackBar.addIconProgressLoading(0, true, false);
        snackBar.show();

        JSONArray jsonArray = new JSONArray();
        for (MarketItemBean marketItemBean : adapterMarketItemList) {
            jsonArray.add(marketItemBean.getCode());
        }

        VolleyRequest volleyRequest = new VolleyRequest(getContext(), getQueue());
        JSONObject jsonParam = volleyRequest.getJsonParam();
        jsonParam.put("uid", userInfo.getUid());
        jsonParam.put("accessToken", userInfo.getToken());
        jsonParam.put("codes", jsonArray);

        volleyRequest.doPost(HttpConstant.QUOTES_SORT, jsonParam, new HttpListener<String>() {
            @Override
            protected void onResponse(String sort) {
                snackBar.setPromptThemBackground(Prompt.SUCCESS).setText("同步成功").setDuration(TSnackbar
                        .LENGTH_LONG)
                        .setMinHeight(SystemUtil.getStatuBarHeight(getContext()), getResources()
                                .getDimensionPixelOffset(R.dimen.actionbar_height)).show();
                postEventBean();
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                postEventBean();
            }
        });
    }
}
