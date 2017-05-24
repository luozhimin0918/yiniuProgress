package com.jyh.kxt.explore.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.annotation.OnItemClickListener;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.explore.json.AuthorJson;
import com.library.util.RegexValidateUtil;

import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerArray;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:Kxt
 * 类描述:名家专栏头部内容Adapter
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/15.
 */

public class AuthorHeadContentAdapter extends RecyclerView.Adapter<AuthorHeadContentAdapter.ViewHolder> {

    private Context context;
    private List<AuthorJson> authors;
    private OnItemClickListener onItemClickListener;

    public AuthorHeadContentAdapter(Context context, List<AuthorJson> authors) {
        this.context = context;
        this.authors = authors;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_explore_author, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        AuthorJson author = authors.get(position);
        holder.tvName.setText(author.getName());
        String num_fans = author.getNum_fans();
        if (RegexValidateUtil.isEmpty(num_fans)) {
            num_fans = "粉丝 0";
        } else {
            num_fans = "粉丝 " + num_fans;
        }
        holder.tvFans.setText(num_fans);
        Glide.with(context).load(author.getPicture()).asBitmap().error(R.mipmap.icon_user_def_photo).placeholder(R.mipmap.icon_user_def_photo).override
                (100, 100)
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
                    onItemClickListener.onItemClick(position, holder.itemView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return authors == null ? 0 : authors.size();
    }

    public List<AuthorJson> getData() {
        return authors;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_photo) RoundImageView ivPhoto;
        @BindView(R.id.tv_name) TextView tvName;
        @BindView(R.id.tv_fans) TextView tvFans;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
