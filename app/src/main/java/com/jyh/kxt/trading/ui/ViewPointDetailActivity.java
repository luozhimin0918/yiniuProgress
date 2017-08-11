package com.jyh.kxt.trading.ui;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.av.json.CommentBean;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.presenter.CommentPresenter;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.widget.SimplePopupWindow;
import com.jyh.kxt.trading.json.ShareDictBean;
import com.jyh.kxt.trading.json.ViewPointTradeBean;
import com.jyh.kxt.trading.presenter.ArticleContentPresenter;
import com.jyh.kxt.trading.presenter.ViewPointDetailPresenter;
import com.jyh.kxt.user.json.UserJson;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import butterknife.BindView;
import butterknife.OnClick;

public class ViewPointDetailActivity extends BaseActivity implements CommentPresenter.OnCommentPublishListener {

    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) ImageView ivBarFunction;

    @BindView(R.id.pplv_content) public PullToRefreshListView mPullPinnedListView;

    @BindView(R.id.iv_like) public ImageView ivZanView;
    @BindView(R.id.iv_comment) public ImageView ivComment;
    @BindView(R.id.tv_zanCount) public TextView tvZanCount;

    @BindView(R.id.iv_collect) public ImageView ivCollect;
    @BindView(R.id.tv_commentCount) public TextView tvCommentCount;


    public CommentPresenter commentPresenter;
    public ArticleContentPresenter articlePresenter;
    private ViewPointDetailPresenter viewPointDetailPresenter;

    public String detailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_point_detail, StatusBarColor.THEME1);
        tvBarTitle.setText("详情页");
        ivBarFunction.setVisibility(View.VISIBLE);
        ivBarFunction.setImageResource(R.mipmap.icon_point_menu);

        commentPresenter = new CommentPresenter(this);//初始化评论相关
        commentPresenter.setOnCommentPublishListener(this);
        commentPresenter.setOnlyAllowSmallEmoJe(true);

        detailId = getIntent().getStringExtra(IntentConstant.O_ID);

        initView();

        articlePresenter = new ArticleContentPresenter(this);
        viewPointDetailPresenter = new ViewPointDetailPresenter(this);
        viewPointDetailPresenter.requestInitData();

        mPullPinnedListView.setMode(PullToRefreshBase.Mode.DISABLED);
        mPullPinnedListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                viewPointDetailPresenter.requestMoreData();
            }
        });
    }

    private void initView() {
        ListView mRefreshableView = mPullPinnedListView.getRefreshableView();
        mRefreshableView.setDivider(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.line_background4)));
        mRefreshableView.setDividerHeight(0);
        mRefreshableView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        mPullPinnedListView.setMode(PullToRefreshBase.Mode.DISABLED);
    }

    @OnClick({R.id.iv_bar_break, R.id.tv_comment, R.id.iv_bar_function})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.tv_comment:
                commentPresenter.showReplyMessageView(view);
                break;
            case R.id.iv_bar_function:
                showShareOrFunction();
                break;
        }
    }

    /**
     * @param popupWindow
     * @param etContent
     * @param commentBean
     * @param parentId    0 表示回复的新评论  1 回复别人的评论  2 回复别人的回复评论
     */
    @Override
    public void onPublish(PopupWindow popupWindow, EditText etContent, CommentBean commentBean, int parentId) {
        viewPointDetailPresenter.requestIssueComment(popupWindow, etContent, commentBean, parentId);
    }


    private SimplePopupWindow functionPopupWindow;

    private void showShareOrFunction() {
        if (viewPointDetailPresenter.mViewPointTradeBean == null) {
            return;
        }

        functionPopupWindow = new SimplePopupWindow(this);
        functionPopupWindow.setSimplePopupListener(new SimplePopupWindow.SimplePopupListener() {

            ViewPointTradeBean mViewPointTradeBean;

            View.OnClickListener functionClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    functionPopupWindow.dismiss();
                    switch (v.getId()) {
                        case R.id.iv_fun_url:

                            break;
                        case R.id.iv_fun_jb:
                            articlePresenter.showReportWindow(mViewPointTradeBean.o_id, mViewPointTradeBean.report);
                            break;
                        case R.id.iv_fun_qx:
                            break;
                    }
                }
            };

            @Override
            public void onCreateView(View popupView) {
                mViewPointTradeBean = viewPointDetailPresenter.mViewPointTradeBean;
                ShareDictBean shareDict = mViewPointTradeBean.shareDict;
                articlePresenter.shareToPlatform(popupView, shareDict);

                TextView tvFunUrl = (TextView) popupView.findViewById(R.id.iv_fun_url);
                TextView tvFunJb = (TextView) popupView.findViewById(R.id.iv_fun_jb);
                TextView tvFunQx = (TextView) popupView.findViewById(R.id.iv_fun_qx);

                UserJson userInfo = LoginUtils.getUserInfo(getContext());
                if (userInfo != null && userInfo.getWriter_id() != null && userInfo.getWriter_id().equals(mViewPointTradeBean.author_id)) {
                    tvFunJb.setVisibility(View.GONE);
                } else {
                    tvFunJb.setVisibility(View.VISIBLE);
                }


                tvFunUrl.setOnClickListener(functionClickListener);
                tvFunJb.setOnClickListener(functionClickListener);
                tvFunQx.setOnClickListener(functionClickListener);
            }

            @Override
            public void onDismiss() {

            }
        });
        functionPopupWindow.show(R.layout.pop_point_share_fun);
    }
}
