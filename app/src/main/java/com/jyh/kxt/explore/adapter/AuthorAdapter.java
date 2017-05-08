package com.jyh.kxt.explore.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.annotation.OnItemClickListener;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.explore.json.AuthorJson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/5.
 */

public class AuthorAdapter extends RecyclerView.Adapter<AuthorAdapter.ViewHolder> {

    private Context mContext;
    private List<AuthorJson> authors;
    private OnItemClickListener onItemClickListener;

    public AuthorAdapter(Context mContext, List<AuthorJson> authors) {
        this.mContext = mContext;
        this.authors = authors;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_explore_header4, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        AuthorJson author = authors.get(position);

        holder.tvCount.setText(author.getArticle_num());
        holder.tvFans.setText(author.getNum_fans());
        holder.tvName.setText(author.getName());

        Glide.with(mContext)
                .load(author.getPicture())
                .asBitmap()
                .override(100, 100)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        holder.ivPhoto.setImageBitmap(resource);
                    }
                });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(position, v);
            }
        });

    }

    @Override
    public int getItemCount() {
        return authors == null ? 0 : authors.size();
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_photo) RoundImageView ivPhoto;
        @BindView(R.id.tv_name) TextView tvName;
        @BindView(R.id.tv_fans) TextView tvFans;
        @BindView(R.id.tv_count) TextView tvCount;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
