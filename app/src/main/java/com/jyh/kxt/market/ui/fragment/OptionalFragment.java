package com.jyh.kxt.market.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.impl.OnSocketTextMessage;
import com.jyh.kxt.base.utils.MarketConnectUtil;
import com.jyh.kxt.base.utils.MarketUtil;
import com.jyh.kxt.market.adapter.MarketMainItemAdapter;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.jyh.kxt.market.presenter.OptionalPresenter;
import com.library.bean.EventBusClass;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.jyh.kxt.base.utils.MarketUtil.getMarketEditOption;
import static org.greenrobot.eventbus.ThreadMode.MAIN;

/**
 * Created by Mr'Dai on 2017/4/25.
 * 行情 - 自选
 */

public class OptionalFragment extends BaseFragment implements OnSocketTextMessage {

    @BindView(R.id.pll_content) PageLoadLayout pllContent;
    @BindView(R.id.ptrlv_content) public PullToRefreshListView ptrContent;
    @BindView(R.id.tv_target_nav) TextView tvTargetNav;

    private OptionalPresenter optionalPresenter;
    /**
     * 切换Item 类型  0 涨跌幅   1 涨跌额
     */
    public int switchItemType = 0;
    public HashMap<String, MarketItemBean> marketMap = new HashMap<>();
    public List<MarketItemBean> marketItemList;
    private JSONArray marketCodeList = new JSONArray();


    private MarketMainItemAdapter marketMainItemAdapter;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_market_item);

        //初始化，因为行情首页需要
        RelativeLayout.LayoutParams contentParams = (RelativeLayout.LayoutParams) ptrContent.getLayoutParams();
        contentParams.addRule(RelativeLayout.BELOW, R.id.rl_list_nav);
        ptrContent.setLayoutParams(contentParams);

        initMarketData(0);

        optionalPresenter = new OptionalPresenter(this);


        marketMainItemAdapter = new MarketMainItemAdapter(getContext(), marketItemList);
        ptrContent.setAdapter(marketMainItemAdapter);
        /*
         * 开始进行Socket通信
         */
        MarketConnectUtil.getInstance().sendSocketParams(
                this,
                marketCodeList,
                this);

        EventBus.getDefault().register(this);

        if (marketItemList.size() == 0) {
            pllContent.loadEmptyData();
        }
    }

    @OnClick(R.id.rl_target_nav)
    public void navClick(View view) {
        switchItemType = switchItemType == 0 ? 1 : 0;
        tvTargetNav.setText(switchItemType == 0 ? "涨跌幅" : "涨跌额");

        for (MarketItemBean marketItemBean : marketItemList) {
            marketItemBean.setSwitchTarget(switchItemType == 0 ?
                    marketItemBean.getRange() : marketItemBean.getChange());
        }
    }


    @Subscribe(threadMode = MAIN)
    public void onMarketEvent(EventBusClass eventBus) {
        switch (eventBus.fromCode) {
            case EventBusClass.MARKET_OPTION_UPDATE:

                List<MarketItemBean> marketList = (List<MarketItemBean>) eventBus.intentObj;
                marketItemList.clear();
                marketItemList.addAll(marketList);

                if (marketItemList.size() == 0) {
                    pllContent.loadEmptyData();
                } else {
                    pllContent.loadOver();
                }

                initMarketData(1);

                marketMainItemAdapter.notifyDataSetChanged();

                //参数改变
                MarketConnectUtil.getInstance().sendSocketParams(
                        this,
                        marketCodeList,
                        this);
                break;
        }
    }

    public void sendSocketParams() {
        //参数改变
        if (marketCodeList != null) {
            MarketConnectUtil.getInstance().sendSocketParams(
                    this,
                    marketCodeList,
                    this);
        }
    }

    /**
     * 进入界面进行初始化
     * form 0 来自于本地
     * form 1 来自于刷新
     */
    private void initMarketData(int form) {
        if (form == 0) {
            marketItemList = getMarketEditOption(getContext());
            if (marketItemList == null || marketItemList.size() == 0) {
                marketItemList = new ArrayList<>();
            }
        }

        marketCodeList.clear();
        marketMap.clear();
        for (MarketItemBean marketItemBean : marketItemList) {
            marketCodeList.add(marketItemBean.getCode());
            marketMap.put(marketItemBean.getCode(), marketItemBean);

            marketItemBean.setSwitchTarget(marketItemBean.getRange());
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onTextMessage(String text) {
        try {
            MarketUtil.mapToMarketBean(ptrContent, switchItemType, marketMap, text);
        } catch (Exception e) {
            try {
                List<String> jsonList = JSONArray.parseArray(text, String.class);
                for (String itemJson : jsonList) {
                    MarketUtil.mapToMarketBean(ptrContent, switchItemType, marketMap, itemJson);
                }
            } catch (Exception e1) {

            }
        }
    }

    @Override
    public void onChangeTheme() {
        super.onChangeTheme();
        if (marketMainItemAdapter != null) {
            marketMainItemAdapter.notifyDataSetChanged();
        }
    }
}
