package com.jyh.kxt.trading.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.util.PopupUtil;
import com.jyh.kxt.base.util.emoje.EmoticonSimpleTextView;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.trading.json.ViewPointTradeBean;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.ui.LoginOrRegisterActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;

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

            if (forwardContent.picture != null && forwardContent.picture.size() == 1) {
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

    /**
     * 收藏
     *
     * @param tvSc
     * @param isCollect
     */
    public void setCollectState(TextView tvSc, boolean isCollect) {
        Drawable drawableTop;
        if (isCollect) {
            drawableTop = ContextCompat.getDrawable(mContext, R.mipmap.icon_point_sc3);
        } else {
            drawableTop = ContextCompat.getDrawable(mContext, R.mipmap.icon_point_sc);
        }
        tvSc.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
    }

    public void setAttentionState(TextView tvGz, boolean isAttention) {
        UserJson userInfo = LoginUtils.getUserInfo(mContext);
        if (userInfo == null) {
            mContext.startActivity(new Intent(mContext, LoginOrRegisterActivity.class));
            return;
        }

        Drawable drawableTop;
        if (isAttention) {
            drawableTop = ContextCompat.getDrawable(mContext, R.mipmap.icon_point_gz3);
            tvGz.setTag("true");
        } else {
            drawableTop = ContextCompat.getDrawable(mContext, R.mipmap.icon_point_gz);
            tvGz.setTag("false");
        }
        tvGz.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
    }

    /**
     * 读取状态
     */
    public void requestGetGzState(final TextView tvGz, String authorId) {
        UserJson userInfo = LoginUtils.getUserInfo(mContext);
        if (userInfo != null) {
            IBaseView iBaseView = (IBaseView) mContext;
            VolleyRequest mVolleyRequest = new VolleyRequest(mContext, iBaseView.getQueue());
            JSONObject mainParam = mVolleyRequest.getJsonParam();
            mainParam.put("writer_id", authorId);
            mainParam.put("uid", userInfo.getUid());

            mVolleyRequest.doGet(HttpConstant.VIEW_POINT_IS_FOLLOW, mainParam, new HttpListener<JSONObject>() {
                @Override
                protected void onResponse(JSONObject jsonObject) {
                    boolean isFollow = jsonObject.getBoolean("is_follow");
                    setAttentionState(tvGz, isFollow);
                }
            });
        }
    }

    /**
     * 请求关注状态
     */
    public void requestAttentionState(String authorId, boolean bool) {
        UserJson userInfo = LoginUtils.getUserInfo(mContext);
        if (userInfo != null) {
            IBaseView iBaseView = (IBaseView) mContext;
            VolleyRequest mVolleyRequest = new VolleyRequest(mContext, iBaseView.getQueue());
            JSONObject mainParam = mVolleyRequest.getJsonParam();
            mainParam.put("id", authorId);
            mainParam.put("uid", userInfo.getUid());
            mainParam.put("accessToken", userInfo.getToken());
            mainParam.put("type", "point");

            String attentionUrl;
            if (bool) {
                attentionUrl = HttpConstant.EXPLORE_BLOG_ADDFAVOR;
            } else {
                attentionUrl = HttpConstant.EXPLORE_BLOG_DELETEFAVOR;
            }

            mVolleyRequest.doGet(attentionUrl, mainParam, new HttpListener<String>() {
                @Override
                protected void onResponse(String jsonObject) {
                }
            });
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
        mGridViewPicture.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String girdImageUrl = finalGridList.get(position);
                setGridViewItemClick(girdImageUrl);
            }
        });
    }

    /**
     * 点击GridView 弹出PopWindow
     */
    private PopupUtil imagePopupUtil;

    private void setGridViewItemClick(String girdImageUrl) {

        imagePopupUtil = new PopupUtil((Activity) mContext);
        View inflate = imagePopupUtil.createPopupView(R.layout.pop_img);
        inflate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imagePopupUtil.isShowing()) {
                    imagePopupUtil.dismiss();
                }
            }
        });
        final ImageView ivPop = (ImageView) inflate.findViewById(R.id.iv_pop);

        PopupUtil.Config config = new PopupUtil.Config();
        config.outsideTouchable = true;
        config.alpha = 0.5f;
        config.bgColor = 0X00000000;
        config.animationStyle = R.style.PopupWindow_Style1;
        config.width = WindowManager.LayoutParams.MATCH_PARENT;
        config.height = WindowManager.LayoutParams.MATCH_PARENT;
        imagePopupUtil.setConfig(config);

        Glide.with(mContext).load(girdImageUrl)
                .asBitmap()
                .error(R.mipmap.icon_def_news)
                .placeholder(R.mipmap.icon_def_news)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        ViewGroup.LayoutParams layoutParams = ivPop.getLayoutParams();
                        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                        ivPop.setLayoutParams(layoutParams);
                        ivPop.setImageBitmap(resource);

                        imagePopupUtil.showAtLocation(ivPop, Gravity.CENTER, 0, 0);
                    }
                });
    }
}
