package com.jyh.kxt.trading.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.util.emoje.EmoticonSimpleTextView;
import com.jyh.kxt.base.widget.SimplePopupWindow;
import com.jyh.kxt.trading.json.ViewPointTradeBean;
import com.jyh.kxt.trading.presenter.ArticleContentPresenter;
import com.jyh.kxt.trading.ui.ViewPointDetailActivity;
import com.jyh.kxt.trading.util.TradeHandlerUtil;
import com.library.widget.window.ToastView;

import java.util.ArrayList;
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

public class ViewpointSearchAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;

    private List<ViewPointTradeBean> dataList;

    private TradeHandlerUtil mTradeHandlerUtil;
    private SimplePopupWindow functionPopupWindow;
    private ArticleContentPresenter articleContentPresenter;
    private String searchKey;

    public ViewpointSearchAdapter(Context mContext, List<ViewPointTradeBean> viewPointTradeBeanList) {

        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);

        articleContentPresenter = new ArticleContentPresenter(mContext);
        dataList = new ArrayList<>(viewPointTradeBeanList);

        mTradeHandlerUtil = TradeHandlerUtil.getInstance();
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList == null ? 0 : dataList.get(position);
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
            convertView = mInflater.inflate(R.layout.view_viewpoint_item1, parent, false);
            viewHolder1 = new ViewHolder1(convertView);
            convertView.setTag(viewHolder1);
        } else {
            viewHolder1 = (ViewHolder1) convertView.getTag();
        }

        viewHolder1.setData(viewPointTradeBean);

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
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ViewPointDetailActivity.class);
                intent.putExtra(IntentConstant.O_ID, viewPointTradeBean.o_id);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    public void setData(List data) {
        this.dataList = data;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public void addData(List list) {
        dataList.addAll(list);
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
        @BindView(R.id.view_point_fx_tv) TextView tvShareView;

        private ViewPointTradeBean viewPointTradeBean;

        @OnClick({R.id.view_point_zan_layout, R.id.view_point_pl_layout, R.id.view_point_fx_layout})
        public void itemNavFunction(View view) {
            switch (view.getId()) {
                case R.id.view_point_zan_layout:
                    if (viewPointTradeBean.isFavour) {
                        ToastView.makeText(mContext, "您已经赞过了");
                    } else {
                        mTradeHandlerUtil.saveState(mContext, viewPointTradeBean.o_id, 1, true);
                        viewPointTradeBean.isFavour = true;
                        articleContentPresenter.initTradeHandler(tvZanView, true);
                    }
                    break;
                case R.id.view_point_pl_layout:

                    break;
                case R.id.view_point_fx_layout:
                    functionPopupWindow = new SimplePopupWindow((Activity) mContext);

                    functionPopupWindow.setSimplePopupListener(new SimplePopupWindow.SimplePopupListener() {
                        @Override
                        public void onCreateView(View popupView) {

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
                                        articleContentPresenter.setCollectState(tvSc, viewPointTradeBean.isCollect);
                                        break;
                                    case R.id.point_function_gz:
                                        articleContentPresenter.setAttentionState(tvGz, true);
                                        break;
                                    case R.id.point_function_jb:

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
                            articleContentPresenter.requestGetGzState(tvGz, viewPointTradeBean.author_id);

                            popupView.findViewById(R.id.point_function_jb).setOnClickListener(functionListener);
                            popupView.findViewById(R.id.point_function_qx).setOnClickListener(functionListener);
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

}
