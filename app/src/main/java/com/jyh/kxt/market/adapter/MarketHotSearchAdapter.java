package com.jyh.kxt.market.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.annotation.OnItemClickListener;
import com.jyh.kxt.market.bean.MarketItemBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:Kxt
 * 类描述:热门搜索Adapter
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/19.
 */

public class MarketHotSearchAdapter extends RecyclerView.Adapter<MarketHotSearchAdapter.ViewHolder> {

    private Context context;
    private List<MarketItemBean> list;
    private OnItemClickListener onItemClickListener;

    public MarketHotSearchAdapter(Context context, List<MarketItemBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MarketHotSearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_market_search_hot, parent, false));
    }

    @Override
    public void onBindViewHolder(final MarketHotSearchAdapter.ViewHolder holder, final int position) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(position, v);
            }
        });

        int rank = position + 1;
        holder.tvRank.setText(rank + "");
        switch (rank) {
            case 1:
                holder.tvRank.setBackgroundResource(R.drawable.bg_rank1);
                break;
            case 2:
                holder.tvRank.setBackgroundResource(R.drawable.bg_rank2);
                break;
            case 3:
                holder.tvRank.setBackgroundResource(R.drawable.bg_rank3);
                break;
            default:
                holder.tvRank.setBackgroundResource(R.drawable.bg_rank4);
                break;
        }
        holder.tvTitle.setText(list.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        int size = list.size();
        if (size > 9) return 9;
        return size;
    }

    public void setData(List<MarketItemBean> data) {
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public List<MarketItemBean> getData() {
        return list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_rank) TextView tvRank;
        @BindView(R.id.tv_title) TextView tvTitle;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
