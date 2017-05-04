package com.jyh.kxt.market.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.market.bean.MarketItemBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Mr'Dai on 2017/4/26.
 */

public class MarketMainItemAdapter extends BaseListAdapter<MarketItemBean> {

    private Context mContext;
    private LayoutInflater mInflater;

    public MarketMainItemAdapter(Context mContext, List<MarketItemBean> dataList) {
        super(dataList);
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_market_quotes, null);
            mViewHolder = new ViewHolder(convertView);

            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        MarketItemBean marketItemBean = dataList.get(position);
        mViewHolder.tvName.setText(marketItemBean.getName());
        mViewHolder.tvNewPrice.setText(marketItemBean.getPrice());
        mViewHolder.tvTarget.setText(marketItemBean.getRange()); //默认涨跌幅

        return convertView;
    }

    class ViewHolder {

        @BindView(R.id.tv_name) TextView tvName;
        @BindView(R.id.tv_new_price) TextView tvNewPrice;
        @BindView(R.id.tv_target) TextView tvTarget;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
