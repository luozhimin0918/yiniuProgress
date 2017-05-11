package com.jyh.kxt.main.adapter;

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

/**
 * 项目名:Kxt
 * 类描述:要闻Adapter
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/17.
 */

public class NewsAdapter extends BaseListAdapter<NewsJson> {

    private Context mContext;
    private boolean isShowTitle = false;

    public boolean isShowTitle() {
        return isShowTitle;
    }

    public void setShowTitle(boolean showTitle) {
        isShowTitle = showTitle;
    }

    public NewsAdapter(Context context, List<NewsJson> dataList) {
        super(dataList);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_news, parent, false);
            holder.ivPhoto = (ImageView) convertView.findViewById(R.id.iv_photo);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tvAuthor = (TextView) convertView.findViewById(R.id.tv_author);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv = (TextView) convertView.findViewById(R.id.tv1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (isShowTitle)
            if (position == 0) holder.tv.setVisibility(View.VISIBLE);
            else holder.tv.setVisibility(View.GONE);

        final NewsJson news = dataList.get(position);

        Glide.with(mContext).load(HttpConstant.IMG_URL + news.getPicture()).placeholder(R.mipmap.ico_def_load).error(R.mipmap.ico_def_load)
                .into
                        (holder.ivPhoto);

        if (BrowerHistoryUtils.isBrowered(mContext, news)) {
            holder.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.font_color6));
        } else {
            holder.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.font_color5));
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

    public void addData(List<NewsJson> newsJsons) {
        dataList.addAll(newsJsons);
        notifyDataSetChanged();
    }

    public void setData(List<NewsJson> data) {
        dataList.clear();
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    class ViewHolder {
        public TextView tv, tvTitle, tvAuthor, tvTime;
        public ImageView ivPhoto;
    }
}
