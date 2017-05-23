package com.jyh.kxt.main.presenter;

import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.av.adapter.CommentAdapter;
import com.jyh.kxt.av.json.CommentBean;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.main.json.NewsContentJson;
import com.jyh.kxt.main.ui.activity.NewsContentActivity;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.util.SystemUtil;
import com.library.widget.handmark.PullToRefreshBase;
import com.trycatch.mysnackbar.TSnackbar;

import java.util.List;

import static com.jyh.kxt.base.constant.HttpConstant.EXPLORE_BLOG_ADDFAVOR;
import static com.jyh.kxt.base.constant.HttpConstant.EXPLORE_BLOG_DELETEFAVOR;
import static com.jyh.kxt.base.constant.HttpConstant.MEMBER_FAVOR_WRITER;


/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/18.
 */

public class NewsContentPresenter extends BasePresenter {

    @BindObject public NewsContentActivity newsContentActivity;
    public List<CommentBean> commentList;
    public CommentAdapter commentAdapter;

    public NewsContentPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    /**
     * 请求初始化评论
     *
     * @param pullMode
     */
    public void requestInitComment(final PullToRefreshBase.Mode pullMode) {

        VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);
        JSONObject jsonParam = volleyRequest.getJsonParam();

        jsonParam.put("id", newsContentActivity.objectId);

        if (pullMode == PullToRefreshBase.Mode.PULL_FROM_END) {
            CommentBean commentBean = commentList.get(commentList.size() - 1);
            jsonParam.put("last_id", commentBean.getId());
        } else if (pullMode == PullToRefreshBase.Mode.PULL_FROM_START) {
            UserJson userInfo = LoginUtils.getUserInfo(mContext);
            if (userInfo != null) {
                jsonParam.put("uid", userInfo.getUid());
                jsonParam.put("token", userInfo.getToken());
            }
        }
        volleyRequest.doGet(HttpConstant.NEWS_CONTENT, jsonParam, new HttpListener<NewsContentJson>() {
            @Override
            protected void onResponse(NewsContentJson newsContentJson) {
                if (pullMode == PullToRefreshBase.Mode.PULL_FROM_START) {

                    commentList = newsContentJson.getComment();
                    //创建相关文章
                    newsContentActivity.commentPresenter.createMoreView(newsContentJson.getArticle());
                    commentAdapter = new CommentAdapter(mContext, commentList, NewsContentPresenter.this);
                    newsContentActivity.ptrLvMessage.setAdapter(commentAdapter);

                    if (newsContentJson.getComment() == null || newsContentJson.getComment().size() == 0) {
                        newsContentActivity.commentPresenter.createNoneComment();
                    } else {//有数据显示加载更多
                        addFootLoadMore();
                    }
                    newsContentActivity.createWebClass(newsContentJson);

                } else {
                    tvLoadMore.setText("加载更多评论");
                    tvLoadMore.setTag("idle");

                    List<CommentBean> comment = newsContentJson.getComment();

                    if (comment.size() == 0) {
                        TSnackbar.make(newsContentActivity.ptrLvMessage,
                                "暂无更多评论",
                                Snackbar.LENGTH_LONG,
                                TSnackbar.APPEAR_FROM_TOP_TO_DOWN)
                                .setMinHeight(SystemUtil.getStatuBarHeight(mContext), mContext.getResources()
                                        .getDimensionPixelOffset(R.dimen.actionbar_height));
                        return;
                    }

                    commentList.addAll(comment);
                    commentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
            }
        });
    }

    public void requestAttention(final boolean isCheck, String author_id) {
        VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);
        JSONObject jsonParam = volleyRequest.getJsonParam();

        jsonParam.put("id", author_id);

        UserJson userInfo = LoginUtils.getUserInfo(mContext);
        if (userInfo != null) {
            jsonParam.put("uid", userInfo.getUid());
            jsonParam.put("accessToken", userInfo.getToken());
        }

        String url;
        if (isCheck) {
            url = EXPLORE_BLOG_ADDFAVOR;
        } else {
            url = EXPLORE_BLOG_DELETEFAVOR;
        }

        volleyRequest.doGet(url, jsonParam, new HttpListener<String>() {
            @Override
            protected void onResponse(String json) {
                if(isCheck){

                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
            }
        });
    }

    private TextView tvLoadMore;
    private long oldTime;

    private void addFootLoadMore() {
        tvLoadMore = new TextView(mContext);
        tvLoadMore.setText("加载更多评论");

        int loadMoreHeight = SystemUtil.dp2px(mContext, 50);
        AbsListView.LayoutParams commentParams = new AbsListView.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, loadMoreHeight);

        tvLoadMore.setLayoutParams(commentParams);
        tvLoadMore.setGravity(Gravity.CENTER);

        int color = ContextCompat.getColor(mContext, R.color.blue);
        tvLoadMore.setTextColor(color);

        newsContentActivity.ptrLvMessage.getRefreshableView().addFooterView(tvLoadMore);

        newsContentActivity.ptrLvMessage.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis - 1000 > oldTime) {
                    if (!"loading".equals(tvLoadMore.getTag()) && firstVisibleItem + visibleItemCount ==
                            totalItemCount) {
                        tvLoadMore.setTag("loading");
                        tvLoadMore.setText("更多评论加载中,请稍等...");

                        requestInitComment(PullToRefreshBase.Mode.PULL_FROM_END);
                    }
                }
                oldTime = currentTimeMillis;
            }
        });
        tvLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"loading".equals(tvLoadMore.getTag())) {
                    tvLoadMore.setTag("loading");
                    tvLoadMore.setText("更多评论加载中,请稍等...");
                    requestInitComment(PullToRefreshBase.Mode.PULL_FROM_END);
                }
            }
        });
    }

    /**
     * 第一次提交
     *
     * @param mCommentBean
     */
    public void commentCommit(CommentBean mCommentBean) {
        try {
            if (commentList.size() == 0) {
                LinearLayout headView = newsContentActivity.commentPresenter.getHeadView();
                View noneComment = headView.findViewWithTag("noneComment");
                headView.removeView(noneComment);

                addFootLoadMore();
            }

            commentList.add(0, mCommentBean);
            commentAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
