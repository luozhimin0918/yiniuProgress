package com.jyh.kxt.market.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.databinding.ItemMarketQuotesBinding;
import com.jyh.kxt.market.bean.MarketItemBean;

import java.util.List;


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

            ItemMarketQuotesBinding dataBinding = DataBindingUtil.inflate(
                    mInflater,
                    R.layout.item_market_quotes,
                    null,
                    false);

            convertView = dataBinding.getRoot();

            mViewHolder = new ViewHolder();
            mViewHolder.setBinding(dataBinding);

            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        MarketItemBean marketItemBean = dataList.get(position);

        ItemMarketQuotesBinding binding = mViewHolder.getBinding();
        binding.setBean(marketItemBean);

        return convertView;
    }

    class ViewHolder {


        private ItemMarketQuotesBinding binding;

        public ViewHolder() {
        }

        public void setBinding(ItemMarketQuotesBinding binding) {
            this.binding = binding;
        }

        public ItemMarketQuotesBinding getBinding() {
            return binding;
        }
    }
}
