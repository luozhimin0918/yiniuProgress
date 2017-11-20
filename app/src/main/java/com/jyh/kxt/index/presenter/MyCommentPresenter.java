package com.jyh.kxt.index.presenter;

import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.av.json.CommentBean;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.presenter.CommentPresenter;
import com.jyh.kxt.base.util.PopupUtil;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.index.ui.fragment.MyCommentFragment;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.util.SystemUtil;
import com.library.widget.window.ToastView;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

/**
 * Created by Mr'Dai on 2017/5/23.
 */

public class MyCommentPresenter extends BasePresenter implements CommentPresenter.OnCommentPublishListener {

    @BindObject MyCommentFragment myCommentFragment;

    private CommentPresenter commentPresenter;

    public MyCommentPresenter(IBaseView iBaseView) {
        super(iBaseView);
        BaseActivity activity = (BaseActivity) myCommentFragment.getActivity();
        commentPresenter = new CommentPresenter(activity);
        commentPresenter.setOnCommentPublishListener(this);
    }


    public void showReplyMessageView(View v, CommentBean commentBean, int commentWho) {
        commentPresenter.showReplyMessageView(v, commentBean, commentWho);
    }

    /**
     * @param popupWindow
     * @param commentEdit
     * @param commentBean
     * @param parentId    0 表示回复的新评论  1 回复别人的评论  2 回复别人的回复评论
     */
    @Override
    public void onPublish(final PopupWindow popupWindow, final EditText commentEdit, CommentBean commentBean,
                          int parentId) {
        String commentContent = commentEdit.getText().toString();
        if (commentContent.trim().length() == 0) {
            commentEdit.setText("");

            TSnackbar.make(commentEdit,
                    "评论好像为空喔,请检查",
                    TSnackbar.LENGTH_LONG,
                    TSnackbar.APPEAR_FROM_TOP_TO_DOWN)
                    .setPromptThemBackground(Prompt.WARNING)
                    .setMinHeight(
                            SystemUtil.getStatuBarHeight(mContext),
                            mContext.getResources().getDimensionPixelOffset(R.dimen.actionbar_height))
                    .show();
            ((PopupUtil) popupWindow).addLock(false);
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
        jsonParam.put("type", commentBean.getType());
        jsonParam.put("object_id", commentBean.getObject_id());
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
                if (popupWindow instanceof PopupUtil) {
                    ((PopupUtil) popupWindow).addLock(false);
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                if (error != null && error.getMessage() != null) {
                    String errorMessage = error.getMessage();
                    ToastView.makeText2(mContext, errorMessage);
                }
                if (popupWindow instanceof PopupUtil) {
                    ((PopupUtil) popupWindow).addLock(false);
                }
            }
        });
    }
}
