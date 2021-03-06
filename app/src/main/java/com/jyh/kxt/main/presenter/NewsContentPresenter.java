package com.jyh.kxt.main.presenter;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
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
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.AttentionUtils;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.utils.NativeStore;
import com.jyh.kxt.base.utils.collect.CollectUtils;
import com.jyh.kxt.main.json.NewsContentJson;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.main.ui.activity.NewsContentActivity;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.ui.LoginActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.bean.EventBusClass;
import com.library.util.JsonUtil;
import com.library.util.SystemUtil;
import com.library.widget.handmark.PullToRefreshBase;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


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
    private String type;
    private AlertDialog loginPop;

    public NewsContentJson newsContentJson;

    public NewsContentPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    /**
     * 请求初始化评论
     *
     * @param pullMode
     * @param type
     */
    public void requestInitComment(final PullToRefreshBase.Mode pullMode, String type) {

        VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);
        JSONObject jsonParam = volleyRequest.getJsonParam();

        jsonParam.put(VarConstant.HTTP_ID, newsContentActivity.objectId);

        if (pullMode == PullToRefreshBase.Mode.PULL_FROM_END) {
            CommentBean commentBean = commentList.get(commentList.size() - 1);
            jsonParam.put(VarConstant.HTTP_LASTID, commentBean.getId());
        } else if (pullMode == PullToRefreshBase.Mode.PULL_FROM_START) {
            UserJson userInfo = LoginUtils.getUserInfo(mContext);
            if (userInfo != null) {
                jsonParam.put(VarConstant.HTTP_UID, userInfo.getUid());
                jsonParam.put(VarConstant.HTTP_ACCESS_TOKEN, userInfo.getToken());
            }
        }

        jsonParam.put("object_id", newsContentActivity.objectId);

        this.type = type;
        String url = HttpConstant.NEWS_CONTENT;
        switch (type) {
            case VarConstant.OCLASS_BLOG:
                url = HttpConstant.EXPLORE_BLOG_CONTENT;
                jsonParam.put("type", "blog_article");
                break;
            case VarConstant.OCLASS_NEWS:
                url = HttpConstant.NEWS_CONTENT;
                jsonParam.put("type", "article");
                break;
        }

        switch (pullMode) {
            case PULL_FROM_END:
                url = HttpConstant.COMMENT_LIST;
                break;
        }

        volleyRequest.doPost(url, jsonParam, new HttpListener<String>() {
            @Override
            protected void onResponse(String json) {
                if (pullMode == PullToRefreshBase.Mode.PULL_FROM_START) {

                    newsContentJson = JSONObject.parseObject(json, NewsContentJson.class);

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
                    List<CommentBean> comment = JsonUtil.parseArray(json, CommentBean.class);

                    if (comment.size() == 0) {
                        tvLoadMore.setText("暂无更多评论");
                        return;
                    }

                    commentList.addAll(comment);
                    commentAdapter.notifyDataSetChanged();
                }
                newsContentActivity.pllContent.loadOver();
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                newsContentActivity.pllContent.loadError();
                try {
                    tvLoadMore.setText("暂无更多评论");
                    tvLoadMore.setTag("idle");
                    if (commentList == null || commentList.size() == 0) {
                        newsContentActivity.pllContent.loadEmptyData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void requestAttention(final boolean isCheck, String author_id, final ImageView cbLike) {

        if (!LoginUtils.isLogined(mContext)) {
            if (loginPop == null) {
                loginPop = new AlertDialog.Builder(mContext)
                        .setTitle("温馨提示")
                        .setMessage("请先登录")
                        .setNegativeButton("登录", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mContext.startActivity(new Intent(mContext, LoginActivity.class));
                            }
                        }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create();
            }
            if (loginPop.isShowing()) {
                loginPop.dismiss();
            }
            loginPop.show();
            return;
        }

        EventBusClass event = new EventBusClass(EventBusClass.EVENT_ATTENTION_OTHER, !isCheck);
        EventBus.getDefault().post(event);

        if (isCheck) {
            cbLike.setSelected(!isCheck);
            AttentionUtils.unAttention(mContext, AttentionUtils.TYPE_AUTHOR, author_id, new ObserverData<Boolean>() {
                @Override
                public void callback(Boolean aBoolean) {
                }

                @Override
                public void onError(Exception e) {
                }
            });
        } else {
            cbLike.setSelected(!isCheck);
            AttentionUtils.attention(mContext, AttentionUtils.TYPE_AUTHOR, author_id, new ObserverData<Boolean>() {
                @Override
                public void callback(Boolean aBoolean) {
                }

                @Override
                public void onError(Exception e) {
                }
            });
        }

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
                if (currentTimeMillis - 3000 > oldTime) {
                    if (!"loading".equals(tvLoadMore.getTag()) && firstVisibleItem + visibleItemCount ==
                            totalItemCount) {
                        tvLoadMore.setTag("loading");
                        tvLoadMore.setText("更多评论加载中,请稍等...");

                        requestInitComment(PullToRefreshBase.Mode.PULL_FROM_END, type);
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
                    requestInitComment(PullToRefreshBase.Mode.PULL_FROM_END, type);
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

    public void collect(String objectId, NewsContentJson newsContentJson, String type) {

//        switch (type) {
//            case VarConstant.OCLASS_BLOG:
//
//                if (!LoginUtils.isLogined(mContext)) {
//                    if (loginPop == null) {
//                        loginPop = new AlertDialog.Builder(mContext)
//                                .setTitle("提醒")
//                                .setMessage("请先登录")
//                                .setNegativeButton("登录", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        mContext.startActivity(new Intent(mContext, LoginOrRegisterActivity.class));
//                                    }
//                                }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                    }
//                                }).create();
//                    }
//                    if (loginPop.isShowing()) {
//                        loginPop.dismiss();
//                    }
//                    loginPop.show();
//                    return;
//                }
//
//                if (newsContentActivity.isCollect) {
//                    snackBar = TSnackbar.make(newsContentActivity.pllContent, "取消关注中...", TSnackbar
//                            .LENGTH_INDEFINITE, TSnackbar
//                            .APPEAR_FROM_TOP_TO_DOWN);
//                    snackBar.setPromptThemBackground(Prompt.SUCCESS);
//                    snackBar.addIconProgressLoading(0, true, false);
//                    snackBar.show();
//                    AttentionUtils.unAttention(mContext, AttentionUtils.TYPE_NEWS, objectId, new
//                            ObserverData<Boolean>() {
//                                @Override
//                                public void callback(Boolean aBoolean) {
//                                    snackBar.setPromptThemBackground(Prompt.SUCCESS).setText("取消关注成功").setDuration
//                                            (TSnackbar
//                                                    .LENGTH_LONG)
//                                            .setMinHeight(SystemUtil.getStatuBarHeight(mContext), mContext
//                                                    .getResources()
//                                                    .getDimensionPixelOffset(R.dimen.actionbar_height)).show();
//                                    newsContentActivity.ivCollect.setSelected(false);
//                                    newsContentActivity.isCollect = false;
//                                }
//
//                                @Override
//                                public void onError(Exception e) {
//                                    snackBar.setPromptThemBackground(Prompt.ERROR).setText("取消关注失败").setDuration
//                                            (TSnackbar
//                                                    .LENGTH_LONG)
//                                            .setMinHeight(SystemUtil.getStatuBarHeight(mContext), mContext
//                                                    .getResources()
//                                                    .getDimensionPixelOffset(R.dimen.actionbar_height)).show();
//                                }
//                            });
//                } else {
//                    snackBar = TSnackbar.make(newsContentActivity.pllContent, "关注中...", TSnackbar.LENGTH_INDEFINITE,
//                            TSnackbar
//                                    .APPEAR_FROM_TOP_TO_DOWN);
//                    snackBar.setPromptThemBackground(Prompt.SUCCESS);
//                    snackBar.addIconProgressLoading(0, true, false);
//                    snackBar.show();
//                    AttentionUtils.attention(mContext, AttentionUtils.TYPE_NEWS, objectId, new
// ObserverData<Boolean>() {
//                        @Override
//                        public void callback(Boolean aBoolean) {
//                            snackBar.setPromptThemBackground(Prompt.SUCCESS).setText("关注成功").setDuration(TSnackbar
//                                    .LENGTH_LONG)
//                                    .setMinHeight(SystemUtil.getStatuBarHeight(mContext), mContext.getResources()
//                                            .getDimensionPixelOffset(R.dimen.actionbar_height)).show();
//                            newsContentActivity.ivCollect.setSelected(true);
//                            newsContentActivity.isCollect = true;
//                        }
//
//                        @Override
//                        public void onError(Exception e) {
//                            snackBar.setPromptThemBackground(Prompt.ERROR).setText("关注失败").setDuration(TSnackbar
//                                    .LENGTH_LONG)
//                                    .setMinHeight(SystemUtil.getStatuBarHeight(mContext), mContext.getResources()
//                                            .getDimensionPixelOffset(R.dimen.actionbar_height)).show();
//                        }
//                    });
//                }
//                break;
//            case VarConstant.OCLASS_NEWS:
        final NewsJson newsJson = new NewsJson();
        newsJson.setAuthor(newsContentJson.getAuthor_name());
        newsJson.setDataType(VarConstant.DB_TYPE_COLLECT_LOCAL);
        newsJson.setDatetime(newsContentJson.getCreate_time());
        newsJson.setTitle(newsContentJson.getTitle());
        newsJson.setHref("");
        newsJson.setO_action(VarConstant.OACTION_DETAIL);
        if (VarConstant.OCLASS_BLOG.equals(type)) {
            newsJson.setO_class(VarConstant.OCLASS_BLOG);
        } else {
            newsJson.setO_class(VarConstant.OCLASS_NEWS);
        }
        newsJson.setO_id(objectId);
        newsJson.setPicture(newsContentJson.getPicture());
        newsJson.setType(newsContentJson.getType());
        if (newsContentActivity.isCollect) {

            newsContentActivity.ivCollect.setSelected(false);
            newsContentActivity.isCollect = false;
            EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_COLLECT_NEWS, newsJson));

            CollectUtils.unCollect(mContext, VarConstant.COLLECT_TYPE_ARTICLE, newsJson, new ObserverData() {
                @Override
                public void callback(Object o) {
                }

                @Override
                public void onError(Exception e) {
                }
            }, null);
        } else {
            newsContentActivity.ivCollect.setSelected(true);
            newsContentActivity.isCollect = true;
            EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_COLLECT_NEWS, newsJson));

            CollectUtils.collect(mContext, VarConstant.COLLECT_TYPE_ARTICLE, newsJson, new ObserverData() {
                @Override
                public void callback(Object o) {
                }

                @Override
                public void onError(Exception e) {
                }
            }, null);
        }
//                break;
//        }
    }

    /**
     * 点赞
     */
    public void attention(String objectId) {
        try {
            if (objectId == null) {
                return;
            }
            if (newsContentActivity.isGood) {
            } else {
                newsContentActivity.mThumbView3.startGiveAnimation();
                String goodType/* = VarConstant.GOOD_TYPE_NEWS*/ = null; //文章点赞
                switch (type) {
                    case VarConstant.OCLASS_BLOG:
                        goodType = VarConstant.GOOD_TYPE_BLOG;
                        break;
                    case VarConstant.OCLASS_NEWS:
                        goodType = VarConstant.GOOD_TYPE_NEWS;
                        break;
                }
                newsContentActivity.ivGood.setSelected(true);
                newsContentActivity.isGood = true;
                if (newsContentActivity.webViewAndHead != null &&
                        newsContentActivity.webViewAndHead.attention != null) {
                    newsContentActivity.webViewAndHead.attention.attention2();//这里再发送网络请求就是重复请求了!
                }

                String dianZanCount = newsContentActivity.tvDianCount.getText().toString();
                String addDianZanCount = null;
                try {
                    addDianZanCount = String.valueOf(Integer.parseInt(dianZanCount) + 1);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    addDianZanCount = "1";
                    newsContentActivity.tvDianCount.setVisibility(View.VISIBLE);
                }
                newsContentActivity.tvDianCount.setText(addDianZanCount);

                NativeStore.addThumbID(mContext, goodType, objectId, new ObserverData() {
                    @Override
                    public void callback(Object o) {

                    }

                    @Override
                    public void onError(Exception e) {
                    }
                }, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
