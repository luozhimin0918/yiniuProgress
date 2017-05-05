package com.jyh.kxt.index.presenter;

import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.index.ui.SearchActivity;
import com.library.util.RegexValidateUtil;
import com.library.util.SPUtils;
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

    @BindObject SearchActivity searchActivity;

    public SearchPresenter(IBaseView iBaseView) {
        super(iBaseView);
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

        List list = new ArrayList();

        searchActivity.initBrowseHistory(list);
    }

    /**
     * 清除搜索历史
     */
    public void clearSearchHistory() {
        SPUtils.save(mContext, SpConstant.SEARCH_HISTORY, null);
        initSearchHistory();
    }

    /**
     * 更多浏览历史
     */
    public void moreBrowseHistory() {

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
        upDataSearchHistory(searchKey);
        initSearchHistory();
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
            searchHistory += "," + searchKey;
        }


        SPUtils.save(mContext, SpConstant.SEARCH_HISTORY, searchHistory);
    }
}
