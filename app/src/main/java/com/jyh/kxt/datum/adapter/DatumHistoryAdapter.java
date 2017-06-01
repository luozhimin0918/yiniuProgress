package com.jyh.kxt.datum.adapter;

import android.content.Context;
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

        ViewHolder mViewHolder = null;
        if (convertView == null) {
            switch (type) {
                case 0:
                    convertView = mInflater.inflate(R.layout.activity_datum_history_suspension, parent, false);
                    break;
                case 1:
                    convertView = mInflater.inflate(R.layout.activity_datum_history_item,  parent, false);
                    mViewHolder = new ViewHolder(convertView);
                    convertView.setTag(mViewHolder);
                    break;
            }
        } else {
            switch (type) {
                case 0:
                    break;
                case 1:
                    mViewHolder = (ViewHolder) convertView.getTag();
                    break;
            }
        }
        switch (type) {
            case 0:
                break;
            case 1:
                long timeLong = Long.parseLong(itemDataBean.getTime()) * 1000;
                mViewHolder.tvTime.setText(DateFormat.format("yyyy-MM-dd", timeLong));

                mViewHolder.tvQian.setText(itemDataBean.getBefore());
                mViewHolder.tvYuce.setText(itemDataBean.getForecast());
                mViewHolder.tvGongbu.setText(itemDataBean.getReality());
                break;
        }
        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.tv_time) TextView tvTime;
        @BindView(R.id.tv_qian) TextView tvQian;
        @BindView(R.id.tv_yuce) TextView tvYuce;
        @BindView(R.id.tv_gongbu) TextView tvGongbu;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
