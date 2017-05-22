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
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.widget.helper.OnStartDragListener;
import com.jyh.kxt.base.widget.helper.SimpleItemTouchHelperCallback;
import com.jyh.kxt.market.adapter.MarketEditAdapter;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.library.bean.EventBusClass;
import com.library.util.SPUtils;
import com.library.widget.PageLoadLayout;

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

        try {
            String marketOption = SPUtils.getString(this, SpConstant.MARKET_MY_OPTION);
            adapterMarketItemList = JSONArray.parseArray(marketOption, MarketItemBean.class);
            defaultInitMarketList.addAll(adapterMarketItemList);

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
        EventBusClass eventBusClass = new EventBusClass(
                EventBusClass.MARKET_OPTION_UPDATE,
                adapterMarketItemList);

        EventBus.getDefault().post(eventBusClass);

        finish();
    }
}
