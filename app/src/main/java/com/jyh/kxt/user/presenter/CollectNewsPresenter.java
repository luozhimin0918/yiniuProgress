package com.jyh.kxt.user.presenter;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;

import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.CollectUtils;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.ui.fragment.CollectNewsFragment;
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
 * 创建日期:2017/5/11.
 */

public class CollectNewsPresenter extends BasePresenter {
    @BindObject CollectNewsFragment collectNewsFragment;
    private VolleyRequest request;
    private String lastId = "";
    private List<NewsJson> newsJsons;//当前填充的数据
    private List<NewsJson> newsAll;//所有数据
    private boolean isMore;//是否拥有更多数据
    private int pageCount = 1;
    private int currentPage = 1;//当前页码

    public CollectNewsPresenter(IBaseView iBaseView) {
        super(iBaseView);
        request = new VolleyRequest(mContext, mQueue);
    }

    /**
     * 初始化
     */
    public void initData() {
        lastId = "";
        collectNewsFragment.plRootView.loadWait();
        if (LoginUtils.isLogined(mContext)) {
            initNetData();
        } else {
            initLocalData();
        }
    }

    /**
     * 刷新
     */
    public void refresh() {
        lastId = "";
        if (LoginUtils.isLogined(mContext)) {
            refreshNet();
        } else {
            refreshLocal();
        }
    }

    /**
     * 加载更多
     */
    public void loadMore() {
        if (LoginUtils.isLogined(mContext)) {
            loadNetMore();
        } else {
            loadLocalMore();
        }
    }


    /**
     * 初始化本地收藏信息
     */
    private void initLocalData() {
        CollectUtils.getCollectData(mContext, VarConstant.COLLECT_TYPE_ARTICLE, new ObserverData<List>() {
            @Override
            public void callback(List list) {
                if (list == null || list.size() == 0) {
                    collectNewsFragment.plRootView.loadEmptyData();
                } else {
                    int size = list.size();
                    pageCount = size / VarConstant.LIST_MAX_SIZE;
                    int i = size % VarConstant.LIST_MAX_SIZE;
                    if (i != 0)
                        pageCount++;

                    if (pageCount == 1) {
                        isMore = false;
                        currentPage = 1;
                        newsJsons = list;
                    } else {
                        isMore = true;
                        currentPage = 1;
                        newsAll = list;
                        newsJsons = newsAll.subList(0, VarConstant.LIST_MAX_SIZE);
                    }

                    List<NewsJson> adapterSourceList = new ArrayList<>(newsJsons);
                    collectNewsFragment.initData(adapterSourceList);

                    collectNewsFragment.plRootView.loadOver();
                }
            }

            @Override
            public void onError(Exception e) {
                collectNewsFragment.plRootView.loadError();
            }
        });
    }

    /**
     * 初始化网络收藏信息
     */
    private void initNetData() {
        request.doGet(getUrl(), new HttpListener<List<NewsJson>>() {
            @Override
            protected void onResponse(List<NewsJson> o) {
                if (o == null || o.size() == 0) {
                    collectNewsFragment.plRootView.loadEmptyData();
                } else {
                    if (o.size() > VarConstant.LIST_MAX_SIZE) {
                        o = o.subList(0, VarConstant.LIST_MAX_SIZE);
                        isMore = true;
                        int index = o.size() - 1;
                        lastId = o.get(index).getO_id();
                    } else {
                        isMore = false;
                        lastId = "";
                    }
                    collectNewsFragment.initData(o);
                    collectNewsFragment.plRootView.loadOver();
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                collectNewsFragment.plRootView.loadError();
            }
        });
    }

    /**
     * 刷新本地收藏信息
     */
    private void refreshLocal() {
        CollectUtils.getCollectData(mContext, VarConstant.COLLECT_TYPE_VIDEO, new ObserverData<List>() {
            @Override
            public void callback(List list) {
                if (list == null || list.size() == 0) {
                } else {
                    int size = list.size();
                    pageCount = size / VarConstant.LIST_MAX_SIZE;
                    int i = size % VarConstant.LIST_MAX_SIZE;
                    if (i != 0)
                        pageCount++;

                    if (pageCount == 1) {
                        isMore = false;
                        currentPage = 1;
                        newsJsons = list;
                    } else {
                        isMore = true;
                        currentPage = 1;
                        newsAll = list;
                        newsJsons = newsAll.subList(0, VarConstant.LIST_MAX_SIZE);
                    }
                    List<NewsJson> adapterSourceList = new ArrayList<>(newsJsons);
                    collectNewsFragment.refresh(adapterSourceList);
                }
            }

            @Override
            public void onError(Exception e) {
                collectNewsFragment.plvContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        collectNewsFragment.plvContent.onRefreshComplete();
                    }
                }, 500);
            }
        });
    }

    /**
     * 刷新网络收藏信息
     */
    private void refreshNet() {
        lastId = "";
        request.doGet(getUrl(), new HttpListener<List<NewsJson>>() {
            @Override
            protected void onResponse(List<NewsJson> o) {
                if (o == null || o.size() == 0) {
                } else {
                    if (o.size() > VarConstant.LIST_MAX_SIZE) {
                        o = o.subList(0, VarConstant.LIST_MAX_SIZE);
                        isMore = true;
                        lastId = o.get(o.size() - 1).getO_id();
                    } else {
                        isMore = false;
                    }
                    collectNewsFragment.refresh(o);
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                collectNewsFragment.plvContent.onRefreshComplete();
            }
        });
    }

    /**
     * 加载本地更多收藏信息
     */
    private void loadLocalMore() {
        if (isMore) {
            List<NewsJson> newsMore;
            currentPage++;
            if (currentPage < pageCount) {
                newsMore = newsAll.subList((currentPage - 1) * VarConstant.LIST_MAX_SIZE, currentPage * VarConstant.LIST_MAX_SIZE);
            } else {
                isMore = false;
                newsMore = newsAll.subList((currentPage - 1) * VarConstant.LIST_MAX_SIZE, newsAll.size());
            }
            collectNewsFragment.loadMore(newsMore);
        } else {
            ToastView.makeText3(mContext, mContext.getString(R.string.no_data));
            collectNewsFragment.plvContent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    collectNewsFragment.plvContent.onRefreshComplete();
                }
            }, 500);
        }
    }

    /**
     * 加载网络更多收藏信息
     */
    private void loadNetMore() {
        request.doGet(getUrl(), new HttpListener<List<NewsJson>>() {
            @Override
            protected void onResponse(List<NewsJson> o) {
                if (o == null || o.size() == 0) {
                } else {
                    if (o.size() > VarConstant.LIST_MAX_SIZE) {
                        o = o.subList(0, VarConstant.LIST_MAX_SIZE);
                        isMore = true;
                        lastId = o.get(o.size() - 1).getO_id();
                    } else {
                        isMore = false;
                        lastId = "";
                    }
                    collectNewsFragment.loadMore(o);
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                collectNewsFragment.plvContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        collectNewsFragment.plvContent.onRefreshComplete();
                    }
                }, 500);
            }
        });
    }

    private String getUrl() {
        String url = HttpConstant.COLLECT_NEWS;
        JSONObject jsonParam = request.getJsonParam();
        UserJson userInfo = LoginUtils.getUserInfo(mContext);
        jsonParam.put(VarConstant.HTTP_UID, userInfo.getUid());
        jsonParam.put(VarConstant.HTTP_ACCESS_TOKEN, userInfo.getToken());
        if (!"".equals(lastId)) {
            jsonParam.put(VarConstant.HTTP_LASTID, lastId);
        }
        try {
            return url + VarConstant.HTTP_CONTENT + EncryptionUtils.createJWT(VarConstant.KEY, jsonParam.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return url;
        }
    }
}
