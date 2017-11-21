package com.jyh.kxt.index.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.android.volley.Request;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.utils.BrowerHistoryUtils;
import com.jyh.kxt.index.ui.BrowerHistoryActivity;
import com.jyh.kxt.index.ui.SearchActivity;
import com.jyh.kxt.main.json.NewsJson;
import com.library.base.http.VolleyRequest;
import com.library.util.RegexValidateUtil;
import com.library.util.SPUtils;
import com.library.util.SystemUtil;
import com.library.widget.window.ToastView;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/4.
 */

public class SearchPresenter extends BasePresenter {

    private final VolleyRequest request;
    @BindObject SearchActivity searchActivity;

    public SearchPresenter(IBaseView iBaseView) {
        super(iBaseView);
        request = new VolleyRequest(mContext, mQueue);
        request.setTag(getClass().getName());
    }

    /**
     * 初始化搜索历史
     */
    public void initSearchHistory() {
        String searchStr = SPUtils.getString(mContext, SpConstant.SEARCH_HISTORY);
        String[] search;
        if (RegexValidateUtil.isEmpty(searchStr)) {
            search = null;
        } else {
            search = searchStr.split(",");
        }

        searchActivity.initSearchHistory(search);
    }

    /**
     * 初始化浏览历史
     */
    public void initBrowseHistory() {

        List<NewsJson> list = BrowerHistoryUtils.getHistory(mContext);
        if (list != null && list.size() > 5) {
            list = list.subList(0, 5);
        }
        searchActivity.initBrowseHistory(list);
    }

    /**
     * 清除搜索历史
     */
    public void clearSearchHistory() {
        SPUtils.save(mContext, SpConstant.SEARCH_HISTORY, "");
        initSearchHistory();
    }

    /**
     * 更多浏览历史
     */
    public void moreBrowseHistory() {
        Intent intent = new Intent(mContext, BrowerHistoryActivity.class);
        mContext.startActivity(intent);
    }

    /**
     * 搜索
     *
     * @param searchKey
     */
    public void search(String searchKey) {
        if (RegexValidateUtil.isEmpty(searchKey)) {
            ToastView.makeText3(mContext, "搜索关键字不能为空");
            return;
        }
        searchActivity.hideHistory();
        upDataSearchHistory(searchKey);
        initSearchHistory();
        if(searchActivity.videoFragment!=null)
            searchActivity.videoFragment.search(searchKey);
        if(searchActivity.articleFragment!=null)
            searchActivity.articleFragment.search(searchKey);
        SystemUtil.closeSoftInputWindow((Activity) mContext);
    }

    /**
     * 更新搜索历史
     *
     * @param searchKey
     */
    private void upDataSearchHistory(String searchKey) {
        String searchHistory = SPUtils.getString(mContext, SpConstant.SEARCH_HISTORY);
        if (RegexValidateUtil.isEmpty(searchHistory)) {
            searchHistory = searchKey;
        } else {
            String[] split = searchHistory.split(",");
            for (String s : split) {
                if (s.equals(searchKey)) {
                    return;
                }
            }
            searchHistory =searchKey+","+ searchHistory;
        }
        SPUtils.save(mContext, SpConstant.SEARCH_HISTORY, searchHistory);
        searchActivity.addHistory(searchKey);
    }
}
