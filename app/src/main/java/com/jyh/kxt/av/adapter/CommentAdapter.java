package com.jyh.kxt.av.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.av.json.VideoDetailCommentBean;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.widget.ThumbView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mr'Dai on 2017/5/4.
 */

public class CommentAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<VideoDetailCommentBean> videoDetailList;

    public CommentAdapter(Context mContext, List<VideoDetailCommentBean> videoDetailList) {
        mInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.videoDetailList = videoDetailList;
    }

    @Override
    public int getCount() {
        return videoDetailList.size();
    }

    @Override
    public Object getItem(int position) {
        return videoDetailList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {

        int mItemViewType;
        if (videoDetailList.get(position).getParent_id() == 0) {
            mItemViewType = 0;
        } else {
            mItemViewType = 1;
        }
        return mItemViewType;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int itemViewType = getItemViewType(position);
        VideoDetailCommentBean videoDetailCommentBean = videoDetailList.get(position);

        ViewHolder1 mViewHolder1 = null;
        ViewHolder2 mViewHolder2 = null;

        if (convertView == null) {

            switch (itemViewType) {
                case 0:
                    convertView = mInflater.inflate(R.layout.item_comment_type1, null);
                    mViewHolder1 = new ViewHolder1(convertView);
                    convertView.setTag(mViewHolder1);
                    break;
                case 1:
                    convertView = mInflater.inflate(R.layout.item_comment_type2, null);
                    mViewHolder2 = new ViewHolder2(convertView);
                    convertView.setTag(mViewHolder2);
                    break;
            }
        } else {
            switch (itemViewType) {
                case 0:
                    mViewHolder1 = (ViewHolder1) convertView.getTag();
                    break;
                case 1:
                    mViewHolder2 = (ViewHolder2) convertView.getTag();
                    break;
            }
        }

        BaseViewHolder baseViewHolder = null;
        switch (itemViewType) {
            case 0:
                baseViewHolder = mViewHolder1;
                break;
            case 1:
                baseViewHolder = mViewHolder2;

                mViewHolder2.tvPrimaryThumb.setThumbCount(videoDetailCommentBean.getParent_num_good());
                mViewHolder2.tvPrimaryMessage.setText(String.valueOf(videoDetailCommentBean.getParent_num_reply()));
                mViewHolder2.tvPrimaryTime.setText(getSimpleTime(videoDetailCommentBean.getParent_create_time()));
                mViewHolder2.tvPrimaryContent.setText(videoDetailCommentBean.getParent_content());
                break;
        }

        /**
         * 设置头像
         */
        String memberPicture = videoDetailCommentBean.getMember_picture();

        final BaseViewHolder finalBaseViewHolder = baseViewHolder;
        Glide.with(mContext)
                .load(memberPicture)
                .asBitmap()
                .override(30, 30)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        finalBaseViewHolder.rivUserAvatar.setImageBitmap(resource);
                    }
                });

        baseViewHolder.tvNickName.setText(videoDetailCommentBean.getMember_nickname());
        baseViewHolder.tvTime.setText(getSimpleTime(videoDetailCommentBean.getCreate_time()));
        baseViewHolder.tvThumb.setThumbCount(videoDetailCommentBean.getNum_good());
        baseViewHolder.tvMessage.setText(String.valueOf(videoDetailCommentBean.getNum_reply()));
        baseViewHolder.tvContent.setText(videoDetailCommentBean.getContent());

        return convertView;
    }

    class BaseViewHolder {
        @BindView(R.id.riv_user_avatar) RoundImageView rivUserAvatar;
        @BindView(R.id.tv_nick_name) TextView tvNickName;
        @BindView(R.id.tv_time) TextView tvTime;
        @BindView(R.id.ll_je_sao) LinearLayout llJeSao;
        @BindView(R.id.tv_thumb) ThumbView tvThumb;
        @BindView(R.id.tv_message) TextView tvMessage;
        @BindView(R.id.tv_content) TextView tvContent;

    }

    class ViewHolder1 extends BaseViewHolder {


        ViewHolder1(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class ViewHolder2 extends BaseViewHolder {
        @BindView(R.id.tv_primary_content) TextView tvPrimaryContent;
        @BindView(R.id.tv_primary_time) TextView tvPrimaryTime;
        @BindView(R.id.tv_primary_thumb) ThumbView tvPrimaryThumb;
        @BindView(R.id.tv_primary_message) TextView tvPrimaryMessage;
        @BindView(R.id.ll_primary_info) RelativeLayout llPrimaryInfo;

        ViewHolder2(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private String getSimpleTime(long time) {
        String simpleTime = (String) DateFormat.format("MM-dd HH:mm", time);
        return simpleTime;
    }
}
