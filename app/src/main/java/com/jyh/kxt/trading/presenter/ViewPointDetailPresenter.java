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
import com.library.bean.EventBusClass;
import com.library.util.SystemUtil;
import com.library.widget.handmark.PullToRefreshBase;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mr'Dai on 2017/8/2.
 */

public class ViewPointDetailPresenter extends BasePresenter {

    @BindObject public ViewPointDetailActivity mViewPointDetailActivity;

    private boolean isToComment = true;
    private LinearLayout headLinearLayout;
    private ViewPointDetailBean viewPointDetailBean;
    private ViewPointDetailAdapter mViewPointDetailAdapter;
    private List<CommentDetailBean> commentDetailList = new ArrayList<>();

    public ViewPointDetailPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }


    /**
     * 加载更多数据
     */
    public void requestMoreData() {
        String lastCommentId = String.valueOf(commentDetailList.get(commentDetailList.size() - 1).getId());

        VolleyRequest mVolleyRequest = new VolleyRequest(mContext, mQueue);

        JSONObject mainParam = mVolleyRequest.getJsonParam();
        mainParam.put("type", "point");
        mainParam.put("object_id", mViewPointDetailActivity.detailId);
        mainParam.put("last_id", lastCommentId);

        UserJson userInfo = LoginUtils.getUserInfo(mContext);
        if (userInfo != null) {
            mainParam.put("uid", userInfo.getUid());
        }
        mVolleyRequest.doGet(HttpConstant.VP_COMMENT_LIST, mainParam, new HttpListener<List<CommentDetailBean>>() {
            @Override
            protected void onResponse(List<CommentDetailBean> newCommentDetailList) {
                mViewPointDetailActivity.mPullPinnedListView.onRefreshComplete();
                if (newCommentDetailList.size() == 0) {
                    mViewPointDetailActivity.mPullPinnedListView.noMoreData();
                    return;
                }
                commentDetailList.addAll(newCommentDetailList);
                mViewPointDetailAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 初始化加载所有信息
     */
    public void requestInitData() {
        //写上网络请求

        VolleyRequest mVolleyRequest = new VolleyRequest(mContext, mQueue);

        JSONObject mainParam = mVolleyRequest.getJsonParam();
        mainParam.put("id", mViewPointDetailActivity.detailId);

        UserJson userInfo = LoginUtils.getUserInfo(mContext);
        if (userInfo != null) {
            mainParam.put("uid", userInfo.getUid());
        }

        mVolleyRequest.doGet(HttpConstant.VIEW_POINT_DETAIL, mainParam, new HttpListener<ViewPointDetailBean>() {
            @Override
            protected void onResponse(ViewPointDetailBean viewPointDetailBean) {
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
                mViewPointDetailAdapter.setPresenter(ViewPointDetailPresenter.this);
                mViewPointDetailActivity.mPullPinnedListView.setAdapter(mViewPointDetailAdapter);
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

        if (viewPointDetailBean.num_good > 0) {
            mViewPointDetailActivity.tvZanCount.setVisibility(View.VISIBLE);
            mViewPointDetailActivity.tvZanCount.setText(String.valueOf(viewPointDetailBean.num_good));
        }
        if (viewPointDetailBean.num_comment > 0) {
            mViewPointDetailActivity.tvCommentCount.setVisibility(View.VISIBLE);
            mViewPointDetailActivity.tvCommentCount.setText(String.valueOf(viewPointDetailBean.num_comment));
        }

        mViewPointDetailActivity.articlePresenter.setAuthorImage(roundImageView, viewPointDetailBean.author_img);

        if (viewPointDetailBean.picture != null) {
            mViewPointDetailActivity.articlePresenter.initGridView(gvPicture);
            mViewPointDetailActivity.articlePresenter.setPictureAdapter(gvPicture, viewPointDetailBean.picture);
        }

        mViewPointDetailActivity.articlePresenter.initTransmitView(rlTransmitLayout,
                tvTransmitContent,
                viewPointDetailBean.forward);

        TradeHandlerUtil.TradeHandlerBean tradeHandlerBean = TradeHandlerUtil.getInstance().checkHandlerState(viewPointDetailBean.o_id);


        if (tradeHandlerBean != null && tradeHandlerBean.isFavour) {
            mViewPointDetailActivity.ivZanView.setImageResource(R.mipmap.icon_comment_like);
        } else {
            mViewPointDetailActivity.ivZanView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isSaveSuccess = TradeHandlerUtil.getInstance().saveState(mContext, viewPointDetailBean.o_id, 1, true);

                    if (isSaveSuccess) {
                        mViewPointDetailActivity.ivZanView.setImageResource(R.mipmap.icon_comment_like);
                        viewPointDetailBean.num_good += 1;
                        mViewPointDetailActivity.tvZanCount.setText(String.valueOf(viewPointDetailBean.num_good));

                        TradeHandlerUtil.EventHandlerBean zanBean = new TradeHandlerUtil.EventHandlerBean(viewPointDetailBean.o_id);
                        zanBean.favourState = 1;
                        EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_VIEW_POINT_HANDLER, zanBean));
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

                    TradeHandlerUtil.EventHandlerBean scBean = new TradeHandlerUtil.EventHandlerBean(viewPointDetailBean.o_id);
                    scBean.collectState = 1;
                    EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_VIEW_POINT_HANDLER, scBean));
                }
            }
        });

        mViewPointDetailActivity.ivComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isToComment) {
                    mViewPointDetailActivity.mPullPinnedListView.getRefreshableView().setSelection(2);
                }else{
                    mViewPointDetailActivity.mPullPinnedListView.getRefreshableView().setSelection(0);
                }
                isToComment = !isToComment;
            }
        });

        String followState = viewPointDetailBean.is_follow;
        boolean isFollow = "1".equals(followState);
        cbAttention.setChecked(isFollow);

        cbAttention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPointDetailActivity.articlePresenter
                        .requestAttentionState(String.valueOf(viewPointDetailBean.author_id), cbAttention.isChecked());
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
        jsonParam.put("type", "point");
        jsonParam.put("object_id", mViewPointDetailActivity.detailId);
        jsonParam.put("uid", userInfo.getUid());
        jsonParam.put("accessToken", userInfo.getToken());
        jsonParam.put("content", commentContent);

        if (parentId != 0) {
            jsonParam.put("root_id", commentBean.getId());
            jsonParam.put("parent_id", parentId);
        }

        volleyRequest.doPost(HttpConstant.VP_COMMENT_PUBLISH, jsonParam, new HttpListener<CommentDetailBean>() {
            @Override
            protected void onResponse(CommentDetailBean mCommentBean) {
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
    public void commentCommit(CommentDetailBean mCommentBean) {
        if (mCommentBean.getParent_id() == 0) {
            try {
                if (commentDetailList.size() == 0) {
                    View noneComment = headLinearLayout.findViewWithTag("noneComment");
                    ViewGroup parent = (ViewGroup) noneComment.getParent();
                    parent.removeView(noneComment);
                }
                mCommentBean.setType(VarConstant.POINT);
                commentDetailList.add(0, mCommentBean);
                mViewPointDetailAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            for (CommentDetailBean commentDetailBean : commentDetailList) {
                if (commentDetailBean.getId() == mCommentBean.getParent_id()) {
                    int subCommentCount = commentDetailBean.getSub_comment_count();
                    subCommentCount += 1;
                    commentDetailBean.setSub_comment_count(subCommentCount);
                }
            }
            mViewPointDetailAdapter.notifyDataSetChanged();
        }
    }
}
