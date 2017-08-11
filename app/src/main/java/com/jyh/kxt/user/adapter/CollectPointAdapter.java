package com.jyh.kxt.user.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private SimplePopupWindow functionPopupWindow;
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
        viewHolder1.ivDel.setSelected(viewPointTradeBean.isSel());

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
                    boolean isSelPoint = viewPointTradeBean.isSel();

                    viewPointTradeBean.setSel(!isSelPoint);
                    finalViewHolder.ivDel.setSelected(!isSelPoint);

                    if (delNumListener != null) {
                        selectItemCount = isSelPoint ? selectItemCount - 1 : selectItemCount + 1;
                        delNumListener.delItem(selectItemCount);
                        if (selectItemCount == 0) {
                            delNumListener.delAll(false);
                        } else if (selectItemCount == dataList.size()) {
                            delNumListener.delAll(true);
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
        @BindView(R.id.view_point_fx_tv) TextView tvShareView;

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
        }

        @OnClick({R.id.view_point_zan_layout, R.id.view_point_pl_layout, R.id.view_point_fx_layout})
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
                case R.id.view_point_pl_layout:

                    break;
                case R.id.view_point_fx_layout:
                    functionPopupWindow = new SimplePopupWindow((Activity) mContext);

                    functionPopupWindow.setSimplePopupListener(new SimplePopupWindow.SimplePopupListener() {
                        @Override
                        public void onCreateView(View popupView) {
                            articleContentPresenter.shareToPlatform(popupView, viewPointTradeBean.shareDict);
                        }

                        @Override
                        public void onDismiss() {

                        }
                    });
                    functionPopupWindow.show(R.layout.pop_point_share);
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
                    functionPopupWindow = new SimplePopupWindow((Activity) mContext);
                    functionPopupWindow.setSimplePopupListener(new SimplePopupWindow.SimplePopupListener() {

                        TextView tvSc;
                        TextView tvGz;

                        View.OnClickListener functionListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                switch (view.getId()) {
                                    case R.id.point_function_sc:
                                        viewPointTradeBean.isCollect = !viewPointTradeBean.isCollect;
                                        boolean b = mTradeHandlerUtil.saveState(mContext, viewPointTradeBean, 2, viewPointTradeBean.isCollect);
                                        if (b) {
                                            articleContentPresenter.setCollectState(tvSc, viewPointTradeBean.isCollect);
                                        }
                                        break;
                                    case R.id.point_function_gz:
                                        boolean isGz = !"true".equals(tvGz.getTag());
                                        articleContentPresenter.setAttentionState(tvGz, isGz);
                                        articleContentPresenter.requestAttentionState(viewPointTradeBean.author_id, isGz);
                                        break;
                                    case R.id.point_function_jb:
                                        articleContentPresenter.showReportWindow(viewPointTradeBean.o_id, viewPointTradeBean.report);
                                        break;
                                    case R.id.point_function_qx:
                                        functionPopupWindow.dismiss();
                                        break;
                                }
                            }
                        };

                        @Override
                        public void onCreateView(View popupView) {
                            tvSc = (TextView) popupView.findViewById(R.id.point_function_sc);
                            tvSc.setOnClickListener(functionListener);
                            articleContentPresenter.setCollectState(tvSc, viewPointTradeBean.isCollect);

                            tvGz = (TextView) popupView.findViewById(R.id.point_function_gz);
                            tvGz.setOnClickListener(functionListener);

                            popupView.findViewById(R.id.point_function_jb).setOnClickListener(functionListener);
                            popupView.findViewById(R.id.point_function_qx).setOnClickListener(functionListener);

                            articleContentPresenter.requestGetGzState(tvGz, viewPointTradeBean.author_id);
                        }

                        @Override
                        public void onDismiss() {

                        }
                    });

                    functionPopupWindow.show(R.layout.pop_point_function);
                }
            });
        }

    }

    public List<ViewPointTradeBean> getData() {
        return dataList;
    }
}
