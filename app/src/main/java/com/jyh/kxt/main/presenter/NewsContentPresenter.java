package com.jyh.kxt.main.presenter;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
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
import com.jyh.kxt.user.ui.LoginOrRegisterActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.bean.EventBusClass;
import com.library.util.SystemUtil;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.window.ToastView;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
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
    private TSnackbar snackBar;
    private AlertDialog loginPop;

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
        jsonParam.put("type", "article");
        jsonParam.put("object_id", newsContentActivity.objectId);

        this.type = type;
        String url = HttpConstant.NEWS_CONTENT;
        switch (type) {
            case VarConstant.OCLASS_BLOG:
                url = HttpConstant.EXPLORE_BLOG_CONTENT;
                break;
            case VarConstant.OCLASS_NEWS:
                url = HttpConstant.NEWS_CONTENT;
                break;
        }

        switch (pullMode) {
            case PULL_FROM_END:
                url = HttpConstant.COMMENT_LIST;
                break;
        }

        volleyRequest.doGet(url, jsonParam, new HttpListener<String>() {
            @Override
            protected void onResponse(String json) {
                if (pullMode == PullToRefreshBase.Mode.PULL_FROM_START) {

                    NewsContentJson newsContentJson = JSONObject.parseObject(json, NewsContentJson.class);

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
                    List<CommentBean> comment = JSONArray.parseArray(json, CommentBean.class);

                    if (comment.size() == 0) {
                        tvLoadMore.setText("暂无更多评论");
                        return;
                    }

                    commentList.addAll(comment);
                    commentAdapter.notifyDataSetChanged();
                }
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
                        .setTitle("提醒")
                        .setMessage("请先登录")
                        .setNegativeButton("登录", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mContext.startActivity(new Intent(mContext, LoginOrRegisterActivity.class));
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

        if (isCheck) {
            snackBar = TSnackbar.make(newsContentActivity.pllContent, "取消关注中...", TSnackbar.LENGTH_INDEFINITE, TSnackbar
                    .APPEAR_FROM_TOP_TO_DOWN);
            snackBar.setPromptThemBackground(Prompt.SUCCESS);
            snackBar.addIconProgressLoading(0, true, false);
            snackBar.show();
            AttentionUtils.unAttention(mContext, AttentionUtils.TYPE_AUTHOR, author_id, new ObserverData<Boolean>() {
                @Override
                public void callback(Boolean aBoolean) {
                    snackBar.setPromptThemBackground(Prompt.SUCCESS).setText("取消关注成功").setDuration(TSnackbar
                            .LENGTH_LONG)
                            .setMinHeight(SystemUtil.getStatuBarHeight(mContext), mContext.getResources()
                                    .getDimensionPixelOffset(R.dimen.actionbar_height)).show();
                    cbLike.setSelected(!isCheck);
                }

                @Override
                public void onError(Exception e) {
                    snackBar.setPromptThemBackground(Prompt.ERROR).setText("取消关注失败").setDuration(TSnackbar.LENGTH_LONG)
                            .setMinHeight(SystemUtil.getStatuBarHeight(mContext), mContext.getResources()
                                    .getDimensionPixelOffset(R.dimen.actionbar_height)).show();
                }
            });
        } else {
            snackBar = TSnackbar.make(newsContentActivity.pllContent, "关注中...", TSnackbar.LENGTH_INDEFINITE, TSnackbar
                    .APPEAR_FROM_TOP_TO_DOWN);
            snackBar.setPromptThemBackground(Prompt.SUCCESS);
            snackBar.addIconProgressLoading(0, true, false);
            snackBar.show();
            AttentionUtils.attention(mContext, AttentionUtils.TYPE_AUTHOR, author_id, new ObserverData<Boolean>() {
                @Override
                public void callback(Boolean aBoolean) {
                    snackBar.setPromptThemBackground(Prompt.SUCCESS).setText("关注成功").setDuration(TSnackbar.LENGTH_LONG)
                            .setMinHeight(SystemUtil.getStatuBarHeight(mContext), mContext.getResources()
                                    .getDimensionPixelOffset(R.dimen.actionbar_height)).show();
                    cbLike.setSelected(!isCheck);
                }

                @Override
                public void onError(Exception e) {
                    snackBar.setPromptThemBackground(Prompt.ERROR).setText("关注失败").setDuration(TSnackbar.LENGTH_LONG)
                            .setMinHeight(SystemUtil.getStatuBarHeight(mContext), mContext.getResources()
                                    .getDimensionPixelOffset(R.dimen.actionbar_height)).show();
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
                if (currentTimeMillis - 1000 > oldTime) {
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
//                    AttentionUtils.attention(mContext, AttentionUtils.TYPE_NEWS, objectId, new ObserverData<Boolean>() {
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
            snackBar = TSnackbar.make(newsContentActivity.pllContent, "取消收藏中...", TSnackbar
                    .LENGTH_INDEFINITE, TSnackbar
                    .APPEAR_FROM_TOP_TO_DOWN);
            snackBar.setPromptThemBackground(Prompt.SUCCESS);
            snackBar.addIconProgressLoading(0, true, false);
            snackBar.show();
            CollectUtils.unCollect(mContext, VarConstant.COLLECT_TYPE_ARTICLE, newsJson, new ObserverData() {
                @Override
                public void callback(Object o) {
                    snackBar.setPromptThemBackground(Prompt.SUCCESS).setText("取消收藏成功").setDuration(TSnackbar
                            .LENGTH_LONG)
                            .setMinHeight(SystemUtil.getStatuBarHeight(mContext), mContext.getResources()
                                    .getDimensionPixelOffset(R.dimen.actionbar_height)).show();
                    newsContentActivity.ivCollect.setSelected(false);
                    newsContentActivity.isCollect = false;
                    EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_COLLECT_NEWS, newsJson));
                }

                @Override
                public void onError(Exception e) {
                    snackBar.setPromptThemBackground(Prompt.ERROR).setText("取消收藏失败").setDuration(TSnackbar
                            .LENGTH_LONG)
                            .setMinHeight(SystemUtil.getStatuBarHeight(mContext), mContext.getResources()
                                    .getDimensionPixelOffset(R.dimen.actionbar_height)).show();
                }
            }, null);
        } else {
            snackBar = TSnackbar.make(newsContentActivity.pllContent, "收藏中...", TSnackbar.LENGTH_INDEFINITE,
                    TSnackbar
                            .APPEAR_FROM_TOP_TO_DOWN);
            snackBar.setPromptThemBackground(Prompt.SUCCESS);
            snackBar.addIconProgressLoading(0, true, false);
            snackBar.show();
            CollectUtils.collect(mContext, VarConstant.COLLECT_TYPE_ARTICLE, newsJson, new ObserverData() {
                @Override
                public void callback(Object o) {
                    snackBar.setPromptThemBackground(Prompt.SUCCESS).setText("收藏成功").setDuration(TSnackbar
                            .LENGTH_LONG)
                            .setMinHeight(SystemUtil.getStatuBarHeight(mContext), mContext.getResources()
                                    .getDimensionPixelOffset(R.dimen.actionbar_height)).show();
                    newsContentActivity.ivCollect.setSelected(true);
                    newsContentActivity.isCollect = true;
                    EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_COLLECT_NEWS, newsJson));
                }

                @Override
                public void onError(Exception e) {
                    snackBar.setPromptThemBackground(Prompt.ERROR).setText("收藏失败").setDuration(TSnackbar
                            .LENGTH_LONG)
                            .setMinHeight(SystemUtil.getStatuBarHeight(mContext), mContext.getResources()
                                    .getDimensionPixelOffset(R.dimen.actionbar_height)).show();
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
        if (newsContentActivity.isGood) {
            snackBar = TSnackbar.make(newsContentActivity.pllContent, "已经赞过了喔", Snackbar.LENGTH_LONG, TSnackbar
                    .APPEAR_FROM_TOP_TO_DOWN)
                    .setMinHeight(SystemUtil.getStatuBarHeight(mContext), mContext.getResources()
                            .getDimensionPixelOffset(R.dimen.actionbar_height));

            int color = ContextCompat.getColor(mContext, R.color.red_btn_bg_color);
            snackBar.setBackgroundColor(color);
            snackBar.setPromptThemBackground(Prompt.WARNING);
            snackBar.show();
        } else {
            snackBar = TSnackbar.make(newsContentActivity.pllContent, "点赞中...", TSnackbar.LENGTH_INDEFINITE, TSnackbar
                    .APPEAR_FROM_TOP_TO_DOWN);
            snackBar.setPromptThemBackground(Prompt.SUCCESS);
            snackBar.addIconProgressLoading(0, true, false);
            snackBar.show();

            String goodType = VarConstant.GOOD_TYPE_NEWS; //文章点赞
//            switch (type) {
//                case VarConstant.OCLASS_BLOG:
//                    goodType = VarConstant.GOOD_TYPE_COMMENT_BLOG;
//                    break;
//                case VarConstant.OCLASS_NEWS:
//                    goodType = VarConstant.GOOD_TYPE_COMMENT_NEWS;
//                    break;
//            }
            NativeStore.addThumbID(mContext, goodType, objectId, new ObserverData() {
                @Override
                public void callback(Object o) {

                    snackBar.setPromptThemBackground(Prompt.SUCCESS).setText("点赞成功").setDuration(TSnackbar.LENGTH_LONG)
                            .setMinHeight(SystemUtil.getStatuBarHeight(mContext), mContext.getResources()
                                    .getDimensionPixelOffset(R.dimen.actionbar_height)).show();
                    newsContentActivity.ivGood.setSelected(true);
                    newsContentActivity.isGood = true;

                    if (newsContentActivity.webViewAndHead != null && newsContentActivity.webViewAndHead.attention !=
                            null) {
                        newsContentActivity.webViewAndHead.attention.attention();
                    }

                    String dianZanCount = newsContentActivity.tvDianCount.getText().toString();
                    String addDianZanCount = String.valueOf(Integer.parseInt(dianZanCount) + 1);
                    newsContentActivity.tvDianCount.setText(addDianZanCount);
                }

                @Override
                public void onError(Exception e) {
                    snackBar.setPromptThemBackground(Prompt.ERROR).setText("点赞失败").setDuration(TSnackbar.LENGTH_LONG)
                            .setMinHeight(SystemUtil.getStatuBarHeight(mContext), mContext.getResources()
                                    .getDimensionPixelOffset(R.dimen.actionbar_height)).show();
                }
            }, null);
        }
    }

    /***
     * 功能：用线程保存图片
     *
     * @author wangyp
     */
    public class SaveImage extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                String sdcard = Environment.getExternalStorageDirectory()
                        .toString();
                File file = new File(sdcard + "/Download");
                if (!file.exists()) {
                    file.mkdirs();
                }
                String imgurl = params[0];
                int idx = imgurl.lastIndexOf(".");
                String ext = imgurl.substring(idx);
                file = new File(sdcard + "/Download/" + new Date().getTime()
                        + ext);
                InputStream inputStream = null;
                URL url = new URL(imgurl);
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(20000);
                if (conn.getResponseCode() == 200) {
                    inputStream = conn.getInputStream();
                }
                byte[] buffer = new byte[4096];
                int len = 0;
                FileOutputStream outStream = new FileOutputStream(file);
                while ((len = inputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                outStream.close();
                result = "图片已保存至：" + file.getAbsolutePath();
                mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.fromFile(file)));
            } catch (Exception e) {
                result = "保存失败！" + e.getLocalizedMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            ToastView.makeText3(mContext, result);
        }
    }
}
