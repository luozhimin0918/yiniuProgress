package com.jyh.kxt.trading.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.trading.json.ViewPointHotBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mr'Dai on 2017/8/1.
 */

public class HotHeadAdapter extends BaseAdapter {

    private Context context;
    private List<ViewPointHotBean> authors;
    private LayoutInflater mInflater;

    public HotHeadAdapter(Context context, List<ViewPointHotBean> authors) {
        this.context = context;
        this.authors = authors;
        mInflater = LayoutInflater.from(context);
    }

    public List<ViewPointHotBean> getData() {
        return authors;
    }

    @Override
    public int getCount() {
        return authors == null ? 0 : authors.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewPointHotBean authorJson = authors.get(position);

        final HotHeadAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_hot_head_view, parent, false);
            holder = new HotHeadAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (HotHeadAdapter.ViewHolder) convertView.getTag();
        }

        holder.tvName.setText(authorJson.getName());

        Glide.with(context).load(authorJson.getPicture())
                .asBitmap().error(R.mipmap.icon_user_def_photo)
                .placeholder(R.mipmap.icon_user_def_photo)
                .override(100, 100)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        holder.ivPhoto.setImageBitmap(resource);
                    }
                });

        holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.font_color5));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        return convertView;
    }

    public class ViewHolder {

        @BindView(R.id.iv_photo) RoundImageView ivPhoto;
        @BindView(R.id.tv_name) TextView tvName;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }
}
