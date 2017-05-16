package com.jyh.kxt.explore.presenter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.explore.json.AuthorJson;
import com.jyh.kxt.explore.ui.AuthorListActivity;
import com.jyh.kxt.index.json.HomeHeaderJson;
import com.jyh.kxt.main.json.NewsJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;
import com.library.widget.window.ToastView;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/15.
 */

public class AuthorListPresenter extends BasePresenter {
    @BindObject AuthorListActivity activity;
    private VolleyRequest request;

    private String lastId = "";
    private boolean isMore;

    public AuthorListPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    /**
     * 初始化数据
     */
    public void init() {
        if (request == null) {
            request = new VolleyRequest(mContext, mQueue);
            request.setTag(this.getClass().getName());
        }
        lastId = "";
        request.doGet(HttpConstant.EXPLORE_BLOG_INDEX, new HttpListener<List<HomeHeaderJson>>() {
            @Override
            protected void onResponse(List<HomeHeaderJson> data) {
                if (data == null || data.size() == 0) {
                    activity.plRootView.loadEmptyData();
                } else {
                    for (HomeHeaderJson json : data) {
                        try {
                            switch (json.getType()) {
                                case VarConstant.EXPLORE_AUTHOR_LIST_TYPE_WRITER_LIST:
                                    List<AuthorJson> authors = JSON.parseArray(json.getData().toString(), AuthorJson.class);
                                    if (authors != null && authors.size() > 0)
                                        activity.addHeadView(authors);
                                    break;
                                case VarConstant.EXPLORE_AUTHOR_LIST_TYPE_RECOMMEND_LIST:
                                    List<NewsJson> newsList = JSON.parseArray(json.getData().toString(), NewsJson.class);
                                    if (newsList != null && newsList.size() > 0) {
                                        if (newsList.size() > VarConstant.LIST_MAX_SIZE) {
                                            isMore = true;
                                            activity.init(new ArrayList<NewsJson>(newsList.subList(0, VarConstant.LIST_MAX_SIZE)));
                                            lastId = activity.newsAdapter.getLastId();
                                        } else {
                                            isMore = false;
                                            activity.init(newsList);
                                        }

                                    } else {
                                        activity.plRootView.loadEmptyData();
                                    }
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            activity.plRootView.loadError();
                        }
                    }
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                activity.plRootView.loadError();
            }
        });
    }

    public void refresh() {
        lastId = "";
        request.doGet(HttpConstant.EXPLORE_BLOG_INDEX, new HttpListener<List<HomeHeaderJson>>() {
            @Override
            protected void onResponse(List<HomeHeaderJson> data) {
                if (data == null || data.size() == 0) {
                } else {
                    for (HomeHeaderJson json : data) {
                            switch (json.getType()) {
                                case VarConstant.EXPLORE_AUTHOR_LIST_TYPE_WRITER_LIST:
                                    List<AuthorJson> authors = JSON.parseArray(json.getData().toString(), AuthorJson.class);
                                    if (authors != null && authors.size() > 0)
                                        activity.addHeadView(authors);
                                    break;
                                case VarConstant.EXPLORE_AUTHOR_LIST_TYPE_RECOMMEND_LIST:
                                    List<NewsJson> newsList = JSON.parseArray(json.getData().toString(), NewsJson.class);
                                    if (newsList != null && newsList.size() > 0) {
                                        if (newsList.size() > VarConstant.LIST_MAX_SIZE) {
                                            isMore = true;
                                            activity.refreshList(new ArrayList<NewsJson>(newsList.subList(0, VarConstant.LIST_MAX_SIZE)));
                                            lastId = activity.newsAdapter.getLastId();
                                        } else {
                                            activity.refreshList(newsList);
                                            isMore = false;
                                        }
                                    }
                                    break;
                            }
                    }
                }
                activity.plvContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            activity.plvContent.onRefreshComplete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 500);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                activity.plvContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            activity.plvContent.onRefreshComplete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 500);
            }
        });
    }

    public void loadMore() {

        if (isMore) {
            request.doGet(getUrl(), new HttpListener<List<NewsJson>>() {
                @Override
                protected void onResponse(List<NewsJson> newsList) {
                    if (newsList != null)
                        if (newsList.size() > VarConstant.LIST_MAX_SIZE) {
                            isMore = true;
                            activity.loadMore(new ArrayList<NewsJson>(newsList.subList(0, VarConstant.LIST_MAX_SIZE)));
                            lastId = activity.newsAdapter.getLastId();
                        } else {
                            activity.loadMore(newsList);
                            isMore = false;
                        }
                    activity.plvContent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            activity.plvContent.onRefreshComplete();
                        }
                    }, 500);
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                    activity.plvContent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            activity.plvContent.onRefreshComplete();
                        }
                    }, 500);
                }
            });
        } else {
            activity.plvContent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    activity.plvContent.onRefreshComplete();
                    ToastView.makeText3(mContext, mContext.getString(R.string.no_data));
                }
            }, 500);

        }
    }

    public String getUrl() {
        String url = HttpConstant.EXPLORE_BLOG_LIST;
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_LIST_TYPE, VarConstant.EXPLORE_AUTHOR_LIST_TYPE_RECOMMEND);
        if (lastId != null && !lastId.equals(""))
            jsonParam.put(VarConstant.HTTP_LASTID, lastId);
        try {
            return url + VarConstant.HTTP_CONTENT + EncryptionUtils.createJWT(VarConstant.KEY, jsonParam.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return url;
        }
    }
}
