package com.jyh.kxt.main.ui.activity;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.av.json.CommentBean;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.presenter.CommentPresenter;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.main.json.NewsContentJson;
import com.jyh.kxt.main.presenter.NewsContentPresenter;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;
import com.library.widget.window.ToastView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:要闻详情
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/18.
 */

public class NewsContentActivity extends BaseActivity implements CommentPresenter.OnCommentClickListener,
        CommentPresenter.OnCommentPublishListener {

    @BindView(R.id.pll_content) PageLoadLayout pllContent;
    @BindView(R.id.rv_message) public PullToRefreshListView ptrLvMessage;

    private NewsContentPresenter newsContentPresenter;
    public CommentPresenter commentPresenter;
    private WebViewAndHead webViewAndHead;

    public String objectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_news_content, StatusBarColor.THEME1);

        objectId = getIntent().getStringExtra(IntentConstant.O_ID);//获取传递过来的Id

        newsContentPresenter = new NewsContentPresenter(this);
        ptrLvMessage.setMode(PullToRefreshBase.Mode.DISABLED);

        commentPresenter = new CommentPresenter(this);//初始化评论相关
        commentPresenter.bindListView(ptrLvMessage);
        webViewAndHead = new WebViewAndHead();
        newsContentPresenter.requestInitComment(PullToRefreshBase.Mode.PULL_FROM_START);

        pllContent.loadWait();

        commentPresenter.setOnCommentClickListener(this);
        commentPresenter.setOnCommentPublishListener(this);
    }

    @OnClick({R.id.iv_break, R.id.iv_comment, R.id.iv_collect, R.id.iv_ding, R.id.iv_share})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_break:
                onBackPressed();
                break;
            case R.id.iv_comment:
                //回复
                ptrLvMessage.getRefreshableView().setSelection(2);
                break;
            case R.id.iv_collect:
                //收藏
                break;
            case R.id.iv_ding:
                //点赞
                break;
            case R.id.iv_share:
                //分享
                break;
        }
    }

    @Override
    public void onClickView(CommentPresenter.ClickName clickName) {
        switch (clickName) {
            case NONE_COMMENT:
                commentPresenter.showReplyMessageView(ptrLvMessage);
                break;
        }
    }

    /**
     * 创建WebView 和 Head头部
     *
     * @param newsContentJson
     */
    public void createWebClass(NewsContentJson newsContentJson) {
        webViewAndHead.createWebViewAndHead(newsContentJson);
    }

    @Override
    public void onPublish(PopupWindow popupWindow, EditText etContent) {
        webViewAndHead.requestIssueComment(popupWindow, etContent);
    }

    class WebViewAndHead {
        @BindView(R.id.tv_title) TextView tvTitle;
        @BindView(R.id.iv_photo) RoundImageView ivPhoto;
        @BindView(R.id.tv_name) TextView tvName;
        @BindView(R.id.tv_type) TextView tvType;
        @BindView(R.id.tv_time) TextView tvTime;
        @BindView(R.id.iv_like) CheckBox cbLike;

        @BindView(R.id.rl_exist_author) RelativeLayout rlExistAuthor;
        @BindView(R.id.rl_not_author) RelativeLayout rlNotAuthor;

        @BindView(R.id.tv_news_type) TextView tvNewsType;
        @BindView(R.id.tv_news_time) TextView tvNewsTime;
        @BindView(R.id.wv_content) WebView wvContent;

        private LinearLayout headView;
        private NewsContentJson newsContentJson;

        /**
         * ----------------创建顶部的Head
         */
        public void createWebViewAndHead(NewsContentJson newsContentJson) {
            this.newsContentJson = newsContentJson;
            this.headView = commentPresenter.getHeadView();

            //头部
            LinearLayout llFullContent = (LinearLayout) LayoutInflater.from(NewsContentActivity.this).
                    inflate(R.layout.layout_news_content_head_title, headView, false);

            ButterKnife.bind(this, llFullContent);


            if ("0".equals(newsContentJson.getAuthor_id())) {
                rlExistAuthor.setVisibility(View.VISIBLE);
                Glide.with(NewsContentActivity.this)
                        .load(HttpConstant.IMG_URL + newsContentJson.getPicture())
                        .asBitmap()
                        .override(50, 50)
                        .thumbnail(0.5f)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap>
                                    glideAnimation) {
                                ivPhoto.setImageBitmap(resource);
                            }
                        });

                tvName.setText(newsContentJson.getAuthor_name());

                boolean isFollow = "1".equals(newsContentJson.getIs_follow());
                cbLike.setChecked(isFollow);
            } else {
                rlNotAuthor.setVisibility(View.VISIBLE);
            }

            tvTitle.setText(newsContentJson.getTitle());
            tvType.setText(newsContentJson.getTypeName());

            long createTime = Long.parseLong(newsContentJson.getCreate_time()) * 1000;
            tvTime.setText(DateFormat.format("yyyy-MM-dd HH:mm:ss", createTime));

            /**
             * ----------  创建WebView
             */
            String webContent = newsContentJson.getContent();

            WebSettings settings = wvContent.getSettings();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }

            settings.setLoadWithOverviewMode(true);
            settings.setBlockNetworkImage(false);

            settings.setJavaScriptEnabled(true);
            settings.setAppCacheEnabled(true);

            settings.setDefaultTextEncodingName("utf-8");
            settings.setLoadWithOverviewMode(true);
            wvContent.loadDataWithBaseURL("", webContent, "text/html", "utf-8", "");

            wvContent.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    pllContent.loadOver();
                }
            });
            headView.addView(llFullContent, 0);


            /**
             * 创建分享的
             */
            LinearLayout llShareContent = (LinearLayout) LayoutInflater.from(NewsContentActivity.this).
                    inflate(R.layout.layout_news_content_head_share, headView, false);
            headView.addView(llShareContent, 1);
        }

        public void setTextZoom(int zoom) {
            WebSettings settings = wvContent.getSettings();
            settings.setTextZoom(zoom);
            wvContent.reload();
        }

        /**
         * 发表评论的
         *
         * @param popupWindow
         * @param commentEdit
         */
        public void requestIssueComment(final PopupWindow popupWindow, final EditText commentEdit) {
            String commentContent = commentEdit.getText().toString();
            if (commentContent.trim().length() == 0) {
                commentEdit.setText("");
                commentEdit.setError("评论不能为空!");
                return;
            }

            UserJson userInfo = LoginUtils.getUserInfo(getContext());
            if (userInfo == null) {
                ToastView.makeText(getContext(), "先去登录?");
                return;
            }

            pllContent.loadWait(PageLoadLayout.BgColor.TRANSPARENT8,"提交中...");

            VolleyRequest volleyRequest = new VolleyRequest(getContext(), getQueue());
            JSONObject jsonParam = volleyRequest.getJsonParam();
            jsonParam.put("type", "article");
            jsonParam.put("object_id", objectId);
            jsonParam.put("uid", userInfo.getUid());
            jsonParam.put("accessToken", userInfo.getToken());
            jsonParam.put("content", commentContent);

            volleyRequest.doPost(HttpConstant.COMMENT_PUBLISH, jsonParam, new HttpListener<CommentBean>() {
                @Override
                protected void onResponse(CommentBean mCommentBean) {
                    popupWindow.dismiss();
                    commentEdit.setText("");

                    newsContentPresenter.commentFirstCommit(mCommentBean);

                    pllContent.loadOver();
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                }
            });
        }
    }
}
