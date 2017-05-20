package com.jyh.kxt.market.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.databinding.ItemMarketRecommendBinding;
import com.jyh.kxt.market.bean.MarketItemBean;

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
                    null,
                    false);

            convertView = dataBinding.getRoot();

            mViewHolder = new ViewHolder();
            mViewHolder.setDataBinding(dataBinding);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        MarketItemBean marketItemBean = dataList.get(position);
        ItemMarketRecommendBinding dataBinding = mViewHolder.getDataBinding();
        dataBinding.setBean(marketItemBean);

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
}
