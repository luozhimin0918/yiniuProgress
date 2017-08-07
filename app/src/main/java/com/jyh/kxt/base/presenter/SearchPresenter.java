package com.jyh.kxt.base.presenter;

import android.app.Activity;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.ui.SearchActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.RegexValidateUtil;
import com.library.util.SPUtils;
import com.library.util.SystemUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/7.
 */

public class SearchPresenter extends BasePresenter {

    @BindObject SearchActivity activity;

    private VolleyRequest request;

    private String type;//搜索类型
    private String searchKey;//搜索关键字

    public SearchPresenter(IBaseView iBaseView, String type) {
        super(iBaseView);
        this.type = type;
        request = new VolleyRequest(mContext, mQueue);
        request.setTag(getClass().getName());
    }

    /**
     * 搜索
     *
     * @param searchKey
     */
    public void search(String searchKey) {
        this.searchKey = searchKey;
        saveSearchHistory(searchKey);
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put("type", "news");
        request.doGet(HttpConstant.SEARCH_LIST, jsonParam, new HttpListener<String>() {
            @Override
            protected void onResponse(String info) {
                activity.init(info);
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
     * 初始化搜索历史记录
     */
    public void initHistorySearch() {
        String searchType = null;
        switch (type) {
            case VarConstant.SEARCH_TYPE_NEWS:
                searchType = SpConstant.SEARCH_HISTORY_NEWS;
                break;
            case VarConstant.SEARCH_TYPE_VIDEO:
                searchType = SpConstant.SEARCH_HISTORY_VIDEO;
                break;
            case VarConstant.SEARCH_TYPE_ARTICLE:
                searchType = SpConstant.SEARCH_HISTORY_ARTICLE;
                break;
            case VarConstant.SEARCH_TYPE_COLUMNIST:
                searchType = SpConstant.SEARCH_HISTORY_COLUMNIST;
                break;
            case VarConstant.SEARCH_TYPE_QUOTE:
                searchType = SpConstant.SEARCH_HISTORY_MARKET;
                break;
            case VarConstant.SEARCH_TYPE_VIEWPOINT:
                searchType = SpConstant.SEARCH_HISTORY_VIEWPOINT;
                break;
        }
        if (searchType == null) return;
        List<String> historys;
        String history = SPUtils.getString(mContext, searchType);
        if (RegexValidateUtil.isEmpty(history)) {
            historys = null;
        } else {
            String[] split = history.split(",");
            historys = new ArrayList(Arrays.asList(split));
        }
        activity.showHistorySearch(historys);
    }

    /**
     * 保存搜索内容
     *
     * @param key
     */
    private void saveSearchHistory(String key) {

        String searchType = null;
        switch (type) {
            case VarConstant.SEARCH_TYPE_NEWS:
                searchType = SpConstant.SEARCH_HISTORY_NEWS;
                break;
            case VarConstant.SEARCH_TYPE_VIDEO:
                searchType = SpConstant.SEARCH_HISTORY_VIDEO;
                break;
            case VarConstant.SEARCH_TYPE_ARTICLE:
                searchType = SpConstant.SEARCH_HISTORY_ARTICLE;
                break;
            case VarConstant.SEARCH_TYPE_COLUMNIST:
                searchType = SpConstant.SEARCH_HISTORY_COLUMNIST;
                break;
            case VarConstant.SEARCH_TYPE_QUOTE:
                searchType = SpConstant.SEARCH_HISTORY_MARKET;
                break;
            case VarConstant.SEARCH_TYPE_VIEWPOINT:
                searchType = SpConstant.SEARCH_HISTORY_VIEWPOINT;
                break;
        }
        if (searchType == null) return;
        String str = SPUtils.getString(mContext, searchType);
        if (str.contains(key)) return;
        StringBuffer history = new StringBuffer(str);
        if (history.length() > 0) {
            history.append(",").append(key);
        } else {
            history.append(key);
        }
        SPUtils.save(mContext, searchType, history.toString());
    }

    public void refresh() {
        request.doGet("", new HttpListener<String>() {
            @Override
            protected void onResponse(String info) {
                activity.refresh(info);
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

    public void loadMore() {
        request.doGet("", new HttpListener<String>() {
            @Override
            protected void onResponse(String info) {
                activity.loadMore(info);
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
}
