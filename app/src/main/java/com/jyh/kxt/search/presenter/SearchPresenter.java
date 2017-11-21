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
import com.jyh.kxt.search.ui.SearchActivity;
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

public class SearchPresenter extends BasePresenter {

    @BindObject SearchActivity activity;

    private VolleyRequest request;

    private String type;//搜索类型
    private String searchKey;//搜索关键字

    private String lastId;
    private boolean isMore;

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
        activity.isCanBreak=true;
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_CODE, type);
        jsonParam.put(VarConstant.HTTP_WORD, searchKey);
        jsonParam.put(VarConstant.HTTP_LASTID, lastId);
        if(LoginUtils.isLogined(mContext)){
            UserJson userInfo = LoginUtils.getUserInfo(mContext);
            jsonParam.put(VarConstant.HTTP_UID,userInfo.getUid());
        }

        request.doGet(HttpConstant.SEARCH_LIST, jsonParam, new HttpListener<String>() {
            @Override
            protected void onResponse(String info) {
                activity.init(info);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                activity.plRootView.setNullText(mContext.getString(R.string.error_search_null));
                activity.plRootView.setNullImgId(R.mipmap.icon_search_null);
                activity.plRootView.setNullTextColor(R.color.font_color8);
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
            case VarConstant.SEARCH_TYPE_BLOG:
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
            case VarConstant.SEARCH_TYPE_BLOG:
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
//        StringBuffer history = new StringBuffer(str);
//        if (history.length() > 0) {
//            history.append(",").append(key);
//        } else {
//            history.append(key);
//        }
        str=key+","+str;
        SPUtils.save(mContext, searchType, str);
    }

    public void refresh() {
        lastId = "";
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_CODE, type);
        jsonParam.put(VarConstant.HTTP_WORD, searchKey);
        if(LoginUtils.isLogined(mContext)){
            UserJson userInfo = LoginUtils.getUserInfo(mContext);
            jsonParam.put(VarConstant.HTTP_UID,userInfo.getUid());
        }
        request.doGet(HttpConstant.SEARCH_LIST, new HttpListener<String>() {
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
        if (isMore) {
            JSONObject jsonParam = request.getJsonParam();
            jsonParam.put(VarConstant.HTTP_CODE, type);
            jsonParam.put(VarConstant.HTTP_WORD, searchKey);
            jsonParam.put(VarConstant.HTTP_LASTID, lastId);
            if(LoginUtils.isLogined(mContext)){
                UserJson userInfo = LoginUtils.getUserInfo(mContext);
                jsonParam.put(VarConstant.HTTP_UID,userInfo.getUid());
            }
            request.doGet(HttpConstant.SEARCH_LIST, new HttpListener<String>() {
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
        } else {
            activity.plvContent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ToastView.makeText3(mContext, mContext.getString(R.string.no_data));
                    activity.plvContent.onRefreshComplete();
                }
            }, 200);
        }
    }

    public void setLastId(String o_id) {
        lastId = o_id;
    }

    public void setMore(boolean ismore) {
        this.isMore = ismore;
    }

    public void delHistory() {
        String searchType = null;
        switch (type) {
            case VarConstant.SEARCH_TYPE_NEWS:
                searchType = SpConstant.SEARCH_HISTORY_NEWS;
                break;
            case VarConstant.SEARCH_TYPE_VIDEO:
                searchType = SpConstant.SEARCH_HISTORY_VIDEO;
                break;
            case VarConstant.SEARCH_TYPE_ARTICLE:
            case VarConstant.SEARCH_TYPE_BLOG:
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
        SPUtils.save(mContext, searchType, "");
        activity.showHistorySearch(null);
    }
}
