package com.jyh.kxt.user.presenter;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.av.json.VideoListJson;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.collect.CollectUtils;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.ui.fragment.CollectVideoFragment;
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
 * 创建日期:2017/5/10.
 */

public class CollectVideoPresenter extends BasePresenter {

    @BindObject CollectVideoFragment collectVideoFragment;
    private VolleyRequest request;
    private String lastId = "";
    private List<VideoListJson> videos;//当前填充的数据
    private List<VideoListJson> videoAll;//所有数据
    private boolean isMore;//是否拥有更多数据
    private int pageCount = 1;
    private int currentPage = 1;//当前页码

    public CollectVideoPresenter(IBaseView iBaseView) {
        super(iBaseView);
        request = new VolleyRequest(mContext, mQueue);
        request.setTag(getClass().getName());
    }

    /**
     * 初始化
     */
    public void initData() {
        lastId = "";
        collectVideoFragment.plRootView.loadWait();
        if (LoginUtils.isLogined(mContext)) {
            //先提交本地收藏,再请求网络收藏
            CollectUtils.localToNetSynchronization(mContext, VarConstant.COLLECT_TYPE_VIDEO, new ObserverData() {
                @Override
                public void callback(Object o) {
                    initNetData();
                }

                @Override
                public void onError(Exception e) {
                    initNetData();
                }
            });
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
        CollectUtils.getCollectData(mContext, VarConstant.COLLECT_TYPE_VIDEO, new ObserverData<List>() {
            @Override
            public void callback(List list) {
                if (list == null || list.size() == 0) {
                    collectVideoFragment.plRootView.setNullImgId(R.mipmap.icon_collect_null);
                    collectVideoFragment.plRootView.setNullText("");
                    collectVideoFragment.plRootView.loadEmptyData();
                } else {
                    int size = list.size();
                    pageCount = size / VarConstant.LIST_MAX_SIZE;
                    int i = size % VarConstant.LIST_MAX_SIZE;
                    if (i != 0)
                        pageCount++;

                    if (pageCount == 1) {
                        isMore = false;
                        currentPage = 1;
                        videos = list;
                    } else {
                        isMore = true;
                        currentPage = 1;
                        videoAll = list;
                        videos = videoAll.subList(0, VarConstant.LIST_MAX_SIZE);
                    }

                    List<VideoListJson> adapterSourceList = new ArrayList<>(videos);
                    collectVideoFragment.initData(adapterSourceList);

                    collectVideoFragment.plRootView.loadOver();
                }
            }

            @Override
            public void onError(Exception e) {
                collectVideoFragment.plRootView.loadError();
            }
        });
    }

    /**
     * 初始化网络收藏信息
     */
    private void initNetData() {
        request.doGet(getUrl(), new HttpListener<List<VideoListJson>>() {
            @Override
            protected void onResponse(List<VideoListJson> o) {
                try {
                    if (o == null || o.size() == 0) {
                        collectVideoFragment.plRootView.setNullImgId(R.mipmap.icon_collect_null);
                        collectVideoFragment.plRootView.setNullText("");
                        collectVideoFragment.plRootView.loadEmptyData();
                    } else {
                        if (o.size() > VarConstant.LIST_MAX_SIZE) {
                            o = o.subList(0, VarConstant.LIST_MAX_SIZE);
                            isMore = true;
                            int index = o.size() - 1;
                            lastId = o.get(index).getId();
                        } else {
                            isMore = false;
                            lastId = "";
                        }
                        collectVideoFragment.initData(o);
                        collectVideoFragment.plRootView.loadOver();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                try {
                    collectVideoFragment.plRootView.loadError();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                        videos = list;
                    } else {
                        isMore = true;
                        currentPage = 1;
                        videoAll = list;
                        videos = videoAll.subList(0, VarConstant.LIST_MAX_SIZE);
                    }
                    List<VideoListJson> adapterSourceList = new ArrayList<>(videos);
                    collectVideoFragment.refresh(adapterSourceList);
                }
            }

            @Override
            public void onError(Exception e) {
                collectVideoFragment.plvContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        collectVideoFragment.plvContent.onRefreshComplete();
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
        request.doGet(getUrl(), new HttpListener<List<VideoListJson>>() {
            @Override
            protected void onResponse(List<VideoListJson> o) {
                if (o == null || o.size() == 0) {
                } else {
                    if (o.size() > VarConstant.LIST_MAX_SIZE) {
                        o = o.subList(0, VarConstant.LIST_MAX_SIZE);
                        isMore = true;
                        lastId = o.get(o.size() - 1).getId();
                    } else {
                        isMore = false;
                    }
                    collectVideoFragment.refresh(o);
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                collectVideoFragment.plvContent.onRefreshComplete();
            }
        });
    }

    /**
     * 加载本地更多收藏信息
     */
    private void loadLocalMore() {
        if (isMore) {
            List<VideoListJson> videoMore;
            currentPage++;
            if (currentPage < pageCount) {
                videoMore = videoAll.subList((currentPage - 1) * VarConstant.LIST_MAX_SIZE, currentPage * VarConstant.LIST_MAX_SIZE);
            } else {
                isMore = false;
                videoMore = videoAll.subList((currentPage - 1) * VarConstant.LIST_MAX_SIZE, videoAll.size());
            }
            collectVideoFragment.loadMore(videoMore);
        } else {
            ToastView.makeText3(mContext, mContext.getString(R.string.no_data));
            collectVideoFragment.plvContent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    collectVideoFragment.plvContent.onRefreshComplete();
                }
            }, 500);
        }
    }

    /**
     * 加载网络更多收藏信息
     */
    private void loadNetMore() {
        if (isMore)
            request.doGet(getUrl(), new HttpListener<List<VideoListJson>>() {
                @Override
                protected void onResponse(List<VideoListJson> o) {
                    if (o == null || o.size() == 0) {
                    } else {
                        if (o.size() > VarConstant.LIST_MAX_SIZE) {
                            o = o.subList(0, VarConstant.LIST_MAX_SIZE);
                            isMore = true;
                            lastId = o.get(o.size() - 1).getId();
                        } else {
                            isMore = false;
                            lastId = "";
                        }
                        collectVideoFragment.loadMore(o);
                        CollectUtils.netToLocalSynchronization(mContext,VarConstant.COLLECT_TYPE_VIDEO,o);
                    }
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                    collectVideoFragment.plvContent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            collectVideoFragment.plvContent.onRefreshComplete();
                        }
                    }, 500);
                }
            });
        else {
            ToastView.makeText3(mContext, mContext.getString(R.string.no_data));
            collectVideoFragment.plvContent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    collectVideoFragment.plvContent.onRefreshComplete();
                }
            }, 500);
        }
    }

    private String getUrl() {
        String url = HttpConstant.COLLECT_VIDEO;
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
