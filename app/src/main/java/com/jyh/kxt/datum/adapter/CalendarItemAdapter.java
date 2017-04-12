package com.jyh.kxt.datum.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.widget.StarView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_calendar_data, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.llStar.refreshConfig(2, false);

        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.iv_guoqi) ImageView ivGuoQi;
        @BindView(R.id.ll_star) StarView llStar;
        @BindView(R.id.ll_left_gq) LinearLayout llLeftGq;
        @BindView(R.id.tv_title) TextView tvTitle;
        @BindView(R.id.tv_describe) TextView tvDescribe;
        @BindView(R.id.tv_time) TextView tvTime;
        @BindView(R.id.ll_publish) LinearLayout llPublish;

        public ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
        }
    }
}
