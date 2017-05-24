package com.jyh.kxt.main.ui.activity;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.widget.ImageView;
import android.widget.Space;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.jyh.kxt.base.adapter.FunctionAdapter;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.json.ShareBtnJson;
import com.jyh.kxt.base.presenter.CommentPresenter;
import com.jyh.kxt.base.util.PopupUtil;
import com.jyh.kxt.base.utils.GoodUtils;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.utils.UmengShareTool;
import com.jyh.kxt.base.utils.collect.CollectLocalUtils;
import com.jyh.kxt.base.utils.collect.CollectUtils;
import com.jyh.kxt.base.widget.SelectLineView;
import com.jyh.kxt.base.widget.night.ThemeUtil;
import com.jyh.kxt.main.json.NewsContentJson;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.main.presenter.NewsContentPresenter;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.SPUtils;
import com.library.util.SystemUtil;
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
    @BindView(R.id.iv_ding) ImageView ivGood;
    @BindView(R.id.iv_collect) ImageView ivCollect;

    private NewsContentPresenter newsContentPresenter;
    public CommentPresenter commentPresenter;
    private WebViewAndHead webViewAndHead;

    public String objectId;
    private PopupUtil sharePop;
    private String title;
    private String shareUrl;
    private String shareImg;
    private boolean isGood;
    private boolean isCollect;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_news_content, StatusBarColor.THEME1);

        objectId = getIntent().getStringExtra(IntentConstant.O_ID);//获取传递过来的Id
        isGood = GoodUtils.isGood(this, objectId, VarConstant.GOOD_TYPE_NEWS);
        ivGood.setSelected(isGood);

        isCollect = CollectLocalUtils.isCollect(this, VarConstant.COLLECT_TYPE_ARTICLE, objectId);
        ivCollect.setSelected(isCollect);

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
                if (isLoadOver) {
                    NewsContentJson newsContentJson = webViewAndHead.newsContentJson;
                    NewsJson newsJson = new NewsJson();
                    newsJson.setAuthor(newsContentJson.getAuthor_name());
                    newsJson.setDataType(VarConstant.DB_TYPE_COLLECT_LOCAL);
                    newsJson.setDatetime(newsContentJson.getCreate_time());
                    newsJson.setTitle(newsContentJson.getTitle());
                    newsJson.setHref("");
                    newsJson.setO_action(VarConstant.OACTION_DETAIL);
                    newsJson.setO_class(VarConstant.OCLASS_NEWS);
                    newsJson.setO_id(objectId);
                    newsJson.setPicture(newsContentJson.getPicture());
                    newsJson.setType(newsContentJson.getType());
                    if (isCollect) {
                        CollectUtils.unCollect(this, VarConstant.COLLECT_TYPE_ARTICLE, newsJson, new ObserverData() {
                            @Override
                            public void callback(Object o) {
                                ivCollect.setSelected(false);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        }, null);
                    } else {

                        CollectUtils.collect(this, VarConstant.COLLECT_TYPE_ARTICLE, newsJson, new ObserverData() {
                            @Override
                            public void callback(Object o) {
                                ivCollect.setSelected(true);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        }, null);
                    }
                }
                break;
            case R.id.iv_ding:
                //点赞
                if (isLoadOver)
                    if (isGood) {
                        GoodUtils.delGood(this, objectId, VarConstant.GOOD_TYPE_NEWS, new ObserverData() {
                            @Override
                            public void callback(Object o) {
                                ivGood.setSelected(false);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        }, null);
                    } else {
                        GoodUtils.addGood(this, objectId, VarConstant.GOOD_TYPE_NEWS, new ObserverData() {
                            @Override
                            public void callback(Object o) {
                                ivGood.setSelected(true);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        }, null);
                    }
                break;
            case R.id.iv_share:
                //分享
                if (isLoadOver) {
                    if (sharePop == null) {
                        initShareLayout();
                    }
                    sharePop.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                }
                break;
        }
    }

    private void initShareLayout() {
        sharePop = new PopupUtil(this);
        popupView = sharePop.createPopupView(R.layout.pop_news_share);

        popupView.setBackgroundColor(ContextCompat.getColor(NewsContentActivity.this, R.color.theme1));

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

        FunctionAdapter functionAdapter = new FunctionAdapter(list, this);
        recyclerView.setAdapter(functionAdapter);

        selectView.setSelectItemListener(new SelectLineView.SelectItemListener() {
            @Override
            public void selectItem(int position) {
                String font = "";
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
                int alertTheme = ThemeUtil.getAlertTheme(getContext());
                switch (alertTheme) {
                    case android.support.v7.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert:
                        webViewAndHead.wvContent.loadDataWithBaseURL("", night + font + content, "text/html", "utf-8", "");
                        break;
                    case android.support.v7.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert:
                        webViewAndHead.wvContent.loadDataWithBaseURL("", font + content, "text/html", "utf-8", "");
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
                        webViewAndHead.wvContent.loadDataWithBaseURL("", content, "text/html", "utf-8", "");
                        break;
                    case android.support.v7.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert:
                        tvTheme.setText("白天模式");
                        setDayNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        SPUtils.save(NewsContentActivity.this, SpConstant.SETTING_DAY_NIGHT, true);
                        webViewAndHead.wvContent.loadDataWithBaseURL("", night + content, "text/html", "utf-8", "");
                        break;
                }
                changePopTheme();
            }
        });

        functionAdapter.setOnClickListener(new FunctionAdapter.OnClickListener()

                                           {
                                               @Override
                                               public void onClick(View view, int position) {
                                                   UMShareAPI umShareAPI = UMShareAPI.get(NewsContentActivity.this);
                                                   switch (position) {
                                                       case 0:
                                                           //朋友圈
                                                           if (umShareAPI.isInstall(NewsContentActivity.this, SHARE_MEDIA.WEIXIN_CIRCLE)) {
                                                               UmengShareTool.setShareContent(NewsContentActivity.this, title, shareUrl, "",
                                                                       shareImg, SHARE_MEDIA.WEIXIN_CIRCLE);
                                                           } else {
                                                               ToastView.makeText3(NewsContentActivity.this, "未安装微信");
                                                           }
                                                           break;
                                                       case 1:
                                                           //微信
                                                           if (umShareAPI.isInstall(NewsContentActivity.this, SHARE_MEDIA.WEIXIN)) {
                                                               UmengShareTool.setShareContent(NewsContentActivity.this, title, shareUrl, "",
                                                                       shareImg, SHARE_MEDIA.WEIXIN);
                                                           } else {
                                                               ToastView.makeText3(NewsContentActivity.this, "未安装微信");
                                                           }
                                                           break;
                                                       case 2:
                                                           //新浪
                                                           UmengShareTool.setShareContent(NewsContentActivity.this, title, shareUrl, "",
                                                                   shareImg, SHARE_MEDIA.SINA);
                                                           break;
                                                       case 3:
                                                           //QQ
                                                           if (umShareAPI.isInstall(NewsContentActivity.this, SHARE_MEDIA.QQ)) {
                                                               UmengShareTool.setShareContent(NewsContentActivity.this, title, shareUrl, "",
                                                                       shareImg, SHARE_MEDIA.QQ);
                                                           } else {
                                                               ToastView.makeText3(NewsContentActivity.this, "未安装QQ");
                                                           }
                                                           break;
                                                       case 4:
                                                           //QQ空间
                                                           if (umShareAPI.isInstall(NewsContentActivity.this, SHARE_MEDIA.QZONE)) {
                                                               UmengShareTool.setShareContent(NewsContentActivity.this, title, shareUrl, "",
                                                                       shareImg, SHARE_MEDIA.QZONE);
                                                           } else {
                                                               ToastView.makeText3(NewsContentActivity.this, "未安装QQ控件");
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

        if (SystemUtil.navigationBar(this) > 10)

        {
            ViewGroup.LayoutParams layoutParams = space.getLayoutParams();
            layoutParams.height = SystemUtil.navigationBar(this);
            space.setLayoutParams(layoutParams);
        }

        PopupUtil.Config config = new PopupUtil.Config();

        config.outsideTouchable = true;
        config.alpha = 0.5f;
        config.bgColor = 0X00000000;

        config.animationStyle = R.style.PopupWindow_Style2;
        config.width = WindowManager.LayoutParams.MATCH_PARENT;
        config.height = WindowManager.LayoutParams.WRAP_CONTENT;
        sharePop.setConfig(config);
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
        content = newsContentJson.getContent();
        night = newsContentJson.getNight_style();
        isLoadOver = true;
        webViewAndHead.createWebViewAndHead(newsContentJson);
    }

    @Override
    public void onPublish(PopupWindow popupWindow, EditText etContent, CommentBean commentBean, int parentId) {
        webViewAndHead.requestIssueComment(popupWindow, etContent, commentBean, parentId);
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
        @BindView(R.id.wv_content) public WebView wvContent;

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


//            if (!"0".equals(newsContentJson.getAuthor_id())) {
            tvType.setText(newsContentJson.getTypeName());

            long createTime = Long.parseLong(newsContentJson.getCreate_time()) * 1000;
            tvTime.setText(DateFormat.format("yyyy-MM-dd HH:mm:ss", createTime));

            rlExistAuthor.setVisibility(View.VISIBLE);
            Glide.with(NewsContentActivity.this)
                    .load(HttpConstant.IMG_URL + newsContentJson.getAuthor_image())
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

            boolean isAllowAttention = "0".equals(newsContentJson.getAuthor_id());
            if (isAllowAttention) {
                cbLike.setVisibility(View.GONE);
            }

            boolean isFollow = "1".equals(newsContentJson.getIs_follow());
            cbLike.setChecked(isFollow);
            cbLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CheckBox checkBox = (CheckBox) v;
                    boolean checked = checkBox.isChecked();

                    newsContentPresenter.requestAttention(
                            checked,
                            WebViewAndHead.this.newsContentJson.getAuthor_id());
                }
            });
//            } else {
//                rlNotAuthor.setVisibility(View.VISIBLE);
//                tvNewsType.setText(newsContentJson.getTypeName());
//
//                long createTime = Long.parseLong(newsContentJson.getCreate_time()) * 1000;
//                tvNewsTime.setText(DateFormat.format("yyyy-MM-dd HH:mm:ss", createTime));
//            }

            tvTitle.setText(newsContentJson.getTitle());
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


            int alertTheme = ThemeUtil.getAlertTheme(getContext());
            switch (alertTheme) {
                case android.support.v7.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert:
                    webViewAndHead.wvContent.loadDataWithBaseURL("", night + content, "text/html", "utf-8", "");
                    break;
                case android.support.v7.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert:
                    webViewAndHead.wvContent.loadDataWithBaseURL("", content, "text/html", "utf-8", "");
                    break;
            }

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
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                }
            });
        }
    }
}
