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
import com.jyh.kxt.datum.bean.HistoryCftcListBean;
import com.jyh.kxt.datum.bean.HistoryChartBean;
import com.jyh.kxt.datum.bean.HistoryEtfListBean;
import com.jyh.kxt.datum.bean.HistoryInfoBean;
import com.jyh.kxt.datum.bean.HistoryListBean;
import com.jyh.kxt.datum.ui.DatumHistoryActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;

import java.util.ArrayList;
import java.util.List;

import static com.alibaba.fastjson.JSON.parseObject;

/**
 * Created by Mr'Dai on 2017/5/26.
 */

public class DatumHistoryPresenter extends BasePresenter {
    @BindObject DatumHistoryActivity datumHistoryActivity;

    public DatumHistoryPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    private DatumHistoryAdapter datumHistoryAdapter;

    private List<HistoryListBean.DataBean> adapterList = new ArrayList<>();
    private List<HistoryEtfListBean.DataBean> etfList;

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
                            HistoryChartBean historyChartBean = parseObject(jsonString, HistoryChartBean
                                    .class);
                            datumHistoryActivity.initChartView(historyChartBean);
                            break;
                        case "info":
                            HistoryInfoBean historyInfoBean = parseObject(jsonString, HistoryInfoBean.class);
                            datumHistoryActivity.initInfoView(historyInfoBean);
                            break;
                        case "history":
                            initListView(JSONObject.parseObject(jsonString).getString("data"));
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
        jsonParam.put("lastTime", adapterList.get(adapterList.size() - 1).getTime());

        volleyRequest.doPost(HttpConstant.MORE_DATA, jsonParam, new HttpListener<String>() {
            @Override
            protected void onResponse(String jsonStr) {
                datumHistoryActivity.fplvContent.getRefreshableView().goneFoot();

                addToAdapterList(jsonStr);

                datumHistoryAdapter.notifyDataSetChanged();
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                datumHistoryActivity.fplvContent.getRefreshableView().noMoreData();
            }
        });
    }


    private void initListView(String historyListBean) {
        addToAdapterList(historyListBean);

        HistoryListBean.DataBean headDataBean = new HistoryListBean.DataBean();
        headDataBean.setListAdapterType(0);
        headDataBean.setListAdapterTypeName(datumHistoryActivity.type);
        adapterList.add(0, headDataBean);

        datumHistoryAdapter = new DatumHistoryAdapter(mContext, adapterList);
        datumHistoryActivity.fplvContent.setAdapter(datumHistoryAdapter);

        AbsListView.LayoutParams absListView = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT);

        datumHistoryActivity.llListHead.setLayoutParams(absListView);
        datumHistoryActivity.fplvContent.getRefreshableView().addHeaderView(datumHistoryActivity.llListHead);
    }

    private void addToAdapterList(String historyListBean) {
        switch (datumHistoryActivity.type) {
            case "finance":
                List<HistoryListBean.DataBean> historyList = JSONObject.parseArray(historyListBean, HistoryListBean
                        .DataBean.class);
                if (historyList.size() == 0) {
                    datumHistoryActivity.fplvContent.getRefreshableView().noMoreData();
                    return;
                }
                adapterList.addAll(historyList);
                break;
            case "etf":
                List<HistoryEtfListBean.DataBean> etfList = JSONObject.parseArray(historyListBean,
                        HistoryEtfListBean.DataBean.class);
                if (etfList.size() == 0) {
                    datumHistoryActivity.fplvContent.getRefreshableView().noMoreData();
                    return;
                }
                for (HistoryEtfListBean.DataBean dataBean : etfList) {

                    HistoryListBean.DataBean headDataBean = new HistoryListBean.DataBean();
                    headDataBean.setBefore(dataBean.getTotalounce());
                    headDataBean.setForecast(dataBean.getTotaltonne());
                    headDataBean.setReality(dataBean.getChange());
                    headDataBean.setTime(dataBean.getTime());

                    adapterList.add(headDataBean);
                }
                break;
            case "cftc":
                List<HistoryCftcListBean.DataBean> cftcList = JSONObject.parseArray(historyListBean,
                        HistoryCftcListBean.DataBean.class);

                if (cftcList.size() == 0) {
                    datumHistoryActivity.fplvContent.getRefreshableView().noMoreData();
                    return;
                }
                for (HistoryCftcListBean.DataBean dataBean : cftcList) {

                    HistoryListBean.DataBean headDataBean = new HistoryListBean.DataBean();
                    headDataBean.setBefore(dataBean.getBull());
                    headDataBean.setForecast(dataBean.getBear());
                    headDataBean.setReality(dataBean.getOnly());
                    headDataBean.setTime(dataBean.getTime());
                    headDataBean.setChange(dataBean.getChange());

                    adapterList.add(headDataBean);
                }
                break;
        }
    }

    public Spanned getHtmlFont(String label, String content) {
        Spanned htmlSpanned = Html.fromHtml("<font color='#B4B6BA'>" + label + "</font>" + content);
        return htmlSpanned;
    }
}
