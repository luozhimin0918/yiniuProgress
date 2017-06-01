package com.jyh.kxt.datum.presenter;

import android.text.Html;
import android.text.Spanned;
import android.widget.AbsListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.datum.adapter.DatumHistoryAdapter;
import com.jyh.kxt.datum.bean.HistoryChartBean;
import com.jyh.kxt.datum.bean.HistoryInfoBean;
import com.jyh.kxt.datum.bean.HistoryListBean;
import com.jyh.kxt.datum.ui.DatumHistoryActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/5/26.
 */

public class DatumHistoryPresenter extends BasePresenter {
    @BindObject DatumHistoryActivity datumHistoryActivity;

    public DatumHistoryPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    private DatumHistoryAdapter datumHistoryAdapter;

    private List<HistoryListBean.DataBean> historyList;

    public void requestInitInfo() {
        datumHistoryActivity.pageLoadLayout.loadWait();

        VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);
        JSONObject jsonParam = volleyRequest.getJsonParam();
        jsonParam.put("code", datumHistoryActivity.datumCode);
        jsonParam.put("group_id", datumHistoryActivity.groupId);

        String url = null;
        switch (datumHistoryActivity.type) {
            case "finance":
                url = HttpConstant.DATA_FINANCE;
                break;
            case "etf":
                url = HttpConstant.DATA_ETF;
                break;
            case "cftc":
                url = HttpConstant.DATA_CFTC;
                break;
        }

        volleyRequest.doPost(url, jsonParam, new HttpListener<String>() {
            @Override
            protected void onResponse(String str) {
                datumHistoryActivity.pageLoadLayout.loadOver();

                JSONArray jsonArray = JSONArray.parseArray(str);

                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String jsonString = jsonObject.toJSONString();


                    String type = jsonObject.getString("type");

                    switch (type) {
                        case "chart":
                            HistoryChartBean historyChartBean = JSONObject.parseObject(jsonString, HistoryChartBean
                                    .class);
                            datumHistoryActivity.initChartView(historyChartBean);
                            break;
                        case "info":
                            HistoryInfoBean historyInfoBean = JSONObject.parseObject(jsonString, HistoryInfoBean.class);
                            datumHistoryActivity.initInfoView(historyInfoBean);
                            break;
                        case "history":
                            HistoryListBean historyListBean = JSONObject.parseObject(jsonString, HistoryListBean.class);
                            initListView(historyListBean);
                            break;
                    }
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                datumHistoryActivity.pageLoadLayout.loadError();
            }
        });
    }

    public void requestMoreData() {
        VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);
        JSONObject jsonParam = volleyRequest.getJsonParam();
        jsonParam.put("type", datumHistoryActivity.type);
        jsonParam.put("code", datumHistoryActivity.datumCode);
        jsonParam.put("lastTime", historyList.get(historyList.size() - 1).getTime());

        volleyRequest.doPost(HttpConstant.MORE_DATA, jsonParam, new HttpListener<List<HistoryListBean.DataBean>>() {
            @Override
            protected void onResponse(List<HistoryListBean.DataBean> moreData) {
                datumHistoryActivity.fplvContent.getRefreshableView().goneFoot();
                if (moreData.size() == 0) {
                    datumHistoryActivity.fplvContent.getRefreshableView().noMoreData();
                    return;
                }
                historyList.addAll(moreData);
                datumHistoryAdapter.notifyDataSetChanged();
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                datumHistoryActivity.fplvContent.getRefreshableView().noMoreData();
            }
        });
    }


    private void initListView(HistoryListBean historyListBean) {
        historyList = historyListBean.getData();

        HistoryListBean.DataBean headDataBean = new HistoryListBean.DataBean();
        headDataBean.setListAdapterType(0);
        historyList.add(0, headDataBean);

        datumHistoryAdapter = new DatumHistoryAdapter(mContext, historyList);
        datumHistoryActivity.fplvContent.setAdapter(datumHistoryAdapter);
        AbsListView.LayoutParams absListView = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT);
        datumHistoryActivity.llListHead.setLayoutParams(absListView);
        datumHistoryActivity.fplvContent.getRefreshableView().addHeaderView(datumHistoryActivity.llListHead);
    }

    public Spanned getHtmlFont(String label, String content) {
        Spanned htmlSpanned = Html.fromHtml("<font color='#B4B6BA'>" + label + "</font>" + content);
        return htmlSpanned;
    }
}
