package com.jyh.kxt.search.presenter;

import android.content.Intent;

import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.search.ui.SearchIndexActivity;
import com.jyh.kxt.search.ui.SearchMainActivity;
import com.library.util.RegexValidateUtil;
import com.library.util.SPUtils;
import com.library.widget.window.ToastView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/7.
 */

public class SearchIndexPresenter extends BasePresenter {

    @BindObject SearchIndexActivity activity;

    public SearchIndexPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void init() {
        List<String> historys;
        String history = SPUtils.getString(mContext, SpConstant.SEARCH_HISTORY);
        if (RegexValidateUtil.isEmpty(history)) {
            historys = null;
        } else {
            String[] split = history.split(",");
            historys = new ArrayList(Arrays.asList(split));
        }
        activity.showHistorySearch(historys);
    }

    public void search(String searchKey) {
        if (!RegexValidateUtil.isEmpty(searchKey)) {
            saveSearchHistory(searchKey);
            Intent intent = new Intent(mContext, SearchMainActivity.class);
            intent.putExtra(SearchMainActivity.SEARCH_KEY, searchKey);
            mContext.startActivity(intent);
        } else {
            ToastView.makeText3(mContext, "搜索内容不能为空");
        }
    }

    private void saveSearchHistory(String searchKey) {
        String str = SPUtils.getString(mContext, SpConstant.SEARCH_HISTORY_ALL);
        if (str.contains(searchKey)) return;
        StringBuffer history = new StringBuffer(str);
        if (history.length() > 0) {
            history.append(",").append(searchKey);
        } else {
            history.append(searchKey);
        }
        SPUtils.save(mContext, SpConstant.SEARCH_HISTORY_ALL, history.toString());
    }

    public void delSearchHistory() {
        SPUtils.save(mContext, SpConstant.SEARCH_HISTORY_ALL, "");
        activity.refreshSearchHistory(new ArrayList<String>());
    }
}
