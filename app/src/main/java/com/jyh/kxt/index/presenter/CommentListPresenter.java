package com.jyh.kxt.index.presenter;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.av.json.CommentBean;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.presenter.CommentPresenter;
import com.jyh.kxt.base.util.PopupUtil;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.index.adapter.CommentArticleAdapter;
import com.jyh.kxt.index.adapter.CommentPointAdapter;
import com.jyh.kxt.index.adapter.CommentVideoAdapter;
import com.jyh.kxt.index.json.PointJson;
import com.jyh.kxt.index.ui.CommentListActivity;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.ui.LoginActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.util.SystemUtil;
import com.library.widget.window.ToastView;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/8/11.
 */

public class CommentListPresenter extends BasePresenter implements CommentPresenter.OnCommentPublishListener {
    @BindObject
    CommentListActivity commentListActivity;
    private CommentPresenter commentPresenter;

    private int navPosition;
    private int listType;


    HashMap<String, PullListViewPresenter> listViewMap = new HashMap<>();

    public CommentListPresenter(IBaseView iBaseView) {
        super(iBaseView);

        commentPresenter = new CommentPresenter(commentListActivity);
        commentPresenter.setOnCommentPublishListener(this);
    }

    /**
     * @param navPosition 最上方导航栏 0我的评论 1回复我的
     * @param listType    0 文章  1视听 2 观点
     */
    public void requestList(int navPosition, int listType) {
        this.navPosition = navPosition;
        this.listType = listType;

        String requestUrl = null;
        switch (navPosition) {
            case 0:
                requestUrl = HttpConstant.MEMBER_COMMENT_MY;
                break;
            case 1:
                requestUrl = HttpConstant.MEMBER_COMMENT_REPLY;
                break;
        }

        if (listType == 2) {
            requestUrl = HttpConstant.POINT_COMMENT_MY;
        }

        UserJson userInfo = LoginUtils.getUserInfo(mContext);
        if (userInfo == null) {
            mContext.startActivity(new Intent(mContext, LoginActivity.class));
            return;
        }
        JSONObject parameterJson = new JSONObject();
        parameterJson.put("uid", userInfo.getUid());
        parameterJson.put("accessToken", userInfo.getToken());
        if (listType == 0) {
            parameterJson.put("type", "article");
        } else if (listType == 1) {
            parameterJson.put("type", "video");
        } else if (listType == 2) {

            if (navPosition == 0) {
                parameterJson.put("type", "comment");
            } else if (navPosition == 1) {
                parameterJson.put("type", "reply");
            }
        }

        String contentTag = navPosition + "" + listType;

        PullListViewPresenter presenter = listViewMap.get(contentTag);
        if (presenter == null) {
            presenter = new PullListViewPresenter(iBaseView);
            presenter.createView(commentListActivity.flContent);

            Class<?> dataClass = null;
            BaseListAdapter baseListAdapter = null;
            switch (listType) {
                case 0:
                    List<CommentBean> articleList = new ArrayList<>();
                    baseListAdapter = new CommentArticleAdapter(mContext, articleList, this);
                    ((CommentArticleAdapter) baseListAdapter).setAdapterFromStatus(navPosition + 1);
                    dataClass = CommentBean.class;
                    break;
                case 1:
                    List<CommentBean> videoList = new ArrayList<>();
                    baseListAdapter = new CommentVideoAdapter(mContext, videoList, this);
                    ((CommentVideoAdapter) baseListAdapter).setAdapterFromStatus(navPosition + 1);//这里公用所以+1
                    dataClass = CommentBean.class;
                    break;
                case 2:
                    List<PointJson> pointList = new ArrayList<>();
                    baseListAdapter = new CommentPointAdapter(mContext, pointList, this);
                    ((CommentPointAdapter) baseListAdapter).setAdapterFromStatus(navPosition);
                    dataClass = PointJson.class;
                    break;
            }
            presenter.setLoadMode(PullListViewPresenter.LoadMode.PAGE_LOAD);
            presenter.setRequestInfo(requestUrl, parameterJson, dataClass);
            presenter.setAdapter(baseListAdapter);
            presenter.switchContentView();
            presenter.startRequest();

            listViewMap.put(contentTag, presenter);
        } else {
            presenter.switchContentView();
        }

        presenter.setOnLoadMoreListener(new PullListViewPresenter.OnLoadMoreListener() {
            @Override
            public void beforeParameter(List dataList, JSONObject jsonObject) {
                String lastId = null;
                switch (CommentListPresenter.this.listType) {
                    case 0:
                    case 1:
                        CommentBean commentBean = (CommentBean) dataList.get(dataList.size() - 1);
                        lastId = "" + commentBean.getId();
                        break;
                    case 2:
                        PointJson pointJson = (PointJson) dataList.get(dataList.size() - 1);
                        lastId = "" + pointJson.getId();
                        break;
                }
                jsonObject.put("last_id", lastId);
            }
        });
    }

    public void showReplyMessageView(View v, CommentBean commentBean, int commentWho) {
        commentPresenter.setAdjustEmoJeView(false);
        commentPresenter.showReplyMessageView(v, commentBean, commentWho);
    }

    /**
     * @param popupWindow
     * @param commentEdit
     * @param commentBean
     * @param parentId    0 表示回复的新评论  1 回复别人的评论  2 回复别人的回复评论
     */
    @Override
    public void onPublish(final PopupWindow popupWindow, final EditText commentEdit, CommentBean commentBean, int parentId) {
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
        jsonParam.put("uid", userInfo.getUid());
        jsonParam.put("accessToken", userInfo.getToken());
        jsonParam.put("content", commentContent);

        String commentUrl = null;

        switch (listType) {
            case 0:
            case 1:
                commentUrl = HttpConstant.COMMENT_PUBLISH;
                jsonParam.put("object_id", commentBean.getObject_id());
                if (parentId != 0) {
                    jsonParam.put("parent_id", parentId);
                }
                break;
            case 2:
                PointJson mPointJson = (PointJson) commentBean;
                commentUrl = HttpConstant.VP_COMMENT_PUBLISH;
                jsonParam.put("type", "point");
                jsonParam.put("object_id", String.valueOf(mPointJson.getO_id()));
                jsonParam.put("parent_id", String.valueOf(parentId));
                if (parentId != 0) {
                    jsonParam.put("root_id", String.valueOf(mPointJson.getRoot_id()));
                }

                break;
        }

        volleyRequest.doPost(commentUrl, jsonParam, new HttpListener<String>() {
            @Override
            protected void onResponse(String jsonData) {
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

//                String contentTag = navPosition + "" + listType;
//                PullListViewPresenter presenter = listViewMap.get(contentTag);
//                BaseListAdapter baseListAdapter = presenter.getBaseListAdapter();
//                switch (listType) {
//                    case 0:
//                    case 1:
//                        CommentBean myComment1 = JSONObject.parseObject(jsonData, CommentBean.class);
//                        baseListAdapter.addData(myComment1);
//                        break;
//                    case 2:
//                        PointJson myComment2 = JSONObject.parseObject(jsonData, PointJson.class);
//                        baseListAdapter.addData(myComment2);
//                        break;
//                }

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
