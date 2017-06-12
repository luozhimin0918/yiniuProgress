package com.jyh.kxt.explore.presenter;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.explore.json.AuthorJson;
import com.jyh.kxt.explore.ui.fragment.AuthorFragment;
import com.jyh.kxt.index.json.HomeHeaderJson;
import com.jyh.kxt.user.ui.LoginOrRegisterActivity;
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
 * 创建日期:2017/6/9.
 */

public class AuthorFragmentPresenter extends BasePresenter {

    private VolleyRequest request;
    @BindObject AuthorFragment fragment;
    private boolean isMore;
    private String lastId;
    private AlertDialog loginPop;

    public AuthorFragmentPresenter(IBaseView iBaseView) {
        super(iBaseView);
        request = new VolleyRequest(mContext, mQueue);
        request.setTag(getClass().getName());
    }

    public void init() {
        fragment.plRootView.loadWait();
        request.doGet(HttpConstant.EXPLORE_BLOG_WRITER, request.getJsonParam(), new HttpListener<List<HomeHeaderJson>>() {
            @Override
            protected void onResponse(List<HomeHeaderJson> data) {

                if (data == null || data.size() == 0) {
                    fragment.plRootView.loadEmptyData();
                } else {
                    for (HomeHeaderJson json : data) {
                        try {
                            switch (json.getType()) {
                                case VarConstant.EXPLORE_AUTHOR_LIST_TYPE_WRITER_LIST:
                                    List<AuthorJson> authors = JSON.parseArray(json.getData().toString(), AuthorJson.class);
                                    if (authors != null && authors.size() > 0)
                                        fragment.addHeadView(authors);
                                    break;
                                case VarConstant.EXPLORE_AUTHOR_LIST_TYPE_ALL_WRITER_LIST:
                                    List<AuthorJson> authorList = JSON.parseArray(json.getData().toString(), AuthorJson.class);
                                    if (authorList != null && authorList.size() > 0) {
                                        if (authorList.size() > VarConstant.LIST_MAX_SIZE) {
                                            isMore = true;
                                            fragment.init(new ArrayList<>(authorList.subList(0, VarConstant.LIST_MAX_SIZE)));
                                            lastId = fragment.adapter.getLastId();
                                        } else {
                                            isMore = false;
                                            fragment.init(authorList);
                                        }
                                        fragment.plRootView.loadOver();
                                    } else {
                                        fragment.plRootView.loadEmptyData();
                                    }
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            fragment.plRootView.loadError();
                        }
                    }
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

        request.doGet(HttpConstant.EXPLORE_BLOG_WRITER, request.getJsonParam(), new HttpListener<List<HomeHeaderJson>>() {
            @Override
            protected void onResponse(List<HomeHeaderJson> data) {

                if (data == null || data.size() == 0) {

                } else {
                    for (HomeHeaderJson json : data) {
                        try {
                            switch (json.getType()) {
                                case VarConstant.EXPLORE_AUTHOR_LIST_TYPE_WRITER_LIST:
                                    List<AuthorJson> authors = JSON.parseArray(json.getData().toString(), AuthorJson.class);
                                    if (authors != null && authors.size() > 0)
                                        fragment.addHeadView(authors);
                                    break;
                                case VarConstant.EXPLORE_AUTHOR_LIST_TYPE_RECOMMEND_LIST:
                                    List<AuthorJson> authorList = JSON.parseArray(json.getData().toString(), AuthorJson.class);
                                    if (authorList != null && authorList.size() > 0) {
                                        if (authorList.size() > VarConstant.LIST_MAX_SIZE) {
                                            isMore = true;
                                            fragment.refresh(new ArrayList<>(authorList.subList(0, VarConstant.LIST_MAX_SIZE)));
                                            lastId = fragment.adapter.getLastId();
                                        } else {
                                            isMore = false;
                                            fragment.refresh(authorList);
                                        }

                                    } else {
                                    }
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                fragment.plvContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fragment.plvContent.onRefreshComplete();
                    }
                }, 200);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                fragment.plvContent.postDelayed(new Runnable() {
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
            jsonParam.put(VarConstant.HTTP_LASTID, lastId);
            request.doGet(HttpConstant.EXPLORE_BLOG_WRITER_LIST, jsonParam, new HttpListener<List<AuthorJson>>() {
                @Override
                protected void onResponse(List<AuthorJson> authorList) {
                    if (authorList != null && authorList.size() > 0) {
                        if (authorList.size() > VarConstant.LIST_MAX_SIZE) {
                            isMore = true;
                            fragment.loadMore(new ArrayList<>(authorList.subList(0, VarConstant.LIST_MAX_SIZE)));
                            lastId = fragment.adapter.getLastId();
                        } else {
                            isMore = false;
                            fragment.loadMore(authorList);
                        }

                    }
                    fragment.plvContent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fragment.plvContent.onRefreshComplete();
                        }
                    }, 200);
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                    fragment.plvContent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fragment.plvContent.onRefreshComplete();
                        }
                    }, 200);
                }
            });
        } else {
            fragment.plvContent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fragment.plvContent.onRefreshComplete();
                    ToastView.makeText3(mContext, mContext.getString(R.string.no_data));
                }
            }, 200);

        }
    }

    public void showLoginDialog() {
        if (loginPop == null) {
            loginPop = new AlertDialog.Builder(mContext)
                    .setTitle("提醒")
                    .setMessage("请先登录")
                    .setNegativeButton("登录", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mContext.startActivity(new Intent(mContext, LoginOrRegisterActivity.class));
                        }
                    }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
        }
        if (loginPop.isShowing()) {
            loginPop.dismiss();
        }
        loginPop.show();
    }
}
