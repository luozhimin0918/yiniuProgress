package com.jyh.kxt.explore.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.explore.json.AuthorJson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/6/12.
 */

public class AuthorListAdapter extends BaseListAdapter<AuthorJson> {

    private Context mContext;

    public AuthorListAdapter(List<AuthorJson> dataList, Context mContext) {
        super(dataList);
        this.mContext = mContext;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_author, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AuthorJson author = dataList.get(position);
        holder.tvArticle.setText("文章数: " + author.getArticle_num());
        holder.tvName.setText(author.getName());
        holder.tvFans.setText("粉丝数: " + author.getNum_fans());
        Glide.with(mContext).load(author.getPicture()).asBitmap().error(R.mipmap.icon_user_def_photo)
                .placeholder(R.mipmap.icon_user_def_photo).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                holder.rivAvatar.setImageBitmap(resource);
            }
        });

        setTheme(holder);

        return convertView;
    }

    private void setTheme(ViewHolder holder) {
        holder.tvArticle.setTextColor(ContextCompat.getColor(mContext, R.color.font_color6));
        holder.tvFans.setTextColor(ContextCompat.getColor(mContext, R.color.font_color6));
        holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.font_color5));
        holder.vLine.setBackgroundColor(ContextCompat.getColor(mContext, R.color.line_color3));
    }

    public List<AuthorJson> getData() {
        return dataList;
    }

    public String getLastId() {
        if (dataList == null) return "";
        return dataList.get(getCount() - 1).getId();
    }

    public void setData(List<AuthorJson> authorList) {
        dataList.clear();
        dataList.addAll(authorList);
        notifyDataSetChanged();
    }

    public void addData(List<AuthorJson> authorList) {
        dataList.addAll(authorList);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        @BindView(R.id.riv_avatar) RoundImageView rivAvatar;
        @BindView(R.id.tv_name) TextView tvName;
        @BindView(R.id.tv_article) TextView tvArticle;
        @BindView(R.id.tv_fans) TextView tvFans;
        @BindView(R.id.v_line) View vLine;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
