package com.jyh.kxt.trading.presenter;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.av.json.CommentBean;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.util.PopupUtil;
import com.jyh.kxt.base.util.emoje.EmoticonSimpleTextView;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.trading.adapter.ViewPointDetailAdapter;
import com.jyh.kxt.trading.json.CommentDetailBean;
import com.jyh.kxt.trading.json.ViewPointDetailBean;
import com.jyh.kxt.trading.ui.ViewPointDetailActivity;
import com.jyh.kxt.trading.util.TradeHandlerUtil;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.SystemUtil;
import com.library.widget.handmark.PullToRefreshBase;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mr'Dai on 2017/8/2.
 */

public class ViewPointDetailPresenter extends BasePresenter {

    @BindObject ViewPointDetailActivity mViewPointDetailActivity;

    private LinearLayout headLinearLayout;
    private ViewPointDetailBean viewPointDetailBean;
    private ViewPointDetailAdapter mViewPointDetailAdapter;
    private List<CommentDetailBean> commentDetailList = new ArrayList<>();

    public ViewPointDetailPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }


    public void requestInitData(final PullToRefreshBase.Mode requestMode) {
        //写上网络请求

        VolleyRequest mVolleyRequest = new VolleyRequest(mContext, mQueue);
        mVolleyRequest.setTag(getClass().getName());


        JSONObject mainParam = mVolleyRequest.getJsonParam();
        mainParam.put("id", mViewPointDetailActivity.detailId);

        UserJson userInfo = LoginUtils.getUserInfo(mContext);
        if (userInfo != null) {
            mainParam.put("uid", userInfo.getUid());
        }

        mVolleyRequest.doGet(HttpConstant.VIEW_POINT_DETAIL, mainParam, new HttpListener<ViewPointDetailBean>() {
            @Override
            protected void onResponse(ViewPointDetailBean viewPointDetailBean) {
                if (requestMode == PullToRefreshBase.Mode.PULL_FROM_START) {
                    ViewPointDetailPresenter.this.viewPointDetailBean = viewPointDetailBean;

                    /**
                     * 初始化LinearLayout
                     */
                    headLinearLayout = new LinearLayout(mContext);
                    headLinearLayout.setLayoutParams(
                            new AbsListView.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT));
                    headLinearLayout.setOrientation(LinearLayout.VERTICAL);
                    /**
                     * 加入头部详情信息
                     */
                    View mHeadDetailView = LayoutInflater.from(mContext)
                            .inflate(R.layout.view_point_detail_head,
                                    mViewPointDetailActivity.mPullPinnedListView,
                                    false);

                    headLinearLayout.addView(mHeadDetailView);
                    headViewHandler(mHeadDetailView);

                    /**
                     *  加入评论
                     */
                    mViewPointDetailActivity.commentPresenter.bindListView(headLinearLayout);
                    mViewPointDetailActivity.commentPresenter.createMoreView(null);

                    /**
                     * 添加头部到ListView
                     */
                    mViewPointDetailActivity.mPullPinnedListView.getRefreshableView().addHeaderView(headLinearLayout);

                    /**
                     * set Adapter
                     */
                    if (viewPointDetailBean.comment != null && viewPointDetailBean.comment.size() != 0) {
                        commentDetailList.addAll(viewPointDetailBean.comment);
                        mViewPointDetailActivity.mPullPinnedListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                    } else {
                        mViewPointDetailActivity.commentPresenter.createNoneComment();
                    }
                    mViewPointDetailAdapter = new ViewPointDetailAdapter(mContext, commentDetailList);
                    mViewPointDetailActivity.mPullPinnedListView.setAdapter(mViewPointDetailAdapter);
                }
            }
        });
    }

    /**
     * 处理头部的视图
     */
    @BindView(R.id.point_detail_img) RoundImageView roundImageView;
    @BindView(R.id.point_detail_nick_name) TextView tvNickName;
    @BindView(R.id.point_detail_time) TextView tvTime;
    @BindView(R.id.point_detail_attention) CheckBox cbAttention;
    @BindView(R.id.point_detail_content) EmoticonSimpleTextView estvContent;
    @BindView(R.id.point_detail_picture) GridView gvPicture;
    @BindView(R.id.point_detail_transmit_layout) RelativeLayout rlTransmitLayout;
    @BindView(R.id.point_detail_transmit_text) EmoticonSimpleTextView tvTransmitContent;

    private void headViewHandler(View mHeadDetailView) {
        ButterKnife.bind(this, mHeadDetailView);

        estvContent.convertToGif(viewPointDetailBean.content);
        tvNickName.setText(viewPointDetailBean.author_name);

        CharSequence formatCreateTime = DateFormat.format("MM-dd HH:mm", viewPointDetailBean.time * 1000);
        tvTime.setText(formatCreateTime.toString());

        mViewPointDetailActivity.tvZanCount.setText(String.valueOf(viewPointDetailBean.num_good));
        mViewPointDetailActivity.tvCommentCount.setText(String.valueOf(viewPointDetailBean.num_commit));

        mViewPointDetailActivity.articlePresenter.setAuthorImage(roundImageView, viewPointDetailBean.author_img);

        if (viewPointDetailBean.picture != null) {
            mViewPointDetailActivity.articlePresenter.initGridView(gvPicture);
            mViewPointDetailActivity.articlePresenter.setPictureAdapter(gvPicture, viewPointDetailBean.picture);
        }

        mViewPointDetailActivity.articlePresenter.initTransmitView(rlTransmitLayout,
                tvTransmitContent,
                viewPointDetailBean.forward);

        TradeHandlerUtil.TradeHandlerBean tradeHandlerBean =
                TradeHandlerUtil.getInstance().checkHandlerState(viewPointDetailBean.o_id);


        if (tradeHandlerBean != null && tradeHandlerBean.isFavour) {
            mViewPointDetailActivity.ivZanView.setImageResource(R.mipmap.icon_comment_like);
        } else {
            mViewPointDetailActivity.ivZanView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isSaveSuccess = TradeHandlerUtil.getInstance().saveState(mContext, viewPointDetailBean.o_id, 1, true);
                    if (isSaveSuccess) {
                        mViewPointDetailActivity.ivZanView.setImageResource(R.mipmap.icon_comment_like);
                    }
                }
            });
        }
        if (tradeHandlerBean != null && tradeHandlerBean.isCollect) {
            mViewPointDetailActivity.ivCollect.setSelected(true);
        }

        mViewPointDetailActivity.ivCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean bool = !mViewPointDetailActivity.ivCollect.isSelected();
                boolean isSaveSuccess = TradeHandlerUtil.getInstance().saveState(mContext, viewPointDetailBean.o_id, 2, bool);
                if (isSaveSuccess) {
                    mViewPointDetailActivity.ivCollect.setSelected(bool);
                }
            }
        });
    }

    /**
     * 评论
     *
     * @param popupWindow
     * @param commentEdit
     * @param commentBean
     * @param parentId
     */
    public void requestIssueComment(final PopupWindow popupWindow, final EditText commentEdit, CommentBean commentBean, int parentId) {
        String commentContent = commentEdit.getText().toString();

        if (commentContent.trim().length() == 0) {
            commentEdit.setText("");

            TSnackbar.make(commentEdit,
                    "评论好像为空喔,请检查",
                    TSnackbar.LENGTH_LONG,
                    TSnackbar.APPEAR_FROM_TOP_TO_DOWN)
                    .setPromptThemBackground(Prompt.WARNING).show();

            return;
        }

        UserJson userInfo = LoginUtils.getUserInfo(mContext);

        final TSnackbar snackBar = TSnackbar.make
                (
                        commentEdit,
                        "正在提交评论",
                        TSnackbar.LENGTH_INDEFINITE,
                        TSnackbar.APPEAR_FROM_TOP_TO_DOWN
                );
        snackBar.setPromptThemBackground(Prompt.SUCCESS);
        snackBar.addIconProgressLoading(0, true, false);
        snackBar.show();

        VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);
        JSONObject jsonParam = volleyRequest.getJsonParam();
        jsonParam.put("type", VarConstant.VIDEO);
        jsonParam.put("object_id", mViewPointDetailActivity.detailId);
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

                snackBar.setPromptThemBackground(Prompt.SUCCESS)
                        .setText("评论提交成功")
                        .setDuration(TSnackbar.LENGTH_LONG)
                        .setMinHeight(SystemUtil.getStatuBarHeight(mContext), mContext.getResources()
                                .getDimensionPixelOffset(R.dimen.actionbar_height))
                        .show();

                commentCommit(mCommentBean);
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


    /**
     * 第一次提交
     *
     * @param mCommentBean
     */
    public void commentCommit(CommentBean mCommentBean) {
        try {
            if (commentDetailList.size() == 0) {
                View noneComment = headLinearLayout.findViewWithTag("noneComment");
                ViewGroup parent = (ViewGroup) noneComment.getParent();
                parent.removeView(noneComment);
            }
            CommentDetailBean mCommentDetailBean = new CommentDetailBean();

            commentDetailList.add(0, mCommentDetailBean);
            mViewPointDetailAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
