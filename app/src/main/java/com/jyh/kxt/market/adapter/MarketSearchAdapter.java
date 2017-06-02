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
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/19.
 */

public class MarketSearchAdapter extends BaseListAdapter<MarketItemBean> {

    private Context context;
    private String searchKey;

    public MarketSearchAdapter(List<MarketItemBean> dataList, Context context) {
        super(dataList);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_market_search, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MarketItemBean bean = dataList.get(position);

        String name = bean.getName();

        holder.tvTitle.setText(name);
        holder.tvContent.setText(bean.getPrice());

        return convertView;
    }

    public void setData(List<MarketItemBean> data) {
        dataList.clear();
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public List<MarketItemBean> getData() {
        return dataList;
    }

    static class ViewHolder {
        @BindView(R.id.tv_title) TextView tvTitle;
        @BindView(R.id.tv_content) TextView tvContent;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
