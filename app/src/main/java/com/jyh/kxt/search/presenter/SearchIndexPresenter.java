package com.jyh.kxt.search.presenter;

import android.app.Activity;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.search.json.QuoteItemJson;
import com.jyh.kxt.search.ui.SearchIndexActivity;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.RegexValidateUtil;
import com.library.util.SPUtils;
import com.library.util.SystemUtil;
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
    private VolleyRequest request;
    private String searchKey;
    private String lastId;
    private boolean isMore;

    public SearchIndexPresenter(IBaseView iBaseView) {
        super(iBaseView);
        request = new VolleyRequest(mContext, mQueue);
        request.setTag(getClass().getName());
    }

    public void init() {
        List<String> historys;
        String history = SPUtils.getString(mContext, SpConstant.SEARCH_HISTORY_ALL);
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
            this.searchKey = searchKey;
            activity.isCanBack = true;
            JSONObject jsonParam = request.getJsonParam();
            jsonParam.put(VarConstant.HTTP_CODE, VarConstant.SEARCH_TYPE_QUOTE);
            jsonParam.put(VarConstant.HTTP_WORD, searchKey);
            if(LoginUtils.isLogined(mContext)){
                UserJson userInfo = LoginUtils.getUserInfo(mContext);
                jsonParam.put(VarConstant.HTTP_UID,userInfo.getUid());
            }
            request.doPost(HttpConstant.SEARCH_LIST, jsonParam, new HttpListener<List<QuoteItemJson>>() {
                @Override
                protected void onResponse(List<QuoteItemJson> quotes) {
                    activity.init(quotes);
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                    activity.plRootView.loadError();
                }
            });

        } else {
            ToastView.makeText3(mContext, "搜索内容不能为空");
        }
        SystemUtil.closeSoftInputWindow((Activity) mContext);
    }

    public void saveSearchHistory(String searchKey) {
        String str = SPUtils.getString(mContext, SpConstant.SEARCH_HISTORY_ALL);
        if (str.contains(searchKey)) return;
//        StringBuffer history = new StringBuffer(str);
//        if (history.length() > 0) {
//            history.append(",").append(searchKey);
//        } else {
//            history.append(searchKey);
//        }
        str=searchKey+","+str;
        SPUtils.save(mContext, SpConstant.SEARCH_HISTORY_ALL, str);
        init();
    }

    public void delSearchHistory() {
        SPUtils.save(mContext, SpConstant.SEARCH_HISTORY_ALL, "");
        activity.refreshSearchHistory(new ArrayList<String>());
    }

    public void loadMore() {
        if (isMore) {
            JSONObject jsonParam = request.getJsonParam();
            jsonParam.put(VarConstant.HTTP_CODE, VarConstant.SEARCH_TYPE_QUOTE);
            jsonParam.put(VarConstant.HTTP_WORD, searchKey);
            jsonParam.put(VarConstant.HTTP_LASTID, lastId);
            if(LoginUtils.isLogined(mContext)){
                UserJson userInfo = LoginUtils.getUserInfo(mContext);
                jsonParam.put(VarConstant.HTTP_UID,userInfo.getUid());
            }
            request.doPost(HttpConstant.SEARCH_LIST, jsonParam, new HttpListener<List<QuoteItemJson>>() {
                @Override
                protected void onResponse(List<QuoteItemJson> quotes) {
                    activity.loadMore(quotes);
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                    activity.plRootView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            activity.rvContent.onRefreshComplete();
                        }
                    }, 200);
                }
            });
        } else {
            activity.plRootView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ToastView.makeText3(mContext, mContext.getString(R.string.no_data));
                    activity.rvContent.onRefreshComplete();
                }
            }, 200);
        }
    }

    public void refresh() {
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_CODE, VarConstant.SEARCH_TYPE_QUOTE);
        jsonParam.put(VarConstant.HTTP_WORD, searchKey);
        if(LoginUtils.isLogined(mContext)){
            UserJson userInfo = LoginUtils.getUserInfo(mContext);
            jsonParam.put(VarConstant.HTTP_UID,userInfo.getUid());
        }
        request.doPost(HttpConstant.SEARCH_LIST, jsonParam, new HttpListener<List<QuoteItemJson>>() {
            @Override
            protected void onResponse(List<QuoteItemJson> quotes) {
                activity.refresh(quotes);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                activity.plRootView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activity.rvContent.onRefreshComplete();
                    }
                }, 200);
            }
        });
    }
}
