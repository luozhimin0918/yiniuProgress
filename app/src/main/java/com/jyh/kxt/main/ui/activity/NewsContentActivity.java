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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateFormat;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Space;
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
import com.jyh.kxt.base.adapter.FunctionAdapter;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.json.ShareBtnJson;
import com.jyh.kxt.base.presenter.CommentPresenter;
import com.jyh.kxt.base.util.PopupUtil;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.utils.NativeStore;
import com.jyh.kxt.base.utils.UmengShareTool;
import com.jyh.kxt.base.utils.collect.CollectLocalUtils;
import com.jyh.kxt.base.widget.SelectLineView;
import com.jyh.kxt.base.widget.ThumbView2;
import com.jyh.kxt.base.widget.night.ThemeUtil;
import com.jyh.kxt.explore.ui.AuthorActivity;
import com.jyh.kxt.index.ui.WebActivity;
import com.jyh.kxt.main.json.NewsContentJson;
import com.jyh.kxt.main.presenter.NewsContentPresenter;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.manager.ActivityManager;
import com.library.util.RegexValidateUtil;
import com.library.util.SPUtils;
import com.library.util.SystemUtil;
import com.library.widget.ZoomImageView;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;
import com.library.widget.window.ToastView;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.library.base.http.VarConstant.APP_WEB_URL;
import static com.taobao.accs.ACCSManager.mContext;

/**
 * 项目名:Kxt
 * 类描述:要闻详情
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/18.
 */

public class NewsContentActivity extends BaseActivity implements CommentPresenter.OnCommentClickListener,
        CommentPresenter.OnCommentPublishListener, PageLoadLayout.OnAfreshLoadListener {

    @BindView(R.id.pll_content) public PageLoadLayout pllContent;
    @BindView(R.id.rv_message) public PullToRefreshListView ptrLvMessage;
    @BindView(R.id.iv_ding) public ImageView ivGood;
    @BindView(R.id.iv_collect) public ImageView ivCollect;
    @BindView(R.id.tv_commentCount) TextView tvCommentCount;
    @BindView(R.id.tv_ding_Count) public TextView tvDianCount;

    private NewsContentPresenter newsContentPresenter;
    public CommentPresenter commentPresenter;
    public WebViewAndHead webViewAndHead;

    public String objectId;
    private PopupUtil sharePop;
    private String title;
    private String shareUrl;
    private String shareImg;
    public boolean isGood;
    public boolean isCollect;
    private boolean isLoadOver;

    private String fontS, fontM, fontB;//字体大小代码
    private String content;//内容
    private String night;//夜间模式
    private RecyclerView recyclerView;
    private SelectLineView selectView;
    private TextView tvTheme;
    private LinearLayout llTheme;
    private ImageView ivTheme;
    private TextView tvFontS;
    private TextView tvFontM;
    private TextView tvFontB;
    private View vLine;
    private View popupView;
    private int commentCount;
    private FunctionAdapter functionAdapter;
    private String font;//字体大小
    private String type = VarConstant.OCLASS_ARTICLE;
    private String imgStr;

    private boolean isScrollToComment = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_news_content, StatusBarColor.THEME1);

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

        ActivityManager
                .getInstance()
                .finishNoCurrentActivity(NewsContentActivity.class, NewsContentActivity.this);  //保证只有一个
    }

    @OnClick({R.id.iv_break, R.id.rl_comment, R.id.iv_collect, R.id.rl_dian_zan, R.id.iv_share})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_break:
                onBackPressed();
                break;
            case R.id.rl_comment:
                //回复
                if (isScrollToComment) {
                    ptrLvMessage.getRefreshableView().setSelection(2);
                } else {
                    commentPresenter.showReplyMessageView(view);
                }
                isScrollToComment = false;
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
                    if (sharePop == null) {
                        initShareLayout();
                    } else {
                        sharePop.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                    }
                }
                break;
        }
    }

    private void initShareLayout() {

        sharePop = new PopupUtil(NewsContentActivity.this);
        PopupUtil.Config config = new PopupUtil.Config();

        config.outsideTouchable = true;
        config.alpha = 0.5f;
        config.bgColor = 0X00000000;

        config.animationStyle = R.style.PopupWindow_Style2;
        config.width = WindowManager.LayoutParams.MATCH_PARENT;
        config.height = WindowManager.LayoutParams.WRAP_CONTENT;
        sharePop.setConfig(config);

        popupView = sharePop.createPopupView(R.layout.pop_news_share);
        sharePop.showAtLocation(popupView, Gravity.BOTTOM, 0, 0);

        int color = ContextCompat.getColor(NewsContentActivity.this, R.color.theme1);
        popupView.setBackgroundColor(color);

        recyclerView = (RecyclerView) popupView.findViewById(R.id.rv_share);

        selectView = (SelectLineView) popupView.findViewById(R.id.sv_fontSize);
        Space space = (Space) popupView.findViewById(R.id.sp);
        tvTheme = (TextView) popupView.findViewById(R.id.tv_theme);
        llTheme = (LinearLayout) popupView.findViewById(R.id.ll_theme);
        ivTheme = (ImageView) popupView.findViewById(R.id.iv_theme);
        tvFontS = (TextView) popupView.findViewById(R.id.tv_font_s);
        tvFontM = (TextView) popupView.findViewById(R.id.tv_font_m);
        tvFontB = (TextView) popupView.findViewById(R.id.tv_font_b);
        vLine = popupView.findViewById(R.id.v_line);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);

        List<ShareBtnJson> list = new ArrayList<>();
        list.add(new ShareBtnJson(R.mipmap.icon_share_qyq, "朋友圈"));
        list.add(new ShareBtnJson(R.mipmap.icon_share_wx, "微信好友"));
        list.add(new ShareBtnJson(R.mipmap.icon_share_sina, "新浪"));
        list.add(new ShareBtnJson(R.mipmap.icon_share_qq, "QQ"));
        list.add(new ShareBtnJson(R.mipmap.icon_share_zone, "QQ空间"));

        int alertTheme = ThemeUtil.getAlertTheme(getContext());
        switch (alertTheme) {
            case android.support.v7.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert:
                tvTheme.setText("白天模式");
                break;
            case android.support.v7.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert:
                tvTheme.setText("夜间模式");
                break;
        }

        functionAdapter = new FunctionAdapter(list, this);
        recyclerView.setAdapter(functionAdapter);

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

        functionAdapter.setOnClickListener(
                new FunctionAdapter.OnClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        UMShareAPI umShareAPI = UMShareAPI.get(NewsContentActivity.this);
                        switch (position) {
                            case 0:
                                //朋友圈
                                if (umShareAPI.isInstall(NewsContentActivity.this,
                                        SHARE_MEDIA.WEIXIN_CIRCLE)) {
                                    UmengShareTool.setShareContent(NewsContentActivity
                                                    .this, title, shareUrl, "",
                                            shareImg, SHARE_MEDIA.WEIXIN_CIRCLE);
                                } else {
                                    ToastView.makeText3(NewsContentActivity.this, "未安装微信");
                                }
                                break;
                            case 1:
                                //微信
                                if (umShareAPI.isInstall(NewsContentActivity.this,
                                        SHARE_MEDIA.WEIXIN)) {
                                    UmengShareTool.setShareContent(NewsContentActivity
                                                    .this, title, shareUrl, "",
                                            shareImg, SHARE_MEDIA.WEIXIN);
                                } else {
                                    ToastView.makeText3(NewsContentActivity.this, "未安装微信");
                                }
                                break;
                            case 2:
                                //新浪
                                UmengShareTool.setShareContent(NewsContentActivity.this,
                                        title, shareUrl, "",
                                        shareImg, SHARE_MEDIA.SINA);
                                break;
                            case 3:
                                //QQ
                                if (umShareAPI.isInstall(NewsContentActivity.this,
                                        SHARE_MEDIA.QQ)) {
                                    UmengShareTool.setShareContent(NewsContentActivity
                                                    .this, title, shareUrl, "",
                                            shareImg, SHARE_MEDIA.QQ);
                                } else {
                                    ToastView.makeText3(NewsContentActivity.this, "未安装QQ");
                                }
                                break;
                            case 4:
                                //QQ空间
                                if (umShareAPI.isInstall(NewsContentActivity.this,
                                        SHARE_MEDIA.QZONE)) {
                                    UmengShareTool.setShareContent(NewsContentActivity
                                                    .this, title, shareUrl, "",
                                            shareImg, SHARE_MEDIA.QZONE);
                                } else {
                                    ToastView.makeText3(NewsContentActivity.this, "未安装QQ");
                                }
                                break;
                        }
                        try {
                            sharePop.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

        );

    }

    private void changePopTheme() {
        if (sharePop == null) return;
        selectView.changeTheme();
        tvTheme.setTextColor(ContextCompat.getColor(NewsContentActivity.this, R.color.font_color60));
        popupView.setBackgroundColor(ContextCompat.getColor(NewsContentActivity.this, R.color.theme1));
        ivTheme.setImageDrawable(ContextCompat.getDrawable(NewsContentActivity.this, R.mipmap.icon_drawer_theme));
        tvFontS.setTextColor(ContextCompat.getColor(NewsContentActivity.this, R.color.font_color60));
        tvFontM.setTextColor(ContextCompat.getColor(NewsContentActivity.this, R.color.font_color60));
        tvFontB.setTextColor(ContextCompat.getColor(NewsContentActivity.this, R.color.font_color60));
        vLine.setBackgroundColor(ContextCompat.getColor(NewsContentActivity.this, R.color.line_color2));
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
        @BindView(R.id.iv_like) ImageView cbLike;

        @BindView(R.id.rl_exist_author) RelativeLayout rlExistAuthor;
        @BindView(R.id.rl_not_author) RelativeLayout rlNotAuthor;

        @BindView(R.id.tv_news_type) TextView tvNewsType;
        @BindView(R.id.tv_news_time) TextView tvNewsTime;
        @BindView(R.id.wv_content) public WebView wvContent;
        @BindView(R.id.fl_web_content) FrameLayout flWebContent;

        public LinearLayout headView;
        private NewsContentJson newsContentJson;
        private TextView tvSource;
        public ThumbView2 attention;
        private boolean isAllowAttention;

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

            tvType.setText(newsContentJson.getTypeName());

            long createTime = Long.parseLong(newsContentJson.getCreate_time()) * 1000;
            tvTime.setText(DateFormat.format("yyyy-MM-dd HH:mm:ss", createTime));

            rlExistAuthor.setVisibility(View.VISIBLE);
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
                            ivPhoto.setImageBitmap(resource);
                        }
                    });

            tvName.setText(newsContentJson.getAuthor_name());

            isAllowAttention = "blog".equals(newsContentJson.getType());
            if (isAllowAttention) {
                cbLike.setVisibility(View.VISIBLE);
            } else {
                cbLike.setVisibility(View.GONE);
            }

            boolean isFollow = "1".equals(newsContentJson.getIs_follow());
            cbLike.setSelected(isFollow);
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
            /**
             * ----------  创建WebView
             */
            final WebSettings settings = wvContent.getSettings();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
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

            tvSource = (TextView) llShareContent.findViewById(R.id.tv_source);

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
            jsonParam.put("type", "article");
            jsonParam.put("object_id", objectId);
            jsonParam.put("uid", userInfo.getUid());
            jsonParam.put("accessToken", userInfo.getToken());
            jsonParam.put("content", commentContent);
            if (parentId != 0) {
                jsonParam.put("parent_id", parentId);
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
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                    if (popupWindow instanceof PopupUtil) {
                        ((PopupUtil) popupWindow).addLock(false);
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_attention:
                    newsContentPresenter.attention(objectId);
                    break;
                case R.id.rv_pyq:

                    if (UMShareAPI.get(NewsContentActivity.this).isInstall(NewsContentActivity.this, SHARE_MEDIA
                            .WEIXIN_CIRCLE)) {
                        UmengShareTool.setShareContent(NewsContentActivity.this, title, shareUrl, "",
                                shareImg, SHARE_MEDIA.WEIXIN_CIRCLE);
                    } else {
                        ToastView.makeText3(NewsContentActivity.this, "未安装微信");
                    }

                    break;
                case R.id.ll_wx:
                    if (UMShareAPI.get(NewsContentActivity.this).isInstall(NewsContentActivity.this, SHARE_MEDIA
                            .WEIXIN)) {
                        UmengShareTool.setShareContent(NewsContentActivity.this, title, shareUrl, "",
                                shareImg, SHARE_MEDIA.WEIXIN);
                    } else {
                        ToastView.makeText3(NewsContentActivity.this, "未安装微信");
                    }
                    break;
                case R.id.rl_sina:
                    UmengShareTool.setShareContent(NewsContentActivity.this, title, shareUrl, "",
                            shareImg, SHARE_MEDIA.SINA);
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
            if (functionAdapter != null) {
                functionAdapter.notifyDataSetChanged();
            }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UmengShareTool.onActivityResult(this, requestCode, resultCode, data);
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

//                                        //图片尺寸
//                                        int width = resource.getWidth();
//                                        int height = resource.getHeight();
//                                        //屏幕尺寸
//                                        DisplayMetrics screenDisplay = SystemUtil.getScreenDisplay
//                                                (NewsContentActivity.this);
//                                        int widthPixels = screenDisplay.widthPixels;
//                                        int heightPixels = screenDisplay.heightPixels;
//                                        //放大1.5倍后的图片尺寸
//                                        double largeWidth = width * 1.5;
//                                        double largeHeight = height * 1.5;
//                                        //放大图片(最大1.5倍),是其宽或高全屏
//                                        if (largeWidth <= widthPixels && largeHeight <= heightPixels) {
//                                            width *= 1.5;
//                                            height *= 1.5;
//                                        } else if (largeWidth > widthPixels && largeHeight > heightPixels) {
//                                            double outWidth = largeWidth - widthPixels;
//                                            double outHeight = largeHeight - heightPixels;
//                                            if (outHeight > outWidth) {
//                                                float size = widthPixels / (float) width;
//                                                width = widthPixels;
//                                                height *= size;
//                                            } else {
//                                                float size = heightPixels / (float) height;
//                                                height = heightPixels;
//                                                width *= size;
//                                            }
//                                        } else if (largeWidth > widthPixels) {
//                                            float size = widthPixels / (float) width;
//                                            width = widthPixels;
//                                            height *= size;
//                                        } else {
//                                            float size = heightPixels / (float) height;
//                                            height = heightPixels;
//                                            width *= size;
//                                        }

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
                    newsContentPresenter.new SaveImage().execute(imgStr);
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
        if (commentPresenter != null) {
            commentPresenter.onResume();
        }
    }
}
