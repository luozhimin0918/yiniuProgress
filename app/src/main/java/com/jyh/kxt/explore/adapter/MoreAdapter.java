package com.jyh.kxt.explore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.explore.json.ActivityJson;
import com.jyh.kxt.explore.json.TopicJson;
import com.library.base.http.VarConstant;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:Kxt
 * 类描述:更多Adapter
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/15.
 */

public class MoreAdapter extends BaseListAdapter {

    private Context context;
    private String type;

    public MoreAdapter(List dataList, Context context, String type) {
        super(dataList);
        this.context = context;
        this.type = type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        switch (type) {
            case VarConstant.EXPLORE_ACTIVITY: {
                ActivityViewHolder viewHolder = null;
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_explore_more_activity, parent, false);
                    viewHolder = new ActivityViewHolder(convertView);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ActivityViewHolder) convertView.getTag();
                }
                ActivityJson activity = JSON.parseObject(dataList.get(position).toString(), ActivityJson.class);
                viewHolder.tvTitle.setText(activity.getTitle());
                viewHolder.tvContent.setText(activity.getRemark());
                viewHolder.tvTime.setText(activity.getStart_time());
                String status = activity.getStatus();
                if (status != null && "1".equals(status)) {
                    viewHolder.ivTime.setSelected(true);
                } else {
                    viewHolder.ivTime.setSelected(false);
                }
                Glide.with(context).load(HttpConstant.IMG_URL + activity.getPicture()).error(R.mipmap.icon_def_video).placeholder(R.mipmap
                        .icon_def_video).into
                        (viewHolder.ivPhoto);
            }
            break;
            case VarConstant.EXPLORE_TOPIC: {
                TopicViewHolder viewHolder = null;
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_explore_more_topic, parent, false);
                    viewHolder = new TopicViewHolder(convertView);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (TopicViewHolder) convertView.getTag();
                }
                TopicJson topic = JSON.parseObject(dataList.get(position).toString(), TopicJson.class);
                viewHolder.tvTitle.setText(topic.getTitle());
                viewHolder.tvContent.setText(topic.getBackground());
                Glide.with(context).load(HttpConstant.IMG_URL + topic.getPicture()).error(R.mipmap.icon_def_video).placeholder(R.mipmap
                        .icon_def_video).into
                        (viewHolder.ivPhoto);
            }
            break;
        }

        return convertView;
    }

    public void setData(List data) {
        dataList.clear();
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(List data) {
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    public String getLastId() {
        switch (type) {
            case VarConstant.EXPLORE_ACTIVITY:
                return JSON.parseObject(dataList.get(dataList.size() - 1).toString(), ActivityJson.class).getId();
            case VarConstant.EXPLORE_TOPIC:
                return JSON.parseObject(dataList.get(dataList.size() - 1).toString(), TopicJson.class).getId();
        }
        return "";
    }

    public List getData() {
        return dataList;
    }

    static class ActivityViewHolder {
        @BindView(R.id.iv_photo) ImageView ivPhoto;
        @BindView(R.id.tv_title) TextView tvTitle;
        @BindView(R.id.tv_content) TextView tvContent;
        @BindView(R.id.iv_time) ImageView ivTime;
        @BindView(R.id.tv_time) TextView tvTime;

        ActivityViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class TopicViewHolder {
        @BindView(R.id.iv_photo) ImageView ivPhoto;
        @BindView(R.id.tv_title) TextView tvTitle;
        @BindView(R.id.tv_content) TextView tvContent;

        TopicViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
