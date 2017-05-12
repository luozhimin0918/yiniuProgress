package com.jyh.kxt.main.presenter;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.av.adapter.CommentAdapter;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.main.json.NewsContentJson;
import com.jyh.kxt.main.ui.activity.NewsContentActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;


/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/18.
 */

public class NewsContentPresenter extends BasePresenter {

    @BindObject NewsContentActivity newsContentActivity;

    public NewsContentPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    /**
     * 请求初始化评论
     */
    public void requestInitComment() {

        VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);
        JSONObject jsonParam = volleyRequest.getJsonParam();
        jsonParam.put("id", newsContentActivity.id);

        volleyRequest.doGet(HttpConstant.NEWS_CONTENT, jsonParam, new HttpListener<NewsContentJson>() {
            @Override
            protected void onResponse(NewsContentJson newsContentJson) {
                //创建相关文章
                newsContentActivity.commentPresenter.createMoreView(newsContentJson.getArticle());

                CommentAdapter commentAdapter = new CommentAdapter(mContext, newsContentJson.getComment());
                newsContentActivity.ptrLvMessage.setAdapter(commentAdapter);

                if (newsContentJson.getComment() == null || newsContentJson.getComment().size() == 0) {
                    newsContentActivity.commentPresenter.createNoneComment();
                }

                newsContentActivity.createWebViewAndHead(newsContentJson);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
            }
        });
    }
}
