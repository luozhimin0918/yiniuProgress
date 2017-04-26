package com.jyh.kxt.market.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.market.bean.MarketItemBean;

import java.util.List;


/**
 * Created by Mr'Dai on 2017/4/26.
 */

public class MarketItemAdapter extends BaseListAdapter<MarketItemBean> {

    private Context mContext;
    private LayoutInflater mInflater;

    public MarketItemAdapter(Context mContext, List<MarketItemBean> dataList) {
        super(dataList);
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        return null;
    }

    class ViewHolder {
        public ViewHolder(View view) {

        }
    }
}
