package com.jyh.kxt.main.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatDelegate;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
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
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.json.UmengShareBean;
import com.jyh.kxt.base.presenter.CommentPresenter;
import com.jyh.kxt.base.util.PopupUtil;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.utils.NativeStore;
import com.jyh.kxt.base.utils.SaveImage;
import com.jyh.kxt.base.utils.ToastSnack;
import com.jyh.kxt.base.utils.UmengShareUI;
import com.jyh.kxt.base.utils.UmengShareUtil;
import com.jyh.kxt.base.utils.collect.CollectLocalUtils;
import com.jyh.kxt.base.widget.AdvertImageLayout;
import com.jyh.kxt.base.widget.SelectLineView;
import com.jyh.kxt.base.widget.SelectedImageView;
import com.jyh.kxt.base.widget.ThumbView2;
import com.jyh.kxt.base.widget.ThumbView3;
import com.jyh.kxt.base.widget.night.ThemeUtil;
import com.jyh.kxt.chat.ChatRoomActivity;
import com.jyh.kxt.index.ui.WebActivity;
import com.jyh.kxt.main.json.NewsContentJson;
import com.jyh.kxt.main.json.SlideJson;
import com.jyh.kxt.main.presenter.NewsContentPresenter;
import com.jyh.kxt.push.PushUtil;
import com.jyh.kxt.trading.ui.AuthorActivity;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.ui.LoginActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.manager.ActivityManager;
import com.library.util.RegexValidateUtil;
import com.library.util.SPUtils;
import com.library.util.SystemUtil;
import com.library.widget.PageLoadLayout;
import com.library.widget.ZoomImageView;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;
import com.library.widget.window.ToastView;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.library.base.http.VarConstant.APP_WEB_URL;

/**
 * 项目名:Kxt
 * 类描述:要闻详情
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/18.
 */

public class NewsContentActivity extends BaseActivity implements CommentPresenter.OnCommentClickListener,
        CommentPresenter.OnCommentPublishListener, PageLoadLayout.OnAfreshLoadListener {

    @BindView(R.id.actionbar) RelativeLayout mActionBarLayout;

    @BindView(R.id.pll_content) public PageLoadLayout pllContent;
    @BindView(R.id.rv_message) public PullToRefreshListView ptrLvMessage;
    @BindView(R.id.iv_ding) public ImageView ivGood;

    @BindView(R.id.thumb_view_zan) public ThumbView3 mThumbView3;
    @BindView(R.id.iv_collect) public ImageView ivCollect;
    @BindView(R.id.tv_commentCount) TextView tvCommentCount;
    @BindView(R.id.tv_ding_Count) public TextView tvDianCount;

    @BindView(R.id.news_author_image) RoundImageView newsAuthorImage;
    @BindView(R.id.news_author_nick) TextView newsAuthorNick;
    @BindView(R.id.news_author_like) SelectedImageView newsAuthorLike;
    @BindView(R.id.news_author_chat) TextView newsAuthorChat;
    @BindView(R.id.news_author_line) View newsAuthorLine;
    @BindView(R.id.tv_bar_title) TextView newsTitleBar;

    private NewsContentPresenter newsContentPresenter;
    public CommentPresenter commentPresenter;
    public WebViewAndHead webViewAndHead;

    public String objectId;
    private String title;
    private String shareUrl;
    private String shareImg;
    public boolean isGood;
    public boolean isCollect;
    private boolean isLoadOver;

    private UmengShareUI umengShareUI;
    private PopupUtil mSharePop;
    private String fontS, fontM, fontB;//字体大小代码
    private String content;//内容
    private String night;//夜间模式
    private SelectLineView selectView;
    private TextView tvTheme;
    private LinearLayout llTheme;
    private ImageView ivTheme;
    private TextView tvFontS;
    private TextView tvFontM;
    private TextView tvFontB;
    private View customLayout;
    private int commentCount;
    private String font;//字体大小
    private String type = VarConstant.OCLASS_ARTICLE;
    private String imgStr;

    private String memberId;
    private String authorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_content, StatusBarColor.THEME1);
        ActivityManager
                .getInstance()
                .finishNoCurrentActivity(NewsContentActivity.class, NewsContentActivity.this);  //保证只有一个

        objectId = getIntent().getStringExtra(IntentConstant.O_ID);//获取传递过来的Id
        String typeIntent = getIntent().getStringExtra(IntentConstant.TYPE);
        type = typeIntent == null ? type : typeIntent;

        String contentType = "blog".equals(type) ? VarConstant.GOOD_TYPE_BLOG : VarConstant.GOOD_TYPE_NEWS;
        isGood = NativeStore.isThumbSucceed(this, contentType, objectId);
        ivGood.setSelected(isGood);

        newsContentPresenter = new NewsContentPresenter(this);
        pllContent.setOnAfreshLoadListener(this);
        ptrLvMessage.setMode(PullToRefreshBase.Mode.DISABLED);

        commentPresenter = new CommentPresenter(this);//初始化评论相关
        commentPresenter.bindListView(ptrLvMessage);
        webViewAndHead = new WebViewAndHead();
        newsContentPresenter.requestInitComment(PullToRefreshBase.Mode.PULL_FROM_START, type);

        pllContent.loadWait();

        commentPresenter.setRecommendLabel(0);
        commentPresenter.setOnCommentClickListener(this);
        commentPresenter.setOnCommentPublishListener(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        PushUtil.pushToMainActivity(this);
    }

    private int mWebScrollPosition = 0;

    private int mCommentPosition;

    @OnClick({R.id.iv_break, R.id.rl_comment, R.id.iv_collect, R.id.rl_dian_zan, R.id.iv_share, R.id.news_author_like, R.id
            .news_author_chat, R.id.tv_comment})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_break:
                onBackPressed();
                PushUtil.pushToMainActivity(this);
                break;
            case R.id.rl_comment:
                //获取评论所在位置
                if (mCommentPosition == 0) {
                    mCommentPosition = commentPresenter.tvCommentCountTitle.getTop();
                }

                int firstVisiblePosition = ptrLvMessage.getRefreshableView().getFirstVisiblePosition();

                if (firstVisiblePosition < 2) {
                    mWebScrollPosition = getListViewScrollPosition();
                    ptrLvMessage.getRefreshableView().setSelectionFromTop(2, 0);
                } else {
                    ptrLvMessage.getRefreshableView().setSelectionFromTop(2, 0);

                    int mScrollY = mCommentPosition - mWebScrollPosition;
                    ptrLvMessage.getRefreshableView().smoothScrollBy(-Math.abs(mScrollY), 1000);
                }

                break;
            case R.id.tv_comment:
                commentPresenter.showReplyMessageView(view);
                break;
            case R.id.iv_collect:
                //收藏
                if (isLoadOver) {
                    NewsContentJson newsContentJson = webViewAndHead.newsContentJson;
                    newsContentPresenter.collect(objectId, newsContentJson, newsContentJson.getType());
                }
                break;
            case R.id.rl_dian_zan:
                //点赞
                if (isLoadOver) {
                    newsContentPresenter.attention(objectId);
                }
                break;
            case R.id.iv_share:
                //分享
                if (isLoadOver) {
                    if (mSharePop == null) {
                        initShareLayout();
                    } else {
                        mSharePop.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                    }
                }
                break;
            case R.id.news_author_like:

                break;
            case R.id.news_author_chat:
                if (memberId == null) {
                    ToastView.makeText3(getContext(), "数据错误");
                    return;
                }

                if (LoginUtils.getUserInfo(getContext()) == null) {
                    Intent loginIntent = new Intent(getContext(), LoginActivity.class);
                    startActivity(loginIntent);
                    return;
                }

                Intent intent = new Intent(this, ChatRoomActivity.class);
                intent.putExtra(IntentConstant.U_ID, memberId);
                intent.putExtra(IntentConstant.NAME, authorName);
                startActivity(intent);
                break;
        }
    }

    private int getListViewScrollPosition() {
        try {
            View childAt0 = ptrLvMessage.getRefreshableView().getChildAt(0);
            return Math.abs(childAt0.getTop());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void initShareLayout() {
        UmengShareBean umengShareBean = new UmengShareBean();
        umengShareBean.setFromSource(UmengShareUtil.SHARE_ARTICLE);
        umengShareBean.setTitle(title);
        umengShareBean.setWebUrl(shareUrl);
        umengShareBean.setImageUrl(shareImg);

        umengShareBean.setSinaTitle(newsContentPresenter.newsContentJson.getShare_sina_title());//替换微博的

        umengShareUI = new UmengShareUI(this);
        customLayout = umengShareUI.getCustomLayout(R.layout.pop_news_share);
        mSharePop = umengShareUI.showSharePopup(umengShareBean, customLayout);

        int color = ContextCompat.getColor(NewsContentActivity.this, R.color.theme1);
        customLayout.setBackgroundColor(color);

        selectView = (SelectLineView) customLayout.findViewById(R.id.sv_fontSize);
        tvTheme = (TextView) customLayout.findViewById(R.id.tv_theme);
        llTheme = (LinearLayout) customLayout.findViewById(R.id.ll_theme);
        ivTheme = (ImageView) customLayout.findViewById(R.id.iv_theme);
        tvFontS = (TextView) customLayout.findViewById(R.id.tv_font_s);
        tvFontM = (TextView) customLayout.findViewById(R.id.tv_font_m);
        tvFontB = (TextView) customLayout.findViewById(R.id.tv_font_b);

        int alertTheme = ThemeUtil.getAlertTheme(getContext());
        switch (alertTheme) {
            case android.support.v7.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert:
                tvTheme.setText("白天模式");
                break;
            case android.support.v7.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert:
                tvTheme.setText("夜间模式");
                break;
        }

        selectView.setSelectItemListener(new SelectLineView.SelectItemListener() {
            @Override
            public void selectItem(int position) {
                switch (position) {
                    case 0:
                        font = fontS;
                        break;
                    case 1:
                        font = fontM;
                        break;
                    case 2:
                        font = fontB;
                        break;
                }
                SPUtils.save(NewsContentActivity.this, SpConstant.WEBFONTSIZE, position + "");
                int alertTheme = ThemeUtil.getAlertTheme(getContext());
                switch (alertTheme) {
                    case android.support.v7.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert:
                        webViewAndHead.wvContent.loadDataWithBaseURL(APP_WEB_URL, night + font + content, "text/html",
                                "utf-8", "");
                        break;
                    case android.support.v7.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert:
                        webViewAndHead.wvContent.loadDataWithBaseURL(APP_WEB_URL, font + content, "text/html",
                                "utf-8", "");
                        break;
                }
            }
        });

        llTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int alertTheme = ThemeUtil.getAlertTheme(getContext());
                switch (alertTheme) {
                    case android.support.v7.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert:
                        tvTheme.setText("夜间模式");
                        setDayNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        SPUtils.save(NewsContentActivity.this, SpConstant.SETTING_DAY_NIGHT, false);

                        webViewAndHead.wvContent.loadDataWithBaseURL(APP_WEB_URL, font + content, "text/html",
                                "utf-8", "");

                        break;
                    case android.support.v7.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert:
                        tvTheme.setText("白天模式");
                        setDayNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        SPUtils.save(NewsContentActivity.this, SpConstant.SETTING_DAY_NIGHT, true);

                        webViewAndHead.wvContent.loadDataWithBaseURL(APP_WEB_URL, night + font + content, "text/html",
                                "utf-8", "");
                        break;
                }
                changePopTheme();
            }
        });
    }

    private void changePopTheme() {
        if (mSharePop == null) {
            return;
        }
        selectView.changeTheme();
        tvTheme.setTextColor(ContextCompat.getColor(NewsContentActivity.this, R.color.font_color60));
        customLayout.setBackgroundColor(ContextCompat.getColor(NewsContentActivity.this, R.color.theme1));
        ivTheme.setImageDrawable(ContextCompat.getDrawable(NewsContentActivity.this, R.mipmap.icon_drawer_theme));
        tvFontS.setTextColor(ContextCompat.getColor(NewsContentActivity.this, R.color.font_color60));
        tvFontM.setTextColor(ContextCompat.getColor(NewsContentActivity.this, R.color.font_color60));
        tvFontB.setTextColor(ContextCompat.getColor(NewsContentActivity.this, R.color.font_color60));
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
        title = newsContentJson.getTitle();
        shareUrl = newsContentJson.getUrl_share();
        shareImg = HttpConstant.IMG_URL + newsContentJson.getPicture();

        fontS = newsContentJson.getFont_small();
        fontM = newsContentJson.getFont_mid();
        fontB = newsContentJson.getFont_big();

        //设置默认文本大小
        String fontSizeStr = SPUtils.getString(NewsContentActivity.this, SpConstant.WEBFONTSIZE);
        if (RegexValidateUtil.isEmpty(fontSizeStr)) {
            font = fontM;
        }
        switch (fontSizeStr) {
            case "0":
                font = fontS;
                break;
            case "1":
                font = fontM;
                break;
            case "2":
                font = fontB;
                break;
        }

        content = newsContentJson.getContent();
        night = newsContentJson.getNight_style();

        try {
            commentCount = Integer.parseInt(newsContentJson.getNum_comment());
            if (commentCount == 0) {
                tvCommentCount.setVisibility(View.GONE);
            } else {
                tvCommentCount.setVisibility(View.VISIBLE);
                tvCommentCount.setText(commentCount + "");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            tvCommentCount.setVisibility(View.GONE);
        }
        try {
            int dianZanCount = Integer.parseInt(newsContentJson.getNum_good());
            if (dianZanCount == 0) {
                tvDianCount.setVisibility(View.GONE);
            } else {
                tvDianCount.setVisibility(View.VISIBLE);
                tvDianCount.setText(dianZanCount + "");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            tvDianCount.setVisibility(View.GONE);
        }

        isLoadOver = true;
        webViewAndHead.createWebViewAndHead(newsContentJson);
    }

    @Override
    public void onPublish(PopupWindow popupWindow, EditText etContent, CommentBean commentBean, int parentId) {
        webViewAndHead.requestIssueComment(popupWindow, etContent, commentBean, parentId);
    }

    @Override
    public void OnAfreshLoad() {
        newsContentPresenter.requestInitComment(PullToRefreshBase.Mode.PULL_FROM_START, type);
        pllContent.loadWait();
    }

    public class WebViewAndHead implements View.OnClickListener {
        @BindView(R.id.tv_title) TextView tvTitle;
        @BindView(R.id.iv_photo) RoundImageView ivPhoto;
        @BindView(R.id.tv_name) TextView tvName;
        @BindView(R.id.tv_type) TextView tvType;
        @BindView(R.id.tv_time) TextView tvTime;
        @BindView(R.id.iv_like) SelectedImageView cbLike;
        @BindView(R.id.head_author_chat) TextView tvHeadAuthorChat;

        @BindView(R.id.news_head_line) View viewLine;

        @BindView(R.id.rl_exist_author) RelativeLayout rlExistAuthor;
        @BindView(R.id.rl_not_author) RelativeLayout rlNotAuthor;

        //        @BindView(R.id.tv_news_type) TextView tvNewsType;
//        @BindView(R.id.tv_news_time) TextView tvNewsTime;

        @BindView(R.id.tv_news_title) TextView tvNewsTitle;
        @BindView(R.id.tv_news_time) TextView tvNewsTime;
        @BindView(R.id.tv_news_label) TextView tvNewsLabel;


        @BindView(R.id.wv_content) public WebView wvContent;
        @BindView(R.id.fl_web_content) FrameLayout flWebContent;

        public LinearLayout headView;
        private NewsContentJson newsContentJson;
        private TextView tvSource;
        private AdvertImageLayout newsContentAd;
        public ThumbView2 attention;
        private boolean isAllowAttention;

        private Bitmap authorBitmap;

        /**
         * ----------------创建顶部的Head
         */
        public void createWebViewAndHead(final NewsContentJson newsContentJson) {
            this.newsContentJson = newsContentJson;
            this.headView = commentPresenter.getHeadView();

            isCollect = CollectLocalUtils.isCollect(getContext(), VarConstant.COLLECT_TYPE_ARTICLE, newsContentJson
                    .getType(), objectId);
            ivCollect.setSelected(isCollect);

            //头部
            final LinearLayout llFullContent = (LinearLayout) LayoutInflater.from(NewsContentActivity.this).
                    inflate(R.layout.layout_news_content_head_title, headView, false);

            ButterKnife.bind(this, llFullContent);

            TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(tvType, R.mipmap.icon_new_bq, 0, 0, 0);
            tvType.setText(newsContentJson.getTypeName());

            TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(tvNewsLabel, R.mipmap.icon_new_bq, 0, 0, 0);
            tvNewsLabel.setText(newsContentJson.getTypeName());

            long createTime = Long.parseLong(newsContentJson.getCreate_time()) * 1000;
            tvTime.setText(DateFormat.format("MM-dd HH:mm", createTime));
            tvNewsTime.setText(" | " + DateFormat.format("yyyy-MM-dd HH:mm", createTime));

            rlExistAuthor.setOnClickListener(this);
            Glide.with(NewsContentActivity.this)
                    .load(newsContentJson.getAuthor_image())
                    .asBitmap()
                    .override(50, 50)
                    .thumbnail(0.5f)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap>
                                glideAnimation) {
                            WebViewAndHead.this.authorBitmap = resource;
                            ivPhoto.setImageBitmap(resource);
                            newsAuthorImage.setImageBitmap(resource);
                        }
                    });

            authorName = newsContentJson.getAuthor_name();
            memberId = newsContentJson.getMember_id();
            if (TextUtils.isEmpty(authorName)) {
                tvName.getLayoutParams().height = 5;
            } else {
                tvName.setText(authorName);
                tvNewsTitle.setText(authorName);

                newsAuthorNick.setText(authorName);
            }

            isAllowAttention = "blog".equals(newsContentJson.getType());


            if (isAllowAttention) {
                rlExistAuthor.setVisibility(View.VISIBLE);
            } else {
                rlNotAuthor.setVisibility(View.VISIBLE);
            }


            UserJson userInfo = LoginUtils.getUserInfo(getContext());
            if (userInfo != null) {
                if (userInfo.getWriter_id() != null && userInfo.getWriter_id().equals(newsContentJson.getAuthor_id())) {
                    isAllowAttention = false;
                }
            }

            if (isAllowAttention) {
                cbLike.setVisibility(View.VISIBLE);
                tvHeadAuthorChat.setVisibility(View.VISIBLE);
                tvHeadAuthorChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (LoginUtils.getUserInfo(getContext()) == null) {
                            Intent loginIntent = new Intent(getContext(), LoginActivity.class);
                            startActivity(loginIntent);
                            return;
                        }
                        if (memberId == null) {
                            ToastView.makeText3(getContext(), "数据错误");
                            return;
                        }
                        Intent intent = new Intent(NewsContentActivity.this, ChatRoomActivity.class);
                        intent.putExtra(IntentConstant.U_ID, memberId);
                        intent.putExtra(IntentConstant.NAME, authorName);
                        startActivity(intent);
                    }
                });

            } else {
                cbLike.setVisibility(View.GONE);
                tvHeadAuthorChat.setVisibility(View.GONE);
            }

            boolean isFollow = "1".equals(newsContentJson.getIs_follow());
            cbLike.setSelected(isFollow);
            cbLike.setOnSelectedListener(new SelectedImageView.OnSelectedListener() {
                @Override
                public void onSelectedUpdate(boolean selected) {
                    if (selected != newsAuthorLike.isSelected()) {
                        newsAuthorLike.setSelected(selected);
                    }
                }
            });

            newsAuthorLike.setSelected(isFollow);
            newsAuthorLike.setOnSelectedListener(new SelectedImageView.OnSelectedListener() {
                @Override
                public void onSelectedUpdate(boolean selected) {
                    if (selected != cbLike.isSelected()) {
                        cbLike.setSelected(selected);
                    }
                }
            });

            cbLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newsContentPresenter.requestAttention(
                            cbLike.isSelected(),
                            WebViewAndHead.this.newsContentJson.getAuthor_id(), cbLike);
                }
            });

            tvTitle.setText(newsContentJson.getTitle());

            flWebContent.setForeground(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.theme1)));
            if (!isAllowAttention) {
                newsTitleBar.setVisibility(View.VISIBLE);
                newsTitleBar.setText("要闻");
            } else {
                ptrLvMessage.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {

                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        //监听是否移动中
                        try {
                            int top = Math.abs(headView.getTop());
                            int linePosition = viewLine.getTop();
                            if (top >= linePosition) {
                                if (!newsAuthorNick.isShown()) {
                                    newsAuthorImage.setVisibility(View.VISIBLE);
                                    newsAuthorNick.setVisibility(View.VISIBLE);

                                    if (isAllowAttention) {
                                        newsAuthorLike.setVisibility(View.VISIBLE);
                                        newsAuthorChat.setVisibility(View.VISIBLE);

                                        newsAuthorLike.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                newsContentPresenter.requestAttention(
                                                        cbLike.isSelected(),
                                                        WebViewAndHead.this.newsContentJson.getAuthor_id(), newsAuthorLike);
                                            }
                                        });
                                    }
                                }
                            } else {
                                if (newsAuthorNick.isShown()) {
                                    newsAuthorImage.setVisibility(View.GONE);
                                    newsAuthorNick.setVisibility(View.GONE);
                                    newsAuthorLike.setVisibility(View.GONE);
                                    newsAuthorChat.setVisibility(View.GONE);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            /**
             * ----------  创建WebView
             */
            final WebSettings settings = wvContent.getSettings();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }

//            settings.setBlockNetworkImage(true);

            settings.setJavaScriptEnabled(true);
            settings.setAppCacheEnabled(true);

            settings.setDefaultTextEncodingName("utf-8");
            settings.setLoadWithOverviewMode(true);

            int alertTheme = ThemeUtil.getAlertTheme(getContext());
            String source = "";
            String sourceStr = newsContentJson.getSource();
            if (RegexValidateUtil.isEmpty(sourceStr)) {
                sourceStr = "";
            }
            switch (alertTheme) {
                case android.support.v7.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert:
                    webViewAndHead.wvContent.loadDataWithBaseURL(APP_WEB_URL, night + font + content, "text/html",
                            "utf-8",
                            "");
                    source = "<font color='#2E3239'>文章来源:</font><font color='#A1ABB2'>" + sourceStr +
                            "</font>";
                    break;
                case android.support.v7.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert:
                    webViewAndHead.wvContent.loadDataWithBaseURL(APP_WEB_URL, font + content, "text/html", "utf-8", "");
                    source = "<font color='#909090'>文章来源:</font><font color='#4D4D4D'>" + sourceStr +
                            "</font>";
                    break;
            }
            wvContent.addJavascriptInterface(new ImgClickListener(), "jsinfo");
            wvContent.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    boolean isUrl = !url.startsWith(APP_WEB_URL);
                    if (isUrl) {
                        Intent intent = new Intent(getContext(), WebActivity.class);
                        intent.putExtra(IntentConstant.WEBURL, url);
                        startActivity(intent);
                    } else {
                        String hrefStr = "href";
                        String o_class_str = "o_class";
                        String o_action_str = "o_action";
                        String o_id_str = "o_id";

                        if (url.contains("?") &&
                                url.contains(hrefStr) &&
                                url.contains(o_class_str) &&
                                url.contains(o_action_str) &&
                                url.contains(o_id_str)) {
                            String[] split = url.split("\\?");
                            if (split[1] != null) {
                                String[] params = split[1].split("&");
                                if (params != null && params.length > 0) {
                                    String o_class = "";
                                    String o_action = "";
                                    String o_id = "";
                                    for (String param : params) {
                                        if (param.contains(hrefStr)) {
                                            String href = param.substring(param.indexOf("=") + 1);//不能包涵等号自己
                                            if (href != null && RegexValidateUtil.isUrl(href)) {
                                                Intent intent = new Intent(getContext(), WebActivity.class);
                                                intent.putExtra(IntentConstant.WEBURL, href);
                                                startActivity(intent);
                                            }
                                        } else {
                                            if (param.contains(o_class_str)) {
                                                o_class = param.substring(param.indexOf("=") + 1);
                                            } else if (param.contains(o_action_str)) {
                                                o_action = param.substring(param.indexOf("=") + 1);
                                            } else if (param.contains(o_id_str)) {
                                                o_id = param.substring(param.indexOf("=") + 1);
                                            }
                                        }
                                    }
                                    JumpUtils.jump(NewsContentActivity.this, o_class, o_action, o_id, null);
                                }
                            }
                        }
                    }
                    return true;
                }

                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                    return super.shouldInterceptRequest(view, request);
                }

                @Override
                public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                    return super.shouldOverrideKeyEvent(view, event);
                }

                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                    return super.shouldInterceptRequest(view, url);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    view.loadUrl("javascript:function clickImg()" +
                            "{" +
                            "   var imgs = document.getElementsByTagName(\"img\");" +
                            "   for(var i = 0; i < imgs.length; i++)" +
                            "   {" +
                            "       imgs[i].onclick = function()" +
                            "       {" +
                            "           jsinfo.imgClick(this.src); " +
                            "       }" +
                            "   }" +
                            "}");
                    view.loadUrl("javascript:clickImg()");

//                    settings.setBlockNetworkImage(false);
//                    if (!settings.getLoadsImagesAutomatically()) { //判断webview是否加载了，图片资源
//                        //设置wenView加载图片资源
//                        settings.setLoadsImagesAutomatically(true);
//                    }
                    flWebContent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            flWebContent.setForeground(null);
                        }
                    }, 150);
//                    view.loadUrl("javascript:function urlClick()" +
//                            "{" +
//                            "   var urls = document.getElementsByTagName(\"a\");" +
//                            "   for(var i = 0; i < urls.length; i++)" +
//                            "   {" +
//                            "       urls[i].onclick = function()" +
//                            "       {" +
//                            "           jsinfo.urlClick(this.href); " +
//                            "       }" +
//                            "   }" +
//                            "}");
//                    view.loadUrl("javascript:urlClick()");
                }
            });
            headView.addView(llFullContent, 0);

            /**
             * 创建分享的
             */
            LinearLayout llShareContent = (LinearLayout) LayoutInflater.from(NewsContentActivity.this).
                    inflate(R.layout.layout_news_content_head_share, headView, false);

            newsContentAd = (AdvertImageLayout) llShareContent.findViewById(R.id.ll_news_content_ad);
            tvSource = (TextView) llShareContent.findViewById(R.id.tv_source);


            List<SlideJson> ads = newsContentJson.getAd();
            newsContentAd.addAdvertViews(ads);

            View attentionBtn = llShareContent.findViewById(R.id.ll_attention);
            View sharePYQ = llShareContent.findViewById(R.id.rv_pyq);
            View shareSina = llShareContent.findViewById(R.id.rl_sina);
            View shareWx = llShareContent.findViewById(R.id.ll_wx);
            attention = (ThumbView2) llShareContent.findViewById(R.id.tv_attention);

            try {
                String goodType = "";
                switch (type) {
                    case VarConstant.OCLASS_BLOG:
                        goodType = VarConstant.GOOD_TYPE_COMMENT_BLOG;
                        break;
                    case VarConstant.OCLASS_NEWS:
                        goodType = VarConstant.GOOD_TYPE_COMMENT_NEWS;
                        break;
                }
                attention.setThumbCount(Integer.parseInt(newsContentJson.getNum_good()), newsContentJson.getId(),
                        goodType, isGood);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            attentionBtn.setOnClickListener(this);
            sharePYQ.setOnClickListener(this);
            shareWx.setOnClickListener(this);
            shareSina.setOnClickListener(this);
            attention.setOnClickListener(this);

            tvSource.setText(Html.fromHtml(source));

            headView.addView(llShareContent, 1);
        }

        /**
         * 发表评论的
         *
         * @param popupWindow
         * @param commentEdit
         * @param commentBean
         * @param parentId
         */
        public void requestIssueComment(final PopupWindow popupWindow,
                                        final EditText commentEdit,
                                        CommentBean commentBean,
                                        int parentId) {

            String commentContent = commentEdit.getText().toString();
            if (commentContent.trim().length() == 0) {
                commentEdit.setText("");

                TSnackbar.make(commentEdit,
                        "评论好像为空喔,请检查",
                        TSnackbar.LENGTH_LONG,
                        TSnackbar.APPEAR_FROM_TOP_TO_DOWN)
                        .setPromptThemBackground(Prompt.WARNING)
                        .setMinHeight(SystemUtil.getStatuBarHeight(getContext()), getResources()
                                .getDimensionPixelOffset(R.dimen.actionbar_height)).show();
                return;
            }

            UserJson userInfo = LoginUtils.getUserInfo(getContext());

            final TSnackbar snackBar = TSnackbar.make
                    (
                            commentEdit,
                            "正在提交评论",
                            TSnackbar.LENGTH_INDEFINITE,
                            TSnackbar.APPEAR_FROM_TOP_TO_DOWN
                    );


            VolleyRequest volleyRequest = new VolleyRequest(getContext(), getQueue());
            JSONObject jsonParam = volleyRequest.getJsonParam();
            jsonParam.put("object_id", objectId);
            jsonParam.put("uid", userInfo.getUid());
            jsonParam.put("accessToken", userInfo.getToken());
            jsonParam.put("content", commentContent);
            if (parentId != 0) {
                jsonParam.put("parent_id", parentId);
            }
            if ("blog".equals(type)) {
                jsonParam.put("type", "blog_article");
            } else {
                jsonParam.put("type", "article");
            }

            volleyRequest.doPost(HttpConstant.COMMENT_PUBLISH, jsonParam, new HttpListener<CommentBean>() {
                @Override
                protected void onResponse(CommentBean mCommentBean) {
                    popupWindow.dismiss();
                    commentEdit.setText("");

                    newsContentPresenter.commentCommit(mCommentBean);

                    snackBar.setPromptThemBackground(Prompt.SUCCESS)
                            .setText("评论提交成功")
                            .setDuration(TSnackbar.LENGTH_LONG)
                            .setMinHeight(SystemUtil.getStatuBarHeight(getContext()), getResources()
                                    .getDimensionPixelOffset(R.dimen.actionbar_height))
                            .show();
                    if (popupWindow instanceof PopupUtil) {
                        ((PopupUtil) popupWindow).addLock(false);
                    }

                    int headerViewsCount = ptrLvMessage.getRefreshableView().getHeaderViewsCount();
                    ptrLvMessage.getRefreshableView().smoothScrollToPosition(headerViewsCount);
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                    if (error != null && error.getMessage() != null) {
                        String errorMessage = error.getMessage();
                        ToastView.makeText2(getContext(), errorMessage);
                    }
                    if (popupWindow instanceof PopupUtil) {
                        ((PopupUtil) popupWindow).addLock(false);
                    }
                }
            });
        }


        private UmengShareBean umengShareBean;
        private UmengShareUtil umengShareUtil;

        @Override
        public void onClick(View v) {
            if (umengShareBean == null) {
                umengShareBean = new UmengShareBean();
                umengShareBean.setTitle(title);
                umengShareBean.setDetail("");
                umengShareBean.setImageUrl(shareImg);
                umengShareBean.setWebUrl(shareUrl);

                umengShareUtil = new UmengShareUtil(NewsContentActivity.this, umengShareBean);
            }

            switch (v.getId()) {
                case R.id.tv_attention:
                    newsContentPresenter.attention(objectId);
                    break;
                case R.id.rv_pyq:
                    umengShareUtil.shareContent1(SHARE_MEDIA.WEIXIN_CIRCLE, umengShareBean);
                    break;
                case R.id.ll_wx:
                    umengShareUtil.shareContent1(SHARE_MEDIA.WEIXIN, umengShareBean);
                    break;
                case R.id.rl_sina:
                    umengShareUtil.shareContent1(SHARE_MEDIA.SINA, umengShareBean);
                    break;
                case R.id.rl_exist_author:
                    if (isAllowAttention) {
                        Intent intent = new Intent(NewsContentActivity.this, AuthorActivity.class);
                        intent.putExtra(IntentConstant.O_ID, newsContentJson.getAuthor_id());
                        startActivity(intent);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onChangeTheme() {
        super.onChangeTheme();
        try {
            int alertTheme = ThemeUtil.getAlertTheme(getContext());
            String content = "";
            String source = webViewAndHead.newsContentJson.getSource();
            if (RegexValidateUtil.isEmpty(source)) {
                source = "";
            }
            switch (alertTheme) {
                case android.support.v7.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert:
                    content = "<font color='#909090'>文章来源:</font><font color='#4D4D4D'>" + source +
                            "</font>";
                    break;
                case android.support.v7.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert:
                    content = "<font color='#2E3239'>文章来源:</font><font color='#A1ABB2'>" + source +
                            "</font>";
                    break;
            }
            webViewAndHead.tvSource.setText(Html.fromHtml(content));
            webViewAndHead.attention.onChangeTheme();
            if (newsContentPresenter != null && newsContentPresenter.commentAdapter != null) {
                newsContentPresenter.commentAdapter.notifyDataSetInvalidated();
            }
            if (commentPresenter != null) {
                commentPresenter.onChangeTheme();
            }
            View noneComment = webViewAndHead.headView.findViewWithTag("noneComment");
            if (noneComment != null) {
                TextView tvNoneComment = (TextView) noneComment;
                int color = ContextCompat.getColor(getContext(), R.color.blue);
                tvNoneComment.setTextColor(color);

                Drawable notDrawable = ContextCompat.getDrawable(getContext(), R.mipmap.icon_comment_not);
                TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        tvNoneComment,
                        null,
                        notDrawable,
                        null,
                        null);
            }
            if (umengShareUI != null) {
                umengShareUI.onChangeTheme();
            }
            mActionBarLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.theme1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UmengShareUI.onActivityResult(this, requestCode, resultCode, data);
    }

    class ImgClickListener implements View.OnClickListener {
        private PopupUtil popupUtil;
        private ZoomImageView ivPop;
        private ImageView ivDownLoad;

        @JavascriptInterface
        public void imgClick(final String imgPath) {
            if (imgPath != null && (imgPath.contains("/Uploads/Editor") || imgPath.contains("/uploads/editor"))) {
                imgStr = imgPath;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(NewsContentActivity.this)
                                .load(imgPath)
                                .asBitmap()
                                .error(R.mipmap.icon_def_video)
                                .placeholder(R.mipmap.icon_def_video)
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap>
                                            glideAnimation) {

                                        if (popupUtil == null) {
                                            popupUtil = new PopupUtil(NewsContentActivity.this);
                                            View inflate = popupUtil.createPopupView(R.layout.pop_img);
                                            ivPop = (ZoomImageView) inflate.findViewById(R.id.iv_pop);
                                            ivDownLoad = (ImageView) inflate.findViewById(R.id.iv_download);
                                            ivDownLoad.setVisibility(View.VISIBLE);
                                            ivDownLoad.setOnClickListener(ImgClickListener.this);
                                            ivPop.setOnClickListener(ImgClickListener.this);

                                            PopupUtil.Config config = new PopupUtil.Config();

                                            config.outsideTouchable = true;
                                            config.alpha = 0.5f;
                                            config.bgColor = 0X00000000;
                                            config.animationStyle = R.style.PopupWindow_Style2;
                                            config.width = WindowManager.LayoutParams.MATCH_PARENT;
                                            config.height = WindowManager.LayoutParams.MATCH_PARENT;

                                            popupUtil.setConfig(config);
                                        }

                                        if (popupUtil.isShowing()) {
                                            popupUtil.dismiss();
                                        }

                                        ViewGroup.LayoutParams layoutParams = ivPop.getLayoutParams();
                                        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                                        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                                        ivPop.setLayoutParams(layoutParams);

                                        ivPop.setImageBitmap(resource);
                                        popupUtil.showAtLocation(ivCollect, Gravity.CENTER, 0, 0);
                                    }
                                });
                    }
                });
            }
        }

        @JavascriptInterface
        public void getJsInfo(String clickInfo) {
            JSONObject clickJson = JSON.parseObject(clickInfo);
            JumpUtils.jump((BaseActivity) getContext(), clickJson.getString("o_class"), clickJson.getString
                            ("o_action"), clickJson
                            .getString("o_id"),
                    clickJson.getString("href"));
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_download:
                    new SaveImage(getContext()).execute(imgStr);
                    break;
                case R.id.iv_pop:

                    break;
                default:
                    if (popupUtil != null && popupUtil.isShowing()) {
                        popupUtil.dismiss();
                    }
                    break;
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
