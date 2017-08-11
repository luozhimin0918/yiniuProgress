package com.jyh.kxt.index.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.av.json.CommentBean;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.util.emoje.EmoticonSimpleTextView;
import com.jyh.kxt.base.util.emoje.EmoticonTextView;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.base.widget.ThumbView;
import com.jyh.kxt.index.json.PointJson;
import com.jyh.kxt.index.presenter.CommentListPresenter;
import com.library.util.SystemUtil;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mr'Dai on 2017/8/11.
 */

public class CommentPointAdapter extends BaseListAdapter<PointJson> {

    private LayoutInflater mInflater;
    private BasePresenter mBasePresenter;
    private int adapterFromStatus;
    private Context mContext;

    public CommentPointAdapter(Context mContext, List<PointJson> dataList, BasePresenter mBasePresenter) {
        super(dataList);
        this.mContext = mContext;
        this.mBasePresenter = mBasePresenter;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final PointJson pointJson = dataList.get(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_comment_type3, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        switch (adapterFromStatus) {
            case 0:
                viewHolder.tvMessage.setVisibility(View.GONE);
                break;
            case 1:
                viewHolder.tvMessage.setVisibility(View.VISIBLE);
                break;
        }
        holderLogic(viewHolder, pointJson);

        /**
         *    回复的内容
         */
        String replyMemberName = "回复 @ " + pointJson.getParent_member_name() + " ";
        SpannableStringBuilder contentSpannable = new SpannableStringBuilder(replyMemberName + pointJson.getContent());
        int color1 = ContextCompat.getColor(mContext, R.color.blue);
        ForegroundColorSpan contentForeground = new ForegroundColorSpan(color1);
        contentSpannable.setSpan(contentForeground, 2, replyMemberName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        viewHolder.tvContent.convertToGif(contentSpannable);

        /**
         * 回复观点
         */
        if (!TextUtils.isEmpty(pointJson.getParent_content())) {
            String memberName = pointJson.getParent_member_name() + ": ";
            SpannableStringBuilder readSpannable1 = new SpannableStringBuilder(memberName + pointJson.getParent_content());
            int color2 = ContextCompat.getColor(mContext, R.color.blue);
            ForegroundColorSpan readForeground1 = new ForegroundColorSpan(color2);
            readSpannable1.setSpan(readForeground1, 0, memberName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.tvReadTitle1.convertToGif(readSpannable1);
            viewHolder.tvReadTitle1.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tvReadTitle1.setVisibility(View.GONE);
        }
        /**
         * 观点的信息
         */
        String pointPublisher = "@ " + pointJson.getPoint_name() + " : ";
        String pointContent = pointPublisher + pointJson.getTitle();
        viewHolder.tvReadTitle2.convertToGif(2, pointJson, pointPublisher.length(), pointContent, pointJson.getPoint_picture());

        viewHolder.tvReadTitle2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JumpUtils.jump((BaseActivity) mContext, pointJson.getO_class(), pointJson.getO_action(), pointJson.getO_id() + "", null);
            }
        });

        return convertView;
    }

    /**
     * 逻辑
     */
    private void holderLogic(final ViewHolder mViewHolder, final PointJson pointJson) {
        String memberPicture = pointJson.getMember_picture();

        Glide.with(mContext)
                .load(memberPicture)
                .asBitmap()
                .override(30, 30)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        mViewHolder.rivUserAvatar.setImageBitmap(resource);
                    }
                });

        mViewHolder.tvNickName.setText(pointJson.getMember_nickname());
        mViewHolder.tvTime.setText(getSimpleTime(pointJson.getTime()));

        mViewHolder.tvMessage.setText(String.valueOf(pointJson.getNum_reply()));
        /**
         * 点赞的数量
         */
        mViewHolder.tvThumb.setThumbCount(pointJson, pointJson.getId(), new ObserverData() {
            @Override
            public void callback(Object o) {

            }

            @Override
            public void onError(Exception e) {

            }
        });
        mViewHolder.tvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentStatement(v, pointJson, pointJson.getId());
            }
        });
    }

    /**
     * @param v
     * @param commentBean
     * @param parentId
     */
    private void commentStatement(View v, CommentBean commentBean, int parentId) {
        if (mBasePresenter == null) {
            TSnackbar.make(v, "只能看不能回", TSnackbar.LENGTH_LONG, TSnackbar.APPEAR_FROM_TOP_TO_DOWN)

                    .setMinHeight(SystemUtil.getStatuBarHeight(mContext),
                            mContext.getResources().getDimensionPixelOffset(R.dimen.actionbar_height))
                    .setPromptThemBackground(Prompt.WARNING).show();
            return;
        }

        if (mBasePresenter instanceof CommentListPresenter) {
            CommentListPresenter commentListPresenter = (CommentListPresenter) mBasePresenter;
            commentListPresenter.showReplyMessageView(v, commentBean, parentId);
        }
    }

    public void setAdapterFromStatus(int adapterFromStatus) {
        this.adapterFromStatus = adapterFromStatus;
    }

    class ViewHolder {
        @BindView(R.id.riv_user_avatar) RoundImageView rivUserAvatar;
        @BindView(R.id.tv_nick_name) TextView tvNickName;
        @BindView(R.id.tv_time) TextView tvTime;

        @BindView(R.id.tv_thumb) ThumbView tvThumb;
        @BindView(R.id.tv_message) TextView tvMessage;

        @BindView(R.id.tv_comment_content) EmoticonSimpleTextView tvContent;
        @BindView(R.id.tv_read_title1) EmoticonSimpleTextView tvReadTitle1;

        @BindView(R.id.tv_read_title2) EmoticonTextView tvReadTitle2;


        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private String getSimpleTime(long time) {
        time = time * 1000;//PHP的时间
        String simpleTime = (String) DateFormat.format("MM-dd HH:mm", time);
        return simpleTime;
    }
}
