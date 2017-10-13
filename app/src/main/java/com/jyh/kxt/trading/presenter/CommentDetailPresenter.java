package com.jyh.kxt.trading.presenter;

import android.content.Intent;
import android.os.Handler;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.av.json.CommentBean;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.util.PopupUtil;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.trading.adapter.CommentDetailAdapter;
import com.jyh.kxt.trading.json.CommentDetailBean;
import com.jyh.kxt.trading.ui.CommentDetailActivity;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.ui.LoginActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.SystemUtil;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/8/3.
 */

public class CommentDetailPresenter extends BasePresenter {

    @BindObject public CommentDetailActivity commentDetailActivity;

    private CommentDetailAdapter commentDetailAdapter;
    private CommentDetailBean mCommentDetailBean;
    private List<CommentDetailBean> commentAdapterList = new ArrayList<>();

    public CommentDetailPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void requestListData() {
        UserJson userInfo = LoginUtils.getUserInfo(mContext);
        if (userInfo == null) {
            mContext.startActivity(new Intent(mContext, LoginActivity.class));
            return;
        }

        VolleyRequest mVolleyRequest = new VolleyRequest(mContext, mQueue);

        JSONObject mainParam = mVolleyRequest.getJsonParam();
        mainParam.put("id", commentDetailActivity.commentId);
        mainParam.put("uid", userInfo.getUid());

        mVolleyRequest.doGet(HttpConstant.VP_COMMENT_DETAIL, mainParam, new HttpListener<CommentDetailBean>() {
            @Override
            protected void onResponse(CommentDetailBean mCommentDetailBean) {
                CommentDetailPresenter.this.mCommentDetailBean = mCommentDetailBean;

                commentAdapterList.addAll(mCommentDetailBean.getSub_comment());
                commentAdapterList.add(0, mCommentDetailBean);//添加自己本身到这个里面

                commentDetailAdapter = new CommentDetailAdapter(mContext, commentAdapterList);
                commentDetailAdapter.setPresenter(CommentDetailPresenter.this);
                commentDetailActivity.pullToRefreshListView.setAdapter(commentDetailAdapter);
            }
        });


//
    }

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
        jsonParam.put("object_id", commentDetailActivity.viewPointId);
        jsonParam.put("uid", userInfo.getUid());
        jsonParam.put("accessToken", userInfo.getToken());
        jsonParam.put("content", commentContent);

        jsonParam.put("root_id", commentDetailActivity.commentId);
        jsonParam.put("parent_id", parentId == 0 ? commentDetailActivity.commentId : parentId);

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
        try {
            mCommentBean.setType(VarConstant.POINT);
            commentAdapterList.add(1, mCommentBean);//添加到主评论下面
            commentDetailAdapter.notifyDataSetChanged();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    commentDetailActivity.pullToRefreshListView.getRefreshableView().setSelection(0);
                }
            }, 500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
