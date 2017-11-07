package com.jyh.kxt.main.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.main.json.AuthorBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:KxtProfessional
 * 类描述:要闻列表专栏作者Adapter
 * 创建人:苟蒙蒙
 * 创建日期:2017/11/7.
 */

public class AuthorAdapter extends RecyclerView.Adapter<AuthorAdapter.ViewHolder> {

    private Context mContext;
    private List<AuthorBean> list;

    public AuthorAdapter(Context mContext, List<AuthorBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_news_author, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        AuthorBean bean = list.get(position);
        Glide.with(mContext).load(bean.getPicture()).asBitmap().error(R.mipmap.icon_user_def_photo).placeholder(R.mipmap.icon_user_def_photo)
                .into(new ImageViewTarget<Bitmap>(holder.ivPhoto) {
            @Override
            protected void setResource(Bitmap resource) {
                holder.ivPhoto.setImageBitmap(resource);
            }
        });
        holder.tvName.setText(bean.getTitle());
        holder.tvContent.setText(bean.getIntroduce());

    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_photo) RoundImageView ivPhoto;
        @BindView(R.id.tv_name) TextView tvName;
        @BindView(R.id.tv_content) TextView tvContent;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
