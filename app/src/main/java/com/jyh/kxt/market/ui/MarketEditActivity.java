package com.jyh.kxt.market.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import com.jyh.kxt.index.json.SingleThreadJson;
import com.jyh.kxt.market.adapter.MarketEditAdapter;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.base.http.VolleySyncHttp;
import com.library.bean.EventBusClass;
import com.library.widget.PageLoadLayout;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
    private List<MarketItemBean> adapterMarketItemList;


    @OnClick({R.id.cb_complete_checked, R.id.tv_delete_count, R.id.iv_bar_break, R.id.iv_bar_function})
    public void onBottomNavClick(View view) {
        switch (view.getId()) {
            case R.id.cb_complete_checked:
                CheckBox completeChecked = (CheckBox) view;
                marketEditAdapter.completeChecked(completeChecked.isChecked());
                break;
            case R.id.tv_delete_count:
                List<MarketItemBean> checkedList = marketEditAdapter.getCheckedList();
                for (int i = 0; i < checkedList.size(); i++) {
                    MarketItemBean checkedPosition = checkedList.get(i);
                    int indexOf = marketEditAdapter.listContent.indexOf(checkedPosition);
                    marketEditAdapter.onItemDismiss(indexOf);
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
        ivBarFunction.setText("完成");

        int rightColor = ContextCompat.getColor(this, R.color.bg_enable_color);
        ivBarFunction.setTextColor(rightColor);

        UserJson userInfo = LoginUtils.getUserInfo(this);
        if (userInfo == null) {
            String marketOption = MarketUtil.getMarketEditOption(getContext());
            adapterMarketItemList = JSONArray.parseArray(marketOption, MarketItemBean.class);
            defaultInitMarketList.addAll(adapterMarketItemList);
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
        new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("自选数据发生改变,是否保存并且退出?")
                .setPositiveButton("保存并退出",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onSaveAndExit();
                            }
                        })
                .setNegativeButton("直接退出",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MarketEditActivity.this.finish();
                            }
                        }).show();
    }

    private void onSaveAndExit() {
        requestRefresh();
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
                TSnackbar.make(tvBarTitle, "已同步自选列表", TSnackbar.LENGTH_LONG, TSnackbar
                        .APPEAR_FROM_BOTTOM_TO_TOP)
                        .setPromptThemBackground(Prompt.WARNING).show();

                String marketOption = MarketUtil.getMarketEditOption(getContext());
                adapterMarketItemList = JSONArray.parseArray(marketOption, MarketItemBean.class);
                defaultInitMarketList.addAll(adapterMarketItemList);

                initEditInfo();
                pllContent.loadOver();
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);

                TSnackbar.make(tvBarTitle, "自选信息同步失败", TSnackbar.LENGTH_LONG, TSnackbar
                        .APPEAR_FROM_BOTTOM_TO_TOP)
                        .setPromptThemBackground(Prompt.WARNING).show();

                String marketOption = MarketUtil.getMarketEditOption(getContext());
                adapterMarketItemList = JSONArray.parseArray(marketOption, MarketItemBean.class);
                defaultInitMarketList.addAll(adapterMarketItemList);

                pllContent.loadOver();
            }
        });
    }

    /**
     * 请求刷新同步
     */
    private void requestRefresh() {
        UserJson userInfo = LoginUtils.getUserInfo(this);
        if (userInfo != null) {
            return;
        }


        Observable.create(new Observable.OnSubscribe<SingleThreadJson>() {
            @Override
            public void call(Subscriber<? super SingleThreadJson> subscriber) {
                VolleySyncHttp volleySyncHttp = VolleySyncHttp.getInstance();

                try {//如果一个出错则不继续进行
                    JSONObject deleteParam = volleySyncHttp.getJsonParam();
                    String deleteList = volleySyncHttp.syncGet(mQueue, HttpConstant.QUOTES_DELFAVOR);

                    String addList = volleySyncHttp.syncGet(mQueue, HttpConstant.QUOTES_ADDFAVOR);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SingleThreadJson>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(SingleThreadJson jsonStr) {

                    }
                });
    }
}
