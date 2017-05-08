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
import com.jyh.kxt.explore.json.TopicJson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/5.
 */

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder> {

    private Context context;
    private List<TopicJson> topics;

    public TopicAdapter(Context context, List<TopicJson> topics) {
        this.context = context;
        this.topics = topics;
    }

    @Override
    public TopicAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_explore_header2, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TopicJson topic = topics.get(position);
        holder.tvTitle.setText(topic.getTitle());
        Glide.with(context).load(topic.getUrl()).error(R.mipmap.ico_def_load).placeholder(R.mipmap.ico_def_load).into(holder.ivBtn);
    }

    @Override
    public int getItemCount() {
        return topics == null ? 0 : topics.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_btn) ImageView ivBtn;
        @BindView(R.id.tv_title) TextView tvTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
