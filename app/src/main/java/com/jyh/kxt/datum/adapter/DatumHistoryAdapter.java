package com.jyh.kxt.datum.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.datum.bean.HistoryListBean;
import com.jyh.kxt.main.widget.FastInfoPinnedListView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mr'Dai on 2017/5/31.
 */

public class DatumHistoryAdapter extends BaseAdapter implements FastInfoPinnedListView.PinnedSectionListAdapter {

    private Context mContext;
    private List<HistoryListBean.DataBean> data;

    private int listType = 0;
    private LayoutInflater mInflater;

    public DatumHistoryAdapter(Context mContext, List<HistoryListBean.DataBean> data) {
        this.mContext = mContext;
        this.data = data;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        HistoryListBean.DataBean dataBean = data.get(position);
        return dataBean.getListAdapterType();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int type = getItemViewType(position);
        HistoryListBean.DataBean itemDataBean = data.get(position);

        ViewHolder0 mViewHolder0 = null;
        ViewHolder1 mViewHolder1 = null;
        if (convertView == null) {
            switch (type) {
                case 0:
                    convertView = mInflater.inflate(R.layout.activity_datum_history_suspension, parent, false);
                    mViewHolder0 = new ViewHolder0(convertView);
                    convertView.setTag(mViewHolder0);
                    break;
                case 1:
                    convertView = mInflater.inflate(R.layout.activity_datum_history_item, parent, false);
                    mViewHolder1 = new ViewHolder1(convertView);
                    convertView.setTag(mViewHolder1);
                    break;
            }
        } else {
            switch (type) {
                case 0:
                    mViewHolder0 = (ViewHolder0) convertView.getTag();
                    break;
                case 1:
                    mViewHolder1 = (ViewHolder1) convertView.getTag();
                    break;
            }
        }
        switch (type) {
            case 0:
                String listAdapterTypeName = itemDataBean.getListAdapterTypeName();
                switch (listAdapterTypeName) {
                    case "finance":
                        mViewHolder0.tvTitle1.setText("前值(%)");
                        mViewHolder0.tvTitle2.setText("预测值(%)");
                        mViewHolder0.tvTitle3.setText("公布值(%)");
                        listType = 0;
                        break;
                    case "etf":
                        mViewHolder0.tvTitle1.setText("净持仓量(盎司)");
                        mViewHolder0.tvTitle2.setText("净持仓量(吨)");
                        mViewHolder0.tvTitle3.setText("增减(吨)");
                        listType = 1;
                        break;
                    case "cftc":
                        mViewHolder0.tvTitle1.setText("多投持仓");
                        mViewHolder0.tvTitle2.setText("空头持仓");
                        mViewHolder0.tvTitle3.setText("多空净投仓");
                        listType = 2;
                        break;
                }

                break;
            case 1:
                long timeLong = Long.parseLong(itemDataBean.getTime()) * 1000;
                mViewHolder1.tvTime.setText(DateFormat.format("yyyy-MM-dd", timeLong));

                mViewHolder1.tvQian.setText(itemDataBean.getBefore());
                mViewHolder1.tvYuce.setText(itemDataBean.getForecast());
                mViewHolder1.tvGongbu.setText(itemDataBean.getReality());

                if (listType == 1) {
                    try {
                        String reality = itemDataBean.getReality();
                        double realityDouble = Double.parseDouble(reality);
                        int color;
                        if (realityDouble < 0) {
                            color = ContextCompat.getColor(mContext, R.color.decline_color);
                        }
                        else if (realityDouble > 0) {
                            color = ContextCompat.getColor(mContext, R.color.rise_color);
                        }else{
                            color = ContextCompat.getColor(mContext, R.color.font_color60);
                        }
                        mViewHolder1.tvTime.setTextColor(color);
                        mViewHolder1.tvQian.setTextColor(color);
                        mViewHolder1.tvYuce.setTextColor(color);
                        mViewHolder1.tvGongbu.setTextColor(color);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        return convertView;
    }

    class ViewHolder0 {
        @BindView(R.id.tv_title1) TextView tvTitle1;
        @BindView(R.id.tv_title2) TextView tvTitle2;
        @BindView(R.id.tv_title3) TextView tvTitle3;

        ViewHolder0(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class ViewHolder1 {
        @BindView(R.id.tv_time) TextView tvTime;
        @BindView(R.id.tv_qian) TextView tvQian;
        @BindView(R.id.tv_yuce) TextView tvYuce;
        @BindView(R.id.tv_gongbu) TextView tvGongbu;

        ViewHolder1(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
