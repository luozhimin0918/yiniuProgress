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
import com.jyh.kxt.av.json.CommentBean;
import com.jyh.kxt.av.presenter.VideoDetailPresenter;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.util.emoje.EmoticonTextView;
import com.jyh.kxt.base.widget.ThumbView;
import com.jyh.kxt.main.presenter.NewsContentPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jyh.kxt.R.id.tv_message;

/**
 * Created by Mr'Dai on 2017/5/4.
 */

public class CommentAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<CommentBean> videoDetailList;
    private BasePresenter basePresenter;

    public CommentAdapter(Context mContext, List<CommentBean> videoDetailList, BasePresenter basePresenter) {
        mInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.basePresenter = basePresenter;
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
        final CommentBean commentBean = videoDetailList.get(position);

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

                mViewHolder2.tvPrimaryThumb.setThumbCount(commentBean, commentBean.getParent_id());

                mViewHolder2.tvPrimaryMessage.setText(String.valueOf(commentBean.getParent_num_reply()));
                mViewHolder2.tvPrimaryTime.setText(getSimpleTime(commentBean.getParent_create_time()));

                String convertContent = "@" + commentBean.getParent_member_nickname() +
                        ":" +
                        commentBean.getParent_content();

                int nickNameLength = commentBean.getParent_member_nickname().length() + 2;//这里包括@ 和 :
                mViewHolder2.tvPrimaryContent.convertToGif(nickNameLength, convertContent);

                break;
        }

        /**
         * 设置头像
         */
        String memberPicture = commentBean.getMember_picture();

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

        baseViewHolder.tvNickName.setText(commentBean.getMember_nickname());
        baseViewHolder.tvTime.setText(getSimpleTime(commentBean.getCreate_time()));

        baseViewHolder.tvThumb.setThumbCount(commentBean, commentBean.getId());

        baseViewHolder.tvMessage.setText(String.valueOf(commentBean.getNum_reply()));
        baseViewHolder.tvContent.convertToGif(0, commentBean.getContent());


        baseViewHolder.tvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (basePresenter instanceof VideoDetailPresenter) {
                    VideoDetailPresenter videoDetailPresenter = (VideoDetailPresenter) basePresenter;
                    videoDetailPresenter.videoDetailActivity.commentPresenter.showReplyMessageView(v, commentBean, 1);
                } else if (basePresenter instanceof NewsContentPresenter) {
                    NewsContentPresenter newsContentPresenter = (NewsContentPresenter) basePresenter;
                    newsContentPresenter.newsContentActivity.commentPresenter.showReplyMessageView(v, commentBean, 1);
                }
            }
        });
        return convertView;
    }

    class BaseViewHolder {
        @BindView(R.id.riv_user_avatar) RoundImageView rivUserAvatar;
        @BindView(R.id.tv_nick_name) TextView tvNickName;
        @BindView(R.id.tv_time) TextView tvTime;
        @BindView(R.id.ll_je_sao) LinearLayout llJeSao;
        @BindView(R.id.tv_thumb) ThumbView tvThumb;
        @BindView(tv_message) TextView tvMessage;
        @BindView(R.id.tv_content) EmoticonTextView tvContent;

    }

    class ViewHolder1 extends BaseViewHolder {


        ViewHolder1(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class ViewHolder2 extends BaseViewHolder {
        @BindView(R.id.tv_primary_content) EmoticonTextView tvPrimaryContent;
        @BindView(R.id.tv_primary_time) TextView tvPrimaryTime;
        @BindView(R.id.tv_primary_thumb) ThumbView tvPrimaryThumb;
        @BindView(R.id.tv_primary_message) TextView tvPrimaryMessage;
        @BindView(R.id.ll_primary_info) RelativeLayout llPrimaryInfo;

        ViewHolder2(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private String getSimpleTime(long time) {
        time = time * 1000;//PHP的时间
        String simpleTime = (String) DateFormat.format("MM-dd HH:mm", time);
        return simpleTime;
    }
}
