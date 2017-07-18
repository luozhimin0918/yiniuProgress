package com.jyh.kxt.market.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.databinding.ItemMarketRecommendBinding;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.jyh.kxt.market.ui.MarketDetailChartActivity;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/5/18.
 */

public class MarketGridAdapter extends BaseListAdapter<MarketItemBean> {

    private Context mContext;
    private LayoutInflater mInflater;

    public MarketGridAdapter(Context mContext, List<MarketItemBean> dataList) {
        super(dataList);
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder mViewHolder;
        if (convertView == null) {
            ItemMarketRecommendBinding dataBinding = DataBindingUtil.inflate(
                    mInflater,
                    R.layout.item_market_recommend,
                    parent,
                    false);

            convertView = dataBinding.getRoot();

            mViewHolder = new ViewHolder();
            mViewHolder.setDataBinding(dataBinding);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        final MarketItemBean marketItemBean = dataList.get(position);
        ItemMarketRecommendBinding dataBinding = mViewHolder.getDataBinding();
        dataBinding.setBean(marketItemBean);

        int bgColor = ContextCompat.getColor(mContext, R.color.theme1);
        marketItemBean.setBgItemColor(bgColor);

        int nameFontColor = ContextCompat.getColor(mContext, R.color.font_color5);
        dataBinding.setNameFontColor(nameFontColor);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MarketDetailChartActivity.class);
                intent.putExtra("market", marketItemBean);
                mContext.startActivity(intent);
            }
        });

        marketItemBean.setMarketFromSource(0);
        return convertView;
    }

    class ViewHolder {
        ItemMarketRecommendBinding dataBinding;

        public ItemMarketRecommendBinding getDataBinding() {
            return dataBinding;
        }

        public void setDataBinding(ItemMarketRecommendBinding dataBinding) {
            this.dataBinding = dataBinding;
        }
    }

    public void updateLayoutColor() {
        int bgColor = ContextCompat.getColor(mContext, R.color.theme1);
        for (MarketItemBean marketItemBean : dataList) {
            marketItemBean.setBgItemColor(bgColor);
        }
    }
}
