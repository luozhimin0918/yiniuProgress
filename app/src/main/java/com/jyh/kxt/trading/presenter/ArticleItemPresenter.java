package com.jyh.kxt.trading.presenter;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.explore.json.AuthorNewsJson;
import com.jyh.kxt.trading.ui.fragment.ArticleItemFragment;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.widget.window.ToastView;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/6/12.
 */

public class ArticleItemPresenter extends BasePresenter {

    private VolleyRequest request;
    @BindObject ArticleItemFragment fragment;
    private boolean isMore;
    private String lastId;
    private String id;
    private String type;
    private boolean isMain;

    public ArticleItemPresenter(IBaseView iBaseView) {
        super(iBaseView);
        request = new VolleyRequest(mContext, mQueue);
    }

    public void init(String id, boolean isMain, String type) {

        this.id = id;
        this.type = type;
        this.isMain = isMain;

        JSONObject jsonParam = request.getJsonParam();
        if (VarConstant.EXPLORE_ARTICLE_LIST_TYPE_FOLLOW.equals(type)) {
            UserJson userInfo = LoginUtils.getUserInfo(mContext);
            jsonParam.put(VarConstant.HTTP_UID, userInfo.getUid());
        } else if (VarConstant.EXPLORE_ARTICLE_LIST_TYPE_ALL.equals(type)) {

        } else {
            jsonParam.put(VarConstant.HTTP_ID, id);
        }

        request.setTag(id + type);
        request.doGet(HttpConstant.EXPLORE_BLOG_LIST + VarConstant.HTTP_CONTENT, jsonParam, new HttpListener<List<AuthorNewsJson>>() {
            @Override
            protected void onResponse(List<AuthorNewsJson> authorNewsJsons) {
                if (authorNewsJsons == null || authorNewsJsons.size() == 0) {
                    fragment.plRootView.loadEmptyData();
                } else {
                    int size = authorNewsJsons.size();
                    List<AuthorNewsJson> list;
                    if (size > VarConstant.LIST_MAX_SIZE) {
                        isMore = true;
                        list = new ArrayList<>(authorNewsJsons.subList(0, VarConstant.LIST_MAX_SIZE));
                        lastId = list.get(VarConstant.LIST_MAX_SIZE - 1).getO_id();
                    } else {
                        isMore = false;
                        list = new ArrayList<>(authorNewsJsons);
                    }
                    fragment.init(list);
                    fragment.plRootView.loadOver();
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                fragment.plRootView.loadError();
            }
        });
    }

    public void refresh() {
        lastId = "";
        JSONObject jsonParam = request.getJsonParam();
        if (VarConstant.EXPLORE_ARTICLE_LIST_TYPE_FOLLOW.equals(type)) {
            UserJson userInfo = LoginUtils.getUserInfo(mContext);
            jsonParam.put(VarConstant.HTTP_UID, userInfo.getUid());
        } else if (VarConstant.EXPLORE_ARTICLE_LIST_TYPE_ALL.equals(type)) {

        } else {
            jsonParam.put(VarConstant.HTTP_ID, id);
        }
        request.doGet(HttpConstant.EXPLORE_BLOG_LIST + VarConstant.HTTP_CONTENT, jsonParam, new HttpListener<List<AuthorNewsJson>>() {
            @Override
            protected void onResponse(List<AuthorNewsJson> authorNewsJsons) {
                if (authorNewsJsons == null || authorNewsJsons.size() == 0) {
                } else {
                    int size = authorNewsJsons.size();
                    List<AuthorNewsJson> list;
                    if (size > VarConstant.LIST_MAX_SIZE) {
                        isMore = true;
                        list = new ArrayList<>(authorNewsJsons.subList(0, VarConstant.LIST_MAX_SIZE));
                        lastId = list.get(VarConstant.LIST_MAX_SIZE - 1).getO_id();
                    } else {
                        isMore = false;
                        list = new ArrayList<>(authorNewsJsons);
                    }
                    fragment.refresh(list);
                }
                fragment.plRootView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fragment.plvContent.onRefreshComplete();
                    }
                }, 200);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                fragment.plRootView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fragment.plvContent.onRefreshComplete();
                    }
                }, 200);
            }
        });
    }

    public void loadMore() {
        if (isMore) {
            JSONObject jsonParam = request.getJsonParam();
            if (VarConstant.EXPLORE_ARTICLE_LIST_TYPE_FOLLOW.equals(type)) {
                UserJson userInfo = LoginUtils.getUserInfo(mContext);
                jsonParam.put(VarConstant.HTTP_UID, userInfo.getUid());
            } else if (VarConstant.EXPLORE_ARTICLE_LIST_TYPE_ALL.equals(type)) {

            } else {
                jsonParam.put(VarConstant.HTTP_ID, id);
            }
            jsonParam.put(VarConstant.HTTP_LASTID, lastId);

            request.doGet(HttpConstant.EXPLORE_BLOG_LIST + VarConstant.HTTP_CONTENT, jsonParam, new HttpListener<List<AuthorNewsJson>>() {
                @Override
                protected void onResponse(List<AuthorNewsJson> authorNewsJsons) {
                    if (authorNewsJsons == null || authorNewsJsons.size() == 0) {
                    } else {
                        int size = authorNewsJsons.size();
                        List<AuthorNewsJson> list;
                        if (size > VarConstant.LIST_MAX_SIZE) {
                            isMore = true;
                            list = new ArrayList<>(authorNewsJsons.subList(0, VarConstant.LIST_MAX_SIZE));
                            lastId = list.get(VarConstant.LIST_MAX_SIZE - 1).getO_id();
                        } else {
                            isMore = false;
                            list = new ArrayList<>(authorNewsJsons);
                        }
                        fragment.loadMore(list);
                    }
                    fragment.plRootView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fragment.plvContent.onRefreshComplete();
                        }
                    }, 200);
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                    fragment.plRootView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fragment.plvContent.onRefreshComplete();
                        }
                    }, 200);
                }
            });

        } else {
            fragment.plRootView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ToastView.makeText3(mContext, mContext.getString(R.string.no_data));
                    fragment.plvContent.onRefreshComplete();
                }
            }, 200);
        }
    }
}
