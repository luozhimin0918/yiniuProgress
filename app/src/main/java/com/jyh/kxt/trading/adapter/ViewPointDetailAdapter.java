package com.jyh.kxt.trading.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.presenter.CommentPresenter;
import com.jyh.kxt.base.util.emoje.EmoticonTextView;
import com.jyh.kxt.base.widget.ThumbView;
import com.jyh.kxt.trading.json.CommentDetailBean;
import com.jyh.kxt.trading.presenter.ViewPointDetailPresenter;
import com.jyh.kxt.trading.ui.CommentDetailActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mr'Dai on 2017/8/2.
 */

public class ViewPointDetailAdapter extends BaseListAdapter<CommentDetailBean> {
    private Context mContext;
    private LayoutInflater mInflater;
    private ViewPointDetailPresenter presenter;

    public ViewPointDetailAdapter(Context mContext, List<CommentDetailBean> dataList) {
        super(dataList);
        this.mContext = mContext;

        mInflater = LayoutInflater.from(mContext);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final CommentDetailBean commentDetailBean = dataList.get(position);

        final ViewHolder mViewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.view_point_detail_comment, parent, false);
            mViewHolder = new ViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        /**
         * 加载头像
         */
        Glide.with(mContext)
                .load(commentDetailBean.getMember_picture())
                .asBitmap()
                .override(100, 100)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        mViewHolder.rivUserAvatar.setImageBitmap(resource);
                    }
                });

        /**
         * 昵称等
         */
        mViewHolder.tvNickName.setText(commentDetailBean.getMember_nickname());

        /**
         * 创建时间
         */
        long createTime = commentDetailBean.getTime() * 1000;//PHP的时间
        String simpleTime = (String) DateFormat.format("MM-dd HH:mm", createTime);
        mViewHolder.tvTime.setText(simpleTime);

        /**
         * 评论内容
         */
        mViewHolder.tvContent.convertToGif(1, commentDetailBean, 0, commentDetailBean.getContent());

        /**
         * 消息的数量
         */
        mViewHolder.tvMessage.setText(String.valueOf(commentDetailBean.getNum_reply()));
        mViewHolder.tvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentPresenter commentPresenter = presenter.mViewPointDetailActivity.commentPresenter;
                commentPresenter.setContentEditHint("回复@" + commentDetailBean.getMember_nickname() + ":");
                commentPresenter.showReplyMessageView(v, commentDetailBean, commentDetailBean.getId());
            }
        });
        /**
         * 点赞的数量
         */
        mViewHolder.tvThumb.setThumbCount(commentDetailBean, commentDetailBean.getId(), new ObserverData() {
            @Override
            public void callback(Object o) {

            }

            @Override
            public void onError(Exception e) {

            }
        });
        /**
         * 子评论数
         */
        int subCommentCount = commentDetailBean.getSub_comment_count();
        if (subCommentCount == 0) {
            mViewHolder.tvReplyCount.setVisibility(View.GONE);
        } else {
            mViewHolder.tvReplyCount.setVisibility(View.VISIBLE);
        }
        mViewHolder.tvReplyCount.setText("共" + subCommentCount + "条回复 > ");
        mViewHolder.tvReplyCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentDetailActivity.class);
                intent.putExtra("viewPointId", presenter.mViewPointDetailActivity.detailId);
                intent.putExtra("commentId", String.valueOf(commentDetailBean.getId()));
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    public void setPresenter(ViewPointDetailPresenter presenter) {
        this.presenter = presenter;
    }

    class ViewHolder {

        @BindView(R.id.riv_user_avatar) RoundImageView rivUserAvatar;
        @BindView(R.id.tv_nick_name) TextView tvNickName;
        @BindView(R.id.tv_time) TextView tvTime;
        @BindView(R.id.tv_thumb) ThumbView tvThumb;
        @BindView(R.id.tv_message) TextView tvMessage;
        @BindView(R.id.tv_content) EmoticonTextView tvContent;
        @BindView(R.id.tv_reply_count) TextView tvReplyCount;

        ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
        }
    }
}
