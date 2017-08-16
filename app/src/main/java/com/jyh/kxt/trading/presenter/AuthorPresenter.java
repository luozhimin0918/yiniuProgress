package com.jyh.kxt.trading.presenter;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.explore.json.AuthorDetailsJson;
import com.jyh.kxt.explore.json.AuthorNewsJson;
import com.jyh.kxt.trading.adapter.AuthorAdapter;
import com.jyh.kxt.trading.json.ViewPointTradeBean;
import com.jyh.kxt.trading.ui.AuthorActivity;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.bean.EventBusClass;
import com.library.widget.window.ToastView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/1.
 */

public class AuthorPresenter extends BasePresenter {
    @BindObject AuthorActivity activity;

    private String authorId;
    private VolleyRequest request;

    private String lastId_viewpoint, lastId_article;
    private boolean isMore_viewpoint, isMore_article;
    private boolean isloading;
    private boolean isloadOver;

    public AuthorPresenter(IBaseView iBaseView, String authorId) {
        super(iBaseView);
        this.authorId = authorId;
        request = new VolleyRequest(mContext, mQueue);
        request.setTag(getClass().getName());
    }

    public void init() {
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_ID, authorId);
        if (LoginUtils.isLogined(mContext)) {
            UserJson userInfo = LoginUtils.getUserInfo(mContext);
            jsonParam.put(VarConstant.HTTP_UID, userInfo.getUid());
        }
        jsonParam.put(VarConstant.HTTP_RELOAD, "yes");
        jsonParam.put(VarConstant.HTTP_TYPE, VarConstant.TRADING_AUTHOR_TYPE_POINT);
        request.doPost(HttpConstant.TRADING_COLUMNIST_PROFILE, jsonParam, new HttpListener<AuthorDetailsJson>() {
            @Override
            protected void onResponse(AuthorDetailsJson authorDetailsJson) {
                activity.setView(authorDetailsJson);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                activity.loadError();
            }
        });
    }


    public void loadMore() {
        int type = 0;
        try {
            type = activity.adapter.getType();
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonParam = request.getJsonParam();
        if (type == AuthorAdapter.TYPE_VIEWPOINT) {
            //观点加载更多
            if (isMore_viewpoint) {
                jsonParam.put(VarConstant.HTTP_ID, authorId);
                jsonParam.put(VarConstant.HTTP_RELOAD, "no");
                jsonParam.put(VarConstant.HTTP_TYPE, VarConstant.TRADING_AUTHOR_TYPE_POINT);
                jsonParam.put(VarConstant.HTTP_LASTID, lastId_viewpoint);
                request.doPost(HttpConstant.TRADING_COLUMNIST_PROFILE, jsonParam, new HttpListener<AuthorDetailsJson>() {
                    @Override
                    protected void onResponse(AuthorDetailsJson authorDetailsJson) {
                        List<ViewPointTradeBean> viewpoints = authorDetailsJson.getView();
                        if (viewpoints != null) {
                            int size = viewpoints.size();
                            List<ViewPointTradeBean> data;
                            if (size > VarConstant.LIST_MAX_SIZE) {
                                isMore_viewpoint = true;
                                data = new ArrayList<>(viewpoints.subList(0, VarConstant.LIST_MAX_SIZE));
                                lastId_viewpoint = data.get(data.size() - 1).o_id;
                            } else {
                                isMore_viewpoint = false;
                                data = viewpoints;
                            }
                            activity.loadMoreViewpoint(data);
                        }
                        activity.plRootView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                activity.plContent.onRefreshComplete();
                            }
                        }, 200);
                    }

                    @Override
                    protected void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        activity.plRootView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                activity.plContent.onRefreshComplete();
                            }
                        }, 200);
                    }
                });
            } else {
                activity.plContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activity.plContent.onRefreshComplete();
                        ToastView.makeText3(mContext, mContext.getString(R.string.no_data));
                    }
                }, 200);

            }
        } else {
            //文章加载更多
            if (isMore_article) {
                jsonParam.put(VarConstant.HTTP_ID, authorId);
                jsonParam.put(VarConstant.HTTP_RELOAD, "no");
                jsonParam.put(VarConstant.HTTP_TYPE, VarConstant.TRADING_AUTHOR_TYPE_ARTICLE);
                jsonParam.put(VarConstant.HTTP_LASTID, lastId_article);
                request.doPost(HttpConstant.TRADING_COLUMNIST_PROFILE, jsonParam, new HttpListener<AuthorDetailsJson>() {
                    @Override
                    protected void onResponse(AuthorDetailsJson authorDetailsJson) {
                        List<AuthorNewsJson> newsJsons = authorDetailsJson.getList();
                        if (newsJsons != null) {
                            int size = newsJsons.size();
                            List<AuthorNewsJson> data;
                            if (size > VarConstant.LIST_MAX_SIZE) {
                                isMore_article = true;
                                data = new ArrayList<>(newsJsons.subList(0, VarConstant.LIST_MAX_SIZE));
                                lastId_article = data.get(data.size() - 1).getO_id();
                            } else {
                                isMore_article = false;
                                data = newsJsons;
                            }
                            activity.loadMoreArticle(data);
                        }
                        activity.plRootView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                activity.plContent.onRefreshComplete();
                            }
                        }, 200);
                    }

                    @Override
                    protected void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        activity.plRootView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                activity.plContent.onRefreshComplete();
                            }
                        }, 200);
                    }
                });

            } else {
                activity.plContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activity.plContent.onRefreshComplete();
                        ToastView.makeText3(mContext, mContext.getString(R.string.no_data));
                    }
                }, 200);
            }
        }

    }

    public String getLastId_viewpoint() {
        return lastId_viewpoint;
    }

    public void setLastId_viewpoint(String lastId_viewpoint) {
        this.lastId_viewpoint = lastId_viewpoint;
    }

    public String getLastId_article() {
        return lastId_article;
    }

    public void setLastId_article(String lastId_article) {
        this.lastId_article = lastId_article;
    }

    public boolean isMore_viewpoint() {
        return isMore_viewpoint;
    }

    public void setMore_viewpoint(boolean more_viewpoint) {
        isMore_viewpoint = more_viewpoint;
    }

    public boolean isMore_article() {
        return isMore_article;
    }

    public void setMore_article(boolean more_article) {
        isMore_article = more_article;
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
                activity.attention(isFollow);
                if (isFollow) {
                    ToastView.makeText3(mContext, "取消成功");
                    EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_ATTENTION_AUTHOR_ADD, authorId));
                } else {
                    ToastView.makeText3(mContext, "关注成功");
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

    public void initTwo() {

        if (isloading || isloadOver) return;
        isloading = true;
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_ID, authorId);
        jsonParam.put(VarConstant.HTTP_RELOAD, "no");
        jsonParam.put(VarConstant.HTTP_TYPE, VarConstant.TRADING_AUTHOR_TYPE_ARTICLE);
        request.doPost(HttpConstant.TRADING_COLUMNIST_PROFILE, jsonParam, new HttpListener<AuthorDetailsJson>() {
            @Override
            protected void onResponse(AuthorDetailsJson authorDetailsJson) {
                isloading = false;
                isloadOver = true;
                List<AuthorNewsJson> news = authorDetailsJson.getList();
                activity.setAuthors(news);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                isloading = false;
                isloadOver = true;
                activity.setAuthors(null);
            }
        });
    }
}
