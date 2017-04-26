package com.jyh.kxt.market.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jyh.kxt.R;
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
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_market_recommend, null);
        MyViewHolder mh = new MyViewHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return marketItemBeanList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(View view) {
            super(view);
        }

    }
}