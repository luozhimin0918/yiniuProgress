package com.jyh.kxt.market.presenter;

import android.app.Activity;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.jyh.kxt.market.ui.SearchActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;
import com.library.util.RegexValidateUtil;
import com.library.util.SPUtils;
import com.library.util.SystemUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/18.
 */

public class SearchPresenter extends BasePresenter {
    @BindObject SearchActivity activity;

    private VolleyRequest request;
    private String key;

    public SearchPresenter(IBaseView iBaseView) {
        super(iBaseView);
        request = new VolleyRequest(mContext, mQueue);
        request.setTag(getClass().getName());
    }

    public void search(String key) {
        this.key = key;
        saveSearchHistory(key);
        request.doGet(getSearchUrl(), new HttpListener<List<MarketItemBean>>() {
            @Override
            protected void onResponse(List<MarketItemBean> markets) {
                activity.init(markets);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                activity.plRootView.setNullText(mContext.getString(R.string.error_search_null));
                activity.plRootView.loadEmptyData();
            }
        });
        SystemUtil.closeSoftInputWindow((Activity) mContext);
    }

    /**
     * 保存搜索内容
     *
     * @param key
     */
    private void saveSearchHistory(String key) {
        String str = SPUtils.getString(mContext, SpConstant.SEARCH_HISTORY_MARKET);
        if (str.contains(key)) return;
//        StringBuffer history = new StringBuffer(str);
//        if (history.length() > 0) {
//            history.append(",").append(key);
//        } else {
//            history.append(key);
//        }
        str=key+","+str;
        SPUtils.save(mContext, SpConstant.SEARCH_HISTORY_MARKET, str);
    }

    public void initHotSearch() {
        List<String> markets;
        String history = SPUtils.getString(mContext, SpConstant.SEARCH_HISTORY_MARKET);
        if (RegexValidateUtil.isEmpty(history)) {
            markets = null;
        } else {
            String[] split = history.split(",");
            markets = new ArrayList(Arrays.asList(split));
        }
        activity.showHotSearch(markets);
    }

    public void refresh() {
        request.doGet(getSearchUrl(), new HttpListener<List<MarketItemBean>>() {
            @Override
            protected void onResponse(List<MarketItemBean> markets) {
                activity.refresh(markets);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                activity.plvContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activity.plvContent.onRefreshComplete();
                    }
                }, 200);
            }
        });
    }

    public String getSearchUrl() {
        String searchUrl = HttpConstant.SEARCH_MARKET;
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_WORD, key);
        try {
            searchUrl += VarConstant.HTTP_CONTENT + EncryptionUtils.createJWT(VarConstant.KEY, jsonParam.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return searchUrl;
    }

    public void clearSearchHistory() {
        SPUtils.save(mContext, SpConstant.SEARCH_HISTORY_MARKET, "");
        initHotSearch();
    }
}
