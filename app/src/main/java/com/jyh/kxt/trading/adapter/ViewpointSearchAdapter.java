package com.jyh.kxt.trading.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.SpannableStringBuilder;
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
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.util.emoje.EmoticonSimpleTextView;
import com.jyh.kxt.base.widget.SimplePopupWindow;
import com.jyh.kxt.trading.json.ViewPointTradeBean;
import com.jyh.kxt.trading.presenter.ArticleContentPresenter;
import com.jyh.kxt.trading.ui.ViewPointDetailActivity;
import com.jyh.kxt.trading.util.TradeHandlerUtil;
import com.library.util.RegexValidateUtil;
import com.library.util.SPUtils;
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

    private int defaultDayColor_name = Color.parseColor("#FF2E3239");
    private int defaultNightColor_name = Color.parseColor("#FF909090");
    private int keyDayColor = Color.parseColor("#FF1C9CF2");
    private int keyNightColor = Color.parseColor("#FF136AA4");

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

        Boolean isNight = SPUtils.getBoolean(mContext, SpConstant.SETTING_DAY_NIGHT);
        if (viewPointTradeBean != null) {
            if (isNight) {
                if (!RegexValidateUtil.isEmpty(searchKey) && viewPointTradeBean.content != null && viewPointTradeBean.content.contains
                        (searchKey)) {
                    String before = viewPointTradeBean.content.substring(0, viewPointTradeBean.content.indexOf(searchKey));
                    String end = viewPointTradeBean.content.substring(viewPointTradeBean.content.indexOf(searchKey) + searchKey.length());
                    String content = "<font color='" + defaultNightColor_name + "'>" + before + "</font><font color='" + keyNightColor +
                            "'>"
                            + searchKey +
                            "</font><font " +
                            "color='" + defaultNightColor_name +
                            "'>" + end + "</font>";
                    viewHolder1.tvContent.convertToGif(new SpannableStringBuilder(Html.fromHtml(content)));
                } else {
                    viewHolder1.tvContent.convertToGif(new SpannableStringBuilder(viewPointTradeBean.content));
                }
            } else {
                if (!RegexValidateUtil.isEmpty(searchKey) && viewPointTradeBean.content != null && viewPointTradeBean.content.contains
                        (searchKey)) {
                    String before = viewPointTradeBean.content.substring(0, viewPointTradeBean.content.indexOf(searchKey));
                    String end = viewPointTradeBean.content.substring(viewPointTradeBean.content.indexOf(searchKey) + searchKey.length());
                    String content = "<font color='" + defaultDayColor_name + "'>" + before + "</font><font color='" + keyDayColor +
                            "'>"
                            + searchKey +
                            "</font><font " +
                            "color='" + defaultDayColor_name +
                            "'>" + end + "</font>";
                    viewHolder1.tvContent.convertToGif(new SpannableStringBuilder(Html.fromHtml(content)));
                } else {
                    viewHolder1.tvContent.convertToGif(new SpannableStringBuilder(viewPointTradeBean.content));
                }
            }
        }
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
        notifyDataSetChanged();
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
                    articleContentPresenter.showFunctionWindow(viewPointTradeBean);
                }
            });
        }
    }

}
