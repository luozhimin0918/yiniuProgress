package com.jyh.kxt.main.presenter;

import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.av.adapter.CommentAdapter;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.main.json.NewsContentJson;
import com.jyh.kxt.main.ui.activity.NewsContentActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.util.SystemUtil;


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
        jsonParam.put("id", newsContentActivity.objectId);

        volleyRequest.doGet(HttpConstant.NEWS_CONTENT, jsonParam, new HttpListener<NewsContentJson>() {
            @Override
            protected void onResponse(NewsContentJson newsContentJson) {
                //创建相关文章
                newsContentActivity.commentPresenter.createMoreView(newsContentJson.getArticle());

                CommentAdapter commentAdapter = new CommentAdapter(mContext, newsContentJson.getComment());
                newsContentActivity.ptrLvMessage.setAdapter(commentAdapter);

                if (newsContentJson.getComment() == null || newsContentJson.getComment().size() == 0) {
                    newsContentActivity.commentPresenter.createNoneComment();
                } else {//有数据显示加载更多
                    addFootLoadMore();
                }

                newsContentActivity.createWebClass(newsContentJson);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
            }
        });
    }


    /**
     *
     */
    private void addFootLoadMore() {
        TextView tvLoadMore = new TextView(mContext);
        tvLoadMore.setText("加载更多评论");

        int loadMoreHeight = SystemUtil.dp2px(mContext, 50);
        LinearLayout.LayoutParams commentParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, loadMoreHeight);

        commentParams.gravity = Gravity.CENTER;
        tvLoadMore.setLayoutParams(commentParams);
        tvLoadMore.setGravity(Gravity.CENTER);

        int color = ContextCompat.getColor(mContext, R.color.blue);
        tvLoadMore.setTextColor(color);

        newsContentActivity.ptrLvMessage.getRefreshableView().addFooterView(tvLoadMore);
    }
}
