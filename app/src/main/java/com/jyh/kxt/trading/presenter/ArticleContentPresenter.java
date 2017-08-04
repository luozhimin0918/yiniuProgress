package com.jyh.kxt.trading.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.util.emoje.EmoticonSimpleTextView;
import com.jyh.kxt.trading.json.ViewPointTradeBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/8/4.
 */

public class ArticleContentPresenter {
    public Context mContext;

    public ArticleContentPresenter(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 作者头像
     */
    public void setAuthorImage(final RoundImageView rivUserAvatar, String authorImageUrl) {
        Glide.with(mContext)
                .load(authorImageUrl)
                .asBitmap()
                .override(80, 80).into(
                new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap>
                            glideAnimation) {
                        rivUserAvatar.setImageBitmap(resource);
                    }
                });
    }

    /**
     * 赞的处理
     */
    public void initTradeHandler(TextView tvZanView, boolean isFavour) {
        if (isFavour) {
            Drawable drawableLeft = ContextCompat.getDrawable(mContext, R.mipmap.icon_point_zan2);
            tvZanView.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
        } else {
            Drawable drawableLeft = ContextCompat.getDrawable(mContext, R.mipmap.icon_point_zan1);
            tvZanView.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
        }
    }

    /**
     * 转发信息的处理
     */
    public void initTransmitView(ViewGroup rlTransmitLayout,
                                 EmoticonSimpleTextView tvTransmitView,
                                 ViewPointTradeBean forwardContent) {
        if (forwardContent != null) {
            rlTransmitLayout.setVisibility(View.VISIBLE);

            String authorInfo = "@ " + forwardContent.author_name;
            StringBuffer contentBuffer = new StringBuffer(forwardContent.content);
            contentBuffer.insert(0, authorInfo);

            if (forwardContent.picture.size() == 1) {
                contentBuffer.insert(contentBuffer.length(), "[图片]");
            } else {
                contentBuffer.insert(contentBuffer.length(), "[图片][图片]...");
            }

            SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(contentBuffer);
            spannableBuilder.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.gray_btn_bg_color)),
                    0,
                    authorInfo.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            tvTransmitView.convertToGif(spannableBuilder);
        } else {
            rlTransmitLayout.setVisibility(View.GONE);
        }
    }

    /**
     * GridView adapter
     */
    public void setPictureAdapter(GridView mGridViewPicture, List<String> gridList) {
        if (gridList == null) {
            gridList = new ArrayList<>();
        }

        final List<String> finalGridList = gridList;
        BaseListAdapter<String> pictureAdapter = new BaseListAdapter<String>(finalGridList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final ViewHolder mViewHolder;

                if (convertView == null) {
                    convertView = LayoutInflater
                            .from(mContext)
                            .inflate(R.layout.view_viewpoint_item_picture, parent, false);

                    mViewHolder = new ViewHolder();
                    convertView.setTag(mViewHolder);
                    mViewHolder.mPictureView = (ImageView) convertView.findViewById(R.id.item_picture);
                } else {
                    mViewHolder = (ViewHolder) convertView.getTag();
                }

                String imageUrl = finalGridList.get(position);

                Glide.with(mContext)
                        .load(imageUrl)
                        .asBitmap()
                        .override(100, 80)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap>
                                    glideAnimation) {
                                mViewHolder.mPictureView.setImageBitmap(resource);
                            }
                        });
                return convertView;
            }

            class ViewHolder {
                ImageView mPictureView;
            }
        };
        mGridViewPicture.setAdapter(pictureAdapter);
    }

    /**
     * 初始化GridView 控件
     */
    public void initGridView(GridView mGridView) {
        mGridView.setMotionEventSplittingEnabled(false);
        mGridView.setNumColumns(3);
        mGridView.setBackgroundColor(Color.TRANSPARENT);
        mGridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        mGridView.setCacheColorHint(0);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mGridView.setGravity(Gravity.CENTER);
        mGridView.setVerticalScrollBarEnabled(false);
    }
}
