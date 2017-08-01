package com.jyh.kxt.trading.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.trading.json.ViewpointJson;
import com.library.widget.listview.PinnedSectionListView;

import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/7/26.
 */

public class ViewpointAdapter extends BaseListAdapter<ViewpointJson> implements
        PinnedSectionListView.PinnedSectionListAdapter {

    private Context mContext;
    private LayoutInflater mInflater;

    private int viewTypeCount = 2;

    public ViewpointAdapter(Context mContext, List<ViewpointJson> dataList) {
        super(dataList);

        this.mContext = mContext;

        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder1 viewHolder1;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.view_viewpoint_item1, parent, false);
            viewHolder1 = new ViewHolder1(convertView);
            convertView.setTag(viewHolder1);
        } else {
            viewHolder1 = (ViewHolder1) convertView.getTag();
        }
        return convertView;
    }

    class ViewHolder1 {
        private View contentView;

        public ViewHolder1(View contentView) {
            this.contentView = contentView;
        }
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return false;
    }
}
