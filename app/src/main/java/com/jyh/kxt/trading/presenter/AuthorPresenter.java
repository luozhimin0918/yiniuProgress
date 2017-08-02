package com.jyh.kxt.trading.presenter;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.explore.json.AuthorDetailsJson;
import com.jyh.kxt.trading.adapter.AuthorAdapter;
import com.jyh.kxt.trading.ui.AuthorActivity;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.bean.EventBusClass;

import org.greenrobot.eventbus.EventBus;

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
        if (type == AuthorAdapter.TYPE_VIEWPOINT) {
            //观点加载更多
            if (isMore_viewpoint) {

            } else {
                activity.plContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activity.plContent.onRefreshComplete();
                    }
                }, 200);
            }
        } else {
            //文章加载更多
            if (isMore_article) {

            } else {
                activity.plContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activity.plContent.onRefreshComplete();
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
}
