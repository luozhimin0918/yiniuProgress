package com.jyh.kxt.trading.presenter;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.util.emoje.EmoticonSimpleTextView;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.trading.adapter.ViewPointDetailAdapter;
import com.jyh.kxt.trading.json.CommentDetailBean;
import com.jyh.kxt.trading.json.ViewPointDetailBean;
import com.jyh.kxt.trading.ui.ViewPointDetailActivity;
import com.jyh.kxt.trading.util.TradeHandlerUtil;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;

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
    private List<CommentDetailBean> commentDetailList = new ArrayList<>();

    public ViewPointDetailPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }


    public void requestInitData() {
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
                if (viewPointDetailBean.comment != null) {
                    commentDetailList.addAll(viewPointDetailBean.comment);
                } else {
                    mViewPointDetailActivity.commentPresenter.createNoneComment();
                }
                ViewPointDetailAdapter mViewPointDetailAdapter = new
                        ViewPointDetailAdapter(mContext, commentDetailList);

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
                    TradeHandlerUtil.getInstance().saveState(mContext, viewPointDetailBean.o_id, 1);
                    mViewPointDetailActivity.ivZanView.setImageResource(R.mipmap.icon_comment_like);
                }
            });
        }
        if (tradeHandlerBean != null && tradeHandlerBean.isCollect) {
            mViewPointDetailActivity.ivCollect.setSelected(true);
        }

        mViewPointDetailActivity.ivCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TradeHandlerUtil.getInstance().saveState(mContext, viewPointDetailBean.o_id, 2);
                mViewPointDetailActivity.ivCollect.setSelected(!mViewPointDetailActivity.ivCollect.isSelected());
            }
        });
    }
}
