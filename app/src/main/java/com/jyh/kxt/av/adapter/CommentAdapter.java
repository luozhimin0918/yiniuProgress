package com.jyh.kxt.av.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.av.json.CommentBean;
import com.jyh.kxt.av.presenter.VideoDetailPresenter;
import com.jyh.kxt.av.ui.VideoDetailActivity;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.util.emoje.EmoticonTextView;
import com.jyh.kxt.base.widget.ThumbView;
import com.jyh.kxt.index.presenter.CommentListPresenter;
import com.jyh.kxt.index.presenter.MyCommentPresenter;
import com.jyh.kxt.main.presenter.NewsContentPresenter;
import com.jyh.kxt.main.ui.activity.NewsContentActivity;
import com.library.base.http.VarConstant;
import com.library.util.SystemUtil;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mr'Dai on 2017/5/4.
 */

public class CommentAdapter extends BaseListAdapter<CommentBean> {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<CommentBean> videoDetailList;
    private BasePresenter basePresenter;
    /**
     * 标题状态
     * 0  平常的回复
     * 1  适配-我的回复
     * 2  适配-我的评论
     */
    private int adapterFromStatus = 0;


    public CommentAdapter(Context mContext, List<CommentBean> videoDetailList, BasePresenter basePresenter) {
        super(videoDetailList);

        mInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.basePresenter = basePresenter;
        this.videoDetailList = videoDetailList;
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

                mViewHolder2.tvPrimaryThumb.setThumbCount(commentBean, commentBean.getParent_id(), new ObserverData() {
                    @Override
                    public void callback(Object o) {
//                        commentBean.setNum_good(commentBean.getNum_good() + 1);
                    }

                    @Override
                    public void onError(Exception e) {
                    }
                });
                mViewHolder2.tvPrimaryMessage.setText(String.valueOf(commentBean.getParent_num_reply()));
                mViewHolder2.tvPrimaryTime.setText(getSimpleTime(commentBean.getParent_create_time()));
                String convertContent = "@ " + commentBean.getParent_member_nickname() +
                        ": " +
                        commentBean.getParent_content();
                int nickNameLnegth = commentBean.getParent_member_nickname().length() + 2;//这里包括@ 和 :

                mViewHolder2.tvPrimaryContent.convertToGif(2, commentBean, nickNameLnegth, convertContent);

                switch (adapterFromStatus) {
                    case 0:
                        mViewHolder2.tvPrimaryReadTitle.setVisibility(View.GONE);
                        break;
                    case 1:
                        mViewHolder2.tvPrimaryReadTitle.setVisibility(View.VISIBLE);
                        setReadTitle(mViewHolder2.tvPrimaryReadTitle, commentBean);
                        mViewHolder2.tvPrimaryThumb.setVisibility(View.GONE);
                        mViewHolder2.tvPrimaryMessage.setVisibility(View.GONE);
                        mViewHolder2.tvPrimaryTime.setVisibility(View.GONE);
                        break;
                    case 2:
                        mViewHolder2.tvPrimaryReadTitle.setVisibility(View.VISIBLE);
                        setReadTitle(mViewHolder2.tvPrimaryReadTitle, commentBean);
                        mViewHolder2.tvPrimaryTime.setVisibility(View.GONE);
                        break;
                }
                mViewHolder2.tvPrimaryMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commentStatement(v, commentBean, commentBean.getParent_id());
                    }
                });

                mViewHolder2.llPrimaryInfo.setBackgroundColor(ContextCompat.getColor(mContext, R.color.bg_color2));

                break;
        }


        holderLogic(baseViewHolder, commentBean);

        switch (adapterFromStatus) {
            case 0:
                baseViewHolder.tvReadTitle.setVisibility(View.GONE);
                break;
            case 1:
                if (itemViewType == 0) {
                    baseViewHolder.tvReadTitle.setVisibility(View.VISIBLE);
                    setReadTitle(baseViewHolder.tvReadTitle, commentBean);
                }
                baseViewHolder.tvMessage.setVisibility(View.GONE);
                break;
            case 2:
                if (itemViewType == 0) {
                    baseViewHolder.tvReadTitle.setVisibility(View.VISIBLE);
                    setReadTitle(baseViewHolder.tvReadTitle, commentBean);
                }


                baseViewHolder.llReplyContent.setVisibility(View.VISIBLE);
                baseViewHolder.llReplyContent.removeAllViews();

                List<CommentBean> reply = commentBean.getReply();
                if (reply != null) {
                    for (CommentBean mCommentBean : reply) {

                        View spanView = new View(mContext);
                        spanView.setBackgroundResource(com.library.R.color.line_background1);
                        spanView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                                .MATCH_PARENT, 1));
                        baseViewHolder.llReplyContent.addView(spanView);

                        View replyContentView = LayoutInflater.from(mContext).inflate(
                                R.layout.item_comment_type1,
                                baseViewHolder.llReplyContent,
                                false);

                        ViewHolder1 viewHolder1 = new ViewHolder1(replyContentView);
                        holderLogic(viewHolder1, mCommentBean);

                        baseViewHolder.llReplyContent.addView(replyContentView);

                    }
                }
                break;
        }


        return convertView;
    }


    private void holderLogic(BaseViewHolder baseViewHolder, final CommentBean commentBean) {
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

        baseViewHolder.tvThumb.setThumbCount(commentBean, commentBean.getId(), new ObserverData() {
            @Override
            public void callback(Object o) {
//                commentBean.setNum_good(commentBean.getNum_good() + 1);
            }

            @Override
            public void onError(Exception e) {

            }
        });

        baseViewHolder.tvMessage.setText(String.valueOf(commentBean.getNum_reply()));
        baseViewHolder.tvContent.convertToGif(1, commentBean, 0, commentBean.getContent());

        baseViewHolder.tvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentStatement(v, commentBean, commentBean.getId());
            }
        });

        baseViewHolder.rlContentItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.theme1));
    }

    private void setReadTitle(TextView textView, final CommentBean commentBean) {
        if ("video".equals(commentBean.getType())) {
            TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    textView,
                    R.mipmap.icon_video_play_small,
                    0,
                    0,
                    0);
            textView.setText(commentBean.getObject_title());

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, VideoDetailActivity.class);
                    intent.putExtra(IntentConstant.O_ID, commentBean.getObject_id());
                    mContext.startActivity(intent);
                }
            });
        } else {
            textView.setText("原文:" + commentBean.getObject_title());
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, NewsContentActivity.class);

                    switch (commentBean.getType()) {
                        case VarConstant.ARTICLE:
                            intent.putExtra(IntentConstant.O_ACTION, VarConstant.OCLASS_NEWS);
                            break;
                        case VarConstant.BLOG_ARTICLE:
                            intent.putExtra(IntentConstant.O_ACTION, VarConstant.OCLASS_BLOG);
                            break;
                    }

                    intent.putExtra(IntentConstant.O_ID, commentBean.getObject_id());
                    mContext.startActivity(intent);
                }
            });
        }
    }

    /**
     * @param v
     * @param commentBean
     * @param parentId
     */
    private void commentStatement(View v, CommentBean commentBean, int parentId) {
        if (basePresenter == null) {
            TSnackbar.make(v, "只能看不能回", TSnackbar.LENGTH_LONG, TSnackbar.APPEAR_FROM_TOP_TO_DOWN)

                    .setMinHeight(SystemUtil.getStatuBarHeight(mContext),
                            mContext.getResources().getDimensionPixelOffset(R.dimen.actionbar_height))
                    .setPromptThemBackground(Prompt.WARNING).show();
            return;
        }
        if (basePresenter instanceof VideoDetailPresenter) {
            VideoDetailPresenter videoDetailPresenter = (VideoDetailPresenter) basePresenter;
            videoDetailPresenter.videoDetailActivity.commentPresenter.showReplyMessageView(v, commentBean, parentId);
        } else if (basePresenter instanceof NewsContentPresenter) {
            NewsContentPresenter newsContentPresenter = (NewsContentPresenter) basePresenter;
            newsContentPresenter.newsContentActivity.commentPresenter.showReplyMessageView(v, commentBean, parentId);
        } else if (basePresenter instanceof MyCommentPresenter) {
            MyCommentPresenter myCommentPresenter = (MyCommentPresenter) basePresenter;
            myCommentPresenter.showReplyMessageView(v, commentBean, parentId);
        }else if (basePresenter instanceof CommentListPresenter) {
            CommentListPresenter commentListPresenter = (CommentListPresenter) basePresenter;
            commentListPresenter.showReplyMessageView(v, commentBean, parentId);
        }
    }

    public void setAdapterFromStatus(int adapterFromStatus) {
        this.adapterFromStatus = adapterFromStatus;
    }

    class BaseViewHolder {
        @BindView(R.id.riv_user_avatar) RoundImageView rivUserAvatar;
        @BindView(R.id.tv_nick_name) TextView tvNickName;
        @BindView(R.id.tv_time) TextView tvTime;
        @BindView(R.id.ll_je_sao) LinearLayout llJeSao;
        @BindView(R.id.tv_thumb) ThumbView tvThumb;
        @BindView(R.id.tv_message) TextView tvMessage;
        @BindView(R.id.tv_content) EmoticonTextView tvContent;

        @BindView(R.id.tv_read_title) TextView tvReadTitle;
        @BindView(R.id.ll_reply_content) LinearLayout llReplyContent;

        @BindView(R.id.rl_content_item) RelativeLayout rlContentItem;

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
        @BindView(R.id.tv_primary_read_title) TextView tvPrimaryReadTitle;

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
