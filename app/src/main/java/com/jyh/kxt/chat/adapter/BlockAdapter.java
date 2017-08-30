package com.jyh.kxt.chat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.chat.json.BlockJson;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/8/30.
 */

public class BlockAdapter extends BaseListAdapter<BlockJson> {
    private Context mContext;

    public BlockAdapter(Context mContext, List<BlockJson> dataList) {
        super(dataList);
        this.mContext = mContext;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
