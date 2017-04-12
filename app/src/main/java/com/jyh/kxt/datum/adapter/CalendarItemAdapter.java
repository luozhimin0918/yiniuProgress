package com.jyh.kxt.datum.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/4/11.
 */

public class CalendarItemAdapter extends BaseListAdapter<String> {
    private LayoutInflater inflater;

    public CalendarItemAdapter(Context mContext, List<String> dataList) {
        super(dataList);
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.item_calendar_data, null);
        return convertView;
    }
}
