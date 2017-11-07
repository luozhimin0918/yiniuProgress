package com.jyh.kxt.main.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.av.json.VideoDetailVideoBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:KxtProfessional
 * 类描述:要闻列表视听Adapter
 * 创建人:苟蒙蒙
 * 创建日期:2017/11/7.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private Context mContext;
    private List<VideoDetailVideoBean> list;

    public VideoAdapter(Context mContext, List<VideoDetailVideoBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_more_video, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VideoDetailVideoBean bean = list.get(position);
        holder.ivVideoName.setText(bean.getTitle());
        Glide.with(mContext).load(bean.getPicture()).error(R.mipmap.icon_def_video).placeholder(R.mipmap.icon_def_video).into(holder
                .ivVideoCover);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_video_cover) ImageView ivVideoCover;
        @BindView(R.id.iv_video_name) TextView ivVideoName;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
