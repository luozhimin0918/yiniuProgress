package com.jyh.kxt.base;

import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/4/11.
 */

public abstract class BaseListAdapter<T> extends BaseAdapter {

    public List<T> dataList;

    public BaseListAdapter(List<T> dataList) {
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public T getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addData(T t) {
        dataList.add(t);
        notifyDataSetChanged();
    }

    public void addAllData(List<? extends T> arrayList) {
        dataList.addAll(arrayList);
    }
}
