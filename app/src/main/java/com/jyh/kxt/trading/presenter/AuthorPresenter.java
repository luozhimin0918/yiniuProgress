package com.jyh.kxt.trading.presenter;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.explore.json.AuthorDetailsJson;
import com.jyh.kxt.explore.json.AuthorNewsJson;
import com.jyh.kxt.trading.ui.AuthorActivity;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.bean.EventBusClass;
import com.library.util.EncryptionUtils;
import com.library.util.RegexValidateUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/8.
 */

public class AuthorPresenter extends BasePresenter {

    @BindObject AuthorActivity authorActivity;
    private VolleyRequest request;

    private String authorId = "";//作者id
    private String lastId = "";
    private boolean isMore;

    public AuthorPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void init(final String authorId) {

        this.authorId = authorId;

        if (request == null) {
            request = new VolleyRequest(mContext, mQueue);
            request.setTag(getClass().getName());
        }
        request.doGet(getUrl(request), new HttpListener<AuthorDetailsJson>() {
            @Override
            protected void onResponse(AuthorDetailsJson authorDetailsJson) {
                authorActivity.setView(authorDetailsJson);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                authorActivity.loadError();
            }
        });
    }


    /**
     * 刷新
     */
    public void refresh() {
        request.doGet(getUrl(request), new HttpListener<AuthorDetailsJson>() {
            @Override
            protected void onResponse(AuthorDetailsJson authorDetailsJson) {
                authorActivity.refresh(authorDetailsJson);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                authorActivity.plListRootView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        authorActivity.plContent.onRefreshComplete();
                    }
                }, 500);
            }
        });
    }

    /**
     * 加载更多
     */
    public void loadMore() {

        if (isMore) {

            if (request == null) {
                request = new VolleyRequest(mContext, mQueue);
            }
            request.doGet(getLoadMoreUrl(), new HttpListener<List<AuthorNewsJson>>() {
                @Override
                protected void onResponse(List<AuthorNewsJson> newsJsons) {
                    if (newsJsons != null) {
                        int size = newsJsons.size();
                        List<AuthorNewsJson> data;
                        if (size > VarConstant.LIST_MAX_SIZE) {
                            isMore = true;
                            data = new ArrayList<>(newsJsons.subList(0, VarConstant.LIST_MAX_SIZE));
                            lastId = authorActivity.newsAdapter.getLastId();
                        } else {
                            isMore = false;
                            data = newsJsons;
                        }
                        authorActivity.loadMore(data);
                    }
                    authorActivity.plListRootView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            authorActivity.plContent.onRefreshComplete();
                        }
                    }, 500);
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                    authorActivity.plListRootView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            authorActivity.plContent.onRefreshComplete();
                        }
                    }, 500);
                }
            });
        } else {
            authorActivity.plContent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    authorActivity.plContent.onRefreshComplete();
                    authorActivity.plContent.noMoreData();
                }
            }, 500);
        }
    }


    /**
     * 重新加载
     */
    public void reLoadListData() {
        request.doGet(getLoadMoreUrl(), new HttpListener<List<AuthorNewsJson>>() {
            @Override
            protected void onResponse(List<AuthorNewsJson> newsJsons) {
                if (newsJsons != null) {
                    int size = newsJsons.size();
                    List<AuthorNewsJson> data;
                    if (size > VarConstant.LIST_MAX_SIZE) {
                        data = new ArrayList<AuthorNewsJson>(newsJsons.subList(0, VarConstant.LIST_MAX_SIZE));
                    } else {
                        data = newsJsons;
                    }
                    authorActivity.reLoadListData(data);
                } else {
                    authorActivity.plListRootView.loadEmptyData();
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                authorActivity.plListRootView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        authorActivity.plContent.onRefreshComplete();
                    }
                }, 500);
            }
        });
    }


    /**
     * 关注
     *
     * @param isFollow
     */
    public void attention(final boolean isFollow) {
        JSONObject jsonParam = request.getJsonParam();
        if (LoginUtils.isLogined(mContext)) {
            UserJson userInfo = LoginUtils.getUserInfo(mContext);
            jsonParam.put(VarConstant.HTTP_UID, userInfo.getUid());
            jsonParam.put(VarConstant.HTTP_ACCESS_TOKEN, userInfo.getToken());
        }
        jsonParam.put(VarConstant.HTTP_ID, authorId);

        request.doGet(getFollowUrl(isFollow), jsonParam, new HttpListener<Object>() {
            @Override
            protected void onResponse(Object o) {
                authorActivity.attention(isFollow);
                if (isFollow) {
                    EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_ATTENTION_AUTHOR_ADD, authorId));
                } else {
                    EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_ATTENTION_AUTHOR_DEL, authorId));
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
            }
        });
    }

    /**
     * 获取关注url
     *
     * @param isFollow
     * @return
     */
    private String getFollowUrl(boolean isFollow) {
        if (isFollow) {
            return HttpConstant.EXPLORE_BLOG_DELETEFAVOR;
        } else {
            return HttpConstant.EXPLORE_BLOG_ADDFAVOR;
        }
    }

    /**
     * 获取加载更多的url
     *
     * @return
     */
    private String getLoadMoreUrl() {
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_LIST_TYPE, VarConstant.EXPLORE_AUTHOR_LIST_TYPE_WRITER);
        if (!RegexValidateUtil.isEmpty(lastId)) {
            jsonParam.put(VarConstant.HTTP_LASTID, lastId);
        }
        jsonParam.put(VarConstant.HTTP_WRITER_ID, authorId);
        try {
            return HttpConstant.EXPLORE_BLOG_LIST + VarConstant.HTTP_CONTENT + EncryptionUtils.createJWT(VarConstant
                    .KEY, jsonParam
                    .toString());
        } catch (Exception e) {
            e.printStackTrace();
            return HttpConstant.EXPLORE_BLOG_LIST;
        }
    }

    /**
     * 获取初次加载地址
     *
     * @param request
     * @return
     */
    private String getUrl(VolleyRequest request) {
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_ID, authorId);
        if (LoginUtils.isLogined(mContext)) {
            UserJson userInfo = LoginUtils.getUserInfo(mContext);
            jsonParam.put(VarConstant.HTTP_UID, userInfo.getUid());
        }
        try {
            return HttpConstant.EXPLORE_BLOG_PROFILE + VarConstant.HTTP_CONTENT + EncryptionUtils.createJWT
                    (VarConstant.KEY, jsonParam
                            .toString());
        } catch (Exception e) {
            e.printStackTrace();
            return HttpConstant.EXPLORE_BLOG_PROFILE;
        }
    }

    public void setLastId(String lastId) {
        this.lastId = lastId;
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }
}
