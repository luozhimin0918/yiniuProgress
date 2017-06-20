package com.jyh.kxt.explore.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.explore.json.AuthorJson;
import com.jyh.kxt.explore.ui.AuthorActivity;
import com.library.util.RegexValidateUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:Kxt
 * 类描述:名家专栏头部内容Adapter
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/15.
 */

public class AuthorHeadContentAdapter extends BaseAdapter {

    private Context context;
    private List<AuthorJson> authors;
    private LayoutInflater mInflater;

    public AuthorHeadContentAdapter(Context context, List<AuthorJson> authors) {
        this.context = context;
        this.authors = authors;
        mInflater = LayoutInflater.from(context);
    }

    public List<AuthorJson> getData() {
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

        final AuthorJson authorJson = authors.get(position);

        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_explore_author, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.tvName.setText(authorJson.getName());
        String num_fans = authorJson.getNum_fans();
        if (RegexValidateUtil.isEmpty(num_fans)) {
            num_fans = "粉丝 0";
        } else {
            num_fans = "粉丝 " + num_fans;
        }

        holder.tvFans.setText(num_fans);
        Glide.with(context).load(authorJson.getPicture()).asBitmap().error(R.mipmap.icon_user_def_photo).placeholder(R
                .mipmap.icon_user_def_photo).override
                (100, 100)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        holder.ivPhoto.setImageBitmap(resource);
                    }
                });

        holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.font_color5));
        holder.tvFans.setTextColor(ContextCompat.getColor(context, R.color.font_color60));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String authorId = authorJson.getId();
                Intent authorIntent = new Intent(context, AuthorActivity.class);
                authorIntent.putExtra(IntentConstant.O_ID, authorId);
                context.startActivity(authorIntent);
            }
        });
        return convertView;
    }

    static class ViewHolder {

        @BindView(R.id.iv_photo) RoundImageView ivPhoto;
        @BindView(R.id.tv_name) TextView tvName;
        @BindView(R.id.tv_fans) TextView tvFans;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }
}
