package com.jyh.kxt.market.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jyh.kxt.R;
import com.jyh.kxt.databinding.ItemMarketRecommendBinding;
import com.jyh.kxt.market.bean.MarketItemBean;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/4/26.
 */
public class MarketRecommendAdapter extends RecyclerView.Adapter<MarketRecommendAdapter.MyViewHolder> {

    private Context mContext;
    private List<MarketItemBean> marketItemBeanList;

    public MarketRecommendAdapter(Context mContext, List<MarketItemBean> marketItemBeanList) {
        this.mContext = mContext;
        this.marketItemBeanList = marketItemBeanList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflate = LayoutInflater.from(mContext);
        ItemMarketRecommendBinding dataBinding = DataBindingUtil.inflate(
                mInflate,
                R.layout.item_market_recommend,
                null,
                false);

        View v = dataBinding.getRoot();

        MyViewHolder mh = new MyViewHolder(v);
        mh.setBinding(dataBinding);

        return mh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MarketItemBean marketItemBean = marketItemBeanList.get(position);

        ItemMarketRecommendBinding binding = holder.getBinding();
        binding.setBean(marketItemBean);
    }

    @Override
    public int getItemCount() {
        return marketItemBeanList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemMarketRecommendBinding binding;

        public MyViewHolder(View view) {
            super(view);
        }

        public ItemMarketRecommendBinding getBinding() {
            return binding;
        }

        public void setBinding(ItemMarketRecommendBinding binding) {
            this.binding = binding;
        }
    }
}