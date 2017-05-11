package com.jyh.kxt.explore.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.explore.json.ActivityJson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/5.
 */

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    private Context mContext;
    private List<ActivityJson> activitys;

    public ActivityAdapter(Context mContext, List<ActivityJson> activitys) {
        this.mContext = mContext;
        this.activitys = activitys;
    }

    @Override
    public ActivityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_explore_header3, parent, false));
    }

    @Override
    public void onBindViewHolder(ActivityAdapter.ViewHolder holder, int position) {
        ActivityJson activity = activitys.get(position);
        holder.tvTitle.setText(activity.getTitle());
        holder.tvTime.setText(activity.getStart_time());

        String status = activity.getStatus();
        if ("0".equals(status)) {
            holder.tvStart.setSelected(false);
        } else {
            holder.tvStart.setSelected(true);
        }

        Glide.with(mContext).load(activity.getUrl()).error(R.mipmap.ico_def_load).placeholder(R.mipmap.ico_def_load).into(holder.ivBtn);

    }

    @Override
    public int getItemCount() {
        return activitys == null ? 0 : activitys.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_btn) ImageView ivBtn;
        @BindView(R.id.tv_title) TextView tvTitle;
        @BindView(R.id.tv_start) ImageView tvStart;
        @BindView(R.id.tv_time) TextView tvTime;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}