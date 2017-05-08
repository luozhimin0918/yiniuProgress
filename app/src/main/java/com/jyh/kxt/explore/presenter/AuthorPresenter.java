package com.jyh.kxt.explore.presenter;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.explore.json.AuthorDetailsJson;
import com.jyh.kxt.explore.ui.AuthorActivity;
import com.jyh.kxt.main.json.NewsJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;
import com.library.util.RegexValidateUtil;

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

    public AuthorPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void init(final String authorId) {

        this.authorId = authorId;

        if (request == null)
            request = new VolleyRequest(mContext, mQueue);
        request.doGet(getUrl(request), new HttpListener<AuthorDetailsJson>() {
            @Override
            protected void onResponse(AuthorDetailsJson authorDetailsJson) {
                authorActivity.setView(authorDetailsJson);
                getLastId(authorDetailsJson.getList());
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                authorActivity.plRootView.loadError();
            }
        });
    }

    /**
     * 加载更多
     */
    public void loadMore() {
        if (request == null)
            request = new VolleyRequest(mContext, mQueue);
        request.doGet(getUrl(request), new HttpListener<AuthorDetailsJson>() {
            @Override
            protected void onResponse(AuthorDetailsJson authorDetailsJson) {
                authorActivity.loadMore(authorDetailsJson);
                getLastId(authorDetailsJson.getList());
            }
        });
    }

    private String getUrl(VolleyRequest request) {
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_ID, authorId);
        if (!RegexValidateUtil.isEmpty(lastId))
            jsonParam.put(VarConstant.HTTP_LASTID, lastId);
        try {
            return HttpConstant.EXPLORE_AUTHOR + VarConstant.HTTP_CONTENT + EncryptionUtils.createJWT(VarConstant.KEY, jsonParam
                    .toString());
        } catch (Exception e) {
            e.printStackTrace();
            return HttpConstant.EXPLORE_AUTHOR;
        }
    }

    /**
     * 获取lastId
     *
     * @param newsJsons
     * @return
     */
    private String getLastId(List<NewsJson> newsJsons) {
        if (newsJsons == null || newsJsons.size() == 0) {
            return lastId = "";
        } else {
            try {
                return lastId = newsJsons.get(newsJsons.size() - 1).getO_id();
            } catch (Exception e) {
                e.printStackTrace();
                return lastId = "";
            }
        }
    }
}
