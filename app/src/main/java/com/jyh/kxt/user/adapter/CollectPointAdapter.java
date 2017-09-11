package com.jyh.kxt.user.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.annotation.DelNumListener;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.util.emoje.EmoticonSimpleTextView;
import com.jyh.kxt.base.widget.SimplePopupWindow;
import com.jyh.kxt.trading.json.ViewPointTradeBean;
import com.jyh.kxt.trading.presenter.ArticleContentPresenter;
import com.jyh.kxt.trading.ui.ViewPointDetailActivity;
import com.jyh.kxt.trading.util.TradeHandlerUtil;
import com.library.util.SystemUtil;
import com.library.widget.window.ToastView;

import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/7/26.
 */

public class CollectPointAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;

    private boolean isEdit = false;

    private int widthPixels;
    private List<ViewPointTradeBean> dataList;

    private TradeHandlerUtil mTradeHandlerUtil;
    private PopupWindow functionPopupWindow;
    private ArticleContentPresenter articleContentPresenter;
    private DelNumListener delNumListener;

    public int selectItemCount = 0;

    public CollectPointAdapter(Context mContext, List<ViewPointTradeBean> dataList, ArticleContentPresenter articleContentPresenter) {
        this.mContext = mContext;
        this.dataList = dataList;
        this.articleContentPresenter = articleContentPresenter;
        mInflater = LayoutInflater.from(mContext);

        widthPixels = (SystemUtil.getScreenDisplay(mContext).widthPixels);

        mTradeHandlerUtil = TradeHandlerUtil.getInstance();
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public void setSelectItemCount(int selectItemCount) {
        this.selectItemCount = selectItemCount;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder1 viewHolder1 = null;

        final ViewPointTradeBean viewPointTradeBean = dataList.get(position);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.view_collect_viewpoint, parent, false);
            viewHolder1 = new ViewHolder1(convertView);
            convertView.setTag(viewHolder1);
        } else {
            viewHolder1 = (ViewHolder1) convertView.getTag();
        }

        if (isEdit) {
            viewHolder1.flDel.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = viewHolder1.rlContentItem.getLayoutParams();
            layoutParams.width = widthPixels;
            viewHolder1.rlContentItem.setLayoutParams(layoutParams);
        } else {
            viewHolder1.flDel.setVisibility(View.GONE);
        }
        viewHolder1.ivDel.setSelected(viewPointTradeBean.isSel);

        viewHolder1.setData(viewPointTradeBean);
        viewHolder1.setItemViewColor(convertView);

        viewHolder1.tvContent.convertToGif(viewPointTradeBean.content);
        viewHolder1.tvNickName.setText(viewPointTradeBean.author_name);

        CharSequence formatCreateTime = DateFormat.format("MM-dd HH:mm", viewPointTradeBean.time * 1000);
        viewHolder1.tvTime.setText(formatCreateTime.toString());

        viewHolder1.tvZanView.setText(String.valueOf(viewPointTradeBean.num_good));
        viewHolder1.tvPinLunView.setText(String.valueOf(viewPointTradeBean.num_comment));

        articleContentPresenter.setAuthorImage(viewHolder1.rivUserAvatar, viewPointTradeBean.author_img);
        articleContentPresenter.initTradeHandler(viewHolder1.tvZanView, viewPointTradeBean.isFavour);

        articleContentPresenter.initTransmitView(
                viewHolder1.rlTransmitLayout,
                viewHolder1.tvTransmitView,
                viewPointTradeBean.forward);

        articleContentPresenter.setPictureAdapter(viewHolder1.gridPictureLayout, viewPointTradeBean.picture);

        final ViewHolder1 finalViewHolder = viewHolder1;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEdit) {
                    boolean isSelPoint = viewPointTradeBean.isSel;

                    viewPointTradeBean.isSel = !isSelPoint;
                    finalViewHolder.ivDel.setSelected(!isSelPoint);

                    if (delNumListener != null) {
                        selectItemCount = isSelPoint ? selectItemCount - 1 : selectItemCount + 1;
                        delNumListener.delItem(selectItemCount);
                        if (selectItemCount == dataList.size()) {
                            delNumListener.delAll(true);
                        } else {
                            delNumListener.delAll(false);
                        }
                    }
                } else {
                    Intent intent = new Intent(mContext, ViewPointDetailActivity.class);
                    intent.putExtra(IntentConstant.O_ID, viewPointTradeBean.o_id);
                    mContext.startActivity(intent);
                }
            }
        });
        return convertView;
    }

    public void setDelNumListener(DelNumListener delNumListener) {
        this.delNumListener = delNumListener;
    }

    public void setTop(String id) {
        for (ViewPointTradeBean viewpoint : dataList) {
            if (viewpoint.o_id != null && viewpoint.o_id.equals(id)) {
                if ("1".equals(viewpoint.is_top)) {
                    viewpoint.setIs_top("0");
                } else {
                    viewpoint.setIs_top("1");
                }
            }
        }
        notifyDataSetChanged();
    }

    public void del(String id) {
        Iterator<ViewPointTradeBean> iterator = dataList.iterator();
        while (iterator.hasNext()) {
            ViewPointTradeBean next = iterator.next();
            if (next != null && next.o_id != null && next.o_id.equals(id)) {
                iterator.remove();
            }
        }
        notifyDataSetChanged();
    }

    class ViewHolder1 {
        @BindView(R.id.riv_user_avatar) RoundImageView rivUserAvatar;
        @BindView(R.id.tv_nick_name) TextView tvNickName;
        @BindView(R.id.tv_time) TextView tvTime;
        @BindView(R.id.viewpoint_function_share) ImageView mFunctionView;
        @BindView(R.id.viewpoint_title) EmoticonSimpleTextView tvContent;
        @BindView(R.id.viewpoint_picture_layout) GridView gridPictureLayout;
        @BindView(R.id.viewpoint_function_zan) LinearLayout viewpointFunctionZan;
        @BindView(R.id.rl_content_item) RelativeLayout rlContentItem;

        @BindView(R.id.viewpoint_transmit_layout) RelativeLayout rlTransmitLayout;
        @BindView(R.id.viewpoint_transmit_text) EmoticonSimpleTextView tvTransmitView;

        @BindView(R.id.view_point_zan_tv) TextView tvZanView;
        @BindView(R.id.view_point_pl_tv) TextView tvPinLunView;
        @BindView(R.id.view_point_fx_tv) ImageView tvShareView;

        @BindView(R.id.viewpoint_space) View viewSpace;

        @BindView(R.id.viewpoint_line) View viewLine1;
        @BindView(R.id.view_line1) View viewLine2;
        @BindView(R.id.view_line2) View viewLine3;

        @BindView(R.id.iv_del) ImageView ivDel;
        @BindView(R.id.fl_del) FrameLayout flDel;

        private ViewPointTradeBean viewPointTradeBean;

        public void setItemViewColor(View convertView) {
            convertView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.theme1));

            tvNickName.setTextColor(ContextCompat.getColor(mContext, R.color.font_color62));
            tvTime.setTextColor(ContextCompat.getColor(mContext, R.color.font_color9));
            tvContent.setTextColor(ContextCompat.getColor(mContext, R.color.font_color5));
            tvTransmitView.setTextColor(ContextCompat.getColor(mContext, R.color.font_color64));
            viewSpace.setBackgroundColor(ContextCompat.getColor(mContext, R.color.line_color6));

            rlTransmitLayout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.view_point_transmit_content));

            viewLine1.setBackgroundColor(ContextCompat.getColor(mContext, R.color.line_color6));
            viewLine2.setBackgroundColor(ContextCompat.getColor(mContext, R.color.line_color6));
            viewLine3.setBackgroundColor(ContextCompat.getColor(mContext, R.color.line_color6));

            int paddingVal = SystemUtil.dp2px(mContext, 8);
            tvZanView.setPadding(paddingVal, paddingVal, paddingVal, paddingVal);
            tvZanView.setTextColor(ContextCompat.getColor(mContext, R.color.font_color9));
            TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(tvZanView, R.mipmap
                    .icon_point_zan1, 0, 0, 0);
            tvPinLunView.setPadding(paddingVal, paddingVal, paddingVal, paddingVal);
            tvPinLunView.setTextColor(ContextCompat.getColor(mContext, R.color.font_color9));
            TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(tvPinLunView, R.mipmap
                    .icon_point_pl, 0, 0, 0);
            tvShareView.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.icon_point_fx));
        }

        @OnClick({R.id.view_point_zan_layout, R.id.view_point_fx_layout})
        public void itemNavFunction(View view) {
            switch (view.getId()) {
                case R.id.view_point_zan_layout:
                    if (viewPointTradeBean.isFavour) {
                        ToastView.makeText(mContext, "您已经赞过了");
                    } else {
                        boolean isSaveSuccess = mTradeHandlerUtil.saveState(mContext, viewPointTradeBean, 1, true);
                        if (isSaveSuccess) {
                            viewPointTradeBean.isFavour = true;
                            articleContentPresenter.initTradeHandler(tvZanView, true);

                            viewPointTradeBean.num_good += 1;
                            tvZanView.setText(String.valueOf(viewPointTradeBean.num_good));
                        }
                    }
                    break;
//                case R.id.view_point_pl_layout:
//
//                    break;
                case R.id.view_point_fx_layout:
                    functionPopupWindow = articleContentPresenter.shareToPlatform(viewPointTradeBean.shareDict);
                    break;
            }
        }


        public void setData(ViewPointTradeBean viewPointTradeBean) {
            this.viewPointTradeBean = viewPointTradeBean;
        }

        ViewHolder1(View contentView) {
            ButterKnife.bind(this, contentView);

            articleContentPresenter.initGridView(gridPictureLayout);
            /**
             * 初始化右上角的点击事件
             */
            mFunctionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    articleContentPresenter.showFunctionWindow(viewPointTradeBean);
                }
            });
        }

    }

    public List<ViewPointTradeBean> getData() {
        return dataList;
    }
}
