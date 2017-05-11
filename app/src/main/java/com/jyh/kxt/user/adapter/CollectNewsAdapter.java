package com.jyh.kxt.user.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.BrowerHistoryUtils;
import com.jyh.kxt.main.json.NewsJson;
import com.library.util.DateUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/11.
 */

public class CollectNewsAdapter extends BaseListAdapter<NewsJson> {

    private Context context;

    public CollectNewsAdapter(List<NewsJson> dataList, Context context) {
        super(dataList);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_collect_news, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        NewsJson news = dataList.get(position);
        Glide.with(context).load(HttpConstant.IMG_URL + news.getPicture()).placeholder(R.mipmap.ico_def_load).error(R.mipmap.ico_def_load)
                .into
                        (holder.ivPhoto);

        if (BrowerHistoryUtils.isBrowered(context, news)) {
            holder.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.font_color6));
        } else {
            holder.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.font_color5));
        }

        holder.tvTitle.setText(news.getTitle());
        String author = news.getAuthor();
        if (author != null)
            author = "文/" + author;
        holder.tvAuthor.setText(author);
        try {
            holder.tvTime.setText(DateUtils.transformTime(Long.parseLong(news.getDatetime()) * 1000));
        } catch (Exception e) {
            e.printStackTrace();
            holder.tvTime.setText("00:00");
        }

        return convertView;
    }

    public void setData(List<NewsJson> data) {
        dataList.clear();
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(List<NewsJson> data) {
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        @BindView(R.id.iv_photo) ImageView ivPhoto;
        @BindView(R.id.tv_title) TextView tvTitle;
        @BindView(R.id.tv_author) TextView tvAuthor;
        @BindView(R.id.tv_time) TextView tvTime;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
