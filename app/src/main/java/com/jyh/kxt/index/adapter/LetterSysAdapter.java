package com.jyh.kxt.index.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.index.json.LetterSysJson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/29.
 */

public class LetterSysAdapter extends BaseListAdapter<LetterSysJson> {

    private Context mContext;

    public LetterSysAdapter(List<LetterSysJson> dataList, Context mContext) {
        super(dataList);
        this.mContext = mContext;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_letter_sys_content, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Glide.with(mContext).load(R.mipmap.icon_msg_sys).asBitmap().into(new ImageViewTarget<Bitmap>(holder.rivAvatar) {
            @Override
            protected void setResource(Bitmap resource) {
                holder.rivAvatar.setImageBitmap(resource);
            }
        });

        LetterSysJson bean = dataList.get(position);
        holder.tvTime.setText("12:12");
        holder.tvContent.setText(bean.getInfo());
        holder.tvTitle.setText("系统");

        return convertView;
    }

    public void setData(List<LetterSysJson> data) {
        this.dataList.clear();
        this.dataList.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(List<LetterSysJson> data) {
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        @BindView(R.id.riv_avatar) RoundImageView rivAvatar;
        @BindView(R.id.tv_title) TextView tvTitle;
        @BindView(R.id.tv_content) TextView tvContent;
        @BindView(R.id.tv_time) TextView tvTime;
        @BindView(R.id.v_line) View vLine;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
