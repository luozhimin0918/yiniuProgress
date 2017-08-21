package com.jyh.kxt.trading.presenter;

import android.app.Activity;

import com.alibaba.fastjson.JSON;
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
import com.jyh.kxt.trading.ui.SearchActivity;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.SPUtils;
import com.library.util.SystemUtil;
import com.library.widget.window.ToastView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/21.
 */

public class SearchPresenter extends BasePresenter {

    @BindObject SearchActivity activity;
    private VolleyRequest request;
    private String key;
    private String lastId = "";
    private boolean isMore;

    public SearchPresenter(IBaseView iBaseView) {
        super(iBaseView);
        request = new VolleyRequest(mContext, mQueue);
    }


    public void init() {
        activity.initView(getHistory(), getHot());
    }

    public void search(String key) {
        this.key = key;
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_CODE, VarConstant.SEARCH_TYPE_QUOTE);
        jsonParam.put(VarConstant.HTTP_WORD, key);
        jsonParam.put(VarConstant.HTTP_LASTID, lastId);
        if (LoginUtils.isLogined(mContext)) {
            UserJson userInfo = LoginUtils.getUserInfo(mContext);
            jsonParam.put(VarConstant.HTTP_UID, userInfo.getUid());
        }

        request.doGet(HttpConstant.SEARCH_LIST, jsonParam, new HttpListener<String>() {
            @Override
            protected void onResponse(String info) {
                activity.initSearchData(info);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                activity.plAfter.setNullText(mContext.getString(R.string.error_search_null));
                activity.plAfter.setNullImgId(R.mipmap.icon_search_null);
                activity.plAfter.setNullTextColor(R.color.font_color8);
                activity.plAfter.loadEmptyData();
            }
        });
        SystemUtil.closeSoftInputWindow((Activity) mContext);
    }

    public void saveSearchHistory(List<QuoteItemJson> items) {
        Set<String> historySet = new HashSet<>(SPUtils.getStringSet(mContext, SpConstant.SEARCH_HISTORY_POINT_MARKET));
        if (items == null) return;
        for (QuoteItemJson item : items) {
            historySet.add(JSON.toJSONString(item));
        }
        SPUtils.save(mContext, SpConstant.SEARCH_HISTORY_POINT_MARKET, historySet);
    }

    public void refresh() {
        lastId = "";
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_CODE, VarConstant.SEARCH_TYPE_QUOTE);
        jsonParam.put(VarConstant.HTTP_WORD, key);
        if (LoginUtils.isLogined(mContext)) {
            UserJson userInfo = LoginUtils.getUserInfo(mContext);
            jsonParam.put(VarConstant.HTTP_UID, userInfo.getUid());
        }
        request.doGet(HttpConstant.SEARCH_LIST, new HttpListener<String>() {
            @Override
            protected void onResponse(String info) {
                activity.refresh(info);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                activity.plAfter.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activity.plvContent.onRefreshComplete();
                    }
                }, 200);
            }
        });
    }

    public void loadMore() {
        if (isMore) {
            JSONObject jsonParam = request.getJsonParam();
            jsonParam.put(VarConstant.HTTP_CODE, VarConstant.SEARCH_TYPE_QUOTE);
            jsonParam.put(VarConstant.HTTP_WORD, key);
            jsonParam.put(VarConstant.HTTP_LASTID, lastId);
            if (LoginUtils.isLogined(mContext)) {
                UserJson userInfo = LoginUtils.getUserInfo(mContext);
                jsonParam.put(VarConstant.HTTP_UID, userInfo.getUid());
            }
            request.doGet(HttpConstant.SEARCH_LIST, new HttpListener<String>() {
                @Override
                protected void onResponse(String info) {
                    activity.loadMore(info);
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                    activity.plAfter.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            activity.plvContent.onRefreshComplete();
                        }
                    }, 200);
                }
            });
        } else {
            activity.plAfter.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ToastView.makeText3(mContext, mContext.getString(R.string.no_data));
                    activity.plvContent.onRefreshComplete();
                }
            }, 200);
        }
    }

    public List<QuoteItemJson> getHistory() {
        Set<String> historySet = SPUtils.getStringSet(mContext, SpConstant.SEARCH_HISTORY_POINT_MARKET);
        if (historySet == null || historySet.size() == 0) return null;
        List<QuoteItemJson> history = new ArrayList<>();
        for (String historyStr : historySet) {
            history.add(JSON.parseObject(historyStr, QuoteItemJson.class));
        }
        return history;
    }

    public List<QuoteItemJson> getHot() {
        Set<String> hotSet = SPUtils.getStringSet(mContext, SpConstant.SEARCH_HOT_POINT_MARKET);
        if (hotSet == null || hotSet.size() == 0)
            return null;
        List<QuoteItemJson> hot = new ArrayList<>();
        for (String hotStr : hotSet) {
            hot.add(JSON.parseObject(hotStr, QuoteItemJson.class));
        }
        return hot;
    }

    public void setLastId(String lastId) {
        this.lastId = lastId;
    }

    public void setMore(boolean more) {
        this.isMore = more;
    }
}
