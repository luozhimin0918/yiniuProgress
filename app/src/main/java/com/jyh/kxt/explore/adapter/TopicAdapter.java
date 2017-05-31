package com.jyh.kxt.explore.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.base.annotation.OnItemClickListener;
import com.jyh.kxt.base.constant.HttpConstant;
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
    private OnItemClickListener onItemClickListener;

    public TopicAdapter(Context context, List<TopicJson> topics) {
        this.context = context;
        this.topics = topics;
    }

    @Override
    public TopicAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_explore_header2, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        TopicJson topic = topics.get(position);
        holder.tvTitle.setText(topic.getTitle());
        holder.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.font_color5));
        holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_contour));
        Glide.with(context).load(HttpConstant.IMG_URL + topic.getPicture()).error(R.mipmap.icon_def_video).placeholder(R.mipmap
                .icon_def_video)
                .into(holder.ivBtn);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(position, holder.itemView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return topics == null ? 0 : topics.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
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
