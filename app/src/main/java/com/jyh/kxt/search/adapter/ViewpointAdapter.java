package com.jyh.kxt.search.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.custom.DiscolorButton;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.util.emoje.EmoticonSimpleTextView;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.widget.SimplePopupWindow;
import com.jyh.kxt.trading.json.ViewPointTradeBean;
import com.jyh.kxt.trading.presenter.ArticleContentPresenter;
import com.jyh.kxt.trading.util.TradeHandlerUtil;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.ui.LoginOrRegisterActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.widget.flowlayout.OptionFlowLayout;
import com.library.widget.window.ToastView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 项目名:KxtProfessional
 * 类描述:观点
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/8.
 */

public class ViewpointAdapter extends BaseListAdapter<ViewPointTradeBean> {

    private Context mContext;
    private TradeHandlerUtil mTradeHandlerUtil;
    private SimplePopupWindow functionPopupWindow;
    private ArticleContentPresenter articleContentPresenter;
    private String searchKey;

    public ViewpointAdapter(List<ViewPointTradeBean> dataList, Context mContext) {
        super(dataList);
        this.mContext = mContext;
        articleContentPresenter = new ArticleContentPresenter(mContext);
        mTradeHandlerUtil = TradeHandlerUtil.getInstance();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.view_viewpoint_item1, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ViewPointTradeBean viewPointTradeBean = dataList.get(position);
        holder.setData(viewPointTradeBean);

        holder.tvContent.convertToGif(viewPointTradeBean.content);
        holder.tvNickName.setText(viewPointTradeBean.author_name);

        CharSequence formatCreateTime = DateFormat.format("MM-dd HH:mm", viewPointTradeBean.time * 1000);
        holder.tvTime.setText(formatCreateTime.toString());

        holder.tvZanView.setText(String.valueOf(viewPointTradeBean.num_good));
        holder.tvPinLunView.setText(String.valueOf(viewPointTradeBean.num_comment));

        articleContentPresenter.setAuthorImage(holder.rivUserAvatar, viewPointTradeBean.author_img);
        articleContentPresenter.initTradeHandler(holder.tvZanView, viewPointTradeBean.isFavour);

        articleContentPresenter.initTransmitView(
                holder.rlTransmitLayout,
                holder.tvTransmitView,
                viewPointTradeBean.forward);

        articleContentPresenter.setPictureAdapter(holder.gridPictureLayout, viewPointTradeBean.picture);
        return convertView;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public void setData(List<ViewPointTradeBean> viewpoints) {
        dataList.clear();
        dataList.addAll(viewpoints);
        notifyDataSetChanged();
    }

    public void addData(List<ViewPointTradeBean> list) {
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder {
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
                        boolean isSaveSuccess = mTradeHandlerUtil.saveState(mContext, viewPointTradeBean.o_id, 1, true);
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
                            articleContentPresenter.shareToPlatform(popupView,viewPointTradeBean.shareDict);
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

        ViewHolder(View contentView) {
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
                                        mTradeHandlerUtil.saveState(mContext, viewPointTradeBean.o_id, 2, viewPointTradeBean.isCollect);
                                        break;
                                    case R.id.point_function_gz:
                                        boolean isGz = !"true".equals(tvGz.getTag());
                                        articleContentPresenter.setAttentionState(tvGz, isGz);
                                        articleContentPresenter.requestAttentionState(viewPointTradeBean.author_id, isGz);
                                        break;
                                    case R.id.point_function_jb:
                                        showReportWindow(viewPointTradeBean);
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

    /**
     * 显示举报的Window
     *
     * @param viewPointTradeBean
     */
    private void showReportWindow(final ViewPointTradeBean viewPointTradeBean) {
        functionPopupWindow.dismiss();

        functionPopupWindow = new SimplePopupWindow((Activity) mContext);
        functionPopupWindow.setSimplePopupListener(new SimplePopupWindow.SimplePopupListener() {

            private OptionFlowLayout mTagFlowLayout;
            private DiscolorButton mDiscolorButton;

            @Override
            public void onCreateView(View popupView) {
                mTagFlowLayout = (OptionFlowLayout) popupView.findViewById(R.id.report_content);
                mDiscolorButton = (DiscolorButton) popupView.findViewById(R.id.report_btn);

                mTagFlowLayout.addOptionView(viewPointTradeBean.report, R.layout.item_point_jb_tv);
                mTagFlowLayout.setDefaultOption(0);
                mTagFlowLayout.setMinOrMaxCheckCount(1, 1);

                mDiscolorButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        List<String> checkBoxText = mTagFlowLayout.getCheckBoxText();
                        if (checkBoxText.size() == 0) {
                            ToastView.makeText3(mContext, "投诉内容不能为空");
                            return;
                        }

                        String reportContent = checkBoxText.get(0);

                        UserJson userInfo = LoginUtils.getUserInfo(mContext);
                        if (userInfo == null) {
                            mContext.startActivity(new Intent(mContext, LoginOrRegisterActivity.class));
                            return;
                        }
                        //读取关注状态
                        IBaseView iBaseView = (IBaseView) mContext;
                        VolleyRequest mVolleyRequest = new VolleyRequest(mContext, iBaseView.getQueue());
                        mVolleyRequest.setTag(getClass().getName());

                        JSONObject mainParam = mVolleyRequest.getJsonParam();
                        mainParam.put("id", viewPointTradeBean.o_id);
                        mainParam.put("uid", userInfo.getUid());
                        mainParam.put("type", reportContent);
                        mVolleyRequest.doGet(HttpConstant.TRADE_FAVORSTATUS, mainParam, new HttpListener<String>() {
                            @Override
                            protected void onResponse(String s) {

                            }
                        });

                    }
                });
            }

            @Override
            public void onDismiss() {

            }
        });
        functionPopupWindow.show(R.layout.pop_point_report);
    }

}
